package com.example.service;

import com.example.dto.AuthRequest;
import com.example.dto.AuthResponse;
import com.example.exception.InvalidPasswordException;
import com.example.exception.UserNotFoundException;
import com.example.exception.UsernameAlreadyExistsException;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.security.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse login(AuthRequest request) {
        try {
            // Проверка, существует ли пользователь
            User user = (User) userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new UserNotFoundException("Пользователь с именем: " + request.getUsername() + " не найден!"));

            // Проверка пароля
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new InvalidPasswordException("Неверный пароль!");
            }

            // Аутентификация и генерация токена
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
            );

            String token = tokenProvider.generateToken(authentication);
            return new AuthResponse(token);
        } catch (BadCredentialsException e) {
            throw new InvalidPasswordException("Неверный пароль или имя пользователя!");
        }
    }


    public void register(AuthRequest request) {
        // Проверка, свободно ли имя пользователя
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Пользователь с именем: " + request.getUsername() + " уже существует!");
        }
        // Записывание пользователя в бд
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }
}