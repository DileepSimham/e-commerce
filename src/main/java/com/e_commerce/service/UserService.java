package com.e_commerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_commerce.entity.User;
import com.e_commerce.repository.UserRepository;


@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	// Create a new user
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get a user by ID
    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    // Update a user
    public User updateUser(Integer id, User updatedUser) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setUsername(updatedUser.getUsername());
//            user.setPassword(updatedUser.getPassword());
            user.setEmail(updatedUser.getEmail());
//            user.setRole(updatedUser.getRole());
            return userRepository.save(user);
        } else {
            // Handle user not found, could throw an exception or return null
            return null;
        }
    }

    // Delete a user by ID
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    // Check if a user exists by ID
    public boolean userExists(Integer id) {
        return userRepository.existsById(id);
    }

}
