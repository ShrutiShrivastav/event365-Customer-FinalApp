package com.ebabu.event365live.homedrawer.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
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
import com.ebabu.event365live.home.adapter.RsvpListAdapter;
import com.ebabu.event365live.homedrawer.modal.NotificationListModal;
import com.ebabu.event365live.utils.CommonUtils;

import java.util.Collections;
import java.util.List;

public class NotificationListAdapter extends RecyclerViewBouncy.Adapter<RecyclerViewBouncy.ViewHolder>{

    private Context context;
    private NotificationListLayoutBinding listLayoutBinding;
    private List<NotificationListModal.NotificationList> notificationLists;
    private int SHOW_DATE = 1;
    private int SHOW_VIEW = 2;
    private RecyclerViewBouncy.ViewHolder holder;
    public NotificationListAdapter(List<NotificationListModal.NotificationList> notificationLists) {
        this.notificationLists = notificationLists;
        //Collections.reverse(notificationLists);
    }

    @NonNull
    @Override
    public RecyclerViewBouncy.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (SHOW_DATE == viewType) {
            View view = inflater.inflate(R.layout.show_date_layout, parent, false);
            holder = new NotificationListAdapter.ShowDateHolder(view);

        }else if (SHOW_VIEW == viewType) {
            listLayoutBinding = DataBindingUtil.inflate(inflater,R.layout.notification_list_layout,parent,false);
            holder = new NotificationListAdapter.NotificationListHolder(listLayoutBinding);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewBouncy.ViewHolder holder, int position) {
        NotificationListModal.NotificationList notificationData =  notificationLists.get(position);

        if(holder instanceof NotificationListHolder){
            ((NotificationListHolder) holder).layoutBinding.tvShowUserName.setText(notificationData.getSender().get(0).getName());
            ((NotificationListHolder) holder).layoutBinding.tvShowInviteMsg.setText(notificationData.getMsg());
            if(notificationData.getSender().get(0).getProfilePic() != null || TextUtils.isEmpty(notificationData.getSender().get(0).getProfilePic())){
                ((NotificationListHolder) holder).layoutBinding.ivUserImg.setVisibility(View.VISIBLE);
                ((NotificationListHolder) holder).layoutBinding.noPhotoContainer.setVisibility(View.GONE);
                Glide.with(context).load(notificationData.getSender().get(0).getProfilePic()).placeholder(R.drawable.wide_loading_img).error(R.drawable.wide_error_img).into(((NotificationListHolder) holder).layoutBinding.ivUserImg);
            }else {
                ((NotificationListHolder) holder).layoutBinding.ivUserImg.setVisibility(View.GONE);
                ((NotificationListHolder) holder).layoutBinding.noPhotoContainer.setVisibility(View.VISIBLE);
                ((NotificationListHolder) holder).layoutBinding.tvShowNameOfImg.setText(CommonUtils.getCommonUtilsInstance().getHostName(notificationData.getSender().get(0).getName()));

            }
            ((NotificationListHolder) holder).layoutBinding.tvShowEgoTime.setText(CommonUtils.getTimeAgo(notificationData.getDateTime(),context,true));
        }else if(holder instanceof ShowDateHolder){
            ((NotificationListAdapter.ShowDateHolder) holder).ivShowDate.setText(CommonUtils.getCommonUtilsInstance().getCurrentDate(notificationData.getHeadTitle()));
        }

    }

    @Override
    public int getItemCount() {
        return notificationLists.size();
    }

    class NotificationListHolder extends RecyclerViewBouncy.ViewHolder {
        private NotificationListLayoutBinding layoutBinding;
        NotificationListHolder(@NonNull NotificationListLayoutBinding layoutBinding) {
            super(layoutBinding.getRoot());
            this.layoutBinding = layoutBinding;
        }
    }

    class ShowDateHolder extends RecyclerView.ViewHolder {
        TextView ivShowDate;

        public ShowDateHolder(@NonNull View itemView) {
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
}
