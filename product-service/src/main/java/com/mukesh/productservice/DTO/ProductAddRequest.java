package com.mukesh.productservice.DTO;

import lombok.Data;

@Data
public class ProductAddRequest {
    private String productName;
    private String productDescription;
    private Double productPrice;
    private Integer productQuantity;
    private String productCategory;
}
