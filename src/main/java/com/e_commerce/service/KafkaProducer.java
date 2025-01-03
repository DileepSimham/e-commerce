package com.e_commerce.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import com.e_commerce.entity.Order;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaProducer {

	private KafkaTemplate<String, Order> kafkaTemplate;


	public KafkaProducer(KafkaTemplate<String, Order> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void updateOrderStatus(Order order) {

		try {
			log.info("Preparing to send order update to Kafka. Order: {}", order);
			Message<Order> message = MessageBuilder.withPayload(order).setHeader(KafkaHeaders.TOPIC, "order-status")
					.build();

			kafkaTemplate.send(message);
			log.info("Order  successfully sent to Kafka topic 'order-status'.");
		} catch (Exception e) {
			log.error("Error sending order update to Kafka: {}", e.getMessage(), e);
		}

	}

}
