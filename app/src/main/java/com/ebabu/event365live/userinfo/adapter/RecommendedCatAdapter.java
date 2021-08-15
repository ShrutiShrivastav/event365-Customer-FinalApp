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

public class RecommendedCatAdapter extends RecyclerView.Adapter<RecommendedCatAdapter.EventChooseHolder>{
    private Context context;
    private List<SelectedEventCategoryModal> selectedEvent;

    public RecommendedCatAdapter(Context context, List<SelectedEventCategoryModal> selectedEvent) {
        this.context = context;
        this.selectedEvent = selectedEvent;

    }

    public void setSabCategoryItem(List<SelectedEventCategoryModal> bubbleSelectedItem){
        if(selectedEvent.size()>0){
            selectedEvent.clear();
            notifyDataSetChanged();
            selectedEvent = bubbleSelectedItem;
            notifyDataSetChanged();
        }
    }
    @NonNull
    @Override
    public EventChooseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recommended_event_select_layout,parent,false);
        return new EventChooseHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventChooseHolder holder, int position) {

            SelectedEventCategoryModal modal = selectedEvent.get(position);
            holder.tvShowEventName.setText(modal.getEventName());

    }

    @Override
    public int getItemCount() {
        return selectedEvent.size() ;
    }

    class EventChooseHolder extends RecyclerView.ViewHolder {
     TextView tvShowEventName;
        EventChooseHolder(@NonNull View view) {
            super(view);
            tvShowEventName = itemView.findViewById(R.id.tvShowEventName);
        }
    }

    public void removeCatItem(String categoryModal)
    {
        for(int i=0;i<selectedEvent.size();i++)
        {
            if(selectedEvent.get(i).getId().equalsIgnoreCase(categoryModal))
            {
                selectedEvent.remove(i);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public List<SelectedEventCategoryModal> getCurrentSelectedItem(){
        return selectedEvent;
    }

}
