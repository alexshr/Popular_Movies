package com.alexshr.popularmovies.ui.list;

import android.os.Bundle;
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
import com.alexshr.popularmovies.util.SpacingItemDecoration;
import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import timber.log.Timber;

import static com.alexshr.popularmovies.AppConstants.MENU_ITEM_KEY;

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

    private androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private MovieListViewModel viewModel;

    private AutoClearedValue<ListFragmentBinding> binding;

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

        initMovieList();

        viewModel.syncData(outState != null ? outState.getInt(MENU_ITEM_KEY) : 0);

        subscribe();
        checkConnection();

        binding.get().setViewModel(viewModel);
    }

    private void initMovieList() {
        MoviePagedListAdapter adapter = new MoviePagedListAdapter(dataBindingComponent, navigationController::navigateToDetail);
        binding.get().movieList.setAdapter(adapter);

        binding.get().setViewModel(viewModel);

        binding.get().movieList.addItemDecoration(new SpacingItemDecoration(1));
    }

    private void subscribe() {
        MoviePagedListAdapter adapter = (MoviePagedListAdapter) binding.get().movieList.getAdapter();
        viewModel.getErrorData().observe(this, this::showError);
        viewModel.getPagedMoviesData().observe(this, adapter::submitList);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(MENU_ITEM_KEY, viewModel.getMenuItemId());
        Timber.d("menuItemId=%d", viewModel.getMenuItemId());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_fragment_menu, menu);
        MenuItem progressBar = menu.findItem(R.id.progress);
        viewModel.getProgressData().observe(this, progressBar::setVisible);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        viewModel.switchTo(item.getItemId());
        subscribe();
        checkConnection();
        return true;
    }

    public void showMessage(String message) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    public void showError(Throwable error) {
        Timber.e(error);
        showMessage(error.getMessage());
    }

    private void checkConnection() {
        if (!connectionChecker.isConnected()) showMessage(getString(R.string.no_internet));
    }
}
