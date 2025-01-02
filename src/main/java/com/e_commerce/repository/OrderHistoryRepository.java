package com.e_commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.e_commerce.entity.OrderHistory;
import com.e_commerce.entity.User;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {
	
	 List<OrderHistory> findByUser(User user);
	 
	 

}
