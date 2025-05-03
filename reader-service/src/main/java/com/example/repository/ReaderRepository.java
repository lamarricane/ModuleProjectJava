package com.example.repository;

import com.example.model.Reader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReaderRepository extends JpaRepository<Reader, Long> {
    List<Reader> findByUserId(UUID userId);
    boolean existsByUserIdAndBookId(UUID userId, Long bookId);
    @Query("SELECT r FROM Reader r WHERE r.userId = :userId AND r.book.id = :bookId")
    Optional<Reader> findByUserIdAndBookId(@Param("userId") UUID userId, @Param("bookId") Long bookId);
}