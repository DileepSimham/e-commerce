package com.e_commerce.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.e_commerce.entity.Cart;
import com.e_commerce.entity.Order;
import com.e_commerce.entity.OrderHistory;
import com.e_commerce.entity.Product;
import com.e_commerce.entity.User;
import com.e_commerce.repository.CartRepository;
import com.e_commerce.repository.OrderHistoryRepository;
import com.e_commerce.repository.OrderRepository;
import com.e_commerce.repository.ProductRepository;
import com.e_commerce.repository.UserRepository;
import com.e_commerce.service.CartService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/webhook")
public class StripeWebhookController {

	@Autowired
	private CartService cartService;

	@Autowired
	private OrderHistoryRepository orderHistoryRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProductRepository productRepository;
	
	
	@Autowired
	private OrderRepository orderRepository;
	
	
	@Autowired
	private CartRepository cartRepository;

	private static final String endpointSecret = "whsec_fc3ceb89476bfa3a1bce1c4bfb694957aa9e10b8b0061409989bc4aa2f75ad0a";

	@PostMapping
	public String handleStripeEvent(HttpServletRequest request, @RequestHeader("Stripe-Signature") String sigHeader,
			@RequestBody String payload) throws IOException {

		Event event = null;

		// Verify webhook signature
		try {
			event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

		} catch (StripeException e) {

			System.out.println(e.getMessage());
			return "Webhook signature verification failed.";
		}

		// Handle the event
		if ("checkout.session.completed".equals(event.getType())) {
			Session session = (Session) event.getData().getObject();

			// Retrieve the session details to confirm payment success
			String sessionId = session.getId();
			String paymentStatus = session.getPaymentStatus();
			String username = session.getMetadata().get("username");
//			String productName = session.getMetadata().get("productName");
			String quantity = session.getMetadata().get("quantity");
			List<String> productNames = session.getMetadata().get("productName") != null
					? List.of(session.getMetadata().get("productName").split(","))
					: new ArrayList<>();

			if ("paid".equals(paymentStatus)) {
				// Payment was successful
				System.out.println("Payment successful for user: " + username + " with session: " + sessionId);
//				cartService.checkout(username);

				// Fetch user
				User user = userRepository.findByUsername(username)
						.orElseThrow(() -> new RuntimeException("User not found"));

//                System.out.println(productNames);
				List<Product> products = new ArrayList<>();
				for (String productName : productNames) {
					Product product = productRepository.findByName(productName)
							.orElseThrow(() -> new RuntimeException("product not found"));
					if (product != null) {
						products.add(product);
					}
				}
				// Calculate total amount
				double totalAmount = products.stream().mapToDouble(Product::getPrice).sum();
				
				
				System.out.println(products);
				
				
				for(Product product:products) {
					// save order
					Order order = new Order();

					order.setUser(user);
					order.setProduct(product);
					order.setQuantity(Integer.parseInt(quantity));
					order.setTotalPrice(product.getPrice() * Integer.parseInt(quantity));
					order.setOrderStatus("PENDING");
					order.setCreatedAt(LocalDateTime.now());
					order.setUpdatedAt(LocalDateTime.now());
					
					

					// Update product stock
					products.get(0).setStock(products.get(0).getStock() - Integer.parseInt(quantity));
					productRepository.save(products.get(0));
					
					orderRepository.save(order);
				}

				
				
				Cart cart = cartRepository.findByUser(user).orElseThrow(()->new RuntimeException("cart not found"));
				
				cart.getProducts().clear();
				
				cartRepository.save(cart);
				

				// Save order history
//                OrderHistory orderHistory = new OrderHistory();
//                orderHistory.setUser(user);
//                orderHistory.setProducts(products);
//                orderHistory.setOrderDate(LocalDateTime.now());
//                orderHistory.setTotalAmount(totalAmount);
//                orderHistory.setStatus("COMPLETED");

//                orderHistoryRepository.save(orderHistory);

//                System.out.println("Order history saved for user: " + username);

				// You can update the order status or perform further actions here
				// Example: updateOrderStatus(sessionId, "COMPLETED");
			} else {
				// Payment failed or incomplete
				System.out.println("Payment failed for session: " + sessionId);
				// Handle the failed payment case
			}
		}

		// Return a response to Stripe indicating the webhook was successfully received
		return "Event received";
	}
}
