package com.example.config;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MonitoringConfig {

    @Bean
    public MeterRegistryCustomizer<PrometheusMeterRegistry> customizeService1() {
        return registry -> registry.config()
                .commonTags("application", "auth-service");
    }
}
