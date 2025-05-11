package com.example.service;

import com.example.repository.jdbc.BookJdbcRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Сервис для работы с книгами через JDBC.
 * Обеспечивает бизнес-логику и транзакционность операций.
 */
@Service
public class BookJdbcSpecialService {
    private static final Logger logger = LoggerFactory.getLogger(BookJdbcSpecialService.class);
    private final BookJdbcRepository bookRepository;

    public BookJdbcSpecialService(BookJdbcRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Map<String, Object>> getGenreStats() {
        Instant start = Instant.now();
        logger.debug("Fetching book stats...");

        List<Map<String, Object>> books = bookRepository.getGenreStats();

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched book stats in {} ms", duration.toMillis());

        return books;
    }

    public List<Map<String, Object>> getFullAuthorStats() {
        Instant start = Instant.now();
        logger.debug("Fetching author stats...");

        List<Map<String, Object>> authors = bookRepository.getFullAuthorStats();

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched author stats in {} ms", duration.toMillis());

        return authors;
    }

    public List<Map<String, Object>> getAuthorStatsSummary() {
        Instant start = Instant.now();
        logger.debug("Fetching author stats summary...");

        List<Map<String, Object>> authors = bookRepository.getAuthorStatsSummary();

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched author stats summary in {} ms", duration.toMillis());

        return authors;
    }

    public Map<String, Object> getCombinedAuthorStats() {
        Instant start = Instant.now();
        logger.debug("Fetching combined author stats...");

        Map<String, Object> authors = bookRepository.getCombinedAuthorStats();

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched combined author stats in {} ms", duration.toMillis());

        return authors;
    }
}