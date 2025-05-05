package com.example.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "readers", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username", "book_id"}, name = "uk_user_book")})
public class Reader {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "book_id", nullable = false)
    private long bookId;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt = LocalDateTime.now();
}




