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
import com.ebabu.event365live.home.modal.GetRecommendedModal;
import com.ebabu.event365live.home.modal.SubCategoryModal;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.userinfo.activity.EventDetailsActivity;
import com.ebabu.event365live.utils.CommonUtils;

import java.util.List;

public class RecommendedEventListAdapter extends RecyclerView.Adapter<RecommendedEventListAdapter.EventListHolder> {

    private Context context;
    private List<GetRecommendedModal.EventList> eventRecommendedList;

    public RecommendedEventListAdapter(List<GetRecommendedModal.EventList> eventRecommendedList) {
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
        GetRecommendedModal.EventList recommendedList = eventRecommendedList.get(position);

        String name = recommendedList.getName();
        String startTime = CommonUtils.getCommonUtilsInstance().getStartEndEventTime(recommendedList.getStart());
        String endTime = CommonUtils.getCommonUtilsInstance().getStartEndEventTime(recommendedList.getEnd());
        String startDate = CommonUtils.getCommonUtilsInstance().getDateMonthYearName(recommendedList.getStart(), false);
        String address = recommendedList.getAddress().get(0).getVenueAddress();

        if (!recommendedList.getEventImages().isEmpty()) {
            Glide.with(context).load(recommendedList.getEventImages().get(0).getEventImage()).placeholder(R.drawable.wide_loading_img).error(R.drawable.wide_error_img).into(holder.ivShowEventPhoto);
        } else
            Glide.with(context).load("").placeholder(R.drawable.wide_loading_img).error(R.drawable.wide_error_img).into(holder.ivShowEventPhoto);


        holder.tvShowEventName.setText(name);
        String showDate = CommonUtils.getCommonUtilsInstance().getLeftDaysAndTime(recommendedList.getStart()).equalsIgnoreCase("ongoing") ? "Ongoing" : "Starts " + startTime + " - " + CommonUtils.getCommonUtilsInstance().getLeftDaysAndTime(recommendedList.getStart());
        holder.tvShowEventTime.setText(showDate);
        holder.btnShowDate.setText(startDate);
        holder.tvShowVenueAdd.setText(address != null ? address : context.getString(R.string.na));

    }

    @Override
    public int getItemCount() {
        return eventRecommendedList.size();
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
            itemView.findViewById(R.id.eventListContainer).setFocusable(false);
            itemView.findViewById(R.id.eventListContainer).setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent eventIntent = new Intent(context, EventDetailsActivity.class);
            eventIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            eventIntent.putExtra(Constants.ApiKeyName.eventId, eventRecommendedList.get(getAdapterPosition() - 1).getId());
            context.startActivity(eventIntent);
        }
    }


}
