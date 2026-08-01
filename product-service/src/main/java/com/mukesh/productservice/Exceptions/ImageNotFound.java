package com.mukesh.productservice.Exceptions;

public class ImageNotFound extends RuntimeException{
    public ImageNotFound() {
        super("image not found");
    }

}
