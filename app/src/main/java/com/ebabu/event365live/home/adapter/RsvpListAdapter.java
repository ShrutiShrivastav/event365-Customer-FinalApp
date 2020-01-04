package com.ebabu.event365live.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.RsvpCustomListLayoutBinding;
import com.ebabu.event365live.home.fragment.RSVPFragment;
import com.ebabu.event365live.home.modal.rsvp.GetRsvpUserModal;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.listener.RsvpAcceptListener;
import com.ebabu.event365live.userinfo.activity.EventDetailsActivity;
import com.ebabu.event365live.utils.CommonUtils;

import java.util.List;

public class RsvpListAdapter extends RecyclerView.Adapter<RsvpListAdapter.RsvpHolder>{
    private Context context;
    private RsvpCustomListLayoutBinding rsvpLayoutBinding;
    private List<GetRsvpUserModal.Datum> rsvpUserList;
    private RsvpAcceptListener rsvpAcceptListener;

    public RsvpListAdapter(List<GetRsvpUserModal.Datum> rsvpUserList, RSVPFragment rsvpFragment) {
        this.rsvpUserList = rsvpUserList;
        rsvpAcceptListener = rsvpFragment;
    }

    @NonNull
    @Override
    public RsvpHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        rsvpLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.rsvp_custom_list_layout,parent,false);

        return new RsvpHolder(rsvpLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RsvpHolder holder, int position) {
        GetRsvpUserModal.Datum datum = rsvpUserList.get(position);

        if(!TextUtils.isEmpty(datum.getSender().get(0).getProfilePic())){
            holder.holderLayoutBinding.showNameImgContainer.setVisibility(View.GONE);
            holder.holderLayoutBinding.ivUserImg.setVisibility(View.VISIBLE);
            Glide.with(context).load(datum.getSender().get(0).getProfilePic()).placeholder(R.drawable.wide_loading_img).error(R.drawable.wide_error_img).into(holder.holderLayoutBinding.ivUserImg);
        }else {
            holder.holderLayoutBinding.showNameImgContainer.setVisibility(View.VISIBLE);
            holder.holderLayoutBinding.ivUserImg.setVisibility(View.GONE);
            holder.holderLayoutBinding.tvShortName.setText(CommonUtils.getCommonUtilsInstance().getHostName(datum.getSender().get(0).getName()));
        }

        holder.holderLayoutBinding.tvShowUserName.setText(datum.getSender().get(0).getName());
        holder.holderLayoutBinding.tvShowInviteMsg.setText(datum.getMsg());
        if(datum.getStatus().equalsIgnoreCase("pending")){
            holder.holderLayoutBinding.btnAccept.setVisibility(View.VISIBLE);
            holder.holderLayoutBinding.btnReject.setVisibility(View.VISIBLE);
            holder.holderLayoutBinding.btnAccepted.setVisibility(View.GONE);
        }else if(datum.getStatus().equalsIgnoreCase("reject")){
         //holder.holderLayoutBinding.cardView.setVisibility(View.GONE);
        }
        else {
            holder.holderLayoutBinding.btnAccept.setVisibility(View.GONE);
            holder.holderLayoutBinding.btnReject.setVisibility(View.GONE);
            holder.holderLayoutBinding.btnAccepted.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return rsvpUserList.size();
    }

    class RsvpHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RsvpCustomListLayoutBinding holderLayoutBinding;
        public RsvpHolder(@NonNull RsvpCustomListLayoutBinding holderLayoutBinding) {
            super(rsvpLayoutBinding.getRoot());
            this.holderLayoutBinding = holderLayoutBinding;
            holderLayoutBinding.btnAccepted.setOnClickListener(this);
            holderLayoutBinding.btnReject.setOnClickListener(this);
            holderLayoutBinding.btnAccept.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.btnAccepted:
                    if(!rsvpUserList.get(getAdapterPosition()-1).getStatus().equalsIgnoreCase("pending")){
                        Intent eventDetailsIntent = new Intent(context, EventDetailsActivity.class);
                        eventDetailsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        eventDetailsIntent.putExtra(Constants.ApiKeyName.eventId,rsvpUserList.get(getAdapterPosition()-1).getEventId());
                        eventDetailsIntent.putExtra(Constants.ApiKeyName.eventImg,rsvpUserList.get(getAdapterPosition()-1).getSender().get(0).getProfilePic());
                        context.startActivity(eventDetailsIntent);
                    }
                    break;

                case R.id.btnAccept:
                    rsvpAcceptListener.acceptRejectListener(rsvpUserList.get(getAdapterPosition()-1).getId(),rsvpUserList.get(getAdapterPosition()-1).getEventId(),"accept");
                    break;

                case R.id.btnReject:
                    rsvpAcceptListener.acceptRejectListener(rsvpUserList.get(getAdapterPosition()-1).getId(),rsvpUserList.get(getAdapterPosition()-1).getEventId(),"reject");
                    break;
            }

        }
    }

}
