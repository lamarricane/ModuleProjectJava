package com.example.repository;

import com.example.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    //Фильтрация
    Page<Book> findByGenre(String genre, Pageable pageable);
    Page<Book> findByPublishingDateBetween(LocalDate lowBound, LocalDate highBound, Pageable pageable);
    Page<Book> findByPagesNumberBetween(int minPages, int maxPages, Pageable pageable);
    //Поиск по частичному совпадению
    Page<Book> findByAuthorNameContainingIgnoreCase(String authorName, Pageable pageable);
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    //Сортировка
    Page<Book> findAllByOrderByTitleAsc(Pageable pageable);
    Page<Book> findAllByOrderByTitleDesc(Pageable pageable);
    Page<Book> findAllByOrderByPagesNumberAsc(Pageable pageable);
    Page<Book> findAllByOrderByPagesNumberDesc(Pageable pageable);
    Page<Book> findAllByOrderByPublishingDateAsc(Pageable pageable);
    Page<Book> findAllByOrderByPublishingDateDesc(Pageable pageable);
}