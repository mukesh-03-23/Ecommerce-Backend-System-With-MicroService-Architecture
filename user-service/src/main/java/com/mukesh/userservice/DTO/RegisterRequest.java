package com.mukesh.userservice.DTO;


import lombok.Data;

@Data
public class RegisterRequest {
    private String fullname;
    private String email;
    private String password;
    private String confirmPassword;
    private String role;
}
