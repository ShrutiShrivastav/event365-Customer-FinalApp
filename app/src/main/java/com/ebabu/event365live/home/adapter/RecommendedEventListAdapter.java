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
import com.ebabu.event365live.home.modal.GetRecommendedModal;
import com.ebabu.event365live.home.modal.SubCategoryModal;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.userinfo.activity.EventDetailsActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecommendedEventListAdapter extends RecyclerView.Adapter<RecommendedEventListAdapter.EventListHolder> {

    private Context context;
    private boolean isFromLandingActivity;
    private List<SubCategoryModal.Event> eventList;
    private List<GetRecommendedModal.EventList> eventRecommendedList;
    private SubCategoryModal.Event event;
    GetRecommendedModal.EventList recommendedList;

    public RecommendedEventListAdapter(boolean isFromLandingActivity, List<SubCategoryModal.Event> eventList, List<GetRecommendedModal.EventList> eventRecommendedList) {
        this.isFromLandingActivity = isFromLandingActivity;
        this.eventList = eventList;
        this.eventRecommendedList = eventRecommendedList;
    }

    @NonNull
    @Override
    public EventListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.custom_event_list_layout, parent, false);
        return new EventListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventListHolder holder, int position) {


        if (isFromLandingActivity) {
            event = eventList.get(position);
            holder.btnShowDate.setBackground(context.getResources().getDrawable(R.drawable.login_round_container));
            holder.btnShowDate.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.itemView.setBackground(context.getResources().getDrawable(android.R.color.transparent));
        }
        else {
            recommendedList = eventRecommendedList.get(position);
        }
        String name = isFromLandingActivity ? event.getName() : recommendedList.getName();
        String startTime = isFromLandingActivity ? getStartEndEventTime(event.getStartTime()) : getStartEndEventTime(recommendedList.getStartTime());
        String startDate = isFromLandingActivity ? getDateMonthName(event.getStartDate()) : getDateMonthName(recommendedList.getStartDate());
        String address = isFromLandingActivity ? event.getAddress().get(0).getVenueAddress() : recommendedList.getAddress().get(0).getVenueAddress();
        String img = isFromLandingActivity ? event.getEventImages().get(0).getEventImage() : recommendedList.getEventImages().get(0).getEventImage();

        Glide.with(context).load(img).into(holder.ivShowEventPhoto);
        holder.tvShowEventName.setText(name);
        holder.tvShowEventTime.setText(startTime);
        holder.btnShowDate.setText(startDate);
        holder.tvShowVenueAdd.setText(address != null ? address : context.getString(R.string.na));

    }

    @Override
    public int getItemCount() {
        return isFromLandingActivity ? eventList.size() : eventRecommendedList.size() ;
    }

    class EventListHolder extends RecyclerViewBouncy.ViewHolder implements View.OnClickListener {
        Button btnShowDate;
        ImageView ivShowEventPhoto;
        TextView tvShowEventName, tvShowEventTime, tvShowVenueAdd;

        EventListHolder(@NonNull View itemView) {
            super(itemView);
            btnShowDate = itemView.findViewById(R.id.btnShowDate);
            ivShowEventPhoto = itemView.findViewById(R.id.ivShowEventPhoto);
            tvShowEventName = itemView.findViewById(R.id.tvShowEventName);
            tvShowEventTime = itemView.findViewById(R.id.tvShowEventTime);
            tvShowVenueAdd = itemView.findViewById(R.id.tvShowVenueAdd);
            itemView.findViewById(R.id.eventListContainer).setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            context.startActivity(new Intent(context, EventDetailsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(Constants.ApiKeyName.eventImg,isFromLandingActivity ? eventList.get(getAdapterPosition()-1).getEventImages().get(0).getEventImage() : eventRecommendedList.get(getAdapterPosition()-1).getEventImages().get(0).getEventImage()).putExtra(Constants.ApiKeyName.eventId, isFromLandingActivity ? eventList.get(getAdapterPosition() - 1).getId() : eventRecommendedList.get(getAdapterPosition()-1).getId()));
        }
    }

    private String getStartEndEventTime(String eventTime) {
        String formattedTime = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss", Locale.ENGLISH);
            Date dt = sdf.parse(eventTime);

            SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            formattedTime = sdfs.format(dt).toLowerCase();

        } catch (ParseException e) {
            e.printStackTrace();

        }
        return formattedTime;
    }

    private String getDateMonthName(String dateFormat) {
        int getDate = 0;
        String getMonth = "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
            Date date = inputFormat.parse(dateFormat);
            Calendar calendar = outputFormat.getCalendar();
            calendar.setTime(date);
            getDate = calendar.get(Calendar.DATE);
            getMonth = (String) DateFormat.format("MMM", date);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("sfsafavfdhdhdss", "ParseException: " + e.getMessage());
        }
        return getDate + " " + getMonth;
    }

}
