package com.ebabu.event365live.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.RsvpCustomListLayoutBinding;

public class RsvpListAdapter extends RecyclerView.Adapter<RsvpListAdapter.RsvpHolder>{
    private Context context;
    private RsvpCustomListLayoutBinding rsvpLayoutBinding;

    public RsvpListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RsvpHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        rsvpLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.rsvp_custom_list_layout,parent,false);

        return new RsvpHolder(rsvpLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RsvpHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 20;
    }

    class RsvpHolder extends RecyclerView.ViewHolder {
        RsvpCustomListLayoutBinding holderLayoutBinding;
        public RsvpHolder(@NonNull RsvpCustomListLayoutBinding holderLayoutBinding) {
            super(rsvpLayoutBinding.getRoot());
            this.holderLayoutBinding = holderLayoutBinding;
        }

        public void bind(RsvpCustomListLayoutBinding holderLayoutBinding){
            {

            }

        }
    }
}
