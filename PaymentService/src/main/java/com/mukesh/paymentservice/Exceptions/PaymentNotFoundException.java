package com.mukesh.paymentservice.Exceptions;

import org.springframework.web.bind.annotation.ExceptionHandler;


public class PaymentNotFoundException extends RuntimeException{
    public PaymentNotFoundException(String message) {
        super(message);
    }
}
