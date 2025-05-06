package com.example.repository;

import com.example.model.Reader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReaderRepository extends JpaRepository<Reader, Long> {
    Page<Reader> findByUsername(String username, Pageable pageable);
    boolean existsByUsernameAndBookId(String username, Long bookId);
    void deleteByUsernameAndBookId(String username, Long bookId);
    Optional<Reader> findByUsernameAndBookId(String username, Long bookId);
}