package com.e_commerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.e_commerce.entity.Order;
import com.e_commerce.service.OrderService;

@Controller
public class OrderWebSocketController {

	@Autowired
	private OrderService orderService;

	private final SimpMessagingTemplate template;

	@Autowired
	OrderWebSocketController(SimpMessagingTemplate template) {
		this.template = template;
	}

	@MessageMapping("/hello")
	@SendTo("/topic/greetings")
	public String message(String message) {
		System.out.println(message);
//		this.template.convertAndSend("/message", message);
		return "hello web socket";
		
	}

	@MessageMapping("/orders") // Listen for messages sent to "/app/orders"
	@SendTo("/topic/orders") // Send response to all clients subscribed to "/topic/orders"
	public List<Order> sendOrderUpdates(String userEmail) {
		return orderService.getOrdersByEmail(userEmail); // Fetch and return orders for the email
	}

}
