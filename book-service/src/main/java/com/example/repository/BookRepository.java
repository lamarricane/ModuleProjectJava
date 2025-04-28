package com.example.repository;

import com.example.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    //Основной метод
    Optional<Book> findById(long id);
    //Фильтрация
    Page<Book> findByGenre(String genre, Pageable pageable);
    Page<Book> findByPublishingDateBetween(LocalDate lowBound, LocalDate highBound, Pageable pageable);
    Page<Book> findByPagesNumberBetween(int minPages, int maxPages, Pageable pageable);
    //Поиск по частичному совпадению
    Page<Book> findByAuthorNameContainingIgnoreCase(String authorName, Pageable pageable);
    Page<Book> findByNameContainingIgnoreCase(String name, Pageable pageable);
    //Сортировка
    Page<Book> findAllByOrderByNameAsc(Pageable pageable);
    Page<Book> findAllByOrderByNameDesc(Pageable pageable);
    Page<Book> findAllByOrderByPageNumberAsc(Pageable pageable);
    Page<Book> findAllByOrderByPageNumberDesc(Pageable pageable);
    Page<Book> findAllByOrderByPublishingDateAsc(Pageable pageable);
    Page<Book> findAllByOrderByPublishingDateDesc(Pageable pageable);
}