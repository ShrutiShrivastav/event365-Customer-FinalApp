package com.ebabu.event365live.homedrawer.fragment;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.FragmentAttendedBinding;
import com.ebabu.event365live.homedrawer.adapter.AttendAdapter;
import com.ebabu.event365live.homedrawer.adapter.UpcomingAdapter;
import com.ebabu.event365live.homedrawer.modal.upcoming.UpcomingAttendModal;
import com.ebabu.event365live.httprequest.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class AttendedFragment extends Fragment {
    private FragmentAttendedBinding attendedBinding;
    private UpcomingAttendModal upcomingAttendModal;
    private AttendAdapter attendAdapter;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        assert getArguments() != null;
        upcomingAttendModal = getArguments().getParcelable(Constants.wishList);
    }
    public AttendedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        attendedBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_attended,container,false);
        initView();
        return attendedBinding.getRoot();
    }


    private void initView(){
        if(upcomingAttendModal.getUpcomingAttendData().getUpcomingEvent() == null){
            attendedBinding.recyclerAttendEventList.setVisibility(View.GONE);
            attendedBinding.layout.setVisibility(View.VISIBLE);
            attendedBinding.layout.findViewById(R.id.tvShowNoDataFoundNote).setVisibility(View.VISIBLE);
            ((TextView)attendedBinding.layout.findViewById(R.id.tvShowNoDataFoundNote)).setText("You does not have any past booked events");
            return;
        }
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        attendedBinding.recyclerAttendEventList.setLayoutManager(manager);
        attendAdapter = new AttendAdapter(upcomingAttendModal.getUpcomingAttendData().getAttendentEvent());
        attendedBinding.recyclerAttendEventList.setAdapter(attendAdapter);
    }


}
