package com.alexshr.popularmovies.util;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import io.reactivex.Observable;


public class ConnectionChecker {

    private Context context;


    public Boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }


    public Observable<Boolean> getConnectionObservable() {
        return Observable.just(isConnected());
    }


    public ConnectionChecker(Application context) {
        this.context = context;
    }

}
