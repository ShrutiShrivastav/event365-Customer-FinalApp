package com.ebabu.event365live.utils;

import android.text.Editable;
import android.text.NoCopySpan;
import android.view.View;

public interface TextWatcherExtendedListener extends NoCopySpan
{
    void afterTextChanged(View v, Editable s);

    void onTextChanged(View v, CharSequence s, int start, int before, int count);

    void beforeTextChanged(View v, CharSequence s, int start, int count, int after);
}