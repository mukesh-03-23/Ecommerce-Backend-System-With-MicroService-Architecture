package com.mukesh.userservice.Exceptions;

public class UserExisted extends RuntimeException{
    public UserExisted(){
        super("user already existed please try to login..");
    }
}
