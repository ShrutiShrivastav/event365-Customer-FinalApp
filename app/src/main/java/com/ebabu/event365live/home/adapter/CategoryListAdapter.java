package com.ebabu.event365live.home.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ebabu.event365live.R;
import com.ebabu.event365live.home.modal.GetCategoryModal;
import com.ebabu.event365live.home.modal.SubCategoryModal;
import com.ebabu.event365live.httprequest.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.List;
public class CategoryListAdapter extends BaseAdapter {
    private Context context;
    private List<GetCategoryModal.Data.GetCategoryData> getCategoryData;
    private LayoutInflater mInflater;
    private int mSelectedIndex = 0;

    public CategoryListAdapter(Context context, List<GetCategoryModal.Data.GetCategoryData> getCategoryData) {
        this.getCategoryData = getCategoryData;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return getCategoryData.size();
    }

    @Override
    public Object getItem(int i) {
        return getCategoryData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View itemView = convertView;
        if(itemView == null)
            itemView = mInflater.inflate(R.layout.custom_category_text,parent,false);
        GetCategoryModal.Data.GetCategoryData categoryData = getCategoryData.get(position);
        if(categoryData != null){
            TextView showCatName = itemView.findViewById(R.id.tvShowCatName);
            showCatName.setText(categoryData.getCategoryName());
        }



      //  itemView.setOnClickListener(v->getCatSelectedData.selectedData(categoryData.getCategoryName()));

        return itemView;
    }

//    @Override
//    public View getDropDownView(int position, View convertView, ViewGroup parent) {
//        View itemView = super.getDropDownView(position, convertView, parent);
//
//        if(position == mSelectedIndex) {
//            itemView.setBackgroundColor(Color.parseColor("#3db6a8de"));
//        }
//        else {
//            itemView.setBackgroundColor(Color.TRANSPARENT);
//        }
//
//        return itemView;
//    }

    public void setSelection(int position) {
        mSelectedIndex =  position;
        notifyDataSetChanged();
    }

  public interface GetCatSelectedData{
        void selectedData(String selectedItem);
   }
}
