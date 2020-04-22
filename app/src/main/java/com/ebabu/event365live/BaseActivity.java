package com.ebabu.event365live;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ebabu.event365live.utils.MyLoader;

/**
 * @author Deep Raj Pandey
 * @mail deeprajpandey5@gmail.com
 * @Company EB
 */
public class BaseActivity extends AppCompatActivity {

    public MyLoader myLoader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLoader = new MyLoader(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("fnalknfa", "onResume: Base Activty ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myLoader.dialogDismiss();
    }
}
