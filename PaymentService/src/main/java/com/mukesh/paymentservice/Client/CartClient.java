package com.mukesh.paymentservice.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name="CART-SERVICE")
public interface CartClient {
    @DeleteMapping("/cart/clear-cart/{email}")
    String clearcart(@PathVariable String email);
    @GetMapping("/cart/productIds")
    List<Long> getproductIds(String email);
}
