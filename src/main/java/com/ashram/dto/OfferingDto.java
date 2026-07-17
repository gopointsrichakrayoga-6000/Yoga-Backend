package com.ashram.dto;

import com.ashram.entity.Offering;
import com.ashram.entity.OfferingPurpose;
import com.ashram.entity.OfferingStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OfferingDto {
    private Long id;
    private String donorName;
    private String donorEmail;
    private String donorPhone;
    private BigDecimal amountINR;
    private OfferingPurpose purpose;
    private String message;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private OfferingStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    public OfferingDto() {
    }

    public static OfferingDto fromEntity(Offering entity) {
        OfferingDto dto = new OfferingDto();
        dto.setId(entity.getId());
        dto.setDonorName(entity.getDonorName());
        dto.setDonorEmail(entity.getDonorEmail());
        dto.setDonorPhone(entity.getDonorPhone());
        dto.setAmountINR(entity.getAmountINR());
        dto.setPurpose(entity.getPurpose());
        dto.setMessage(entity.getMessage());
        dto.setRazorpayOrderId(entity.getRazorpayOrderId());
        dto.setRazorpayPaymentId(entity.getRazorpayPaymentId());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setPaidAt(entity.getPaidAt());
        return dto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public String getDonorEmail() {
        return donorEmail;
    }

    public void setDonorEmail(String donorEmail) {
        this.donorEmail = donorEmail;
    }

    public String getDonorPhone() {
        return donorPhone;
    }

    public void setDonorPhone(String donorPhone) {
        this.donorPhone = donorPhone;
    }

    public BigDecimal getAmountINR() {
        return amountINR;
    }

    public void setAmountINR(BigDecimal amountINR) {
        this.amountINR = amountINR;
    }

    public OfferingPurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(OfferingPurpose purpose) {
        this.purpose = purpose;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }

    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }

    public void setRazorpayPaymentId(String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
    }

    public OfferingStatus getStatus() {
        return status;
    }

    public void setStatus(OfferingStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }
}
