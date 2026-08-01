package com.mukesh.cartservice.Exceptions;

public class CartNotFoundException extends RuntimeException{
    public CartNotFoundException() {
        super("cart not found with userId");
    }
}
