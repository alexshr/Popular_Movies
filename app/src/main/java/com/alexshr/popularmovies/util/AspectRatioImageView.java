package com.alexshr.popularmovies.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.alexshr.popularmovies.R;

public class AspectRatioImageView extends AppCompatImageView {

    private static final float DEFAULT_ASPECT_RATIO = 1f;

    private float aspectRatio;

    public AspectRatioImageView(Context context) {
        this(context, null);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AspectRatioImageView);
        aspectRatio = a.getFloat(R.styleable.AspectRatioImageView_aspect_ratio, DEFAULT_ASPECT_RATIO);

        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int newWidth;
        int newHeight;

        newWidth = getMeasuredWidth();
        newHeight = (int) (newWidth * aspectRatio);

        setMeasuredDimension(newWidth, newHeight);
    }

    /**
     * Get the aspect ratio for this image view.
     */
    public float getAspectRatio() {
        return aspectRatio;
    }

    /**
     * Set the aspect ratio for this image view. This will copyToRealmOrUpdateOrder the view instantly.
     */
    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
        requestLayout();
    }


}