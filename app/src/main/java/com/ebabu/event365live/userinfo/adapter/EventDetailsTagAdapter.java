package com.ebabu.event365live.userinfo.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.EventDetailsTagLayoutBinding;

import java.util.List;

public class EventDetailsTagAdapter extends RecyclerView.Adapter<EventDetailsTagAdapter.TagHolder> {

    private Context context;
    private List<String> showTagName;

    public EventDetailsTagAdapter(List<String> showTagName) {
        this.showTagName = showTagName;
    }

    @NonNull
    @Override
    public TagHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

       EventDetailsTagLayoutBinding tagLayoutBinding =  DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.event_details_tag_layout,parent,false);

        return new TagHolder(tagLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TagHolder holder, int position) {
        if(position == 0){
            holder.tagLayoutBinding.tvShowTagName.setText(showTagName.get(position));
            holder.tagLayoutBinding.tvShowTagName.setBackgroundResource(R.drawable.sky_round_continer);
        }
        else if(position % 2 == 0){
            holder.tagLayoutBinding.tvShowTagName.setText(showTagName.get(position));
            holder.tagLayoutBinding.tvShowTagName.setBackgroundResource(R.drawable.orange_container);
        }else if(position % 3 != 0){
            holder.tagLayoutBinding.tvShowTagName.setText(showTagName.get(position));
            holder.tagLayoutBinding.tvShowTagName.setBackgroundResource(R.drawable.pink_round_container);
        }
        else {
            holder.tagLayoutBinding.tvShowTagName.setText(showTagName.get(position));
            holder.tagLayoutBinding.tvShowTagName.setBackgroundResource(R.drawable.sky_round_continer);
        }


    }

    @Override
    public int getItemCount() {
        return showTagName.size();
    }

    class TagHolder extends RecyclerView.ViewHolder {
        EventDetailsTagLayoutBinding tagLayoutBinding;
        public TagHolder(@NonNull EventDetailsTagLayoutBinding tagLayoutBinding) {
            super(tagLayoutBinding.getRoot());
            this.tagLayoutBinding = tagLayoutBinding;
        }
    }
}
