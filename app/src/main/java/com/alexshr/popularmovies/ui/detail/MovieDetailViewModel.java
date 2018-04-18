package com.alexshr.popularmovies.ui.detail;

import android.arch.lifecycle.ViewModel;

import com.alexshr.popularmovies.api.MoviesRepository;
import com.alexshr.popularmovies.data.Movie;

import javax.inject.Inject;

/**
 * Created by alexshr on 21.03.2018.
 */

public class MovieDetailViewModel extends ViewModel {

    private MoviesRepository repository;

    //private MutableLiveData<Movie> movieData = new MutableLiveData<>();
    private Movie movie;
    //private MutableLiveData<Boolean> isFavoriteData = new MutableLiveData<>();
    //private MutableLiveData<String> messageData = new MutableLiveData<>();



    @Inject
    public MovieDetailViewModel(MoviesRepository rep) {
        repository = rep;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
        movie.setFavorite(repository.isFavorite(movie.getId()));
        //movieData.setValue(movie);
    }

    public void switchFavorite() {
        if (movie.isFavorite()) {
            repository.deleteFromFavorites(movie.getId());
        } else {
            repository.addToFavorites(movie);
        }
        movie.setFavorite(!movie.isFavorite());
    }

    /*public MutableLiveData<Movie> getMovieData() {
        return movieData;
    }*/

    public Movie getMovie() {
        return movie;
    }


}
