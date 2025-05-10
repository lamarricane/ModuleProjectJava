package com.example.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;

/**
 * Модель сущности книг в каталоге.
 * Хранит информацию о книгах.
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "books", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"title", "author_id"}, name = "uk_book_title_author")})
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotBlank
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank
    @Column(name = "genre", nullable = false)
    private String genre;

    @NotNull
    @Positive
    @Column(name = "pages_number", nullable = false)
    private int pagesNumber;

    @NotNull
    @PastOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "publishing_date", nullable = false, columnDefinition = "DATE")
    private LocalDate publishingDate;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    @JsonBackReference
    private Author author;
}
