package com.mukesh.orderservice.Client;

import com.mukesh.orderservice.DTO.PaymentOrderResponse;
import com.mukesh.orderservice.DTO.PaymentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="PAYMENT-SERVICE")
public interface PaymentClient {
    @PostMapping("/payments/createorder")
    PaymentOrderResponse CreatePayment(@RequestBody PaymentRequest request);
}
