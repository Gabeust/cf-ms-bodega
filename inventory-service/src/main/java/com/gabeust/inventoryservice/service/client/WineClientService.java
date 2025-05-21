package com.gabeust.inventoryservice.service.client;

import com.gabeust.inventoryservice.dto.WineDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


@Service
public class WineClientService {

    @Value("${api.gateway.secret}")
    private String gatewaySecret;
    private final WebClient webClient;

    public WineClientService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080/api/v1/wines").build(); // URL base de tu WineService
    }

    // Método para obtener un vino por ID desde WineService
    public WineDTO getWineById(Long wineId)  {

        return webClient.get()
                .uri("/{id}", wineId)
                .header("X-Gateway-Auth", gatewaySecret)
                .retrieve()
                .bodyToMono(WineDTO.class)
                .block();
    }
}
