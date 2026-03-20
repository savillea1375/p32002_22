package com.example.p320_22.model;

public class UserRating {
    private String username;
    private int movieId;
    private int rating;

    public UserRating(String username, int movieId, int rating) {
        this.username = username;
        this.movieId = movieId;
        this.rating = rating;
    }

    public String getUsername() { return username; }
    public int getMovieId() { return movieId; }
    public int getRating() { return rating; }

    public void setRating(int rating) { this.rating = rating; }
}
