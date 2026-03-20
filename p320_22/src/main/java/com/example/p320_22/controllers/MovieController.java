package com.example.p320_22.controllers;

import java.sql.SQLException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.p320_22.model.Movie;
import com.example.p320_22.persistence.MovieDAO;

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
		try {
			Movie movie = movieDAO.getMovie(id);
			System.out.println(movie);
			if (movie == null) {
				return ResponseEntity.notFound().build();
			}
			return ResponseEntity.status(HttpStatus.OK).body(movie);
		} catch (SQLException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/search")
	public ResponseEntity<List<Movie>> searchMovies(
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String castMember,
			@RequestParam(required = false) String genre,
			@RequestParam(required = false) String sortBy,
			@RequestParam(defaultValue = "asc") String sortOrder) {
		try {
			List<Movie> movies = movieDAO.searchMovies(name, castMember, genre, sortBy, sortOrder);
			
			return ResponseEntity.status(HttpStatus.OK).body(movies);
		} catch (SQLException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
