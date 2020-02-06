package com.ebabu.event365live.homedrawer.fragment;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.FragmentComingSoonBinding;
import com.ebabu.event365live.homedrawer.adapter.ComingSoonAdapter;
import com.ebabu.event365live.homedrawer.adapter.UpcomingAdapter;
import com.ebabu.event365live.homedrawer.modal.pastmodal.FavoritesEventListModal;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.listener.LikeDislikeListener;
import com.ebabu.event365live.userinfo.modal.eventdetailsmodal.RelatedEvent;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 */
public class ComingSoonFragment extends Fragment {

    private FragmentComingSoonBinding comingSoonBinding;
    private ComingSoonAdapter comingSoonAdapter;
    private FavoritesEventListModal favoritesEventListModal;

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        assert getArguments() != null;
        favoritesEventListModal = getArguments().getParcelable(Constants.favoritesList);
    }

    public ComingSoonFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        comingSoonBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_coming_soon,container,false);
        showUpComingList();
        return comingSoonBinding.getRoot();
    }

    private void showUpComingList(){

        if(favoritesEventListModal.getData().getComingSoon() != null){
            if(favoritesEventListModal.getData().getComingSoon().size() != 0){
                comingSoonBinding.layout.setVisibility(View.GONE);
                comingSoonBinding.recyclerComingSoon.setVisibility(View.VISIBLE);
                LinearLayoutManager manager = new LinearLayoutManager(getContext());
                comingSoonBinding.recyclerComingSoon.setLayoutManager(manager);
                comingSoonAdapter = new ComingSoonAdapter(favoritesEventListModal.getData().getComingSoon(), ComingSoonFragment.this);
                comingSoonBinding.recyclerComingSoon.setAdapter(comingSoonAdapter);
                comingSoonAdapter.notifyDataSetChanged();
                return;
            }
            comingSoonBinding.layout.setVisibility(View.VISIBLE);
            comingSoonBinding.recyclerComingSoon.setVisibility(View.GONE);
        }
    }

    public void updateList(int listSize){
        if(listSize ==0){
            comingSoonBinding.layout.setVisibility(View.VISIBLE);
            comingSoonBinding.recyclerComingSoon.setVisibility(View.GONE);
        }
    }
}
