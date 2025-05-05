package com.example.filter;

import com.example.provider.JwtTokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
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
        System.out.println("Token received: " + token);

        if (!StringUtils.hasText(token)) {
            System.out.println("No token found");
            return chain.filter(exchange);
        }

        return tokenProvider.validateToken(token)
                .flatMap(valid -> {
                    System.out.println("Token valid: " + valid);
                    if (!valid) {
                        return chain.filter(exchange);
                    }
                    return tokenProvider.getAuthentication(token)
                            .flatMap(auth -> {
                                System.out.println("Authentication successful for user: " + auth.getName());
                                exchange.getRequest().mutate()
                                        .header("X-Authenticated-User", auth.getName())
                                        .build();

                                return chain.filter(exchange)
                                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
                            });
                });
    }

    private String resolveToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}