package com.ebabu.event365live.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.ebabu.event365live.databinding.RsvpCustomListLayoutBinding;
import com.ebabu.event365live.home.fragment.RSVPFragment;
import com.ebabu.event365live.home.modal.RsvpHeaderModal;
import com.ebabu.event365live.home.modal.rsvp.GetRsvpUserModal;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.listener.RsvpAcceptListener;
import com.ebabu.event365live.userinfo.activity.EventDetailsActivity;
import com.ebabu.event365live.utils.CommonUtils;

import java.util.List;

public class RsvpListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private RsvpCustomListLayoutBinding rsvpLayoutBinding;
    List<GetRsvpUserModal.RSPVList> headerModals;
    private RsvpAcceptListener rsvpAcceptListener;
    private int SHOW_DATE = 1;
    private int SHOW_VIEW = 2;
    private int LOADING = 3;
    private RecyclerView.ViewHolder holder;
    private boolean match = true;
    private RsvpCustomListLayoutBinding holderLayoutBindings;
    private boolean isLoading = false;


    public RsvpListAdapter(List<GetRsvpUserModal.RSPVList> headerModals, RsvpAcceptListener rsvpAcceptListener) {
        this.headerModals = headerModals;
        this.rsvpAcceptListener = rsvpAcceptListener;
        Log.d("fnklasnflsa", "RsvpListAdapter: " + headerModals.size());
    }

    public boolean isItemAvailable(){
        return headerModals.size()>0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        if (SHOW_DATE == viewType) {
            View view = inflater.inflate(R.layout.show_date_layout, parent, false);
            holder = new ShowDateHolder(view);

        } else if (SHOW_VIEW == viewType) {
            rsvpLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.rsvp_custom_list_layout, parent, false);
            holder = new RsvpHolder(rsvpLayoutBinding);

        } else if (LOADING == viewType) {
            View view = inflater.inflate(R.layout.progress_bar_layout, parent, false);
            holder = new LoadingHolder(view);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        GetRsvpUserModal.RSPVList datum = headerModals.get(position);

//        if(datum.getDateTime() != null)
//        Log.d("fnbaslfnskla", "onBindViewHolder: "+);

        if(holder instanceof RsvpHolder){
            if(datum.getDateTime() != null) {
                //((RsvpHolder) holder).holderLayoutBinding.tvShowEgoTime.setText(CommonUtils.getTimeAgo(datum.getDateTime(),context,true));
                ((RsvpHolder) holder).holderLayoutBinding.tvShowEgoTime.setText("" + datum.getId());
            }if(!TextUtils.isEmpty(datum.getSender().get(0).getProfilePic())){
                ((RsvpHolder) holder).holderLayoutBinding.showNameImgContainer.setVisibility(View.GONE);
                ((RsvpHolder) holder).holderLayoutBinding.ivUserImg.setVisibility(View.VISIBLE);
                Glide.with(context).load(datum.getSender().get(0).getProfilePic()).placeholder(R.drawable.wide_loading_img).error(R.drawable.wide_error_img).into(((RsvpHolder) holder).holderLayoutBinding.ivUserImg);
            }else {
                ((RsvpHolder) holder).holderLayoutBinding.showNameImgContainer.setVisibility(View.VISIBLE);
                ((RsvpHolder) holder).holderLayoutBinding.ivUserImg.setVisibility(View.GONE);
                ((RsvpHolder) holder).holderLayoutBinding.tvShortName.setText(CommonUtils.getCommonUtilsInstance().getHostName(datum.getSender().get(0).getName()));
            }
            ((RsvpHolder) holder).holderLayoutBinding.tvShowUserName.setText(datum.getSender().get(0).getName());
            ((RsvpHolder) holder).holderLayoutBinding.tvShowInviteMsg.setText(datum.getMsg());
            if(datum.getStatus().equalsIgnoreCase("pending")){
                ((RsvpHolder) holder).holderLayoutBinding.btnAccept.setVisibility(View.VISIBLE);
                ((RsvpHolder) holder).holderLayoutBinding.btnReject.setVisibility(View.VISIBLE);
                ((RsvpHolder) holder).holderLayoutBinding.btnAccepted.setVisibility(View.GONE);
            }else if(datum.getStatus().equalsIgnoreCase("reject")){
//                ((RsvpHolder) holder).holderLayoutBinding.getRoot().setVisibility(View.GONE);
                Log.d("fnalfnkla", "reject: ");
            }else if(datum.getStatus().equalsIgnoreCase("accept")){
//                ((RsvpHolder) holder).holderLayoutBinding.btnAccept.setVisibility(View.GONE);
//                ((RsvpHolder) holder).holderLayoutBinding.btnReject.setVisibility(View.GONE);
//                ((RsvpHolder) holder).holderLayoutBinding.btnAccepted.setVisibility(View.VISIBLE);
            }
        }else if(holder instanceof ShowDateHolder){
            Log.d("fnalfnkla", "onBindViewHolder: "+datum.getHeadTitle());
            ((ShowDateHolder) holder).ivShowDate.setText(CommonUtils.getCommonUtilsInstance().getCurrentDate(datum.getHeadTitle()));
            ((ShowDateHolder) holder).ivShowDate.setTextColor(context.getResources().getColor(R.color.colorSmoothBlack));
        }
    }
    @Override
    public int getItemCount() {
        return headerModals.size();
    }

    class RsvpHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private RsvpCustomListLayoutBinding holderLayoutBinding;
        public RsvpHolder(@NonNull RsvpCustomListLayoutBinding holderLayoutBinding) {
            super(rsvpLayoutBinding.getRoot());
            this.holderLayoutBinding = holderLayoutBinding;
            holderLayoutBinding.btnAccepted.setOnClickListener(this);
            holderLayoutBinding.btnReject.setOnClickListener(this);
            holderLayoutBinding.btnAccept.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String statusMsg = "";

            switch (v.getId()) {
                case R.id.btnAccepted:
                    if(!headerModals.get(getAdapterPosition()-1).getStatus().equalsIgnoreCase("pending")){
                        Intent eventDetailsIntent = new Intent(context, EventDetailsActivity.class);
                        eventDetailsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        eventDetailsIntent.putExtra(Constants.ApiKeyName.eventId,headerModals.get(getAdapterPosition()-1).getEventId());
                        eventDetailsIntent.putExtra(Constants.ApiKeyName.eventImg,headerModals.get(getAdapterPosition()-1).getSender().get(0).getProfilePic());
                        context.startActivity(eventDetailsIntent);
                    }
                    break;

                case R.id.btnAccept:
                    statusMsg =  "accepted";
                    rsvpAcceptListener.acceptRejectListener(headerModals.get(getAdapterPosition()-1).getId(),headerModals.get(getAdapterPosition()-1).getEventId(),statusMsg);
                    break;

                case R.id.btnReject:
                    statusMsg =  "rejected";
                     rsvpAcceptListener.acceptRejectListener(headerModals.get(getAdapterPosition()-1).getId(),headerModals.get(getAdapterPosition()-1).getEventId(),statusMsg);
                    break;
            }

        }


    }

    class ShowDateHolder extends RecyclerView.ViewHolder {
        TextView ivShowDate;

        public ShowDateHolder(@NonNull View itemView) {
            super(itemView);
            ivShowDate = itemView.findViewById(R.id.ivShowDate);
        }
    }

    class LoadingHolder extends RecyclerView.ViewHolder {

        public LoadingHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (headerModals.get(position).isHead()) {
            return SHOW_DATE;
        } else if (isLoading)
            return LOADING;
        return SHOW_VIEW;
    }

    public void showLoading() {
        isLoading = true;
        headerModals.add(new GetRsvpUserModal.RSPVList());
        notifyItemInserted(headerModals.size() - 1);

    }

    public void dismissLoading() {
        isLoading = false;
        int position = headerModals.size() - 1;
        GetRsvpUserModal.RSPVList item = getItem(position);
        if (item != null) {
            headerModals.remove(position);
            notifyItemRemoved(position);
        }
    }


    public void clear() {
        headerModals.clear();
        notifyDataSetChanged();
    }

    GetRsvpUserModal.RSPVList getItem(int position) {
        return headerModals.get(position);

    }

    public void dataNotify(int rsvpId, String msg){
            for(int i=0;i<headerModals.size();i++){
                if(!headerModals.get(i).isHead()){
                    if(msg.equalsIgnoreCase("rejected")){
                        if(headerModals.get(i).getId() == rsvpId){
                            headerModals.remove(headerModals.get(i));
                            break;
                        }
                    }else if(msg.equalsIgnoreCase("accpeted")){
                        headerModals.get(i).setStatus("accept");
                        break;
                    }
                }
            }
            notifyDataSetChanged();

    }

}
