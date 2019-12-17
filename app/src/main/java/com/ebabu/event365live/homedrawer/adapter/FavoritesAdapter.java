package com.ebabu.event365live.homedrawer.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.ebabu.event365live.homedrawer.fragment.ComingSoonFragment;
import com.ebabu.event365live.homedrawer.fragment.PastFragment;
import com.ebabu.event365live.homedrawer.modal.pastmodal.FavoritesEventListModal;
import com.ebabu.event365live.httprequest.Constants;

public class FavoritesAdapter extends FragmentStatePagerAdapter {


    private String[] favouritesTitleName = {"Past", "Coming Soon"};
    private PastFragment pastFragment;
    private ComingSoonFragment soonFragment;
    private FavoritesEventListModal favoritesEventListModal;
    private Bundle bundle;

    public FavoritesAdapter(@NonNull FragmentManager fm, int behavior,FavoritesEventListModal favoritesEventListModal) {
        super(fm, behavior);
        this.favoritesEventListModal = favoritesEventListModal;

    }

    @Override
    public int getCount() {
        return favouritesTitleName.length;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if(position == 0)
        {
            if(pastFragment == null)
            {
                pastFragment = new PastFragment();
            }
            fragment = pastFragment;
        }
        else if(position == 1)
        {
            if(soonFragment == null)
            {
                soonFragment = new ComingSoonFragment();
            }
            fragment = soonFragment;
        }
        else {
            fragment = new PastFragment();
        }

        fragment.setArguments(getBundle());
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return favouritesTitleName[position];
    }

    private Bundle getBundle()
    {
        if(bundle == null) {
            bundle = new Bundle();
        }
        bundle.putParcelable(Constants.favoritesList,favoritesEventListModal);
        return bundle;
    }
}
