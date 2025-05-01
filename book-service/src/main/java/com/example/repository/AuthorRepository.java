package com.example.repository;

import com.example.model.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    //Фильтрация
    Page<Author> findByLocation(String location, Pageable pageable);
    Page<Author> findByBirthDateBetween(LocalDate lowBound, LocalDate highBound, Pageable pageable);
    @Query("SELECT DISTINCT a FROM Author a JOIN a.books b WHERE b.genre = :genre")
    Page<Author> findByBookGenre(String genre, Pageable pageable);
    //Поиск по частичному совпадению
    Page<Author> findByNameContainingIgnoreCase(String name, Pageable pageable);
    //Сортировка
    Page<Author> findAllByOrderByNameAsc(Pageable pageable);
    Page<Author> findAllByOrderByNameDesc(Pageable pageable);
    Page<Author> findAllByOrderByBirthDateAsc(Pageable pageable);
    Page<Author> findAllByOrderByBirthDateDesc(Pageable pageable);
    @Query("SELECT a FROM Author a LEFT JOIN a.books b GROUP BY a.id ORDER BY COUNT(b) ASC")
    Page<Author> findAllOrderByBooksCountAsc(Pageable pageable);
    @Query("SELECT a FROM Author a LEFT JOIN a.books b GROUP BY a.id ORDER BY COUNT(b) DESC")
    Page<Author> findAllOrderByBooksCountDesc(Pageable pageable);
}

