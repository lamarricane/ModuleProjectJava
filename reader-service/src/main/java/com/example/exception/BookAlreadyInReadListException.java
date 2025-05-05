package com.example.exception;

public class BookAlreadyInReadListException extends RuntimeException {
  public BookAlreadyInReadListException(String message) {
    super(message);
  }
}