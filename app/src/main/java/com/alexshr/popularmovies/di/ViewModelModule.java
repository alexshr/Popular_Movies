package com.alexshr.popularmovies.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.alexshr.popularmovies.ui.detail.MovieDetailViewModel;
import com.alexshr.popularmovies.ui.list.MovieListViewModel;
import com.alexshr.popularmovies.viewmodel.ViewModelFactory;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

//creating Map<Class<? extends ViewModel>, Provider<ViewModel>> (multibinding)
//to insert to ViewModelFactory as constructor parameter (to single factory for many ViewModels)
@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MovieListViewModel.class)
    abstract ViewModel bindMovieListViewModel(MovieListViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MovieDetailViewModel.class)
    abstract ViewModel bindMovieDetailViewModel(MovieDetailViewModel viewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}
