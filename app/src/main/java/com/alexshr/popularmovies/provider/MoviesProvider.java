/*
 * Copyright 2018 Dionysios Karatzas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alexshr.popularmovies.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import timber.log.Timber;

import static com.alexshr.popularmovies.provider.MoviesContract.MoviesEntry.COLUMN_ID;
import static com.alexshr.popularmovies.provider.MoviesContract.MoviesEntry.CONTENT_URI;
import static com.alexshr.popularmovies.provider.MoviesContract.MoviesEntry.TABLE_NAME;


public class MoviesProvider extends ContentProvider {

    public static final int MOVIES = 100;
    public static final int MOVIE_ID = 101;

    private MoviesDbHelper mOpenHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_MOVIES, MOVIES);//for queryAll and insert
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_MOVIES + "/#", MOVIE_ID);//for delete

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mOpenHelper = new MoviesDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        Cursor cursor = null;

        switch (sUriMatcher.match(uri)) {
            case MOVIES:
                cursor = db.query(TABLE_NAME,
                        projection,
                        selection, selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                Timber.e("Unknown uri: %s", uri);
        }
        if (cursor != null) cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri resultUri = null;

        switch (sUriMatcher.match(uri)) {
            case MOVIES:
                long id = mOpenHelper.getWritableDatabase().insert(TABLE_NAME, null, values);
                if (id > 0) {
                    resultUri = ContentUris.withAppendedId(CONTENT_URI, id);
                    getContext().getContentResolver().notifyChange(resultUri, null);
                } else {
                    Timber.e("Failed to insert %s into %s", values, uri);
                }
                break;
            default:
                Timber.e("unknown url %s", uri);
        }

        return resultUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int delCount = 0;

        switch (sUriMatcher.match(uri)) {
            case MOVIE_ID:
                String id = uri.getPathSegments().get(1);//not a primary key in db
                delCount = mOpenHelper.getWritableDatabase().delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{id});
                break;
            default:
                Timber.d("Unknown uri=%s", uri);
        }

        if (delCount != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            Timber.d("not deleted uri=%s", uri);
        }

        return delCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new RuntimeException("Not implemented");
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("Not implemented");
    }

}
