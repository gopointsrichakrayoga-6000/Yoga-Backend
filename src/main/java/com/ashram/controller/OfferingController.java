package com.ashram.controller;

import com.ashram.dto.*;
import com.ashram.service.OfferingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OfferingController {

    private final OfferingService offeringService;

    public OfferingController(OfferingService offeringService) {
        this.offeringService = offeringService;
    }

    @PostMapping("/offerings/create-order")
    public ResponseEntity<OrderCreateResponseDto> createOrder(@Valid @RequestBody OrderCreateRequestDto request) {
        OrderCreateResponseDto response = offeringService.createOrder(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/offerings/verify-payment")
    public ResponseEntity<OfferingDto> verifyPayment(@Valid @RequestBody PaymentVerifyRequestDto request) {
        OfferingDto response = offeringService.verifyPaymentSignature(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/offerings/ledger")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OfferingDto>> getLedger() {
        List<OfferingDto> ledger = offeringService.getVerifiedLedger();
        return ResponseEntity.ok(ledger);
    }
}
