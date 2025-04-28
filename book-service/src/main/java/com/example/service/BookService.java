package com.example.service;

import com.example.model.Book;
import com.example.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional
    public void createBook(Book book) {
        bookRepository.save(book);
    }

    @Transactional
    public void updateBook(long id, Book bookInfo) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Книга не найдена!"));
        if (!book.getName().isEmpty()) book.setName(bookInfo.getName());
        if (!book.getGenre().isEmpty()) book.setGenre(bookInfo.getGenre());
        if (book.getPagesNumber() > 0) book.setPagesNumber(bookInfo.getPagesNumber());
        if (book.getPublishingDate() != null) book.setPublishingDate(bookInfo.getPublishingDate());
        if (book.getDescription().isEmpty()) book.setDescription(bookInfo.getDescription());
        bookRepository.save(book);
    }

    @Transactional
    public void deleteBook(long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
        } else throw new RuntimeException("Книга не найдена!");
    }

    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Optional<Book> getBookById(long id) {
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

    public Page<Book> getByName(String name, Pageable pageable) {
        return bookRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    public Page<Book> getOrderByNameAsc(Pageable pageable) {
        return bookRepository.findAllByOrderByNameAsc(pageable);
    }

    public Page<Book> getOrderByNameDesc(Pageable pageable) {
        return bookRepository.findAllByOrderByNameDesc(pageable);
    }

    public Page<Book> getOrderBySizeAsc(Pageable pageable) {
        return bookRepository.findAllByOrderByPageNumberAsc(pageable);
    }

    public Page<Book> getOrderBySizeDesc(Pageable pageable) {
        return bookRepository.findAllByOrderByPageNumberDesc(pageable);
    }

    public Page<Book> getOrderByPublishingDateAsc(Pageable pageable) {
        return bookRepository.findAllByOrderByPublishingDateAsc(pageable);
    }

    public Page<Book> getOrderByPublishingDateDesc(Pageable pageable) {
        return bookRepository.findAllByOrderByPublishingDateDesc(pageable);
    }
}
