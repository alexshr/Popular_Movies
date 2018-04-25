package com.alexshr.popularmovies.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieVideos {

    @SerializedName("id")
    private int movieId;
    @SerializedName("results")
    private List<Video> results;

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public List<Video> getResults() {
        return results;
    }

    public void setResults(List<Video> results) {
        this.results = results;
    }
}
