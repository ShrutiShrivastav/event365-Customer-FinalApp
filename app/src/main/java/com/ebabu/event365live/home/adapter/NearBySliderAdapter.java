package com.ebabu.event365live.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ebabu.event365live.R;

public class NearBySliderAdapter extends RecyclerView.Adapter<NearBySliderAdapter.SliderHolder> {



    @NonNull
    @Override
    public SliderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.near_you_custom_layout,parent,false);
        return new SliderHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 30;
    }

    class SliderHolder extends RecyclerView.ViewHolder {
        SliderHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
