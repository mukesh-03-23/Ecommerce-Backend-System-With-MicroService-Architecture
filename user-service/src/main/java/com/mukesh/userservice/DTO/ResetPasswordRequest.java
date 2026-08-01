package com.mukesh.userservice.DTO;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String email;
    private String otp;
    private String password;
    private String confirmPassword;
}
