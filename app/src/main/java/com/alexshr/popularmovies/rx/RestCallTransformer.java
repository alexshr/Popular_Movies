package com.alexshr.popularmovies.rx;


import com.alexshr.popularmovies.AppConfig;
import com.alexshr.popularmovies.api.ApiException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import timber.log.Timber;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class RestCallTransformer<R> implements ObservableTransformer<Response<R>, R> {


    @Override
    public ObservableSource<R> apply(Observable<Response<R>> responseObservable) {
        return getApiResponseObservable(responseObservable)
                .subscribeOn(Schedulers.io());

    }

    private Observable<R> getApiResponseObservable(Observable<Response<R>> responseObservable) {
        return responseObservable
                .flatMap(response -> {
                    switch (response.code()) {
                        case 200:
                        case 201:
                        case 202:
                        case 204:
                            return Observable.just(response.body());
                        case 304:
                            return Observable.empty();
                        default:
                            return Observable.error(ApiException.parseError(response));
                    }
                })
                .retryWhen(errorObservable -> errorObservable
                        .zipWith(Observable.range(1, AppConfig.RETRY_COUNT + 1), (e, attempt) -> {
                            Timber.e("network request error after attempt %d; max count fo retry %d", attempt, AppConfig.RETRY_COUNT);
                            if (attempt == AppConfig.RETRY_COUNT + 1)
                                throw new RuntimeException(e);
                            else
                                return attempt;
                        })
                        .flatMap(attempt -> {
                            long delay = getExponentialDelayInMs(attempt, AppConfig.RETRY_INITIAL_DELAY);
                            Timber.d("delay %d ms; after attempt %d; max count fo retry %d", delay, attempt, AppConfig.RETRY_COUNT);
                            return Observable.timer(delay, MILLISECONDS);
                        })
                );

    }


    private long getExponentialDelayInMs(int runCount, long initialBackOffInMs) {
        return initialBackOffInMs * (long) Math.pow(2.0D, (double) Math.max(0, runCount - 1));
    }

}
