package com.ebabu.event365live.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.bouncerecycler.RecyclerViewBouncy;
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

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ListEventHolder>{

    private Context context;
    private boolean isFromLandingActivity;
    private List<NearByNoAuthModal.EventList> eventList;
    public EventListAdapter(Context context, boolean isFromLandingActivity,List<NearByNoAuthModal.EventList> eventList) {
        this.context = context;
        this.eventList = eventList;
        this.isFromLandingActivity = isFromLandingActivity;
    }

    @NonNull
    @Override
    public EventListAdapter.ListEventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_event_list_layout,parent,false);
         return new ListEventHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull EventListAdapter.ListEventHolder holder, int position) {
        NearByNoAuthModal.EventList event = eventList.get(position);

        if(isFromLandingActivity){
            holder.btnShowDate.setBackground(context.getResources().getDrawable(R.drawable.login_round_container));
            holder.btnShowDate.setTextColor(context.getResources().getColor(R.color.colorPrimary));
//            holder.itemView.setBackground(context.getResources().getDrawable(android.R.color.transparent));
//            holder.tvShowEventName.setTextColor(context.getResources().getColor(R.color.colorWhite));
//            holder.tvShowEventTime.setTextColor(context.getResources().getColor(R.color.colorWhite));
//            holder.tvShowVenueAdd.setTextColor(context.getResources().getColor(R.color.colorWhite));
        }

        if(event.getName() != null ){
            holder.tvShowEventName.setText(event.getName());
        }
        if(event.getStartTime() !=  null){
            holder.tvShowEventTime.setText(getStartEndEventTime(event.getStartTime()));
        }
        if(event.getStartDate() != null)
            holder.btnShowDate.setText(getDateMonthName(event.getStartDate()));
        if(event.getAddress()!= null){
            holder.tvShowVenueAdd.setText(event.getAddress().get(0).getVenueAddress());
        }else {
            holder.tvShowVenueAdd.setVisibility(View.GONE);
        }

        if(event.getEventImages().size() >0)
            Glide.with(context).load(event.getEventImages().get(0).getEventImage()).into(holder.ivShowEventPhoto);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    class ListEventHolder extends RecyclerViewBouncy.ViewHolder implements View.OnClickListener {
        Button btnShowDate;
        ImageView ivShowEventPhoto;
        TextView tvShowEventName,tvShowEventTime,tvShowVenueAdd;
        ListEventHolder(@NonNull View itemView) {
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
            context.startActivity(new Intent(context, EventDetailsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(Constants.ApiKeyName.eventId,eventList.get(getAdapterPosition()-1).getId()));
        }
    }

    private String getStartEndEventTime(String eventTime)
    {
        String formattedTime = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss",Locale.ENGLISH);
            Date dt = sdf.parse(eventTime);

            SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm a",Locale.ENGLISH);
            formattedTime = sdfs.format(dt).toLowerCase();

        } catch (ParseException e) {
            e.printStackTrace();

        }
        return formattedTime;
    }

    private String getDateMonthName(String dateFormat){
        int getDate = 0;
        String getMonth = "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy",Locale.ENGLISH);
            Date date = inputFormat.parse(dateFormat);
            Calendar calendar = outputFormat.getCalendar();
            calendar.setTime(date);
            getDate = calendar.get(Calendar.DATE);
            getMonth = (String) DateFormat.format("MMM",date);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("sfsafavfdhdhdss", "ParseException: "+e.getMessage());
        }
        return getDate+" "+getMonth;
    }

}
