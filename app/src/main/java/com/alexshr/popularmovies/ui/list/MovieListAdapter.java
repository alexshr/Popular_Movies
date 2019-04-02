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

import io.reactivex.functions.Consumer;

/**
 * A RecyclerView adapter for {@link Movie} class.
 */
public class MovieListAdapter extends DataBoundListAdapter<Movie, MovieItemBinding> {
    private final DataBindingComponent mDataBindingComponent;

    public MovieListAdapter(DataBindingComponent dataBindingComponent,
                            Consumer<Movie> mMovieClickCallback) {
        this.mDataBindingComponent = dataBindingComponent;
        itemClickCallback = mMovieClickCallback;
    }

    @Override
    protected MovieItemBinding createBinding(ViewGroup parent) {
        MovieItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.movie_item,
                        parent, false, mDataBindingComponent);
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
}
