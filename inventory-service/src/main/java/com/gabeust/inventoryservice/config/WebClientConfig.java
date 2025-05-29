package com.gabeust.inventoryservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
/**
 * Clase de configuración que define un bean reutilizable de {@link WebClient.Builder}.
 *
 * Este bean permite realizar llamadas HTTP reactivas desde cualquier componente de la aplicación,
 * como filtros, servicios o controladores.
 *
 * {@code WebClient} es una alternativa no bloqueante a {@code RestTemplate}, ideal para arquitecturas reactivas.
 */
@Configuration
public class WebClientConfig {
    /**
     * Crea y expone un {@link WebClient.Builder} como bean para su inyección en otros componentes.
     *
     * @return una instancia de {@link WebClient.Builder}
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
