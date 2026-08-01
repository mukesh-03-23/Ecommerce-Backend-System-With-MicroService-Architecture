package com.mukesh.productservice.Exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ImageNotFound.class)
    public ResponseEntity<String> imageNotFound(ImageNotFound ig){
         return new ResponseEntity<>(ig.getMessage(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(ProductNotFound.class)
    public ResponseEntity<String> productNotFound(ProductNotFound pn){
        return new ResponseEntity<>(pn.getMessage(), HttpStatus.NOT_FOUND);
    }
}
