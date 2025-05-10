package com.example.service.jooq;

import com.example.dto.BookDetailsResponse;
import com.example.dto.BookRequest;
import com.example.dto.jooq.AuthorStatsDto;
import com.example.dto.jooq.AuthorSummaryDto;
import com.example.dto.jooq.CombinedStatsDto;
import com.example.dto.jooq.GenreStatsDto;
import com.example.model.Author;
import com.example.model.Book;
import com.example.repository.jooq.AuthorJooqRepository;
import com.example.repository.jooq.BookJooqRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookJooqService {
    private static final Logger logger = LoggerFactory.getLogger(BookJooqService.class);
    private final DSLContext dsl;
    private final BookJooqRepository bookRepository;
    private final AuthorJooqRepository authorRepository;
    private final ObjectMapper objectMapper;

    public BookJooqService(BookJooqRepository bookRepository, AuthorJooqRepository authorRepository, DSLContext dsl, ObjectMapper objectMapper) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.dsl = dsl;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void create(Book book) {
        Instant start = Instant.now();
        logger.info("Creating new book: {}", book.getTitle());

        if (book.getAuthor() == null || book.getAuthor().getId() == null) {
            throw new IllegalArgumentException("Author must be specified");
        }

        Author author = authorRepository.findById(book.getAuthor().getId())
                .orElseThrow(() -> new EntityNotFoundException("Автор не найден"));
        book.setAuthor(author);

        try {
            Book savedBook = bookRepository.save(book);
            logger.info("Book created successfully with ID: {}", savedBook.getId());

            Duration duration = Duration.between(start, Instant.now());
            logger.debug("Book creation completed in {} ms", duration.toMillis());

        } catch (Exception e) {
            logger.error("Error creating book: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void update(long id, Book updatedBook) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Книга не найдена!"));

        Author author = authorRepository.findById(updatedBook.getAuthor().getId())
                .orElseThrow(() -> new EntityNotFoundException("Автор не найден!"));

        book.setTitle(updatedBook.getTitle());
        book.setGenre(updatedBook.getGenre());
        book.setPagesNumber(updatedBook.getPagesNumber());
        book.setPublishingDate(updatedBook.getPublishingDate());
        book.setDescription(updatedBook.getDescription());
        book.setAuthor(author);

        bookRepository.save(book);
    }

    @Transactional
    public void delete(long id) {
        Instant start = Instant.now();
        logger.info("Deleting book with ID: {}", id);

        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("Книга не найдена!");
        }
        bookRepository.deleteById(id);

        logger.info("Author with ID: {} deleted successfully", id);
        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Author deletion completed in {} ms", duration.toMillis());
    }

    public Page<Book> getAll(Pageable pageable) {
        Instant start = Instant.now();
        logger.debug("Fetching all books with pagination: {}", pageable);

        Page<Book> books = bookRepository.findAll(pageable);

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched {} all books in {} ms", books.getTotalElements(), duration.toMillis());

        return books;
    }

    public Book getById(long id) {
        Instant start = Instant.now();
        logger.debug("Fetching book by ID: {}", id);

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Book not found with ID: {}", id);
                    return new EntityNotFoundException("Книга не найдена!");
                });

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Book fetched in {} ms", duration.toMillis());

        return book;
    }

    public Page<Book> getByGenre(String genre, Pageable pageable) {
        Instant start = Instant.now();
        logger.debug("Fetching books by genre with pagination: {}", pageable);

        Page<Book> books = bookRepository.findByGenre(genre, pageable);

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched {} books by genre in {} ms", books.getTotalElements(), duration.toMillis());

        return books;
    }

    public Page<Book> getByPeriod(LocalDate lowBound, LocalDate highBound, Pageable pageable) {
        Instant start = Instant.now();
        logger.debug("Fetching books by publishing date with pagination: {}", pageable);

        Page<Book> books = bookRepository.findByPublishingDateBetween(lowBound, highBound, pageable);

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched {} books by publishing date in {} ms", books.getTotalElements(), duration.toMillis());

        return books;
    }

    public Page<Book> getBySize(int min, int max, Pageable pageable) {
        Instant start = Instant.now();
        logger.debug("Fetching books by page number with pagination: {}", pageable);

        Page<Book> books = bookRepository.findByPagesNumberBetween(min, max, pageable);

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched {} books by page number in {} ms", books.getTotalElements(), duration.toMillis());

        return books;
    }

    public Page<Book> getByAuthor(String authorName, Pageable pageable) {
        Instant start = Instant.now();
        logger.debug("Fetching books by authors with pagination: {}", pageable);

        Page<Book> books = bookRepository.findByAuthorNameContainingIgnoreCase(authorName, pageable);

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched {} books by authors in {} ms", books.getTotalElements(), duration.toMillis());

        return books;
    }

    public Page<Book> getByTitle(String title, Pageable pageable) {
        Instant start = Instant.now();
        logger.debug("Fetching books by title with pagination: {}", pageable);

        Page<Book> books = bookRepository.findByTitleContainingIgnoreCase(title, pageable);

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched {} books by title in {} ms", books.getTotalElements(), duration.toMillis());

        return books;
    }

    public Page<Book> getOrderByTitleAsc(Pageable pageable) {
        Instant start = Instant.now();
        logger.debug("Fetching all books by title with pagination: {}", pageable);

        Page<Book> books = bookRepository.findAllByOrderByTitleAsc(pageable);

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched {} books by title in {} ms", books.getTotalElements(), duration.toMillis());

        return books;
    }

    public Page<Book> getOrderByTitleDesc(Pageable pageable) {
        Instant start = Instant.now();
        logger.debug("Fetching all books by title with pagination: {}", pageable);

        Page<Book> books = bookRepository.findAllByOrderByTitleDesc(pageable);

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched {} books by title in {} ms", books.getTotalElements(), duration.toMillis());

        return books;
    }

    public Page<Book> getOrderBySizeAsc(Pageable pageable) {
        Instant start = Instant.now();
        logger.debug("Fetching all books by page number with pagination: {}", pageable);

        Page<Book> books = bookRepository.findAllByOrderByPagesNumberAsc(pageable);

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched {} books by page number in {} ms", books.getTotalElements(), duration.toMillis());

        return books;
    }

    public Page<Book> getOrderBySizeDesc(Pageable pageable) {
        Instant start = Instant.now();
        logger.debug("Fetching all books by page number with pagination: {}", pageable);

        Page<Book> books = bookRepository.findAllByOrderByPagesNumberDesc(pageable);

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched {} books by page number in {} ms", books.getTotalElements(), duration.toMillis());

        return books;
    }

    public Page<Book> getOrderByPublishingDateAsc(Pageable pageable) {
        Instant start = Instant.now();
        logger.debug("Fetching all books by publishing date with pagination: {}", pageable);

        Page<Book> books = bookRepository.findAllByOrderByPublishingDateAsc(pageable);

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched {} books by publishing date in {} ms", books.getTotalElements(), duration.toMillis());

        return books;
    }

    public Page<Book> getOrderByPublishingDateDesc(Pageable pageable) {
        Instant start = Instant.now();
        logger.debug("Fetching all books by publishing date with pagination: {}", pageable);

        Page<Book> books = bookRepository.findAllByOrderByPublishingDateDesc(pageable);

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched {} books by publishing date in {} ms", books.getTotalElements(), duration.toMillis());

        return books;
    }

    public List<Map<String, Object>> getFullAuthorStats() {
        Instant start = Instant.now();
        logger.debug("Fetching author stats...");

        List<AuthorStatsDto> authors = bookRepository.getFullAuthorStats();

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched author stats in {} ms", duration.toMillis());
        return convertToMapList(authors);
    }

    public List<Map<String, Object>> getGenreStats() {
        Instant start = Instant.now();
        logger.debug("Fetching books stats...");

        List<GenreStatsDto> books = bookRepository.getGenreStats();

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched books stats in {} ms", duration.toMillis());
        return convertToMapList(books);
    }

    public List<Map<String, Object>> getAuthorStatsSummary() {
        Instant start = Instant.now();
        logger.debug("Fetching author summary...");

        List<AuthorSummaryDto> authors = bookRepository.getAuthorStatsSummary();

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched author summary in {} ms", duration.toMillis());
        return convertToMapList(authors);
    }

    public Map<String, Object> getCombinedAuthorStats() {
        Instant start = Instant.now();
        logger.debug("Fetching combined author stats...");

        CombinedStatsDto authors = bookRepository.getCombinedAuthorStats();

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched combined author stats in {} ms", duration.toMillis());
        return objectMapper.convertValue(authors, Map.class);
    }

    public Book convertToBook(BookRequest dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setGenre(dto.getGenre());
        book.setPagesNumber(dto.getPagesNumber());
        book.setPublishingDate(dto.getPublishingDate());
        book.setDescription(dto.getDescription());
        book.setAuthor(authorRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new EntityNotFoundException("Автор не найден!")));
        return book;
    }

    public BookDetailsResponse convertToBookDetails(Book book) {
        BookDetailsResponse response = new BookDetailsResponse();
        response.setTitle(book.getTitle());
        response.setGenre(book.getGenre());
        response.setPagesNumber(book.getPagesNumber());
        response.setPublishingDate(book.getPublishingDate());
        response.setDescription(book.getDescription());

        // Гарантируем, что author никогда не будет null
        BookDetailsResponse.AuthorInfo authorInfo = new BookDetailsResponse.AuthorInfo();

        if (book.getAuthor() != null) {
            authorInfo.setName(book.getAuthor().getName());
        } else {
            // Устанавливаем значения по умолчанию, если автор не указан
            authorInfo.setName("Неизвестный автор");
            logger.warn("Book {} has no author assigned", book.getId());
        }

        response.setAuthor(authorInfo);
        response.setAddedAt(LocalDateTime.now());

        return response;
    }

    private <T> List convertToMapList(List<T> dtos) {
        return dtos.stream()
                .map(dto -> objectMapper.convertValue(dto, Map.class))
                .collect(Collectors.toList());
    }
}