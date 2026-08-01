package com.mukesh.orderservice.Client;

import com.mukesh.orderservice.DTO.InventoryRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="PRODUCT-SERVICE")
public interface ProductClient {
    @PostMapping("product/Inventoryupdate")
    String UpdateInventory(@RequestBody InventoryRequest request);
}
