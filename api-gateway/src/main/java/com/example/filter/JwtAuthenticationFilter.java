package com.example.filter;

import com.example.provider.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

/**
 * Фильтр для обработки JWT токенов в запросах:
 * - извлекает токен из заголовка Authorization;
 * - устанавливает аутентификацию в контекст безопасности;
 * - добавляет информацию об аутентифицированном пользователе в заголовки запроса.
 */
public class JwtAuthenticationFilter implements WebFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        Instant start = Instant.now();
        String token = resolveToken(exchange.getRequest());

        if (!StringUtils.hasText(token)) {
            logger.debug("No JWT token found in request to {}", exchange.getRequest().getPath());
            return chain.filter(exchange);
        }

        logger.debug("Processing JWT token for request to {}", exchange.getRequest().getPath());

        return tokenProvider.parseToken(token)
                .flatMap(claims -> {
                    Authentication auth = tokenProvider.createAuthentication(claims);
                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                            .header("X-Authenticated-User", auth.getName())
                            .build();
                    logger.info("Authenticated user {} for request to {}",
                            auth.getName(), exchange.getRequest().getPath());
                    Duration duration = Duration.between(start, Instant.now());
                    logger.debug("JWT processing completed in {} ms", duration.toMillis());

                    return chain.filter(exchange.mutate().request(mutatedRequest).build())
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    /**
     * Извлекает токен из заголовка Authorization
     */
    private String resolveToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}