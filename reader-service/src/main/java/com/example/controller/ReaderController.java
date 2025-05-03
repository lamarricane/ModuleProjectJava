package com.example.controller;


import com.example.model.Reader;
import com.example.service.ReaderService;
import com.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readers")
public class ReaderController {

    private final ReaderService readerService;
    private final UserService userService;

    public ReaderController(ReaderService readerService, UserService userService){
        this.readerService = readerService;
        this.userService = userService;
    }

    @PostMapping("/books/{bookId}")
    public ResponseEntity<Reader> addBookToRead(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long bookId) {
        UUID userId = userService.findByUsername(userDetails.getUsername()).getId();
        Reader reader = readerService.addBookToRead(userId, bookId);
        return ResponseEntity.ok(reader);
    }

    @GetMapping
    public ResponseEntity<List<Reader>> getReadBooks(
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = userService.findByUsername(userDetails.getUsername()).getId();
        List<Reader> readers = readerService.getReadBooks(userId);
        return ResponseEntity.ok(readers);
    }

    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<Void> removeBookFromRead(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long bookId) {
        UUID userId = userService.findByUsername(userDetails.getUsername()).getId();
        readerService.removeBookFromRead(userId, bookId);
        return ResponseEntity.noContent().build();
    }
}
