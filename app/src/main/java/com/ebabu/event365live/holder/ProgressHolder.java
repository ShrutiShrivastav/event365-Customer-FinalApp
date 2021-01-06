package com.ebabu.event365live.holder;

import androidx.annotation.NonNull;

import com.ebabu.event365live.bouncerecycler.RecyclerViewBouncy;
import com.ebabu.event365live.databinding.CircularProgressBarBinding;

public class ProgressHolder extends RecyclerViewBouncy.ViewHolder {
    CircularProgressBarBinding circularProgressBarBinding;
    public ProgressHolder(@NonNull CircularProgressBarBinding circularProgressBarBinding) {
        super(circularProgressBarBinding.getRoot());
        this.circularProgressBarBinding = circularProgressBarBinding;
    }
}
