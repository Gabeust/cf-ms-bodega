package com.gabeust.notificationservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
/**
 * Configuración de WebSocket para habilitar la comunicación en tiempo real
 * entre el servidor y los clientes mediante STOMP sobre WebSocket.
 *
 * Esta clase define el endpoint para las conexiones WebSocket y configura
 * el broker de mensajes para enrutar los mensajes entrantes y salientes.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    /**
     * Registra el endpoint principal de WebSocket al que los clientes pueden conectarse.
     *
     * Se permite el uso de SockJS como fallback para navegadores que no soportan WebSocket.
     *
     * @param registry el registro donde se definen los endpoints STOMP
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stock-alerts").setAllowedOriginPatterns("*").withSockJS();
    }
    /**
     * Configura el broker de mensajes.
     *
     * Los mensajes enviados desde el cliente con prefijo "/app" serán manejados por métodos del servidor.
     * Los mensajes enviados desde el servidor hacia los clientes deben tener el prefijo "/topic".
     *
     * @param config configuración del broker de mensajes
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }


}