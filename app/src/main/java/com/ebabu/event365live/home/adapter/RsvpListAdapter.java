package com.ebabu.event365live.home.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.ebabu.event365live.home.fragment.RSVPCompletedFragment;
import com.ebabu.event365live.home.fragment.RSVPPendingFragment;
import com.ebabu.event365live.home.modal.rsvp.GetRsvpUserModal;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.userinfo.activity.EventDetailsActivity;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.ShowToast;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class RsvpListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private RsvpCustomListLayoutBinding rsvpLayoutBinding;
    List<GetRsvpUserModal.RSPVList> headerModals;
    private RSVPPendingFragment rsvpPendingFragment;
    private RSVPCompletedFragment rsvpCompletedFragment;
    private int SHOW_DATE = 1;
    private int SHOW_VIEW = 2;
    private int LOADING = 3;
    private RecyclerView.ViewHolder holder;
    private boolean match = true;
    private RsvpCustomListLayoutBinding holderLayoutBindings;
    private boolean isLoading = false;
    private CompositeDisposable compositeDisposable;
    private String deviceToken;


    public RsvpListAdapter(List<GetRsvpUserModal.RSPVList> headerModals, RSVPPendingFragment rsvpFragment, RSVPCompletedFragment rsvpCompletedFragment) {
        this.headerModals = headerModals;
        this.rsvpPendingFragment = rsvpFragment;
        this.rsvpCompletedFragment = rsvpCompletedFragment;
        compositeDisposable = new CompositeDisposable();
        deviceToken = CommonUtils.getCommonUtilsInstance().getDeviceAuth();
        Log.d("fnklasnflsa", "RsvpListAdapter: " + headerModals.size());
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
        try {
            Log.d("fnalfnkla", position + " onBindViewHolder: " + datum.getDateTime());
            if (holder instanceof RsvpHolder) {
                Log.d("fnaslfnsa", "onBindViewHolder: " + datum.getDateTime());

                if (datum.getDateTime() != null) {
                    ((RsvpHolder) holder).holderLayoutBinding.tvShowEgoTime.setText(CommonUtils.getTimeAgo(datum.getDateTime(), context));
                    //((RsvpHolder) holder).holderLayoutBinding.tvShowEgoTime.setText("" + datum.getId());
                }
                if (datum.getSender() != null && !TextUtils.isEmpty(datum.getSender().get(0).getProfilePic())) {
                    ((RsvpHolder) holder).holderLayoutBinding.showNameImgContainer.setVisibility(View.GONE);
                    ((RsvpHolder) holder).holderLayoutBinding.ivUserImg.setVisibility(View.VISIBLE);
                    Glide.with(context).load(datum.getSender().get(0).getProfilePic()).placeholder(R.drawable.wide_loading_img).error(R.drawable.wide_error_img).into(((RsvpHolder) holder).holderLayoutBinding.ivUserImg);
                } else {
                    ((RsvpHolder) holder).holderLayoutBinding.showNameImgContainer.setVisibility(View.VISIBLE);
                    ((RsvpHolder) holder).holderLayoutBinding.ivUserImg.setVisibility(View.GONE);
                    ((RsvpHolder) holder).holderLayoutBinding.tvShortName.setText(CommonUtils.getCommonUtilsInstance().getHostName(datum.getSender().get(0).getName()));
                }
                ((RsvpHolder) holder).holderLayoutBinding.tvShowUserName.setText(datum.getSender().get(0).getName());
                ((RsvpHolder) holder).holderLayoutBinding.tvShowInviteMsg.setText(datum.getMsg());
                if (datum.getStatus().equalsIgnoreCase("pending")) {
                    Log.d("nsanklfna", "pending: " + datum.getStatus());
                    ((RsvpHolder) holder).holderLayoutBinding.btnAccept.setVisibility(View.VISIBLE);
                    ((RsvpHolder) holder).holderLayoutBinding.btnReject.setVisibility(View.VISIBLE);
                    ((RsvpHolder) holder).holderLayoutBinding.btnAccepted.setVisibility(View.GONE);
                } else if (datum.getStatus().equalsIgnoreCase("accepted")) {
                    ((RsvpHolder) holder).holderLayoutBinding.btnAccept.setVisibility(View.GONE);
                    ((RsvpHolder) holder).holderLayoutBinding.btnReject.setVisibility(View.GONE);
                    ((RsvpHolder) holder).holderLayoutBinding.btnAccepted.setVisibility(View.VISIBLE);
                    Log.d("nflksanfa", position + " onBindViewHolder: " + headerModals.get(position).getHeadTitle());
                    Log.d("nsanklfna", "accept: " + datum.getStatus());
                }
            } else if (holder instanceof ShowDateHolder) {
                Log.d("fnalfnkla", position + " onBindViewHolder: " + datum.getHeadTitle());
                ((ShowDateHolder) holder).ivShowDate.setText(CommonUtils.getCommonUtilsInstance().getCurrentDate(datum.getHeadTitle()));
                // ((ShowDateHolder) holder).ivShowDate.setTextColor(context.getResources().getColor(R.color.colorSmoothBlack));
            }
        }catch (Exception e){
            e.printStackTrace();
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
                    Log.d("nfklasn", getAdapterPosition() - 1 + " btnAccepted: " + headerModals.get(getAdapterPosition() - 1).getStatus());
                    if (headerModals.get(getAdapterPosition() - 1).getStatus().equalsIgnoreCase("accept") || headerModals.get(getAdapterPosition() - 1).getStatus().equalsIgnoreCase("accepted")) {
                        Intent eventDetailsIntent = new Intent(context, EventDetailsActivity.class);
                        eventDetailsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        eventDetailsIntent.putExtra(Constants.ApiKeyName.eventId, headerModals.get(getAdapterPosition() - 1).getEventId());
                        eventDetailsIntent.putExtra(Constants.ApiKeyName.eventImg, headerModals.get(getAdapterPosition() - 1).getSender().get(0).getProfilePic());
                        context.startActivity(eventDetailsIntent);
                    }
                    break;

                case R.id.btnAccept:
                    statusMsg = "accepted";
                    // rsvpAcceptListener.acceptRejectListener(headerModals.get(getAdapterPosition() - 1).getId(), headerModals.get(getAdapterPosition() - 1).getEventId(), statusMsg);
                    changeStatusRsvpRequest(headerModals.get(getAdapterPosition() - 1).getId(), statusMsg, getAdapterPosition() - 1);
                    //rsvpFragment.showRsvpRequest(RSVPFragment.currentPage-1;
                    break;

                case R.id.btnReject:
                    statusMsg = "rejected";
                    //rsvpAcceptListener.acceptRejectListener(headerModals.get(getAdapterPosition() - 1).getId(), headerModals.get(getAdapterPosition() - 1).getEventId(), statusMsg);
                    Log.d("nfklasn", getAdapterPosition() - 1 + " rejected: " + headerModals.get(getAdapterPosition() - 1).getStatus());
                    changeStatusRsvpRequest(headerModals.get(getAdapterPosition() - 1).getId(), statusMsg, getAdapterPosition() - 1);

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
        }
        return SHOW_VIEW;
    }

    GetRsvpUserModal.RSPVList getItem(int position) {
        return headerModals.get(position);

    }

    private void changeStatusRsvpRequest(int rsvpId, String statusMsg, int itemPosition) {
        AtomicInteger invitationItemCount = new AtomicInteger();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Constants.id, rsvpId);
        jsonObject.addProperty(Constants.ApiKeyName.status, statusMsg);
        compositeDisposable.add(APICall.getApiInterface().statusRsvp(deviceToken, jsonObject)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(responseBody -> {

                            String rawData = responseBody.string();
                            JSONObject obj = new JSONObject(rawData);
                            boolean success = obj.getBoolean("success");
                            if (success) {
                                if (statusMsg.equalsIgnoreCase("accepted")) {
//                                    headerModals.get(itemPosition).setStatus("accept");
//                                    notifyDataSetChanged();
                                    if (rsvpPendingFragment != null) {
                                        rsvpPendingFragment.showRsvpRequest(1, true);
                                        //rsvpFragment.smoothRecyclerScrolled(itemPosition);
                                        rsvpPendingFragment.rspvList.clear();
                                    } else {
                                        rsvpCompletedFragment.showRsvpRequest(1, true);
                                        //rsvpFragment.smoothRecyclerScrolled(itemPosition);
                                        rsvpCompletedFragment.rspvList.clear();
                                    }

                                } else if (statusMsg.equalsIgnoreCase("rejected")) {

                                    //if list size is 2 its means it has only one invitation so if user reject this, should also remove time as well
//                                    if (headerModals.size() == 2) {
//                                        headerModals.remove(itemPosition);
//                                        headerModals.remove(0);
//                                        rsvpFragment.hideView();
//                                        notifyDataSetChanged();
//                                        return;
//                                    }
//
//
//
//
//                                    Log.d("nfaklsnl", itemPosition+"changeStatusRsvpRequest: "+statusMsg);
//                                    headerModals.get(itemPosition).setStatus("reject");
//                                    headerModals.remove(itemPosition);
//                                    headerModals.remove(itemPosition - 1);
//                                    notifyDataSetChanged();
                                    //notifyItemRangeRemoved();

                                    if (rsvpPendingFragment != null) {
                                        rsvpPendingFragment.showRsvpRequest(1, true);
                                        //rsvpFragment.smoothRecyclerScrolled(itemPosition);
                                        rsvpPendingFragment.rspvList.clear();
                                    } else {
                                        rsvpCompletedFragment.showRsvpRequest(1, true);
                                        //rsvpFragment.smoothRecyclerScrolled(itemPosition);
                                        rsvpCompletedFragment.rspvList.clear();
                                    }
                                }


                            } else {
                                ShowToast.errorToast(context, context.getString(R.string.something_wrong));
                            }

                        }, throwable -> {
                            ShowToast.errorToast(context, context.getString(R.string.something_wrong));
                        })
        );
    }


}
