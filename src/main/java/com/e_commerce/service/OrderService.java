package com.e_commerce.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.e_commerce.dto.ProductRequest;
import com.e_commerce.dto.StripeResponse;
import com.e_commerce.entity.Order;
import com.e_commerce.entity.Product;
import com.e_commerce.entity.User;
import com.e_commerce.repository.OrderRepository;
import com.e_commerce.repository.ProductRepository;
import com.e_commerce.repository.UserRepository;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private StripeService stripeService;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	public void updateOrderStatus(Long orderId, String newStatus) {
		// Update order status in the database
		Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
		order.setOrderStatus(newStatus);
		orderRepository.save(order);

		// Notify WebSocket clients about the status update
		
		String channel="/topic/orders/"+order.getUser().getUsername();
		
		messagingTemplate.convertAndSend(channel, order);
	}

	public Order placeOrder(Long productId, String userEmail, int quantity) {

		// make -payment first

		// Find user by email
		User user = userRepository.findByUsername(userEmail)
				.orElseThrow(() -> new RuntimeException("useremail not found"));
		if (user == null) {
			throw new IllegalArgumentException("User not found with email: " + userEmail);
		}

		// Find product by ID
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

		// Validate stock
		if (product.getStock() < quantity) {
			throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
		}

		// Create new order
		Order order = new Order();
		order.setUser(user);
		order.setProduct(product);
		order.setQuantity(quantity);
		order.setTotalPrice(product.getPrice() * quantity);
		order.setOrderStatus("PENDING");
		order.setCreatedAt(LocalDateTime.now());
		order.setUpdatedAt(LocalDateTime.now());

		// Update product stock
		product.setStock(product.getStock() - quantity);
		productRepository.save(product);

		// Save the order
		return orderRepository.save(order);
	}

	public StripeResponse placeOrder2(Long productId, String userEmail, int quantity) {

		// make -payment first

		// Find product by ID
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

		ProductRequest productRequest = new ProductRequest();

		productRequest.setQuantity((long) quantity);
		productRequest.setAmount((long) product.getPrice());
		productRequest.setCurrency("USD");
		productRequest.setName(product.getName());

		StripeResponse checkoutProducts = stripeService.checkoutProducts(productRequest, userEmail,"noncart");

		return checkoutProducts;
	}

	// New method to fetch orders by user email
	public List<Order> getOrdersByEmail(String userEmail) {
		// Find user by email
		User user = userRepository.findByUsername(userEmail)
				.orElseThrow(() -> new RuntimeException("User email not found"));

		// Fetch and return all orders for this user
		return orderRepository.findByUser(user);
	}

}
