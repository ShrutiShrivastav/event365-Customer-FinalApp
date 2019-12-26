package com.ebabu.event365live.userinfo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

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

import java.util.List;

public class RelatedEventAdapter extends RecyclerViewBouncy.Adapter<RecyclerViewBouncy.ViewHolder>{

    private Context context;
    private List<RelatedEvent> relatedEvents;
    private LayoutInflater inflater;
    private EventRelatedCustomLayoutBinding customLayoutBinding;
    private CircularProgressBarBinding circularProgressBarBinding;

    private RecyclerViewBouncy.ViewHolder holder;
    private boolean isLoaderVisible = false;

    public RelatedEventAdapter(Context context, List<RelatedEvent> relatedEvents) {
        this.context = context;
        this.relatedEvents = relatedEvents;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerViewBouncy.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case Constants.VIEW_TYPE_NORMAL:
                customLayoutBinding = DataBindingUtil.inflate(inflater,R.layout.event_related_custom_layout, parent, false);
                holder = new RelatedEventHolder(customLayoutBinding);
                break;

            case Constants.VIEW_TYPE_LOADING:
                circularProgressBarBinding = DataBindingUtil.inflate(inflater,R.layout.circular_progress_bar, parent, false);
                holder = new ProgressHolder(circularProgressBarBinding);
                break;
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewBouncy.ViewHolder holder, int position) {
        RelatedEvent eventData = relatedEvents.get(position);
        if(holder instanceof RelatedEventHolder){

            ((RelatedEventHolder) holder).customLayoutBinding.tvShowEventName.setText(eventData.getName());
            String[] getDate = CommonUtils.getCommonUtilsInstance().getSplitMonthDate(eventData.getStartDate()).split(",");
            ((RelatedEventHolder) holder).customLayoutBinding.tvShowDateInNumeric.setText(String.valueOf(getDate[0]));
            ((RelatedEventHolder) holder).customLayoutBinding.tvShowDateInChar.setText(String.valueOf(getDate[1]));
            if(eventData.getEventImages().size() != 0)
                Glide.with(context).load(eventData.getEventImages().get(0).getEventImage()).into(((RelatedEventHolder) holder).customLayoutBinding.ivEventImg);
            if(eventData.getEventAddress() != null)
                ((RelatedEventHolder) holder).customLayoutBinding.tvShowEventAdd.setText(eventData.getEventAddress().get(0).getVenueAddress());
            else {
                ((RelatedEventHolder) holder).customLayoutBinding.tvShowEventAdd.setText(context.getString(R.string.na));
            }
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
            context.startActivity(new Intent(context, EventDetailsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(Constants.ApiKeyName.eventImg,relatedEvents.get(0).getEventImages().get(0).getEventImage()).putExtra(Constants.ApiKeyName.eventId,relatedEvents.get(getAdapterPosition()).getId()));
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
