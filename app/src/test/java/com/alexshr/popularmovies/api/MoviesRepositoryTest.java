package com.alexshr.popularmovies.api;

import com.alexshr.popularmovies.data.Movie;
import com.alexshr.popularmovies.data.MoviesPage;
import com.alexshr.popularmovies.db.AppDao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Created by alexshr on 24.04.2019.
 */
@RunWith(MockitoJUnitRunner.class)
public class MoviesRepositoryTest {
    @Mock
    ApiService apiService;
    @Mock
    AppDao appDao;

    @Test
    public void testGetMoviesObservable() {
        //given
        RxJavaPlugins.setIoSchedulerHandler(
                scheduler -> Schedulers.trampoline());

        /*RxAndroidPlugins.setMainThreadSchedulerHandler(
                //scheduler -> immediate
                scheduler -> Schedulers.trampoline());*/

        MoviesRepository repository = new MoviesRepository(apiService, appDao);

        MoviesPage moviesPage = new MoviesPage();
        moviesPage.setPage(1);
        moviesPage.setTotalResults(1000);
        moviesPage.setTotalPages(100);
        List<Movie> movieList = new ArrayList<>();
        Movie movie = new Movie();
        movie.setId(1);
        movieList.add(movie);

        Response<MoviesPage> resp = Response.success(moviesPage);
        Mockito.when(apiService.getMoviesObservable(Mockito.anyString(), Mockito.anyInt())).thenReturn(Observable.just(resp));

        //when

        TestObserver<MoviesPage> testObserver = repository.getMoviesObservable("", 0).test();

        //then
        testObserver.assertValue(moviesPage);
        testObserver.assertComplete();
        testObserver.assertNoErrors();
    }
}