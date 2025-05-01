package com.example.service;

import com.example.dto.BookRequest;
import com.example.dto.BookResponse;
import com.example.model.Book;
import com.example.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void createBook(@RequestBody BookRequest bookRequest){
        Book book = new Book();
        book.setTitle(bookRequest.getTitle());
        book.setGenre(bookRequest.getGenre());
        book.setPagesNumber(bookRequest.getPagesNumber());
        book.setPublishingDate(bookRequest.getPublishingDate());
        book.setDescription(bookRequest.getDescription());
        bookRepository.save(book);
    }

    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream().map(this::mapToBookResponse).toList();
    }

    public BookResponse mapToBookResponse(Book book){
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .genre(book.getGenre())
                .pagesNumber(book.getPagesNumber())
                .publishingDate(book.getPublishingDate())
                .description(book.getDescription())
                .authorId(book.getAuthor().getId()).build();
    }
}
