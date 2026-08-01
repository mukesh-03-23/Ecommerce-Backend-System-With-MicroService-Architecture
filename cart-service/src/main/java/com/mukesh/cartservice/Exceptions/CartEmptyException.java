package com.mukesh.cartservice.Exceptions;

public class CartEmptyException extends RuntimeException{
    public CartEmptyException(){
        super("cart is Empty..");
    }
}
