package com.mukesh.userservice.Exceptions;

public class WrongCredentials extends RuntimeException{
    public WrongCredentials(){
        super("Wrong credentials try again..");
    }
}
