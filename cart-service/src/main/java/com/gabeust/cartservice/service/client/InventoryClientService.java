package com.gabeust.cartservice.service.client;

import com.gabeust.cartservice.dto.InventoryDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
/**
 * Servicio cliente para interactuar con el microservicio de inventario.
 *
 * Este servicio usa WebClient para realizar llamadas HTTP al InventoryService,
 * incluyendo el encabezado de autenticación {@code X-Gateway-Auth} con un secreto configurado,
 * garantizando que las solicitudes provengan del API Gateway autorizado.
 */
@Service
public class InventoryClientService {
    /**
     * Secreto para autenticación con el API Gateway.
     */
    @Value("${api.gateway.secret}")
    private String gatewaySecret;

    private final WebClient inventoryClient;

    public InventoryClientService(WebClient inventoryServiceClient) {
        this.inventoryClient = inventoryServiceClient;
    }

    /**
     * Obtiene el inventario detallado de un vino específico mediante su ID.
     *
     * @param wineId ID del vino a consultar.
     * @return DTO con información del inventario y datos del vino.
     */
    public InventoryDTO getInventoryByWineId(Long wineId) {
        return inventoryClient.get()
                .uri("/details/{wineId}", wineId)
                .header("X-Gateway-Auth", gatewaySecret)
                .retrieve()
                .bodyToMono(InventoryDTO.class)
                .block();
    }
    /**
     * Disminuye el stock disponible para un vino específico.
     *
     * @param wineId ID del vino cuyo stock será reducido.
     * @param amount Cantidad a disminuir del stock.
     */
    public void decreaseStock(Long wineId, int amount) {
        inventoryClient.post()
                .uri("/{wineId}/decrease?amount={amount}", wineId, amount)
                .header("X-Gateway-Auth", gatewaySecret)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}