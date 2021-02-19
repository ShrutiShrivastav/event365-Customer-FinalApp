package com.ebabu.event365live.home.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.ebabu.event365live.home.fragment.RSVPCompletedFragment;
import com.ebabu.event365live.home.fragment.RSVPPendingFragment;
import com.ebabu.event365live.home.fragment.RSVPTicketFragment;

public class RsvpAdapter extends FragmentStatePagerAdapter {

    private RSVPPendingFragment rsvpPendingFragment;
    private RSVPCompletedFragment rsvpCompletedFragment;
    private String[] tabName = {"Pending", "Accepted", "Tickets"};
    private Fragment fragment;

    public RsvpAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new RSVPPendingFragment();
                break;
            case 1:
                fragment = new RSVPCompletedFragment();
                break;
            case 2:
                fragment = new RSVPTicketFragment();
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return tabName.length;
    }
}

