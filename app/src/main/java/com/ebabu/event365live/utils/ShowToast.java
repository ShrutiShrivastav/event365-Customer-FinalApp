package com.ebabu.event365live.utils;

import android.content.Context;
import android.widget.Toast;

import com.ebabu.event365live.R;
import com.valdesekamdem.library.mdtoast.MDToast;

public class ShowToast {

    public static void errorToast(Context context, String msg){
        MDToast.makeText(context,msg,MDToast.LENGTH_LONG,MDToast.TYPE_ERROR).show();
    }
    public static void warningToast(Context context, String msg){
        MDToast.makeText(context,msg,MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING).show();
    }
    public static void successToast(Context context, String msg){
        MDToast.makeText(context,msg,MDToast.LENGTH_SHORT,MDToast.TYPE_SUCCESS).show();
    }
    public static void infoToast(Context context, String msg){
        MDToast.makeText(context,msg,MDToast.LENGTH_SHORT,MDToast.TYPE_INFO).show();
    }

    public static void toastNoConnection(Context context) {
        MDToast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
    }

    public static void toastAuthFailed(Context context) {
        MDToast.makeText(context, context.getString(R.string.error_auth_failed), Toast.LENGTH_SHORT).show();
    }

    public static void toastAuthCancelled(Context context) {
        MDToast.makeText(context, context.getString(R.string.error_auth_cancelled), Toast.LENGTH_SHORT).show();
    }

    public static void toastWentWrongMsg(Context context) {
        MDToast.makeText(context, context.getString(R.string.error_something_wrong), Toast.LENGTH_SHORT).show();
    }
    
    public static void toastComingSoon(Context context) {
        MDToast.makeText(context, context.getString(R.string.error_coming_soon), Toast.LENGTH_SHORT).show();
    }


    public static void infoToastWrong(Context context) {
        infoToast(context, context.getString(R.string.something_wrong));
    }
}
