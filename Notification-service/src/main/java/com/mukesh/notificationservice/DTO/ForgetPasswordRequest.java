package com.mukesh.notificationservice.DTO;

import lombok.Data;

@Data
public class ForgetPasswordRequest {
    private String email;
    private String username;
}
