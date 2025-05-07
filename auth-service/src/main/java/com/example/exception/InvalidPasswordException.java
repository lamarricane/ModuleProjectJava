package com.example.exception;

/**
 * Исключение о некорректности пароля.
 */
public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}