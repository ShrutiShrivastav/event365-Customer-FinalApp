package com.ebabu.event365live.userinfo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.bouncerecycler.RecyclerViewBouncy;
import com.ebabu.event365live.databinding.SeeReviewLayoutBinding;
import com.ebabu.event365live.userinfo.modal.eventdetailsmodal.Review;
import com.ebabu.event365live.userinfo.modal.seemore.SeeMoreData;
import com.ebabu.event365live.utils.CommonUtils;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsHolder>{

    private Context context;
    private SeeReviewLayoutBinding seeReviewLayoutBinding;
    private List<Review> reviewsList;
    private boolean showFullList;
    private List<SeeMoreData> seeMoreDataList;

    public ReviewsAdapter(Context context,List<Review> reviewsList,List<SeeMoreData> seeMoreDataList,boolean showFullList) {
        this.showFullList = showFullList;
        this.context = context;
        if(showFullList){
            this.seeMoreDataList = seeMoreDataList;
            return;
        }
        this.reviewsList = reviewsList;

    }

    @NonNull
    @Override
    public ReviewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        seeReviewLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.see_review_layout,parent,false);


        return new ReviewsHolder(seeReviewLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsHolder holder, int position) {
        if(showFullList){
            SeeMoreData moreData = seeMoreDataList.get(position);
            holder.binding.ratingBar.setRating(Float.valueOf(moreData.getReviewStar()));
            holder.binding.tvShowComment.setText(moreData.getReviewText());
            holder.binding.tvReviewedName.setText(moreData.getReviewer().getName());
            Glide.with(context).load(moreData.getReviewer().getProfilePic()).into(holder.binding.ivReviewedImg);
            holder.binding.tvCommentDate.setText(CommonUtils.getCommonUtilsInstance().getDateMonthYearName(moreData.getUpdatedAt(),true));
            return;
        }


        Review review = reviewsList.get(position);
        holder.binding.ratingBar.setRating(Float.valueOf(review.getReviewStar()));
        holder.binding.tvShowComment.setText(review.getReviewText());
        holder.binding.tvReviewedName.setText(review.getReviewer().getName());
        Glide.with(context).load(review.getReviewer().getProfilePic()).into(holder.binding.ivReviewedImg);
        holder.binding.tvCommentDate.setText(CommonUtils.getCommonUtilsInstance().getDateMonthYearName(review.getUpdatedAt(),true));

    }

    @Override
    public int getItemCount() {
        return showFullList? seeMoreDataList.size() : reviewsList.size();
    }

    class ReviewsHolder extends RecyclerView.ViewHolder {
        private SeeReviewLayoutBinding binding;

        ReviewsHolder(@NonNull SeeReviewLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}
