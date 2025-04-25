package com.gabeust.cartservice.service.client;

import com.gabeust.cartservice.dto.WineDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class WineClientService {

    private final WebClient wineClient;

    public WineClientService(WebClient wineServiceClient) {
        this.wineClient = wineServiceClient;
    }

    public WineDTO getWineById(Long wineId) {
        return wineClient.get()
                .uri("/{id}", wineId)
                .retrieve()
                .bodyToMono(WineDTO.class)
                .block();
    }
}