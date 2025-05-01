package com.example.service;

import com.example.dto.BookRequest;
import com.example.model.Book;
import com.example.repository.AuthorRepository;
import com.example.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    @Transactional
    public void createBook(Book book) {
        bookRepository.save(book);
    }

    @Transactional
    public void update(long id, Book updatedBook) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Книга не найдена!"));

        book.setTitle(updatedBook.getTitle());
        book.setGenre(updatedBook.getGenre());
        book.setPagesNumber(updatedBook.getPagesNumber());
        book.setPublishingDate(updatedBook.getPublishingDate());
        book.setDescription(updatedBook.getDescription());

        bookRepository.save(book);
    }

    @Transactional
    public void deleteBook(long id) {
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Книга не найдена!");
        }
        bookRepository.deleteById(id);
    }

    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Optional<Book> getBookById(long id) {
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Книга не найдена!");
        }
        return bookRepository.findById(id);
    }

    public Page<Book> getByGenre(String genre, Pageable pageable) {
        return bookRepository.findByGenre(genre, pageable);
    }

    public Page<Book> getByPeriod(LocalDate lowBound, LocalDate highBound, Pageable pageable) {
        return bookRepository.findByPublishingDateBetween(lowBound, highBound, pageable);
    }

    public Page<Book> getBySize(int min, int max, Pageable pageable) {
        return bookRepository.findByPagesNumberBetween(min, max, pageable);
    }

    public Page<Book> getByAuthor(String authorName, Pageable pageable) {
        return bookRepository.findByAuthorNameContainingIgnoreCase(authorName, pageable);
    }

    public Page<Book> getByTitle(String title, Pageable pageable) {
        return bookRepository.findByTitleContainingIgnoreCase(title, pageable);
    }

    public Page<Book> getOrderByTitleAsc(Pageable pageable) {
        return bookRepository.findAllByOrderByTitleAsc(pageable);
    }

    public Page<Book> getOrderByTitleDesc(Pageable pageable) {
        return bookRepository.findAllByOrderByTitleDesc(pageable);
    }

    public Page<Book> getOrderBySizeAsc(Pageable pageable) {
        return bookRepository.findAllByOrderByPagesNumberAsc(pageable);
    }

    public Page<Book> getOrderBySizeDesc(Pageable pageable) {
        return bookRepository.findAllByOrderByPagesNumberDesc(pageable);
    }

    public Page<Book> getOrderByPublishingDateAsc(Pageable pageable) {
        return bookRepository.findAllByOrderByPublishingDateAsc(pageable);
    }

    public Page<Book> getOrderByPublishingDateDesc(Pageable pageable) {
        return bookRepository.findAllByOrderByPublishingDateDesc(pageable);
    }

    public Book convertToBook(BookRequest dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setGenre(dto.getGenre());
        book.setPagesNumber(dto.getPagesNumber());
        book.setPublishingDate(dto.getPublishingDate());
        book.setDescription(dto.getDescription());
        book.setAuthor(authorRepository.findById(dto.getAuthorId()).get());
        return book;
    }
}
