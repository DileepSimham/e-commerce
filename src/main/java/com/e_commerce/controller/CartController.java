package com.e_commerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.e_commerce.dto.ProductRequest;
import com.e_commerce.dto.StripeResponse;
import com.e_commerce.entity.Cart;
import com.e_commerce.entity.Product;
import com.e_commerce.service.CartService;
import com.e_commerce.service.StripeService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	@Autowired
	private CartService cartService;
	
	@Autowired
	private StripeService stripeService;

	@PostMapping("/add")
	public ResponseEntity<Cart> addToCart(@RequestParam Long productId, String user) {
		Cart updatedCart = cartService.addToCart(user, productId);
		return ResponseEntity.ok(updatedCart);
	}

	@PostMapping("/remove")
	public ResponseEntity<Cart> removeFromCart(@RequestParam Long productId, String user) {
		Cart updatedCart = cartService.removeFromCart(user, productId);
		return ResponseEntity.ok(updatedCart);
	}

	@GetMapping("/products/{user}")
	public ResponseEntity<List<Product>> getCartProducts(@PathVariable("user") String user) {
		List<Product> products = cartService.getCartProducts(user);
		return ResponseEntity.ok(products);
	}

	@PostMapping("/checkout/{user}")
	public ResponseEntity<Cart> checkout(@PathVariable("user") String user) {
		Cart updatedCart = cartService.checkout(user);
		return ResponseEntity.ok(updatedCart);
	}
	
	@PostMapping("/checkout2/{user}")
	public ResponseEntity<StripeResponse> checkout2(@PathVariable("user") String user) {
		StripeResponse stripeResponse = cartService.checkout2(user);
		return ResponseEntity.ok(stripeResponse);
	}
	
//	@PostMapping("/checkout")
//    public ResponseEntity<StripeResponse> checkoutProducts(@RequestBody ProductRequest productRequest) {
//        StripeResponse stripeResponse = stripeService.checkoutProducts(productRequest);
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(stripeResponse);
//    }
}
