package com.example.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * Компонент для работы с JWT-токенами:
 * - генерация токенов;
 * - валидация токенов.
 */
@Component
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    private final SecretKey secretKey;

    public JwtTokenProvider(@Value("${app.jwt-secret}") String secret) {
        if (secret.getBytes(StandardCharsets.UTF_8).length < 64) {
            logger.error("JWT secret key is too short - must be at least 64 characters");
            throw new IllegalArgumentException("JWT secret key must be at least 64 characters long");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        logger.info("JwtTokenProvider initialized with secret key");
    }

    /**
     * Генерирует JWT-токен на основе аутентификации пользователя
     */
    public String generateToken(Authentication authentication) {
        Instant start = Instant.now();
        String username = authentication.getName();
        logger.debug("Generating token for user: {}", username);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 86400000); // 24 часа

        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
        Duration duration = Duration.between(start, Instant.now());
        logger.debug("Token generated for user {} in {} ms", username, duration.toMillis());

        return token;
    }
}