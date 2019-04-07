package com.alexshr.popularmovies.db;

import com.alexshr.popularmovies.data.Movie;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Movie.class}, version = 1)
public abstract class AppDb extends RoomDatabase {
    public abstract AppDao moviesDao();
}
