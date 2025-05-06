package com.example.service;

import com.example.dto.BookResponse;
import com.example.exception.BookAlreadyInReadListException;
import com.example.exception.BookNotInReadListException;
import com.example.model.Reader;
import com.example.repository.ReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReaderService {

    private final ReaderRepository readerRepository;
    private final RestTemplate restTemplate; // Для проверки существования книги в book-service

    @Transactional
    public Reader addBookToReadList(String username, Long bookId) {
        checkIfBookExists(bookId); // Проверяем существование книги перед добавлением

        if (readerRepository.existsByUsernameAndBookId(username, bookId)) {
            throw new BookAlreadyInReadListException(
                    "Book with ID " + bookId + " is already in your read list");
        }

        Reader reader = new Reader();
        reader.setUsername(username);
        reader.setBookId(bookId);
        reader.setAddedAt(LocalDateTime.now());

        return readerRepository.save(reader);
    }

    private void checkIfBookExists(Long bookId) {
        try {
            ResponseEntity<Void> response = restTemplate.getForEntity(
                    "http://book-service/api/catalog/books/{id}",
                    Void.class,
                    bookId);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new BookNotInReadListException("Book with ID " + bookId + " does not exist");
            }
        } catch (RestClientException e) {
            throw new BookNotInReadListException("Failed to verify book existence: " + e.getMessage());
        }
    }

    @Transactional
    public void removeBookFromReadList(String username, Long bookId) {
        if (!readerRepository.existsByUsernameAndBookId(username, bookId)) {
            throw new BookNotInReadListException(
                    "Book with ID " + bookId + " is not in your read list");
        }
        readerRepository.deleteByUsernameAndBookId(username, bookId);
    }

    public BookResponse getBookFromReadList(String username, Long bookId) {
        Reader reader = readerRepository.findByUsernameAndBookId(username, bookId)
                .orElseThrow(() -> new BookNotInReadListException(
                        "Book with ID " + bookId + " is not in your read list"));

        return fetchBookDetails(bookId, reader.getAddedAt());
    }

    public Page<BookResponse> getAllReadBooks(String username, Pageable pageable) {
        Page<Reader> readerPage = readerRepository.findByUsername(username, pageable);

        List<BookResponse> bookResponses = readerPage.getContent().stream()
                .map(reader -> {
                    try {
                        return fetchBookDetails(reader.getBookId(), reader.getAddedAt());
                    } catch (BookNotInReadListException e) {
                        return null; // Возвращаем null вместо частичного ответа
                    }
                })
                .filter(Objects::nonNull) // Фильтруем null-значения
                .collect(Collectors.toList());

        return new PageImpl<>(bookResponses, pageable, readerPage.getTotalElements());
    }

    private BookResponse fetchBookDetails(Long bookId, LocalDateTime addedAt) {
        String url = "http://book-service/api/catalog/books/" + bookId + "/details";

        try {
            ResponseEntity<BookResponse> response = restTemplate.getForEntity(url, BookResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                BookResponse bookResponse = response.getBody();
                bookResponse.setAddedAt(addedAt);
                return bookResponse;
            }

            throw new BookNotInReadListException("Invalid response for book ID: " + bookId);

        } catch (HttpClientErrorException.NotFound e) {
            throw new BookNotInReadListException("Book not found with ID: " + bookId);
        } catch (RestClientException e) {
            throw new BookNotInReadListException("Service error for book ID: " + bookId);
        }
    }
}