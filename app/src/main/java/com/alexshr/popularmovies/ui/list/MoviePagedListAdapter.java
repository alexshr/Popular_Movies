package com.alexshr.popularmovies.ui.list;

import android.arch.paging.PagedListAdapter;
import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.alexshr.popularmovies.R;
import com.alexshr.popularmovies.data.Movie;
import com.alexshr.popularmovies.databinding.MovieItemBinding;

import io.reactivex.functions.Consumer;
import timber.log.Timber;

/**
 * A RecyclerView adapter for {@link Movie} class.
 */

public class MoviePagedListAdapter extends PagedListAdapter<Movie, MoviePagedListAdapter.MovieViewHolder> {

    private DataBindingComponent dataBindingComponent;
    private Consumer<Movie> itemClickCallback;

    public MoviePagedListAdapter(DataBindingComponent dataBindingComponent, Consumer<Movie> itemClickCallback) {
        super(Movie.DIFF_CALLBACK);
        this.dataBindingComponent = dataBindingComponent;
        this.itemClickCallback = itemClickCallback;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MovieItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.movie_item, parent, false,dataBindingComponent);
        return new MovieViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        holder.binding.setMovie(getItem(position));
        if (itemClickCallback != null) {
            holder.itemView.setOnClickListener(v -> {
                try {
                    itemClickCallback.accept(getItem(position));
                } catch (Exception e) {
                    Timber.e(e);
                }
            });
        }
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {

        MovieItemBinding binding;

        MovieViewHolder(MovieItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
