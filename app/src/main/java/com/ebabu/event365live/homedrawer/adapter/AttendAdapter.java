package com.ebabu.event365live.homedrawer.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.bouncerecycler.RecyclerViewBouncy;
import com.ebabu.event365live.databinding.UpcomingCustomLayoutBinding;
import com.ebabu.event365live.homedrawer.modal.upcoming.AttendentEvent;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.userinfo.activity.EventDetailsActivity;
import com.ebabu.event365live.userinfo.utils.GalleryListItemDecoration;
import com.ebabu.event365live.utils.CommonUtils;

import java.util.List;

public class AttendAdapter extends RecyclerViewBouncy.Adapter<AttendAdapter.AttendEventHolder> {

    private Context context;
    private boolean isFromEventDetailsActivity;
    private RecyclerViewBouncy.ViewHolder holder;
    private DataBindingUtil dataBindingUtil;
    private List<AttendentEvent> attendEventList;

    public AttendAdapter(List<AttendentEvent> attendEventList) {
               this.attendEventList = attendEventList;
    }

    @NonNull
    @Override
    public AttendEventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        UpcomingCustomLayoutBinding customLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.upcoming_custom_layout, parent, false);
        return new  AttendEventHolder(customLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendEventHolder holder, int position) {
        AttendentEvent attendentEvent = attendEventList.get(position);

        Glide.with(context).load(attendentEvent.getEventImages().get(0).getEventImage()).into(holder.binding.ivUpcomingEventImg);
        holder.binding.tvUpcomingEventName.setText(attendentEvent.getName());
        holder.binding.tvUpcomingEventName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        String[] getDate = CommonUtils.getCommonUtilsInstance().getSplitMonthDate(attendentEvent.getStartDate()).split(",");
        holder.binding.tvUpcomingDateInNumeric.setText(getDate[0]);
        holder.binding.tvUpcomingDateInName.setText(getDate[1]);
        if(attendentEvent.getVenueEvents() != null)
        holder.binding.tvUpcomingEventAdd.setText(CommonUtils.getCommonUtilsInstance().getAddressFromLatLng(context,attendentEvent.getVenueEvents().get(0).getLatitude(),attendentEvent.getVenueEvents().get(0).getLongitude()));
        else
            holder.binding.tvUpcomingEventAdd.setText(context.getString(R.string.na));
    }



    @Override
    public int getItemCount() {
        return attendEventList.size();
    }
    class AttendEventHolder extends RecyclerViewBouncy.ViewHolder implements View.OnClickListener {
        private UpcomingCustomLayoutBinding binding;
        AttendEventHolder(@NonNull UpcomingCustomLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            Intent detailsIntent = new Intent(context, EventDetailsActivity.class);
            detailsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            detailsIntent.putExtra(Constants.ApiKeyName.eventId,attendEventList.get(getAdapterPosition()-1).getId());
            detailsIntent.putExtra(Constants.ApiKeyName.eventImg,attendEventList.get(getAdapterPosition()-1).getEventImages().get(0).getEventImage());
            context.startActivity(detailsIntent);
        }
    }
}
