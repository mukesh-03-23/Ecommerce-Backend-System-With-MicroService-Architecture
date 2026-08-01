package com.mukesh.cartservice.Client;

import com.mukesh.cartservice.DTO.ProductResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="PRODUCT-SERVICE")
public interface ProductClient{
    @GetMapping("/product/getproduct/{product_id}")
    ProductResponseDto getproductById(@PathVariable Long product_id);
}
