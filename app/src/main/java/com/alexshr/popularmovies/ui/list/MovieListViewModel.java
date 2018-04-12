package com.alexshr.popularmovies.ui.list;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.alexshr.popularmovies.AppConfig;
import com.alexshr.popularmovies.api.MoviesRepository;
import com.alexshr.popularmovies.data.Movie;
import com.alexshr.popularmovies.data.MoviesPage;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by alexshr on 21.03.2018.
 */

public class MovieListViewModel extends ViewModel {

    private MoviesRepository repository;

    private MutableLiveData<List<Movie>> moviesListData = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> progressData = new MutableLiveData<>();
    private String path= AppConfig.POPULAR_PATH;
    private Integer nextPage;
    private Integer totalPages;
    private int scrollPosition;

    @Inject
    public MovieListViewModel(MoviesRepository rep) {
        repository = rep;
    }

    public void loadPage() {
        Timber.d("path=%s; nextPage=%d; mTotalPage=%d", path, nextPage, totalPages);
        if (totalPages == null || nextPage <= totalPages) {
            progressData.postValue(true);
            repository.loadPage(path, nextPage)
                    .doFinally(() -> progressData.postValue(false))
                    .subscribe(this::onData, this::onError);
        }
    }

    private void onError(Throwable e){
        String mes=e.getMessage().contains("UnknownHostException")?"no_internet":e.getMessage();
        errorMessage.postValue(mes);
    }

    private void onData(MoviesPage moviesPage) {
        totalPages = moviesPage.getTotalPages();
        nextPage = moviesPage.getPage() + 1;
        updateMoviesData(moviesPage.getResults());
        Timber.d("path=%s; nextPage=%d; mTotalPage=%d;", path, nextPage, totalPages);
    }

    public void startLoad(){
        nextPage = 1;
        moviesListData.setValue(new ArrayList<>());
        loadPage();
    }

    private void updateMoviesData(List<Movie> pageData) {

        if (pageData.isEmpty()) return;

        List<Movie> data = new ArrayList<>();
        if (moviesListData.getValue() != null) data.addAll(moviesListData.getValue());

        data.addAll(pageData);

        moviesListData.postValue(data);
    }

    public MutableLiveData<List<Movie>> getMoviesListData() {
        return moviesListData;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
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

    public Integer getNextPage() {
        return nextPage;
    }

    public void setScrollPosition(int scrollPosition) {
        this.scrollPosition = scrollPosition;
    }

    public int getScrollPosition() {
        return scrollPosition;
    }
}

