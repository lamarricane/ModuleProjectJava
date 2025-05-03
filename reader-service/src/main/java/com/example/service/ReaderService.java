package com.example.service;

import com.example.model.Book;
import com.example.model.Reader;
import com.example.repository.BookRepository;
import com.example.repository.ReaderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ReaderService {

    private final ReaderRepository readerRepository;
    private final BookRepository bookRepository;

    public ReaderService(ReaderRepository readerRepository, BookRepository bookRepository){
        this.bookRepository = bookRepository;
        this.readerRepository = readerRepository;
    }

    @Transactional
    public Reader addBookToRead(UUID userId, Long bookId) {
        if (readerRepository.existsByUserIdAndBookId(userId, bookId)) {
            throw new IllegalArgumentException("Книга уже добавлена в прочитанное!");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Книга не найдена!"));

        Reader reader = new Reader();
        reader.setUserId(userId);
        reader.setBook(book);

        return readerRepository.save(reader);
    }

    public List<Reader> getReadBooks(UUID userId) {
        return readerRepository.findByUserId(userId);
    }

    @Transactional
    public void removeBookFromRead(UUID userId, Long bookId) {
        Reader reader = readerRepository.findByUserIdAndBookId(userId, bookId)
                .orElseThrow(() -> new IllegalArgumentException("Книга отсутствует в каталоге!"));
        readerRepository.delete(reader);
    }
}