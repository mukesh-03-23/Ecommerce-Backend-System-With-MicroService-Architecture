package com.mukesh.productservice.DTO;

import lombok.Data;

@Data
public class ProductResponseDto {
    private Long productId;
    private String productName;
    private String productDescription;
    private Double productPrice;
    private Integer productQuantity;
    private String categoryName;
    private String imageUrl;
}
