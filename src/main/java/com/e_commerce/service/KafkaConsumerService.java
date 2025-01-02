package com.e_commerce.service;

import com.e_commerce.entity.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private final ObjectMapper objectMapper = new ObjectMapper();

//    @KafkaListener(topics = "order-status-updates", groupId = "myGroup")
//    public void consumeOrderStatusUpdate(String message) {
//        try {
//            // Deserialize the JSON into the Order entity
//            Order order = objectMapper.readValue(message, Order.class);
//            System.out.println("Received Order Update: " + order);
//
//            // Forward the updated order to WebSocket
////            OrderStatusWebSocketHandler.broadcastMessage(message); // Broadcast JSON message
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.err.println("Failed to deserialize order: " + e.getMessage());
//        }
//    }
}
