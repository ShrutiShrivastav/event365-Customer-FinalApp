package com.ebabu.event365live.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.ebabu.event365live.R;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.oncelaunch.LandingActivity;

public class SessionValidation {

    private static SessionValidation sessionValidationInstance;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    Activity activity;

    private SessionValidation(Context context) {
        sessionValidationInstance = this;
        String prefFileName = context.getString(R.string.app_name);
        sharedPreferences = context.getSharedPreferences(prefFileName,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    static void init(Context context){
        if(sessionValidationInstance == null){
            new SessionValidation(context);
        } }

    public static SessionValidation getPrefsHelper() {
        return sessionValidationInstance;
    }

    public boolean delete(String key) {
        boolean isDeleted = false;
        if (sharedPreferences.contains(key)) {
            isDeleted = editor.remove(key).commit();
        }
        return isDeleted; }

    public boolean savePref(String key, Object value) {
        delete(key);
        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Enum) {
            editor.putString(key, value.toString());
        } else if (value != null) {
            throw new RuntimeException("Attempting to save non-primitive preference");
        }
        return editor.commit();
    }

    public boolean isPrefExists(String key) {
        return sharedPreferences.contains(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getPref(String key, T defValue) {
        T returnValue = (T) sharedPreferences.getAll().get(key);
        return returnValue == null ? defValue : returnValue;
    }

    @SuppressWarnings("unchecked")
    public <T> T getPref(String key) {

        return (T) sharedPreferences.getAll().get(key);
    }


}
