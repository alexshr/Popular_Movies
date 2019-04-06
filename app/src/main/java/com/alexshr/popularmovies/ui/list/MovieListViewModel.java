package com.alexshr.popularmovies.ui.list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.databinding.ObservableInt;

import com.alexshr.popularmovies.AppConfig;
import com.alexshr.popularmovies.R;
import com.alexshr.popularmovies.api.MoviesRepository;
import com.alexshr.popularmovies.data.Movie;
import com.alexshr.popularmovies.viewmodel.BaseViewModel;

import javax.inject.Inject;

import timber.log.Timber;

import static com.alexshr.popularmovies.AppConfig.POPULAR_PATH;
import static com.alexshr.popularmovies.AppConfig.TOP_RATED_PATH;

/**
 * Created by alexshr on 21.03.2018.
 */

public class MovieListViewModel extends BaseViewModel {

    private MoviesRepository repo;

    private LiveData<PagedList<Movie>> pagedMoviesData;
    private LiveData<Boolean> progressData = new MutableLiveData<>();
    private LiveData<Throwable> errorData = new MutableLiveData<>();

    //private String path;

    private int menuItemId;

    //private String path = AppConfig.POPULAR_PATH;
    public final ObservableInt titleRes =
            new ObservableInt(R.string.popular_movies);
    private int scrollPos;

    @Inject
    public MovieListViewModel(MoviesRepository rep) {
        repo = rep;
    }

    public void switchTo(int menuItemId) {
        if (this.menuItemId == menuItemId) return;

        this.menuItemId = menuItemId;

        String path = null;

        switch (menuItemId) {
            case R.id.popular:
                path = POPULAR_PATH;
                titleRes.set(R.string.popular_movies);
                break;
            case R.id.top_rated:
                path = TOP_RATED_PATH;
                titleRes.set(R.string.top_rated_movies);
                break;
            case R.id.favorites:
                titleRes.set(R.string.favorites);
        }

        Timber.d("path=%s", path);

        PagedList.Config pagedConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(AppConfig.PAGE_SIZE)
                .build();

        DataSource.Factory<Integer, Movie> dsFactory = path != null ?
                new MovieListDataSourceFactory(repo, path, getCompositeDisposable()) ://api
                repo.getFavoritesDSFactory();//dao

        pagedMoviesData = new LivePagedListBuilder<>(dsFactory, pagedConfig).build();

        if (path != null) {//api
            errorData = Transformations.switchMap(
                    ((MovieListDataSourceFactory) dsFactory).getDataSourceLiveData(), MovieListDataSource::getErrorData);
            progressData = Transformations.switchMap(
                    ((MovieListDataSourceFactory) dsFactory).getDataSourceLiveData(), MovieListDataSource::getProgressData);
        }
    }



/*public void loadFavorites() {
        repo.getFavoritesObservable()
                .subscribeOn(Schedulers.io())
                .subscribe(this::updateMoviesData);
    }*/

    public LiveData<PagedList<Movie>> getPagedMoviesData() {
        return pagedMoviesData;
    }

    public LiveData<Boolean> getProgressData() {
        return progressData;
    }

    public LiveData<Throwable> getErrorData() {
        return errorData;
    }

    public int getMenuItemId() {
        return menuItemId;
    }
}

