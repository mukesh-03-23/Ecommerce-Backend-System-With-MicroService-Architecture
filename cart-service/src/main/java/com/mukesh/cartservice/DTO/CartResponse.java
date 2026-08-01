package com.mukesh.cartservice.DTO;

import com.mukesh.cartservice.Entity.CartItem;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CartResponse {
    private String email;
    private List<CartItemResponse> cartItems;
    private Double total;
}
