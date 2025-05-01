package com.example.dto;

import com.example.model.Book;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {
    private long id;
    private String title;
    private String genre;
    private int pagesNumber;
    private LocalDate publishingDate;
    private String description;
    private long authorId;

    public BookResponse(Book book){
        id = book.getId();
        title = book.getTitle();
        genre = book.getGenre();
        pagesNumber = book.getPagesNumber();
        publishingDate = book.getPublishingDate();
        description = book.getDescription();
        authorId = book.getAuthor().getId();
    }
}
