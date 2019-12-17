package com.ebabu.event365live.homedrawer.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.ebabu.event365live.homedrawer.fragment.AttendedFragment;
import com.ebabu.event365live.homedrawer.fragment.ComingSoonFragment;
import com.ebabu.event365live.homedrawer.fragment.PastFragment;
import com.ebabu.event365live.homedrawer.fragment.UpcomingFragment;
import com.ebabu.event365live.homedrawer.modal.upcoming.AttendentEvent;
import com.ebabu.event365live.homedrawer.modal.upcoming.UpcomingAttendModal;
import com.ebabu.event365live.homedrawer.modal.upcoming.UpcomingEvent;
import com.ebabu.event365live.httprequest.Constants;

import java.util.List;

public class MyWishListAdapter extends FragmentPagerAdapter {

    private UpcomingFragment upcomingFragment;
    private AttendedFragment attendedFragment;
    private String[] myWishListName = {"Upcoming", "Attended"};
    private UpcomingAttendModal upcomingAttendModal;
    private Bundle bundle;


    public MyWishListAdapter(@NonNull FragmentManager fm, int behavior, UpcomingAttendModal upcomingAttendModal) {
        super(fm, behavior);
        this.upcomingAttendModal = upcomingAttendModal;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if(position == 0)
        {
            if(upcomingFragment == null){
                upcomingFragment = new UpcomingFragment();
            }
            fragment = upcomingFragment;
        }
        else if(position == 1)
        {
            if(attendedFragment == null)
            {
                attendedFragment = new AttendedFragment();
            }
            fragment = attendedFragment;
        }
        else {
            fragment = new UpcomingFragment();
        }

        fragment.setArguments(getBundle());
        return fragment;
    }


    @Override
    public int getCount() {
        return myWishListName.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return myWishListName[position];
    }

    private Bundle getBundle()
    {
        if(bundle == null) {
            bundle = new Bundle();
        }
        bundle.putParcelable(Constants.wishList,upcomingAttendModal);
        return bundle;
    }
}
