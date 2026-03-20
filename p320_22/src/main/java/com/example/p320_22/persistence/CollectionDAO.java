package com.example.p320_22.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.example.p320_22.DatabaseConnection;
import com.example.p320_22.model.Collection;
import com.example.p320_22.model.Movie;

public class CollectionDAO {
	private MovieDAO movieDAO;

	public CollectionDAO() {
		this.movieDAO = new MovieDAO();
	}

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

					Movie movie = movieDAO.getMovie(movieId);

					collection.addMovie(movie);
				}

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
		String query = "SELECT collectionid FROM collection WHERE username = ? ORDER BY name ASC";
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

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);

			statement.setInt(1, collectionID);
			statement.setString(2, username);

			ResultSet rs = statement.executeQuery();
			return rs.next();
		}
	}

	/**
	 * Renames a collection
	 * 
	 * @return true is successfully changed, false is collection not found
	 */
	public boolean rename(int collectionID, String newName) throws SQLException {
		String query = "UPDATE collection SET name = ? WHERE collectionid = ?";

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);

			statement.setString(1, newName);
			statement.setInt(2, collectionID);

			int res = statement.executeUpdate();
			
			if (res > 0) return true;
			else return false;
		}
	}
}
