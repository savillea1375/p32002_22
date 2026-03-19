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
	/** 
	 * Finds a user by their email
	 * 
	 * @return The user with the matching email
	 */
	public User getByEmail(String email) {
		String query = "SELECT * FROM users WHERE email = ?";

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, email);

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
			System.err.println("getByEmail");
			e.printStackTrace();
		}

		return null;
	}

	/** 
	 * Finds a user by their username
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

	/**
	 * Creates a user if they do not already exists
	 */
	public User createUser(User user) throws SQLException {
		String query = "INSERT INTO users (username, email, password, first_name, last_name) VALUES (?, ?, ?, ?, ?)";

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, user.getUsername());
			statement.setString(2, user.getEmail());
			statement.setString(3, user.getPassword());
			statement.setString(4, user.getFirstName());
			statement.setString(5, user.getLastName());

			statement.executeUpdate();

			updateLastAccess(user.getUsername());
		}

		return user;
	}

	/** 
	 * Given a username, updates the last access time of that user to the current time
	 */
	public void updateLastAccess(String username) throws SQLException {
		String query = "UPDATE users SET last_access_date = CURRENT_TIMESTAMP WHERE username = ?";

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, username);
			statement.executeUpdate();
		}
	}

	/**
	 * Follows the specified user if they exist in the database
	 */
	public void follow(String follower, String followingEmail) throws SQLException {
		String query = "INSERT INTO follows (follower_username, following_username) SELECT ?, username FROM users WHERE email = ?";

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, follower);
			statement.setString(2, followingEmail);
			statement.executeUpdate();
		}
	}

	/**
	 * Unfollows the specified user if they exist in the database and the
	 * current user is following the other
	 */
	public boolean unfollow(String follower, String followingEmail) throws SQLException {
		String query = "DELETE FROM follows WHERE following_username = (SELECT username FROM users WHERE email = ?) AND follower_username = ?";

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, followingEmail);
			statement.setString(2, follower);
			int res = statement.executeUpdate();

			if (res > 0) return true;
			else return false;
		}
	}
}
