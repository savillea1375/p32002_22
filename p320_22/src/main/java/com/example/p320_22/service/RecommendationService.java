package com.example.p320_22.service;

import com.example.p320_22.model.Movie;
import com.example.p320_22.persistence.MovieDAO;
import com.example.p320_22.persistence.UserDAO;

import java.util.*;

public class RecommendationService {
    private final MovieDAO movieDAO;
    private final UserDAO userDAO;
    private static final double GENRE_WEIGHT = 3.0;
    private static final double ACTOR_WEIGHT = 2.0;
    private static final double DIRECTOR_WEIGHT = 2.0;
    private static final double PRODUCER_WEIGHT = 1.0;
    private static final double USER_RATIO = 0.7;
    private static final double SIMILAR_USER_RATIO = 0.3;

    public RecommendationService(){
        this.movieDAO = new MovieDAO();
        this.userDAO = new UserDAO();
    }

    public List<Movie> recommendMovies(String username){
        List<Integer> unwatched = movieDAO.getUnwatchedMovieIDs(username);
        if (unwatched.isEmpty()){
            return Collections.emptyList();
        }

        Map<String, Integer> genreFavs = movieDAO.getUserFavGenres(username);
        Map<String, Integer> actorFavs = movieDAO.getUserFavActors(username);
        Map<String, Integer> producerFavs = movieDAO.getUserFavProducers(username);
        Map<Integer, Integer> directorFavs = movieDAO.getUserFavDirectors(username);
        Map<Integer, Double> userRatings = movieDAO.getUserRatings(username);

        Map<String, Double> similarUsers = userDAO.getSimilarUsers(username);
        Map<String, Map<Integer, Double>> similarRatings = similarUsers.isEmpty() ? new HashMap<>() : movieDAO.getMultipleUsersRatings(similarUsers.keySet());

        Map<Integer, List<String>> genres = movieDAO.getGenresForMovies(unwatched);
        Map<Integer, List<String>> actors = movieDAO.getActorsForMovies(unwatched);
        Map<Integer, List<String>> producers = movieDAO.getProducersForMovies(unwatched);
        Map<Integer, Integer> directors = movieDAO.getAllMovieDirectors();

        Map<Integer, Double> userScores = new HashMap<>();
        Map<Integer, Double> similarUsersScores = new HashMap<>();

        for(int id : unwatched){
            if(userRatings.containsKey(id)){
                continue;
            }

            double userScore = 0;

            for(String g : genres.getOrDefault(id, List.of())){
                userScore += genreFavs.getOrDefault(g, 0) * GENRE_WEIGHT;
            }

            for(String a : actors.getOrDefault(id, List.of())){
                userScore += actorFavs.getOrDefault(a, 0) * ACTOR_WEIGHT;
            }

            for(String p : producers.getOrDefault(id, List.of())){
                userScore += producerFavs.getOrDefault(p, 0) * PRODUCER_WEIGHT;
            }

            int directorID = directors.getOrDefault(id, -1);
            userScore += directorFavs.getOrDefault(directorID, 0) * DIRECTOR_WEIGHT;

            double similarScore = calculateSimilarUserScore(id, similarUsers, similarRatings);

            if(userScore > 0 || similarScore > 0){
                userScores.put(id, userScore);
                similarUsersScores.put(id, similarScore);
            }
        }

        double maxUserScore = userScores.values().stream().mapToDouble(value -> value).max().orElse(1.0);
        double maxSimilarScore = similarUsersScores.values().stream().mapToDouble(value -> value).max().orElse(1.0);

        Map<Integer, Double> scores = new HashMap<>();
        for(int id : userScores.keySet()){
            double normalizedUserScore = userScores.getOrDefault(id, 0.0) / maxUserScore;
            double normalizedSimilarScore = similarUsersScores.getOrDefault(id, 0.0) / maxSimilarScore;

            double finalScore = (USER_RATIO * normalizedUserScore) + (SIMILAR_USER_RATIO * normalizedSimilarScore);
            scores.put(id, finalScore);
        }

        List<Integer> top = scores.entrySet()
                .stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(20)
                .map(Map.Entry::getKey)
                .toList();

        return movieDAO.getMovieDetails(top);
    }

    private double calculateSimilarUserScore(int movieID, Map<String, Double> similarUsers, Map<String, Map<Integer, Double>> ratings){
        double weightedSum = 0;
        double totalWeight = 0;

        for(String user : similarUsers.keySet()){

            Map<Integer, Double> userRatings = ratings.get(user);
            if (userRatings == null){
                continue;
            }

            Double rating = userRatings.get(movieID);
            if (rating == null){
                continue;
            }

            double weight = similarUsers.get(user);
            weightedSum += weight * rating;
            totalWeight += weight;
        }

        return totalWeight == 0 ? 0 : weightedSum / totalWeight;
    }
}