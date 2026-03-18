package com.example.p320_22.controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import io.github.cdimascio.dotenv.Dotenv;

public class ConnectTest {
	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();

		String URI = "jdbc:postgresql://127.0.0.1:5433/p32002_22";
		String user = dotenv.get("USERNAME");
		String password = dotenv.get("PASSWORD");

		try {
			final Properties props = new Properties();
			props.setProperty("user", user);
			props.setProperty("password", password);
			Connection connection = DriverManager.getConnection(URI, props);
			System.out.println("Connected to database!");
            connection.close();
		} catch (SQLException e) {
			System.out.println("Error connecting to database");
			e.printStackTrace();
		}
	}
}
