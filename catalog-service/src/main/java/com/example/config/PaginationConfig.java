package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Настройка для дефолтных значений пагинации
 */
@Configuration
public class PaginationConfig implements WebMvcConfigurer {

    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer pageableResolverCustomizer() {
        return pageableResolver -> {
            pageableResolver.setFallbackPageable(PageRequest.of(0, 10));
            pageableResolver.setMaxPageSize(100);
            pageableResolver.setOneIndexedParameters(true);
        };
    }
}