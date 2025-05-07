package com.example.exception;

/**
 * Исключение о отсутствие пользователя в базе данных.
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}