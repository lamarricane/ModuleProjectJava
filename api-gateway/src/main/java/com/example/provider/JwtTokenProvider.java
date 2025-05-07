package com.example.provider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;

/**
 * Реактивный компонент для работы с JWT-токенами:
 * - валидация токенов;
 * - извлечение информации о пользователе из токена.
 */
@Component
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    private final SecretKey secretKey;

    public JwtTokenProvider(@Value("${app.jwt-secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        logger.info("JwtTokenProvider initialized");
    }

    /**
     * Извлечение данных из токена
     */
    public Mono<Authentication> getAuthentication(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            logger.debug("Created authentication for user: {}", username);
            return Mono.just(new UsernamePasswordAuthenticationToken(
                    username, null, Collections.emptyList()));
        } catch (JwtException e) {
            logger.warn("JWT authentication failed: {}", e.getMessage());
            return Mono.empty();
        }
    }

    /**
     * Проверяет валидность токена и возвращает Claims (если токен валиден)
     */
    public Mono<Claims> parseToken(String token) {
        Instant start = Instant.now();
        logger.debug("Parsing JWT token");

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            Duration duration = Duration.between(start, Instant.now());
            logger.debug("Token parsed successfully in {} ms", duration.toMillis());

            return Mono.just(claims);
        } catch (JwtException | IllegalArgumentException e) {
            return Mono.empty();
        }
    }

    /**
     * Создает аутентификацию на основе Claims
     */
    public Authentication createAuthentication(Claims claims) {
        String username = claims.getSubject();
        logger.debug("Creating authentication for user: {}", username);
        return new UsernamePasswordAuthenticationToken(
                username, null, Collections.emptyList());
    }
}