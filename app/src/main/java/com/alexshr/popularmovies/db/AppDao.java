package com.alexshr.popularmovies.db;

import com.alexshr.popularmovies.data.Movie;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

/**
 * Created by alexshr on 05.04.2019.
 */

@Dao
public interface AppDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addToFavorites(Movie movie);

    @Query("DELETE FROM movies WHERE id=:id")
    void deleteFromFavorites(int id);

    @Query("SELECT * FROM movies")
    DataSource.Factory<Integer, Movie> selectFavorites();

    @Query("SELECT count(*) FROM movies WHERE id=:id")
    LiveData<Integer> count(int id);
}
