package com.gabeust.apigateway.filter;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

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

    private boolean isPublicPath(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        return path.contains("/api/auth/login");
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
