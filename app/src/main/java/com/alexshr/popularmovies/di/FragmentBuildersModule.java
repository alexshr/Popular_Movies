package com.alexshr.popularmovies.di;

import com.alexshr.popularmovies.ui.detail.MovieDetailFragment;
import com.alexshr.popularmovies.ui.list.MovieListFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract MovieListFragment contributeMovieListFragment();

   @ContributesAndroidInjector
    abstract MovieDetailFragment contributeMovieDetailFragment();
}
