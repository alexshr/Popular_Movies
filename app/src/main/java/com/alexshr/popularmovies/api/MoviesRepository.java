package com.alexshr.popularmovies.api;

import com.alexshr.popularmovies.data.MoviesPage;
import com.alexshr.popularmovies.rx.RestCallTransformer;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;

/**
 * Created by alexshr on 20.03.2018.
 */
@Singleton
public class MoviesRepository {
    private ApiService mApiService;

    @Inject
    public MoviesRepository(ApiService apiService) {
        mApiService = apiService;
    }

    public Observable<MoviesPage> loadPage(String path, int page) {
        return mApiService.getMoviesObservable(path, page)
                .compose(new RestCallTransformer<>());
    }
}
