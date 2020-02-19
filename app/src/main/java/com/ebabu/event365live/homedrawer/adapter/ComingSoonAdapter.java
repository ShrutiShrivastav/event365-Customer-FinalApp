package com.ebabu.event365live.homedrawer.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.bouncerecycler.RecyclerViewBouncy;
import com.ebabu.event365live.databinding.PastFavoritesCustomLayoutBinding;
import com.ebabu.event365live.homedrawer.fragment.ComingSoonFragment;
import com.ebabu.event365live.homedrawer.modal.pastmodal.ComingSoon;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.listener.LikeDislikeListener;

import com.ebabu.event365live.listener.MarkAsFavoriteEventListener;

import com.ebabu.event365live.userinfo.activity.EventDetailsActivity;
import com.ebabu.event365live.utils.CommonUtils;

import java.util.List;

public class ComingSoonAdapter extends RecyclerViewBouncy.Adapter<ComingSoonAdapter.ComingSoonHolder> {

    private Context context;
    private List<ComingSoon> comingSoonList;
    private ComingSoonFragment comingSoonFragment;

    private MarkAsFavoriteEventListener markAsFavoriteEventListener;
    public ComingSoonAdapter(List<ComingSoon> comingSoonList, ComingSoonFragment comingSoonFragment) {
        this.comingSoonList = comingSoonList;
        this.comingSoonFragment = comingSoonFragment;
    }

    @NonNull
    @Override
    public ComingSoonHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        markAsFavoriteEventListener = (MarkAsFavoriteEventListener) context;
        LayoutInflater inflater = LayoutInflater.from(context);
        PastFavoritesCustomLayoutBinding pastFavoritesCustomLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.past_favorites_custom_layout, parent, false);
        return new ComingSoonHolder(pastFavoritesCustomLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ComingSoonHolder holder, int position) {
        ComingSoon comingSoon = comingSoonList.get(position);
        Log.d("nalfnlanfklanl", "onBindViewHolder: "+comingSoon.getEventImages().get(0).getEventImage());

        Glide.with(context).load(comingSoon.getEventImages().get(0).getEventImage()).placeholder(R.drawable.wide_loading_img).error(R.drawable.wide_error_img).into(holder.binding.ivPastEventImg);
        holder.binding.tvPastEventName.setText(comingSoon.getName());
        holder.binding.tvPastEventName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        holder.binding.tvStartPastEventDate.setText(CommonUtils.getCommonUtilsInstance().getDateMonthYearName(comingSoon.getStartDate(), false));
        holder.binding.tvEndPastEventDate.setText(CommonUtils.getCommonUtilsInstance().getDateMonthYearName(comingSoon.getEndDate(), false));
        holder.binding.tvStartPastEventTime.setText(CommonUtils.getCommonUtilsInstance().getStartEndEventTime(comingSoon.getStartDate()));
        holder.binding.tvEndPastEventTime.setText(CommonUtils.getCommonUtilsInstance().getStartEndEventTime(comingSoon.getEndDate()));
        if(comingSoon.getVenueEvents() != null){
            holder.binding.tvPastEventAdd.setText(comingSoon.getVenueEvents().get(0).getVenueAddress());
        }else
            holder.binding.tvPastEventAdd.setText(context.getString(R.string.na));
    }

    @Override
    public int getItemCount() {
        return comingSoonList.size();
    }

    class ComingSoonHolder extends RecyclerViewBouncy.ViewHolder implements View.OnClickListener {
        private PastFavoritesCustomLayoutBinding binding;
        ComingSoonHolder(@NonNull PastFavoritesCustomLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setFocusable(false);
            binding.getRoot().findViewById(R.id.pastEventContainer).setOnClickListener(this);
            binding.getRoot().findViewById(R.id.ivMarkEventFavorite).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.pastEventContainer:
                    Intent detailsIntent = new Intent(context, EventDetailsActivity.class);
                    detailsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    detailsIntent.putExtra(Constants.ApiKeyName.eventId,comingSoonList.get(getAdapterPosition()-1).getId());
                    detailsIntent.putExtra(Constants.ApiKeyName.eventImg,comingSoonList.get(getAdapterPosition()-1).getEventImages().get(0).getEventImage());
                    context.startActivity(detailsIntent);
                    break;

                case R.id.ivMarkEventFavorite:
                    markAsFavoriteEventListener.eventFavMarkListener(comingSoonList.get(getAdapterPosition()-1).getId());
                    comingSoonList.remove(getAdapterPosition()-1);
                    comingSoonFragment.updateList(comingSoonList.size());
                    notifyDataSetChanged();
                    break;
            }

        }
    }


}
