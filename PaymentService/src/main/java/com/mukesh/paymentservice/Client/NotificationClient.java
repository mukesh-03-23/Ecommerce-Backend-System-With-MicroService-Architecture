package com.mukesh.paymentservice.Client;

import com.mukesh.paymentservice.DTO.MessageDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="NOTIFICATION-SERVICE")
public interface NotificationClient {
    @PostMapping("/send-notification")
    String Orderconfirm(@RequestBody MessageDetails Details);
}
