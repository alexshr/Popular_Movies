package com.alexshr.popularmovies.ui.detail;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.alexshr.popularmovies.R;
import com.alexshr.popularmovies.binding.FragmentDataBindingComponent;
import com.alexshr.popularmovies.data.Movie;
import com.alexshr.popularmovies.data.Video;
import com.alexshr.popularmovies.databinding.DetailFragmentBinding;
import com.alexshr.popularmovies.di.Injectable;
import com.alexshr.popularmovies.ui.NavigationController;
import com.alexshr.popularmovies.util.AutoClearedValue;
import com.alexshr.popularmovies.util.ConnectionChecker;
import com.alexshr.popularmovies.util.SpacingItemDecoration;

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
    @Inject
    ConnectionChecker connectionChecker;

    DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private MovieDetailViewModel viewModel;

    private Video sharedVideo;

    AutoClearedValue<DetailFragmentBinding> binding;

    public static MovieDetailFragment create(Movie movie) {
        Timber.d("movie: %s", movie);
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

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MovieDetailViewModel.class);
        binding.get().setViewModel(viewModel);

        setupVideos();
        setupReviews();

        Movie movie = getArguments().getParcelable(MOVIE_KEY);
        viewModel.setMovie(movie);

        viewModel.getErrorData().observe(this, this::showError);

        viewModel.getIsFavoriteData().observe(this, isFavorite -> {
            movie.setFavorite(isFavorite);
        });

        if (!connectionChecker.isConnected()) showMessage(getString(R.string.no_internet));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_fragment_menu, menu);
        MenuItem progressBar = menu.findItem(R.id.progress);
        viewModel.getProgressData().observe(this, progressBar::setVisible);

        MenuItem shareMenuItem = menu.findItem(R.id.action_share);
        ShareActionProvider shareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(shareMenuItem);

        viewModel.getVideosData().observe(this, update -> {
            RecyclerView rv = binding.get().videos;
            VideoListAdapter adapter = (VideoListAdapter) rv.getAdapter();
            adapter.replace(update);

            if (update == null || update.isEmpty()) {
                rv.setVisibility(View.GONE);
                shareMenuItem.setEnabled(false);
                sharedVideo = null;
                shareAction.setShareIntent(null);
            } else {
                rv.setVisibility(View.VISIBLE);
                shareMenuItem.setEnabled(true);
                sharedVideo = update.get(0);
                shareAction.setShareIntent(getShareIntent());
            }
        });

        viewModel.loadVideosAndReviews();//to get the result after getting menu
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                navigationController.backFromDetails();
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void setupVideos() {
        RecyclerView recyclerView = binding.get().videos;

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        VideoListAdapter videosAdapter = new VideoListAdapter(dataBindingComponent, this::startVideo);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SpacingItemDecoration(2));
        recyclerView.setAdapter(videosAdapter);
    }

    private Intent getShareIntent() {

        return new Intent(Intent.ACTION_SEND)
                .setType("text/plain")

                .putExtra(Intent.EXTRA_TITLE, viewModel.getMovie().getTitle() + " - " + sharedVideo.getName())
                .putExtra(Intent.EXTRA_SUBJECT, viewModel.getMovie().getTitle() + " - " + sharedVideo.getName())
                .putExtra(Intent.EXTRA_TITLE, viewModel.getMovie().getTitle() + " - " + sharedVideo.getName())
                .putExtra(Intent.EXTRA_TEXT, sharedVideo.getVideoUrl());

        //return Intent.createChooser(sharingIntent, getString(R.string.share_trailer));
    }

    private void setupReviews() {
        RecyclerView recyclerView = binding.get().reviews;

        ReviewListAdapter reviewsAdapter = new ReviewListAdapter(dataBindingComponent);
        recyclerView.addItemDecoration(new SpacingItemDecoration(16));
        recyclerView.setAdapter(reviewsAdapter);

        viewModel.getReviewsData().observe(this, update -> {
            //recyclerView.setVisibility(update.isEmpty() ? View.GONE : View.VISIBLE);
            binding.get().reviewsTitle.setVisibility(update != null && !update.isEmpty() ? View.VISIBLE : View.GONE);
            reviewsAdapter.replace(update);
        });
    }

    private void startVideo(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        getContext().startActivity(intent);
    }

    public void showMessage(String message) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    public void showError(Throwable error) {
        Timber.e(error);
        if (connectionChecker.isConnected()) showMessage(error.getMessage());
    }
}
