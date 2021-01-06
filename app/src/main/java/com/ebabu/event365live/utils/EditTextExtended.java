package com.ebabu.event365live.utils;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;


public class EditTextExtended extends AppCompatEditText {

    private TextWatcherExtendedListener  mListeners = null;


    public EditTextExtended(Context context) {
        super(context);
    }

    public EditTextExtended(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public EditTextExtended(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }


    public void addTextChangedListener(TextWatcherExtendedListener watcher)
    {
        if (mListeners == null)
        {
            mListeners = watcher;
        }
    }

    void  sendBeforeTextChanged(CharSequence text, int start, int before, int after)
    {
        if (mListeners != null)
        {
            mListeners.beforeTextChanged(this, text, start, before, after);
        }
    }

    void  sendOnTextChanged(CharSequence text, int start, int before,int after)
    {
        if (mListeners != null)
        {
            mListeners.onTextChanged(this, text, start, before, after);
        }
    }

    void  sendAfterTextChanged(Editable text)
    {
        if (mListeners != null)
        {
            mListeners.afterTextChanged(this, text);
        }
    }

}
