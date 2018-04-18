package com.alexshr.popularmovies.api;

import com.alexshr.popularmovies.data.Movie;
import com.alexshr.popularmovies.data.MoviesPage;
import com.alexshr.popularmovies.provider.MoviesClient;
import com.alexshr.popularmovies.rx.RestCallTransformer;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;

/**
 * Created by alexshr on 20.03.2018.
 */
@Singleton
public class MoviesRepository {
    private ApiService apiService;
    private MoviesClient favoritesClient;

    @Inject
    public MoviesRepository(ApiService apiService, MoviesClient favoritesClient) {
        this.apiService = apiService;
        this.favoritesClient = favoritesClient;

    }

    public Observable<MoviesPage> getApiObservable(String path, int page) {
        return apiService.getMoviesObservable(path, page)
                .compose(new RestCallTransformer<>());
    }

    public Observable<List<Movie>> getFavoritesObservable() {
        return favoritesClient.getFavoritesObservable();
    }

    public void deleteFromFavorites(int movieId) {
        favoritesClient.delete(movieId);
    }

    public void addToFavorites(Movie movie) {
        favoritesClient.insert(movie);
    }

    public boolean isFavorite(int movieId) {
        return favoritesClient.isFavorite(movieId);
    }
}
