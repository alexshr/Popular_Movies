package com.alexshr.popularmovies.ui.list;

import com.alexshr.popularmovies.api.MoviesRepository;
import com.alexshr.popularmovies.data.Movie;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class MovieListDataSourceFactory extends DataSource.Factory<Integer, Movie> {
    private final String path;
    private final CompositeDisposable compositeDisposable;
    private MoviesRepository repo;
    private MutableLiveData<MovieListDataSource> dataSourceLiveData= new MutableLiveData<>();

    public MovieListDataSourceFactory(MoviesRepository repo, String path, CompositeDisposable compositeDisposable) {
        this.repo=repo;
        this.path=path;
        this.compositeDisposable=compositeDisposable;
        Timber.d("path=%s",path);
    }

    @Override
    public DataSource<Integer, Movie> create() {
        MovieListDataSource dataSource = new MovieListDataSource(repo,path,compositeDisposable);
       dataSourceLiveData.postValue(dataSource);
       return dataSource;
    }

    public MutableLiveData<MovieListDataSource> getDataSourceLiveData() {
        return dataSourceLiveData;
    }
}
