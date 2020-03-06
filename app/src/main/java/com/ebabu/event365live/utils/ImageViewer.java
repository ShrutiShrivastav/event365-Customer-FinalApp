package com.ebabu.event365live.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.LayoutImageViewerBinding;
import com.ebabu.event365live.userinfo.modal.GetAllGalleryImgModal;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ImageViewer {

    private static ImageViewer imageViewer;

    private ImageViewer() {
    }

    public static ImageViewer getInstance() {
        return imageViewer == null ? imageViewer = new ImageViewer() : imageViewer;
    }

    private AlertDialog dialog;

    public void showImageViewer(Context context, List<GetAllGalleryImgModal> images, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutImageViewerBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_image_viewer, null, false);
        builder.setView(binding.getRoot());
        dialog = builder.create();
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(dialog.getWindow().getAttributes());
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.show();
       // dialog.getWindow().setAttributes(lp);
        if(dialog.getWindow() != null)
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        binding.closeIcon.setOnClickListener(v -> dialog.dismiss());
        binding.viewpager.setAdapter(new CustomPagerAdapter(context, images));
        binding.viewpager.setCurrentItem(position,true);


        binding.tab.setupWithViewPager(binding.viewpager, true);
    }

    public static class CustomPagerAdapter extends PagerAdapter {
        private Context mContext;
        List<GetAllGalleryImgModal> images;

        CustomPagerAdapter(Context context, List<GetAllGalleryImgModal> images) {
            mContext = context;
            this.images = images;
        }

        @NotNull
        @Override
        public Object instantiateItem(@NotNull ViewGroup collection, int position) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.layout_pager_image, collection, false);
            ImageView img = layout.findViewById(R.id.img);
            Glide.with(mContext).load(images.get(position).getEventImg()).placeholder(R.drawable.tall_loading_img).error(R.drawable.tall_error_img).into(img);
            collection.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, @NotNull Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(@NotNull View view, @NotNull Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }

    }


}
