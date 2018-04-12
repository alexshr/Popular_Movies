package com.alexshr.popularmovies.ui.list;


import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
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
import com.alexshr.popularmovies.binding.FragmentDataBindingComponent;
import com.alexshr.popularmovies.databinding.ListFragmentBinding;
import com.alexshr.popularmovies.di.Injectable;
import com.alexshr.popularmovies.ui.NavigationController;
import com.alexshr.popularmovies.util.AutoClearedValue;
import com.alexshr.popularmovies.util.ConnectionChecker;
import com.alexshr.popularmovies.util.EndlessRecyclerViewScrollListener;

import javax.inject.Inject;

import timber.log.Timber;

import static com.alexshr.popularmovies.AppConfig.POPULAR_PATH;
import static com.alexshr.popularmovies.AppConfig.TOP_RATED_PATH;
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
    //private AutoClearedValue<MovieListAdapter> mAdapter;
    private Integer startPos;
    private MenuItem progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
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

        showTitle();

        MovieListAdapter rvAdapter = new MovieListAdapter(dataBindingComponent, navigationController::navigateToDetail);

        if (outState != null) {
            viewModel.setPath(outState.getString(PATH_KEY));
        }
        startPos = viewModel.getScrollPosition();

        binding.get().movieList.setAdapter(rvAdapter);

        binding.get().movieList.addOnScrollListener(new EndlessRecyclerViewScrollListener((GridLayoutManager) binding.get().movieList.getLayoutManager()) {
            @Override
            public void onLoadMore() {
                viewModel.loadPage();
            }

            @Override
            public void onScrollPositionChanged(int pos) {
                if (!connectionChecker.isConnected()) showMessage("no_internet");
                viewModel.setScrollPosition(pos);
                Timber.d("scrollPosition=%d", viewModel.getScrollPosition());
            }
        });




        viewModel.getErrorMessage().observe(this, this::showMessage);


        viewModel.getMoviesListData().observe(this, update -> {



            rvAdapter.replace(update);

            if (startPos != null) {
                binding.get().movieList.getLayoutManager().scrollToPosition(startPos);
                startPos = null;
            }
        });

        if (viewModel.getNextPage() == null) viewModel.startLoad();

    }




    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(PATH_KEY, viewModel.getPath());
        Timber.d("path=%s", viewModel.getPath());
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_fragment_menu, menu);
        progressBar = menu.findItem(R.id.actionProgress);
        viewModel.getProgressData().observe(this,progressBar::setVisible);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        viewModel.setPath(item.getItemId() == R.id.popular ? POPULAR_PATH : TOP_RATED_PATH);
        viewModel.startLoad();
        showTitle();
        return true;
    }

    private void showTitle() {
        binding.get().setListName(getString(
                viewModel.getPath().equals(POPULAR_PATH) ? R.string.popular_movies : R.string.top_rated_movies));
    }

    public void showMessage(String message) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }


}
