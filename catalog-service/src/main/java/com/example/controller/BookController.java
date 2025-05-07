package com.example.controller;

import com.example.dto.BookDetailsResponse;
import com.example.dto.BookRequest;
import com.example.model.Book;
import com.example.service.BookService;
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
 * Контроллер для эндпоинтов при работе с каталогом книг
 */
@RestController
@RequestMapping("/api/catalog/books")
public class BookController {
    private static final Logger logger = LoggerFactory.getLogger(BookController.class);
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<String> createBook(@Valid @RequestBody BookRequest bookRequest) {
        try {
            Book book = bookService.convertToBook(bookRequest);
            bookService.createBook(book);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Книга с ID: " + book.getId() + " успешно создана!");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error creating book: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Произошла ошибка при создании книги!");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateBook(@Valid @PathVariable long id, @RequestBody BookRequest bookRequest) {
        try {
            Book book = bookService.convertToBook(bookRequest);
            bookService.update(id, book);
            return ResponseEntity.ok("Книга с ID: " + id + " успешно обновлена!");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating book: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Произошла ошибка при изменении книги!");
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.ok("Книга с ID: " + id + " успешно удалена!");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error deleting book: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Произошла ошибка при удалении книги!");
        }
    }

    @GetMapping
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookService.getAllBooks(pageable);
    }

    @GetMapping("/{id}")
    public Book getBookById(@PathVariable long id) {
        return bookService.getBookById(id);
    }

    @GetMapping("/genre/{genre}")
    public Page<Book> getBooksByGenre(@PathVariable String genre, Pageable pageable) {
        return bookService.getByGenre(genre, pageable);
    }

    @GetMapping("/period")
    public Page<Book> getBooksByPeriod(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end,
            Pageable pageable) {
        return bookService.getByPeriod(start, end, pageable);
    }

    @GetMapping("/size")
    public Page<Book> getBooksBySize(
            @RequestParam int min,
            @RequestParam int max,
            Pageable pageable) {
        return bookService.getBySize(min, max, pageable);
    }

    @GetMapping("/author")
    public Page<Book> getBooksByAuthor(
            @RequestParam String authorName,
            Pageable pageable) {
        return bookService.getByAuthor(authorName, pageable);
    }

    @GetMapping("/search")
    public Page<Book> searchBooksByTitle(
            @RequestParam String title,
            Pageable pageable) {
        return bookService.getByTitle(title, pageable);
    }

    @GetMapping("/sort/title")
    public Page<Book> sortBooksByTitle(
            @RequestParam(defaultValue = "asc") String direction,
            Pageable pageable) {
        return "desc".equalsIgnoreCase(direction) ?
                bookService.getOrderByTitleDesc(pageable) :
                bookService.getOrderByTitleAsc(pageable);
    }

    @GetMapping("/sort/size")
    public Page<Book> sortBooksBySize(
            @RequestParam(defaultValue = "asc") String direction,
            Pageable pageable) {
        return "desc".equalsIgnoreCase(direction) ?
                bookService.getOrderBySizeDesc(pageable) :
                bookService.getOrderBySizeAsc(pageable);
    }

    @GetMapping("/sort/date")
    public Page<Book> sortBooksByDate(
            @RequestParam(defaultValue = "asc") String direction,
            Pageable pageable) {
        return "desc".equalsIgnoreCase(direction) ?
                bookService.getOrderByPublishingDateDesc(pageable) :
                bookService.getOrderByPublishingDateAsc(pageable);
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<?> getBookDetails(@PathVariable long id) {
        try {
            BookDetailsResponse response = bookService.convertToBookDetails(getBookById(id));
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Книга с ID " + id + " не найдена в каталоге!");
        } catch (Exception e) {
            logger.error("Error fetching book details: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Произошла ошибка при получении информации о книге!");
        }
    }
}