package com.mukesh.cartservice.DTO;

import lombok.Data;
@Data
public class CartItemResponse {
    private Long productId;
    private Integer quantity;
    private Double price;
}
