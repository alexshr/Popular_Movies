package com.alexshr.popularmovies.ui;

import android.support.v4.app.FragmentManager;

import com.alexshr.popularmovies.R;
import com.alexshr.popularmovies.data.Movie;
import com.alexshr.popularmovies.ui.detail.MovieDetailFragment;
import com.alexshr.popularmovies.ui.list.MovieListFragment;

import javax.inject.Inject;

/**
 * Created by alexshr on 28.03.2018.
 */
public class NavigationController {
    private final int containerId;
    private final FragmentManager fragmentManager;

    @Inject
    public NavigationController(MainActivity mainActivity) {
        this.containerId = R.id.container;
        this.fragmentManager = mainActivity.getSupportFragmentManager();
    }

    public void navigateToList() {
        fragmentManager.beginTransaction()
                .replace(containerId, new MovieListFragment())
                .commit();
    }

    public void navigateToDetail(Movie movie) {
        fragmentManager.beginTransaction()
                .replace(containerId, MovieDetailFragment.create(movie))
                .addToBackStack(null)
                .commit();
    }

    public void backFromDetails() {
        fragmentManager.popBackStackImmediate();
    }
}
