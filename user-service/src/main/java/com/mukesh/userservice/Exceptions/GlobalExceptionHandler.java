package com.mukesh.userservice.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFound.class)
    public ResponseEntity<String> userNotFoundException(UserNotFound userNotFound){
        return new ResponseEntity<>(userNotFound.getMessage(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(WrongCredentials.class)
    public ResponseEntity<String> wrongCredentials(WrongCredentials wrongCredentials){
        return new ResponseEntity<>(wrongCredentials.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(UserExisted.class)
    public ResponseEntity<String> UserExisted(UserExisted userExisted){
        return new ResponseEntity<>(userExisted.getMessage(), HttpStatus.CONFLICT);
    }
}
