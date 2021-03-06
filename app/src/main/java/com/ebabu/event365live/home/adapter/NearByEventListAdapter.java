package com.ebabu.event365live.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.bouncerecycler.RecyclerViewBouncy;
import com.ebabu.event365live.home.modal.nearbymodal.EventList;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.userinfo.activity.EventDetailsActivity;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.Utility;

import java.util.List;

public class NearByEventListAdapter extends RecyclerView.Adapter<NearByEventListAdapter.NearByHolder> {

    private Context context;
    private boolean isFromLandingActivity;
    private List<EventList> eventList;

    public NearByEventListAdapter(List<EventList> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public NearByEventListAdapter.NearByHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.near_by_event_layout, parent, false);
        return new NearByHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NearByEventListAdapter.NearByHolder holder, int position) {
        EventList event = eventList.get(position);

        if (event.getName() != null) {
            holder.tvShowEventName.setText(Utility.toUpperCase(event.getName()));
        }
        if (event.getStartDate() != null) {
            String startDate = CommonUtils.getCommonUtilsInstance().getDateMonthYearName(event.getStartDate(),false);
            String startTime = CommonUtils.getCommonUtilsInstance().getStartEndEventTime(event.getStartDate());

            String showDate = CommonUtils.getCommonUtilsInstance().getLeftDaysAndTime(event.getStartDate()).equalsIgnoreCase("ongoing") ? "Ongoing" : "Starts " + startTime + " - " + CommonUtils.getCommonUtilsInstance().getLeftDaysAndTime(event.getStartDate());

            holder.tvShowEventTime.setText(showDate);
            holder.btnShowDate.setText(startDate);
        }

        if (event.getVenueEvents() != null) {
            holder.tvShowVenueAdd.setText(event.getVenueEvents().get(0).getVenueAddress());
        } else {
            holder.tvShowVenueAdd.setText(context.getString(R.string.na));
        }

        if(!event.getEventImages().isEmpty()){
            Glide.with(context).load(event.getEventImages().get(0).getEventImage()).placeholder(R.drawable.wide_loading_img).error(R.drawable.wide_error_img).into(holder.ivShowEventPhoto);
        }else  Glide.with(context).load("").placeholder(R.drawable.wide_loading_img).error(R.drawable.wide_error_img).into(holder.ivShowEventPhoto);


    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    class NearByHolder extends RecyclerViewBouncy.ViewHolder implements View.OnClickListener {
        Button btnShowDate;
        ImageView ivShowEventPhoto;
        TextView tvShowEventName, tvShowEventTime, tvShowVenueAdd;

        NearByHolder(@NonNull View itemView) {
            super(itemView);
            btnShowDate = itemView.findViewById(R.id.btnShowDate);
            ivShowEventPhoto = itemView.findViewById(R.id.ivShowEventPhoto);
            tvShowEventName = itemView.findViewById(R.id.tvShowEventName);
            tvShowEventTime = itemView.findViewById(R.id.tvShowEventTime);
            tvShowVenueAdd = itemView.findViewById(R.id.tvShowVenueAdd);
            itemView.findViewById(R.id.eventListContainer).setOnClickListener(this);
            itemView.findViewById(R.id.eventListContainer).setFocusable(false);
        }

        @Override
        public void onClick(View view) {
            context.startActivity(new Intent(context, EventDetailsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(Constants.ApiKeyName.eventImg, eventList.get(getAdapterPosition() - 1).getEventImages().get(0).getEventImage()).putExtra(Constants.ApiKeyName.eventId, eventList.get(getAdapterPosition() - 1).getId()));
        }
    }

}
