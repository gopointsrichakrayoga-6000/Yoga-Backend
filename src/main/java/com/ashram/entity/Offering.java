package com.ashram.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "offerings")
public class Offering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String donorName;

    @Column(nullable = false)
    private String donorEmail;

    @Column(length = 20)
    private String donorPhone;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amountINR;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OfferingPurpose purpose;

    @Column(length = 1000)
    private String message;

    @Column(unique = true)
    private String razorpayOrderId;

    @Column(unique = true)
    private String razorpayPaymentId;

    @Column(length = 500)
    private String razorpaySignature;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OfferingStatus status = OfferingStatus.CREATED;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime paidAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Offering() {
    }

    public Offering(String donorName, String donorEmail, String donorPhone, BigDecimal amountINR, OfferingPurpose purpose, String message) {
        this.donorName = donorName;
        this.donorEmail = donorEmail;
        this.donorPhone = donorPhone;
        this.amountINR = amountINR;
        this.purpose = purpose;
        this.message = message;
        this.status = OfferingStatus.CREATED;
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

    public String getRazorpaySignature() {
        return razorpaySignature;
    }

    public void setRazorpaySignature(String razorpaySignature) {
        this.razorpaySignature = razorpaySignature;
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
