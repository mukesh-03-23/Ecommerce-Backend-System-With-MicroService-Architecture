package com.mukesh.notificationservice.client;

import com.mukesh.notificationservice.DTO.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="ORDER-SERVICE")
public interface OrderClient {
    @GetMapping("/order/getorder/{orderId}")
    OrderResponse getorderbyId(@PathVariable String orderId);
}
