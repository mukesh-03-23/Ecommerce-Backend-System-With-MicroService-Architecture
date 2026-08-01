package com.mukesh.orderservice.DTO;

import lombok.Data;

import java.util.UUID;
@Data
public class PaymentOrderResponse {
    private String order_Id;
    private String razorpayorderId;
    private Double amount;
    private String currency;
    private String email;
}
