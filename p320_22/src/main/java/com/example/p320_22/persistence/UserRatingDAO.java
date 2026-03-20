package com.example.p320_22.persistence;

import java.sql.*;
import com.example.p320_22.DatabaseConnection;
import com.example.p320_22.model.UserRating;

public class UserRatingDAO {

    /*
     * Adds a new rating or updates an existing rating for a user and movie.
    */
    public void addOrUpdateRating(UserRating rating) {
        String query = """
            INSERT INTO ratesmovie (username, movieId, rating)
            VALUES (?, ?, ?)
            ON CONFLICT (username, movieId)
            DO UPDATE SET rating = EXCLUDED.rating
        """;

        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, rating.getUsername());
            statement.setInt(2, rating.getMovieId());
            statement.setInt(3, rating.getRating());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("addOrUpdateRating");
            e.printStackTrace();
        }
    }
}
