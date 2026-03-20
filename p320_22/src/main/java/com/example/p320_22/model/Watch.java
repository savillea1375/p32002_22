package com.example.p320_22.model;
import java.time.Instant;



public class Watch {
    private String username;
    private int movieId;
    private long timestamp;

    public Watch(String username, int movieId, long timestamp) {
        this.username = username;
        this.movieId = movieId;
        this.timestamp = Instant.now().getEpochSecond();;
    }

    public String getUsername() { return username; }
    public int getMovieId() { return movieId; }
    public long getTimestamp() { return timestamp; }
}
