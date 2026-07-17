package com.ashram.dto;

import com.ashram.entity.OfferingPurpose;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class OrderCreateRequestDto {

    @NotBlank(message = "Donor name is required")
    private String donorName;

    @NotBlank(message = "Donor email is required")
    @Email(message = "Please provide a valid email address")
    private String donorEmail;

    private String donorPhone;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Minimum offering amount is ₹1.00")
    private BigDecimal amountINR;

    @NotNull(message = "Offering purpose is required")
    private OfferingPurpose purpose;

    private String message;

    public OrderCreateRequestDto() {
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
}
