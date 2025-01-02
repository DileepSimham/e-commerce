package com.e_commerce.config;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.e_commerce.entity.Order;
import com.e_commerce.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;



public class OrderWebSocketHandler extends TextWebSocketHandler {

	@Autowired
	private OrderService orderService;

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {

		List<Order> ordersByEmail = orderService.getOrdersByEmail("simhamdileepkumar@gmail.com");

		// Sending StockPrice
		TextMessage message = new TextMessage(objectMapper.writeValueAsString(ordersByEmail));
		session.sendMessage(message);

		sessions.add(session);
	}

}
