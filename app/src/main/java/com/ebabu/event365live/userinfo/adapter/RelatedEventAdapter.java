package com.ebabu.event365live.userinfo.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.bouncerecycler.RecyclerViewBouncy;
import com.ebabu.event365live.databinding.EventRelatedCustomLayoutBinding;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.userinfo.activity.EventDetailsActivity;
import com.ebabu.event365live.userinfo.modal.eventdetailsmodal.RelatedEvent;
import com.ebabu.event365live.utils.CommonUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RelatedEventAdapter extends RecyclerViewBouncy.Adapter<RelatedEventAdapter.RelatedEventHolder>{

    private Context context;
    private List<RelatedEvent> relatedEvents;
    private LayoutInflater inflater;
    private EventRelatedCustomLayoutBinding customLayoutBinding;

    public RelatedEventAdapter(Context context, List<RelatedEvent> relatedEvents) {
        this.context = context;
        this.relatedEvents = relatedEvents;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RelatedEventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        customLayoutBinding = DataBindingUtil.inflate(inflater,R.layout.event_related_custom_layout, parent, false);
        return new RelatedEventHolder(customLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RelatedEventHolder holder, int position) {
        RelatedEvent eventData = relatedEvents.get(position);
        holder.customLayoutBinding.tvShowEventName.setText(eventData.getName());
        String[] getDate = CommonUtils.getCommonUtilsInstance().getSplitMonthDate(eventData.getStartDate()).split(",");
        holder.customLayoutBinding.tvShowDateInNumeric.setText(String.valueOf(getDate[0]));
        holder.customLayoutBinding.tvShowDateInChar.setText(String.valueOf(getDate[1]));
        if(eventData.getEventImages().size() != 0)
        Glide.with(context).load(eventData.getEventImages().get(0).getEventImage()).into(holder.customLayoutBinding.ivEventImg);
        if(eventData.getEventAddress() != null)
            holder.customLayoutBinding.tvShowEventAdd.setText(eventData.getEventAddress().get(0).getVenueAddress());
        else {
            holder.customLayoutBinding.tvShowEventAdd.setText(context.getString(R.string.na));
        }
    }

    @Override
    public int getItemCount() {
        return relatedEvents.size();
    }

    class RelatedEventHolder extends RecyclerViewBouncy.ViewHolder implements View.OnClickListener {
        EventRelatedCustomLayoutBinding customLayoutBinding;
        public RelatedEventHolder(@NonNull EventRelatedCustomLayoutBinding customLayoutBinding) {
            super(customLayoutBinding.getRoot());
            this.customLayoutBinding = customLayoutBinding;
            customLayoutBinding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            context.startActivity(new Intent(context, EventDetailsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(Constants.ApiKeyName.eventImg,relatedEvents.get(0).getEventImages().get(0).getEventImage()).putExtra(Constants.ApiKeyName.eventId,relatedEvents.get(getAdapterPosition()-1).getId()));
        }
    }
    private String getSplitMonthDate(String dateFormat)
    {
        int getDate = 0;
        String getMonth = "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
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
        return getDate+","+getMonth;
    }
}
