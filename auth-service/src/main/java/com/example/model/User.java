package com.example.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Модель сущности пользователя.
 * Содержит основные данные для аутентификации и авторизации.
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;
}
