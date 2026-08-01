package com.mukesh.cartservice.Exceptions;

public class OutOfStock extends RuntimeException{
    public OutOfStock() {
        super("Out of Stock");
    }
}
