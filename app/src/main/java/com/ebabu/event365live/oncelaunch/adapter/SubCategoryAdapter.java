package com.ebabu.event365live.oncelaunch.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
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
import com.ebabu.event365live.home.modal.SubCategoryModal;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.userinfo.activity.EventDetailsActivity;
import com.ebabu.event365live.utils.CommonUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.SubCatEventHolder>{

    private Context context;
    private boolean isFromLandingActivity;
    private List<SubCategoryModal.Event> eventList;
    public SubCategoryAdapter(Context context,List<SubCategoryModal.Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }
    @NonNull
    @Override
    public SubCatEventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_event_list_layout,parent,false);
        return new SubCatEventHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubCatEventHolder holder, int position) {
        SubCategoryModal.Event event = eventList.get(position);
        if(isFromLandingActivity){
            holder.btnShowDate.setBackground(context.getResources().getDrawable(R.drawable.login_round_container));
            holder.btnShowDate.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.itemView.setBackground(context.getResources().getDrawable(android.R.color.transparent));
        }

        if(event.getName() != null ){
            holder.tvShowEventName.setText(event.getName());
        }else
            holder.tvShowEventName.setText(context.getString(R.string.na));

        if(event.getStartTime() !=  null){
            holder.tvShowEventTime.setText(getStartEndEventTime(event.getStartTime()));
        }else holder.tvShowEventTime.setText(context.getString(R.string.na));
        if(event.getStartDate() != null)
            holder.btnShowDate.setText(getDateMonthName(event.getStartDate()));
        else holder.btnShowDate.setText(context.getString(R.string.na));
        if(event.getAddress() != null){
            holder.tvShowVenueAdd.setText(CommonUtils.getCommonUtilsInstance().getAddressFromLatLng(context,event.getAddress().get(0).getLatitude(),event.getAddress().get(0).getLongitude()));
        }else {
            holder.tvShowVenueAdd.setText(context.getString(R.string.na));
        }
        if(event.getEventImages().size() >0)
            Glide.with(context).load(event.getEventImages().get(0).getEventImage()).into(holder.ivShowEventPhoto);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    class SubCatEventHolder extends RecyclerViewBouncy.ViewHolder implements View.OnClickListener {
        Button btnShowDate;
        ImageView ivShowEventPhoto;
        TextView tvShowEventName,tvShowEventTime,tvShowVenueAdd;
        SubCatEventHolder(@NonNull View itemView) {
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
            context.startActivity(new Intent(context, EventDetailsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(Constants.ApiKeyName.eventId,eventList.get(getAdapterPosition()-1).getId()));
        }
    }
    private String getStartEndEventTime(String eventTime)
    {
        String formattedTime = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss", Locale.ENGLISH);
            Date dt = sdf.parse(eventTime);

            SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm a",Locale.ENGLISH);
            formattedTime = sdfs.format(dt).toLowerCase();

        } catch (ParseException e) {
            e.printStackTrace();

        }
        return formattedTime;
    }

    private String getDateMonthName(String dateFormat)
    {
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
        }
        return getDate+" "+getMonth;
    }
}
