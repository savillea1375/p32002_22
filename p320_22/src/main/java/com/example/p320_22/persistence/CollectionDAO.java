package com.example.p320_22.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.example.p320_22.DatabaseConnection;
import com.example.p320_22.model.Actor;
import com.example.p320_22.model.Collection;
import com.example.p320_22.model.Genre;
import com.example.p320_22.model.Movie;
import com.example.p320_22.model.Platform;
import com.example.p320_22.model.Producer;
import com.example.p320_22.model.Rating;

public class CollectionDAO {
	/**
	 * Fetches all the movies in a collection
	 * 
	 * @return Status code 200 if collection exists, 404 if it doesn't exist
	 */
	public Collection getCollection(int id) throws SQLException {
		String query = "SELECT * FROM collection WHERE collectionid = ?";

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setInt(1, id);

			ResultSet rs = statement.executeQuery();
			
			if (rs.next()) {
				int collectionID = rs.getInt("collectionid");
				String owner = rs.getString("username");
				String name = rs.getString("name");

				Collection collection = new Collection(collectionID, owner, name);

				String movieQuery = "SELECT m.* FROM movie AS m JOIN storesmovieincollection AS smc ON m.movieid = smc.movieid WHERE smc.collectionid = ?";
				PreparedStatement movieStatement = connection.prepareStatement(movieQuery);
				movieStatement.setInt(1, collectionID);

				ResultSet movieSet = movieStatement.executeQuery();

				while (movieSet.next()) {
					int movieId = movieSet.getInt("movieid");
					String title = movieSet.getString("title");
					Rating rating = Rating.fromString(movieSet.getString("mpaarating"));
					int directorID = movieSet.getInt("contributorid");
					int length = movieSet.getInt("length");
					ArrayList<Platform> platforms = new ArrayList<>();
					ArrayList<Genre> genres = new ArrayList<>();
					ArrayList<Producer> producers = new ArrayList<>();
					ArrayList<Actor> actors = new ArrayList<>();

					// TODO get platforms, genres, producers, and actors

					Movie movie = new Movie(movieId, title, rating, length, directorID, platforms, genres, producers, actors);

					collection.addMovie(movie);
				}

				System.out.println(collection.getMovies().size());
				return collection;
			} else {
				return null;
			}
		}
	}

	/**
	 * Fetches all the movies in a collection
	 * 
	 * @return Status code 200 if collection exists, 404 if it doesn't exist
	 */
	public ArrayList<Collection> getAllCollectionsFromUser(String username) throws SQLException {
		String query = "SELECT collectionid FROM collection WHERE username = ?";
		ArrayList<Collection> collections = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, username);

			ResultSet rs = statement.executeQuery();
			
			while (rs.next()) {
				int collectionID = rs.getInt("collectionid");
				
				Collection collection = getCollection(collectionID);
				collections.add(collection);
			}
		}

		return collections;
	}
	
	/**
	 * Creates a collection for a user
	 */
	public void create(String username, String collectionName) throws SQLException {
		String query = "INSERT INTO collection (username, name) VALUES (?, ?)";

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, username);
			statement.setString(2, collectionName);

			statement.executeUpdate();
		}
	}

	/**
	 * Deletes a collection for a user if are the owner of it
	 */
	public void delete(String username, int collectionID) throws SQLException {
		String query = "DELETE FROM collection WHERE collectionID = ? AND username = ?";

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setInt(1, collectionID);
			statement.setString(2, username);

			statement.executeUpdate();
		}
	}

	/**
	 * Adds a movie to the collection given the movie's id and collections id
	 */
	public void addToCollection(int collectionID, int movieID) throws SQLException {
		String query = "INSERT INTO storesmovieincollection (collectionid, movieid) VALUES (?, ?)";

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setInt(1, collectionID);
			statement.setInt(2, movieID);
			
			statement.executeUpdate();
		}
	}

	/**
	 * From a movie from the collection given the movie's id and collections id
	 */
	public void removeFromCollection(String owner, int collectionID, int movieID) throws SQLException {
		String query = "DELETE FROM storesmovieincollection WHERE collectionid = ? AND movieid = ?";

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setInt(1, collectionID);
			statement.setInt(2, movieID);
			
			statement.executeUpdate();
		}
	}

	/**
	 * Checks if a user is the owner of a collection
	 * 
	 * @return true is user is the owner of the collection, false otherwise
	 */
	public boolean isOwner(String username, int collectionID) throws SQLException {
		String query = "SELECT * FROM collection WHERE collectionid = ? AND username = ?";

		try (Connection connection = DatabaseConnection.getConnection();
			PreparedStatement stmt = connection.prepareStatement(query)) {

			stmt.setInt(1, collectionID);
			stmt.setString(2, username);

			ResultSet rs = stmt.executeQuery();
			return rs.next();
		}
	}
}
