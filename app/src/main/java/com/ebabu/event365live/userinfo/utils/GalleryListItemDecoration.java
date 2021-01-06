package com.ebabu.event365live.userinfo.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ebabu.event365live.R;

public class GalleryListItemDecoration extends RecyclerView.ItemDecoration {

    private Context context;

    public GalleryListItemDecoration(Context context) {
        this.context = context;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        final int itemPosition = parent.getChildAdapterPosition(view);
        final RecyclerView.Adapter adapter = parent.getAdapter();
        outRect.top = dpToPx();
        outRect.right = dpToPx();
        outRect.left = dpToPx();

        if (adapter != null && itemPosition == adapter.getItemCount() - 1) {
            outRect.bottom = dpToPx();
        }
    }

    private int dpToPx() {
        return context.getResources().getDimensionPixelOffset(R.dimen._4sdp); }


}
