package com.ebabu.event365live.homedrawer.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.ebabu.event365live.BR;
import com.ebabu.event365live.R;
import com.ebabu.event365live.bouncerecycler.RecyclerViewBouncy;
import com.ebabu.event365live.databinding.ExploreEventCustomLayoutBinding;
import com.ebabu.event365live.homedrawer.modal.SearchModal;
import com.ebabu.event365live.homedrawer.modal.searchevent.SearchEventModal;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.userinfo.activity.EventDetailsActivity;

import java.util.List;

public class ExploreEventAdapter extends RecyclerViewBouncy.Adapter<ExploreEventAdapter.ExploreHolder> {
    private Context context;
    private ExploreEventCustomLayoutBinding customLayoutBinding;
    private List<SearchModal> searchModalList;

    public ExploreEventAdapter(Context context,List<SearchModal> searchModalList) {
        this.context = context;
        this.searchModalList = searchModalList;
    }

    @NonNull
    @Override
    public ExploreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        customLayoutBinding = DataBindingUtil.inflate(inflater,R.layout.explore_event_custom_layout,parent,false);
        return new ExploreHolder(customLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ExploreHolder holder, int position) {
        SearchModal searchEventData = searchModalList.get(position);
        String name = searchEventData.getName();
        String eventImg = searchEventData.getEventImg();

        holder.customLayoutBinding.tvShowEventName.setText(name);
        Glide.with(context).load(eventImg).into(customLayoutBinding.ivExploreEventImg);

    }

    @Override
    public int getItemCount() {
        return searchModalList.size();
    }

    class ExploreHolder extends RecyclerViewBouncy.ViewHolder implements View.OnClickListener {
        ExploreEventCustomLayoutBinding customLayoutBinding;
        ExploreHolder(@NonNull ExploreEventCustomLayoutBinding customLayoutBinding) {
            super(customLayoutBinding.getRoot());
            this.customLayoutBinding = customLayoutBinding;
            customLayoutBinding.getRoot().setFocusable(false);
            customLayoutBinding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent eventDetailsIntent = new Intent(context, EventDetailsActivity.class);
            eventDetailsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            eventDetailsIntent.putExtra(Constants.ApiKeyName.eventId,searchModalList.get(getAdapterPosition()-1).getId());
            context.startActivity(eventDetailsIntent);
        }
    }

}
