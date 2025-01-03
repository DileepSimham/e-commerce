package com.e_commerce.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

import com.e_commerce.entity.Product;
import com.e_commerce.service.ProductService;

@RestController
@RequestMapping("/api/products")
@Slf4j
public class ProductController {

    @Autowired
    private ProductService productService;

    // Create or Update a Product
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        log.info("Request received to create/update product: {}", product);
        Product savedProduct = productService.saveProduct(product);
        log.info("Product saved with ID: {}", savedProduct.getId());
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    // Get All Products
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        log.info("Fetching all products.");
        List<Product> products = productService.getAllProducts();
        log.info("Returning {} products.", products.size());
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Get Product by ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") Long id) {
        log.info("Request received to fetch product by ID: {}", id);
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            log.info("Product found with ID: {}", id);
            return ResponseEntity.ok(product.get());
        }
        log.warn("Product not found with ID: {}", id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // Update Product
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable("id") Long id, @RequestBody Product product) {
        log.info("Request received to update product with ID: {}", id);
        Optional<Product> existingProduct = productService.getProductById(id);
        if (existingProduct.isPresent()) {
            product.setId(id);
            Product updatedProduct = productService.saveProduct(product);
            log.info("Product updated with ID: {}", id);
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        }
        log.warn("Product not found with ID: {}", id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // Delete Product
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id) {
        log.info("Request received to delete product with ID: {}", id);
        Optional<Product> existingProduct = productService.getProductById(id);
        if (existingProduct.isPresent()) {
            productService.deleteProduct(id);
            log.info("Product deleted with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        log.warn("Product not found with ID: {}", id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
