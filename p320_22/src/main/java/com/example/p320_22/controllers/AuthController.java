package com.example.p320_22.controllers;

import java.sql.SQLException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.p320_22.model.User;
import com.example.p320_22.persistence.UserDAO;

import jakarta.servlet.http.HttpSession;

/**
 * Handles creating a user account, logging in, and logging out
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
	private UserDAO userDAO;

	public AuthController() {
		userDAO = new UserDAO();
	}

	/** 
	 * Signs up a user by supplying a username and password
	 * 
	 * @return The user if they were created and a status code of 201,
	 * else status code of 409
	 */
	@PostMapping("signup")
	public ResponseEntity<User> signup(@RequestBody User user, HttpSession session) {
		try {
			User createdUser = userDAO.createUser(user);

			session.setAttribute("username", user.getUsername());

			return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
		} catch (SQLException e) {
			if (e.getSQLState().equals("23505")) {
            	return ResponseEntity.status(HttpStatus.CONFLICT).build();
        	}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	/** 
	 * Logs in a user with their username and password
	 * 
	 * @return Status code of 200 if successful, else 
	 * status code of 401 otherwise
	 */
	@PostMapping("login")
	public ResponseEntity<User> login(@RequestBody User req, HttpSession session) {
		try {
			User user = userDAO.getByUsername(req.getUsername());

			if (user == null || !user.getPassword().equals(req.getPassword())) {
				return ResponseEntity.status(401).build();
			}

			session.setAttribute("username", user.getUsername());

			userDAO.updateLastAccess(user.getUsername());
			return ResponseEntity.status(HttpStatus.OK).body(user);
		} catch (SQLException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
