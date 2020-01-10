package com.ebabu.event365live.homedrawer.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.bouncerecycler.RecyclerViewBouncy;
import com.ebabu.event365live.databinding.NotificationListLayoutBinding;
import com.ebabu.event365live.homedrawer.modal.NotificationListModal;
import com.ebabu.event365live.utils.CommonUtils;

import java.util.List;

public class NotificationListAdapter extends RecyclerViewBouncy.Adapter<NotificationListAdapter.NotificationListHolder>{

    private Context context;
    private NotificationListLayoutBinding listLayoutBinding;
    private List<NotificationListModal.NotificationList> notificationLists;

    public NotificationListAdapter(List<NotificationListModal.NotificationList> notificationLists) {
        this.notificationLists = notificationLists;
    }

    @NonNull
    @Override
    public NotificationListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        listLayoutBinding = DataBindingUtil.inflate(inflater,R.layout.notification_list_layout,parent,false);
        return new NotificationListHolder(listLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationListHolder holder, int position) {
        NotificationListModal.NotificationList notificationData =  notificationLists.get(position);
        holder.layoutBinding.tvShowUserName.setText(notificationData.getSender().get(0).getName());
        holder.layoutBinding.tvShowInviteMsg.setText(notificationData.getMsg());
        holder.layoutBinding.tvShowInviteMsg.setText(notificationData.getMsg());
        if(notificationData.getSender().get(0).getProfilePic() != null || TextUtils.isEmpty(notificationData.getSender().get(0).getProfilePic())){
            holder.layoutBinding.ivUserImg.setVisibility(View.GONE);
            holder.layoutBinding.noPhotoContainer.setVisibility(View.VISIBLE);
            holder.layoutBinding.tvShowNameOfImg.setText(CommonUtils.getCommonUtilsInstance().getHostName(notificationData.getSender().get(0).getName()));
        }else {
            Glide.with(context).load(notificationData.getSender().get(0).getProfilePic()).placeholder(R.drawable.wide_loading_img).error(R.drawable.wide_error_img).into(holder.layoutBinding.ivUserImg);
        }
        holder.layoutBinding.tvShowEgoTime.setText(CommonUtils.getTimeAgo(notificationData.getDateTime(),context,true));


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
}
