package com.mukesh.paymentservice.DTO;

import lombok.Data;

import java.util.UUID;

@Data
public class VerifyPaymentRequest {
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
}
