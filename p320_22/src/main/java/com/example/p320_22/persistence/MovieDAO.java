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

	// Get all movies in collection
	//Get all movies in collection
	public List<Movie> getAllMovies() {
		String query = "SELECT * FROM movie";
		List<Movie> movies = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);
			ResultSet rs = statement.executeQuery();

			while(rs.next()) {
				int movieID = rs.getInt("movieid");

				Movie movie = getMovie(movieID);

				movies.add(movie);
			}

			return movies;

		} catch (SQLException e) {
			System.err.println("getAllMovies");
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<Platform> getPlatforms(int id) throws SQLException {
		String query = "SELECT rp.* FROM releaseplatform rp JOIN releasedon ro ON ro.platformid = rp.platformid WHERE ro.movieid = ?";

		ArrayList<Platform> platforms = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				platforms.add(new Platform(rs.getInt("platformid"), rs.getString("type")));
			}
		}

		return platforms;
	}

	private ArrayList<Genre> getGenres(int movieId) throws SQLException {
		ArrayList<Genre> genres = new ArrayList<>();
		String query = "SELECT g.* FROM genre g JOIN isgenre ig ON ig.genreid = g.genreid WHERE ig.movieid = ?";

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setInt(1, movieId);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				genres.add(new Genre(rs.getInt("genreid"), rs.getString("genretitle")));
			}
		}

		return genres;
	}

	private ArrayList<Producer> getProducers(int movieId) throws SQLException {
		ArrayList<Producer> producers = new ArrayList<>();
		String query = "SELECT c.* FROM contributor c JOIN produces p ON p.contributorid = c.contributorid WHERE p.movieid = ?";

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setInt(1, movieId);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				producers.add(new Producer(rs.getInt("contributorid"), rs.getString("name")));
			}
		}
		return producers;
	}

	private ArrayList<Actor> getActors(int movieId) throws SQLException {
		ArrayList<Actor> actors = new ArrayList<>();
		String query = "SELECT c.* FROM contributor c JOIN actsin a ON a.contributorid = c.contributorid WHERE a.movieid = ?";

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setInt(1, movieId);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				actors.add(new Actor(rs.getInt("contributorid"), rs.getString("name")));
			}
		}
		return actors;
	}

	public Movie getMovie(int id) throws SQLException {
		String query = "SELECT * FROM movie WHERE movieid = ?";

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			ResultSet rs = statement.executeQuery();

			if(rs.next()) {
				ArrayList<Platform> platforms = getPlatforms(id);
				ArrayList<Genre> genres = getGenres(id);
				ArrayList<Producer> producers = getProducers(id);
				ArrayList<Actor> actors = getActors(id);

				Movie movie = new Movie(
						rs.getInt("movieid"),
						rs.getString("title"),
						Rating.fromString(rs.getString("mpaarating")),
						rs.getInt("length"),
						rs.getInt("contributorid"),
						platforms,
						genres,
						producers,
						actors
				);

				return movie;
			}
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
		String query = "SELECT movieid FROM storesmovieincollection WHERE collectionid = ?";

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);

			statement.setInt(1, collectionID);
			ResultSet rs = statement.executeQuery();

			while (rs.next()) {
				int movieID = rs.getInt("movieid");

				watchMovie(username, movieID);
			}
		}
	}

	public void rateMovie(String username, int movieID, int rating) throws SQLException {
		String query = "INSERT INTO ratesmovie (username, movieid, rating) VALUES (?, ?, ?) ON CONFLICT (username, movieid) DO UPDATE SET rating = EXCLUDED.rating";

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);

			statement.setString(1, username);
			statement.setInt(2, movieID);
			statement.setInt(3, rating);
			statement.executeUpdate();
		}
	}

	public ArrayList<Movie> getMostPopularLast90Days() throws SQLException {
		ArrayList<Movie> movies = new ArrayList<>();

		String query = "SELECT * FROM movie WHERE movieid IN " +
				"(SELECT movieid FROM watchesmovie WHERE " +
				"watchtimestamp >= NOW() - INTERVAL '90 days' GROUP BY movieid ORDER BY COUNT(movieid) DESC LIMIT 20)";

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);
			ResultSet rs = statement.executeQuery();

			while (rs.next()) {
				Movie movie = getMovie(rs.getInt("movieid"));
				movies.add(movie);
			}
		}

		return movies;
	}

	public ArrayList<Movie> getMostPopularFollowing(String username) throws SQLException {
		ArrayList<Movie> movies = new ArrayList();

		//Get top 20 movies with highest watch count among the users that are being followed sort DESC
		String query = "SELECT movieid FROM " +
		"(SELECT wm.movieid FROM watchesmovie wm " +
		"JOIN follows f ON wm.username = f.following_username " +
		"WHERE f.follower_username = ? " +
		"GROUP BY wm.movieid ORDER BY COUNT(*) DESC LIMIT 20)" +
		"AS popular_following";

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, username);
			ResultSet rs = statement.executeQuery();

			while (rs.next()) {
				Movie movie = getMovie(rs.getInt("movieid"));
				movies.add(movie);
			}
		}
		return movies;
	}

	public ArrayList<Movie> getNewReleasesOfMonth() throws SQLException {
		ArrayList<Movie> movies = new ArrayList<>();

		String query = "SELECT m.*, COUNT(w.movieid) AS watch_count " +
				"FROM movie m " +
				"LEFT JOIN watchesmovie w ON m.movieid = w.movieid " +
				"GROUP BY m.movieid " +
				"ORDER BY watch_count DESC " +
				"LIMIT 5";

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);
			ResultSet rs = statement.executeQuery();

			while (rs.next()) {
				Movie movie = getMovie(rs.getInt("movieid"));
				movies.add(movie);
			}
		}

		return movies;
	}
	public List<Movie> searchMovies(String movieName, String castMember, String genre, String sortBy, String sortOrder) throws SQLException {
		List<Movie> movies = new ArrayList<>();

		StringBuilder query = new StringBuilder(
				"SELECT m.* FROM movie m "
		);

		if (castMember != null && !castMember.isEmpty()) {
			query.append("JOIN actsin a ON m.movieid = a.movieid JOIN contributor c ON a.contributorid = c.contributorid ");
		}

		if (genre != null && !genre.isEmpty()) {
			query.append("JOIN isgenre ig ON m.movieid = ig.movieid JOIN genre g ON ig.genreid = g.genreid ");
		}

		boolean isFirst = true;

		if (movieName != null && !movieName.isEmpty()) {
			if (!isFirst) query.append("AND ");
			else {
				isFirst = false;
				query.append("WHERE ");
			}
			query.append("LOWER(m.title) = LOWER(?) ");
		}

		if (castMember != null && !castMember.isEmpty()) {
			if (!isFirst) query.append("AND ");
			else {
				isFirst = false;
				query.append("WHERE ");
			}
			query.append("LOWER(c.name) = LOWER(?) ");
		}

		if (genre != null && !genre.isEmpty()) {
			if (!isFirst) query.append("AND ");
			else {
				isFirst = false;
				query.append("WHERE ");
			}
			query.append("LOWER(g.genretitle) = LOWER(?) ");
		}

		query.append("ORDER BY ");

		if (sortBy != null && !sortBy.isEmpty()) {
			switch (sortBy.toLowerCase()) {
				case "title" -> query.append("m.title ");
				case "genre" -> query.append("g.genretitle ");
				default -> query.append("m.title ");
			}
		} else {
			query.append("m.title ");
		}

		if (sortOrder != null && sortOrder.equalsIgnoreCase("desc")) {
			query.append("DESC ");
		} else {
			query.append("ASC ");
		}

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query.toString());

			int i = 1;

			if (movieName != null && !movieName.isEmpty()) {
				statement.setString(i++, movieName);
			}
			if (castMember != null && !castMember.isEmpty()) {
				statement.setString(i++, castMember);
			}
			if (genre != null && !genre.isEmpty()) {
				statement.setString(i++, genre);
			}

			ResultSet rs = statement.executeQuery();

			while (rs.next()) {
				movies.add(getMovie(rs.getInt("movieid")));
			}
		}

		return movies;
	}

	public Map<Integer, Double> getAllUserRatings(String username) {
		Map<Integer, Double> ratings = new HashMap<>();

		String query = "SELECT movieid, rating FROM ratesmovie WHERE username = ?";

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, username);
			ResultSet rs = statement.executeQuery();

			while (rs.next()) {
				ratings.put(rs.getInt("movieid"), rs.getDouble("rating"));
			}

		} catch (SQLException e) {
			System.out.println("getAllUserRatings");
			e.printStackTrace();
		}

		return ratings;
	}

	public Map<String, Map<Integer, Double>> getUserRatings(Set<String> usernames){
		Map<String, Map<Integer, Double>> result = new HashMap<>();

		if(usernames.isEmpty()){
			return result;
		}

		StringBuilder query = new StringBuilder(
				"SELECT username, movieid, rating FROM ratesmovie WHERE username IN ("
		);

		query.append("?,".repeat(usernames.size()));
		query.setLength(query.length() - 1);
		query.append(")");

		try(Connection connection = DatabaseConnection.getConnection()){
			PreparedStatement statement = connection.prepareStatement(query.toString());

			int i = 1;
			for(String user : usernames){
				statement.setString(i++, user);
			}

			ResultSet rs = statement.executeQuery();

			while(rs.next()){
				String user = rs.getString("username");
				int movieId = rs.getInt("movieid");
				double rating = rs.getDouble("rating");

				result.putIfAbsent(user, new HashMap<>());
				result.get(user).put(movieId, rating);
			}

		} catch(SQLException e){
			System.out.println("getUserRatings");
			e.printStackTrace();
		}

		return result;
	}

	public Map<String, Integer> getUserFavGenres(String username){
		Map<String, Integer> map = new HashMap<>();

		String query = """
		SELECT g.genretitle, COUNT(*) as counter
		FROM ratesmovie r
		JOIN isgenre ig ON r.movieid = ig.movieid
		JOIN genre g ON ig.genreid = g.genreid
		WHERE r.username = ? AND r.rating >= 7
		GROUP BY g.genretitle
	""";

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, username);
			ResultSet rs = statement.executeQuery();

			while(rs.next()){
				map.put(rs.getString("genretitle"), rs.getInt("counter"));
			}

		} catch (SQLException e){
			e.printStackTrace();
		}

		return map;
	}

	public Map<String, Integer> getUserFavProducers(String username){
		Map<String, Integer> map = new HashMap<>();

		String query = """
		SELECT c.name, COUNT(*) as counter
		FROM ratesmovie r
		JOIN produces p ON r.movieid = p.movieid
		JOIN contributor c ON p.contributorid = c.contributorid
		WHERE r.username = ? AND r.rating >= 7
		GROUP BY c.name
	""";

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, username);
			ResultSet rs = statement.executeQuery();

			while(rs.next()){
				map.put(rs.getString("name"), rs.getInt("counter"));
			}

		} catch (SQLException e){
			e.printStackTrace();
		}

		return map;
	}

	public Map<String, Integer> getUserFavActors(String username){
		Map<String, Integer> map = new HashMap<>();

		String query = """
		SELECT c.name, COUNT(*) as counter
		FROM ratesmovie r
		JOIN actsin a ON r.movieid = a.movieid
		JOIN contributor c ON a.contributorid = c.contributorid
		WHERE r.username = ? AND r.rating >= 7
		GROUP BY c.name
	""";

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, username);
			ResultSet rs = statement.executeQuery();

			while(rs.next()){
				map.put(rs.getString("name"), rs.getInt("counter"));
			}

		} catch (SQLException e){
			e.printStackTrace();
		}

		return map;
	}

	public Map<Integer, Integer> getUserFavDirectors(String username){
		Map<Integer, Integer> map = new HashMap<>();

		String query = """
		SELECT m.contributorid AS director_id, COUNT(*) as counter
		FROM ratesmovie r
		JOIN movie m ON r.movieid = m.movieid
		WHERE r.username = ? AND r.rating >= 7
		GROUP BY m.contributorid
	""";

		try (Connection connection = DatabaseConnection.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, username);
			ResultSet rs = statement.executeQuery();

			while(rs.next()){
				map.put(rs.getInt("director_id"), rs.getInt("counter"));
			}

		} catch (SQLException e){
			e.printStackTrace();
		}

		return map;
	}

	public List<Movie> getMovieDetailsBatch(List<Integer> movieIds){
		if(movieIds == null || movieIds.isEmpty()){
			return new ArrayList<>();
		}

		List<Movie> movies = new ArrayList<>();

		String query = "SELECT * FROM movie WHERE movieid = ANY (?)";

		try(Connection connection = DatabaseConnection.getConnection()){
			java.sql.Array sqlArray = connection.createArrayOf("INTEGER", movieIds.toArray());

			PreparedStatement statement = connection.prepareStatement(query);
			statement.setArray(1, sqlArray);

			ResultSet rs = statement.executeQuery();

			while (rs.next()) {
				movies.add(new Movie(
						rs.getInt("movieid"),
						rs.getString("title"),
						Rating.fromString(rs.getString("mpaarating")),
						rs.getInt("length"),
						rs.getInt("contributorid"),
						new ArrayList<>(),
						new ArrayList<>(),
						new ArrayList<>(),
						new ArrayList<>()
				));
			}

		} catch(SQLException e){
			e.printStackTrace();
		}

		return movies;
	}

	public List<Integer> getCandidateMovieIDs(String username) {

		List<Integer> ids = new ArrayList<>();

		String query =
				"SELECT movieid FROM movie " +
						"WHERE movieid NOT IN (" +
						"   SELECT movieid FROM ratesmovie WHERE username = ?" +
						") LIMIT 2000";

		try (Connection connection = DatabaseConnection.getConnection();
			 PreparedStatement stmt = connection.prepareStatement(query)) {

			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				ids.add(rs.getInt("movieid"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ids;
	}

	public Map<Integer, List<String>> getGenresForMovies(List<Integer> movieIds){
		Map<Integer, List<String>> map = new HashMap<>();
		if (movieIds.isEmpty()) return map;

		String query = """
			SELECT ig.movieid, g.genretitle
			FROM isgenre ig
			JOIN genre g ON ig.genreid = g.genreid
			WHERE ig.movieid = ANY (?)
		""";

		try(Connection connection = DatabaseConnection.getConnection()){

			java.sql.Array sqlArray = connection.createArrayOf("INTEGER", movieIds.toArray());
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setArray(1, sqlArray);

			ResultSet rs = stmt.executeQuery();

			while(rs.next()){
				int id = rs.getInt("movieid");
				String genre = rs.getString("genretitle");

				map.putIfAbsent(id, new ArrayList<>());
				map.get(id).add(genre);
			}

		} catch(SQLException e){
			e.printStackTrace();
		}

		return map;
	}

	public Map<Integer, List<String>> getActorsForMovies(List<Integer> movieIds){
		Map<Integer, List<String>> map = new HashMap<>();
		if (movieIds.isEmpty()) return map;

		String query = """
			SELECT a.movieid, c.name
			FROM actsin a
			JOIN contributor c ON a.contributorid = c.contributorid
			WHERE a.movieid = ANY (?)
		""";

		try(Connection connection = DatabaseConnection.getConnection()){
			java.sql.Array sqlArray = connection.createArrayOf("INTEGER", movieIds.toArray());
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setArray(1, sqlArray);

			ResultSet rs = stmt.executeQuery();

			while(rs.next()){
				int id = rs.getInt("movieid");
				String actor = rs.getString("name");

				map.putIfAbsent(id, new ArrayList<>());
				map.get(id).add(actor);
			}
		} catch(SQLException e){
			e.printStackTrace();
		}

		return map;
	}

	public Map<Integer, List<String>> getProducersForMovies(List<Integer> movieIds){
		Map<Integer, List<String>> map = new HashMap<>();
		if (movieIds.isEmpty()) return map;

		String query = """
			SELECT p.movieid, c.name
			FROM produces p
			JOIN contributor c ON p.contributorid = c.contributorid
			WHERE p.movieid = ANY (?)
		""";

		try(Connection connection = DatabaseConnection.getConnection()){
			java.sql.Array sqlArray = connection.createArrayOf("INTEGER", movieIds.toArray());
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setArray(1, sqlArray);

			ResultSet rs = statement.executeQuery();

			while(rs.next()){
				int id = rs.getInt("movieid");
				String producer = rs.getString("name");

				map.putIfAbsent(id, new ArrayList<>());
				map.get(id).add(producer);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return map;
	}

	public Map<Integer, Integer> getAllMovieDirectors(){
		Map<Integer, Integer> map = new HashMap<>();

		String query = "SELECT movieid, contributorid FROM movie";

		try(Connection connection = DatabaseConnection.getConnection();
			 PreparedStatement statement = connection.prepareStatement(query);
			 ResultSet rs = statement.executeQuery()) {

			while(rs.next()){
				map.put(rs.getInt("movieid"), rs.getInt("contributorid"));
			}
		} catch(SQLException e){
			e.printStackTrace();
		}

		return map;
	}
}