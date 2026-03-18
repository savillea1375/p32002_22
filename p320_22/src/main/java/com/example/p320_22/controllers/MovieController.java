package com.example.p320_22.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.p320_22.model.Movie;

@RestController
@RequestMapping("/movies")
public class MovieController {
	private List<Movie> movies = new ArrayList<>();

	@GetMapping
	public List<Movie> getAllMovies() {
		return movies;
	}

	@GetMapping("/{id}")
	public Movie getMovie(@PathVariable int id) {
		return null;
	}

	@PostMapping
	public Movie addMovie(@RequestBody Movie movie) {
		movies.add(movie);
		return movie;
	}
}
