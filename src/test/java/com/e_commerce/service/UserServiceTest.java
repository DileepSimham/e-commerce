package com.e_commerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.e_commerce.entity.User;
import com.e_commerce.repository.UserRepository;

class UserServiceTest {

	@Mock
	private UserRepository userRepository; // Mock the UserRepository

	@InjectMocks
	private UserService userService; // Inject the mock into UserService

	private User user;

	@BeforeEach
	public void setUp() {
		// Initialize mocks and set up any data needed for the tests
		MockitoAnnotations.openMocks(this);

		// Set up a User object for the test
		user = new User();
		user.setId(1L);
		user.setUsername("johndoe");
//		user.setPassword("password123");
		user.setEmail("johndoe@example.com");
//		user.setRole("USER");
//		user.setCreatedAt(LocalDateTime.now());
	}

	@Test
	void testCreateUser() {
		// Arrange: mock the save method of the userRepository
		when(userRepository.save(any(User.class))).thenReturn(user);

		// Act: call the method under test
		User createdUser = userService.createUser(user);

		// Assert: verify that the returned user is correct
		assertNotNull(createdUser);
		assertEquals(user.getId(), createdUser.getId());
		assertEquals(user.getUsername(), createdUser.getUsername());
		assertEquals(user.getEmail(), createdUser.getEmail());
//		assertEquals(user.getRole(), createdUser.getRole());
//		assertNotNull(createdUser.getCreatedAt());

		// Verify that save was called once with the given user
		verify(userRepository, times(1)).save(user);
	}

	@Test
	void testGetAllUsers() {
		// Arrange: mock the findAll method of the userRepository
//		User user2 = new User(2L, "janedoe", "password456", "janedoe@example.com", "ADMIN", LocalDateTime.now());
//		when(userRepository.findAll()).thenReturn(Arrays.asList(user, user2));

		// Act: call the method under test
		List<User> users = userService.getAllUsers();

		// Assert: verify that the returned list is correct
		assertNotNull(users); // Ensure the list is not null
		assertEquals(2, users.size()); // Ensure the list contains 2 users

		// Verify the contents of the list
		assertEquals("johndoe", users.get(0).getUsername());
		assertEquals("janedoe", users.get(1).getUsername());

		// Verify that findAll was called once
		verify(userRepository, times(1)).findAll();
	}
	
	@Test
	void testGetUserById() {
	    // Arrange: mock the findById method of the userRepository
	    when(userRepository.findById(1)).thenReturn(Optional.of(user));

	    // Act: call the method under test
	    Optional<User> foundUser = userService.getUserById(1);

	    // Assert: verify the returned Optional<User>
	    assertNotNull(foundUser);
	    assertEquals(true, foundUser.isPresent());
	    assertEquals(user.getId(), foundUser.get().getId());
	    assertEquals(user.getUsername(), foundUser.get().getUsername());
	    assertEquals(user.getEmail(), foundUser.get().getEmail());
//	    assertEquals(user.getRole(), foundUser.get().getRole());
//	    assertNotNull(foundUser.get().getCreatedAt());

	    // Verify that findById was called once with the correct ID
	    verify(userRepository, times(1)).findById(1);
	}
	
	@Test
	void testUpdateUser_Success() {
	    // Arrange
	    User updatedUser = new User();
	    updatedUser.setUsername("updatedName");
//	    updatedUser.setPassword("updatedPassword");
	    updatedUser.setEmail("updated@example.com");
//	    updatedUser.setRole("ADMIN");

	    when(userRepository.findById(1)).thenReturn(Optional.of(user));
	    when(userRepository.save(any(User.class))).thenReturn(updatedUser);

	    // Act
	    User result = userService.updateUser(1, updatedUser);

	    // Assert
	    assertNotNull(result);
	    assertEquals("updatedName", result.getUsername());
//	    assertEquals("updatedPassword", result.getPassword());
	    assertEquals("updated@example.com", result.getEmail());
//	    assertEquals("ADMIN", result.getRole());

	    // Verify repository interactions
	    verify(userRepository, times(1)).findById(1);
	    verify(userRepository, times(1)).save(user);
	}

	@Test
	void testUpdateUser_NotFound() {
	    // Arrange
	    User updatedUser = new User();
	    updatedUser.setUsername("updatedName");
//	    updatedUser.setPassword("updatedPassword");
	    updatedUser.setEmail("updated@example.com");
//	    updatedUser.setRole("ADMIN");

	    when(userRepository.findById(1)).thenReturn(Optional.empty());

	    // Act
	    User result = userService.updateUser(1, updatedUser);

	    // Assert
	    assertEquals(null, result);

	    // Verify repository interactions
	    verify(userRepository, times(1)).findById(1);
	    verify(userRepository, times(0)).save(any(User.class)); // Save should not be called
	}
	
	@Test
	void testDeleteUser() {
	    // Act: Call the method to delete a user by ID
	    userService.deleteUser(1);

	    // Assert: Verify that the deleteById method is called exactly once
	    verify(userRepository, times(1)).deleteById(1);
	}
	
	@Test
	void testUserExists_ReturnsTrue() {
	    // Arrange: Mock the existsById method to return true
	    when(userRepository.existsById(1)).thenReturn(true);

	    // Act: Call the method to check if the user exists
	    boolean exists = userService.userExists(1);

	    // Assert: Verify the result is true
	    assertEquals(true, exists);

	    // Verify that the existsById method was called once
	    verify(userRepository, times(1)).existsById(1);
	}

	@Test
	void testUserExists_ReturnsFalse() {
	    // Arrange: Mock the existsById method to return false
	    when(userRepository.existsById(1)).thenReturn(false);

	    // Act: Call the method to check if the user exists
	    boolean exists = userService.userExists(1);

	    // Assert: Verify the result is false
	    assertEquals(false, exists);

	    // Verify that the existsById method was called once
	    verify(userRepository, times(1)).existsById(1);
	}





}
