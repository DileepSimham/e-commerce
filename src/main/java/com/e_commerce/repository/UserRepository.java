package com.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.e_commerce.entity.User;

public interface UserRepository extends JpaRepository<User,Integer> {

}
