package com.ebabu.event365live.homedrawer.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.bouncerecycler.RecyclerViewBouncy;
import com.ebabu.event365live.databinding.NotificationListLayoutBinding;
import com.ebabu.event365live.homedrawer.activity.RSVPTicketActivity;
import com.ebabu.event365live.homedrawer.modal.NotificationListModal;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.userinfo.activity.EventDetailsActivity;
import com.ebabu.event365live.utils.CommonUtils;

import java.util.List;

public class NotificationListAdapter extends RecyclerViewBouncy.Adapter<RecyclerViewBouncy.ViewHolder> {

    private Context context;
    private NotificationListLayoutBinding listLayoutBinding;
    private List<NotificationListModal.NotificationList> notificationLists;
    private int SHOW_DATE = 1;
    private int SHOW_VIEW = 2;
    private RecyclerViewBouncy.ViewHolder holder;

    public NotificationListAdapter(List<NotificationListModal.NotificationList> notificationLists) {
        this.notificationLists = notificationLists;
    }

    @NonNull
    @Override
    public RecyclerViewBouncy.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        if (SHOW_DATE == viewType) {
            View view = inflater.inflate(R.layout.show_date_layout, parent, false);
            holder = new ShowDateHolder(view);

        } else if (SHOW_VIEW == viewType) {
            listLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.notification_list_layout, parent, false);
            holder = new NotificationListHolder(listLayoutBinding);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewBouncy.ViewHolder holder, int position) {
        NotificationListModal.NotificationList notificationData = notificationLists.get(position);

        if (holder instanceof NotificationListHolder) {
            // there is no need to check null safety for sender but getting null from backend
            if (notificationData.getSender() != null) {
                try {
                    ((NotificationListHolder) holder).layoutBinding.tvShowUserName.setText(notificationData.getSender().get(0).getName());
                }
                catch (Exception e){}
            } else {
                ((NotificationListHolder) holder).layoutBinding.tvShowUserName.setText(context.getString(R.string.na));
            }
            ((NotificationListHolder) holder).layoutBinding.tvShowInviteMsg.setText(notificationData.getMsg());
            if (notificationData.getSender() != null && notificationData.getSender().get(0).getProfilePic() != null) {
                ((NotificationListHolder) holder).layoutBinding.ivUserImg.setVisibility(View.VISIBLE);
                ((NotificationListHolder) holder).layoutBinding.noPhotoContainer.setVisibility(View.GONE);
                Glide.with(context).load(notificationData.getSender().get(0).getProfilePic()).placeholder(R.drawable.wide_loading_img).error(R.drawable.wide_error_img).into(((NotificationListHolder) holder).layoutBinding.ivUserImg);
            } else {
                ((NotificationListHolder) holder).layoutBinding.ivUserImg.setVisibility(View.GONE);
                ((NotificationListHolder) holder).layoutBinding.noPhotoContainer.setVisibility(View.VISIBLE);
                ((NotificationListHolder) holder).layoutBinding.tvShowNameOfImg.setText(CommonUtils.getCommonUtilsInstance().getHostName(notificationData.getSender().get(0).getName()));
            }
            ((NotificationListHolder) holder).layoutBinding.tvShowEgoTime.setText(CommonUtils.getTimeAgo(notificationData.getDateTime(), context));
        } else if (holder instanceof ShowDateHolder) {
            ((NotificationListAdapter.ShowDateHolder) holder).ivShowDate.setText(CommonUtils.getCommonUtilsInstance().getCurrentDate(notificationData.getHeadTitle()));

        }
    }

    @Override
    public int getItemCount() {
        return notificationLists.size();
    }

    class NotificationListHolder extends RecyclerViewBouncy.ViewHolder implements View.OnClickListener {
        private NotificationListLayoutBinding layoutBinding;

        NotificationListHolder(@NonNull NotificationListLayoutBinding layoutBinding) {
            super(layoutBinding.getRoot());
            this.layoutBinding = layoutBinding;
            this.layoutBinding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if(notificationLists.get(getAdapterPosition()-1).getType().equals("eventOfInterest") ||
                    notificationLists.get(getAdapterPosition()-1).getType().equals("eventFav") ||
                    notificationLists.get(getAdapterPosition()-1).getType().equals("eventRminder") ||
                    notificationLists.get(getAdapterPosition()-1).getType().equals("Invited")
            ){
                navigate(EventDetailsActivity.class, true, getAdapterPosition());
            }else if(notificationLists.get(getAdapterPosition()-1).getType().equals("ticketBooked")){
                navigate(RSVPTicketActivity.class, false, getAdapterPosition());
            }


        }
    }

    static class ShowDateHolder extends RecyclerView.ViewHolder {
        TextView ivShowDate;

        ShowDateHolder(@NonNull View itemView) {
            super(itemView);
            ivShowDate = itemView.findViewById(R.id.ivShowDate);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (notificationLists.get(position).isHead()) {
            return SHOW_DATE;
        } else
            return SHOW_VIEW;
    }

    private void navigate(Class clazz, boolean isRequireToOpenEventDetails, int position) {
        Intent detailsIntent = new Intent(context, clazz);
        detailsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (isRequireToOpenEventDetails)
            detailsIntent.putExtra(Constants.ApiKeyName.eventId, notificationLists.get(position - 1).getEventId());
        context.startActivity(detailsIntent);
    }
}
