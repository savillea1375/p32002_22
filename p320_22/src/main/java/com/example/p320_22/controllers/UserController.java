package com.example.p320_22.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	 * Gets a user by the given username
	 * 
	 * @return The requested user
	 */
	@GetMapping("/{username}")
	public User getUser(@PathVariable String username) {
		return dao.getByUsername(username);
	}
}
