package com.alexshr.popularmovies.viewmodel;

import androidx.lifecycle.ViewModel;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public abstract class BaseViewModel extends ViewModel {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
        Timber.d(" ");
    }

    public CompositeDisposable getCompositeDisposable() {
        return compositeDisposable;
    }
}
