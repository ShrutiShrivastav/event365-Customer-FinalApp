package com.ebabu.event365live.home.adapter;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.ebabu.event365live.home.fragment.NearYouFragment;
import com.ebabu.event365live.home.fragment.RSVPFragment;
import com.ebabu.event365live.home.fragment.RecommendedFragment;
import com.ebabu.event365live.home.modal.nearbymodal.EventList;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.oncelaunch.modal.nearbynoauth.NearByNoAuthModal;

import java.util.ArrayList;

public class HomeViewAdapter extends FragmentStatePagerAdapter {

    private NearYouFragment nearYouFragment;
    private RecommendedFragment recommendedFragment;
    private RSVPFragment rsvpFragment;
    private ArrayList<EventList> nearByNoAuthModal =  new ArrayList<>();
    private Bundle bundle;
    private String[] tabName = {"Near You", "Recommended", "RSVP"};

    public HomeViewAdapter(@NonNull FragmentManager fm, ArrayList<EventList> nearByNoAuthModal) {
        super(fm);
        Log.d("fnlkanfla", this.nearByNoAuthModal.size()+"HomeViewAdapter: "+nearByNoAuthModal.size());
        //this.nearByNoAuthModal.clear();
        this.nearByNoAuthModal =  nearByNoAuthModal;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        Log.d("fnlkanfla", "getItem: "+position);
        Fragment fragment = null;
        switch (position)
        {
            case 0:
                if(nearYouFragment == null)
                    nearYouFragment = new NearYouFragment();
                    nearYouFragment.setArguments(getBundle());
                    fragment = nearYouFragment;
                break;

            case 1:
//                if(recommendedFragment == null)
                    recommendedFragment = new RecommendedFragment();
                    fragment = recommendedFragment;
                break;

            case 2:
//                if(rsvpFragment == null)
                    rsvpFragment = new RSVPFragment();
                    fragment = rsvpFragment;

                break;

//                default:
//                    fragment = new NearYouFragment();
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return tabName.length;
    }

//    @Nullable
//    @Override
//    public CharSequence getPageTitle(int position) {
//        return tabName[position];
//    }

    private Bundle getBundle(){
        if(bundle == null){
            bundle = new Bundle();
        }
        Log.d("fnlkanfla", "Bundle: "+nearByNoAuthModal.size());
        bundle.putParcelableArrayList(Constants.nearByData,nearByNoAuthModal);
        return bundle;
    }
}

