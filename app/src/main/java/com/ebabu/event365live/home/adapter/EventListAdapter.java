package com.ebabu.event365live.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.bouncerecycler.RecyclerViewBouncy;
import com.ebabu.event365live.databinding.CircularProgressBarBinding;
import com.ebabu.event365live.holder.ProgressHolder;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.oncelaunch.modal.nearbynoauth.NearByNoAuthModal;
import com.ebabu.event365live.userinfo.activity.EventDetailsActivity;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.GlobalBus;

import java.util.List;

public class EventListAdapter extends RecyclerView.Adapter<RecyclerViewBouncy.ViewHolder> {

    private Context context;
    private boolean isFromLandingActivityOrSearch;
    private List<NearByNoAuthModal.EventList> eventList;
    private RecyclerViewBouncy.ViewHolder holder;
    private boolean isLoaderVisible = false;

    public EventListAdapter(Context context, boolean isFromLandingActivityOrSearch, List<NearByNoAuthModal.EventList> eventList) {
        this.context = context;
        this.eventList = eventList;
        this.isFromLandingActivityOrSearch = isFromLandingActivityOrSearch;
    }

    @NonNull
    @Override
    public RecyclerViewBouncy.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case Constants.VIEW_TYPE_NORMAL:
                View view = LayoutInflater.from(context).inflate(R.layout.custom_event_list_layout, parent, false);
                holder = new ListEventHolder(view);
                break;

            case Constants.VIEW_TYPE_LOADING:
                CircularProgressBarBinding circularProgressBarBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.circular_progress_bar, parent, false);
                holder = new ProgressHolder(circularProgressBarBinding);
                break;
        }

        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerViewBouncy.ViewHolder holder, int position) {
        NearByNoAuthModal.EventList event = eventList.get(position);
        Log.d("nfkalsnklfas", event.getEndDate() + " onBindViewHolder: " + event.getStartDate());

        if (holder instanceof ListEventHolder) {
            if (isFromLandingActivityOrSearch) {
                ((ListEventHolder) holder).btnShowDate.setBackground(context.getResources().getDrawable(R.drawable.login_round_container));
                //Glide.with(context).load(R.drawable.market_gradient).into(((ListEventHolder) holder).ivShowMarkerIcon);
                ((ListEventHolder) holder).ivShowMarkerIcon.setImageResource(R.drawable.white_marker_iconn);
            } else {
                ((ListEventHolder) holder).ivShowMarkerIcon.setImageResource(R.drawable.market_gradient);
            }

            if (event.getName() != null) {
                ((ListEventHolder) holder).tvShowEventName.setText(event.getName());
                ((ListEventHolder) holder).tvShowEventName.setTextColor(isFromLandingActivityOrSearch ? Color.WHITE : Color.BLACK);
            }
            if (event.getStartDate() != null) {
                String startDate = CommonUtils.getCommonUtilsInstance().getDateMonthName(event.getStartDate(), false);
                String startTime = CommonUtils.getCommonUtilsInstance().getStartEndEventTime(event.getStartDate());
                String endTime = CommonUtils.getCommonUtilsInstance().getStartEndEventTime(event.getEndDate());

                String showDate = CommonUtils.getCommonUtilsInstance().getLeftDaysAndTime(event.getStartDate()).equalsIgnoreCase("ongoing") ? "Ongoing" : "Starts " + startTime + " - " + CommonUtils.getCommonUtilsInstance().getLeftDaysAndTime(event.getStartDate());
                ((ListEventHolder) holder).tvShowEventTime.setText(showDate);
                ((ListEventHolder) holder).tvShowEventTime.setTextColor(isFromLandingActivityOrSearch ? Color.WHITE : Color.BLACK);
                ((ListEventHolder) holder).btnShowDate.setText(startDate);
                ((ListEventHolder) holder).btnShowDate.setTextColor(isFromLandingActivityOrSearch ? context.getResources().getColor(R.color.colorPrimary) : context.getResources().getColor(R.color.white));
            }

            if (event.getAddress() != null) {
                ((ListEventHolder) holder).tvShowVenueAdd.setText(event.getAddress().get(0).getVenueAddress());
                ((ListEventHolder) holder).tvShowVenueAdd.setTextColor(isFromLandingActivityOrSearch ? Color.WHITE : Color.BLACK);
            } else {
                ((ListEventHolder) holder).tvShowVenueAdd.setVisibility(View.GONE);
            }
            Glide.with(context).load(event.getEventImages().get(0).getEventImage()).placeholder(R.drawable.wide_loading_img).error(R.drawable.wide_error_img).into(((ListEventHolder) holder).ivShowEventPhoto);
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    class ListEventHolder extends RecyclerViewBouncy.ViewHolder implements View.OnClickListener {
        Button btnShowDate;
        ImageView ivShowEventPhoto, ivShowMarkerIcon;
        TextView tvShowEventName, tvShowEventTime, tvShowVenueAdd;

        ListEventHolder(@NonNull View itemView) {
            super(itemView);
            btnShowDate = itemView.findViewById(R.id.btnShowDate);
            ivShowEventPhoto = itemView.findViewById(R.id.ivShowEventPhoto);
            tvShowEventName = itemView.findViewById(R.id.tvShowEventName);
            tvShowEventTime = itemView.findViewById(R.id.tvShowEventTime);
            tvShowVenueAdd = itemView.findViewById(R.id.tvShowVenueAdd);
            ivShowMarkerIcon = itemView.findViewById(R.id.ivShowMarkerIcon);
            itemView.findViewById(R.id.eventListContainer).setOnClickListener(this);
            itemView.findViewById(R.id.eventListContainer).setFocusable(false);
        }

        @Override
        public void onClick(View view) {
            context.startActivity(new Intent(context, EventDetailsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(Constants.ApiKeyName.eventId, eventList.get(getAdapterPosition() - 1).getId()));
            CommonUtils.getCommonUtilsInstance().getLeftDaysAndTime(eventList.get(getAdapterPosition() - 1).getStartDate());
            Log.d("fsanfklsa", "getLeftDaysAndTime: " + eventList.get(getAdapterPosition() - 1).getName());

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == eventList.size() - 1 ? Constants.VIEW_TYPE_LOADING : Constants.VIEW_TYPE_NORMAL;
        } else {
            return Constants.VIEW_TYPE_NORMAL;
        }
    }

    public void setLoading(boolean isLoading) {
        this.isLoaderVisible = isLoading;
    }

    public void stopLoading(boolean stop) {
        this.isFromLandingActivityOrSearch = stop;
    }

}




