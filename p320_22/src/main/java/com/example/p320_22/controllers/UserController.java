package com.example.p320_22.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import com.example.p320_22.model.User;
import com.example.p320_22.persistence.UserDAO;

/** Handles fetching/updating users and their information */
@RestController
@RequestMapping("/users")
public class UserController {
	UserDAO dao;

	public UserController() {
		this.dao = new UserDAO();
	}

	/** 
	 * Gets a user by the given email
	 * 
	 * @return The requested user if it exists and an HTTP status code of 200, else
	 * null and a status code of 404
	 */
	@GetMapping("/{email}")
	public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
		User user = dao.getByEmail(email);
		if (user == null) {
			return ResponseEntity.notFound().build();
		}
		
		return ResponseEntity.ok(user);
	}

	/** 
	 * Gets a user by the given username
	 * 
	 * @return The requested user if it exists and an HTTP status code of 200, else
	 * null and a status code of 404
	 */
	@GetMapping("/{username}")
	public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
		User user = dao.getByUsername(username);
		if (user == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(user);
	}
}
