package com.mukesh.orderservice.DTO;

import lombok.Data;

import java.util.List;
@Data
public class OrderResponse {
    private String orderId;
    private String email;
    private String PaymentStatus;
    private List<OrderItemResponse> items;
    private Double amount;
}
