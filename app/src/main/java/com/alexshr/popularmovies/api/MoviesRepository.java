package com.alexshr.popularmovies.api;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.DataSource;

import com.alexshr.popularmovies.data.Movie;
import com.alexshr.popularmovies.data.MovieReviews;
import com.alexshr.popularmovies.data.MovieVideos;
import com.alexshr.popularmovies.data.MoviesPage;
import com.alexshr.popularmovies.data.Review;
import com.alexshr.popularmovies.data.Video;
import com.alexshr.popularmovies.db.AppDao;
import com.alexshr.popularmovies.rx.RestCallTransformer;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class MoviesRepository {
    private ApiService apiService;
    private AppDao dao;

    @Inject
    public MoviesRepository(ApiService apiService, AppDao dao) {
        this.apiService = apiService;
        this.dao = dao;
    }

    public Observable<MoviesPage> getMoviesObservable(String path, int page) {
        return apiService.getMoviesObservable(path, page)
                .compose(new RestCallTransformer<>());
    }

    public Observable<List<Video>> getMovieVideosObservable(int moviedId) {
        return apiService.getMovieVideosObservable(moviedId)
                .compose(new RestCallTransformer<>())
                .map(MovieVideos::getResults);
    }

    public Observable<List<Review>> getMovieReviewsObservable(int moviedId) {
        return apiService.getMovieReviewsObservable(moviedId)
                .compose(new RestCallTransformer<>())
                .map(MovieReviews::getResults);
    }

    public DataSource.Factory<Integer, Movie> getFavoritesDSFactory() {
        return dao.selectFavorites();
    }

    public void deleteFromFavorites(int movieId) {
        Completable.fromAction(() -> dao.deleteFromFavorites(movieId))
                .subscribeOn(Schedulers.computation())
                .subscribe(() -> {
                });
    }

    public void addToFavorites(Movie movie) {

        Completable.fromAction(() -> dao.addToFavorites(movie))
                .subscribeOn(Schedulers.computation())
                .subscribe(() -> {
                });

        // movie.setFavorite(true);
    }

    public LiveData<Boolean> getIsFavoriteData(int movieId) {
        return Transformations.map(dao.count(movieId), count -> count == 1);
    }
}
