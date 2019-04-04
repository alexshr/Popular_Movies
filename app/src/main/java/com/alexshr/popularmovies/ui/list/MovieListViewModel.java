package com.alexshr.popularmovies.ui.list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
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
    private LiveData<Boolean> progressData;
    private LiveData<Throwable> errorData;

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

        if (path != null) {

            MovieListDataSourceFactory dsFactory = new MovieListDataSourceFactory(repo, path, getCompositeDisposable());

            PagedList.Config pagedConfig = new PagedList.Config.Builder()
                    .setEnablePlaceholders(false)
                    .setPageSize(AppConfig.PAGE_SIZE)
                    .build();

            pagedMoviesData = new LivePagedListBuilder<>(dsFactory, pagedConfig).build();

            errorData = Transformations.switchMap(
                    dsFactory.getDataSourceLiveData(), MovieListDataSource::getErrorData);
            progressData = Transformations.switchMap(
                    dsFactory.getDataSourceLiveData(), MovieListDataSource::getProgressData);
        } else {
            //TODO fetch favorites
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

