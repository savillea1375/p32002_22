package com.example.p320_22.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.example.p320_22.DatabaseConnection;
import com.example.p320_22.model.Collection;

public class CollectionDAO {
	/**
	 * Fetches all the movies in a collection
	 * 
	 * @return Status code 200 if collection exists, 404 if it doesn't exist
	 */
	// public Collection getCollection(int id) {
		
	// }
	
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
}
