package com.example.controller;

import com.example.exception.BookAlreadyInReadListException;
import com.example.exception.BookNotInReadListException;
import com.example.model.Reader;
import com.example.service.ReaderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/readers")
public class ReaderController {

    private final ReaderService readerService;

    public ReaderController(ReaderService readerService) {
        this.readerService = readerService;
    }

    @PostMapping("/{bookId}")
    public ResponseEntity<?> addBookToReadList(
            @PathVariable Long bookId,
            @RequestHeader(value = "X-Authenticated-User") String username) {

        if (username == null) {
            log.error("X-Authenticated-User header is missing");
            return ResponseEntity.badRequest().body("Authentication required");
        }

        log.info("Processing request for user: {}", username);

        try {
            Reader reader = readerService.addBookToReadList(username, bookId);
            return ResponseEntity.ok(reader);
        } catch (BookAlreadyInReadListException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (BookNotInReadListException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> removeBookFromReadList(
            @PathVariable Long bookId,
            @RequestHeader("X-Authenticated-User") String username) {
        readerService.removeBookFromReadList(username, bookId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<Reader> getBookFromReadList(
            @PathVariable Long bookId,
            @RequestHeader("X-Authenticated-User") String username) {
        Reader reader = readerService.getBookFromReadList(username, bookId);
        return ResponseEntity.ok(reader);
    }

    @GetMapping
    public ResponseEntity<Page<Reader>> getAllReadBooks(
            Pageable pageable,
            @RequestHeader("X-Authenticated-User") String username) {
        Page<Reader> readers = readerService.getAllReadBooks(username, pageable);
        return ResponseEntity.ok(readers);
    }
}