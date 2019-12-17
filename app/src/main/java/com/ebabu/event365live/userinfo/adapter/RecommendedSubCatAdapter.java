package com.ebabu.event365live.userinfo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ebabu.event365live.R;
import com.ebabu.event365live.homedrawer.modal.SelectedEventCategoryModal;

import java.util.List;

public class RecommendedSubCatAdapter extends RecyclerView.Adapter<RecommendedSubCatAdapter.EventChooseHolder>{
    private Context context;
    private List<SelectedEventCategoryModal> selectedSubCatEvent;

    public RecommendedSubCatAdapter(Context context, List<SelectedEventCategoryModal> selectedSubCatEvent) {
        this.context = context;
        this.selectedSubCatEvent = selectedSubCatEvent;

    }
    @NonNull
    @Override
    public RecommendedSubCatAdapter.EventChooseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recommended_event_select_layout,parent,false);
        return new RecommendedSubCatAdapter.EventChooseHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendedSubCatAdapter.EventChooseHolder holder, int position) {

        SelectedEventCategoryModal modal = selectedSubCatEvent.get(position);
        holder.tvShowEventName.setText(modal.getEventName());
    }

    @Override
    public int getItemCount() {
        return selectedSubCatEvent.size() ;
    }

    class EventChooseHolder extends RecyclerView.ViewHolder {
        TextView tvShowEventName;
        EventChooseHolder(@NonNull View view) {
            super(view);
            tvShowEventName = itemView.findViewById(R.id.tvShowEventName);
        }
    }

    public void removeSubCatItem(String categoryModal)
    {
        for(int i=0;i<selectedSubCatEvent.size();i++)
        {
            if(selectedSubCatEvent.get(i).getId().equalsIgnoreCase(categoryModal))
            {
                selectedSubCatEvent.remove(i);
                notifyDataSetChanged();
                break;
            }
        }
    }
//    //public List<SelectedEventCategoryModal> getCurrentSelectedItem(){
//        return eventCategoryModals;
//    }

}
