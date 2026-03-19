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
		return null;
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

		if (collectionName == null || collectionName.isEmpty()) {
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

	/** 
	 * Creates a collection with the given name if the user is logged in
	 * 
	 * @return Status code of 200 if the collection was created, 401 if the 
	 * user is not logged, and 500 otherwise
	 */
	@PostMapping("delete")
	public ResponseEntity<String> deleteCollection(@RequestBody Map<String, Integer> body, HttpSession session) {
		String username = (String) session.getAttribute("username");

		if (username == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		int collectionID = body.get("collectionid");

		try {
			if (!collectiondDAO.isOwner(username, collectionID)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not own this collection");
			}

			collectiondDAO.delete(username, collectionID);
			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (SQLException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Make sure to include 'collectionid'");
		}
	}

	/**
	 * Adds a single movie to the collection, given the movie's id and the collection's id
	 * 
	 * @return Status code 200 if movie was added, 404 if movie does not exist,
	 * 409 if movie is already in collecton, 500 otherwise
	 */
	@PostMapping("/add")
	public ResponseEntity<String> addToCollection(@RequestBody Map<String, Integer> body, HttpSession session) {
		int collectionID = body.get("collectionid");
		int movieID = body.get("movieid");

		String username = (String) session.getAttribute("username");

		if (username == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		try {
			if (!collectiondDAO.isOwner(username, collectionID)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not own this collection");
			}

			collectiondDAO.addToCollection(collectionID, movieID);

			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (SQLException e) {
			if (e.getSQLState().equals("23505")) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body("Movie is already in collection");
			} else if (e.getSQLState().equals("02000")) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Movie does not exist");
			}

			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	/**
	 * Removes a single movie from a collection, given the movie's id and the collection's id
	 * 
	 * @return Status code 200 if movie was removed, 500 otherwise
	 */
	@PostMapping("/remove")
	public ResponseEntity<String> removeFromCollection(@RequestBody Map<String, Integer> body, HttpSession session) {
		int collectionID = body.get("collectionid");
		int movieID = body.get("movieid");

		String username = (String) session.getAttribute("username");

		if (username == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You do not own this collection");
		}
		
		try {
			if (!collectiondDAO.isOwner(username, collectionID)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}

			collectiondDAO.removeFromCollection(username, collectionID, movieID);

			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (SQLException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
