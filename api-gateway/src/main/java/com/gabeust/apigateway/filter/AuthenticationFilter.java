package com.gabeust.apigateway.filter;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
/**
 * Filtro de autenticación para el API Gateway que valida el token JWT
 * en las peticiones entrantes, excepto en rutas públicas.
 *
 * Usa un servicio externo para validar el token y agrega un header
 * secreto para que los microservicios confíen en que la petición
 * viene del Gateway.
 */
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Value("${api.gateway.secret}")
    private String gatewaySecret;
    @Autowired
    private WebClient.Builder webClientBuilder;

    public AuthenticationFilter() {
        super(Config.class);
    }

    public static class Config {

    }

    /**
     * Aplica el filtro de autenticación.
     *
     * Verifica si la ruta es pública para dejar pasar sin token.
     * En caso contrario, valida la existencia y formato del token JWT en el header Authorization.
     * Luego llama a un servicio externo para validar el token.
     * Si es válido, añade el header secreto y permite el paso.
     * Si no, responde con 401 Unauthorized.
     *
     * @param config configuración del filtro (vacía en esta implementación)
     * @return un filtro que valida la autenticación
     */
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Ignorar rutas públicas
            if (isPublicPath(request)) {
                return chain.filter(exchange);
            }

            // Validar Authorization header
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return unauthorized(exchange);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return unauthorized(exchange);
            }

            String token = authHeader.substring(7); // Remueve "Bearer "

            return webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8083/api/v1/auth/validate?token=" + token)
                    .header("X-Gateway-Auth", gatewaySecret)
                    .retrieve()
                    .onStatus(status -> status.isError(), response -> Mono.error(new RuntimeException("Token inválido")))
                    .toBodilessEntity()
                    .flatMap(response -> {
                        // Agregar X-Gateway-Auth al request original
                        var modifiedRequest = exchange.getRequest().mutate()
                                .header("X-Gateway-Auth", gatewaySecret)
                                .build();

                        var modifiedExchange = exchange.mutate()
                                .request(modifiedRequest)
                                .build();

                        return chain.filter(modifiedExchange);

                    })

                    .onErrorResume(e -> unauthorized(exchange));
        };
    }

    /**
     * Comprueba si la ruta actual es pública y no requiere autenticación.
     *
     * @param request petición HTTP
     * @return true si la ruta es pública, false si requiere autenticación
     */
    private boolean isPublicPath(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        return path.contains("/api/auth/login");
    }

    /**
     * Responde con un código 401 Unauthorized y termina la cadena de filtros.
     *
     * @param exchange contexto del servidor web
     * @return un Mono que representa la operación de respuesta completa
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
