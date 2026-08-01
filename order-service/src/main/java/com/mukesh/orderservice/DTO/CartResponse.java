package com.mukesh.orderservice.DTO;

import com.mukesh.orderservice.Entity.OrderItem;
import lombok.Data;

import java.util.List;
import java.util.UUID;
@Data
public class CartResponse {
    private String email;
    private List<CartItemResponse> cartItems;
    private Double total;
}
