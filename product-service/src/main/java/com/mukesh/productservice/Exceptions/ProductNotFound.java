package com.mukesh.productservice.Exceptions;

public class ProductNotFound extends RuntimeException{
    public ProductNotFound(){
        super("product Not found..");
    }
}
