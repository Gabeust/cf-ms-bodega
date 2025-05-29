package com.gabeust.notificationservice.kafka.listener;

import com.gabeust.notificationservice.entity.StockAlert;
import com.gabeust.notificationservice.repository.StockAlertRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class StockAlertListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final StockAlertRepository alertRepository;


    public StockAlertListener(SimpMessagingTemplate messagingTemplate, StockAlertRepository alertRepository) {
        this.messagingTemplate = messagingTemplate;
        this.alertRepository = alertRepository;
    }

    @KafkaListener(topics = "stock-alerts", groupId = "stock-alert-group")
    public void listen(String message) {
        System.out.println("🛑 STOCK ALERT RECEIVED: " + message);

        // Guardar en la base de datos
        StockAlert alert = new StockAlert();
        alert.setMessage(message);
        alertRepository.save(alert);
        messagingTemplate.convertAndSend("/topic/stock", message);
    }
}
