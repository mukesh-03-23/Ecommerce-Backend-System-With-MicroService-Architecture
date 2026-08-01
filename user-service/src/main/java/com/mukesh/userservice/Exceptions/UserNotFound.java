package com.mukesh.userservice.Exceptions;

public class UserNotFound extends RuntimeException{
    public UserNotFound(){
        super("No such user with that email");
    }
}
