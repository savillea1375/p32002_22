package com.example.p320_22.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import com.example.p320_22.DatabaseConnection;
import com.example.p320_22.model.*;

public class MovieDAO {
    
    //Get all movies in collection 
    public List<Movie> getAllMovies() {
        String query = "SELECT * FROM movie";
        List<Movie> movies = new ArrayList<>();

        ArrayList<Platform> platforms = new ArrayList<>();
        ArrayList<Genre> genres = new ArrayList<>();
        ArrayList<Producer> producers = new ArrayList<>();
        ArrayList<Actor> actors = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            
            while(rs.next()) {
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
			System.err.println("getByUsername");
			e.printStackTrace();
		}
        return null;
    }

    public Movie getMovie() {

        return null;
    }
}
