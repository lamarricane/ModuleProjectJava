package com.example.provider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final SecretKey secretKey;

    public JwtTokenProvider(@Value("${app.jwt-secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Authentication authentication) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 86400000); // 24 часа

        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public Mono<Authentication> getAuthentication(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            System.out.println(username);
            return Mono.just(new UsernamePasswordAuthenticationToken(
                    username, null, Collections.emptyList()));
        } catch (JwtException | IllegalArgumentException e) {
            return Mono.empty();
        }
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            boolean isValid = !claims.getBody().getExpiration().before(new Date());
            System.out.println("Токен валиден: " + isValid);
            return isValid;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Ошибка валидации токена: " + e.getMessage());
            return false;
        }
    }
}