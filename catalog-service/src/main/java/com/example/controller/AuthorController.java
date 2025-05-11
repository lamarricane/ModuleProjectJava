package com.example.controller;

import com.example.dto.AuthorRequest;
import com.example.model.Author;
import com.example.service.AuthorService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Контроллер для эндпоинтов при работе с каталогом авторов
 */
@RestController
@RequestMapping("/api/catalog/authors")
public class AuthorController {
    private static final Logger logger = LoggerFactory.getLogger(AuthorController.class);
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping
    public ResponseEntity<String> createAuthor(@Valid @RequestBody AuthorRequest authorRequest) {
        try {
            Author author = authorService.convertToAuthor(authorRequest);
            authorService.create(author);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Автор с ID: " + author.getId() + " успешно создан!");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error creating author: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Произошла ошибка при создании автора!");
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
                    .body("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating author: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Произошла ошибка при изменении автора!");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAuthor(@PathVariable long id) {
        try {
            authorService.delete(id);
            return ResponseEntity.ok("Автор с ID: " + id + " успешно удален!");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error deleting author: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Произошла ошибка при удалении автора!");
        }
    }

    @GetMapping
    public Page<Author> getAllAuthors(Pageable pageable) {
        return authorService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public Author getAuthorById(@PathVariable long id) {
        return authorService.getById(id);
    }

    @GetMapping("/search")
    public Page<Author> searchAuthorsByName(
            @RequestParam String name,
            Pageable pageable) {
        return authorService.getByName(name, pageable);
    }

    @GetMapping("/birthdate")
    public Page<Author> getAuthorsByBirthDate(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end,
            Pageable pageable) {
        return authorService.getByBirthDateBetween(start, end, pageable);
    }

    @GetMapping("/location/{location}")
    public Page<Author> getAuthorsByLocation(@PathVariable String location, Pageable pageable) {
        return authorService.getByLocation(location, pageable);
    }

    @GetMapping("/genre/{genre}")
    public Page<Author> getAuthorsByBookGenre(@PathVariable String genre, Pageable pageable) {
        return authorService.getByBookGenre(genre, pageable);
    }

    @GetMapping("/sort/name")
    public Page<Author> sortAuthorsByName(
            @RequestParam(defaultValue = "asc") String direction,
            Pageable pageable) {
        return "desc".equalsIgnoreCase(direction) ?
                authorService.getAllByOrderByNameDesc(pageable) :
                authorService.getAllByOrderByNameAsc(pageable);
    }

    @GetMapping("/sort/birthdate")
    public Page<Author> sortAuthorsByBirthDate(
            @RequestParam(defaultValue = "asc") String direction,
            Pageable pageable) {
        return "desc".equalsIgnoreCase(direction) ?
                authorService.getAllByOrderByBirthDateDesc(pageable) :
                authorService.getAllByOrderByBirthDateAsc(pageable);
    }

    @GetMapping("/sort/books")
    public Page<Author> sortAuthorsByBooksCount(
            @RequestParam(defaultValue = "asc") String direction,
            Pageable pageable) {
        return "desc".equalsIgnoreCase(direction) ?
                authorService.getAllOrderByBooksCountDesc(pageable) :
                authorService.getAllOrderByBooksCountAsc(pageable);
    }
}