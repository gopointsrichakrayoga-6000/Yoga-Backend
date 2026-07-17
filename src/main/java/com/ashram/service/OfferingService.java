package com.ashram.service;

import com.ashram.dto.*;
import com.ashram.entity.Offering;
import com.ashram.entity.OfferingStatus;
import com.ashram.repository.OfferingRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OfferingService {

    private static final Logger logger = LoggerFactory.getLogger(OfferingService.class);

    private final OfferingRepository offeringRepository;

    @Value("${razorpay.key.id:rzp_test_placeholder}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret:secret_placeholder}")
    private String razorpayKeySecret;

    public OfferingService(OfferingRepository offeringRepository) {
        this.offeringRepository = offeringRepository;
    }

    public boolean isGatewayReady() {
        return razorpayKeyId != null && !razorpayKeyId.contains("placeholder") 
            && !razorpayKeyId.isBlank() && razorpayKeySecret != null 
            && !razorpayKeySecret.contains("placeholder") && !razorpayKeySecret.isBlank();
    }

    @Transactional
    public OrderCreateResponseDto createOrder(OrderCreateRequestDto request) {
        logger.info("Creating offering order for {} (₹{}) - Purpose: {}", 
                request.getDonorName(), request.getAmountINR(), request.getPurpose());

        // Save offering record initially in CREATED state
        Offering offering = new Offering(
                request.getDonorName(),
                request.getDonorEmail(),
                request.getDonorPhone(),
                request.getAmountINR(),
                request.getPurpose(),
                request.getMessage()
        );
        offering = offeringRepository.save(offering);

        long amountInPaisa = request.getAmountINR().multiply(new BigDecimal("100")).longValue();

        // Check if gateway keys are configured or still placeholders
        if (!isGatewayReady()) {
            logger.warn("Razorpay API keys are still placeholders (`rzp_test_placeholder`). Returning gateway setup pending status without creating simulated order.");
            String pendingOrderId = "order_setup_pending_" + offering.getId();
            offering.setRazorpayOrderId(pendingOrderId);
            offeringRepository.save(offering);

            return new OrderCreateResponseDto(
                    pendingOrderId,
                    request.getAmountINR(),
                    amountInPaisa,
                    "INR",
                    razorpayKeyId,
                    false,
                    "Payment gateway setup pending. Please provide real Razorpay test API keys in backend configuration."
            );
        }

        try {
            RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaisa); // Amount in paisa
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "rcpt_offering_" + offering.getId());
            orderRequest.put("payment_capture", 1); // Auto capture

            Order razorpayOrder = client.orders.create(orderRequest);
            String razorpayOrderId = razorpayOrder.get("id");

            offering.setRazorpayOrderId(razorpayOrderId);
            offeringRepository.save(offering);

            return new OrderCreateResponseDto(
                    razorpayOrderId,
                    request.getAmountINR(),
                    amountInPaisa,
                    "INR",
                    razorpayKeyId,
                    true,
                    "Order created successfully on Razorpay."
            );
        } catch (RazorpayException e) {
            logger.error("Failed to create order on Razorpay servers: {}", e.getMessage(), e);
            throw new IllegalStateException("Failed to communicate with Razorpay servers: " + e.getMessage(), e);
        }
    }

    @Transactional
    public OfferingDto verifyPaymentSignature(PaymentVerifyRequestDto request) {
        logger.info("Verifying Razorpay payment signature for Order: {} / Payment: {}", 
                request.getRazorpayOrderId(), request.getRazorpayPaymentId());

        if (!isGatewayReady()) {
            logger.error("Verification attempted while Razorpay API keys are placeholders.");
            throw new IllegalStateException("Payment gateway setup pending. Cannot verify signatures without valid API keys.");
        }

        Offering offering = offeringRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Offering order not found: " + request.getRazorpayOrderId()));

        if (offering.getStatus() == OfferingStatus.PAID) {
            logger.info("Offering order {} is already marked as PAID.", request.getRazorpayOrderId());
            return OfferingDto.fromEntity(offering);
        }

        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", request.getRazorpayOrderId());
            options.put("razorpay_payment_id", request.getRazorpayPaymentId());
            options.put("razorpay_signature", request.getRazorpaySignature());

            boolean validSignature = Utils.verifyPaymentSignature(options, razorpayKeySecret);

            if (!validSignature) {
                logger.warn("Razorpay HMAC signature mismatch! Order: {} / Payment: {}", 
                        request.getRazorpayOrderId(), request.getRazorpayPaymentId());
                offering.setStatus(OfferingStatus.FAILED);
                offeringRepository.save(offering);
                throw new IllegalArgumentException("Payment signature verification failed. Potential tampering or invalid transaction.");
            }

            logger.info("Razorpay signature verified successfully. Marking offering {} as PAID.", request.getRazorpayOrderId());
            offering.setStatus(OfferingStatus.PAID);
            offering.setRazorpayPaymentId(request.getRazorpayPaymentId());
            offering.setRazorpaySignature(request.getRazorpaySignature());
            offering.setPaidAt(LocalDateTime.now());
            offering = offeringRepository.save(offering);

            return OfferingDto.fromEntity(offering);
        } catch (RazorpayException e) {
            logger.error("Error verifying signature via Razorpay SDK: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Signature verification error: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<OfferingDto> getVerifiedLedger() {
        // Return strictly PAID offerings ordered by latest payment date
        return offeringRepository.findByStatusOrderByPaidAtDesc(OfferingStatus.PAID)
                .stream()
                .map(OfferingDto::fromEntity)
                .collect(Collectors.toList());
    }
}
