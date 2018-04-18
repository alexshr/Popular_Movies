package com.alexshr.popularmovies.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;

import com.alexshr.popularmovies.data.Movie;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import timber.log.Timber;

/**
 * Created by alexshr on 14.04.2018.
 */
@Singleton
public class MoviesClient {

    Context context;

    @Inject
    public MoviesClient(Context context) {
        this.context = context;
    }

    //https://stackoverflow.com/a/10723771/2886841 auto closed cursor
    //https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
    public List<Movie> queryFavorites() {
        ArrayList<Movie> movieList = new ArrayList<>();
        try (Cursor cursor = context.getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                MoviesContract.MoviesEntry._ID);) {

            int movieIdIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_ID);
            int titleIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_TITLE);
            int overviewIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_OVERVIEW);
            int posterIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH);
            int backdropIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_BACKDROP_PATH);
            int releaseDateIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE);
            int voteIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE);

            while (cursor.moveToNext()) {
                Movie movie = new Movie();
                movie.setId(cursor.getInt(movieIdIndex));
                movie.setTitle(cursor.getString(titleIndex));
                movie.setOverview(cursor.getString(overviewIndex));
                movie.setPosterPath(cursor.getString(posterIndex));
                movie.setBackdropPath(cursor.getString(backdropIndex));
                movie.setReleaseDate(cursor.getString(releaseDateIndex));
                movie.setVoteAverage(cursor.getDouble(voteIndex));
                movieList.add(movie);
            }
        }
        return movieList;
    }

    public Observable<List<Movie>> getFavoritesObservable() {
        return Observable.just(queryFavorites());
    }


    public boolean isFavorite(int movieId) {
        try (Cursor cursor = context.getContentResolver()
                .query(MoviesContract.MoviesEntry.CONTENT_URI, null, "movie_id=?", new String[]{String.valueOf(movieId)}, null);) {
            return cursor.getCount() == 1;
        }
    }

    public void insert(Movie movie) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_ID, movie.getId());
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_TITLE, movie.getTitle());
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_OVERVIEW, movie.getOverview());
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());

        Uri uri = context.getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI, contentValues);
        Timber.d("added movie=%s; result uri=%s",movie,uri);

        if (uri == null) throw new SQLException("failed to insert Movie: " + movie);
    }

    public int delete(int movieId) {
        Uri uri = MoviesContract.MoviesEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(String.valueOf(movieId)).build();
        int delCount = context.getContentResolver().delete(uri, null, null);
        if (delCount == 1) {
            Timber.d("deleted movieId=%d",movieId);
            return delCount;
        } else {
            throw new SQLException("deletion error; movieId=%d, delCount=%d");
        }
    }
}
