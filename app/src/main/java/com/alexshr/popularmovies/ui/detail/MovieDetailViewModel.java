package com.alexshr.popularmovies.ui.detail;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.alexshr.popularmovies.api.MoviesRepository;
import com.alexshr.popularmovies.data.Movie;
import com.alexshr.popularmovies.data.Review;
import com.alexshr.popularmovies.data.Video;

import java.util.List;

import javax.inject.Inject;

public class MovieDetailViewModel extends ViewModel {

    private MoviesRepository repository;

    private Movie movie;

    private MutableLiveData<List<Video>> videosData = new MutableLiveData<>();
    private MutableLiveData<List<Review>> reviewsData = new MutableLiveData<>();

    private MutableLiveData<Throwable> errorData = new MutableLiveData<>();
    private MutableLiveData<Boolean> progressData = new MutableLiveData<>();

    private boolean isVideosLoading;
    private boolean isReviewsLoading;

    @Inject
    public MovieDetailViewModel(MoviesRepository rep) {
        repository = rep;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
        movie.setFavorite(repository.isFavorite(movie.getId()));
    }

    public void switchFavorite() {
        if (movie.isFavorite()) {
            repository.deleteFromFavorites(movie.getId());
        } else {
            repository.addToFavorites(movie);
        }
        movie.setFavorite(!movie.isFavorite());
    }

    public void loadVideosAndReviews() {
        isVideosLoading = isReviewsLoading = true;

        repository.getMovieVideosObservable(movie.getId())
                .doFinally(() -> {
                    isVideosLoading = false;
                    progressData.postValue(isReviewsLoading);
                })
                .subscribe(videosData::postValue, errorData::postValue);

        repository.getMovieReviewsObservable(movie.getId())
                .doFinally(() -> {
                    isReviewsLoading = false;
                    progressData.postValue(isVideosLoading);
                })
                .subscribe(reviewsData::postValue, errorData::postValue);
    }

    public Movie getMovie() {
        return movie;
    }

    public MutableLiveData<List<Video>> getVideosData() {
        return videosData;
    }

    public MutableLiveData<List<Review>> getReviewsData() {
        return reviewsData;
    }

    public MutableLiveData<Throwable> getErrorData() {
        return errorData;
    }

    public MutableLiveData<Boolean> getProgressData() {
        return progressData;
    }
}
