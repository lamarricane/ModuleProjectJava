package com.example.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/api/auth/**")
                        .uri("lb://auth-service"))
                .route("book-service", r -> r.path("/api/catalog/**")
                        .uri("lb://book-service"))
                .route("readers-service", r -> r.path("/api/readers/**")
                        .uri("lb://readers-service"))
                .build();
    }
}