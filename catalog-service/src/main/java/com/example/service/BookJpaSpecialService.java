package com.example.service;

import com.example.repository.jpa.BookJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class BookJpaSpecialService {
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);
    private final BookJpaRepository bookRepository;

    public BookJpaSpecialService(BookJpaRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // Жанровая статистика
    public List<Map<String, Object>> getGenreStats() {
        Instant start = Instant.now();
        logger.debug("Fetching book stats...");

        List<Map<String, Object>> books = bookRepository.getGenreStats();

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched book stats in {} ms", duration.toMillis());

        return books;
    }

    // Полная статистика по авторам
    public List<Map<String, Object>> getFullAuthorStats() {
        Instant start = Instant.now();
        logger.debug("Fetching author stats...");

        List<Map<String, Object>> authors = bookRepository.getAuthorStats();

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched author stats in {} ms", duration.toMillis());

        return authors;
    }

    // Краткая статистика (топ-10)
    public List<Map<String, Object>> getAuthorStatsSummary() {
        Instant start = Instant.now();
        logger.debug("Fetching author stats summary...");

        List<Map<String, Object>> authors = bookRepository.getAuthorStatsSummary();

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched author stats summary in {} ms", duration.toMillis());

        return authors;
    }

    // Комбинированный метод с многоуровневым кэшированием
    //@Cacheable(value = "authorStats", key = "'combined'")
    public Map<String, Object> getCombinedAuthorStats() {
        Instant start = Instant.now();
        logger.debug("Fetching combined author stats...");
        Map<String, Object> authors = Map.of(
                "summary", bookRepository.getAuthorStatsSummary(),
                "fullStatsLastUpdated", LocalDateTime.now()
        );
        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched combined author stats in {} ms", duration.toMillis());
        return authors;
    }
}
