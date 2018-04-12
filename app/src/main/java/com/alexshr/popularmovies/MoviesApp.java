package com.alexshr.popularmovies;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.support.annotation.NonNull;

import com.alexshr.popularmovies.di.AppInjector;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import timber.log.Timber;
import timber.log.Timber.DebugTree;


public class MoviesApp extends Application implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> mDispatchingAndroidInjector;
    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new DebugTree() {
                @SuppressLint("DefaultLocale")
                @Override
                protected String createStackElementTag(@NonNull StackTraceElement element) {
                    return String.format("%s: %s:%d (%s)",
                            super.createStackElementTag(element),
                            element.getMethodName(),
                            element.getLineNumber(),
                            Thread.currentThread().getName());
                }
            });
            AppInjector.init(this);
        }
    }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return mDispatchingAndroidInjector;
    }
}
