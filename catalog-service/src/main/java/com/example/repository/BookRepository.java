package com.example.repository;

import com.example.model.Book;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Репозиторий для работы с книгами в базе данных.
 * Наследует JpaRepository для базовых CRUD-операций.
 */
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

    //@Cacheable(value = "authorStats", key = "'full'")
    @Query(value = """
        SELECT a.name as authorName, COUNT(b.id) as bookCount, 
               AVG(b.pages_number) as avgPages, 
               MIN(b.publishing_date) as firstPublication
        FROM authors a
        LEFT JOIN books b ON a.id = b.author_id
        GROUP BY a.id, a.name
        ORDER BY bookCount DESC
        """, nativeQuery = true)
    List<Map<String, Object>> getAuthorStats();

    //@Cacheable(value = "authorStats", key = "'summary'")
    @Query(value = """
        SELECT a.name as authorName, COUNT(b.id) as bookCount
        FROM authors a
        LEFT JOIN books b ON a.id = b.author_id
        GROUP BY a.id, a.name
        ORDER BY bookCount DESC
        LIMIT 10
        """, nativeQuery = true)
    List<Map<String, Object>> getAuthorStatsSummary();

    //@Cacheable(value = "genreStats", key = "'full'")
    @Query(value = """
        SELECT genre, COUNT(id) as bookCount, 
               AVG(pages_number) as avgPages
        FROM books
        GROUP BY genre
        ORDER BY bookCount DESC
        """, nativeQuery = true)
    List<Map<String, Object>> getGenreStats();

    @CacheEvict(value = {"authorStats", "genreStats"}, allEntries = true)
    @Modifying
    @Query(value = "SELECT 1", nativeQuery = true)
    void clearStatsCache();
}