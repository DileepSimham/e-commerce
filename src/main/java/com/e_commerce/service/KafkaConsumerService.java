package com.e_commerce.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.e_commerce.entity.Order;
import com.e_commerce.entity.OrderHistory;
import com.e_commerce.entity.Product;
import com.e_commerce.repository.OrderHistoryRepository;
import com.e_commerce.repository.OrderRepository;
import com.e_commerce.repository.ProductRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaConsumerService {

	@Autowired
	private OrderHistoryRepository orderHistoryRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private EmailService emailService;

	@KafkaListener(topics = "order-status", groupId = "e-commerce", concurrency = "3")
	public void consumeOrderStatusUpdate(Order order) {
		try {

			log.info("Order received by Kafka listener: {}", order);
			// Update order status sequentially
			updateOrderStatus(order);

		} catch (Exception e) {

			log.error("Error occurred while receiving order: {}", e.getMessage(), e);
		}
	}

	private void updateOrderStatus(Order order) {
		String[] statuses = { "PROCESSING", "SHIPPED", "OUT-FOR-DELIVERY", "DELIVERED" };

		String channel = "/topic/orders/" + order.getUser().getUsername();
		for (String status : statuses) {
			try {
				Thread.sleep(10000); // Simulate delay between state transitions (5 seconds)
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			order.setOrderStatus(status);
			order.setUpdatedAt(LocalDateTime.now());
			orderRepository.save(order);

			log.info("Order status updated to: {}", status);

			// Notify WebSocket clients about the status update
			messagingTemplate.convertAndSend(channel, order);
		}

		log.info("Final order state achieved: {}", order.getOrderStatus());

		saveInOrderHistory(order);

		try {
			log.info("sending email to user {} that order has been delivered", order.getUser().getUsername());

			emailService.sendMail(order.getUser().getUsername(), "Order Delivered",
					"Hi, Your order has been delivered", order);

		} catch (Exception e) {
			log.error("Error occurred while sending mail: {}", e.getMessage());
		}

	}

	private void saveInOrderHistory(Order order) {
		try {
			log.info("Starting to save order history for Order : {}", order);
			log.info("Product Id: {}", order.getProduct().getId());
			Long id = order.getProduct().getId();
			System.out.println(id);

			Product product = productRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("product not found with ID: " + id));

			// Create a new OrderHistory object
			OrderHistory orderHistory = new OrderHistory();
			orderHistory.setOrderDate(order.getCreatedAt());

			// Add the product to the history
			List<Product> products = new ArrayList<>();
			products.add(product);
			orderHistory.setProducts(products);
			orderHistory.setOrderPlacedDate(order.getCreatedAt());
			orderHistory.setOrderDeliveryDate(order.getUpdatedAt());

			// Set additional details
			orderHistory.setStatus("COMPLETED");
			orderHistory.setTotalAmount(order.getTotalPrice());
			orderHistory.setUser(order.getUser());

			// Save to the repository
			orderHistoryRepository.saveAndFlush(orderHistory);

			log.info("Order history successfully saved for Order ID: {}. Order details: {}", order.getId(),
					orderHistory);
		} catch (Exception e) {
			log.error("Failed to save order history for Order ID: {}. Error: {}", order.getId(), e.getMessage(), e);
		}
	}

}
