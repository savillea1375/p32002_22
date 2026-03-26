package com.example.p320_22.model;

import java.util.ArrayList;

public class UserProfile {
	private String username;
	private int collectionCount;
	private int followerCount;
	private int followingCount;
	private ArrayList<Movie> topTenMovies;

	public UserProfile(String username, int collectionCount, int followerCount, int followingCount, ArrayList<Movie> topTenMovies) {
		this.username = username;
		this.collectionCount = collectionCount;
		this.followerCount = followerCount;
		this.followingCount = followingCount;
		this.topTenMovies = topTenMovies;
	}

	/** Getters */
	public String getUsername() { return this.username; }
	public int getCollectionCount() { return this.collectionCount; }
	public int getFollowerCount() { return this.followerCount; }
	public int getFollowingCount() { return this.followingCount; }
	public ArrayList<Movie> getTopTenMovies() { return this.topTenMovies; }
}
