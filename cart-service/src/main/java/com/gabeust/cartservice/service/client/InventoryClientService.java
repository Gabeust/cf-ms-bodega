package com.gabeust.cartservice.service.client;

import com.gabeust.cartservice.dto.InventoryDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class InventoryClientService {
    @Value("${api.gateway.secret}")
    private String gatewaySecret;

    private final WebClient inventoryClient;

    public InventoryClientService(WebClient inventoryServiceClient) {
        this.inventoryClient = inventoryServiceClient;
    }

    public InventoryDTO getInventoryByWineId(Long wineId) {
        return inventoryClient.get()
                .uri("/details/{wineId}", wineId)
                .header("X-Gateway-Auth", gatewaySecret)
                .retrieve()
                .bodyToMono(InventoryDTO.class)
                .block();
    }

    public void decreaseStock(Long wineId, int amount) {
        inventoryClient.post()
                .uri("/{wineId}/decrease?amount={amount}", wineId, amount)
                .header("X-Gateway-Auth", gatewaySecret)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}