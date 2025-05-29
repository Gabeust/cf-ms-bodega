package com.gabeust.notificationservice.kafka.listener;

import com.gabeust.notificationservice.entity.StockAlert;
import com.gabeust.notificationservice.repository.StockAlertRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
/**
 * Servicio que escucha mensajes de alertas de stock desde un tópico de Kafka,
 * los guarda en la base de datos y los transmite por WebSocket.
 */
@Service
public class StockAlertListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final StockAlertRepository alertRepository;

    /**
     * Constructor de StockAlertListener.
     *
     * @param messagingTemplate plantilla para enviar mensajes por WebSocket
     * @param alertRepository   repositorio para guardar las alertas de stock
     */
    public StockAlertListener(SimpMessagingTemplate messagingTemplate, StockAlertRepository alertRepository) {
        this.messagingTemplate = messagingTemplate;
        this.alertRepository = alertRepository;
    }

    /**
     * Escucha mensajes en el tópico de Kafka "stock-alerts",
     * los guarda en la base de datos y los envía a los clientes
     * suscritos por WebSocket.
     *
     * @param message mensaje de alerta recibido desde Kafka
     */
    @KafkaListener(topics = "stock-alerts", groupId = "stock-alert-group")
    public void listen(String message) {
        System.out.println("🛑 STOCK ALERT RECEIVED: " + message);

        // Guarda en la base de datos
        StockAlert alert = new StockAlert();
        alert.setMessage(message);
        alertRepository.save(alert);

        // Envía mensaje por WebSocket
        messagingTemplate.convertAndSend("/topic/stock", message);
    }
}
