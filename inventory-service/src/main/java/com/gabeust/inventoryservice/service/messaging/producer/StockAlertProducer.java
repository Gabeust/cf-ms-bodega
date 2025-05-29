package com.gabeust.inventoryservice.service.messaging.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
/**
 * Productor de mensajes Kafka para enviar alertas de stock mínimo en vinos.
 *
 * Envía mensajes al tópico {@code stock-alerts} indicando que un vino ha alcanzado
 * el nivel mínimo de stock para que otros microservicios puedan procesar la alerta.
 */
@Service
public class StockAlertProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "stock-alerts";
    /**
     * Constructor que inyecta la plantilla Kafka para enviar mensajes.
     *
     * @param kafkaTemplate plantilla de Kafka para enviar mensajes
     */
    public StockAlertProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    /**
     * Envía una alerta de stock mínimo para el vino especificado.
     *
     * @param wineId ID del vino cuyo stock ha llegado al mínimo
     */
    public void sendStockAlert(String wineId) {
        kafkaTemplate.send(TOPIC, "Stock Alert: Wine with ID " + wineId + " has reached minimum stock level.");
    }
}

