package com.example.exception;

public class BookNotInReadListException extends RuntimeException {
    public BookNotInReadListException(String message) {
        super(message);
    }
}
