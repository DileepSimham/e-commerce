package com.e_commerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.e_commerce.entity.User;

public interface UserRepository extends JpaRepository<User,Integer> {
	
	  Optional<User> findByUsername(String username);

}
