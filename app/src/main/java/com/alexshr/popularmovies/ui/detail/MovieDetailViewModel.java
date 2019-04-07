package com.alexshr.popularmovies.ui.detail;

import com.alexshr.popularmovies.api.MoviesRepository;
import com.alexshr.popularmovies.data.Movie;
import com.alexshr.popularmovies.data.Review;
import com.alexshr.popularmovies.data.Video;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import lombok.Getter;
import timber.log.Timber;

public class MovieDetailViewModel extends ViewModel {

    private MoviesRepository repository;
    @Getter
    private Movie movie;
    @Getter
    private MutableLiveData<List<Video>> videosData = new MutableLiveData<>();
    @Getter
    private MutableLiveData<List<Review>> reviewsData = new MutableLiveData<>();

    @Getter
    private MutableLiveData<Throwable> errorData = new MutableLiveData<>();
    @Getter
    private MutableLiveData<Boolean> progressData = new MutableLiveData<>();

    private boolean isVideosLoading;
    private boolean isReviewsLoading;

    @Inject
    public MovieDetailViewModel(MoviesRepository rep) {
        repository = rep;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    @Override
    protected void onCleared() {
        Timber.d("onClear");
        videosData = null;
        reviewsData = null;
        errorData = null;
        progressData = null;
        movie = null;
        repository = null;
    }

    public void switchFavorite() {
        if (movie.isFavorite()) {
            repository.deleteFromFavorites(movie.getId());
        } else {
            repository.addToFavorites(movie);
        }
    }

    public LiveData<Boolean> getIsFavoriteData() {
        return repository.getIsFavoriteData(movie.getId());
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
}
