package com.example.p320_22.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.p320_22.DatabaseConnection;
import com.example.p320_22.model.Collection;

public class CollectionDAO {
	/**
	 * Fetches all the movies in a collection
	 * 
	 * @return Status code 200 if collection exists, 404 if it doesn't exist
	 */
	public Collection getCollection(int id) {
		return null;
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
		String query = "SELECT 1 FROM collection WHERE collectionid = ? AND username = ?";

		try (Connection connection = DatabaseConnection.getConnection();
			PreparedStatement stmt = connection.prepareStatement(query)) {

			stmt.setInt(1, collectionID);
			stmt.setString(2, username);

			ResultSet rs = stmt.executeQuery();
			return rs.next();
		}
	}
}
