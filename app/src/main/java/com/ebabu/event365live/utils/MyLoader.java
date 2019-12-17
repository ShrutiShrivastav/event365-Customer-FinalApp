package com.ebabu.event365live.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Window;

import androidx.databinding.DataBindingUtil;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.CustomLayoutLoaderBinding;

public class MyLoader {


    private CustomLayoutLoaderBinding loaderBinding;
    private Dialog dialog;


    public MyLoader(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        loaderBinding = DataBindingUtil.inflate(inflater, R.layout.custom_layout_loader, null, false);
        try {
            dialog = new Dialog(context);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setContentView(loaderBinding.getRoot());
            dialog.getWindow().setLayout(-1, -2);
            dialog.setCancelable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show(String msg) {
        loaderBinding.tvShowProgressName.setText(msg);
        dialog.show();
    }



    public void dismiss() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

}



