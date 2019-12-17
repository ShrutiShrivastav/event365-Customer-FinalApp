package com.ebabu.event365live.home.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.FragmentRsvBinding;
import com.ebabu.event365live.home.adapter.RsvpListAdapter;
import com.ebabu.event365live.home.utils.RsvpItemDecoration;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.ShowToast;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 */
public class RSVPFragment extends Fragment implements View.OnClickListener {

    private  FragmentRsvBinding rsvBinding;
    private RsvpListAdapter rsvpListAdapter;
    private RsvpItemDecoration rsvpItemDecoration;


    public RSVPFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rsvBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_rsv,container,false);

        if(!CommonUtils.getCommonUtilsInstance().isUserLogin()){
            rsvBinding.noDataFoundContainer.setVisibility(View.GONE);
            rsvBinding.recyclerRsvp.setVisibility(View.GONE);
            rsvBinding.rsvpCardView.setVisibility(View.VISIBLE);
            rsvBinding.rsvpCardView.setOnClickListener(this);
        }else {
            rsvBinding.recyclerRsvp.setVisibility(View.GONE);
            rsvBinding.rsvpCardView.setVisibility(View.GONE);
            rsvBinding.noDataFoundContainer.setVisibility(View.VISIBLE);
           // setupRsvpShowList();
        }

        return rsvBinding.getRoot();
    }
    private void setupRsvpShowList(){
        rsvpItemDecoration = new RsvpItemDecoration();
        rsvpListAdapter = new RsvpListAdapter(getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rsvBinding.recyclerRsvp.setLayoutManager(linearLayoutManager);
        rsvBinding.recyclerRsvp.addItemDecoration(rsvpItemDecoration);
        rsvBinding.recyclerRsvp.setAdapter(rsvpListAdapter);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.rsvpCardView){
            ShowToast.infoToast(getContext(),getString(R.string.on_progress));
        }
    }
}
