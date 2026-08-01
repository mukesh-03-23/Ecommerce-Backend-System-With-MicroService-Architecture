package com.mukesh.notificationservice.Controller;

import com.mukesh.notificationservice.DTO.ForgetPasswordRequest;
import com.mukesh.notificationservice.DTO.MessageDetails;
import com.mukesh.notificationservice.Service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MailController {
    private final NotificationService notificationService;

    public MailController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    @PostMapping("/send-notification")
    public ResponseEntity<String> Orderconfirm(@RequestBody MessageDetails Details){
        return new ResponseEntity<>(notificationService.OrderConfirm(Details), HttpStatus.OK);
    }
    @PostMapping("/getotp")
    public ResponseEntity<String> getOtp(@RequestBody ForgetPasswordRequest request){
        return new ResponseEntity<>(notificationService.getOtp(request), HttpStatus.OK);
    }
}
