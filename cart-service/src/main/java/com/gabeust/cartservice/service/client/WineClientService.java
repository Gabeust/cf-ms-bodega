package com.gabeust.cartservice.service.client;

import com.gabeust.cartservice.dto.WineDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
/**
 * Servicio cliente para interactuar con el microservicio de vinos.
 *
 * Usa WebClient para llamar al WineService a través del API Gateway,
 * incluyendo el encabezado de autenticación {@code X-Gateway-Auth} con el secreto configurado.
 */
@Service
public class WineClientService {
    /**
     * Secreto para autenticación con el API Gateway.
     */
    @Value("${api.gateway.secret}")
    private String gatewaySecret;

    private final WebClient wineClient;

    public WineClientService(WebClient wineServiceClient) {
        this.wineClient = wineServiceClient;
    }

    /**
     * Obtiene los datos de un vino por su ID.
     *
     * @param wineId ID del vino.
     * @return DTO con los datos del vino.
     */
    public WineDTO getWineById(Long wineId) {
        return wineClient.get()
                .uri("/{wineId}", wineId)
                .header("X-Gateway-Auth", gatewaySecret)
                .retrieve()
                .bodyToMono(WineDTO.class)
                .block();
    }
}