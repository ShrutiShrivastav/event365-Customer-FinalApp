package com.ebabu.event365live.homedrawer.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.bouncerecycler.RecyclerViewBouncy;
import com.ebabu.event365live.databinding.UpcomingCustomLayoutBinding;
import com.ebabu.event365live.homedrawer.modal.upcoming.UpcomingEvent;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.userinfo.activity.EventDetailsActivity;
import com.ebabu.event365live.utils.CommonUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

public class UpcomingAdapter extends RecyclerViewBouncy.Adapter<UpcomingAdapter.UpcomingHolder> {

    private Context context;
    private DataBindingUtil dataBindingUtil;
    private List<UpcomingEvent> upcomingEventList;
    private LayoutInflater inflater;

    public UpcomingAdapter(List<UpcomingEvent> upcomingEventList) {
        this.upcomingEventList = upcomingEventList;
    }

    @NonNull
    @Override
    public UpcomingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        inflater = LayoutInflater.from(context);
        UpcomingCustomLayoutBinding customLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.upcoming_custom_layout, parent, false);
        return new UpcomingHolder(customLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingHolder holder, int position) {

        UpcomingEvent upcomingModal = upcomingEventList.get(position);

        if(upcomingModal.getEventImages() != null && !upcomingModal.getEventImages().isEmpty()){
            Glide.with(context).load(upcomingModal.getEventImages().get(0).getEventImage()).placeholder(R.drawable.wide_loading_img).error(R.drawable.wide_error_img).into(holder.binding.ivUpcomingEventImg);
        }else Glide.with(context).load("").placeholder(R.drawable.wide_loading_img).error(R.drawable.wide_error_img).into(holder.binding.ivUpcomingEventImg);
        Glide.with(context).load(upcomingModal.getEventImages().get(0).getEventImage()).placeholder(R.drawable.wide_loading_img).error(R.drawable.wide_error_img).into(holder.binding.ivUpcomingEventImg);
        holder.binding.tvUpcomingEventName.setText(upcomingModal.getName());
        holder.binding.tvUpcomingEventName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        String[] getDate = CommonUtils.getCommonUtilsInstance().getSplitMonthDate(upcomingModal.getStartDate()).split(",");
        holder.binding.tvUpcomingDateInNumeric.setText(getDate[0]);
        holder.binding.tvUpcomingDateInName.setText(getDate[1]);

        if(upcomingModal.getStartDate() !=null){
            String startTime = CommonUtils.getCommonUtilsInstance().getStartEndEventTime(upcomingModal.getStartDate());
            holder.binding.tvUpcomingEventAdd.setText("Starts "+startTime+ " - "+CommonUtils.getCommonUtilsInstance().getLeftDaysAndTime(upcomingModal.getStartDate()));
        }else {
            holder.binding.tvUpcomingEventAdd.setText(context.getString(R.string.na));
        }

    }

    @Override
    public int getItemCount() {
        return upcomingEventList.size();
    }

    class UpcomingHolder extends RecyclerViewBouncy.ViewHolder implements View.OnClickListener {
        UpcomingCustomLayoutBinding binding;
        UpcomingHolder(@NonNull UpcomingCustomLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent detailsIntent = new Intent(context, EventDetailsActivity.class);
            detailsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            detailsIntent.putExtra(Constants.ApiKeyName.eventId,upcomingEventList.get(getAdapterPosition()-1).getId());
            detailsIntent.putExtra(Constants.ApiKeyName.eventImg,upcomingEventList.get(getAdapterPosition()-1).getEventImages().get(0).getEventImage());
            context.startActivity(detailsIntent);
        }
    }
}
