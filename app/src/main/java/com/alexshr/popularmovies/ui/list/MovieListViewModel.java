package com.alexshr.popularmovies.ui.list;

import com.alexshr.popularmovies.AppConfig;
import com.alexshr.popularmovies.R;
import com.alexshr.popularmovies.api.MoviesRepository;
import com.alexshr.popularmovies.data.Movie;
import com.alexshr.popularmovies.viewmodel.BaseViewModel;

import javax.inject.Inject;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import lombok.Getter;
import timber.log.Timber;

import static com.alexshr.popularmovies.AppConfig.POPULAR_PATH;
import static com.alexshr.popularmovies.AppConfig.TOP_RATED_PATH;

public class MovieListViewModel extends BaseViewModel {

    public final ObservableInt titleRes =
            new ObservableInt(R.string.popular_movies);
    private MoviesRepository repo;

    @Getter
    private LiveData<PagedList<Movie>> pagedMoviesData;
    @Getter
    private LiveData<Boolean> progressData = new MutableLiveData<>();
    @Getter
    private LiveData<Throwable> errorData = new MutableLiveData<>();
    @Getter
    private int menuItemId = R.id.popular;

    @Inject
    public MovieListViewModel(MoviesRepository rep) {
        repo = rep;
    }

    public void syncData(int menuItemId) {
        if (menuItemId != 0 && this.menuItemId != menuItemId) {
            this.menuItemId = menuItemId;
            pagedMoviesData = null;
        }
        if (pagedMoviesData == null) refreshData();
    }

    private void refreshData() {

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

    public void switchTo(int menuItemId) {
        if (this.menuItemId != menuItemId) {
            this.menuItemId = menuItemId;
            refreshData();
        }
    }
}

