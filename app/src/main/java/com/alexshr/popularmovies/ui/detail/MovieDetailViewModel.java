package com.alexshr.popularmovies.ui.detail;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.alexshr.popularmovies.data.Movie;

import javax.inject.Inject;

/**
 * Created by alexshr on 21.03.2018.
 */

public class MovieDetailViewModel extends ViewModel {

    private MutableLiveData<Movie> movieData = new MutableLiveData<>();


    @Inject
    public MovieDetailViewModel() {
    }

    public void setMovie(Movie movie) {
        movieData.postValue(movie);
    }

    public MutableLiveData<Movie> getMovieData() {
        return movieData;
    }
}
