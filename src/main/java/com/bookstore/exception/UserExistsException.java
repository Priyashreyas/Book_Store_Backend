package com.bookstore.exception;

public class UserExistsException extends RuntimeException {
    public UserExistsException(String message, Throwable error) {
        super(message, error);
    }

    public UserExistsException(String message) {
        super(message);
    }
}
