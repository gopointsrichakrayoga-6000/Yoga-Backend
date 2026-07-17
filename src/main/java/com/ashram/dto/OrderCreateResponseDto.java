package com.ashram.dto;

import java.math.BigDecimal;

public class OrderCreateResponseDto {
    private String orderId;
    private BigDecimal amountINR;
    private Long amountInPaisa;
    private String currency;
    private String keyId;
    private boolean gatewayReady;
    private String message;

    public OrderCreateResponseDto() {
    }

    public OrderCreateResponseDto(String orderId, BigDecimal amountINR, Long amountInPaisa, String currency, String keyId, boolean gatewayReady, String message) {
        this.orderId = orderId;
        this.amountINR = amountINR;
        this.amountInPaisa = amountInPaisa;
        this.currency = currency;
        this.keyId = keyId;
        this.gatewayReady = gatewayReady;
        this.message = message;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getAmountINR() {
        return amountINR;
    }

    public void setAmountINR(BigDecimal amountINR) {
        this.amountINR = amountINR;
    }

    public Long getAmountInPaisa() {
        return amountInPaisa;
    }

    public void setAmountInPaisa(Long amountInPaisa) {
        this.amountInPaisa = amountInPaisa;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public boolean isGatewayReady() {
        return gatewayReady;
    }

    public void setGatewayReady(boolean gatewayReady) {
        this.gatewayReady = gatewayReady;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
