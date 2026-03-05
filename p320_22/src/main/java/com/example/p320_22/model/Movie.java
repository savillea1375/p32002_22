package com.example.p320_22.model;

public class Movie {
	private int id;
	private String title;
	private Rating mpaaRating;
	private int lenInMin;
	private Contributor[] contributors;

	public Movie(int id, String title, Rating mpaaRating, int length, Contributor[] contributors) {
		this.id = id;
		this.title = title;
		this.mpaaRating = mpaaRating;
		this.lenInMin = length;
		this.contributors = contributors;
	}

	/** Getters */
	public int getId() { return this.id; }
	public String getTitle() { return this.title; }
	public Rating getRating() { return mpaaRating; }
	public int getLenInMin() { return lenInMin; }
	public Contributor[] getContributors() { return contributors; }
}
