package com.mukesh.userservice.Client;

import com.mukesh.userservice.DTO.ForgetPasswordRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="NOTIFICATION-SERVICE")
public interface NotificationClient {
    @PostMapping("/getotp")
    String getOtp(ForgetPasswordRequest forgetpasswordRequest);
}
