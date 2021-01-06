package com.ebabu.event365live.homedrawer.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.bouncerecycler.RecyclerViewBouncy;
import com.ebabu.event365live.databinding.PastFavoritesCustomLayoutBinding;
import com.ebabu.event365live.homedrawer.fragment.PastFragment;
import com.ebabu.event365live.homedrawer.modal.pastmodal.Past;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.listener.MarkAsFavoriteEventListener;
import com.ebabu.event365live.userinfo.activity.EventDetailsActivity;
import com.ebabu.event365live.utils.CommonUtils;

import java.util.List;


public class PastAdapter extends RecyclerViewBouncy.Adapter<PastAdapter.PastFavoritesHolder> {

    private Context context;
    private DataBindingUtil dataBindingUtil;
    private PastFavoritesCustomLayoutBinding customLayoutBinding;
    private List<Past> pastFavoritesList;
    private PastFragment pastFragment;


    private MarkAsFavoriteEventListener markAsFavoriteEventListener;

    public PastAdapter(Context context, List<Past> pastFavoritesList , PastFragment pastFragment) {
        this.context = context;
        this.pastFragment = pastFragment;
        this.pastFavoritesList = pastFavoritesList;
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
        if(!pastModal.getEventImages().isEmpty()){
            Glide.with(context).load(pastModal.getEventImages().get(0).getEventImage()).placeholder(R.drawable.wide_loading_img).error(R.drawable.wide_error_img).into(holder.binding.ivPastEventImg);
        } else Glide.with(context).load("").placeholder(R.drawable.wide_loading_img).error(R.drawable.wide_error_img).into(holder.binding.ivPastEventImg);
       String startTime = CommonUtils.getCommonUtilsInstance().getStartEndEventTime(pastModal.getStartDate());
       String endTime = CommonUtils.getCommonUtilsInstance().getStartEndEventTime(pastModal.getEndDate());
        holder.binding.tvPastEventName.setText(pastModal.getName());
        holder.binding.tvPastEventName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        holder.binding.tvStartPastEventDate.setText(CommonUtils.getCommonUtilsInstance().getDateMonthYearName(pastModal.getStartDate(), true));
        holder.binding.tvEndPastEventDate.setText(CommonUtils.getCommonUtilsInstance().getDateMonthYearName(pastModal.getEndDate(), true));
        holder.binding.tvStartPastEventTime.setText(startTime+" - "+ endTime);
        holder.binding.tvEndPastEventTime.setText(startTime+" - "+ endTime);
        if(pastModal.getAddresses() != null){
            holder.binding.tvPastEventAdd.setText(pastModal.getAddresses().get(0).getVenueAddress());
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
                    detailsIntent.putExtra(Constants.ApiKeyName.eventImg,pastFavoritesList.get(getAdapterPosition()-1).getEventImages().get(0).getEventImage());
                    context.startActivity(detailsIntent);
                    break;
                case R.id.ivMarkEventFavorite:
                    markAsFavoriteEventListener.eventFavMarkListener(pastFavoritesList.get(getAdapterPosition()-1).getId());
                    pastFavoritesList.remove(getAdapterPosition()-1);
                    pastFragment.updateList(pastFavoritesList.size());
                    notifyDataSetChanged();
                    break;
            }


        }
    }




}
