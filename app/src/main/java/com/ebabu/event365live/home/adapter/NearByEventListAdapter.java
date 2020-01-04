package com.ebabu.event365live.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
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
import com.ebabu.event365live.oncelaunch.modal.nearbynoauth.NearByNoAuthModal;
import com.ebabu.event365live.userinfo.activity.EventDetailsActivity;
import com.ebabu.event365live.utils.CommonUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NearByEventListAdapter extends RecyclerView.Adapter<NearByEventListAdapter.NearByHolder>{

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
        View view = LayoutInflater.from(context).inflate(R.layout.near_by_event_layout,parent,false);
        return new NearByHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NearByEventListAdapter.NearByHolder holder, int position) {
        EventList event = eventList.get(position);

        if(event.getName() != null ){
            holder.tvShowEventName.setText(event.getName());
        }
        if(event.getStartDate() !=  null){
            String startDate = CommonUtils.getCommonUtilsInstance().getDateMonthName(event.getStartDate());
            String startTime = CommonUtils.getCommonUtilsInstance().getStartEndEventTime(event.getStartDate());
            String endTime = CommonUtils.getCommonUtilsInstance().getStartEndEventTime(event.getStartDate());
            holder.tvShowEventTime.setText(startTime + " - "+endTime);
            holder.btnShowDate.setText(startDate);
        }

        if(event.getVenueEvents()!= null){
            holder.tvShowVenueAdd.setText(event.getVenueEvents().get(0).getVenueAddress());
        }else {
            holder.tvShowVenueAdd.setText(context.getString(R.string.na));
        }

        Glide.with(context).load(event.getEventImages().get(0).getEventImage()).placeholder(R.drawable.wide_loading_img).error(R.drawable.wide_error_img).into(holder.ivShowEventPhoto);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    class NearByHolder extends RecyclerViewBouncy.ViewHolder implements View.OnClickListener {
        Button btnShowDate;
        ImageView ivShowEventPhoto;
        TextView tvShowEventName,tvShowEventTime,tvShowVenueAdd;
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
            context.startActivity(new Intent(context, EventDetailsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(Constants.ApiKeyName.eventImg,eventList.get(getAdapterPosition()-1).getEventImages().get(0).getEventImage()).putExtra(Constants.ApiKeyName.eventId,eventList.get(getAdapterPosition()-1).getId()));
        }
    }

}
