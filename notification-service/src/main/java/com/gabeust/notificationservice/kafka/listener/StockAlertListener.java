package com.gabeust.notificationservice.kafka.listener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class StockAlertListener {
    @KafkaListener(topics = "stock-alerts", groupId = "notification-group")
    public void handleStockAlert(ConsumerRecord<String, String> record) {
        System.out.println("🛑 STOCK ALERT RECEIVED: " + record.value());

    }
}
