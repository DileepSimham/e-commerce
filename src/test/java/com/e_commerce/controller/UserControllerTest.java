package com.e_commerce.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;  // Correct content import
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.e_commerce.entity.User;
import com.e_commerce.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;


class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        // Initialize a sample user
        user = new User();
        user.setId(1L);
        user.setUsername("johndoe");
        user.setPassword("password123");
        user.setEmail("johndoe@example.com");
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateUser() throws Exception {
        // Arrange: Mock the service to return the user object
        when(userService.createUser(any(User.class))).thenReturn(user);

        // Act & Assert: Send a POST request and verify the response
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.email").value("johndoe@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    // Helper method to convert an object to JSON string
    private static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            // Register JavaTimeModule for LocalDateTime support
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Test
     void testGetAllUsers() throws Exception {
        // Prepare mock data: a list of users
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("johndoe");
        user1.setPassword("password123");
        user1.setEmail("johndoe@example.com");
        user1.setRole("USER");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("janedoe");
        user2.setPassword("password456");
        user2.setEmail("janedoe@example.com");
        user2.setRole("USER");

        // Mock the service call
        when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        // Perform the GET request to /api/users
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/users"))
                .andExpect(status().isOk())  // Expecting 200 OK status
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))  // Expecting JSON response
                .andExpect(jsonPath("$[0].username").value(user1.getUsername()))  // Verify the first user's username
                .andExpect(jsonPath("$[1].username").value(user2.getUsername()))  // Verify the second user's username
                .andExpect(jsonPath("$[0].email").value(user1.getEmail()))  // Verify the first user's email
                .andExpect(jsonPath("$[1].email").value(user2.getEmail()));  // Verify the second user's email
    }
    
    @Test
    void testGetUserById() throws Exception {
        // Prepare mock data: a user with ID 1
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("johndoe");
        user1.setPassword("password123");
        user1.setEmail("johndoe@example.com");
        user1.setRole("USER");

        // Mock the service call to return the user when looking for ID 1
        when(userService.getUserById(1)).thenReturn(Optional.of(user1));

        // Perform the GET request to /api/users/{id} and verify the response
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/users/{id}", 1))
                .andExpect(status().isOk())  // Expecting 200 OK status
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))  // Expecting JSON response
                .andExpect(jsonPath("$.id").value(1))  // Verify the user's ID
                .andExpect(jsonPath("$.username").value("johndoe"))  // Verify the username
                .andExpect(jsonPath("$.email").value("johndoe@example.com"))  // Verify the email
                .andExpect(jsonPath("$.role").value("USER"));  // Verify the role
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        // Mock the service call to return an empty Optional (user not found)
        when(userService.getUserById(1)).thenReturn(Optional.empty());

        // Perform the GET request to /api/users/{id} and verify the response
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/users/{id}", 1))
                .andExpect(status().isNotFound());  // Expecting 404 NOT_FOUND status
    }
    
    @Test
    void testUpdateUser() throws Exception {
        // Prepare mock data: a user with ID 1
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("johndoe");
        existingUser.setPassword("password123");
        existingUser.setEmail("johndoe@example.com");
        existingUser.setRole("USER");

        // Prepare the updated user data
        User updatedUser = new User();
        updatedUser.setUsername("john_doe_updated");
        updatedUser.setPassword("newpassword123");
        updatedUser.setEmail("john_doe_updated@example.com");
        updatedUser.setRole("ADMIN");

        // Mock the service call to update the user
        when(userService.updateUser(1, updatedUser)).thenReturn(updatedUser);

        // Perform the PUT request to /api/users/{id} and verify the response
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/users/{id}", 1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(updatedUser)))
                .andExpect(status().isOk())  // Expecting 200 OK status
                .andExpect(jsonPath("$.username").value("john_doe_updated"))  // Verify the updated username
                .andExpect(jsonPath("$.email").value("john_doe_updated@example.com"))  // Verify the updated email
                .andExpect(jsonPath("$.role").value("ADMIN"));  // Verify the updated role
    }

    @Test
    void testUpdateUser_NotFound() throws Exception {
        // Prepare the updated user data
        User updatedUser = new User();
        updatedUser.setUsername("john_doe_updated");
        updatedUser.setPassword("newpassword123");
        updatedUser.setEmail("john_doe_updated@example.com");
        updatedUser.setRole("ADMIN");

        // Mock the service call to return null (user not found)
        when(userService.updateUser(1, updatedUser)).thenReturn(null);

        // Perform the PUT request to /api/users/{id} and verify the response
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/users/{id}", 1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(updatedUser)))
                .andExpect(status().isNotFound());  // Expecting 404 NOT_FOUND status
    }
    
    @Test
    void testDeleteUser() throws Exception {
        // Prepare the mock service to return true for user existence
        when(userService.userExists(1)).thenReturn(true);

        // Perform the DELETE request to /api/users/delete/{id} and verify the response
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/users/delete/{id}", 1))
                .andExpect(status().isNoContent());  // Expecting 204 NO_CONTENT status
    }

    @Test
    void testDeleteUser_NotFound() throws Exception {
        // Prepare the mock service to return false for user existence
        when(userService.userExists(1)).thenReturn(false);

        // Perform the DELETE request to /api/users/delete/{id} and verify the response
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/users/delete/{id}", 1))
                .andExpect(status().isNotFound());  // Expecting 404 NOT_FOUND status
    }



}
