package com.example.p320_22.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.p320_22.model.Watch;
import com.example.p320_22.persistence.UserRatingDAO;
import com.example.p320_22.persistence.WatchDAO;
import com.example.p320_22.model.UserRating;

@RestController
@RequestMapping("/user-movies")
public class UserWatchRateController {

    private UserRatingDAO ratingDAO = new UserRatingDAO();
    private WatchDAO watchDAO = new WatchDAO();

    /*
     * Rates a movie for the current user.
     * @return
     */
    @PostMapping("/rate")
    public ResponseEntity<String> rateMovie(@RequestBody UserRating rating) {
        ratingDAO.addOrUpdateRating(rating);
        if (rating == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /*
     * Records a timestamp when a user watches a movie.
     * @return
     */
    @PostMapping("/watch")
    public ResponseEntity<String> watchMovie(@RequestBody Watch watch) {
        watchDAO.addWatch(watch);
        if (watch == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
