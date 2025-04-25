package com.gabeust.cartservice.service.client;

import com.gabeust.cartservice.dto.InventoryDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class InventoryClientService {

    private final WebClient inventoryClient;

    public InventoryClientService(WebClient inventoryServiceClient) {
        this.inventoryClient = inventoryServiceClient;
    }

    public InventoryDTO getInventoryByWineId(Long wineId) {
        return inventoryClient.get()
                .uri("/{wineId}", wineId)
                .retrieve()
                .bodyToMono(InventoryDTO.class)
                .block();
    }

    public void decreaseStock(Long wineId, int amount) {
        inventoryClient.post()
                .uri("/{wineId}/decrease?amount={amount}", wineId, amount)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}