package com.alexshr.popularmovies.ui.detail;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.alexshr.popularmovies.R;
import com.alexshr.popularmovies.binding.FragmentDataBindingComponent;
import com.alexshr.popularmovies.data.Movie;
import com.alexshr.popularmovies.databinding.DetailFragmentBinding;
import com.alexshr.popularmovies.di.Injectable;
import com.alexshr.popularmovies.ui.NavigationController;
import com.alexshr.popularmovies.util.AutoClearedValue;

import javax.inject.Inject;

import timber.log.Timber;

import static com.alexshr.popularmovies.AppConstants.MOVIE_KEY;

/**
 * Created by alexshr on 01.04.2018.
 */
public class MovieDetailFragment extends Fragment implements Injectable {

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    NavigationController navigationController;

    android.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private MovieDetailViewModel movieViewModel;

    AutoClearedValue<DetailFragmentBinding> binding;

    public static MovieDetailFragment create(Movie movie) {
        Timber.d("movie:" + movie);
        MovieDetailFragment detailFragment = new MovieDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(MOVIE_KEY, movie);
        detailFragment.setArguments(bundle);
        return detailFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        DetailFragmentBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.detail_fragment,
                container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        return dataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.get().toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        movieViewModel = ViewModelProviders.of(this, viewModelFactory).get(MovieDetailViewModel.class);
        binding.get().setViewModel(movieViewModel);

        Movie movie = getArguments().getParcelable(MOVIE_KEY);
        movieViewModel.setMovie(movie);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                navigationController.backFromDetails();
        }
        return super.onOptionsItemSelected(menuItem);
    }

}
