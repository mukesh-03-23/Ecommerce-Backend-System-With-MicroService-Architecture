package com.mukesh.notificationservice.client;

import com.mukesh.notificationservice.DTO.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="PRODUCT-SERVICE")
public interface ProductClient {
    @GetMapping("product/getproduct/{product_id}")
    ProductResponse getproductById(@PathVariable Long product_id);
}
