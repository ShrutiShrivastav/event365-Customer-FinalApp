package com.ebabu.event365live.homedrawer.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.FragmentUpcomingBinding;
import com.ebabu.event365live.homedrawer.adapter.UpcomingAdapter;
import com.ebabu.event365live.homedrawer.modal.upcoming.UpcomingAttendModal;
import com.ebabu.event365live.httprequest.Constants;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpcomingFragment extends Fragment {

    private UpcomingAdapter upcomingAdapter;
    private FragmentUpcomingBinding upcomingBinding;
    private UpcomingAttendModal upcomingAttendModal;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        assert getArguments() != null;
        upcomingAttendModal = getArguments().getParcelable(Constants.wishList);
    }

    public UpcomingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        upcomingBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_upcoming,container,false);
        initView();
        return upcomingBinding.getRoot();
    }

    private void initView(){
        if(upcomingAttendModal.getUpcomingAttendData().getUpcomingEvent().size() == 0){
            upcomingBinding.recyclerUpcoming.setVisibility(View.GONE);
            upcomingBinding.layout.setVisibility(View.VISIBLE);
            upcomingBinding.layout.findViewById(R.id.tvShowNoDataFoundNote).setVisibility(View.VISIBLE);
            ((TextView)upcomingBinding.layout.findViewById(R.id.tvShowNoDataFoundNote)).setText("You does not have any upcoming booked events");
            return;
        }
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        upcomingBinding.recyclerUpcoming.setLayoutManager(manager);
        upcomingAdapter = new UpcomingAdapter(upcomingAttendModal.getUpcomingAttendData().getUpcomingEvent());
        upcomingBinding.recyclerUpcoming.setAdapter(upcomingAdapter);
    }
}
