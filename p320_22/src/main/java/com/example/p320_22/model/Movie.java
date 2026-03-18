package com.example.p320_22.model;

import java.util.ArrayList;

public class Movie {
	private int id;
	private String title;
	private Rating mpaaRating;
	private int lenInMin;
	private int directorId;
	private ArrayList<Platform> platforms;
	private ArrayList<Genre> genres;
	private ArrayList<Producer> producers;
	private ArrayList<Actor> actors;

	public Movie(int id, String title, Rating mpaaRating, int length, int directorId, ArrayList<Platform> platforms, 
					ArrayList<Genre> genres, ArrayList<Producer> producers, ArrayList<Actor> actors) {
		this.id = id;
		this.title = title;
		this.mpaaRating = mpaaRating;
		this.lenInMin = length;
		this.directorId = directorId;
		this.platforms = platforms;
		this.genres = genres;
		this.producers = producers;
		this.actors = actors;
	}

	/** Getters */
	public int getId() { return this.id; }
	public String getTitle() { return this.title; }
	public Rating getRating() { return this.mpaaRating; }
	public int getLenInMin() { return this.lenInMin; }
	public int getDirectorId() { return this.directorId; }
	public int getPlatformId() { return this.platformId; }
	public ArrayList<Genre> getGenres() { return this.genres; }
	public ArrayList<Producer> getProducers() { return this.producers; }
	public ArrayList<Actor> getActors() { return this.actors; }
}
