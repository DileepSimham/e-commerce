package com.e_commerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_commerce.dto.ProductRequest;
import com.e_commerce.dto.StripeResponse;
import com.e_commerce.entity.Cart;
import com.e_commerce.entity.Product;
import com.e_commerce.entity.User;
import com.e_commerce.repository.CartRepository;
import com.e_commerce.repository.ProductRepository;
import com.e_commerce.repository.UserRepository;

@Service
public class CartService {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private StripeService stripeService;

	// 1. Add product to cart
	public Cart addToCart(String user, Long productId) {
		// Find or create cart for the user

		User byUsername = findByUsername(user);

		Cart cart = cartRepository.findByUser(byUsername).orElseGet(() -> new Cart());
		cart.setUser(byUsername); // Ensure cart is associated with the user

		// Find the product by ID
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new RuntimeException("Product not found with ID" + productId));

		// Add product to cart
		cart.getProducts().add(product);

		// Save or update cart in the database
		return cartRepository.save(cart);
	}

	// 2. Remove product from cart
	public Cart removeFromCart(String user, Long productId) {
		// Find the user's cart
		Cart cart = cartRepository.findByUser(findByUsername(user))
				.orElseThrow(() -> new RuntimeException("Cart not found"));

		// Remove product from cart
		cart.getProducts().removeIf(product -> product.getId().equals(productId));

		// Save updated cart
		return cartRepository.save(cart);
	}

	// 3. Get all products in the user's cart
	public List<Product> getCartProducts(String user) {
		// Find the user's cart
		Cart cart = cartRepository.findByUser(findByUsername(user))
				.orElseThrow(() -> new RuntimeException("Cart not found"));

		return cart.getProducts(); // Return list of products in the cart
	}

	// 4. Checkout (empty cart after checkout)
	public Cart checkout(String user) {
		// Find the user's cart
		Cart cart = cartRepository.findByUser(findByUsername(user))
				.orElseThrow(() -> new RuntimeException("Cart not found"));

		// Add logic for processing checkout (order, payment, etc.)

		// Empty the cart after checkout
		cart.getProducts().clear();
		return cartRepository.save(cart); // Save the empty cart
	}

	// 4. Checkout (empty cart after checkout)
	public StripeResponse checkout2(String user) {
		// Find the user's cart
		Cart cart = cartRepository.findByUser(findByUsername(user))
				.orElseThrow(() -> new RuntimeException("Cart not found"));

		// Add logic for processing checkout (order, payment, etc.)
		List<Product> products = cart.getProducts();

		String productName = "";
		double totalPrice = 0.0;
		Long quantity = 0L;

		for (Product product : products) {
			productName += product.getName()+" , ";
			totalPrice += product.getPrice();

		}
		ProductRequest productRequest = new ProductRequest();

		productRequest.setName(productName);

		double myDouble = 10.75;
		Long myLong = Long.valueOf((long) totalPrice); // Convert to long, then wrap in Long
		System.out.println(myLong);
		productRequest.setAmount(myLong*100);

		productRequest.setCurrency("USD");
		productRequest.setQuantity(Long.valueOf(products.size()));

		StripeResponse checkoutProducts = stripeService.checkoutProducts(productRequest,user);

		// Empty the cart after checkout
//		cart.getProducts().clear();
//		cartRepository.save(cart);
		// Save the empty cart

		return checkoutProducts;
	}

	private User findByUsername(String username) {
		Optional<User> byUsername = userRepository.findByUsername(username);
		if (byUsername.isPresent()) {
			return byUsername.get();
		} else {
			throw new RuntimeException("username not found");
		}
	}
}
