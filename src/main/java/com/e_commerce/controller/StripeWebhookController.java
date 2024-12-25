package com.e_commerce.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
			String username=session.getMetadata().get("username");

			

			if ("paid".equals(paymentStatus)) {
				// Payment was successful
				System.out.println("Payment successful for user: "+username +" with session: " + sessionId);
				cartService.checkout(username);
				
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
