package com.mukesh.productservice.Utility;

import com.mukesh.productservice.DTO.ProductResponseDto;
import com.mukesh.productservice.Entity.Product;

public class MappingDtos {
    public static ProductResponseDto MapProductToProductResponse(Product pro){
           ProductResponseDto res=new ProductResponseDto();
           res.setProductId(pro.getProduct_id());
           res.setProductName(pro.getProductName());
           res.setProductPrice(pro.getProductPrice());
           res.setProductQuantity(pro.getProductQuantity());
           res.setProductDescription(pro.getProductDescription());
           res.setCategoryName(pro.getProductCategory());
           res.setImageUrl(pro.getImageurl());
           return res;
    }
}
