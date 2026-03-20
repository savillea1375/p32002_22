package com.example.p320_22.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import com.example.p320_22.DatabaseConnection;
import com.example.p320_22.model.*;

public class MovieDAO {
    
    //Get all movies in collection 
    public List<Movie> getAllMovies() {
        String query = "SELECT * FROM movie";
        List<Movie> movies = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            
            while(rs.next()) {
                ArrayList<Platform> platforms = new ArrayList<>();
                ArrayList<Genre> genres = new ArrayList<>();
                ArrayList<Producer> producers = new ArrayList<>();
                ArrayList<Actor> actors = new ArrayList<>();

                Movie movie = new Movie(
                    rs.getInt("movieid"),
                    rs.getString("title"),
                    Rating.valueOf(rs.getString("mpaarating")),
                    rs.getInt("length"),
                    rs.getInt("contributorid"),
                    platforms,
                    genres,
                    producers,
                    actors
                );
                movies.add(movie);
            }

            return movies;

        } catch (SQLException e) {
			System.err.println("getAllMovies");
			e.printStackTrace();
		}
        return null;
    }

    public Movie getMovie(int id) {
        String query = "SELECT * FROM movie WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, String.valueOf(id));
            ResultSet rs = statement.executeQuery();
            
            if(rs.next()) {
                ArrayList<Platform> platforms = new ArrayList<>();
                ArrayList<Genre> genres = new ArrayList<>();
                ArrayList<Producer> producers = new ArrayList<>();
                ArrayList<Actor> actors = new ArrayList<>();

                Movie movie = new Movie(
                    rs.getInt("movieid"),
                    rs.getString("title"),
                    Rating.valueOf(rs.getString("mpaarating")),
                    rs.getInt("length"),
                    rs.getInt("contributorid"),
                    platforms,
                    genres,
                    producers,
                    actors
                );
                
                return movie;
            }

        } catch (SQLException e) {
			System.err.println("getMovie");
			e.printStackTrace();
		}
        return null;
    }

	public void watchMovie(String username, int movieID) throws SQLException {
		String query = "INSERT INTO watchesmovie (username, movieid, watchtimestamp) VALUES (?, ?, ?)";

		try (Connection connection = DatabaseConnection.getConnection();
			PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setString(1, username);
			statement.setInt(2, movieID);
			statement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));

			statement.executeUpdate();
    	}
	}

	public void watchCollection(String username, int collectionID) throws SQLException {
		String selectQuery = "SELECT movieid FROM storesmovieincollection WHERE collectionid = ?";

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement selectStatement = connection.prepareStatement(selectQuery);

			selectStatement.setInt(1, collectionID);
			ResultSet rs = selectStatement.executeQuery();

			while (rs.next()) {
				int movieID = rs.getInt("movieid");
				
				watchMovie(username, movieID);
        	}
		}
	}
}
