package com.mukesh.paymentservice.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name="ORDER-SERVICE")
public interface OrderClient {
    @PostMapping("/order/updateInventory/{orderId}")
    String updateInventory(@PathVariable String orderId);
    @PostMapping("/order/updatepayment/{orderId}")
    String UpdatePayment(@PathVariable String orderId);
}
