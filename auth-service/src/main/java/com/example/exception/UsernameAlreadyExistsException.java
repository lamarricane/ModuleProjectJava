package com.example.exception;

/**
 * Исключение о занятости имени пользователя.
 */
public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}