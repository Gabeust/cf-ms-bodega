package com.gabeust.apigateway.filter;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
/**
 * Configuración de CORS para permitir solicitudes desde orígenes específicos.
 *
 * Esta configuración permite solicitudes desde "http://127.0.0.1:5500" y "http://localhost:5500"
 * con cualquier header y método HTTP, además de permitir el envío de credenciales (cookies, headers de autenticación).
 */
@Configuration
public class CorsConfig {
    /**
     * Crea y configura un filtro CORS para habilitar el acceso desde orígenes permitidos.
     *
     * @return un filtro CORS que intercepta las peticiones y aplica las reglas definidas
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOrigin("http://127.0.0.1:5500");
        corsConfig.addAllowedOrigin("http://localhost:5500");
        corsConfig.addAllowedHeader("*");
        corsConfig.addAllowedMethod("*");
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}
