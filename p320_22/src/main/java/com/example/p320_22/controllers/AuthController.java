package com.example.p320_22.controllers;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.p320_22.model.User;
import com.example.p320_22.persistence.UserDAO;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
	@PostMapping("/signup")
	public ResponseEntity<User> signup(@RequestBody User user, HttpSession session) {
		try {
			userDAO.createUser(user);


			session.setAttribute("username", user.getUsername());

			return ResponseEntity.status(HttpStatus.CREATED).build();
		} catch (SQLException e) {
			e.printStackTrace();
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
	@PostMapping("/login")
	public ResponseEntity<User> login(@RequestBody User req, HttpSession session) {
		try {
			User user = userDAO.getByUsername(req.getUsername());

			//this finds the salt value of the username in the database
			//and it hashes it with the password given by the user
			//and checks if the hash given matches what's in the database password column
			String saltPassword = null;
			String saltString = userDAO.getSaltByUsername(req.getUsername());
			try{
				saltPassword = userDAO.byteToString(userDAO.getSHA((req.getPassword()+saltString)));

			} catch (NoSuchAlgorithmException e){
				System.out.println("error trying to login");
				e.printStackTrace();
			}


			if (user == null || !user.getPassword().equals(saltPassword)) {
				return ResponseEntity.status(401).build();
			}

			session.setAttribute("username", user.getUsername());

			userDAO.updateLastAccess(user.getUsername());
			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (SQLException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	/**
	 * Logs a user out if they are logged in
	 * 
	 * @return Status code of 200 if successfully logged out, 
	 */
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpSession session, HttpServletResponse res) {
		try {
			session.invalidate();

			Cookie cookie = new Cookie("JSESSIONID", null);
			cookie.setPath("/");
			cookie.setMaxAge(0);

    		res.addCookie(cookie);

			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (IllegalStateException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}



}
