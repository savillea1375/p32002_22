package com.example.p320_22.controllers;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.p320_22.model.Movie;
import com.example.p320_22.persistence.MovieDAO;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/movies")
public class MovieController {
	MovieDAO movieDAO;

	public MovieController() {
		this.movieDAO = new MovieDAO();
	}

	@GetMapping
	public ResponseEntity<List<Movie>> getAllMovies() {
		List<Movie> movies = movieDAO.getAllMovies();
		if (movies == null) {
			return ResponseEntity.notFound().build();
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(movies);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Movie> getMovie(@PathVariable int id) {
		Movie movie = movieDAO.getMovie(id);
		if (movie == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.status(HttpStatus.OK).body(movie);
	}

	@PostMapping
	public Movie addMovie(@RequestBody Movie movie) {
		return movie;
	}

	@PostMapping("watch/{id}")
	public ResponseEntity<String> watchMovie(@PathVariable int id, HttpSession session) {
		String username = (String) session.getAttribute("username");

		if (username == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Must be signed in to watch a movie");

		try {
			movieDAO.watchMovie(username, id);

			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (SQLException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("rate/{id}")
	public ResponseEntity<String> rateMovie(@PathVariable int id, @RequestBody Map<String, Integer> body, HttpSession session) {
		String username = (String) session.getAttribute("username");
		int rating = body.get("rating");

		if (username == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Must be signed in to rate a movie");

		if (rating < 0 || rating > 10) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Rating must be a number 1-10");

		try {
			movieDAO.rateMovie(username, id, rating);

			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (SQLException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
