package com.example.filter;

import com.example.provider.JwtTokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class JwtAuthenticationFilter implements WebFilter {
    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = resolveToken(exchange.getRequest());
        if (token == null || !tokenProvider.validateToken(token)) {
            return chain.filter(exchange);
        }

        return tokenProvider.getAuthentication(token)
                .flatMap(authentication -> {
                    // Сохраняем аутентификацию в реактивном контексте
                    return chain.filter(exchange)
                            .contextWrite(ctx -> ctx.put(Authentication.class, authentication));
                });
    }

    private String resolveToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            System.out.println(token);
            return token;
        }
        return null;
    }
}