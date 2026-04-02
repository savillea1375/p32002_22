package com.example.p320_22;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

/** Handles calling the SSHTunnel class to start the SSH tunnel to starbug server
 *  and contains method to get connection to DB on starbug
 */
public class DatabaseConnection {
	private static Dotenv dotenv = Dotenv.load();
	private static final String URL = dotenv.get("JDBC_URL");
	private static final String USERNAME = dotenv.get("DB_USERNAME");
	private static final String PASSWORD = dotenv.get("PASSWORD");

	// Start SSH tunneling once before attemping DB conneciton
	static {
		SSHTunnel.startTunnel();
	}

	/** Attempts to connect to DB, and returns the connection if successful */
	public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
