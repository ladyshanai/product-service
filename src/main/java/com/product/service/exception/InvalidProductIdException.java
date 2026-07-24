package com.product.service.exception;

public class InvalidProductIdException extends RuntimeException {
    public InvalidProductIdException(String message) {
        super(message);
    }
}
