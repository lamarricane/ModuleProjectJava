package com.example.service;

import com.example.dto.AuthRequest;
import com.example.dto.AuthResponse;
import com.example.exception.InvalidPasswordException;
import com.example.exception.UserNotFoundException;
import com.example.exception.UsernameAlreadyExistsException;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.security.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

/**
 * Сервис для обработки логики аутентификации и регистрации пользователей.
 */
@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(JwtTokenProvider tokenProvider, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Аутентификация пользователя и генерация токена
     */
    public AuthResponse login(AuthRequest request) {
        Instant start = Instant.now();
        logger.info("Attempting login for user: {}", request.getUsername());

        try {
            // Проверка, существует ли пользователь
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> {
                        logger.warn("User not found: {}", request.getUsername());
                        return new UserNotFoundException("Пользователь с именем: " + request.getUsername() + " не найден!");
                    });

            // Проверка пароля
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                logger.warn("Invalid password for user: {}", request.getUsername());
                throw new InvalidPasswordException("Неверный пароль!");
            }

            // Аутентификация и генерация токена
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
            );

            String token = tokenProvider.generateToken(authentication);

            logger.info("User {} successfully logged in. Token generated.", request.getUsername());
            Duration duration = Duration.between(start, Instant.now());
            logger.debug("Login process completed in {} ms", duration.toMillis());

            return new AuthResponse(token);

        } catch (BadCredentialsException e) {
            logger.error("Authentication error for user {}: {}", request.getUsername(), e.getMessage());
            throw new InvalidPasswordException("Неверный пароль или имя пользователя!");
        }
    }

    /**
     * Регистрация нового пользователя в системе
     */
    public void register(AuthRequest request) {
        Instant start = Instant.now();
        logger.info("Attempting registration for user: {}", request.getUsername());

        // Проверка уникальности имени пользователя
        if (userRepository.existsByUsername(request.getUsername())) {
            logger.warn("Username already exists: {}", request.getUsername());
            throw new UsernameAlreadyExistsException("Пользователь с именем: " + request.getUsername() + " уже существует!");
        }
        // Сохранение нового пользователя в бд
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        try {
            userRepository.save(user);
            logger.info("User {} successfully registered", request.getUsername());

            Duration duration = Duration.between(start, Instant.now());
            logger.debug("Registration process completed in {} ms", duration.toMillis());
        } catch (Exception e) {
            logger.error("Error during registration for user {}: {}", request.getUsername(), e.getMessage());
            throw e;
        }
    }
}
