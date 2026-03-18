package com.example.p320_22.controllers;

import org.apache.catalina.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles creating a user account, logging in, and logging out
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
	@PostMapping("signup")
	public User signup() {
		return null;
	}
}
