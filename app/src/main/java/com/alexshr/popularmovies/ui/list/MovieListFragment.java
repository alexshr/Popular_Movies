package com.alexshr.popularmovies.ui.list;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.alexshr.popularmovies.R;
import com.alexshr.popularmovies.binding.DataBoundListAdapter;
import com.alexshr.popularmovies.binding.FragmentDataBindingComponent;
import com.alexshr.popularmovies.databinding.ListFragmentBinding;
import com.alexshr.popularmovies.di.Injectable;
import com.alexshr.popularmovies.ui.NavigationController;
import com.alexshr.popularmovies.util.AutoClearedValue;
import com.alexshr.popularmovies.util.ConnectionChecker;
import com.alexshr.popularmovies.util.EndlessRecyclerViewScrollListener;
import com.alexshr.popularmovies.util.SpacingItemDecoration;

import javax.inject.Inject;

import timber.log.Timber;

import static com.alexshr.popularmovies.AppConstants.PATH_KEY;

/**
 * Created by alexshr on 27.03.2018.
 */

public class MovieListFragment extends Fragment implements Injectable {
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    NavigationController navigationController;
    @Inject
    ConnectionChecker connectionChecker;

    private android.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private MovieListViewModel viewModel;

    private AutoClearedValue<ListFragmentBinding> binding;

    private Integer positionToScroll;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ListFragmentBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.list_fragment,
                container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);
        return dataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle outState) {
        super.onActivityCreated(outState);

        setHasOptionsMenu(true);

        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.get().toolbar);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MovieListViewModel.class);

        if (outState != null) {
            viewModel.setPath(outState.getString(PATH_KEY));
        }

        viewModel.init();

        binding.get().setViewModel(viewModel);

        MovieListAdapter rvAdapter = new MovieListAdapter(dataBindingComponent, navigationController::navigateToDetail);

        binding.get().movieList.setAdapter(rvAdapter);

        binding.get().movieList.addItemDecoration(new SpacingItemDecoration(1));

        binding.get().movieList.addOnScrollListener(new EndlessRecyclerViewScrollListener((GridLayoutManager) binding.get().movieList.getLayoutManager()) {
            @Override
            public void onLoadMore() {
                if (viewModel.getPath() != null) viewModel.loadApiPage();
            }

            @Override
            public void onScrollPositionChanged(int pos) {
                if (!connectionChecker.isConnected()) showMessage(getString(R.string.no_internet));
                viewModel.setScrollPosition(pos);
                Timber.d("scrollPosition=%d", viewModel.getScrollPosition());
                binding.get().movieList.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getErrorData().observe(this, this::showError);

        positionToScroll = viewModel.getScrollPosition();

        viewModel.getMoviesListData().observe(this, update -> {
            Timber.d("!!onChanged path=%s size=%d", viewModel.getPath(), update.size());
            rvAdapter.replace(update);

            if (positionToScroll != null) {
                new Handler().postDelayed(() -> binding.get().movieList.scrollToPosition(viewModel.getScrollPosition()), 100);
                positionToScroll = null;
            }
        });

        if (viewModel.getMoviesListData().getValue() == null || viewModel.getMoviesListData().getValue().isEmpty()) {
            viewModel.startLoading();
        }

        if (!connectionChecker.isConnected()) showMessage(getString(R.string.no_internet));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(PATH_KEY, viewModel.getPath());
        Timber.d("path=%s", viewModel.getPath());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_fragment_menu, menu);
        MenuItem progressBar = menu.findItem(R.id.progress);
        viewModel.getProgressData().observe(this, progressBar::setVisible);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ((DataBoundListAdapter) binding.get().movieList.getAdapter()).replace(null);
        if (!connectionChecker.isConnected()) showMessage(getString(R.string.no_internet));
        viewModel.startLoading(item.getItemId());
        return true;
    }

    public void showMessage(String message) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    public void showError(Throwable error) {
        Timber.e(error);
        if (connectionChecker.isConnected()) showMessage(error.getMessage());
    }
}
