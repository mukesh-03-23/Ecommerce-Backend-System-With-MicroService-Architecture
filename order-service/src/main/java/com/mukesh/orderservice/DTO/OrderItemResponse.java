package com.mukesh.orderservice.DTO;

import lombok.Data;

@Data
public class OrderItemResponse {
        private Long productId;
        private Double productPrice;//subtaotal
        private Integer quantity;
}
