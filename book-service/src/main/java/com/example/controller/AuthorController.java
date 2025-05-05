package com.example.controller;

import com.example.dto.AuthorRequest;
import com.example.model.Author;
import com.example.service.AuthorService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/catalog/authors")
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping
    public ResponseEntity<String> createAuthor(@Valid @RequestBody AuthorRequest authorRequest) {
        try {
            Author author = authorService.convertToAuthor(authorRequest);
            authorService.createAuthor(author);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Автор с ID: " + author.getId() + " успешно создан!");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Ошибка при создании автора: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateAuthor(@Valid @PathVariable long id, @RequestBody AuthorRequest authorRequest) {
        try {
            Author author = authorService.convertToAuthor(authorRequest);
            authorService.update(id, author);
            return ResponseEntity.ok("Автор с ID: " + id + " успешно обновлен!");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAuthor(@PathVariable long id) {
        try {
            authorService.deleteAuthor(id);
            return ResponseEntity.ok("Автор с ID: " + id + " успешно удален!");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @GetMapping
    public Page<Author> getAllAuthors(Pageable pageable) {
        return authorService.getAllAuthors(pageable);
    }

    @GetMapping("/{id}")
    public Optional<Author> getAuthorById(@PathVariable long id) {
        return authorService.getAuthorById(id);
    }

    @GetMapping("/location/{location}")
    public Page<Author> getAuthorsByLocation(@PathVariable String location, Pageable pageable) {
        return authorService.getByLocation(location, pageable);
    }

    @GetMapping("/genre/{genre}")
    public Page<Author> getAuthorsByBookGenre(@PathVariable String genre, Pageable pageable) {
        return authorService.getByBookGenre(genre, pageable);
    }

    @GetMapping("/birthdate")
    public Page<Author> getAuthorsByBirthDate(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end,
            Pageable pageable) {
        return authorService.getByBirthDateBetween(start, end, pageable);
    }

    @GetMapping("/search")
    public Page<Author> searchAuthorsByName(
            @RequestParam String name,
            Pageable pageable) {
        return authorService.findByNameContainingIgnoreCase(name, pageable);
    }

    @GetMapping("/sort/name")
    public Page<Author> sortAuthorsByName(
            @RequestParam(defaultValue = "asc") String direction,
            Pageable pageable) {
        return "desc".equalsIgnoreCase(direction) ?
                authorService.findAllByOrderByNameDesc(pageable) :
                authorService.findAllByOrderByNameAsc(pageable);
    }

    @GetMapping("/sort/birthdate")
    public Page<Author> sortAuthorsByBirthDate(
            @RequestParam(defaultValue = "asc") String direction,
            Pageable pageable) {
        return "desc".equalsIgnoreCase(direction) ?
                authorService.findAllByOrderByBirthDateDesc(pageable) :
                authorService.findAllByOrderByBirthDateAsc(pageable);
    }

    @GetMapping("/sort/books")
    public Page<Author> sortAuthorsByBooksCount(
            @RequestParam(defaultValue = "asc") String direction,
            Pageable pageable) {
        return "desc".equalsIgnoreCase(direction) ?
                authorService.findAllOrderByBooksCountDesc(pageable) :
                authorService.findAllOrderByBooksCountAsc(pageable);
    }
}