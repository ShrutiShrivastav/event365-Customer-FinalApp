package com.ebabu.event365live.homedrawer.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.bouncerecycler.RecyclerViewBouncy;
import com.ebabu.event365live.databinding.PastFavoritesCustomLayoutBinding;
import com.ebabu.event365live.homedrawer.modal.pastmodal.Past;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.listener.LikeDislikeListener;
import com.ebabu.event365live.listener.MarkAsFavoriteEventListener;
import com.ebabu.event365live.userinfo.activity.EventDetailsActivity;
import com.ebabu.event365live.utils.CommonUtils;

import java.util.List;

import retrofit2.http.HEAD;



public class PastAdapter extends RecyclerViewBouncy.Adapter<PastAdapter.PastFavoritesHolder> {

    private Context context;
    private DataBindingUtil dataBindingUtil;
    private PastFavoritesCustomLayoutBinding customLayoutBinding;
    private List<Past> pastFavoritesList;

    private LikeDislikeListener likeDislikeListener;

    private MarkAsFavoriteEventListener markAsFavoriteEventListener;

    public PastAdapter(Context context, List<Past> pastFavoritesList) {
        this.context = context;
        this.pastFavoritesList = pastFavoritesList;
        likeDislikeListener = (LikeDislikeListener) context;
        markAsFavoriteEventListener = (MarkAsFavoriteEventListener) context;
    }

    @NonNull
    @Override
    public PastFavoritesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
            customLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.past_favorites_custom_layout, parent, false);
            return new PastFavoritesHolder(customLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PastFavoritesHolder holder, int position) {
        Past pastModal = pastFavoritesList.get(position);
        Glide.with(context).load(pastModal.getEventImages().get(0).getEventImage()).into(holder.binding.ivPastEventImg);
        holder.binding.tvPastEventName.setText(pastModal.getName());
        holder.binding.tvStartPastEventDate.setText(CommonUtils.getCommonUtilsInstance().getDateMonthYearName(pastModal.getStartDate(), false));
        holder.binding.tvEndPastEventDate.setText(CommonUtils.getCommonUtilsInstance().getDateMonthYearName(pastModal.getEndDate(), false));
        holder.binding.tvStartPastEventTime.setText(CommonUtils.getCommonUtilsInstance().getStartEndEventTime(pastModal.getStartTime()));
        holder.binding.tvEndPastEventTime.setText(CommonUtils.getCommonUtilsInstance().getStartEndEventTime(pastModal.getEndTime()));
        if(pastModal.getVenueEvents() != null){
            holder.binding.tvPastEventAdd.setText(CommonUtils.getCommonUtilsInstance().getAddressFromLatLng(context,pastModal.getVenueEvents().get(0).getLatitude(),pastModal.getVenueEvents().get(0).getLongitude()));
        }else
            holder.binding.tvPastEventAdd.setText(context.getString(R.string.na));

    }


    @Override
    public int getItemCount() {
        return pastFavoritesList.size();
    }

    class PastFavoritesHolder extends RecyclerViewBouncy.ViewHolder implements View.OnClickListener {
        PastFavoritesCustomLayoutBinding binding;
        PastFavoritesHolder(@NonNull PastFavoritesCustomLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setFocusable(false);
            //binding.getRoot().setOnClickListener(this);
            binding.getRoot().findViewById(R.id.pastEventContainer).setOnClickListener(this);
            binding.getRoot().findViewById(R.id.ivMarkEventFavorite).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.pastEventContainer:
                    Intent detailsIntent = new Intent(context, EventDetailsActivity.class);
                    detailsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    detailsIntent.putExtra(Constants.ApiKeyName.eventId,pastFavoritesList.get(getAdapterPosition()-1).getId());
                    context.startActivity(detailsIntent);
                    break;
                case R.id.ivMarkEventFavorite:
                    likeDislikeListener.eventLikeListener(pastFavoritesList.get(getAdapterPosition()-1).getId());
                    markAsFavoriteEventListener.eventFavMarkListener(pastFavoritesList.get(getAdapterPosition()-1).getId());
                    break;
            }


        }
    }


}
