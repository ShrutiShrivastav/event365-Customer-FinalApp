package com.ebabu.event365live.utils;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VerticalItemDecoration extends RecyclerView.ItemDecoration {
    private Context context;
    private boolean isTopMarginRequire;

    public VerticalItemDecoration(Context context, boolean isTopMarginRequire) {
        this.context = context;
        this.isTopMarginRequire = isTopMarginRequire;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int itemPosition = parent.getChildAdapterPosition(view);
        if(isTopMarginRequire){
            if(itemPosition == 1)
                outRect.top = 0;
        }else{
            if(itemPosition == 1)
                outRect.top = CommonUtils.getCommonUtilsInstance().dpToPixel(context,10);
        }
        if(itemPosition == state.getItemCount()-1){
            outRect.bottom = CommonUtils.getCommonUtilsInstance().dpToPixel(context,10);
        }
    }


}
