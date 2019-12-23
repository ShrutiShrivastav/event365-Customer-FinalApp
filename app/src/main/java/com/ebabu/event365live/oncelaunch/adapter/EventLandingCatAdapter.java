package com.ebabu.event365live.oncelaunch.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import com.ebabu.event365live.R;
import com.ebabu.event365live.bouncerecycler.RecyclerViewBouncy;
import com.ebabu.event365live.databinding.LandingEventCatChooseLayoutBinding;
import com.ebabu.event365live.homedrawer.modal.searchevent.SearchEventModal;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.oncelaunch.activity.EventListActivity;
import com.ebabu.event365live.oncelaunch.modal.nearbynoauth.NearByNoAuthModal;
import java.util.List;

public class EventLandingCatAdapter extends RecyclerViewBouncy.Adapter<EventLandingCatAdapter.LandingHolder>{
    private Context context;
    LandingEventCatChooseLayoutBinding chooseLayoutBinding;
    private List<NearByNoAuthModal.Category> categoryList;
    private List<SearchEventModal.RecentSearch> recentSearchList;
    private boolean isFromSearchScreen;
    private String recentSearchKeyword;

    public EventLandingCatAdapter(List<NearByNoAuthModal.Category> categoryList, List<SearchEventModal.RecentSearch> recentSearchList, boolean isFromSearchScreen) {
        this.categoryList = categoryList;
        this.recentSearchList = recentSearchList;
        this.isFromSearchScreen = isFromSearchScreen;
    }

    @NonNull
    @Override
    public LandingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        chooseLayoutBinding = DataBindingUtil.inflate(inflater,R.layout.landing_event_cat_choose_layout,parent,false);
        return new LandingHolder(chooseLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull LandingHolder holder, int position) {
        if(!isFromSearchScreen){
            NearByNoAuthModal.Category categoryData = categoryList.get(position);
            if(categoryData.getCategoryName() != null)
                holder.binding.tvEventItemName.setText(categoryData.getCategoryName());
            return;
        }
        SearchEventModal.RecentSearch recentSearch = recentSearchList.get(position);
        holder.binding.tvEventItemName.setText(recentSearch.getText());


    }
    @Override
    public int getItemCount() {
        return isFromSearchScreen? recentSearchList.size() :categoryList.size();
    }

    class LandingHolder extends RecyclerViewBouncy.ViewHolder implements View.OnClickListener {
        LandingEventCatChooseLayoutBinding binding;
        LandingHolder(@NonNull LandingEventCatChooseLayoutBinding  binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.tvEventItemName.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(!isFromSearchScreen){
                Intent eventIntent = new Intent(context,EventListActivity.class);
                eventIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                eventIntent.putExtra(Constants.ApiKeyName.categoryId,categoryList.get(getAdapterPosition()-1).getId());
                context.startActivity(eventIntent);
                return;
            }
            recentSearchKeyword = recentSearchList.get(getAdapterPosition()-1).getText();
        }
    }

    public String getRecentKeyWord(){
        return recentSearchKeyword;
    }
}
