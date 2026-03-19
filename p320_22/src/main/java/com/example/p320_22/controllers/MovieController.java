package com.example.p320_22.controllers;

import java.util.ArrayList;
import java.util.List;

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
import com.example.p320_22.persistence.UserDAO;

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
}
