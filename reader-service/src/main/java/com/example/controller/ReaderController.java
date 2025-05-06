package com.example.controller;

import com.example.dto.BookResponse;
import com.example.exception.BookAlreadyInReadListException;
import com.example.exception.BookNotInReadListException;
import com.example.model.Reader;
import com.example.service.ReaderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            return ResponseEntity.badRequest().body("Authentication required");
        }
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
    public ResponseEntity<BookResponse> getBookFromReadList(
            @PathVariable Long bookId,
            @RequestHeader("X-Authenticated-User") String username) {
        BookResponse bookResponse = readerService.getBookFromReadList(username, bookId);
        return ResponseEntity.ok(bookResponse);
    }

    @GetMapping
    public ResponseEntity<Page<BookResponse>> getAllReadBooks(
            Pageable pageable,
            @RequestHeader("X-Authenticated-User") String username) {
        Page<BookResponse> books = readerService.getAllReadBooks(username, pageable);
        return ResponseEntity.ok(books);
    }
}