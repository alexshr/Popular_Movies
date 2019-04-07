package com.alexshr.popularmovies.binding;

import android.content.res.ColorStateList;
import android.widget.ImageView;

import com.alexshr.popularmovies.GlideApp;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.databinding.BindingAdapter;
import androidx.fragment.app.Fragment;

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
