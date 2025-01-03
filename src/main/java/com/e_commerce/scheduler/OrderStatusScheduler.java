//package com.e_commerce.scheduler;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import com.e_commerce.entity.Order;
//import com.e_commerce.entity.OrderHistory;
//import com.e_commerce.repository.OrderRepository;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Service
//@Slf4j
//public class OrderStatusScheduler {
//
//	@Autowired
//	private OrderRepository orderRepository;
//
//	@Autowired
//	private SimpMessagingTemplate messagingTemplate;
//
//	// Scheduled task to run every minute
//	@Scheduled(cron = "0 * * * * ?") // Runs at the start of every minute
//	public void updateOrderStatus() {
//		// Get all orders with a status of "Pending"
//		List<Order> pendingOrders = orderRepository.findByOrderStatus("PENDING");
//
//		for (Order order : pendingOrders) {
//			if (order.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(1))) {
//				order.setOrderStatus("PROCESSING");
//				orderRepository.save(order);
//				log.info("Order with ID {} has been updated to 'PROCESSING' at {}", order.getId(), LocalDateTime.now());
//				notifyClients(order);
//			}
//		}
//
//		// Get all orders with a status of "Processing"
//		List<Order> processingOrders = orderRepository.findByOrderStatus("PROCESSING");
//
//		for (Order order : processingOrders) {
//			if (order.getUpdatedAt().isBefore(LocalDateTime.now().minusMinutes(2))) {
//				order.setOrderStatus("SHIPPED");
//				orderRepository.save(order);
//				log.info("Order with ID {} has been updated to 'SHIPPED' at {}", order.getId(), LocalDateTime.now());
//				notifyClients(order);
//			}
//		}
//
//		// Get all orders with a status of "Shipped"
//		List<Order> shippedOrders = orderRepository.findByOrderStatus("SHIPPED");
//
//		for (Order order : shippedOrders) {
//			if (order.getUpdatedAt().isBefore(LocalDateTime.now().minusMinutes(3))) {
//				order.setOrderStatus("OUT-FOR-DELIVERY");
//				orderRepository.save(order);
//				log.info("Order with ID {} has been updated to 'OUT-FOR-DELIVERY' at {}", order.getId(),
//						LocalDateTime.now());
//				notifyClients(order);
//			}
//		}
//
//		// Get all orders with a status of "Out-for-Delivery"
//		List<Order> outForDeliveryOrders = orderRepository.findByOrderStatus("OUT-FOR-DELIVERY");
//
//		for (Order order : outForDeliveryOrders) {
//			if (order.getUpdatedAt().isBefore(LocalDateTime.now().minusMinutes(4))) {
//				order.setOrderStatus("DELIVERED");
//				orderRepository.save(order);
//				log.info("Order with ID {} has been updated to 'DELIVERED' at {}", order.getId(), LocalDateTime.now());
//				notifyClients(order);
//			}
//		}
//	}
//
//	private void notifyClients(Order order) {
//		// Send a message to the WebSocket channel
//		String channel = "/topic/orders/" + order.getUser().getUsername();
//		messagingTemplate.convertAndSend(channel, order);
//		log.info("Notification sent to WebSocket channel '{}' for order with ID {}", channel, order.getId());
//	}
//}
