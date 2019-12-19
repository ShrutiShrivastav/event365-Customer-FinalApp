package com.ebabu.event365live.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.oncelaunch.LandingActivity;
import com.ebabu.event365live.userinfo.fragment.UpdateInfoFragmentDialog;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Driver;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.ebabu.event365live.httprequest.Constants.AUTOCOMPLETE_REQUEST_CODE;

public class CommonUtils{
    private static CommonUtils mCommonUtilsInstance;
    private FusedCurrentLocationListener currentLocationListener;
    public static CommonUtils getCommonUtilsInstance(){
        if(mCommonUtilsInstance == null)
        {
            mCommonUtilsInstance = new CommonUtils();
        }
        return mCommonUtilsInstance;
    }

    public void transparentStatusAndNavigation(AppCompatActivity activity){
        //make full transparent statusBar
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, true, activity);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);

        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false, activity);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }

    }

    public void transparentStatusBar(AppCompatActivity activity){
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowTransparentFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowTransparentFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }


    public static void showSuccessToast(Context context, String message){
        if (message != null && !message.isEmpty()) {
            MDToast mdToast = MDToast.makeText(context, message, MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
            mdToast.setIcon(null);
            mdToast.show();
        }
    }

    public static void showErrorToast(Context context, String message){
        if (message != null && !message.isEmpty()) {
            MDToast mdToast = MDToast.makeText(context, message, MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR);
            mdToast.setIcon(null);
            mdToast.show();
        }
    }

    public static void showWarningToast(Context context, String message){
        if (message != null && !message.isEmpty()) {
            MDToast mdToast = MDToast.makeText(context, message, MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING);
            mdToast.setIcon(null);
            mdToast.show();
        }
    }

    public static void showKeyboard(Context context, View view){
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm !=null)
                imm.showSoftInput(view, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideKeyboard(Context context, View view) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm != null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideSoftKeyboard(AppCompatActivity act){
        try {
            if (act.getCurrentFocus() != null) {
                InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null)
                    imm.hideSoftInputFromWindow(act.getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();

    }

    private static void setWindowFlag(final int bits, boolean on, AppCompatActivity activity) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public static void setWindowTransparentFlag(AppCompatActivity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public <T> void launchActivity(Context context, Class<T> tClass,boolean isNeedClearStack){
        Intent intent = new Intent(context,tClass);
        if(isNeedClearStack)
        { intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);}
        context.startActivity(intent);
    }

    public String getDateMonthYearName(String dateFormat, boolean isYearNeed){
        int getDate = 0;
        int getYear = 0;
        String getMonth = "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy",Locale.ENGLISH);
            Date date = inputFormat.parse(dateFormat);
            Calendar calendar = outputFormat.getCalendar();
            calendar.setTime(date);
            getDate = calendar.get(Calendar.DATE);
            getYear = calendar.get(Calendar.YEAR);
            getMonth = (String) DateFormat.format("MMM",date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(isYearNeed)
            return getDate+" "+getMonth + " "+getYear ;

        return getDate+" "+getMonth;
    }

    public String getStartEndEventTime(String eventTime){
        String formattedTime = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss", Locale.ENGLISH);
            Date dt = sdf.parse(eventTime);

            SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm a",Locale.ENGLISH);
            formattedTime = sdfs.format(dt).toLowerCase();

        } catch (ParseException e) {
            e.printStackTrace();

        }
        return formattedTime;
    }
    public String getSplitMonthDate(String dateFormat){
        int getDate = 0;
        String getMonth = "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy",Locale.ENGLISH);
            Date date = inputFormat.parse(dateFormat);
            Calendar calendar = outputFormat.getCalendar();
            calendar.setTime(date);
            getDate = calendar.get(Calendar.DATE);
            getMonth = (String) DateFormat.format("MMM",date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return getDate+","+getMonth;
    }
    public void launchSelectAddressFrag(Activity activity, UpdateInfoFragmentDialog dialog, boolean isFromDialog){
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields).setTypeFilter(TypeFilter.CITIES)
                .build(activity);
        if(isFromDialog){
            dialog.startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            return;
        }
        activity.startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    public void validateDeviceAuth(String deviceAuth){
        SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.deviceAuth,deviceAuth);
    }

    public String getDeviceAuth() {
        return SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.deviceAuth);
    }

    public void  getUserId(String userId){
        SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.userId,userId);
    }



    public void validateUser(JSONObject res){

        try {
            String userId = res.getJSONObject("data").getString("id");
            String name = res.getJSONObject("data").getString("name");
            String profilePic = res.getJSONObject("data").getString("profilePic");
            Boolean isRemind = res.getJSONObject("data").getBoolean("isRemind");
            Boolean isNotify = res.getJSONObject("data").getBoolean("isNotify");

            SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.userName,name);
            SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.userId,userId);
            SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.profilePic,profilePic);
            SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.isRemind,isRemind);
            SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.isNotify,isNotify);

            CommonUtils.getCommonUtilsInstance().validateSwipeMode(true);
            CommonUtils.getCommonUtilsInstance().validateUserIsLogin(true);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(){
        SessionValidation.getPrefsHelper().delete(Constants.SharedKeyName.userName);
        SessionValidation.getPrefsHelper().delete(Constants.SharedKeyName.userId);
        SessionValidation.getPrefsHelper().delete(Constants.SharedKeyName.profilePic);
        SessionValidation.getPrefsHelper().delete(Constants.SharedKeyName.isRemind);
        SessionValidation.getPrefsHelper().delete(Constants.SharedKeyName.isNotify);
        SessionValidation.getPrefsHelper().delete(Constants.SharedKeyName.isUserLogin);
        SessionValidation.getPrefsHelper().delete(Constants.SharedKeyName.isHomeSwipeView);
        SessionValidation.getPrefsHelper().delete(Constants.SharedKeyName.deviceAuth);
        SessionValidation.getPrefsHelper().delete(Constants.SharedKeyName.deviceToken);

    }

    public void validateUserIdFromErrorResponse(JSONObject errorRes){
        try {
             String id = errorRes.getJSONObject("data").getString("id");
            SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.userId,id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getUserId(){
        return SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.userId);
    }

    public String getAddressFromLatLng(Context context,String lat, String lng){
        String address = "";
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lng), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }
    public void getCurrentLocation(final Activity activity) {
        getCurrentLocationListener((FusedCurrentLocationListener) activity);

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},Constants.CURRENT_FUSED_LOCATION_REQUEST);
            return;
        }

        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        //TODO: UI updates.
                    }
                }
            }
        };
               FusedLocationProviderClient fusedCurrentLocationListener = LocationServices.getFusedLocationProviderClient(activity);


//        LocationServices.getFusedLocationProviderClient(activity).requestLocationUpdates(mLocationRequest,mLocationCallback,null);
//
//        LocationServices.getFusedLocationProviderClient(activity).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//               // Log.d("nflanfnaklfnlan", "onSuccess: "+location.getLatitude());
//            }
//        });

        fusedCurrentLocationListener.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    currentLocationListener.getFusedCurrentSuccess(location);
                }

              //  Log.d("nflankfnlanlfa", "onSuccess: "+location.getLatitude());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                currentLocationListener.getFusedCurrentFailed(e);
                Log.d("nflankfnlanlfa", "onFailure: "+e.getMessage());
            }
        });

    }

    public interface FusedCurrentLocationListener{
        void getFusedCurrentSuccess(Location location);
        void getFusedCurrentFailed(Exception e);
    }

    private void getCurrentLocationListener(FusedCurrentLocationListener currentLocationListener){
        this.currentLocationListener = currentLocationListener;
    }

    public void shareIntent(Context context){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        context.startActivity(shareIntent);
    }

    public static void openBrowser(Context mContext,String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        mContext.startActivity(i);
    }

    public void validateUserIsLogin(boolean isUserLogin){
        SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.isUserLogin,isUserLogin);
    }

    public boolean isUserLogin(){
        return SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.isUserLogin) == null ? false : SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.isUserLogin);
    }

    public void validateSwipeMode(boolean isSwipeMode){
        SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.isHomeSwipeView,isSwipeMode);
    }

    public boolean isSwipeMode(){
        return SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.isHomeSwipeView) == null ? false : SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.isHomeSwipeView) ;
    }
}
