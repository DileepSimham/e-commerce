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
import com.e_commerce.entity.Cart;
import com.e_commerce.service.CartService;
import com.e_commerce.service.StripeService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/cart")
@Slf4j
public class CartController {

	@Autowired
	private CartService cartService;

	@Autowired
	private StripeService stripeService;

	@PostMapping("/add")
	public ResponseEntity<Cart> addToCart(@RequestParam Long productId, String user) {
		log.info("Request to add product to cart. Product ID: {}, User: {}", productId, user);
		Cart updatedCart = cartService.addToCart(user, productId);
		log.info("Product added to cart successfully. User: {}, Product ID: {}", user, productId);
		return ResponseEntity.ok(updatedCart);
	}

	@PostMapping("/remove")
	public ResponseEntity<Cart> removeFromCart(@RequestParam Long productId, String user) {
		log.info("Request to remove product from cart. Product ID: {}, User: {}", productId, user);
		Cart updatedCart = cartService.removeFromCart(user, productId);
		log.info("Product removed from cart successfully. User: {}, Product ID: {}", user, productId);
		return ResponseEntity.ok(updatedCart);
	}

	@GetMapping("/cart/{user}")
	public ResponseEntity<Cart> getCart(@PathVariable("user") String user) {
		log.info("Request to fetch cart for user: {}", user);
		Cart cartProducts = cartService.getCartProducts(user);
		log.info("Cart fetched successfully for User: {}. Total items: {}", user, cartProducts.getProducts().size());
		return ResponseEntity.ok(cartProducts);
	}

	@GetMapping("/getAllCarts")
	public ResponseEntity<List<Cart>> getAllCart() {
		log.info("Request to fetch all carts.");
		List<Cart> carts = cartService.getAllCarts();
		log.info("Total number of carts fetched: {}", carts.size());
		return ResponseEntity.ok(carts);
	}

	@PostMapping("/checkout/{user}")
	public ResponseEntity<Cart> checkout(@PathVariable("user") String user) {
		log.info("Request to checkout for user: {}", user);
		Cart updatedCart = cartService.checkout(user);
		log.info("Checkout successful for User: {}. Updated cart: {}", user, updatedCart);
		return ResponseEntity.ok(updatedCart);
	}

	@PostMapping("/checkout2/{user}")
	public ResponseEntity<StripeResponse> checkout2(@PathVariable("user") String user) {
		log.info("Request to process Stripe checkout for user: {}", user);
		StripeResponse stripeResponse = cartService.checkout2(user);
		log.info("Stripe checkout successful for User: {}. Stripe Response: {}", user, stripeResponse);
		return ResponseEntity.ok(stripeResponse);
	}

	@PutMapping("/update-status")
	public ResponseEntity<Cart> changeProductStatus(@RequestParam String user, @RequestParam String newStatus) {
		log.info("Request to update product status in cart. User: {}, New Status: {}", user, newStatus);
		Cart updatedCart = cartService.changeProductStatus(user, newStatus);
		log.info("Product status updated successfully in cart. User: {}, New Status: {}", user, newStatus);
		return ResponseEntity.ok(updatedCart);
	}

//	@PostMapping("/checkout")
//    public ResponseEntity<StripeResponse> checkoutProducts(@RequestBody ProductRequest productRequest) {
//        StripeResponse stripeResponse = stripeService.checkoutProducts(productRequest);
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(stripeResponse);
//    }
}
