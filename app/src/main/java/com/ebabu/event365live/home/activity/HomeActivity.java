package com.ebabu.event365live.home.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.auth.activity.LoginActivity;
import com.ebabu.event365live.databinding.ActivityHomeBinding;
import com.ebabu.event365live.databinding.DrawerLayoutBinding;
import com.ebabu.event365live.home.adapter.HomeViewAdapter;
import com.ebabu.event365live.home.modal.LoginViewModal;
import com.ebabu.event365live.home.modal.nearbymodal.EventList;
import com.ebabu.event365live.home.modal.nearbymodal.NearByEventModal;
import com.ebabu.event365live.homedrawer.activity.ChooseRecommendedCatActivity;
import com.ebabu.event365live.homedrawer.activity.ContactUsActivity;
import com.ebabu.event365live.homedrawer.activity.FavoritesActivity;
import com.ebabu.event365live.homedrawer.activity.NotificationActivity;
import com.ebabu.event365live.homedrawer.activity.RSVPTicketActivity;
import com.ebabu.event365live.homedrawer.activity.SearchHomeActivity;
import com.ebabu.event365live.homedrawer.activity.SettingsActivity;
import com.ebabu.event365live.homedrawer.activity.WishListActivity;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.userinfo.activity.ProfileActivity;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.SessionValidation;
import com.ebabu.event365live.utils.ShowToast;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.yarolegovich.slidingrootnav.SlidingRootNav;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;
import retrofit2.Call;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, GetResponseData {
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;
    private ActivityHomeBinding activityHomeBinding;
    private SlidingRootNav slidingRootNav;
    private DrawerLayoutBinding drawerLayoutBinding;
    private HomeViewAdapter homeViewAdapter;
    private ArrayList<EventList> nearByNoAuthModal;

    int widthOne;
    int totalWidth1;
    int width1;
    LoginViewModal mViewModel;
    private boolean isFirstClick = true,isSecondClick = true ,isThirdClick = true;
    private boolean isUserLogin,isSwipeListView;
    private boolean getIsUserLogin, getIsHomeSwipeView;
    private Geocoder geocoder;
    private List<Address> addresses;
    private LatLng getCurrentLatLng;
    private Bundle bundle;
    MyLoader myLoader;
    private boolean isEventFilter;
    private View drawerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomeBinding = DataBindingUtil.setContentView(this,R.layout.activity_home);
        activityHomeBinding.tabOne.setOnClickListener(this);
        activityHomeBinding.tabTwo.setOnClickListener(this);
        activityHomeBinding.tabThree.setOnClickListener(this);
        activityHomeBinding.tabLayout.setSaveEnabled(false);
        activityHomeBinding.tabLayout.setTabRippleColor(null);
        mViewModel = ViewModelProviders.of(this).get(LoginViewModal.class);
        myLoader = new MyLoader(this);
        setSupportActionBar(activityHomeBinding.homeToolbar);
        bundle = getIntent().getExtras();

        if(CommonUtils.getCommonUtilsInstance().isUserLogin())
            initView();

        setBundleData();
        isEventFilter = false;
        showGmailProfileDetails();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(HomeFilterActivity.currentLatLng != null &HomeFilterActivity.place != null){
            convertLocationToLatLng(new LatLng(HomeFilterActivity.currentLatLng.latitude,HomeFilterActivity.currentLatLng.longitude));
        }else {
            getCurrentLocation();
        }
    }

    private void getFbLoginDetails(String fbLoginDetails) {
        try {
            JSONObject fbObj = new JSONObject(fbLoginDetails);
            String userFirstName = fbObj.getString("first_name");
            String userLastName = fbObj.getString("last_name");
            String userEmail = fbObj.getString("email");
            String id = fbObj.getString("id");
            String userImg = "https://graph.facebook.com/" + id + "/picture?type=normal";
            // fromLoginFb = true;
            Log.d("nflanlfnlkanfla", userEmail+" getFbLoginDetails: "+userFirstName);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("nflanlfnlkanfla", " JSONException: "+e.getMessage());
        }
    }

    private void initView(){
        handleDrawer();
    }
    @Override
    protected void onResume() {
        super.onResume();
        CommonUtils.getCommonUtilsInstance().transparentStatusBar(this);
        if(CommonUtils.getCommonUtilsInstance().getUserImg() !=null && !TextUtils.isEmpty(CommonUtils.getCommonUtilsInstance().getUserImg())){
            Glide.with(HomeActivity.this).load(CommonUtils.getCommonUtilsInstance().getUserImg()).into((CircleImageView)drawerView.findViewById(R.id.ivShowUserImg));
            drawerView.findViewById(R.id.homeNameImgContainer).setVisibility(View.GONE);
            return;
        }
        if(CommonUtils.getCommonUtilsInstance().isUserLogin()){
            ((TextView)drawerView.findViewById(R.id.ivShowImgName)).setText(CommonUtils.getCommonUtilsInstance().getHostName(CommonUtils.getCommonUtilsInstance().getUserName()));
            drawerView.findViewById(R.id.homeNameImgContainer).setVisibility(View.VISIBLE);
            drawerView.findViewById(R.id.ivShowUserImg).setVisibility(View.GONE);
        }
    }

    public void loginOnClick(View view) {
        navigateToLoginScreen();
    }

    public void filterOnClick(View view) {
        Intent homeFilterIntent = new Intent(HomeActivity.this, HomeFilterActivity.class);
        homeFilterIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeFilterIntent);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.viewProfileContainer:
                activityHomeBinding.drawer.closeDrawer();
                startIntent(ProfileActivity.class);
                break;

            case R.id.homeContainer:
                activityHomeBinding.drawer.closeDrawer();
                startIntent(HomeFilterActivity.class);
                break;
            case R.id.searchEventContainer:
                activityHomeBinding.drawer.closeDrawer();
                startIntent(SearchHomeActivity.class);
                break;

            case R.id.notificationContainer:
                activityHomeBinding.drawer.closeDrawer();
                startIntent(NotificationActivity.class);
                break;

            case R.id.rsvpTicketContainer:
                activityHomeBinding.drawer.closeDrawer();
                startIntent(RSVPTicketActivity.class);
                break;

            case R.id.favoritesContainer:
                activityHomeBinding.drawer.closeDrawer();
                startIntent(FavoritesActivity.class);
                break;

            case R.id.wishListContainer:
                //slidingRootNav.closeMenu(true);
                activityHomeBinding.drawer.closeDrawer();
                startIntent(WishListActivity.class);
                break;

            case R.id.preferenceContainer:
                activityHomeBinding.drawer.closeDrawer();
                startIntent(ChooseRecommendedCatActivity.class);
                break;

            case R.id.contactUsContainer:
                activityHomeBinding.drawer.closeDrawer();
                startIntent(ContactUsActivity.class);
                break;

            case R.id.settingsContainer:
                activityHomeBinding.drawer.closeDrawer();
                startIntent(SettingsActivity.class);
                break;

            case R.id.tabOne:
                activityHomeBinding.tabLayout.getTabAt(0).select();
               // activityHomeBinding.tabTwo.setTypeface(activityHomeBinding.tabTwo.getTypeface(), Typeface.NORMAL);
                //activityHomeBinding.tabOne.setTypeface(activityHomeBinding.tabTwo.getTypeface(), Typeface.BOLD);

                activityHomeBinding.tabThree.clearAnimation();
                activityHomeBinding.tabTwo.clearAnimation();

                RunAnimation(activityHomeBinding.tabOne);
                break;

            case R.id.tabTwo:
                activityHomeBinding.tabLayout.getTabAt(1).select();
               // activityHomeBinding.tabTwo.setTypeface(activityHomeBinding.tabTwo.getTypeface(), Typeface.BOLD);
//                tabOne.setTypeface(tabTwo.getTypeface(), Typeface.DEFAULT);
//                tabThree.setTypeface(tabTwo.getTypeface(), Typeface.NORMAL);
                activityHomeBinding.tabOne.clearAnimation();
                activityHomeBinding.tabThree.clearAnimation();
                RunAnimation(activityHomeBinding.tabTwo);
                break;

            case R.id.tabThree:

                activityHomeBinding.tabLayout.getTabAt(2).select();
                //tabTwo.setTypeface(tabTwo.getTypeface(), Typeface.NORMAL);
                // tabOne.setTypeface(tabTwo.getTypeface(), Typeface.NORMAL);
               // activityHomeBinding.tabThree.setTypeface(activityHomeBinding.tabTwo.getTypeface(), Typeface.BOLD);
                RunAnimation(activityHomeBinding.tabThree);
                activityHomeBinding.tabTwo.clearAnimation();
                activityHomeBinding.tabOne.clearAnimation();
                break;
        }
    }
    private void RunAnimation(TextView textView){
        Animation a = AnimationUtils.loadAnimation(this, R.anim.zoom);
        a.reset();
        textView.clearAnimation();
        textView.startAnimation(a);
    }
    public void searchEventOnClick(View view) {
        Intent searchIntent = new Intent(HomeActivity.this,SearchHomeActivity.class);
        searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, activityHomeBinding.ivSearchEvent, getString(R.string.search_event_transition));
        startActivity(searchIntent,options.toBundle());
    }

    public void showLocationOnClick(View view) {
        CommonUtils.getCommonUtilsInstance().launchSelectAddressFrag(HomeActivity.this,null,false);
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();

        Log.d("fbnalfnlansfklsa", " onSuccess==: "+responseObj.toString());
        NearByEventModal eventModal = new Gson().fromJson(responseObj.toString(), NearByEventModal.class);

        nearByNoAuthModal = eventModal.getData().getEventList();
        setupViewPager();
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        if(errorCode == 406){
            /* at 406, show event not found at this location*/
            if(nearByNoAuthModal == null) {
                nearByNoAuthModal = new ArrayList<>();
            }
           if(nearByNoAuthModal.size()>0)
               nearByNoAuthModal.clear();
            setupViewPager();
        }
    }


    private void navigateToLoginScreen(){
        Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginIntent);
        finish();
    }
    private void setupViewPager(){
        activityHomeBinding.tabContainer.setVisibility(View.VISIBLE);
        activityHomeBinding.homeViewPager.setVisibility(View.VISIBLE);
        activityHomeBinding.tabLayout.setVisibility(View.VISIBLE);

        homeViewAdapter = new HomeViewAdapter(getSupportFragmentManager(),nearByNoAuthModal);
        activityHomeBinding.homeViewPager.setAdapter(homeViewAdapter);
        homeViewAdapter.notifyDataSetChanged();
        activityHomeBinding.tabLayout.setupWithViewPager(activityHomeBinding.homeViewPager);
        activityHomeBinding.tabLayout.getTabAt(0).select();
        activityHomeBinding.tabThree.clearAnimation();
        activityHomeBinding.tabTwo.clearAnimation();
        RunAnimation(activityHomeBinding.tabOne);
         activityHomeBinding.homeViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
             @Override
             public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

             }

             @Override
             public void onPageSelected(int position) {
                 if(position == 0){
                     activityHomeBinding.tabLayout.getTabAt(0).select();
                     activityHomeBinding.tabThree.clearAnimation();
                     activityHomeBinding.tabTwo.clearAnimation();
                     RunAnimation(activityHomeBinding.tabOne);

                 }else if(position == 1){

                     activityHomeBinding.tabLayout.getTabAt(1).select();
                     activityHomeBinding.tabOne.clearAnimation();
                     activityHomeBinding.tabThree.clearAnimation();
                     RunAnimation(activityHomeBinding.tabTwo);

                 }else if(position == 2){

                     activityHomeBinding.tabLayout.getTabAt(2).select();
                     RunAnimation(activityHomeBinding.tabThree);
                     activityHomeBinding.tabTwo.clearAnimation();
                     activityHomeBinding.tabOne.clearAnimation();
                 }
             }

             @Override
             public void onPageScrollStateChanged(int state) {

             }
         });
    }

    public void isUserLoginListener()
    {
        if(getIsUserLogin && getIsHomeSwipeView)
        {
            initView();
            activityHomeBinding.tvLoginBtn.setVisibility(View.GONE);
            Log.d("bnfabsfkbjkasf", "isUserLoginListener:  if");
        }
        else if(getIsHomeSwipeView && !getIsUserLogin)
        {
            activityHomeBinding.tvLoginBtn.setVisibility(View.VISIBLE);
            Log.d("bnfabsfkbjkasf", "isUserLoginListener: else if");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1001)
        {
            if(grantResults.length >0 && permissions[0].equals(Manifest.permission.ACCESS_COARSE_LOCATION) &&
                    permissions[1].equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                getCurrentLocation();
            }
            else {
                ShowToast.errorToast(HomeActivity.this,getString(R.string.permission_denied));
                }
        }
    }

    private void getCurrentLocation() {
        final FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1001);
            return;
        }
            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                        convertLocationToLatLng(new LatLng(location.getLatitude(),location.getLongitude()));
                        validateUIBehalfOfUserLogin();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                getCurrentLatLng = place.getLatLng();
                Geocoder geocoder = new Geocoder(HomeActivity.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(getCurrentLatLng.latitude, getCurrentLatLng.longitude, 1);
                    String stateName = addresses.get(0).getAdminArea();
                    String cityName = addresses.get(0).getLocality();
                    setAddressToTv(cityName,stateName);
                    validateUIBehalfOfUserLogin();
                    Log.d("flaksnfslan", getCurrentLatLng.latitude+" onActivityResult: "+getCurrentLatLng.longitude+" == "+place.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void checkSessionOfGoogleFb(){
        account = GoogleSignIn.getLastSignedInAccount(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        if(account != null){
            Log.d("fnlanflknalf", "login with google: ");

            mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                }
            });
        }else {
            if(LoginManager.getInstance() != null){
                Log.d("fnlanflknalf", "login with fb: ");
                LoginManager.getInstance().logOut();
            }
        }
    }

    private void showGmailProfileDetails() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            String userFullName = account.getDisplayName();
            String userEmail = account.getEmail();
            String userImg = account.getPhotoUrl().toString();
            Log.d("nflanlfnklanfklas", userFullName+" showGmailProfileDetails: "+userEmail);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        checkSessionOfGoogleFb();
    }
    private <T> void startIntent (final Class<T> className){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CommonUtils.getCommonUtilsInstance().launchActivity(HomeActivity.this,className,true);
            }
        },300);
    }
    private void validateUIBehalfOfUserLogin(){
        nearByEventAuthRequest();
    }
    private void nearByEventAuthRequest(){
        myLoader.show("");
        JsonObject filterObj = new JsonObject();
        Log.d("flaksnfslan", getCurrentLatLng.latitude+" nearByEventAuthRequest: "+getCurrentLatLng.longitude);
        filterObj.addProperty(Constants.latitude, getCurrentLatLng.latitude);
        filterObj.addProperty(Constants.longitude, getCurrentLatLng.longitude);
        filterObj.addProperty(Constants.miles,"10000");
        filterObj.addProperty(Constants.cost,"4000");
        if(CommonUtils.getCommonUtilsInstance().isUserLogin()){
            Log.d("flanflka", "login: "+CommonUtils.getCommonUtilsInstance().isUserLogin());
            setMarginToShowLocation();
            activityHomeBinding.tvLoginBtn.setVisibility(View.GONE);
            Call<JsonElement> landingCall = APICall.getApiInterface().nearByWithAuthEvent(CommonUtils.getCommonUtilsInstance().getDeviceAuth(),filterObj);
            new APICall(HomeActivity.this).apiCalling(landingCall,this, APIs.NEAR_BY_AUTH_EVENT);
            return;
        }
        Call<JsonElement> landingCall = APICall.getApiInterface().noAuthNearByEvent(filterObj);
        new APICall(HomeActivity.this).apiCalling(landingCall,this, APIs.NO_AUTH_NEAR_BY_EVENT);
        Log.d("flanflka", "no login: "+CommonUtils.getCommonUtilsInstance().isUserLogin());
        activityHomeBinding.tvLoginBtn.setVisibility(View.VISIBLE);
    }

    private void setAddressToTv(String cityName, String stateName){

        activityHomeBinding.tvShowCurrentLocation.setText(cityName + ", " + stateName);
        activityHomeBinding.tvShowCurrentLocation.setSelected(true);
    }

    private void convertLocationToLatLng(LatLng currentLatLng){
        getCurrentLatLng = new LatLng(currentLatLng.latitude,currentLatLng.longitude);
        try {
            Geocoder geocoder = new Geocoder(HomeActivity.this, Locale.getDefault());
            addresses = geocoder.getFromLocation(currentLatLng.latitude,currentLatLng.longitude, 1);
            String city = addresses.get(0).getLocality();
            String country = addresses.get(0).getCountryName();
            String state = addresses.get(0).getAdminArea();
            if(city != null && country != null)
                if(HomeFilterActivity.currentLatLng != null && HomeFilterActivity.place != null){
                    setAddressToTv(city,state);
                    return;
                }
                setAddressToTv(city,state);
            //activityHomeBinding.tvShowCurrentLocation.setText(country+" "+city);
        } catch (IOException e) {
            e.printStackTrace();
        }


        Log.d("flaksnfslan", getCurrentLatLng.latitude+" convertLocationToLatLng: "+getCurrentLatLng.longitude);
    }

    private void handleDrawer() {
        DuoDrawerToggle duoDrawerToggle = new DuoDrawerToggle(this,
                activityHomeBinding.drawer,
                activityHomeBinding.homeToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        activityHomeBinding.drawer.setDrawerListener(duoDrawerToggle);
        duoDrawerToggle.syncState();

        drawerView = activityHomeBinding.drawerMenu.getHeaderView();
        drawerView.findViewById(R.id.searchEventContainer).setOnClickListener(this);;
        drawerView.findViewById(R.id.notificationContainer).setOnClickListener(this);
        drawerView.findViewById(R.id.rsvpTicketContainer).setOnClickListener(this);
        drawerView.findViewById(R.id.wishListContainer).setOnClickListener(this);
        drawerView.findViewById(R.id.settingsContainer).setOnClickListener(this);
        drawerView.findViewById(R.id.favoritesContainer).setOnClickListener(this);
        drawerView.findViewById(R.id.viewProfileContainer).setOnClickListener(this);
        drawerView.findViewById(R.id.preferenceContainer).setOnClickListener(this);
        drawerView.findViewById(R.id.homeContainer).setOnClickListener(this);
        drawerView.findViewById(R.id.contactUsContainer).setOnClickListener(this);

        ((TextView)drawerView.findViewById(R.id.tvShowUserName)).setText(CommonUtils.getCommonUtilsInstance().getUserName());


    }

    private void setMarginToShowLocation(){
        /* whenever app in login mode, i do not know why show location container more slide to right side, on wihtout login it shows perfect on
        * centre of the screen, but problem only occur when user in login stage, to that's why is used below to to change left side margin, its work fine*/
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) activityHomeBinding.locationContainer.getLayoutParams();
        params.leftMargin = 0;
    }

    private void setBundleData(){
        if(bundle != null){
            String activityName = bundle.getString(Constants.activityName);
            if(activityName != null && activityName.equalsIgnoreCase(getString(R.string.home_filter_activity))){
                nearByNoAuthModal =  bundle.getParcelableArrayList(Constants.nearByData);
                setupViewPager();
                Log.d("fnalksnfskla", "onCreate: "+nearByNoAuthModal.size());
                isEventFilter = true;

            }
            String fbLoginDetails = bundle.getString(getString(R.string.fb_login_details));
            if(fbLoginDetails != null){

                getFbLoginDetails(fbLoginDetails);
            }
        }

    }


}
