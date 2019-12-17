package com.ebabu.event365live.homedrawer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.ebabu.event365live.R;
import com.ebabu.event365live.bouncerecycler.RecyclerViewBouncy;
import com.ebabu.event365live.databinding.NotificationListLayoutBinding;

public class NotificationListAdapter extends RecyclerViewBouncy.Adapter<NotificationListAdapter.NotificationListHolder>{

    private Context context;
    private NotificationListLayoutBinding listLayoutBinding;

    public NotificationListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public NotificationListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        listLayoutBinding = DataBindingUtil.inflate(inflater,R.layout.notification_list_layout,parent,false);
        return new NotificationListHolder(listLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationListHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 20;
    }

    class NotificationListHolder extends RecyclerViewBouncy.ViewHolder {
        NotificationListHolder(@NonNull NotificationListLayoutBinding layoutBinding) {
            super(layoutBinding.getRoot());
        }
    }
}
