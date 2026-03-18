package com.example.p320_22.model;

public class Genre {
	private int id;
	private String title;

	public Genre(int id, String title) {
		this.id = id;
		this.title = title;
	}

	/** Getters */
	public int getId() { return this.id; }
	public String getTitle() { return this.title; }
}
