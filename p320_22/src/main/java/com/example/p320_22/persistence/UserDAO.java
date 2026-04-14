package com.example.p320_22.persistence;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.security.MessageDigest;

import com.example.p320_22.DatabaseConnection;
import com.example.p320_22.model.Collection;
import com.example.p320_22.model.User;

public class UserDAO {
	private CollectionDAO collectionDAO;

	public UserDAO() {
		collectionDAO = new CollectionDAO();
	}

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
				String username = rs.getString("username");
				ArrayList<Collection> userCollections = collectionDAO.getAllCollectionsFromUser(username);

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
				ArrayList<Collection> userCollections = collectionDAO.getAllCollectionsFromUser(username);

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
		String query = "INSERT INTO users (username, email, password, first_name, last_name, salt) VALUES (?, ?, ?, ?, ?, ?)";

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);

			String password = null;
			byte[] salt = generateSalt();
			String saltString = byteToString(salt);
			try{
				password = byteToString(getSHA((user.getPassword()+saltString)));
			} catch (NoSuchAlgorithmException e){
				System.out.print("Error trying to sign up");
				e.printStackTrace();
			}
			statement.setString(1, user.getUsername());
			statement.setString(2, user.getEmail());
			statement.setString(3, password);
			statement.setString(4, user.getFirstName());
			statement.setString(5, user.getLastName());
			statement.setString(6, saltString);
			statement.executeUpdate();

			updateLastAccess(user.getUsername());
		} 

		return user;
	}



	//this generates a random salt and returns it
	private byte[] generateSalt(){
		SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16]; 
        random.nextBytes(salt);
        return salt;
	}



		//this hashes the password+salt with SHA256 
	public static byte[] getSHA(String input) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

	

		//converts bytes to string
	public String byteToString(byte[] hash)
    {
        BigInteger num = new BigInteger(1, hash);
        StringBuilder hString = new StringBuilder(num.toString(16));

        while (hString.length() < 64)
        {
            hString.insert(0, '0');
        }

        return hString.toString();
    }


	//this gets the salt value in the database
	public String getSaltByUsername(String username){

		String query = "SELECT salt FROM users WHERE username = ?";

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, username);
			ResultSet rs = statement.executeQuery();
			if(rs.next()){
				return rs.getString("salt");
			}
		} catch (SQLException e){
			System.err.println("getSaltByUsername");
			e.printStackTrace();

		}
		return null;
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
	public boolean follow(String follower, String followingEmail) throws SQLException {
		String query = "INSERT INTO follows (follower_username, following_username) SELECT ?, username FROM users WHERE email = ?";

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, follower);
			statement.setString(2, followingEmail);
			int res = statement.executeUpdate();

			return res > 0;
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
