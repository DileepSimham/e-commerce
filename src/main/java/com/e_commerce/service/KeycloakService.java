package com.e_commerce.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.e_commerce.entity.User;
import com.e_commerce.repository.UserRepository;

@Service
public class KeycloakService {

	@Value("${keycloak.auth-server-url}")
	private String keycloakServerUrl;

	@Value("${keycloak.realm}")
	private String keycloakRealm;

	@Value("${keycloak.client-id}")
	private String clientId;

	@Value("${keycloak.client-secret}")
	private String clientSecret;

	private final RestTemplate restTemplate = new RestTemplate();

	@Autowired
	private UserRepository userRepository;

	private String getAccessToken() {

		String tokenUrl = keycloakServerUrl + "/realms/" + keycloakRealm + "/protocol/openid-connect/token";

		HttpHeaders headers = new HttpHeaders();

		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		String body = "client_id=" + clientId + "&client_secret=" + clientSecret + "&grant_type=client_credentials";

		HttpEntity<String> entity = new HttpEntity<>(body, headers);

		ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, Map.class);

		if (response.getStatusCode() == HttpStatus.OK) {
			return (String) response.getBody().get("access_token");
		} else {
			throw new RuntimeException("Failed to fetch access token: " + response.getBody());
		}

	}

	// Fetch Users from Keycloak
	public List<Map<String, Object>> fetchUsers() {
	    String accessToken = getAccessToken();
	    String usersUrl = keycloakServerUrl + "/admin/realms/" + keycloakRealm + "/users";

	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Authorization", "Bearer " + accessToken);

	    HttpEntity<Void> entity = new HttpEntity<>(headers);

	    ResponseEntity<List> response = restTemplate.exchange(usersUrl, HttpMethod.GET, entity, List.class);

	    if (response.getStatusCode() == HttpStatus.OK) {
	        List<Map<String, Object>> users = response.getBody();
	        saveUsersToDatabase(users);
	        return users;
	    } else {
	        throw new RuntimeException("Failed to fetch users: " + response.getBody());
	    }
	}

	// Method to save users to the database
	private void saveUsersToDatabase(List<Map<String, Object>> users) {
	    for (Map<String, Object> userMap : users) {
	        // Check if the user already exists in the database
	        String username = (String) userMap.get("username");
	        Optional<User> existingUser = userRepository.findByUsername(username);

	        if (existingUser.isEmpty()) {
	            // User doesn't exist, create a new user entity
	            User userEntity = new User();

	            userEntity.setUsername(username);
	            userEntity.setFirstName((String) userMap.get("firstName"));
	            userEntity.setLastName((String) userMap.get("lastName"));
	            userEntity.setEmail((String) userMap.get("email"));
	            userEntity.setEmailVerified((Boolean) userMap.get("emailVerified"));
	            userEntity.setEnabled((Boolean) userMap.get("enabled"));
	            userEntity.setTotp((Boolean) userMap.get("totp"));
	            userEntity.setCreatedTimestamp((Long) userMap.get("createdTimestamp"));

	            // Save the user entity to the database
	            userRepository.save(userEntity);
	        }
	    }
	}


}
