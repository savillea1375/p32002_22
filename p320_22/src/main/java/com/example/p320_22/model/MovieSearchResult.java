package com.example.p320_22.model;

import java.time.LocalDate;
import java.util.List;

public class MovieSearchResult {
	private int movieId;
	private String movieName;
	private List<String> castMembers;
	private String director;
	private int length;
	private Rating mpaaRating;
	private Double userRating;
	private String studio;
	private String genre;
	private LocalDate releaseDate;
	private Integer releaseYear;

	public MovieSearchResult(int movieId, String movieName, List<String> castMembers, String director, int length,
			Rating mpaaRating, Double userRating, String studio, String genre, LocalDate releaseDate, Integer releaseYear) {
		this.movieId = movieId;
		this.movieName = movieName;
		this.castMembers = castMembers;
		this.director = director;
		this.length = length;
		this.mpaaRating = mpaaRating;
		this.userRating = userRating;
		this.studio = studio;
		this.genre = genre;
		this.releaseDate = releaseDate;
		this.releaseYear = releaseYear;
	}

	public int getMovieId() {
		return movieId;
	}

	public String getMovieName() {
		return movieName;
	}

	public List<String> getCastMembers() {
		return castMembers;
	}

	public String getDirector() {
		return director;
	}

	public int getLength() {
		return length;
	}

	public Rating getMpaaRating() {
		return mpaaRating;
	}

	public Double getUserRating() {
		return userRating;
	}

	public String getStudio() {
		return studio;
	}

	public String getGenre() {
		return genre;
	}

	public LocalDate getReleaseDate() {
		return releaseDate;
	}

	public Integer getReleaseYear() {
		return releaseYear;
	}
}