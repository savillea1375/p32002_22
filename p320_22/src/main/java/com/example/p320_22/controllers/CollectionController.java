package com.example.p320_22.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	public ResponseEntity<Collection> getCollectionByID(@PathVariable int id) {
		try {
			Collection collection = collectiondDAO.getCollection(id);

			return ResponseEntity.status(HttpStatus.OK).body(collection);
		} catch (SQLException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	/**
	 * Gets all of the collections owned by a specified user
	 * 
	 * @return The collection or null if it doesn't exist
	 */
	@GetMapping("/user/{username}")
	public ResponseEntity<ArrayList<Collection>> getCollectionsByUsername(@PathVariable String username) {
		try {
			ArrayList<Collection> collections = collectiondDAO.getAllCollectionsFromUser(username);

			return ResponseEntity.status(HttpStatus.OK).body(collections);
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
	@PostMapping("")
	public ResponseEntity<?> createCollection(@RequestBody Map<String, String> body, HttpSession session) {
		String username = (String) session.getAttribute("username");

		if (username == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Must be logged in to make a collection");
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
	@DeleteMapping("delete")
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
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Must be logged in to remove a movie from a collection");
		}
		
		try {
			if (!collectiondDAO.isOwner(username, collectionID)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not own this collection");
			}

			collectiondDAO.removeFromCollection(username, collectionID, movieID);

			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (SQLException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	/**
	 * Renames a collection
	 * 
	 * @return 200 if successful, 404 if collection doesn't exist, 403 if user
	 * doesn't own the collection, 401 if the user is not logged in, and 500 elsewise
	 */
	@PutMapping("/{collectionID}/rename")
	public ResponseEntity<?> rename(@PathVariable int collectionID, @RequestBody Map<String, String> body, HttpSession session) {
		String username = (String) session.getAttribute("username");

		if (username == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Must be logged in to remove a movie from a collection");
		}

		String newName = body.get("newName");
		if (newName == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Must provide new name as key 'newName'");
		}

		try {
			if (!collectiondDAO.isOwner(username, collectionID)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not own this collection");
			}

			if (collectiondDAO.rename(collectionID, newName)) {
				return ResponseEntity.status(HttpStatus.OK).build();
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
