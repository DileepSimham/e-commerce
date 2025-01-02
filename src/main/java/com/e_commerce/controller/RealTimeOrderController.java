package com.e_commerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.e_commerce.entity.Order;
import com.e_commerce.service.OrderService;

@Controller
public class RealTimeOrderController {
	
	@Autowired
	private OrderService orderService;
	
	@MessageMapping("/sendMessage")
	@SendTo("/topic/orderStatus")
	public ResponseEntity<List<Order>> getOrdersByEmail(@RequestParam String userEmail) {
		try {
			List<Order> orders = orderService.getOrdersByEmail(userEmail);
			return ResponseEntity.ok(orders);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(null);
		}
	}

}
