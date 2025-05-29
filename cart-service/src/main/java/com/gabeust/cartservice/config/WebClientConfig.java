package com.gabeust.cartservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
/**
 * Configuración de clientes WebClient para comunicarse con otros microservicios.
 *
 * Esta clase define beans de WebClient preconfigurados con la URL base
 * de los servicios WineService e InventoryService, facilitando la inyección y uso en la aplicación.
 */
@Configuration
public class WebClientConfig {
    /**
     * Bean de WebClient configurado para comunicarse con el servicio de vinos.
     *
     * @return instancia de WebClient con baseUrl configurada para WineService
     */
    @Bean
    public WebClient wineServiceClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8080/api/v1/wines")
                .build();
    }
    /**
     * Bean de WebClient configurado para comunicarse con el servicio de inventario.
     *
     * @return instancia de WebClient con baseUrl configurada para InventoryService
     */
    @Bean
    public WebClient inventoryServiceClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8081/api/v1/inventory")
                .build();
    }
 }
