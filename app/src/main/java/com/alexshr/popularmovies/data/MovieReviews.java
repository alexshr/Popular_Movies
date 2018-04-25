
package com.alexshr.popularmovies.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MovieReviews {

    @SerializedName("id")
    private Integer movieId;
    @SerializedName("results")
    private List<Review> results = new ArrayList<>();

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public List<Review> getResults() {
        return results;
    }

    public void setResults(List<Review> results) {
        this.results = results;
    }
}
