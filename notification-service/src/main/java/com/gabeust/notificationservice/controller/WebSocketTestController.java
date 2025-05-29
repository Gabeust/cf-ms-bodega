package com.gabeust.notificationservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * Controlador de prueba para enviar mensajes a través de WebSocket.
 *
 * Permite verificar que el canal WebSocket está funcionando correctamente enviando
 * un mensaje de prueba a un tópico específico.
 */
@RestController
@RequestMapping("/test-ws")
public class WebSocketTestController {

    private final SimpMessagingTemplate messagingTemplate;
    /**
     * Constructor que inyecta la plantilla de mensajería WebSocket.
     *
     * @param messagingTemplate plantilla de mensajería utilizada para enviar mensajes a los tópicos
     */
    public WebSocketTestController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Endpoint para enviar un mensaje de prueba al tópico "/topic/stock" vía WebSocket.
     *
     * @return respuesta HTTP indicando que el mensaje fue enviado correctamente
     */
    @GetMapping("/send")
    public ResponseEntity<String> sendTestMessage() {
        messagingTemplate.convertAndSend("/topic/stock", "TEST ALERT from WebSocket backend");
        return ResponseEntity.ok("Message sent via WebSocket");
    }
}
