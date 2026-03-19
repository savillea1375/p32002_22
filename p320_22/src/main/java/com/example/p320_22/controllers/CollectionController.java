package com.example.p320_22.controllers;

import java.sql.SQLException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.p320_22.model.Collection;
import com.example.p320_22.persistence.CollectionDAO;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/collections")
public class CollectionController {
	CollectionDAO collectiondDAO;

	public CollectionController() {
		collectiondDAO = new CollectionDAO();
	}

	/**
	 * Gets a collection with all its movies and returns it if it exists
	 * 
	 * @return The collection or null if it doesn't exist
	 */
	@GetMapping("/{id}")
	public ResponseEntity<Collection> getCollection(@PathVariable int id) {

	}

	/** 
	 * Creates a collection with the given name if the user is logged in
	 * 
	 * @return Status code of 200 if the collection was created, 401 if the 
	 * user is not logged, and 500 otherwise
	 */
	@PostMapping("")
	public ResponseEntity<String> createCollection(@RequestBody Map<String, String> body, HttpSession session) {
		String username = (String) session.getAttribute("username");

		if (username == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		String collectionName = body.get("name");

		if (collectionName == "" || collectionName.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Must provide name of collection as key 'name'");
		}

		try {
			collectiondDAO.create(username, collectionName);
			return ResponseEntity.status(HttpStatus.CREATED).build();
		} catch (SQLException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
