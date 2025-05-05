package com.example.service;

import com.example.dto.ReaderRequest;
import com.example.model.Reader;
import com.example.repository.ReaderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReaderService {
    private final ReaderRepository readerRepository;

    public ReaderService(ReaderRepository readerRepository) {
        this.readerRepository = readerRepository;
    }

    @Transactional
    public void addBookToReader(ReaderRequest request) {
        if (readerRepository.existsByUsernameAndBookId(request.getUsername(), request.getBookId())) {
            throw new IllegalArgumentException("Книга уже добавлена в список прочитанного!");
        }

        Reader reader = new Reader();
        reader.setUsername(request.getUsername());
        reader.setBookId(request.getBookId());
        readerRepository.save(reader);
    }

    @Transactional
    public void removeBookFromReader(String username, Long bookId) {
        if (!readerRepository.existsByUsernameAndBookId(username, bookId)) {
            throw new EntityNotFoundException("Книга не найдена в списке прочитанного!");
        }
        readerRepository.deleteByUsernameAndBookId(username, bookId);
    }

    public Page<Reader> getReaderBooks(String username, Pageable pageable) {
        return readerRepository.findByUsername(username, pageable);
    }
}