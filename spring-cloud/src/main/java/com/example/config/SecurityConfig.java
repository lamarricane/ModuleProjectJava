package com.example.config;

import com.example.filter.JwtAuthenticationFilter;
import com.example.provider.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, JwtAuthenticationFilter jwtFilter) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.GET, "/api/catalog/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/catalog/**").authenticated()
                        .pathMatchers(HttpMethod.PUT, "/api/catalog/**").authenticated()
                        .pathMatchers(HttpMethod.DELETE, "/api/catalog/**").authenticated()
                        .pathMatchers(HttpMethod.GET, "/api/readers/**").authenticated()
                        .pathMatchers(HttpMethod.POST, "/api/readers/**").authenticated()
                        .pathMatchers(HttpMethod.DELETE, "/api/readers/**").authenticated()
                        .pathMatchers("/api/auth/**").permitAll()
                        .pathMatchers("/eureka/**").permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider(jwtSecret);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        return new JwtAuthenticationFilter(tokenProvider);
    }

    @Bean
    public WebSessionServerSecurityContextRepository securityContextRepository() {
        return new WebSessionServerSecurityContextRepository();
    }
}