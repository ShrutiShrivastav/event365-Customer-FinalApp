package com.ebabu.event365live.utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class RecyclerPagination extends RecyclerView.OnScrollListener {
    private LinearLayoutManager layoutManager;

    protected RecyclerPagination(LinearLayoutManager layoutManager) {
        super();
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

        if((visibleItemCount+firstVisibleItemPosition) >= totalItemCount){
            loadMore();
        }

    }

    protected abstract void loadMore();


}
