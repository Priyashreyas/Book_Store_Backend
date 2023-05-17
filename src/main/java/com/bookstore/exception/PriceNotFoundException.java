package com.bookstore.exception;

public class PriceNotFoundException extends RuntimeException {
    public PriceNotFoundException(String message, Throwable error) {
        super(message, error);
    }

    public PriceNotFoundException(String message) {
        super(message);
    }
}
