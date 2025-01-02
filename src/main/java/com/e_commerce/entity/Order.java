package com.e_commerce.entity;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id", nullable = false)
	private User user; // Reference to the User who placed the order

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product; // Reference to the Product in the order

	@Column(nullable = false)
	private int quantity; // Quantity of the product ordered

	@Column(nullable = false)
	private double totalPrice; // Total price for the order (price * quantity)

	@Column(name = "order_status", nullable = false)
	private String orderStatus; // E.g., "PENDING", "SHIPPED", "DELIVERED", etc.

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt; // Timestamp when the order was placed
//
	@Column(name = "updated_at")
	private LocalDateTime updatedAt; // Timestamp when the order was last updated
}
