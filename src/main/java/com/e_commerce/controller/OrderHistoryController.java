package com.e_commerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.e_commerce.entity.OrderHistory;
import com.e_commerce.entity.User;
import com.e_commerce.repository.OrderHistoryRepository;
import com.e_commerce.repository.UserRepository;

@RestController
@RequestMapping("/orders")
public class OrderHistoryController {

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Fetch all orders for a user by username.
     * 
     * @param username The username of the user.
     * @return A list of orders for the user.
     */
    @GetMapping("/{username}")
    public ResponseEntity<List<OrderHistory>> getOrdersByUsername(@PathVariable String username) {
        // Fetch user by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        // Fetch order history for the user
        List<OrderHistory> orders = orderHistoryRepository.findByUser(user);

        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 if no orders found
        }

        return ResponseEntity.ok(orders); // Return 200 with orders
    }
}
