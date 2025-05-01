package com.example.service;

import com.example.dto.BookResponse;
import com.example.dto.ReaderRequest;
import com.example.dto.ReaderResponse;
import com.example.model.Book;
import com.example.model.Reader;
import com.example.repository.BookRepository;
import com.example.repository.ReaderRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReaderService {
    private final ReaderRepository readerRepository;
    private final BookRepository bookRepository;

    public ReaderService(ReaderRepository readerRepository, BookRepository bookRepository){
        this.bookRepository = bookRepository;
        this.readerRepository = readerRepository;
    }

    @Transactional
    public void createReader(ReaderRequest readerRequest) {
        Reader reader = new Reader();

        var bookOptional = bookRepository.findById(readerRequest.getBookRequest().getId());
        if (bookOptional.isPresent()) {
            reader.setBook(bookOptional.get());
            reader.setUser(readerRequest.getUser());
            readerRepository.save(reader);
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");

    }

    public List<ReaderResponse> getAllReaders() {
        return readerRepository.findAll().stream().map(this::mapToBookResponse).toList();
    }

    public ReaderResponse mapToBookResponse(Reader reader){
        Book book = reader.getBook();
        return ReaderResponse.builder()
                .id(reader.getId())
                .bookResponse(new BookResponse(reader.getBook()))
                .user(reader.getUser()).build();
    }

    public List<ReaderResponse> getAllReadersByUser(int id){
        List<Reader> readers = readerRepository.findByUser(id);
        return readers.stream().map(this::mapToBookResponse).collect(Collectors.toList());
    }
}