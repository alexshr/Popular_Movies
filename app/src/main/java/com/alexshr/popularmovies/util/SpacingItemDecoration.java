package com.alexshr.popularmovies.util;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SpacingItemDecoration extends RecyclerView.ItemDecoration {
    private final int spacing;
    private Integer displayMode;

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    public static final int GRID = 2;

    public SpacingItemDecoration(int spacing) {
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildViewHolder(view).getAdapterPosition();
        int itemCount = state.getItemCount();
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        setSpacingForDirection(outRect, layoutManager, position, itemCount);
    }

    private void setSpacingForDirection(Rect outRect,
                                        RecyclerView.LayoutManager layoutManager,
                                        int position,
                                        int itemCount) {

        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            int cols = gridLayoutManager.getSpanCount();
            int column = position % cols; // item column

            outRect.left = spacing - column * spacing / cols; // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * spacing / cols; // (column + 1) * ((1f / spanCount) * spacing)

            if (position < cols) { // top edge
                outRect.top = spacing;
            }
            outRect.bottom = spacing; // item bottom
        } else if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            if (linearLayoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
                outRect.top = spacing;
                outRect.bottom = position == itemCount - 1 ? spacing : 0;
            } else {
                outRect.right = position == itemCount - 1 ? 0 : spacing;
                outRect.top = spacing;
                outRect.bottom = spacing;
            }
        }
    }
}
