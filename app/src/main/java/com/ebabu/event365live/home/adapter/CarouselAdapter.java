package com.ebabu.event365live.home.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.NearBySliderLayoutBinding;
import com.ebabu.event365live.home.fragment.NearYouFragment;
import com.ebabu.event365live.home.modal.nearbymodal.EventList;
import com.ebabu.event365live.home.modal.nearbymodal.UserLikes;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.listener.BottomSheetOpenListener;
import com.ebabu.event365live.listener.EventLikeDislikeListener;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.ShowToast;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.CarouselHolder> {

    private ArrayList<EventList> eventListArrayList;
    private Context context;
    private EventLikeDislikeListener eventLikeDislikeListener;
    private NearYouFragment nearYouFragment;
    BottomSheetOpenListener bottomSheetOpenListener;
    private CompositeDisposable compositeDisposable;
    private String deviceToken;
    private MyLoader myLoader;

    public CarouselAdapter(ArrayList<EventList> eventListArrayList, NearYouFragment nearYouFragment, MyLoader myLoader) {
        this.eventListArrayList = eventListArrayList;
        this.nearYouFragment = nearYouFragment;
        eventLikeDislikeListener = nearYouFragment;
        bottomSheetOpenListener = nearYouFragment;
        compositeDisposable = new CompositeDisposable();
        this.myLoader = myLoader;
        deviceToken = CommonUtils.getCommonUtilsInstance().getDeviceAuth();
    }

    @NonNull
    @Override
    public CarouselAdapter.CarouselHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        NearBySliderLayoutBinding sliderLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.near_by_slider_layout, parent, false);
        return new CarouselAdapter.CarouselHolder(sliderLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselAdapter.CarouselHolder holder, int position) {
        EventList eventList = eventListArrayList.get(position);

        if (eventList.getEventImages() != null && eventList.getEventImages().size() > 0)
            Glide.with(context).load(eventList.getEventImages().get(0).getEventImage()).placeholder(R.drawable.tall_loading_img).error(R.drawable.tall_error_img).into(holder.sliderBinding.ivSliderEventImg);
        // eventDataChangeListener.eventDataListener(eventList);

        if (eventList.getGuestList() != null && eventList.getGuestList().size() > 0) {
            for (int i = 0; i < eventList.getGuestList().size(); i++) {
                switch (i) {
                    case 0:
                        holder.sliderBinding.ivShowUserOne.setVisibility(View.VISIBLE);
                        Glide.with(context).load(eventList.getGuestList().get(0).getGuestUser().getProfilePic()).into(holder.sliderBinding.ivShowUserOne);
                        break;
                    case 1:
                        holder.sliderBinding.ivShowUserTwo.setVisibility(View.VISIBLE);
                        Glide.with(context).load(eventList.getGuestList().get(1).getGuestUser().getProfilePic()).into(holder.sliderBinding.ivShowUserTwo);
                        break;
                    case 2:
                        holder.sliderBinding.ivShowThreeUser.setVisibility(View.VISIBLE);
                        Glide.with(context).load(eventList.getGuestList().get(2).getGuestUser().getProfilePic()).into(holder.sliderBinding.ivShowThreeUser);
                        break;
                }
            }

            if (eventList.getGuestList().size() > 4) {
                if (eventList.getGuestCount() != null) {
                    holder.sliderBinding.tvShowMoreUserLikeCount.setText(eventList.getGuestCount() + " + Going");
                    holder.sliderBinding.tvShowMoreUserLikeCount.setVisibility(View.VISIBLE);
                }
            }
        } else {
            holder.sliderBinding.ivShowUserOne.setVisibility(View.INVISIBLE);
            holder.sliderBinding.ivShowUserTwo.setVisibility(View.INVISIBLE);
            holder.sliderBinding.ivShowThreeUser.setVisibility(View.INVISIBLE);
            holder.sliderBinding.tvShowMoreUserLikeCount.setVisibility(View.INVISIBLE);
        }

        likeDislikeUI(holder.sliderBinding, eventList);

        // clickEvent(holder.sliderBinding,eventList);
    }

    @Override
    public int getItemCount() {
        return eventListArrayList.size();
    }

    class CarouselHolder extends RecyclerView.ViewHolder {
        private NearBySliderLayoutBinding sliderBinding;

        CarouselHolder(@NonNull NearBySliderLayoutBinding sliderBinding) {
            super(sliderBinding.getRoot());
            this.sliderBinding = sliderBinding;

            sliderBinding.getRoot().setOnClickListener(v->{
                int bottomSheetStatus = nearYouFragment.getBottomSheetStatus();
                if(bottomSheetStatus == 4){
                    bottomSheetOpenListener.openBottomSheet(true);
                }else if (bottomSheetStatus == 3){
                    bottomSheetOpenListener.openBottomSheet(false);
                }
            });

            sliderBinding.likeEventContainer.setOnClickListener(v -> {

                if (!CommonUtils.getCommonUtilsInstance().isUserLogin()) {
                    //ShowToast.infoToast(context, context.getString(R.string.please_login_rsvp));
                    CommonUtils.getCommonUtilsInstance().loginAlert((Activity) context,false,"");


                    return;
                }

                if (eventListArrayList.get(getAdapterPosition()).getIsLike() == 1) {
                    likeOrDislike(eventListArrayList.get(getAdapterPosition()).getId(), 0, getAdapterPosition(), sliderBinding, 1);
                    return;
                }
                likeOrDislike(eventListArrayList.get(getAdapterPosition()).getId(), 1, getAdapterPosition(), sliderBinding, -1);

            });

            sliderBinding.disLikeEventContainer.setOnClickListener(v -> {

                if (!CommonUtils.getCommonUtilsInstance().isUserLogin()) {
                    //ShowToast.infoToast(context, context.getString(R.string.please_login_rsvp));
                    CommonUtils.getCommonUtilsInstance().loginAlert((Activity) context,false,"");
                    return;
                }
                if (eventListArrayList.get(getAdapterPosition()).getUserLikes() == null)

                    if (eventListArrayList.get(getAdapterPosition()).getIsLike() == 2) {
                        likeOrDislike(eventListArrayList.get(getAdapterPosition()).getId(), 0, getAdapterPosition(), sliderBinding, 2);

                        return;
                    }
                likeOrDislike(eventListArrayList.get(getAdapterPosition()).getId(), 2, getAdapterPosition(), sliderBinding, -2);

            });
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    private void likeOrDislike(int eventId, int type, int itemPosition, NearBySliderLayoutBinding sliderBinding, int likeType) {
        myLoader.show("");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Constants.ApiKeyName.eventId, eventId);
        jsonObject.addProperty(Constants.type, type);
        AtomicInteger currentLikeCount = new AtomicInteger(Integer.parseInt(eventListArrayList.get(itemPosition).getCurrentLikeCount()));
        AtomicInteger currentDislikeCount = new AtomicInteger(Integer.parseInt(eventListArrayList.get(itemPosition).getCurrentDisLikeCount()));
        compositeDisposable.add(APICall.getApiInterface().eventLikeDislike(deviceToken, jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    myLoader.dismiss();
                    try {
                        String rawData = responseBody.string();
                        JSONObject obj = new JSONObject(rawData);
                        boolean success = obj.getBoolean("success");
                        if (success) {
                            if (type == 1) {
                                currentLikeCount.getAndIncrement();
                                eventListArrayList.get(itemPosition).setCurrentLikeCount(currentLikeCount.toString());
                                sliderBinding.likeEventContainer.setBackgroundResource(R.drawable.bubble_chooser_bg_wrapper);
                                sliderBinding.disLikeEventContainer.setBackgroundResource(R.drawable.bubble_chooser_border);
                                if (eventListArrayList.get(itemPosition).getIsLike() == 2) {
                                    currentDislikeCount.getAndDecrement();
                                    eventListArrayList.get(itemPosition).setCurrentDisLikeCount(currentDislikeCount.toString());
                                }
                                eventListArrayList.get(itemPosition).setIsLike(1);

                                ShowToast.infoToast(context, "Liked");
                                notifyDataSetChanged();
                                return;
                            } else if (type == 2) {
                                currentDislikeCount.getAndIncrement();
                                eventListArrayList.get(itemPosition).setCurrentDisLikeCount(currentDislikeCount.toString());
                                sliderBinding.likeEventContainer.setBackgroundResource(R.drawable.bubble_chooser_border);
                                sliderBinding.disLikeEventContainer.setBackgroundResource(R.drawable.bubble_chooser_bg_wrapper);
                                if (eventListArrayList.get(itemPosition).getIsLike() == 1) {
                                    currentLikeCount.getAndDecrement();
                                    eventListArrayList.get(itemPosition).setCurrentLikeCount(currentLikeCount.toString());
                                }
                                eventListArrayList.get(itemPosition).setIsLike(2);
                                ShowToast.infoToast(context, "Disliked");
                                notifyDataSetChanged();
                                return;
                            } else if (type == 0) {
                                if (eventListArrayList.get(itemPosition).getIsLike() == 0 && likeType == 1) {
                                    currentLikeCount.getAndIncrement();
                                    eventListArrayList.get(itemPosition).setCurrentLikeCount(currentLikeCount.toString());
                                    sliderBinding.likeEventContainer.setBackgroundResource(R.drawable.bubble_chooser_bg_wrapper);
                                    eventListArrayList.get(itemPosition).setIsLike(1);
                                    notifyDataSetChanged();
                                    return;

                                } else if (eventListArrayList.get(itemPosition).getIsLike() == 0 && likeType == 2) {
                                    currentDislikeCount.getAndIncrement();
                                    eventListArrayList.get(itemPosition).setCurrentDisLikeCount(currentDislikeCount.toString());
                                    sliderBinding.disLikeEventContainer.setBackgroundResource(R.drawable.bubble_chooser_bg_wrapper);
                                    eventListArrayList.get(itemPosition).setIsLike(2);
                                    notifyDataSetChanged();
                                    return;

                                } else if (eventListArrayList.get(itemPosition).getIsLike() == 1) {
                                    currentLikeCount.getAndDecrement();
                                    eventListArrayList.get(itemPosition).setCurrentLikeCount(currentLikeCount.toString());
                                    sliderBinding.likeEventContainer.setBackgroundResource(R.drawable.bubble_chooser_border);
                                    eventListArrayList.get(itemPosition).setIsLike(0);
                                    notifyDataSetChanged();
                                    return;

                                } else if (eventListArrayList.get(itemPosition).getIsLike() == 2) {
                                    currentDislikeCount.getAndDecrement();
                                    eventListArrayList.get(itemPosition).setCurrentDisLikeCount(currentDislikeCount.toString());
                                    sliderBinding.disLikeEventContainer.setBackgroundResource(R.drawable.bubble_chooser_border);
                                    eventListArrayList.get(itemPosition).setIsLike(0);
                                    notifyDataSetChanged();
                                    return;
                                }
                            }
                            return;
                        }
                        ShowToast.errorToast(context, context.getString(R.string.something_wrong));


                    } catch (Exception e) {
                        Log.d("bjbnl", "likeOrDislike: " + e.getMessage());
                        ShowToast.errorToast(context, context.getString(R.string.something_wrong));
                    }
                })
        );

    }

    private void likeDislikeUI(NearBySliderLayoutBinding sliderBinding, EventList eventList) {
        if (eventList.getUserLikes() != null && eventList.getUserLikes().getLike()) {
            sliderBinding.likeEventContainer.setBackgroundResource(R.drawable.bubble_chooser_bg_wrapper);
            sliderBinding.disLikeEventContainer.setBackgroundResource(R.drawable.bubble_chooser_border);
        } else if (eventList.getUserLikes() != null && eventList.getUserLikes().getDisLike()) {
            sliderBinding.likeEventContainer.setBackgroundResource(R.drawable.bubble_chooser_border);
            sliderBinding.disLikeEventContainer.setBackgroundResource(R.drawable.bubble_chooser_bg_wrapper);
        }

        /* isLike 2 shows user dislike the event or 1 means like, o means default*/
        if (eventList.getStartDate() != null) {
            String[] getDate = CommonUtils.getCommonUtilsInstance().getSplitMonthDate(eventList.getStartDate()).split(",");
            sliderBinding.tvShowDateInNumeric.setText(getDate[0]);
            sliderBinding.ivShowDateInName.setText(getDate[1]);
        }
        if (eventList.getCurrentLikeCount() != null)
            sliderBinding.tvEventLikeCount.setText(eventList.getCurrentLikeCount());
        if (eventList.getCurrentDisLikeCount() != null)
            sliderBinding.tvShowDislike.setText(eventList.getCurrentDisLikeCount());
    }


}