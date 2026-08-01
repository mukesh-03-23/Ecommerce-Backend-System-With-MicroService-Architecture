package com.mukesh.productservice.DTO;

import lombok.Data;

@Data
public class InventoryRequest {
    private Integer orderquantity;
    private Long productId;
}
