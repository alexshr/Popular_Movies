package com.alexshr.popularmovies.ui.detail;

import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.alexshr.popularmovies.R;
import com.alexshr.popularmovies.binding.DataBoundListAdapter;
import com.alexshr.popularmovies.data.Review;
import com.alexshr.popularmovies.databinding.ReviewItemBinding;

import java.util.Objects;

/**
 * A RecyclerView adapter for {@link Review} class.
 */
public class ReviewListAdapter extends DataBoundListAdapter<Review, ReviewItemBinding> {
    private final DataBindingComponent mDataBindingComponent;

    public ReviewListAdapter(DataBindingComponent dataBindingComponent) {
        this.mDataBindingComponent = dataBindingComponent;
    }

    @Override
    protected ReviewItemBinding createBinding(ViewGroup parent) {
        ReviewItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.review_item,
                        parent, false, mDataBindingComponent);

        return binding;
    }

    @Override
    protected void bind(ReviewItemBinding binding, Review item) {
        binding.setReview(item);
    }

    @Override
    protected boolean areItemsTheSame(Review oldItem, Review newItem) {
        return Objects.equals(oldItem.getId(), newItem.getId());
    }

    @Override
    protected boolean areContentsTheSame(Review oldItem, Review newItem) {
        return Objects.equals(oldItem, newItem);
    }
}
