package com.gabeust.cartservice.service.client;

import com.gabeust.cartservice.dto.WineDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class WineClientService {

    @Value("${api.gateway.secret}")
    private String gatewaySecret;

    private final WebClient wineClient;

    public WineClientService(WebClient wineServiceClient) {
        this.wineClient = wineServiceClient;
    }

    public WineDTO getWineById(Long wineId) {
        return wineClient.get()
                .uri("/{wineId}", wineId)
                .header("X-Gateway-Auth", gatewaySecret)
                .retrieve()
                .bodyToMono(WineDTO.class)
                .block();
    }
}