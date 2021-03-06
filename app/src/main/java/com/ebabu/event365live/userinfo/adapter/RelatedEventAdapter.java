package com.ebabu.event365live.userinfo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.bouncerecycler.RecyclerViewBouncy;
import com.ebabu.event365live.databinding.CircularProgressBarBinding;
import com.ebabu.event365live.databinding.EventRelatedCustomLayoutBinding;
import com.ebabu.event365live.holder.ProgressHolder;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.userinfo.activity.EventDetailsActivity;
import com.ebabu.event365live.userinfo.modal.eventdetailsmodal.RelatedEvent;
import com.ebabu.event365live.utils.CommonUtils;

import java.util.LinkedList;
import java.util.List;

public class RelatedEventAdapter extends RecyclerView.Adapter<RecyclerViewBouncy.ViewHolder> {

    private Context context;
    private LinkedList<RelatedEvent> relatedEvents;
    private LayoutInflater inflater;
    private EventRelatedCustomLayoutBinding customLayoutBinding;
    private CircularProgressBarBinding circularProgressBarBinding;

    private RecyclerView.ViewHolder holder;
    private boolean isLoaderVisible = false;

    public RelatedEventAdapter(Context context, LinkedList<RelatedEvent> relatedEvents) {
        this.context = context;
        this.relatedEvents = relatedEvents;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerViewBouncy.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case Constants.VIEW_TYPE_NORMAL:
                customLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.event_related_custom_layout, parent, false);
                holder = new RelatedEventHolder(customLayoutBinding);
                break;

            case Constants.VIEW_TYPE_LOADING:
                circularProgressBarBinding = DataBindingUtil.inflate(inflater, R.layout.circular_progress_bar, parent, false);
                holder = new ProgressHolder(circularProgressBarBinding);
                break;
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RelatedEvent eventData = relatedEvents.get(position);
        if (holder instanceof RelatedEventHolder) {
            ((RelatedEventHolder) holder).customLayoutBinding.tvShowEventName.setText(eventData.getName());
            String[] getDate = CommonUtils.getCommonUtilsInstance().getDateMonthYearName(eventData.getStartDate(), false).split(" ");
            ((RelatedEventHolder) holder).customLayoutBinding.tvShowDateInNumeric.setText(String.valueOf(getDate[0]));
            ((RelatedEventHolder) holder).customLayoutBinding.tvShowDateInChar.setText(String.valueOf(getDate[1]));

            if (!eventData.getEventImages().isEmpty()) {
                Glide.with(context).load(eventData.getEventImages().get(0).getEventImage()).placeholder(R.drawable.wide_loading_img).error(R.drawable.wide_error_img).into(((RelatedEventHolder) holder).customLayoutBinding.ivEventImg);
            } else
                Glide.with(context).load("").placeholder(R.drawable.wide_loading_img).error(R.drawable.wide_error_img).into(((RelatedEventHolder) holder).customLayoutBinding.ivEventImg);

            if (eventData.getStartDate() != null) {
                String startDate = CommonUtils.getCommonUtilsInstance().getDateMonthYearName(eventData.getStartDate(), false);
                String startTime = CommonUtils.getCommonUtilsInstance().getStartEndEventTime(eventData.getStartDate());
                ((RelatedEventHolder) holder).customLayoutBinding.tvShowEventAdd.setText("Starts " + startTime + " - " + CommonUtils.getCommonUtilsInstance().getLeftDaysAndTime(eventData.getStartDate()));
            } else {
                ((RelatedEventHolder) holder).customLayoutBinding.tvShowEventAdd.setText(context.getString(R.string.na));
            }
        }
    }

    @Override
    public int getItemCount() {
        return relatedEvents.size();
    }

    class RelatedEventHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        EventRelatedCustomLayoutBinding customLayoutBinding;

        RelatedEventHolder(@NonNull EventRelatedCustomLayoutBinding customLayoutBinding) {
            super(customLayoutBinding.getRoot());
            this.customLayoutBinding = customLayoutBinding;
            customLayoutBinding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent eventIntent = new Intent(context, EventDetailsActivity.class);
            eventIntent.putExtra(Constants.ApiKeyName.eventId, relatedEvents.get(getAdapterPosition()).getId());
            context.startActivity(eventIntent);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == relatedEvents.size() - 1 ? Constants.VIEW_TYPE_LOADING : Constants.VIEW_TYPE_NORMAL;
        } else {
            return Constants.VIEW_TYPE_NORMAL;
        }
    }

}
