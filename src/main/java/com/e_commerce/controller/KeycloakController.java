package com.e_commerce.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.e_commerce.entity.User;
import com.e_commerce.service.KeycloakService;
import com.e_commerce.service.UserService;

@RestController
@RequestMapping("/keycloak")
public class KeycloakController {

	@Autowired
	private KeycloakService keycloakService;
	
	@Autowired
	private UserService userService;

	@GetMapping("/users")
	public List<Map<String, Object>> getUsers() {
		return keycloakService.fetchUsers();
	}
	
	 // Get all users
    @GetMapping("/users2")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

}
