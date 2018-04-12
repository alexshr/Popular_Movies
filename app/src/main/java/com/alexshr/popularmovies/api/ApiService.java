package com.alexshr.popularmovies.api;

import com.alexshr.popularmovies.data.MoviesPage;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by alexshr on 15.03.2018.
 */

public interface ApiService {

    @GET("movie/{path}")
    Observable<Response<MoviesPage>> getMoviesObservable(@Path("path") String path, @Query("page") int page);

}
