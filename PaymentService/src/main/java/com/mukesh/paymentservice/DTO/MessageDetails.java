package com.mukesh.paymentservice.DTO;

import lombok.Data;

import java.util.UUID;
@Data
public class MessageDetails {
    private String email;
    private String orderId;
}
