package com.bookstore.exception;

public class AuthorNotFoundException extends RuntimeException {
    public AuthorNotFoundException(String message, Throwable error) {
        super(message, error);
    }

    public AuthorNotFoundException(String message) {
        super(message);
    }
}
