package com.example.exception;

/**
 * Исключение отсутствия книги в списке прочитанного
 */
public class BookNotInReadListException extends RuntimeException {
    public BookNotInReadListException(String message) {
        super(message);
    }
}
