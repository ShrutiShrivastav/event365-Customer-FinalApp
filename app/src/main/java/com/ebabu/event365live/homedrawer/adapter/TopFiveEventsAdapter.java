package com.ebabu.event365live.homedrawer.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ExploreEventCustomLayoutBinding;
import com.ebabu.event365live.homedrawer.modal.searchevent.SearchEventModal;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.userinfo.activity.EventDetailsActivity;

import java.util.List;

public class TopFiveEventsAdapter extends RecyclerView.Adapter<TopFiveEventsAdapter.GridHolder>{

    private Context context;
    private ExploreEventCustomLayoutBinding customLayoutBinding;
    private List<SearchEventModal.TopEvent> topEventList;

    public TopFiveEventsAdapter(List<SearchEventModal.TopEvent> topEventList) {
        this.topEventList = topEventList;
    }

    @NonNull
    @Override
    public GridHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        customLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.explore_event_custom_layout, parent, false);
        return new GridHolder(customLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull GridHolder holder, int position) {
        SearchEventModal.TopEvent topEvent = topEventList.get(position);
        String name = topEvent.getName();
        holder.customLayoutBinding.tvShowEventName.setText(name);
        if(!topEvent.getEventImage().isEmpty()){
            Glide.with(context).load(topEvent.getEventImage().get(0).getEventImg()).into(customLayoutBinding.ivExploreEventImg);
        }else Glide.with(context).load("").into(customLayoutBinding.ivExploreEventImg);
    }

    @Override
    public int getItemCount() {
        return topEventList.size();
    }

    class GridHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ExploreEventCustomLayoutBinding customLayoutBinding;

        GridHolder(@NonNull ExploreEventCustomLayoutBinding customLayoutBinding) {
            super(customLayoutBinding.getRoot());
            this.customLayoutBinding = customLayoutBinding;
            customLayoutBinding.getRoot().setFocusable(false);
            customLayoutBinding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            navigateToDetails(getAdapterPosition());
        }
    }

    private void navigateToDetails(int position) {
        Intent eventDetailsIntent = new Intent(context, EventDetailsActivity.class);
        eventDetailsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        eventDetailsIntent.putExtra(Constants.ApiKeyName.eventId, topEventList.get(position).getId());
        eventDetailsIntent.putExtra(Constants.ApiKeyName.eventImg, topEventList.get(position).getEventImage().get(0).getEventImg());
        context.startActivity(eventDetailsIntent);
    }

}
