package com.example.p320_22.model;

import java.util.ArrayList;

/** A collection of movies owned by a user */
public class MovieCol {
	private int id;
	private String ownerUsername;
	private String collectionName;
	private ArrayList<Movie> movies; //List of movie ids in the collection

	public MovieCol(int id, String ownerUsername, String collectionName) {
		this.id = id;
		this.ownerUsername = ownerUsername;
		this.collectionName = collectionName;
		this.movies = new ArrayList<>();
	}

	/** Getters */
	public int getId() { return this.id; }
	public String getOwner() { return this.ownerUsername; }
	public String getCollectionName() { return this.collectionName; }
	public ArrayList<Movie> getMovies() { return this.movies; }

	/** Add a movie to the collection */
	public void addMovie(Movie m) {
		this.movies.add(m);
	}

	/** Remove a movie from the collection */
	public void removeMovie(Movie m) {
		this.movies.remove(m);
	}
}
