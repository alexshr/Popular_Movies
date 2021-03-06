package com.alexshr.popularmovies.binding;

import android.content.res.ColorStateList;
import android.databinding.BindingAdapter;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.alexshr.popularmovies.GlideApp;

/**
 * Binding adapters that work with a fragment instance.
 */
public class FragmentBindingAdapters {
    final Fragment fragment;


    public FragmentBindingAdapters(Fragment fragment) {
        this.fragment = fragment;
    }

    @BindingAdapter("imageUrl")
    public void bindImage(ImageView imageView, String url) {
        GlideApp.with(fragment)
                .load(url)
                .into(imageView);
    }

    @BindingAdapter("app:backgroundTint")
    public void setFabColor(FloatingActionButton fab, int color) {
        fab.setBackgroundTintList(ColorStateList.valueOf(color));

    }
}
