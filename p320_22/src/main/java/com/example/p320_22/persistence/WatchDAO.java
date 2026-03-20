package com.example.p320_22.persistence;

import java.sql.*;
import com.example.p320_22.DatabaseConnection;
import com.example.p320_22.model.Watch;

public class WatchDAO {

    /*
     * Records a timestamp (in UNIX format) when a user watches a movie.
     */
    public void addWatch(Watch watch) {
        String query = """
            INSERT INTO watchesmovie (username, movieId, timestamp)
            VALUES (?, ?, ?)
        """;

        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, watch.getUsername());
            statement.setInt(2, watch.getMovieId());
            statement.setLong(3, watch.getTimestamp());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("addWatch");
            e.printStackTrace();
        }
    }
}
