package com.example.repository;

import com.example.model.Reader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с прочитанными книгами в базе данных.
 * Наследует JpaRepository для базовых CRUD-операций.
 */
@Repository
public interface ReaderRepository extends JpaRepository<Reader, Long> {
    Page<Reader> findByUsername(String username, Pageable pageable);
    boolean existsByUsernameAndBookId(String username, long bookId);
    void deleteByUsernameAndBookId(String username, long bookId);
    Optional<Reader> findByUsernameAndBookId(String username, long bookId);
}