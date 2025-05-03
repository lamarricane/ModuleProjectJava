package com.example.service;

import com.example.dto.AuthorRequest;
import com.example.model.Author;
import com.example.repository.AuthorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Transactional
    public void createAuthor(Author author) {
        authorRepository.save(author);
    }

    @Transactional
    public void update(long id, Author updatedAuthor) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Автор не найден!"));

        author.setName(updatedAuthor.getName());
        author.setBirthDate(updatedAuthor.getBirthDate());
        author.setLocation(updatedAuthor.getLocation());
        author.setBio(updatedAuthor.getBio());

        authorRepository.save(author);
    }

    @Transactional
    public void deleteAuthor(long id) {
        if (!authorRepository.existsById(id)) {
            throw new EntityNotFoundException("Автор не найден!");
        }
        authorRepository.deleteById(id);
    }

    public Page<Author> getAllAuthors(Pageable pageable) {
        return authorRepository.findAll(pageable);
    }

    public Optional<Author> getAuthorById(long id) {
        return Optional.ofNullable(authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Автор не найден!")));
    }

    public Page<Author> getByLocation(String location, Pageable pageable) {
        return authorRepository.findByLocation(location, pageable);
    }

    public Page<Author> getByBirthDateBetween(LocalDate start, LocalDate end, Pageable pageable) {
        return authorRepository.findByBirthDateBetween(start, end, pageable);
    }

    public Page<Author> findByNameContainingIgnoreCase(String name, Pageable pageable) {
        return authorRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    public Page<Author> findAllByOrderByNameAsc(Pageable pageable) {
        return authorRepository.findAllByOrderByNameAsc(pageable);
    }

    public Page<Author> findAllByOrderByNameDesc(Pageable pageable) {
        return authorRepository.findAllByOrderByNameDesc(pageable);
    }

    public Page<Author> findAllByOrderByBirthDateAsc(Pageable pageable) {
        return authorRepository.findAllByOrderByBirthDateAsc(pageable);
    }

    public Page<Author> findAllByOrderByBirthDateDesc(Pageable pageable) {
        return authorRepository.findAllByOrderByBirthDateDesc(pageable);
    }

    public Page<Author> findAllOrderByBooksCountAsc(Pageable pageable) {
        return authorRepository.findAllOrderByBooksCountAsc(pageable);
    }

    public Page<Author> findAllOrderByBooksCountDesc(Pageable pageable) {
        return authorRepository.findAllOrderByBooksCountDesc(pageable);
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