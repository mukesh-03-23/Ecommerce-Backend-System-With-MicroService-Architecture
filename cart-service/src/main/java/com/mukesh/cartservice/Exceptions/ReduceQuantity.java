package com.mukesh.cartservice.Exceptions;

public class ReduceQuantity extends RuntimeException{
    public ReduceQuantity(){
        super("please reduce the quantity");
    }
}
