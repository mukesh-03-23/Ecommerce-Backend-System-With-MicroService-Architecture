package com.mukesh.orderservice.Client;

import com.mukesh.orderservice.DTO.CartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name="CART-SERVICE")
public interface CartClient {
    @GetMapping("/cart/getcart/{email}")
    CartResponse getcartByEmail(@PathVariable String email);
}
