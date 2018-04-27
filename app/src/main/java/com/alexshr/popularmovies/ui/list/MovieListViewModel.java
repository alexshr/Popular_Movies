package com.alexshr.popularmovies.ui.list;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableInt;

import com.alexshr.popularmovies.AppConfig;
import com.alexshr.popularmovies.R;
import com.alexshr.popularmovies.api.MoviesRepository;
import com.alexshr.popularmovies.data.Movie;
import com.alexshr.popularmovies.data.MoviesPage;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.alexshr.popularmovies.AppConfig.POPULAR_PATH;
import static com.alexshr.popularmovies.AppConfig.TOP_RATED_PATH;

/**
 * Created by alexshr on 21.03.2018.
 */

public class MovieListViewModel extends ViewModel {

    private MoviesRepository repository;

    private MutableLiveData<List<Movie>> moviesListData = new MutableLiveData<>();
    private MutableLiveData<Throwable> errorData = new MutableLiveData<>();
    private MutableLiveData<Boolean> progressData = new MutableLiveData<>();

    private Integer nextPage;
    private Integer totalPages;
    private Integer scrollPosition;
    private String path = AppConfig.POPULAR_PATH;
    public final ObservableInt titleRes =
            new ObservableInt(R.string.popular_movies);

    @Inject
    public MovieListViewModel(MoviesRepository rep) {
        repository = rep;
    }

    public void init() {
        errorData = new MutableLiveData<>();
        progressData = new MutableLiveData<>();
        if (path == null) moviesListData = new MutableLiveData<>();//to force reloading
    }

    @Override
    protected void onCleared() {
        Timber.d("onClear");
        moviesListData = null;
    }

    public void loadApiPage() {
        if (nextPage == null) nextPage = 1;
        Timber.d("path=%s; nextPage=%d; mTotalPage=%d", path, nextPage, totalPages);
        if (totalPages == null || nextPage <= totalPages) {
            progressData.postValue(true);
            repository.getMoviesObservable(path, nextPage)
                    .doFinally(() -> progressData.postValue(false))
                    .subscribe(this::onData, errorData::postValue);
        }
    }

    private void onData(MoviesPage moviesPage) {
        totalPages = moviesPage.getTotalPages();
        nextPage = moviesPage.getPage() + 1;
        updateMoviesData(moviesPage.getResults());
        Timber.d("path=%s; nextPage=%d; mTotalPage=%d;", path, nextPage, totalPages);
    }

    public void startLoading(int menuId) {
        //nextPage = null;
        switch (menuId) {
            case R.id.popular:
                path = POPULAR_PATH;
                titleRes.set(R.string.popular_movies);
                break;
            case R.id.top_rated:
                path = TOP_RATED_PATH;
                titleRes.set(R.string.top_rated_movies);
                break;
            default:
                path = null;
                titleRes.set(R.string.favorites);
        }

        startLoading();
    }

    public void startLoading() {
        if (moviesListData.getValue() != null) {
            moviesListData.getValue().clear();
        }
        if (path != null) {
            nextPage = 1;
            loadApiPage();
        } else loadFavorites();
    }

    public void loadFavorites() {
        repository.getFavoritesObservable()
                .subscribeOn(Schedulers.io())
                .subscribe(this::updateMoviesData);
    }

    private void updateMoviesData(List<Movie> pageData) {

        List<Movie> data = new ArrayList<>();
        if (moviesListData.getValue() != null) data.addAll(moviesListData.getValue());

        data.addAll(pageData);

        Timber.d("moviesListData.postValue size=%d", data.size());
        moviesListData.postValue(data);
    }

    public MutableLiveData<List<Movie>> getMoviesListData() {
        return moviesListData;
    }

    public MutableLiveData<Throwable> getErrorData() {
        return errorData;
    }

    public MutableLiveData<Boolean> getProgressData() {
        return progressData;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setScrollPosition(Integer scrollPosition) {
        this.scrollPosition = scrollPosition;
    }

    public Integer getScrollPosition() {
        return scrollPosition;
    }
}

