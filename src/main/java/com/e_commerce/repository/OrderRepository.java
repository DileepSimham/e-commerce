package com.e_commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.e_commerce.entity.Order;
import com.e_commerce.entity.User;

public interface OrderRepository extends JpaRepository<Order, Long> {

	// Custom query to find orders by user
	List<Order> findByUser(User user);

	List<Order> findByOrderStatus(String string);

}
