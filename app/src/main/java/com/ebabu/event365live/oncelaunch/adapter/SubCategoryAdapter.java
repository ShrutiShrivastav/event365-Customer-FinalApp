package com.ebabu.event365live.oncelaunch.adapter;

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
import com.ebabu.event365live.home.modal.SubCategoryModal;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.userinfo.activity.EventDetailsActivity;
import com.ebabu.event365live.utils.CommonUtils;

import java.util.List;

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

        if(event.getStart() !=  null){
            String startDate = CommonUtils.getCommonUtilsInstance().getDateMonthName(event.getStart());
            String startTime = CommonUtils.getCommonUtilsInstance().getStartEndEventTime(event.getStart());
            String endTime = CommonUtils.getCommonUtilsInstance().getStartEndEventTime(event.getEnd());
            holder.btnShowDate.setText(startDate);
            holder.tvShowEventTime.setText("Starts "+startTime+ " - "+CommonUtils.getCommonUtilsInstance().getLeftDaysAndTime(event.getStart(),event.getEnd()));
        }
        else {
            holder.tvShowEventTime.setText(context.getString(R.string.na));
            holder.btnShowDate.setText(context.getString(R.string.na));
        }

        if(event.getAddress() != null){
            holder.tvShowVenueAdd.setText(event.getAddress().get(0).getVenueAddress());
        }else {
            holder.tvShowVenueAdd.setText(context.getString(R.string.na));
        }
            Glide.with(context).load(event.getEventImages().get(0).getEventImage()).placeholder(R.drawable.wide_loading_img).error(R.drawable.wide_error_img).into(holder.ivShowEventPhoto);
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
            context.startActivity(new Intent(context, EventDetailsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(Constants.ApiKeyName.eventImg,eventList.get(getAdapterPosition()).getEventImages().get(0).getEventImage()).putExtra(Constants.ApiKeyName.eventId,eventList.get(getAdapterPosition()).getId()));
        }
    }
}
