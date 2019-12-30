package com.ebabu.event365live.userinfo.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.bouncerecycler.RecyclerViewBouncy;
import com.ebabu.event365live.databinding.ShowGalleryLayoutBinding;
import com.ebabu.event365live.userinfo.modal.GetAllGalleryImgModal;
import com.ebabu.event365live.userinfo.modal.eventdetailsmodal.EventImage;
import com.ebabu.event365live.userinfo.modal.eventdetailsmodal.Review;
import com.ebabu.event365live.utils.ImageViewer;

import java.util.List;

public class GalleryAdapter extends RecyclerViewBouncy.Adapter<GalleryAdapter.GalleryHolder>{

    private Context context;
    private ShowGalleryLayoutBinding galleryLayoutBinding;
    private List<GetAllGalleryImgModal> eventImageList;

    public GalleryAdapter(List<GetAllGalleryImgModal> eventImageList) {
        this.eventImageList = eventImageList;
    }

    @NonNull
    @Override
    public GalleryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        galleryLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.show_gallery_layout,parent,false);
        return new GalleryHolder(galleryLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryHolder holder, int position) {
        Glide.with(context).load(eventImageList.get(position).getEventImg()).placeholder(R.drawable.wide_loading_img).error(R.drawable.wide_error_img).into(holder.binding.ivShowImg);
    }

    @Override
    public int getItemCount() {
        return eventImageList.size();
    }

    class GalleryHolder extends RecyclerViewBouncy.ViewHolder implements View.OnClickListener {
        ShowGalleryLayoutBinding binding;
        GalleryHolder(@NonNull ShowGalleryLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setFocusable(false);
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ImageViewer.getInstance().showImageViewer(context,eventImageList);
        }
    }
}
