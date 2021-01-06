package com.ebabu.event365live.home.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ebabu.event365live.bouncerecycler.RecyclerViewBouncy;

public class RsvpItemDecoration extends RecyclerViewBouncy.ItemDecoration{

    Context context;

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int itemPosition = parent.getChildAdapterPosition(view);
        RecyclerViewBouncy.Adapter adapter = parent.getAdapter();

        if(adapter != null && itemPosition == 0){
            outRect.top =0 ;
            Log.d("fafasfasfaf", "getItemOffsets: ");
        }


    }



}
