package com.mukesh.cartservice.DTO;

import lombok.Data;

@Data
public class AddCartRequest {
    private Long productId;
    private Integer quantity;
}
