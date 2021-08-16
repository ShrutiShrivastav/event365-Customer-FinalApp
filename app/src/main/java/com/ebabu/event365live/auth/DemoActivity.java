package com.ebabu.event365live.auth;

import android.graphics.Typeface;
import android.os.Bundle;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityDemoBinding;
import com.kienht.bubblepicker.BubblePickerListener;
import com.kienht.bubblepicker.adapter.BubblePickerAdapter;
import com.kienht.bubblepicker.model.PickerItem;

import org.jetbrains.annotations.NotNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;

public class DemoActivity extends AppCompatActivity {

    private ActivityDemoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_demo);

        String[] titles = getResources().getStringArray(R.array.countries);
        String[] MM = getResources().getStringArray(R.array.countries);

        BubblePickerAdapter categoryBubbleAdapter = new BubblePickerAdapter() {
            @Override
            public int getTotalCount() {
                return titles.length;
            }

            @NotNull
            @Override
            public PickerItem getItem(int i) {
                PickerItem pickerItem = new PickerItem();
                pickerItem.setTitle(titles[i]);
                pickerItem.setColor(ContextCompat.getColor(DemoActivity.this, R.color.bubbleColor));
                pickerItem.setCustomData(MM[i]);
                pickerItem.setTextColor(ContextCompat.getColor(DemoActivity.this, R.color.bubbleTextColor));
                Typeface regular = ResourcesCompat.getFont(DemoActivity.this, R.font.caros_medium);
                pickerItem.setTypeface(regular);
                return pickerItem;

            }
        };

        binding.bubbleDemo.setAdapter(categoryBubbleAdapter);
        binding.bubbleDemo.onPause();
        binding.bubbleDemo.onResume();
         binding.bubbleDemo.setMaxSelectedCount(5);
         binding.bubbleDemo.setAlwaysSelected(false);
         binding.bubbleDemo.setCenterImmediately(true);
         binding.bubbleDemo.setBubbleSize(80);
         binding.bubbleDemo.setSwipeMoveSpeed(.4f);

         binding.bubbleDemo.setListener(new BubblePickerListener() {
            @Override
            public void onBubbleSelected(@NotNull PickerItem pickerItem) {

            }

            @Override
            public void onBubbleDeselected(@NotNull PickerItem pickerItem) {
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(binding.bubbleDemo!=null)
            binding.bubbleDemo.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if(binding.bubbleDemo!=null)
                binding.bubbleDemo.onPause();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
