package com.example.service;

import com.example.dto.BookResponse;
import com.example.exception.BookAlreadyInReadListException;
import com.example.exception.BookNotInReadListException;
import com.example.model.Reader;
import com.example.repository.ReaderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Сервис для работы с читательскими списками:
 * - добавление/удаление книг в список чтения;
 * - получение информации о книгах в списке.
 */
@Service
@RequiredArgsConstructor
public class ReaderService {
    private static final Logger logger = LoggerFactory.getLogger(ReaderService.class);
    private final ReaderRepository readerRepository;
    private final RestTemplate restTemplate; // для проверки существования книги в book-service

    @Transactional
    public void addBookToReadList(String username, long bookId) {
        Instant start = Instant.now();
        logger.info("Adding book {} to read list for user {}", bookId, username);

        checkIfBookExists(bookId);

        if (readerRepository.existsByUsernameAndBookId(username, bookId)) {
            logger.warn("Book {} already in read list for user {}", bookId, username);
            throw new BookAlreadyInReadListException(
                    "Книга с ID: " + bookId + " уже есть в списке прочитанного!");
        }

        Reader reader = new Reader();
        reader.setUsername(username);
        reader.setBookId(bookId);
        reader.setAddedAt(LocalDateTime.now());

        readerRepository.save(reader);
        logger.info("Book {} added to read list for user {}", bookId, username);

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Add book to read list completed in {} ms", duration.toMillis());
    }

    private void checkIfBookExists(long bookId) {
        try {
            Instant start = Instant.now();
            logger.debug("Checking if book {} exists", bookId);

            ResponseEntity<Void> response = restTemplate.getForEntity(
                    "http://catalog-service/api/catalog/books/{id}",
                    Void.class,
                    bookId);

            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.warn("Book {} not found", bookId);
                throw new BookNotInReadListException("Книга с ID: " + bookId + " не найдена!");
            }

            Duration duration = Duration.between(start, Instant.now());
            logger.debug("Book existence check completed in {} ms", duration.toMillis());

        } catch (RestClientException e) {
            logger.error("Error checking book existence: {}", e.getMessage());
            throw new BookNotInReadListException("Ошибка при проверке существования книги: " + e.getMessage());
        }
    }

    @Transactional
    public void removeBookFromReadList(String username, long bookId) {
        Instant start = Instant.now();
        logger.info("Attempting to remove book {} from read list for user {}", bookId, username);

        if (!readerRepository.existsByUsernameAndBookId(username, bookId)) {
            logger.warn("Book {} not found in read list for user {}", bookId, username);
            throw new BookNotInReadListException(
                    "Книга с ID: " + bookId + " отсутствует в списке прочитанного!");
        }

        readerRepository.deleteByUsernameAndBookId(username, bookId);

        Duration duration = Duration.between(start, Instant.now());
        logger.info("Successfully removed book {} from read list for user {} in {} ms",
                bookId, username, duration.toMillis());
    }


    public BookResponse getBookFromReadList(String username, long bookId) {
        Instant start = Instant.now();
        logger.info("Fetching book {} from read list for user {}", bookId, username);

        Reader reader = readerRepository.findByUsernameAndBookId(username, bookId)
                .orElseThrow(() -> {
                    logger.warn("Book {} not found in read list for user {}", bookId, username);
                    return new BookNotInReadListException(
                            "Книга с ID: " + bookId + " отсутствует в списке прочитанного!");
                });

        BookResponse response = fetchBookDetails(bookId, reader.getAddedAt());

        Duration duration = Duration.between(start, Instant.now());
        logger.info("Successfully fetched book {} details for user {} in {} ms",
                bookId, username, duration.toMillis());

        return response;
    }

    public Page<BookResponse> getAllReadBooks(String username, Pageable pageable) {
        Instant start = Instant.now();
        logger.info("Fetching all books from read list for user {}, page {}", username, pageable.getPageNumber());

        Page<Reader> readerPage = readerRepository.findByUsername(username, pageable);

        List<BookResponse> bookResponses = readerPage.getContent().stream()
                .map(reader -> {
                    try {
                        return fetchBookDetails(reader.getBookId(), reader.getAddedAt());
                    } catch (BookNotInReadListException e) {
                        logger.warn("Book {} not found in catalog, skipping", reader.getBookId());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Page<BookResponse> result = new PageImpl<>(bookResponses, pageable, readerPage.getTotalElements());

        Duration duration = Duration.between(start, Instant.now());
        logger.info("Fetched {} books for user {} in {} ms",
                bookResponses.size(), username, duration.toMillis());

        return result;
    }

    private BookResponse fetchBookDetails(long bookId, LocalDateTime addedAt) {
        Instant start = Instant.now();
        logger.debug("Fetching details for book {}", bookId);

        String url = "http://catalog-service/api/catalog/books/" + bookId + "/details";

        try {
            ResponseEntity<BookResponse> response = restTemplate.getForEntity(url, BookResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                BookResponse bookResponse = response.getBody();
                bookResponse.setAddedAt(addedAt);

                Duration duration = Duration.between(start, Instant.now());
                logger.debug("Fetched book {} details in {} ms", bookId, duration.toMillis());

                return bookResponse;
            }

            throw new BookNotInReadListException("Неверный ответ для книги с ID: " + bookId);

        } catch (HttpClientErrorException.NotFound e) {
            logger.error("Book {} not found in catalog", bookId);
            throw new BookNotInReadListException("Книга с ID: " + bookId + " не найдена в каталоге!");
        } catch (RestClientException e) {
            logger.error("Error fetching book details: {}", e.getMessage());
            throw new BookNotInReadListException("Ошибка при получении информации о книге: " + e.getMessage());
        }
    }
}