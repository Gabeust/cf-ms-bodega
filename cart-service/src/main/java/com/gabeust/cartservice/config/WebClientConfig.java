package com.gabeust.cartservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient wineServiceClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8081/api/v1/wines") // Ajustá el puerto
                .build();
    }

    @Bean
    public WebClient inventoryServiceClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8082/api/v1/inventory") // Ajustá el puerto
                .build();
    }
}
