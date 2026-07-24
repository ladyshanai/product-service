package com.product.service.exception;

public class DuplicateProductException extends RuntimeException {
    public DuplicateProductException(String message, Throwable cause) {
        super(message, cause);
    }
}
