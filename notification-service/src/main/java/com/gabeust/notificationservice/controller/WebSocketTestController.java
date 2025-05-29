package com.gabeust.notificationservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test-ws")
public class WebSocketTestController {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketTestController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/send")
    public ResponseEntity<String> sendTestMessage() {
        messagingTemplate.convertAndSend("/topic/stock", "🚨 TEST ALERT desde WebSocket backend");
        return ResponseEntity.ok("Mensaje enviado por WebSocket");
    }
}
