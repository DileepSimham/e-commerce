package com.e_commerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.e_commerce.dto.StripeResponse;
import com.e_commerce.entity.Order;
import com.e_commerce.service.OrderService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {

	@Autowired
	private OrderService orderService;

	@PostMapping("/place")
	public ResponseEntity<Order> placeOrder(@RequestParam Long productId, @RequestParam String userEmail,
			@RequestParam int quantity) {

		log.info("Request to place an order received. Product ID: {}, User Email: {}, Quantity: {}", productId,
				userEmail, quantity);
		try {
			Order order = orderService.placeOrder(productId, userEmail, quantity);
			log.info("Order placed successfully. Order ID: {}", order.getId());
			return ResponseEntity.ok(order);
		} catch (IllegalArgumentException e) {
			log.error("Error placing order: {}", e.getMessage());
			return ResponseEntity.badRequest().body(null);
		}
	}

	@PostMapping("/makePayment")
	public ResponseEntity<StripeResponse> makeOrderWithStripe(@RequestParam Long productId,
			@RequestParam String userEmail, @RequestParam int quantity) {

		log.info("Request to make payment received. Product ID: {}, User Email: {}, Quantity: {}", productId, userEmail,
				quantity);
		try {
			StripeResponse stripeResponse = orderService.placeOrder2(productId, userEmail, quantity);

			log.info("Payment link sent for Product ID: {}, User Email: {}", productId, userEmail);
			return ResponseEntity.ok(stripeResponse);
		} catch (IllegalArgumentException e) {
			log.error("Error fetching stripe payment link: {}", e.getMessage());
			return ResponseEntity.badRequest().body(null);
		}
	}

	// New endpoint to fetch orders by email
	@GetMapping("/by-email")
	public ResponseEntity<List<Order>> getOrdersByEmail(@RequestParam String userEmail) {

		log.info("Request to fetch orders by email. User Email: {}", userEmail);
		try {
			List<Order> orders = orderService.getOrdersByEmail(userEmail);
			log.info("Orders retrieved successfully for User Email: {}. Total Orders: {}", userEmail, orders.size());
			return ResponseEntity.ok(orders);
		} catch (RuntimeException e) {
			log.error("Error fetching orders for User Email: {}. Error: {}", userEmail, e.getMessage());
			return ResponseEntity.badRequest().body(null);
		}
	}

	@PutMapping("/changestatus")
	public ResponseEntity<?> updateOrderStatus(@RequestParam Long orderId, @RequestParam String newStatus) {
		log.info("Request to update order status. Order ID: {}, New Status: {}", orderId, newStatus);

		try {

			orderService.updateOrderStatus(orderId, newStatus);
			log.info("Order status updated successfully. Order ID: {}, New Status: {}", orderId, newStatus);
			return ResponseEntity.ok("Order status updated successfully.");
		} catch (RuntimeException e) {
			 log.error("Error updating order status for Order ID: {}. Error: {}", orderId, e.getMessage());
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

}
