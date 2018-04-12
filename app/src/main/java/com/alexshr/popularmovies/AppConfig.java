package com.alexshr.popularmovies;

/**
 * Created by alexshr on 28.03.2018.
 */

public interface AppConfig {

    String API_KEY_NAME="api_key";
    String API_KEY_VALUE="b3465885c51333d19c9cab79c7980218";

    String BASE_URL = "http://api.themoviedb.org/3/";

    String BASE_POSTER_URL = "http://image.tmdb.org/t/p/";
    String POSTER_SIZE_W185 = "w185";
    String POSTER_SIZE_W342 = "w342";
    String POPULAR_PATH = "popular";
    String TOP_RATED_PATH = "top_rated";
    int RETRY_COUNT = 3;
    long RETRY_INITIAL_DELAY = 100;

    int MAX_CONNECTION_TIMEOUT = 5000;
    int MAX_READ_TIMEOUT = 5000;
    int MAX_WRITE_TIMEOUT = 5000;

    int CACHE_SIZE = 10 * 1024 * 1024;//10 mb
    int CACHE_OFFLINE_MAX_STALE = 60 * 60 * 24 * 28; // tolerate 4-weeks stale for offline

}
