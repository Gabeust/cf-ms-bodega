package com.gabeust.inventoryservice.service.messaging.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class StockAlertProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "stock-alerts";

    public StockAlertProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendStockAlert(String wineId) {
        kafkaTemplate.send(TOPIC, "Stock Alert: Wine with ID " + wineId + " has reached minimum stock level.");
    }
}

