package com.example.service;

import com.example.exception.BookAlreadyInReadListException;
import com.example.exception.BookNotInReadListException;
import com.example.model.Reader;
import com.example.repository.ReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

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

    public Reader getBookFromReadList(String username, Long bookId) {
        return readerRepository.findByUsernameAndBookId(username, bookId)
                .orElseThrow(() -> new BookNotInReadListException(
                        "Book with ID " + bookId + " is not in your read list"));
    }

    public Page<Reader> getAllReadBooks(String username, Pageable pageable) {
        return readerRepository.findByUsername(username, pageable);
    }

}