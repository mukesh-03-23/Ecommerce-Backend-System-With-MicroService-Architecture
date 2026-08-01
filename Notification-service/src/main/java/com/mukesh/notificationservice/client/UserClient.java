package com.mukesh.notificationservice.client;

import com.mukesh.notificationservice.DTO.UserResponse;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name="USER-SERVICE")
public interface UserClient {
    @GetMapping("/user/getuser")
    UserResponse GetuserByEmail(@RequestParam String  email);
}
