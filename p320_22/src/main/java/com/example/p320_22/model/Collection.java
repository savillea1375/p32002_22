package com.example.p320_22.model;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;

/** A collection of movies owned by a user */
public class Collection {
	private int id;
	private String ownerUsername;
	private String collectionName;
	private ArrayList<Movie> movies; // List of movie ids in the collection
	private int movieCount;
	@JsonIgnore private int runtime;
	private String formattedRuntime; // HH:MM

	public Collection(int id, String ownerUsername, String collectionName) {
		this.id = id;
		this.ownerUsername = ownerUsername;
		this.collectionName = collectionName;
		this.movies = new ArrayList<>();
		this.movieCount = movies.size();
		this.runtime = 0;
		for (Movie movie : movies) {
			runtime += movie.getLenInMin();
		}
		this.formattedRuntime = formatRuntime();
	}

	/** Getters */
	public int getId() { return this.id; }
	public String getOwner() { return this.ownerUsername; }
	public String getCollectionName() { return this.collectionName; }
	public ArrayList<Movie> getMovies() { return this.movies; }
	public int getMovieCount() { return this.movieCount; }
	public int getRuntime() { return this.runtime; }
	public String getFormattedRuntime() { return this.formattedRuntime; }

	/** Add a movie to the collection */
	public void addMovie(Movie m) {
		this.movies.add(m);
		this.runtime += m.getLenInMin();
		this.movieCount++;
		this.formattedRuntime = formatRuntime();
	}

	/** Remove a movie from the collection */
	public void removeMovie(Movie m) {
		this.movies.remove(m);
		this.runtime -= m.getLenInMin();
		this.movieCount--;
		this.formattedRuntime = formatRuntime();
	}

	private String formatRuntime() {
		int hours = runtime / 60;
		int minutes = runtime % 60;
		String formatted = String.format("%02d:%02d", hours, minutes);

		return formatted;
	}
}
