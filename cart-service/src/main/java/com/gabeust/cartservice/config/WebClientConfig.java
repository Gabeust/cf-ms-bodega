package com.gabeust.cartservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient wineServiceClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8080/api/v1/wines")
                .build();
    }

    @Bean
    public WebClient inventoryServiceClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8081/api/v1/inventory")
                .build();
    }
 }
