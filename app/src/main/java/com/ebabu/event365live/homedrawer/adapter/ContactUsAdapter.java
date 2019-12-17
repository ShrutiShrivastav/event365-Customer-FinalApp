package com.ebabu.event365live.homedrawer.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ebabu.event365live.R;
import com.ebabu.event365live.homedrawer.modal.GetIssueModal;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ContactUsAdapter extends BaseAdapter {
    private Context context;

    private LayoutInflater mInflater;
    private int mSelectedIndex = -1;
    private List<GetIssueModal.GetIssueData> getIssueDataList;

    public ContactUsAdapter(Context context, List<GetIssueModal.GetIssueData> getIssueDataList) {
        this.context = context;
        this.getIssueDataList = getIssueDataList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return getIssueDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return getIssueDataList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View itemView = convertView;
        if (itemView == null)
            itemView = mInflater.inflate(R.layout.custom_category_text, parent, false);
        GetIssueModal.GetIssueData getIssueData = getIssueDataList.get(position);
        TextView showCatName = itemView.findViewById(R.id.tvShowCatName);
        showCatName.setText(getIssueData.getHeading());

        return itemView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View itemView = super.getDropDownView(position, convertView, parent);

        if (position == mSelectedIndex) {
            itemView.setBackgroundColor(Color.parseColor("#3db6a8de"));
        } else {
            itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        return itemView;
    }

    public void setSelection(int position) {
        mSelectedIndex = position;
        notifyDataSetChanged();
    }
}
