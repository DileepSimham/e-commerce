package com.e_commerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_commerce.entity.Product;
import com.e_commerce.repository.ProductRepository;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;

	// Create or Update Product
	public Product saveProduct(Product product) {
		return productRepository.save(product);
	}

	// Get All Products
	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	// Get Product by ID
	public Optional<Product> getProductById(Long id) {
		return productRepository.findById(id);
	}

	// Delete Product by ID
	public void deleteProduct(Long id) {
		productRepository.deleteById(id);
	}
}
