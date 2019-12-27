package com.ebabu.event365live.homedrawer.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.bouncerecycler.RecyclerViewBouncy;
import com.ebabu.event365live.databinding.CustomEventListLayoutBinding;
import com.ebabu.event365live.databinding.ExploreEventCustomLayoutBinding;
import com.ebabu.event365live.homedrawer.modal.searchevent.SearchEventModal;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.userinfo.activity.EventDetailsActivity;
import com.ebabu.event365live.utils.CommonUtils;

import java.util.List;

public class ExploreEventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ExploreEventCustomLayoutBinding customLayoutBinding;
    private CustomEventListLayoutBinding eventListLayoutBinding;
    private List<SearchEventModal.TopEvent> topEventList;
    private List<SearchEventModal.SearchDatum> searchDataList;
    private boolean isSearchedData;
    private SearchEventModal.SearchDatum searchedData;
    private RecyclerView.ViewHolder holder;

    public ExploreEventAdapter(List<SearchEventModal.TopEvent> topEventList, List<SearchEventModal.SearchDatum> searchDataList, boolean isSearchedData) {
        this.topEventList = topEventList;
        this.searchDataList = searchDataList;
        this.isSearchedData = isSearchedData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (isSearchedData) {
            eventListLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.custom_event_list_layout, parent, false);
            holder = new RectangularHolder(eventListLayoutBinding);
        } else {
            customLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.explore_event_custom_layout, parent, false);
            holder = new GridHolder(customLayoutBinding);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof GridHolder) {
            SearchEventModal.TopEvent topEvent = topEventList.get(position);
            String name = topEvent.getName();
            String eventImg = topEvent.getEventImage();
            ((GridHolder) holder).customLayoutBinding.tvShowEventName.setText(name);
            Glide.with(context).load(eventImg).into(customLayoutBinding.ivExploreEventImg);
        } else if (holder instanceof RectangularHolder) {

            if (isSearchedData) {
                searchedData = searchDataList.get(position);
                Glide.with(context).load(searchedData.getEventImage()).into(eventListLayoutBinding.ivShowEventPhoto);
                eventListLayoutBinding.tvShowEventName.setText(searchedData.getName());
                eventListLayoutBinding.tvShowEventTime.setText(CommonUtils.getCommonUtilsInstance().getStartEndEventTime(searchedData.getStartDate()));
                eventListLayoutBinding.tvShowVenueAdd.setText(searchedData.getVenueAddress());
            }


        }


    }

    @Override
    public int getItemCount() {
        Log.d("fnkanfla", "getItemCount: "+isSearchedData);
        return isSearchedData ? searchDataList.size() : topEventList.size();
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
            Intent eventDetailsIntent = new Intent(context, EventDetailsActivity.class);
            eventDetailsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            eventDetailsIntent.putExtra(Constants.ApiKeyName.eventId, isSearchedData ? searchDataList.get(getAdapterPosition()).getId() : topEventList.get(getAdapterPosition()).getId());
            eventDetailsIntent.putExtra(Constants.ApiKeyName.eventImg, isSearchedData ? searchDataList.get(getAdapterPosition()).getEventImage() : topEventList.get(getAdapterPosition()).getEventImage());
            context.startActivity(eventDetailsIntent);
        }
    }

    class RectangularHolder extends RecyclerView.ViewHolder {
        CustomEventListLayoutBinding eventListLayoutBinding;

        public RectangularHolder(@NonNull CustomEventListLayoutBinding eventListLayoutBinding) {
            super(eventListLayoutBinding.getRoot());
            this.eventListLayoutBinding = eventListLayoutBinding;
        }
    }

}
