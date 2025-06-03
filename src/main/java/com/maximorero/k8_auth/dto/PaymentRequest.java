package com.maximorero.k8_auth.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private BigDecimal amount;
    private String currency;
    private String method;
    private String userId;
}
