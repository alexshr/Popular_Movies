package com.alexshr.popularmovies.ui.list;

import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import com.alexshr.popularmovies.api.MoviesRepository;
import com.alexshr.popularmovies.data.Movie;
import com.alexshr.popularmovies.data.MoviesPage;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class MovieListDataSource extends PageKeyedDataSource<Integer, Movie> {

    private static final int START_PAGE = 1;
    private final String path;
    private final MoviesRepository repo;
    private final CompositeDisposable compositeDisposable;
    private MutableLiveData<Boolean> progressData = new MutableLiveData<>();
    private MutableLiveData<Throwable> errorData = new MutableLiveData<>();

    @Inject
    public MovieListDataSource(MoviesRepository repo, String path, CompositeDisposable compositeDisposable) {
        this.repo = repo;
        this.path = path;
        this.compositeDisposable = compositeDisposable;
        Timber.d("path=%s",path);
    }

    private Observable<MoviesPage> getPageObservable(String path, int page) {
        return repo.getMoviesObservable(path, page)
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnError(errorData::postValue)
                .doOnSubscribe(compositeDisposable::add)
                .doOnSubscribe(disp -> progressData.postValue(true))
                .doFinally(() -> progressData.postValue(false));
    }

    @SuppressLint("CheckResult")
    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Movie> callback) {

        getPageObservable(path, START_PAGE)
                .subscribe(mPage -> {
                    Timber.d("loadInitial path=%s, page=%d",path,START_PAGE);
                    callback.onResult(mPage.getMovies(), null, mPage.getPage() + 1);
                });
    }

    @SuppressLint("CheckResult")
    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Movie> callback) {

        getPageObservable(path, params.key)
                .subscribe(mPage -> {
                    Timber.d("loadBefore path=%s, page=%d",path,params.key);
                    callback.onResult(mPage.getMovies(), mPage.getPage() > 1 ? mPage.getPage() - 1 : null);
                });
    }

    @SuppressLint("CheckResult")
    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Movie> callback) {

        getPageObservable(path, params.key)
                .subscribe(mPage -> {
                    Timber.d("loadAfter path=%s, page=%d",path,params.key);
                    callback.onResult(mPage.getMovies(), mPage.getPage() < mPage.getTotalPages() ? mPage.getPage() + 1 : null);
                });
    }

    public MutableLiveData<Boolean> getProgressData() {
        return progressData;
    }

    public MutableLiveData<Throwable> getErrorData() {
        return errorData;
    }
}
