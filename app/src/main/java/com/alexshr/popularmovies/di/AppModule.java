package com.alexshr.popularmovies.di;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.alexshr.popularmovies.BuildConfig;
import com.alexshr.popularmovies.api.ApiService;
import com.alexshr.popularmovies.util.ConnectionChecker;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

import static com.alexshr.popularmovies.AppConfig.API_KEY_PARAM;
import static com.alexshr.popularmovies.AppConfig.BASE_URL;
import static com.alexshr.popularmovies.AppConfig.CACHE_OFFLINE_MAX_STALE;
import static com.alexshr.popularmovies.AppConfig.CACHE_SIZE;
import static com.alexshr.popularmovies.AppConfig.MAX_CONNECTION_TIMEOUT;
import static com.alexshr.popularmovies.AppConfig.MAX_READ_TIMEOUT;
import static com.alexshr.popularmovies.AppConfig.MAX_WRITE_TIMEOUT;

@Module(includes = ViewModelModule.class)
class AppModule {

    //region ================= network providers =================

    @Singleton
    @Provides
    ApiService provideApiService(OkHttpClient okHttpClient) {

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
                .create(ApiService.class);

    }

    @Singleton
    @Provides
    OkHttpClient provideOkHttpClient(Application app, ConnectionChecker connectionChecker) {
        return new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(createApiKeyInterceptor())
                .addInterceptor(createCacheControlInterceptor(connectionChecker))
                .connectTimeout(MAX_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(MAX_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(MAX_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .cache(createCache(app))
                .build();
    }

    @Singleton
    @Provides
    ConnectionChecker provideConnectionChecker(Application app) {
        return new ConnectionChecker(app);
    }


    private Cache createCache(Context context) {
        File httpCacheDirectory = new File(context.getCacheDir(), "responses");
        return new Cache(httpCacheDirectory, CACHE_SIZE);
    }

    private Interceptor createApiKeyInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();

                HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter(API_KEY_PARAM, BuildConfig.API_KEY)
                        .build();

                Request.Builder requestBuilder = original.newBuilder()
                        .url(url);

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };
    }

    private Interceptor createCacheControlInterceptor(ConnectionChecker connectionChecker) {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Timber.d("CacheControlInterceptor called");
                Response originalResponse = chain.proceed(chain.request());
                if (!connectionChecker.isConnected()) {
                    Timber.d("adding Cache-Control for offline work");
                    return originalResponse.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + CACHE_OFFLINE_MAX_STALE)
                            .build();
                } else return originalResponse;
            }
        };
    }

    //endregion

    @Singleton
    @Provides
    Context provideContext(Application app) {
        return app;
    }
}
