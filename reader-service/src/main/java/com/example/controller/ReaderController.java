package com.example.controller;

import com.example.dto.BookResponse;
import com.example.exception.BookAlreadyInReadListException;
import com.example.exception.BookNotInReadListException;
import com.example.service.ReaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для работы с читательскими списками
 */
@RestController
@RequestMapping("/api/readers")
public class ReaderController {
    private static final Logger logger = LoggerFactory.getLogger(ReaderController.class);
    private final ReaderService readerService;

    public ReaderController(ReaderService readerService) {
        this.readerService = readerService;
    }

    @PostMapping("/{bookId}")
    public ResponseEntity<String> addBookToReadList(
            @PathVariable long bookId,
            @RequestHeader(value = "X-Authenticated-User") String username) {

        logger.info("Received request to add book {} to read list for user {}", bookId, username);

        if (username == null || username.isBlank()) {
            logger.warn("Unauthorized attempt to add book to read list");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Требуется аутентификация");
        }

        try {
            readerService.addBookToReadList(username, bookId);
            return ResponseEntity.ok("Книга успешно добавлена в список прочитанного!");
        } catch (BookAlreadyInReadListException e) {
            logger.warn("Attempt to add duplicate book {} for user {}", bookId, username);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (BookNotInReadListException e) {
            logger.error("Book not found error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error adding book to read list: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Произошла ошибка при добавлении книги в список прочитанного!");
        }
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<?> removeBookFromReadList(
            @PathVariable long bookId,
            @RequestHeader("X-Authenticated-User") String username) {

        logger.info("Received request to remove book {} from read list for user {}", bookId, username);

        if (username == null || username.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Требуется аутентификация!");
        }

        try {
            readerService.removeBookFromReadList(username, bookId);
            return ResponseEntity.ok("Книга успешно удалена из списка прочитанного!");
        } catch (BookNotInReadListException e) {
            logger.warn("Attempt to remove non-existent book {} for user {}", bookId, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error removing book from read list: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Произошла ошибка при удалении книги из списка прочитанного!");
        }
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<?> getBookFromReadList(
            @PathVariable long bookId,
            @RequestHeader("X-Authenticated-User") String username) {

        logger.info("Received request to get book {} from read list for user {}", bookId, username);

        if (username == null || username.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Требуется аутентификация");
        }

        try {
            BookResponse bookResponse = readerService.getBookFromReadList(username, bookId);
            return ResponseEntity.ok(bookResponse);
        } catch (BookNotInReadListException e) {
            logger.warn("Requested book {} not found for user {}", bookId, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error fetching book from read list: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Произошла ошибка при получении информации о книге!");
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllReadBooks(
            Pageable pageable,
            @RequestHeader("X-Authenticated-User") String username) {

        logger.info("Received request to get all books from read list for user {}", username);

        if (username == null || username.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Требуется аутентификация!");
        }

        try {
            Page<BookResponse> books = readerService.getAllReadBooks(username, pageable);
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            logger.error("Error fetching read books list: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Произошла ошибка при получении списка книг!");
        }
    }
}