package com.example.p320_22.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.example.p320_22.DatabaseConnection;
import com.example.p320_22.model.Collection;
import com.example.p320_22.model.User;

public class UserDAO {
	/** Finds a user by their username
	 * 
	 * @return The user with the matching username
	 */
	public User getByUsername(String username) {
		String query = "SELECT * FROM users WHERE username = ?";

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, username);

			ResultSet rs = statement.executeQuery();

			if (rs.next()) {
				ArrayList<Collection> userCollections = new ArrayList<>();
				// TODO: call get user collections query

				User user = new User(
					rs.getTimestamp("creation_date").toInstant(),
					rs.getString("email"),
					rs.getString("username"),
					rs.getString("password"),
					rs.getString("first_name"),
					rs.getString("last_name"),
					rs.getTimestamp("last_access_date").toInstant(),
					userCollections
				);

				return user;
			}
		} catch (SQLException e) {
			System.err.println("getByUsername");
			e.printStackTrace();
		}

		return null;
	}
}
