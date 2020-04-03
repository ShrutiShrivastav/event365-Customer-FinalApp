package com.ebabu.event365live.home.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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
import androidx.core.app.ActivityOptionsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.bumptech.glide.Glide;
import com.ebabu.event365live.MainActivity;
import com.ebabu.event365live.R;
import com.ebabu.event365live.auth.activity.LoginActivity;
import com.ebabu.event365live.databinding.ActivityHomeBinding;
import com.ebabu.event365live.home.adapter.HomeViewAdapter;
import com.ebabu.event365live.home.fragment.NearYouFragment;
import com.ebabu.event365live.home.fragment.RSVPFragment;
import com.ebabu.event365live.home.fragment.RecommendedFragment;
import com.ebabu.event365live.home.modal.LoginViewModal;
import com.ebabu.event365live.home.modal.nearbymodal.EventList;
import com.ebabu.event365live.home.modal.nearbymodal.NearByEventModal;
import com.ebabu.event365live.homedrawer.activity.BookedEventsActivity;
import com.ebabu.event365live.homedrawer.activity.ChooseRecommendedCatActivity;
import com.ebabu.event365live.homedrawer.activity.ContactUsActivity;
import com.ebabu.event365live.homedrawer.activity.FavoritesActivity;
import com.ebabu.event365live.homedrawer.activity.NotificationActivity;
import com.ebabu.event365live.homedrawer.activity.RSVPTicketActivity;
import com.ebabu.event365live.homedrawer.activity.SearchHomeActivity;
import com.ebabu.event365live.homedrawer.activity.SettingsActivity;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.userinfo.activity.ProfileActivity;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.SessionValidation;
import com.ebabu.event365live.utils.Utility;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;
import retrofit2.Call;

public class HomeActivity extends MainActivity implements View.OnClickListener, GetResponseData {
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;
    private ActivityHomeBinding activityHomeBinding;
    private HomeViewAdapter homeViewAdapter;
    private ArrayList<EventList> nearByNoAuthModal;
    LoginViewModal mViewModel;
    private static LatLng getCurrentLatLng;
    private Bundle bundle;
    MyLoader myLoader;
    private boolean isEventFilter;
    private View drawerView;
    public static boolean isComeFromPreferencesScreen;
    private NearYouFragment nearYouFragment;
    private RecommendedFragment recommendedFragment;
    private RSVPFragment rsvpFragment;
    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    private boolean enableHumberIcon;
    private DuoDrawerToggle duoDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        activityHomeBinding.tabOne.setOnClickListener(this);
        activityHomeBinding.tabTwo.setOnClickListener(this);
        activityHomeBinding.tabThree.setOnClickListener(this);
        activityHomeBinding.tabLayout.setSaveEnabled(false);
        activityHomeBinding.tabLayout.setTabRippleColor(null);
        mViewModel = ViewModelProviders.of(this).get(LoginViewModal.class);
        myLoader = new MyLoader(this);
        myLoader.show("");
        setSupportActionBar(activityHomeBinding.homeToolbar);
        bundle = getIntent().getExtras();
        RunAnimation(activityHomeBinding.tabOne);
        getCurrentLocationInstance(currentLatLng -> {
            activityHomeBinding.ivFilterBtn.setVisibility(View.VISIBLE);
            if (currentLatLng != null) {
                myLoader.dismiss();
                if (bundle != null) {
                    String activityName = bundle.getString(Constants.activityName);
                    if (activityName != null && activityName.equalsIgnoreCase(getString(R.string.home_filter_activity))) {
                        nearByNoAuthModal = bundle.getParcelableArrayList(Constants.nearByData);
                        String[] currentLocation = CommonUtils.getCommonUtilsInstance().getCurrentLocation().split(" ");
                        getCurrentLatLng = new LatLng(Double.parseDouble(currentLocation[0]), Double.parseDouble(currentLocation[1]));
                        setLocation(getCurrentLatLng.latitude, getCurrentLatLng.longitude, true);
                        setupViewPager();
                    }
                    return;
                }
                getCurrentLatLng = currentLatLng;
                setLocation(getCurrentLatLng.latitude, getCurrentLatLng.longitude, false);
            }
        });

        if (isComeFromPreferencesScreen) {
            activityHomeBinding.tabLayout.getTabAt(1).select();
            activityHomeBinding.ivFilterBtn.setVisibility(View.INVISIBLE);
            activityHomeBinding.tabOne.clearAnimation();
            activityHomeBinding.tabThree.clearAnimation();
            RunAnimation(activityHomeBinding.tabTwo);
            isComeFromPreferencesScreen = false;
            return;
        }
        isEventFilter = false;
        showGmailProfileDetails();
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
            Log.d("nflanlfnlkanfla", userEmail + " getFbLoginDetails: " + userFirstName);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("nflanlfnlkanfla", " JSONException: " + e.getMessage());
        }
    }

    private void initView() {
        if(NotificationActivity.isNotificationActivityLaunched){
            getNotificationCountRequest();
            NotificationActivity.isNotificationActivityLaunched = false;
        }
        handleDrawer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CommonUtils.getCommonUtilsInstance().transparentStatusBar(this);
        if (CommonUtils.getCommonUtilsInstance().isUserLogin()){
            setMarginToShowLocation();
            activityHomeBinding.tvLoginBtn.setVisibility(View.INVISIBLE);
            initView();
            if (CommonUtils.getCommonUtilsInstance().getUserImg() != null && !TextUtils.isEmpty(CommonUtils.getCommonUtilsInstance().getUserImg())) {
                Glide.with(HomeActivity.this).load(CommonUtils.getCommonUtilsInstance().getUserImg()).placeholder(R.drawable.wide_loading_img).into((CircleImageView) drawerView.findViewById(R.id.ivShowUserImg));
                drawerView.findViewById(R.id.ivShowUserImg).setVisibility(View.VISIBLE);
                drawerView.findViewById(R.id.homeNameImgContainer).setVisibility(View.GONE);
            } else {
                ((TextView) drawerView.findViewById(R.id.ivShowImgName)).setText(CommonUtils.getCommonUtilsInstance().getHostName(CommonUtils.getCommonUtilsInstance().getUserName()));
                drawerView.findViewById(R.id.homeNameImgContainer).setVisibility(View.VISIBLE);
                drawerView.findViewById(R.id.ivShowUserImg).setVisibility(View.GONE);
            }
        }else if(!CommonUtils.getCommonUtilsInstance().isUserLogin()){
            activityHomeBinding.tvLoginBtn.setVisibility(View.VISIBLE);
            handleDrawer();
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
        switch (view.getId()) {
            case R.id.viewProfileContainer:
                activityHomeBinding.drawer.closeDrawer();
                startIntent(ProfileActivity.class,false);
                break;

            case R.id.homeContainer:

                break;
            case R.id.searchEventContainer:
                activityHomeBinding.drawer.closeDrawer();
                startIntent(SearchHomeActivity.class,false);
                break;

            case R.id.notificationContainer:
                activityHomeBinding.drawer.closeDrawer();
                startIntent(NotificationActivity.class,false);
                break;

            case R.id.rsvpTicketContainer:
                activityHomeBinding.drawer.closeDrawer();
                startIntent(RSVPTicketActivity.class,false);
                break;

            case R.id.favoritesContainer:
                activityHomeBinding.drawer.closeDrawer();
                startIntent(FavoritesActivity.class,false);
                break;

            case R.id.bookedEventsContainer:
                //slidingRootNav.closeMenu(true);
                activityHomeBinding.drawer.closeDrawer();
                startIntent(BookedEventsActivity.class,false);
                break;

            case R.id.preferenceContainer:
                activityHomeBinding.drawer.closeDrawer();
                new Handler().postDelayed(() -> {
                    Intent openBubbleScreenIntent = new Intent(HomeActivity.this, ChooseRecommendedCatActivity.class);
                    startActivityForResult(openBubbleScreenIntent, 1005);
                }, 300);

                break;

            case R.id.contactUsContainer:
                activityHomeBinding.drawer.closeDrawer();
                startIntent(ContactUsActivity.class,false);
                break;

            case R.id.settingsContainer:
                activityHomeBinding.drawer.closeDrawer();
                startIntent(SettingsActivity.class,false);
                break;

            case R.id.tabOne:
                activityHomeBinding.tabLayout.getTabAt(0).select();
                activityHomeBinding.ivFilterBtn.setVisibility(View.VISIBLE);
                // activityHomeBinding.tabTwo.setTypeface(activityHomeBinding.tabTwo.getTypeface(), Typeface.NORMAL);
                //activityHomeBinding.tabOne.setTypeface(activityHomeBinding.tabTwo.getTypeface(), Typeface.BOLD);

                activityHomeBinding.tabThree.clearAnimation();
                activityHomeBinding.tabTwo.clearAnimation();

                RunAnimation(activityHomeBinding.tabOne);
                break;

            case R.id.tabTwo:
                activityHomeBinding.tabLayout.getTabAt(1).select();
                activityHomeBinding.ivFilterBtn.setVisibility(View.INVISIBLE);
                // activityHomeBinding.tabTwo.setTypeface(activityHomeBinding.tabTwo.getTypeface(), Typeface.BOLD);
//                tabOne.setTypeface(tabTwo.getTypeface(), Typeface.DEFAULT);
//                tabThree.setTypeface(tabTwo.getTypeface(), Typeface.NORMAL);
                activityHomeBinding.tabOne.clearAnimation();
                activityHomeBinding.tabThree.clearAnimation();
                RunAnimation(activityHomeBinding.tabTwo);
                break;

            case R.id.tabThree:

                activityHomeBinding.tabLayout.getTabAt(2).select();
                activityHomeBinding.ivFilterBtn.setVisibility(View.INVISIBLE);
                //tabTwo.setTypeface(tabTwo.getTypeface(), Typeface.NORMAL);
                // tabOne.setTypeface(tabTwo.getTypeface(), Typeface.NORMAL);
                // activityHomeBinding.tabThree.setTypeface(activityHomeBinding.tabTwo.getTypeface(), Typeface.BOLD);
                RunAnimation(activityHomeBinding.tabThree);
                activityHomeBinding.tabTwo.clearAnimation();
                activityHomeBinding.tabOne.clearAnimation();
                break;

            case R.id.msgContainer:
                activityHomeBinding.drawer.closeDrawer();

                new Handler().postDelayed(() -> {

                    Intent intent = new Intent(HomeActivity.this, ConversationActivity.class);
                    startActivity(intent);

                }, 400);

                //startIntent(MsgActivity.class);
                break;
        }
    }

    private void RunAnimation(TextView textView) {
        Animation a = AnimationUtils.loadAnimation(this, R.anim.zoom);
        a.reset();
        textView.clearAnimation();
        textView.startAnimation(a);
    }

    public void searchEventOnClick(View view) {
        Intent searchIntent = new Intent(HomeActivity.this, SearchHomeActivity.class);
        searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, activityHomeBinding.ivSearchEvent, getString(R.string.search_event_transition));
        startActivity(searchIntent, options.toBundle());
    }

    public void showLocationOnClick(View view) {
        CommonUtils.getCommonUtilsInstance().launchSelectAddressFrag(HomeActivity.this, null, false);
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        if (typeAPI.equalsIgnoreCase(APIs.NOTIFICATION_COUNT)) {
            try {
                int count = responseObj.getJSONObject("data").getInt("count");
                if (count > 0) {
                    drawerView.findViewById(R.id.notificationCountContainer).setVisibility(View.VISIBLE);
                    ((TextView) drawerView.findViewById(R.id.ivNotificationCount)).setText(String.valueOf(count));
                }else if(count == 0){
                    drawerView.findViewById(R.id.notificationCountContainer).setVisibility(View.INVISIBLE);
                    ((TextView) drawerView.findViewById(R.id.ivNotificationCount)).setText(String.valueOf(count));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }
        NearByEventModal eventModal = new Gson().fromJson(responseObj.toString(), NearByEventModal.class);
        nearByNoAuthModal = eventModal.getData().getEventList();
        setupViewPager();
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        if (errorCode == 406) {
            /* at 406, show event not found at this location*/
            if (nearByNoAuthModal == null) {
                nearByNoAuthModal = new ArrayList<>();
            }
            if (nearByNoAuthModal.size() > 0)
                nearByNoAuthModal.clear();
            setupViewPager();
        }
    }

    private void navigateToLoginScreen() {
        Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(loginIntent, 1005);
    }

    public Bundle getBundle(){
        if(bundle == null){
            bundle = new Bundle();
        }
        bundle.putParcelableArrayList(Constants.nearByData,nearByNoAuthModal);
        return bundle;
    }
    private void setupViewPager() {
        activityHomeBinding.homeViewPager.setVisibility(View.VISIBLE);
        activityHomeBinding.tabLayout.setVisibility(View.VISIBLE);
        activityHomeBinding.tabContainer.setVisibility(View.VISIBLE);
        homeViewAdapter = new HomeViewAdapter(getSupportFragmentManager(), nearByNoAuthModal);
        activityHomeBinding.homeViewPager.setAdapter(homeViewAdapter);
        homeViewAdapter.notifyDataSetChanged();
        activityHomeBinding.tabLayout.setupWithViewPager(activityHomeBinding.homeViewPager);
        activityHomeBinding.tabLayout.getTabAt(0).select();
        activityHomeBinding.tabThree.clearAnimation();
        activityHomeBinding.tabTwo.clearAnimation();
        if(nearYouFragment == null)
            nearYouFragment = new NearYouFragment();
        nearYouFragment.setArguments(getBundle());
        launchFrag(nearYouFragment);


//        activityHomeBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//
//                switch (tab.getPosition())
//                {
//                    case 0:
//                        if(nearYouFragment == null)
//                            nearYouFragment = new NearYouFragment();
//                        nearYouFragment.setArguments(getBundle());
//                        launchFrag(nearYouFragment);
//                        break;
//
//                    case 1:
////                if(recommendedFragment == null)
//                        recommendedFragment = new RecommendedFragment();
//                        launchFrag(recommendedFragment);
//                        break;
//
//                    case 2:
////                if(rsvpFragment == null)
//                        rsvpFragment = new RSVPFragment();
//                        launchFrag(rsvpFragment);
//
//                        break;
//
////                default:
////                    fragment = new NearYouFragment();
//                }
//
//
//
//
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
//
//


        activityHomeBinding.homeViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    activityHomeBinding.ivFilterBtn.setVisibility(View.VISIBLE);
                    activityHomeBinding.tabLayout.getTabAt(0).select();
                    activityHomeBinding.tabThree.clearAnimation();
                    activityHomeBinding.tabTwo.clearAnimation();
                    RunAnimation(activityHomeBinding.tabOne);

                } else if (position == 1) {
                    activityHomeBinding.ivFilterBtn.setVisibility(View.INVISIBLE);
                    activityHomeBinding.tabLayout.getTabAt(1).select();
                    activityHomeBinding.tabOne.clearAnimation();
                    activityHomeBinding.tabThree.clearAnimation();
                    RunAnimation(activityHomeBinding.tabTwo);

                } else if (position == 2) {
                    activityHomeBinding.ivFilterBtn.setVisibility(View.INVISIBLE);
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

//    public void isUserLoginListener()
//    {
//        if(getIsUserLogin && getIsHomeSwipeView)
//        {
//            initView();
//            activityHomeBinding.tvLoginBtn.setVisibility(View.GONE);
//            Log.d("bnfabsfkbjkasf", "isUserLoginListener:  if");
//        }
//        else if(getIsHomeSwipeView && !getIsUserLogin)
//        {
//            activityHomeBinding.tvLoginBtn.setVisibility(View.VISIBLE);
//            Log.d("bnfabsfkbjkasf", "isUserLoginListener: else if");
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("fnaslkfnasl", "onActivityResult: ");
        if (requestCode == Constants.AUTOCOMPLETE_REQUEST_CODE) {
            //enableHumberIcon = !CommonUtils.getCommonUtilsInstance().isUserLogin();
            if (resultCode == RESULT_OK && data != null) {
                Log.d("fnaslkfnasl", "RESULT_OK: ");
                Place place = Autocomplete.getPlaceFromIntent(data);
                getCurrentLatLng = place.getLatLng();
                if (getCurrentLatLng != null) {
                    CommonUtils.getCommonUtilsInstance().saveCurrentLocation(getCurrentLatLng.latitude, getCurrentLatLng.longitude);
                    setLocation(getCurrentLatLng.latitude, getCurrentLatLng.longitude, false);
                }

            }
        } else if (requestCode == 1005) {
            Log.d("fnaslkfnasl", "1005: ");
            //if(CommonUtils.getCommonUtilsInstance().isUserLogin()) enableHumberIcon = false;
            if (resultCode == Activity.RESULT_OK) {
                activityHomeBinding.tabLayout.getTabAt(1).select();
                activityHomeBinding.ivFilterBtn.setVisibility(View.INVISIBLE);
                activityHomeBinding.tabOne.clearAnimation();
                activityHomeBinding.tabThree.clearAnimation();
                RunAnimation(activityHomeBinding.tabTwo);
                return;
            }
            String lat = SessionValidation.getPrefsHelper().getPref(Constants.currentLat);
            String lng = SessionValidation.getPrefsHelper().getPref(Constants.currentLng);
            nearByEventAuthRequest(Double.parseDouble(lat), Double.parseDouble(lng));
        }else if(Activity.RESULT_OK == resultCode && Constants.LOGOUT_SUCCESS_REQUEST_CODE == requestCode){

                setMarginToShowLocation();
                activityHomeBinding.drawer.setDrawerLockMode(DuoDrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                nearByEventAuthRequest(getCurrentLatLng.latitude, getCurrentLatLng.longitude);
        }
    }

    private void checkSessionOfGoogleFb() {
        account = GoogleSignIn.getLastSignedInAccount(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        if (account != null) {
            Log.d("fnlanflknalf", "login with google: ");

            mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                }
            });
        } else {
            if (LoginManager.getInstance() != null) {
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
            Log.d("nflanlfnklanfklas", userFullName + " showGmailProfileDetails: " + userEmail);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myLoader.dismiss();
        checkSessionOfGoogleFb();
    }

    private <T> void startIntent(final Class<T> className, boolean isRequireStartForActivity) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CommonUtils.getCommonUtilsInstance().launchActivity(HomeActivity.this, className, true,isRequireStartForActivity);
            }
        }, 300);
    }

    private void validateUIBehalfOfUserLogin() {
    }

    private void nearByEventAuthRequest(double lat, double lng) {
        myLoader.show("");
        JsonObject filterObj = new JsonObject();
        Log.d("flaksnfslan", lat + " nearByEventAuthRequest: " + lng);

        filterObj.addProperty(Constants.latitude, lat);
        filterObj.addProperty(Constants.longitude, lng);
        filterObj.addProperty(Constants.miles, String.valueOf(CommonUtils.getCommonUtilsInstance().getFilterDistance()));
        filterObj.addProperty(Constants.cost, String.valueOf(CommonUtils.getCommonUtilsInstance().getFilterAdmissionCost()));

        filterObj.addProperty(Constants.startDate,Utility.startDate);
        filterObj.addProperty(Constants.endDate,Utility.endDate);
//        filterObj.addProperty(Constants.startDate, "");
//        filterObj.addProperty(Constants.endDate, "");


        if (CommonUtils.getCommonUtilsInstance().isUserLogin()) {
            Log.d("flanflka", "login: " + CommonUtils.getCommonUtilsInstance().isUserLogin());
            Call<JsonElement> landingCall = APICall.getApiInterface().nearByWithAuthEvent(CommonUtils.getCommonUtilsInstance().getDeviceAuth(), filterObj);
            new APICall(HomeActivity.this).apiCalling(landingCall, this, APIs.NEAR_BY_AUTH_EVENT);

        } else {
            Call<JsonElement> landingCall = APICall.getApiInterface().noAuthNearByEvent(filterObj);
            new APICall(HomeActivity.this).apiCalling(landingCall, this, APIs.NO_AUTH_NEAR_BY_EVENT);
            Log.d("flanflka", "no login: " + CommonUtils.getCommonUtilsInstance().isUserLogin());
        }
    }

    private void handleDrawer() {
        Log.d("fnaslkfnasl", "handleDrawer: "+CommonUtils.getCommonUtilsInstance().isUserLogin());
        if(CommonUtils.getCommonUtilsInstance().isUserLogin()){
           duoDrawerToggle = new DuoDrawerToggle(this,
                    activityHomeBinding.drawer,
                    activityHomeBinding.homeToolbar,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close);
            activityHomeBinding.drawer.setDrawerListener(duoDrawerToggle);
            duoDrawerToggle.syncState();
            activityHomeBinding.drawer.setDrawerLockMode(DuoDrawerLayout.LOCK_MODE_UNLOCKED);
        }else {
            duoDrawerToggle = new DuoDrawerToggle(this,
                    activityHomeBinding.drawer,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close);
            activityHomeBinding.drawer.setDrawerListener(duoDrawerToggle);
            duoDrawerToggle.syncState();
            activityHomeBinding.drawer.setDrawerLockMode(DuoDrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }

        if(CommonUtils.getCommonUtilsInstance().isUserLogin()){
            //duoDrawerToggle.setDrawerIndicatorEnabled(true);

        }else {
            //duoDrawerToggle.setDrawerIndicatorEnabled(false);

        }



        drawerView = activityHomeBinding.drawerMenu.getHeaderView();
        drawerView.findViewById(R.id.searchEventContainer).setOnClickListener(this);
        drawerView.findViewById(R.id.notificationContainer).setOnClickListener(this);
        drawerView.findViewById(R.id.rsvpTicketContainer).setOnClickListener(this);
        drawerView.findViewById(R.id.bookedEventsContainer).setOnClickListener(this);
        drawerView.findViewById(R.id.settingsContainer).setOnClickListener(this);
        drawerView.findViewById(R.id.favoritesContainer).setOnClickListener(this);
        drawerView.findViewById(R.id.viewProfileContainer).setOnClickListener(this);
        drawerView.findViewById(R.id.preferenceContainer).setOnClickListener(this);
        drawerView.findViewById(R.id.homeContainer).setOnClickListener(this);
        drawerView.findViewById(R.id.contactUsContainer).setOnClickListener(this);
        drawerView.findViewById(R.id.msgContainer).setOnClickListener(this);

        ((TextView) drawerView.findViewById(R.id.tvShowUserName)).setText(CommonUtils.getCommonUtilsInstance().getUserName());

        activityHomeBinding.drawer.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                getNotificationCountRequest();

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });



    }

    private void setMarginToShowLocation() {
        /* whenever app in login mode, i do not know why show location container more slide to right side, on wihtout login it shows perfect on
         * centre of the screen, but problem only occur when user in login stage, to that's why is used below to to change left side margin, its work fine*/
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) activityHomeBinding.locationContainer.getLayoutParams();
        if(!CommonUtils.getCommonUtilsInstance().isUserLogin()){
            params.setMarginStart(Utility.dpToPx(HomeActivity.this,60));
            return;
        }
        params.leftMargin = 0;
    }

    private void setBundleData() {
        if (bundle != null) {
            String activityName = bundle.getString(Constants.activityName);
            if (activityName != null && activityName.equalsIgnoreCase(getString(R.string.home_filter_activity))) {
                nearByNoAuthModal = bundle.getParcelableArrayList(Constants.nearByData);
                setupViewPager();
                Log.d("fnalksnfskla", "onCreate: " + nearByNoAuthModal.size());

            }


//            String fbLoginDetails = bundle.getString(getString(R.string.fb_login_details));
//            if(fbLoginDetails != null){
//
//                getFbLoginDetails(fbLoginDetails);
//            }
        }
    }

    private void getNotificationCountRequest() {
        Call<JsonElement> elementCall = APICall.getApiInterface().notificationCount(CommonUtils.getCommonUtilsInstance().getDeviceAuth());
        new APICall(HomeActivity.this).apiCalling(elementCall, this, APIs.NOTIFICATION_COUNT);
    }

    private void setLocation(double lat, double lng, boolean isFromHomeFilter) {
        Log.d("fnaslfnklas", lat + " MainActivity: " + lng);
        Geocoder geocoder = new Geocoder(HomeActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            String stateName = addresses.get(0).getAdminArea();
            String city = addresses.get(0).getLocality();
            String country = addresses.get(0).getCountryName();

            activityHomeBinding.tvShowCurrentLocation.setText(city + " " + stateName + " " + country);
            activityHomeBinding.tvShowCurrentLocation.setSelected(true);
            if (!isFromHomeFilter)
                nearByEventAuthRequest(lat, lng);
            Log.d("flaksnfslan", lat + " onActivityResult: " + lng);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("fnasklfnla", "setLocation: " + e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        myLoader.dismiss();
    }
    private void launchFrag(Fragment fragment){
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameRootView,fragment);
        transaction.commit();
    }
}
