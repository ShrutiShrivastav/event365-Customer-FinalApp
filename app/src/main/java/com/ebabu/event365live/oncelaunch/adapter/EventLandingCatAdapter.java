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
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.oncelaunch.activity.EventListActivity;
import com.ebabu.event365live.oncelaunch.modal.nearbynoauth.NearByNoAuthModal;
import java.util.List;

public class EventLandingCatAdapter extends RecyclerViewBouncy.Adapter<EventLandingCatAdapter.LandingHolder>{
    private Context context;
    LandingEventCatChooseLayoutBinding chooseLayoutBinding;
    private final int AUTOCOMPLETE_REQUEST_CODE = 1001;
    private List<NearByNoAuthModal.Category> categoryList;

    public EventLandingCatAdapter(Context context, List<NearByNoAuthModal.Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public LandingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        chooseLayoutBinding = DataBindingUtil.inflate(inflater,R.layout.landing_event_cat_choose_layout,parent,false);
        return new LandingHolder(chooseLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull LandingHolder holder, int position) {
        NearByNoAuthModal.Category categoryData = categoryList.get(position);
        if(categoryData.getCategoryName() != null)
        holder.binding.tvEventItemName.setText(categoryData.getCategoryName());
    }
    @Override
    public int getItemCount() {
        return categoryList.size();
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
            Intent eventIntent = new Intent(context,EventListActivity.class);
            eventIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            eventIntent.putExtra(Constants.ApiKeyName.categoryId,categoryList.get(getAdapterPosition()-1).getId());
            context.startActivity(eventIntent);
        }
    }
}
