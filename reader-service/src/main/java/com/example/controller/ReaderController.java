package com.example.controller;

import com.example.dto.ReaderRequest;
import com.example.model.Reader;
import com.example.service.ReaderService;
import jakarta.persistence.EntityNotFoundException;
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

    @PostMapping
    public ResponseEntity<String> addBook(@RequestBody ReaderRequest request) {
        try {
            readerService.addBookToReader(request);
            return ResponseEntity.ok("Книга успешно добавлена в список прочитанного!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<String> removeBook(
            @RequestParam String username,
            @RequestParam Long bookId) {
        try {
            readerService.removeBookFromReader(username, bookId);
            return ResponseEntity.ok("Книга успешно удалена из списка прочитанного!");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{username}")
    public Page<Reader> getBooks(
            @PathVariable String username,
            Pageable pageable) {
        return readerService.getReaderBooks(username, pageable);
    }
}