package com.example.service;

import com.example.dto.AuthorRequest;
import com.example.model.Author;
import com.example.repository.AuthorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Сервис для работы с авторами:
 * - внутренняя логика CRUD операций;
 * - фильтрация, сортировка и поиск авторов.
 */
@Service
public class AuthorService {
    private static final Logger logger = LoggerFactory.getLogger(AuthorService.class);
    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Transactional
    public void createAuthor(Author author) {
        Instant start = Instant.now();
        logger.info("Creating new author: {}", author.getName());

        try {
            authorRepository.save(author);
            logger.info("Author created successfully with ID: {}", author.getId());

            Duration duration = Duration.between(start, Instant.now());
            logger.debug("Author creation completed in {} ms", duration.toMillis());

        } catch (Exception e) {
            logger.error("Error creating author: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void update(long id, Author updatedAuthor) {
        Instant start = Instant.now();
        logger.info("Updating author with ID: {}", id);

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Author not found with ID: {}", id);
                    return new EntityNotFoundException("Автор не найден!");
                });

        author.setName(updatedAuthor.getName());
        author.setBirthDate(updatedAuthor.getBirthDate());
        author.setLocation(updatedAuthor.getLocation());
        author.setBio(updatedAuthor.getBio());

        authorRepository.save(author);

        logger.info("Author with ID: {} updated successfully", id);
        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Author update completed in {} ms", duration.toMillis());
    }

    @Transactional
    public void deleteAuthor(long id) {
        Instant start = Instant.now();
        logger.info("Deleting author with ID: {}", id);

        if (!authorRepository.existsById(id)) {
            throw new EntityNotFoundException("Автор не найден!");
        }
        authorRepository.deleteById(id);

        logger.info("Author with ID: {} deleted successfully", id);
        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Author deletion completed in {} ms", duration.toMillis());
    }

    public Page<Author> getAllAuthors(Pageable pageable) {
        Instant start = Instant.now();
        logger.debug("Fetching all authors with pagination: {}", pageable);

        Page<Author> authors = authorRepository.findAll(pageable);

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched {} all authors in {} ms", authors.getTotalElements(), duration.toMillis());

        return authors;
    }

    public Author getAuthorById(long id) {
        Instant start = Instant.now();
        logger.debug("Fetching author by ID: {}", id);

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Author not found with ID: {}", id);
                    return new EntityNotFoundException("Автор не найден!");
                });

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Author fetched in {} ms", duration.toMillis());

        return author;
    }

    public Page<Author> getByLocation(String location, Pageable pageable) {
        Instant start = Instant.now();
        logger.debug("Fetching authors by location with pagination: {}", pageable);

        Page<Author> authors = authorRepository.findByLocation(location, pageable);

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched {} authors by location in {} ms", authors.getTotalElements(), duration.toMillis());

        return authors;
    }

    public Page<Author> getByBookGenre(String genre, Pageable pageable) {
        Instant start = Instant.now();
        logger.debug("Fetching authors by genre with pagination: {}", pageable);

        Page<Author> authors = authorRepository.findByBookGenre(genre, pageable);

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched {} authors by genre in {} ms", authors.getTotalElements(), duration.toMillis());

        return authors;
    }

    public Page<Author> getByBirthDateBetween(LocalDate begin, LocalDate end, Pageable pageable) {
        Instant start = Instant.now();
        logger.debug("Fetching authors by birth date with pagination: {}", pageable);

        Page<Author> authors = authorRepository.findByBirthDateBetween(begin, end, pageable);

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched {} authors by birth date in {} ms", authors.getTotalElements(), duration.toMillis());

        return authors;
    }

    public Page<Author> findByNameContainingIgnoreCase(String name, Pageable pageable) {
        Instant start = Instant.now();
        logger.debug("Fetching authors by name with pagination: {}", pageable);

        Page<Author> authors = authorRepository.findByNameContainingIgnoreCase(name, pageable);

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched {} authors by name in {} ms", authors.getTotalElements(), duration.toMillis());

        return authors;
    }

    public Page<Author> findAllByOrderByNameAsc(Pageable pageable) {
        Instant start = Instant.now();
        logger.debug("Fetching all authors by name with pagination: {}", pageable);

        Page<Author> authors = authorRepository.findAllByOrderByNameAsc(pageable);

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched {} authors by name in {} ms", authors.getTotalElements(), duration.toMillis());

        return authors;
    }

    public Page<Author> findAllByOrderByNameDesc(Pageable pageable) {
        Instant start = Instant.now();
        logger.debug("Fetching all authors by name with pagination: {}", pageable);

        Page<Author> authors = authorRepository.findAllByOrderByNameDesc(pageable);

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched {} authors by name in {} ms", authors.getTotalElements(), duration.toMillis());

        return authors;
    }

    public Page<Author> findAllByOrderByBirthDateAsc(Pageable pageable) {
        Instant start = Instant.now();
        logger.debug("Fetching all authors by birth date with pagination: {}", pageable);

        Page<Author> authors = authorRepository.findAllByOrderByBirthDateAsc(pageable);

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched {} authors by birth date in {} ms", authors.getTotalElements(), duration.toMillis());

        return authors;
    }

    public Page<Author> findAllByOrderByBirthDateDesc(Pageable pageable) {
        Instant start = Instant.now();
        logger.debug("Fetching all authors by birth date with pagination: {}", pageable);

        Page<Author> authors = authorRepository.findAllByOrderByBirthDateDesc(pageable);

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched {} authors by birth date in {} ms", authors.getTotalElements(), duration.toMillis());

        return authors;
    }

    public Page<Author> findAllOrderByBooksCountAsc(Pageable pageable) {
        Instant start = Instant.now();
        logger.debug("Fetching all authors by book genre with pagination: {}", pageable);

        Page<Author> authors = authorRepository.findAllByOrderByBirthDateAsc(pageable);

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched {} authors by book genre in {} ms", authors.getTotalElements(), duration.toMillis());

        return authors;
    }

    public Page<Author> findAllOrderByBooksCountDesc(Pageable pageable) {
        Instant start = Instant.now();
        logger.debug("Fetching all authors by books count with pagination: {}", pageable);

        Page<Author> authors = authorRepository.findAllByOrderByBirthDateDesc(pageable);

        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Fetched {} authors by books count in {} ms", authors.getTotalElements(), duration.toMillis());

        return authors;
    }

    public Author convertToAuthor(AuthorRequest dto) {
        Author author = new Author();
        author.setName(dto.getName());
        author.setBirthDate(dto.getBirthDate());
        author.setLocation(dto.getLocation());
        author.setBio(dto.getBio());
        return author;
    }
}