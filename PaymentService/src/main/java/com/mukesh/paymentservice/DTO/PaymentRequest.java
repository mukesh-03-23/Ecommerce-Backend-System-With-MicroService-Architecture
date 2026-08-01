package com.mukesh.paymentservice.DTO;

import lombok.Data;

import java.util.UUID;

@Data
public class PaymentRequest {
    private String orderId;
    private Double amount;
    private String currency;
    private String email;
}
