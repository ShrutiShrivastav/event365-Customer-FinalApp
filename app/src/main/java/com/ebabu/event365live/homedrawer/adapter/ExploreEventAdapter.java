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
import com.ebabu.event365live.databinding.CircularProgressBarBinding;
import com.ebabu.event365live.databinding.CustomEventListLayoutBinding;
import com.ebabu.event365live.databinding.ExploreEventCustomLayoutBinding;
import com.ebabu.event365live.holder.ProgressHolder;
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
    private List<SearchEventModal.SearchData> searchDataList;
    private boolean isSearchedData;
    private SearchEventModal.SearchData searchedData;
    private RecyclerView.ViewHolder holder;
    private boolean isLoading;

    public ExploreEventAdapter(List<SearchEventModal.TopEvent> topEventList, List<SearchEventModal.SearchData> searchDataList, boolean isSearchedData) {
        this.topEventList = topEventList;
        this.searchDataList = searchDataList;
        this.isSearchedData = isSearchedData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        if (isSearchedData){
            if(isLoading){
                CircularProgressBarBinding barBinding = DataBindingUtil.inflate(inflater,R.layout.circular_progress_bar,parent,false);
                holder = new ProgressHolder(barBinding);
            }else {
                eventListLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.custom_event_list_layout, parent, false);
                holder = new RectangularHolder(eventListLayoutBinding);
            }
        } else {
            customLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.explore_event_custom_layout, parent, false);
            holder = new GridHolder(customLayoutBinding);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d("bjbjbjb", "onBindViewHolder: "+position);
        if (holder instanceof GridHolder) {
            SearchEventModal.TopEvent topEvent = topEventList.get(position);
            String name = topEvent.getName();
            String eventImg = topEvent.getEventImage().get(0).getEventImg();
            ((GridHolder) holder).customLayoutBinding.tvShowEventName.setText(name);
            Glide.with(context).load(eventImg).into(customLayoutBinding.ivExploreEventImg);

        } else if (holder instanceof RectangularHolder) {
            if (isSearchedData) {
                searchedData = searchDataList.get(position);
                eventListLayoutBinding.tvShowEventName.setText(searchedData.getName());
                if (searchedData.getStartDate() != null) {
                    String startDate = CommonUtils.getCommonUtilsInstance().getDateMonthName(searchedData.getStartDate());
                    String startTime = CommonUtils.getCommonUtilsInstance().getStartEndEventTime(searchedData.getStartDate());

                    eventListLayoutBinding.tvShowEventTime.setText("Starts " + startTime + " - " + CommonUtils.getCommonUtilsInstance().getLeftDaysAndTime(searchedData.getStartDate()));
                    eventListLayoutBinding.btnShowDate.setText(startDate);
                }

                if (searchedData.getAddress() != null) {
                    eventListLayoutBinding.tvShowVenueAdd.setText(searchedData.getAddress().get(0).getVenueAddress());
                } else {
                    eventListLayoutBinding.tvShowVenueAdd.setVisibility(View.GONE);
                }
                Glide.with(context).load(searchedData.getEventImage().get(0).getEventImg()).placeholder(R.drawable.wide_loading_img).error(R.drawable.wide_error_img).into(eventListLayoutBinding.ivShowEventPhoto);
            }
        }
    }

    @Override
    public int getItemCount() {
        Log.d("fnkanfla", topEventList.size()+" getItemCount: "+isSearchedData);
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
            navigateToDetails(getAdapterPosition());
        }
    }

    class RectangularHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CustomEventListLayoutBinding eventListLayoutBinding;

        public RectangularHolder(@NonNull CustomEventListLayoutBinding eventListLayoutBinding) {
            super(eventListLayoutBinding.getRoot());
            this.eventListLayoutBinding = eventListLayoutBinding;
            eventListLayoutBinding.eventListContainer.setFocusable(false);
            eventListLayoutBinding.eventListContainer.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Log.d("asfmnkasnf", "onClick: "+getAdapterPosition());
            navigateToDetails(getAdapterPosition());
        }
    }

    private void navigateToDetails(int position){
        Intent eventDetailsIntent = new Intent(context, EventDetailsActivity.class);
        eventDetailsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        eventDetailsIntent.putExtra(Constants.ApiKeyName.eventId, isSearchedData ? searchDataList.get(position).getId() : topEventList.get(position).getId());
        eventDetailsIntent.putExtra(Constants.ApiKeyName.eventImg, isSearchedData ? searchDataList.get(position).getEventImage().get(0).getEventImg() : topEventList.get(position).getEventImage().get(0).getEventImg());
        context.startActivity(eventDetailsIntent);
    }



    public void isLoading(boolean isLoading){
        this.isLoading = isLoading;
        notifyDataSetChanged();
    }
}
