package com.mukesh.userservice.DTO;

import lombok.Data;

@Data
public class ForgetPasswordRequest {
    private String email;
    private String username;
}
