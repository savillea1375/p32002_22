package com.example.p320_22.model;

/** A collection of movies owned by a user */
public class Collection {
	private int id;
	private String ownerUsername;
	private String collectionName;

	public Collection(int id, String ownerUsername, String collectionName) {
		this.id = id;
		this.ownerUsername = ownerUsername;
		this.collectionName = collectionName;
	}

	/** Getters */
	public int getId() { return this.id; }
	public String getOwner() { return this.ownerUsername; }
	public String getCollectionName() { return this.collectionName; }
}
