package com.mukesh.notificationservice.DTO;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.UUID;
@Data
public class MessageDetails {
    private String email;
    private String orderId;
}
