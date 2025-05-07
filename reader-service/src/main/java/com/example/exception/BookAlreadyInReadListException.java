package com.example.exception;

/**
 * Исключение "книга уже есть в списке прочитанного"
 */
public class BookAlreadyInReadListException extends RuntimeException {
  public BookAlreadyInReadListException(String message) {
    super(message);
  }
}