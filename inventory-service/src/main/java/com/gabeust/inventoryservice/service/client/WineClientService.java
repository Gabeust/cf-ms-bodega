package com.gabeust.inventoryservice.service.client;

import com.gabeust.inventoryservice.dto.WineDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Servicio que actúa como cliente para comunicarse con el microservicio WineService a través de WebClient.
 *
 * Este servicio utiliza la URL base de WineService y agrega la cabecera de seguridad {@code X-Gateway-Auth}
 * para garantizar que las solicitudes sean aceptadas cuando pasan por el API Gateway.
 */
@Service
public class WineClientService {

    @Value("${api.gateway.secret}")
    private String gatewaySecret;
    /**
     * Constructor que inicializa el WebClient con la URL base del microservicio WineService.
     *
     * @param webClientBuilder constructor de WebClient inyectado por Spring
     */
    private final WebClient webClient;

    public WineClientService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080/api/v1/wines").build(); // URL base de tu WineService
    }

    /**
     * Obtiene la información de un vino por su ID desde el microservicio WineService.
     *
     * @param wineId ID del vino que se desea obtener
     * @return {@link WineDTO} con los datos del vino
     */
    public WineDTO getWineById(Long wineId)  {

        return webClient.get()
                .uri("/{id}", wineId)
                .header("X-Gateway-Auth", gatewaySecret)
                .retrieve()
                .bodyToMono(WineDTO.class)
                .block();
    }
}
