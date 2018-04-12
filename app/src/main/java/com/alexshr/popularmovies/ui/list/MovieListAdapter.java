/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alexshr.popularmovies.ui.list;

import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.alexshr.popularmovies.R;
import com.alexshr.popularmovies.binding.DataBoundListAdapter;
import com.alexshr.popularmovies.data.Movie;
import com.alexshr.popularmovies.databinding.MovieItemBinding;

import java.util.Objects;


/**
 * A RecyclerView adapter for {@link Movie} class.
 */
public class MovieListAdapter extends DataBoundListAdapter<Movie, MovieItemBinding> {
    private final DataBindingComponent mDataBindingComponent;
    private final MovieClickCallback mMovieClickCallback;

    public MovieListAdapter(DataBindingComponent dataBindingComponent,
                            MovieClickCallback mMovieClickCallback) {
        this.mDataBindingComponent = dataBindingComponent;
        this.mMovieClickCallback = mMovieClickCallback;
    }


    @Override
    protected MovieItemBinding createBinding(ViewGroup parent) {
        MovieItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.movie_item,
                        parent, false, mDataBindingComponent);
        binding.getRoot().setOnClickListener(v -> {
            Movie movie = binding.getMovie();
            if (movie != null && mMovieClickCallback != null) {
                mMovieClickCallback.onClick(movie);
            }
        });
        return binding;
    }

    @Override
    protected void bind(MovieItemBinding binding, Movie item) {
        binding.setMovie(item);
    }

    @Override
    protected boolean areItemsTheSame(Movie oldItem, Movie newItem) {
        return Objects.equals(oldItem.getId(), newItem.getId());
    }

    @Override
    protected boolean areContentsTheSame(Movie oldItem, Movie newItem) {
        return Objects.equals(oldItem.getPosterPath(), newItem.getPosterPath());
    }

    public interface MovieClickCallback {
        void onClick(Movie movie);
    }
}
