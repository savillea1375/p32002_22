package com.example.p320_22.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.p320_22.model.User;
import com.example.p320_22.model.UserProfile;
import com.example.p320_22.persistence.UserDAO;

import jakarta.servlet.http.HttpSession;

/** Handles fetching/updating users and their information */
@RestController
@RequestMapping("/users")
public class UserController {
	UserDAO userDAO;

	public UserController() {
		this.userDAO = new UserDAO();
	}



	/** 
	 * Gets a user by the given email
	 * 
	 * @return The requested user if it exists and an HTTP status code of 200, else
	 * null and a status code of 404
	 */
	@GetMapping("/email/{email}")
	public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
		User user = userDAO.getByEmail(email);
		if (user == null) {
			return ResponseEntity.notFound().build();
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(user);
	}

	/** 
	 * Gets a user by the given username
	 * 
	 * @return The requested user if it exists and an HTTP status code of 200, else
	 * null and a status code of 404
	 */
	@GetMapping("/username/{username}")
	public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
		User user = userDAO.getByUsername(username);
		if (user == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(user);
	}

	/**
	 * Follows a user by their email if a current user is logged in
	 *
	 * @return 
	 */
	@PostMapping("/follow/{target}")
	public ResponseEntity<String> follow(@PathVariable String target, HttpSession session) {
		String currentUser = (String) session.getAttribute("username");

		if (currentUser == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		try {
			boolean res = userDAO.follow(currentUser, target);

			if (!res) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
		} catch (SQLException e) {
			if (e.getSQLState().equals("23505")) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body("You are already following this user");
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

		return ResponseEntity.status(HttpStatus.OK).build();
	}

	/**
	 * Unfollows a user if a current user is logged in and is following the other
	 *
	 * @return 
	 */
	@PostMapping("/unfollow/{target}")
	public ResponseEntity<String> unfollow(@PathVariable String target, HttpSession session) {
		String currentUser = (String) session.getAttribute("username");

		if (currentUser == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		try {
			boolean res = userDAO.unfollow(currentUser, target);

			if (!res) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
			}
		} catch (SQLException e) {
			if (e.getSQLState().equals("23503")) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
