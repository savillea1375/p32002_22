package com.example.p320_22.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import com.example.p320_22.DatabaseConnection;
import com.example.p320_22.model.Collection;
import com.example.p320_22.model.User;
import com.example.p320_22.model.Movie;

public class MovieDAO {
    public List<Movie> getAllMovies() {
        try (Connection connection = DatabaseConnection.getConnection()) {

        } catch (SQLException e) {
			System.err.println("getByUsername");
			e.printStackTrace();
		}
        return null;
    }
}
