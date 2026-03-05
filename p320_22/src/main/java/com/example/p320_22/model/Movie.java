package com.example.p320_22.model;

public class Movie {
	private int id;
	private String title;
	private Rating mpaaRating;
	private int lenInMin;
	private int directorId;

	public Movie(int id, String title, Rating mpaaRating, int length, int directorId) {
		this.id = id;
		this.title = title;
		this.mpaaRating = mpaaRating;
		this.lenInMin = length;
		this.directorId = directorId;
	}

	/** Getters */
	public int getId() { return this.id; }
	public String getTitle() { return this.title; }
	public Rating getRating() { return this.mpaaRating; }
	public int getLenInMin() { return this.lenInMin; }
	public int getDirectorId() { return this.directorId; }
}
