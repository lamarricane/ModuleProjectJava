package com.example.service;

import com.example.dto.jooq.AuthorStatsDto;
import com.example.dto.jooq.AuthorSummaryDto;
import com.example.dto.jooq.CombinedStatsDto;
import com.example.dto.jooq.GenreStatsDto;
import com.example.repository.jooq.BookJooqRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Сервис для работы с книгами через Jooq.
 * Обеспечивает бизнес-логику и транзакционность операций.
 */
@Service
public class BookJooqSpecialService {
    private static final Logger logger = LoggerFactory.getLogger(BookJooqSpecialService.class);
    private final BookJooqRepository bookRepository;
    private final ObjectMapper objectMapper;

    public BookJooqSpecialService(BookJooqRepository bookRepository, ObjectMapper objectMapper) {
        this.bookRepository = bookRepository;
        this.objectMapper = objectMapper;
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

    private <T> List convertToMapList(List<T> dtos) {
        return dtos.stream()
                .map(dto -> objectMapper.convertValue(dto, Map.class))
                .collect(Collectors.toList());
    }
}