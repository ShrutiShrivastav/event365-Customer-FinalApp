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
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.applozic.mobicomkit.Applozic;
import com.applozic.mobicomkit.ApplozicClient;
import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.PushNotificationTask;
import com.applozic.mobicomkit.api.account.user.User;
import com.applozic.mobicomkit.api.account.user.UserLoginTask;
import com.applozic.mobicomkit.listners.AlLogoutHandler;
import com.ebabu.event365live.R;
import com.ebabu.event365live.auth.activity.LoginActivity;
import com.ebabu.event365live.home.activity.HomeActivity;
import com.ebabu.event365live.homedrawer.activity.ChooseRecommendedSubCatActivity;
import com.ebabu.event365live.homedrawer.modal.searchevent.SearchEventModal;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.oncelaunch.LandingActivity;
import com.ebabu.event365live.userinfo.fragment.UpdateInfoFragmentDialog;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Driver;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.ebabu.event365live.httprequest.Constants.AUTOCOMPLETE_REQUEST_CODE;
import static com.ebabu.event365live.httprequest.Constants.favoritesList;

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

    public <T> void launchActivity(Context context, Class<T> tClass, boolean isNeedClearStack){
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

    public String getStartEndEventTime(String eventTime) {
        String formattedTime = "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
            inputFormat.setTimeZone(TimeZone.getTimeZone(Calendar.getInstance().getTimeZone().getDisplayName()));
            Date time = inputFormat.parse(eventTime);
            SimpleDateFormat sdfs = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
            formattedTime = sdfs.format(time).toLowerCase();
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("fasbkfbasjka", "ParseException: "+e.getMessage());
        }
        return formattedTime;
    }

    public String getDateMonthName(String dateFormat){
        int getDate = 0;
        String getMonth = "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",Locale.ENGLISH);
            inputFormat.setTimeZone(TimeZone.getTimeZone(Calendar.getInstance().getTimeZone().getDisplayName()));
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy",Locale.ENGLISH);
            outputFormat.setTimeZone(TimeZone.getTimeZone(Calendar.getInstance().getTimeZone().getDisplayName()));
            Date date = inputFormat.parse(dateFormat);
            Calendar calendar = outputFormat.getCalendar();
            calendar.setTime(date);
            getDate = calendar.get(Calendar.DATE);
            getMonth = (String) DateFormat.format("MMM",date);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("sfsafavfdhdhdss", "ParseException: "+e.getMessage());
        }
        return getDate+" "+getMonth;
    }

    public String getCurrentDate(String setDate){
        String getDate = "";
        try {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",Locale.ENGLISH);
        inputFormat.setTimeZone(TimeZone.getTimeZone(calendar.getTimeZone().getDisplayName()));
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy",Locale.ENGLISH);
            outputFormat.setTimeZone(TimeZone.getTimeZone(calendar.getTimeZone().getDisplayName()));
            Date date = inputFormat.parse(setDate);


        String getCompareDate = outputFormat.format(date);

        Date today = calendar.getTime();
        String getTodayDate = outputFormat.format(today);
        /*getting tomorrow date */
        calendar.add(Calendar.DATE, -1);
        Date yesterday = calendar.getTime();
        String getTomorrowDate = outputFormat.format(yesterday);

        Log.d("fnalksfnksal", getTodayDate+" getCurrentDate: "+getTomorrowDate+" == "+getCompareDate);


        if(getTodayDate.equalsIgnoreCase(getCompareDate)){
            getDate = "Today";
        }else if(getTomorrowDate.equalsIgnoreCase(getCompareDate)){
            getDate = "Yesterday";
        }else {
            getDate = getCompareDate;
        }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return getDate;

    }


    public String getSplitMonthDate(String dateFormat){
        int getDate = 0;
        String getMonth = "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
            inputFormat.setTimeZone(TimeZone.getTimeZone(Calendar.getInstance().getTimeZone().getDisplayName()));
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy",Locale.ENGLISH);
            outputFormat.setTimeZone(TimeZone.getTimeZone(Calendar.getInstance().getTimeZone().getDisplayName()));
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

    public void validateUser(String userId, String userName, boolean isRemind, boolean isNotify, String customerId){
        SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.userId, userId);
        SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.userName, userName);
        SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.isRemind, isRemind);
        SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.isNotify, isNotify);
        SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.isHomeSwipeView, true);
        SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.forStripeCustomerId, customerId);
        CommonUtils.getCommonUtilsInstance().validateUserIsLogin(true);
    }

    public void deleteUser(){
        SessionValidation.getPrefsHelper().delete(Constants.SharedKeyName.userName);
        SessionValidation.getPrefsHelper().delete(Constants.SharedKeyName.userId);
        if(SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.profilePic) !=null)
        SessionValidation.getPrefsHelper().delete(Constants.SharedKeyName.profilePic);
        SessionValidation.getPrefsHelper().delete(Constants.SharedKeyName.isRemind);
        SessionValidation.getPrefsHelper().delete(Constants.SharedKeyName.isNotify);
        SessionValidation.getPrefsHelper().delete(Constants.SharedKeyName.isUserLogin);
        SessionValidation.getPrefsHelper().delete(Constants.SharedKeyName.isHomeSwipeView);
        SessionValidation.getPrefsHelper().delete(Constants.SharedKeyName.deviceAuth);
        SessionValidation.getPrefsHelper().delete(Constants.SharedKeyName.deviceToken);
        SessionValidation.getPrefsHelper().delete(Constants.SharedKeyName.forStripeCustomerId);
        SessionValidation.getPrefsHelper().delete(Constants.currentLat);
        SessionValidation.getPrefsHelper().delete(Constants.currentLng);

        if(SessionValidation.getPrefsHelper().getPref(Constants.distance) !=null)
        SessionValidation.getPrefsHelper().delete(Constants.distance);
        if(SessionValidation.getPrefsHelper().getPref(Constants.admission_cost) !=null)
        SessionValidation.getPrefsHelper().delete(Constants.admission_cost);
        if(SessionValidation.getPrefsHelper().getPref(Constants.event_date) !=null)
        SessionValidation.getPrefsHelper().delete(Constants.event_date);
//        try {
//            FirebaseInstanceId.getInstance().deleteInstanceId();profilePic
//        } catch (IOException e) {
//            e.printStackTrace();
//        }



    }

    private String getDeviceToken(){
        return SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.deviceToken);
    }

    public void validateUserIdFromErrorResponse(JSONObject errorRes){
        try {
             String id = errorRes.getJSONObject("data").getString("id");
            SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.userId,id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String getUserName(){
        return SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.userName);
    }

    public String getUserImg(){
        return SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.profilePic);
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
        LocationServices.getFusedLocationProviderClient(activity).requestLocationUpdates(mLocationRequest,mLocationCallback,null);
        LocationServices.getFusedLocationProviderClient(activity).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
               // Log.d("nflanfnaklfnlan", "onSuccess: "+location.getLatitude());
            }
        });

        fusedCurrentLocationListener.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    currentLocationListener.getFusedCurrentSuccess(location);
                    Log.d("nflankfnlanlfa", "onSuccess: "+location.getLatitude());
                }


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

    public void showSnackBar(Context context,View view, String msg){
        Snackbar snackbar = Snackbar.make(view,msg,Snackbar.LENGTH_SHORT);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.light_orange_color));
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)sbView.getLayoutParams();
        params.gravity = Gravity.TOP;
        sbView.setLayoutParams(params);
        snackbar.show();
    }

    public String getHostName(String hostName){
        StringBuffer stringBuffer = new StringBuffer();
        if(hostName.contains(" ")){
            String[] getHostName = hostName.split(" ");
            for(int i=0;i<getHostName.length;i++){
                stringBuffer.append(getHostName[i].charAt(0));
            }
        }else{
            stringBuffer.append(hostName.charAt(0));
        }

        return stringBuffer.toString().toUpperCase();
    }

    public static void shareEvent(Context context){
        final String appPackageName = context.getPackageName();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out the App at: https://google.com");
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    public String makeFirstLatterCapital(String name){
        StringBuffer stringBuffer = new StringBuffer();
        String getName = name;

        if(name.contains("  "))
             getName = name.replace("  "," ");

        if(name.contains(" ")){
            String[] arrayName = getName.split(" ");
            for(int i=0;i<arrayName.length;i++){
                stringBuffer.append(arrayName[i].substring(0,1).toUpperCase()+arrayName[i].substring(1)+" ") ;
            }
            return stringBuffer.toString();
        }

        return  stringBuffer.append(getName.substring(0,1)+getName.substring(1)).toString().toUpperCase();
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        return true;
                    }
                }
            } else {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }
        }
        return false;
    }


    public String getCurrentLocation(){
        String currentLocation = "";
        String lat = SessionValidation.getPrefsHelper().getPref(Constants.currentLat);
        String lng = SessionValidation.getPrefsHelper().getPref(Constants.currentLng);
        if(lat != null && lng != null)
            currentLocation = lat +" "+ lng;
        return currentLocation;
    }

    public void saveCurrentLocation(double lat, double lng){
        SessionValidation.getPrefsHelper().savePref(Constants.currentLat, String.valueOf(lat));
        SessionValidation.getPrefsHelper().savePref(Constants.currentLng, String.valueOf(lng));
    }

    public String getStripeCustomerId(){
        return SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.forStripeCustomerId);
    }
    public int getFilterDistance(){
        return SessionValidation.getPrefsHelper().getPref(Constants.distance) == null ? 500 : SessionValidation.getPrefsHelper().getPref(Constants.distance);
    }

    public void saveFilterDistance(int distance){
        SessionValidation.getPrefsHelper().savePref(Constants.distance,distance);
    }

    public void saveFilterAdmissionCost(int admission_cost){
        SessionValidation.getPrefsHelper().savePref(Constants.admission_cost,admission_cost);
    }

    public int getFilterAdmissionCost(){
        return SessionValidation.getPrefsHelper().getPref(Constants.admission_cost) == null ? 4000 : SessionValidation.getPrefsHelper().getPref(Constants.admission_cost);
    }

    public int getEventDate(){
        return SessionValidation.getPrefsHelper().getPref(Constants.event_date) == null ? 2 : SessionValidation.getPrefsHelper().getPref(Constants.event_date);
    }

    public void saveEventDate(int eventDateIndex){
        SessionValidation.getPrefsHelper().savePref(Constants.event_date,eventDateIndex);
    }


    public static String getTimeAgo(String stringDate, Context ctx, boolean isRawAdded) {

        if (stringDate == null) {
            return null;
        }
        int raw = 0;

        if (isRawAdded) {
            TimeZone tz = TimeZone.getDefault();
            raw = tz.getRawOffset();
        }

        Date date = getNotificationDate(getTimeFromDate(stringDate));
        long time = date.getTime() - raw;

        Date curDate = currentDate();
        long now = curDate.getTime();
        if (time > now || time <= 0) {
            return "Just Now";
        }

        int dim = getTimeDistanceInMinutes(time);
        int dimSec = getTimeDistanceInSeconds(time);

        String timeAgo = null;
        if (dimSec == 1) {
            return "1 " + ctx.getResources().getString(R.string.date_util_unit_second);
        } else if (dimSec > 1 && dim == 0) {
            timeAgo = dimSec + " " + ctx.getResources().getString(R.string.date_util_unit_seconds);
        } else if (dim == 1) {
            return "1 " + ctx.getResources().getString(R.string.date_util_unit_minute);
        } else if (dim >= 2 && dim <= 44) {
            timeAgo = dim + " " + ctx.getResources().getString(R.string.date_util_unit_minutes);
        } else if (dim >= 45 && dim <= 89) {
            timeAgo = "1 " + " " + ctx.getResources().getString(R.string.date_util_unit_hour);
        } else if (dim >= 90 && dim <= 1439) {
            timeAgo = (Math.round(dim / 60)) + " " + ctx.getResources().getString(R.string.date_util_unit_hours);
        } else if (dim >= 1440 && dim <= 2519) {
            timeAgo = "1 " + ctx.getResources().getString(R.string.date_util_unit_day);
        } else if (dim >= 2520 && dim <= 43199) {
            timeAgo = (Math.round(dim / 1440)) + " " + ctx.getResources().getString(R.string.date_util_unit_days);
        } else if (dim >= 43200 && dim <= 86399) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_month);
        } else if (dim >= 86400 && dim <= 525599) {
            timeAgo = (Math.round(dim / 43200)) + " " + ctx.getResources().getString(R.string.date_util_unit_months);
        } else if (dim >= 525600 && dim <= 655199) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_year);
        } else if (dim >= 655200 && dim <= 914399) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_over) + " " + ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_year);
        } else if (dim >= 914400 && dim <= 1051199) {
            timeAgo = " 2 " + ctx.getResources().getString(R.string.date_util_unit_years);
        } else {
            timeAgo = (Math.round(dim / 525600)) + " " + ctx.getResources().getString(R.string.date_util_unit_years);
        }
        if (timeAgo.equalsIgnoreCase("0 Years")) {
            return "just now";
        } else
            return timeAgo + " " + ctx.getResources().getString(R.string.date_util_suffix);
    }

    public static Date getNotificationDate(Date date) {
        TimeZone tz = TimeZone.getDefault();
        int raw = tz.getRawOffset();

        long millis = date.getTime();
        long newMillis = millis + raw;

        return new Date(newMillis);
    }

    public static Date getTimeFromDate(String createdAt) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
        Date date = null;
        try {
            date = sdf.parse(createdAt);
        } catch (ParseException e) {
            e.printStackTrace();

        }
        return date;
    }

    public static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }


    private static int getTimeDistanceInSeconds(long time) {
        long timeDistance = currentDate().getTime() - time;
        return Math.round((Math.abs(timeDistance) / 1000));
    }

    private static int getTimeDistanceInMinutes(long time) {
        long timeDistance = currentDate().getTime() - time;
        return Math.round((Math.abs(timeDistance) / 1000) / 60);
    }

    private static int getTimeDistanceInHours(long time) {
        long timeDistance = currentDate().getTime() - time;
        return Math.round((Math.abs(timeDistance) / 1000000) / (60 * 60));
    }

    public void appLozicRegister(final Activity activity, String userId, String userName,boolean isRemind, boolean isNotify,boolean isFromLogin, MyLoader myLoader, String customerId) {

        UserLoginTask.TaskListener listener = new UserLoginTask.TaskListener() {
            @Override
            public void onSuccess(RegistrationResponse registrationResponse, final Context context) {
                if (MobiComUserPreference.getInstance(context).isRegistered()) {
                    PushNotificationTask pushNotificationTask = null;
                    PushNotificationTask.TaskListener listener = new PushNotificationTask.TaskListener() {
                        @Override
                        public void onSuccess(RegistrationResponse registrationResponse) {
                            ApplozicClient.getInstance(context).hideChatListOnNotification();

                            Log.d("fnalkfnkla", "AppLoziccc onSuccess: " + registrationResponse.toString());

                            if (getUserName() != null && !TextUtils.isEmpty(getUserName())) {
                                validateUser(userId,userName,isRemind,isNotify,customerId);
                                if (isFromLogin)
                                    navigateToLanding(activity);
                                else
                                    navigateToHomePage(activity);
                            }

                            myLoader.dismiss();

                        }

                        @Override
                        public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                            myLoader.dismiss();
                            ShowToast.errorToast(activity, registrationResponse.getMessage());
                        }
                    };
                    pushNotificationTask = new PushNotificationTask(getDeviceToken() !=null ? getDeviceToken() : "", listener, context);
                    pushNotificationTask.execute((Void) null);

                }
            }

            @Override
            public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                ShowToast.errorToast(activity, registrationResponse.getMessage());
                Log.d("fnaklnfklanl", "onFailure: "+registrationResponse.getMessage());
                // If any failure in registration the callback  will come here
            }
        };


        User user = new User();
        user.setUserId(userId); //userId it can be any unique user identifier
        user.setDisplayName(userName); //displayName is the name of the user which will be shown in chat messages
        user.setAuthenticationTypeId(User.AuthenticationType.APPLOZIC.getValue());  //User.AuthenticationType.APPLOZIC.getValue() for password verification from Applozic server and User.AuthenticationType.CLIENT.getValue() for access Token verification from your server set access token as password
        user.setPassword(""); //optional, leave it blank for testing purpose, read this if you want to add additional security by verifying password from your server https://www.applozic.com/docs/configuration.html#access-token-url
        user.setImageLink(SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.profilePic) != null ? SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.profilePic) : "");//optional,pass your image link
        new UserLoginTask(user, listener, activity).execute((Void) null);
    }

    private void navigateToLanding(Activity activity){
        Intent intentHome = new Intent(activity, LandingActivity.class);
        intentHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intentHome);
        activity.finish();
    }

    private void navigateToHomePage(Activity activity){
        Intent homeIntent = new Intent(activity, HomeActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(homeIntent);
        activity.finish();
    }
    private void logoutAppLozic(Context context){
        Applozic.logoutUser(context, new AlLogoutHandler() {
            @Override
            public void onSuccess(Context context) {

            }

            @Override
            public void onFailure(Exception exception) {

            }
        });
    }



    public void googleLogout(Context context){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        GoogleSignInAccount account  = GoogleSignIn.getLastSignedInAccount(context);
        if (account != null) {
            mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
        }else if(LoginManager.getInstance() != null){
            LoginManager.getInstance().logOut();
        }
    }

    public boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    public String getStripeCustomer(){
        return SessionValidation.getPrefsHelper().getPref(Constants.customer) == null ? null : SessionValidation.getPrefsHelper().getPref(Constants.customer).toString();
    }

    public <T> void navigateTo(Context context, Class<T> className,boolean clearStack){
        Intent navigateIntent = new Intent(context,className);
        if(clearStack){
            navigateIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(navigateIntent);
        ((Activity)context).finish();

    }

    public String getCountOfDays(String expireDateString) {
        Calendar calendar = Calendar.getInstance();
        Date startFromToday = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());


        Date createdConvertedDate = null, expireCovertedDate = null, todayWithZeroTime = null;
        try {
            createdConvertedDate = dateFormat.parse(dateFormat.format(startFromToday));
            expireCovertedDate = dateFormat.parse(expireDateString);

            Date today = new Date();

            todayWithZeroTime = dateFormat.parse(dateFormat.format(today));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int cYear = 0, cMonth = 0, cDay = 0;

        if (createdConvertedDate.after(todayWithZeroTime)) {
            Calendar cCal = Calendar.getInstance();
            cCal.setTime(createdConvertedDate);
            cYear = cCal.get(Calendar.YEAR);
            cMonth = cCal.get(Calendar.MONTH);
            cDay = cCal.get(Calendar.DAY_OF_MONTH);

        } else {
            Calendar cCal = Calendar.getInstance();
            cCal.setTime(todayWithZeroTime);
            cYear = cCal.get(Calendar.YEAR);
            cMonth = cCal.get(Calendar.MONTH);
            cDay = cCal.get(Calendar.DAY_OF_MONTH);
        }


        Calendar eCal = Calendar.getInstance();
        eCal.setTime(expireCovertedDate);

        int eYear = eCal.get(Calendar.YEAR);
        int eMonth = eCal.get(Calendar.MONTH);
        int eDay = eCal.get(Calendar.DAY_OF_MONTH);

        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();

        date1.clear();
        date1.set(cYear, cMonth, cDay);
        date2.clear();
        date2.set(eYear, eMonth, eDay);

        long diff = date2.getTimeInMillis() - date1.getTimeInMillis();

        float dayCount = (float) diff / (24 * 60 * 60 * 1000);

        int leftDays = (int) dayCount;

        return (leftDays == 0 ? "today end" : leftDays + " days left");
    }

}
