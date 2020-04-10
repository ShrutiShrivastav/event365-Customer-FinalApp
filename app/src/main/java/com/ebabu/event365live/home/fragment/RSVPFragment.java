package com.ebabu.event365live.home.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.FragmentRsvBinding;
import com.ebabu.event365live.home.adapter.RsvpListAdapter;
import com.ebabu.event365live.home.modal.RsvpHeaderModal;
import com.ebabu.event365live.home.modal.rsvp.GetRsvpUserModal;
import com.ebabu.event365live.home.utils.RsvpItemDecoration;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.EndlessRecyclerViewScrollListener;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.ShowToast;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class RSVPFragment extends Fragment implements View.OnClickListener, GetResponseData {

    private MyLoader myLoader;
    private FragmentRsvBinding rsvBinding;
    private RsvpListAdapter rsvpListAdapter;
    private RsvpItemDecoration rsvpItemDecoration;
    private Activity activity;
    private Context context;
    private int rsvpId;
    private String getStatusMsg;
    private List<GetRsvpUserModal.RSPVList> datumList;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    private List<RsvpHeaderModal> rsvpHeaderModals;
    public List<GetRsvpUserModal.RSPVList> rspvList;
    private List<GetRsvpUserModal.RSPVList> adapterRsvpList = new ArrayList<>();
    private int currentPage = 1;
    private int pageSize = 25;
    private LinearLayoutManager manager;
    private static boolean isRSVPLaunchFirstTime;
    private boolean isFromAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myLoader = new MyLoader(context);
        activity = (Activity) context;
        this.context = context;
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rsvBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_rsv, container, false);
        rsvBinding.rsvpBtnContainer.setOnClickListener(this);
        datumList = new ArrayList<>();
        rsvpHeaderModals = new ArrayList<>();

        if (!CommonUtils.getCommonUtilsInstance().isUserLogin()) {
            rsvBinding.noDataFoundContainer.setVisibility(View.GONE);
            rsvBinding.rsvpRecyclerContainer.setVisibility(View.GONE);
            rsvBinding.rsvpCardView.setVisibility(View.VISIBLE);
            rsvBinding.rsvpCardView.setOnClickListener(this);

        } else {
            rsvBinding.rsvpRecyclerContainer.setVisibility(View.VISIBLE);
            rsvBinding.rsvpCardView.setVisibility(View.GONE);
            rsvBinding.noDataFoundContainer.setVisibility(View.GONE);

            rspvList = new ArrayList<>();
            showRsvpRequest(currentPage, false);
            setupRsvpShowList();

        }


        return rsvBinding.getRoot();
    }

    private void setupRsvpShowList() {
        rsvpItemDecoration = new RsvpItemDecoration();
        rsvpListAdapter = new RsvpListAdapter(rspvList, RSVPFragment.this);
        manager = new LinearLayoutManager(getContext());
        rsvBinding.recyclerRsvp.setLayoutManager(manager);
        rsvBinding.recyclerRsvp.addItemDecoration(rsvpItemDecoration);
        rsvBinding.recyclerRsvp.setAdapter(rsvpListAdapter);

    }

    private void refreshData(List<GetRsvpUserModal.RSPVList> lists) {
        rsvpListAdapter.notifyDataSetChanged();
        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(manager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (lists.size() != 0) {
                    currentPage++;
                    showRsvpRequest(currentPage, false);
                }
            }
        };
        rsvBinding.recyclerRsvp.addOnScrollListener(endlessRecyclerViewScrollListener);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rsvpCardView) {
            ShowToast.infoToast(getContext(), getString(R.string.on_progress));
        } else if (v.getId() == R.id.rsvpBtnContainer) {
            CommonUtils.getCommonUtilsInstance().loginAlert(activity, false, "");
        }
    }

    public void showRsvpRequest(int currentPage, boolean isFromAdapter) {
        this.isFromAdapter = isFromAdapter;
        if (isFromAdapter) currentPage = 1;
        myLoader.show("");
        Call<JsonElement> rsvpCall = APICall.getApiInterface().showUserRsvp(CommonUtils.getCommonUtilsInstance().getDeviceAuth(), 25, currentPage);
        new APICall(activity).apiCalling(rsvpCall, this, APIs.GET_USER_RSVP);
        Log.d("nflknaklnfknnak", isFromAdapter + " showRsvpRequest: " + currentPage);
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        if (typeAPI.equalsIgnoreCase(APIs.GET_USER_RSVP)) {
            GetRsvpUserModal getRsvpUserModal = new Gson().fromJson(responseObj.toString(), GetRsvpUserModal.class);
            if (getRsvpUserModal.getData().getData().size() == 0) {
                if (currentPage > 1) {
                    refreshData(getRsvpUserModal.getData().getData());
                    return;
                }
                hideView();
            } else {
                adapterRsvpList = prepareList(getRsvpUserModal.getData().getData());
                rspvList.addAll(adapterRsvpList);
                refreshData(rspvList);
            }
            isRSVPLaunchFirstTime = true;
        }
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        isRSVPLaunchFirstTime = false;
    }

    private List<GetRsvpUserModal.RSPVList> prepareList(List<GetRsvpUserModal.RSPVList> rspvLocalList) {
        List<String> uniqueList = new ArrayList<>();

        for (int i = 0; i < rspvLocalList.size(); i++) {
            GetRsvpUserModal.RSPVList list = rspvLocalList.get(i);
            boolean flag = false;

            if (rspvList != null) {
                for (int j = rspvList.size() - 1; j > 0; j--) {
                    String s = rspvList.get(j).getDateString();
                    String s2 = list.getDateString();
                    if (s != null && s.equals(s2)) {
                        rspvList.add(list);
                        flag = true;
                        break;
                    }
                }
            }

            if (!flag && !uniqueList.contains(list.getDateString())) {
                uniqueList.add(list.getDateString());
            }
        }
        List<GetRsvpUserModal.RSPVList> expectedList = new ArrayList<>();

        for (String getDateOnly : uniqueList) {

            GetRsvpUserModal.RSPVList mItemHead = new GetRsvpUserModal.RSPVList();
            mItemHead.setHead(true);
            mItemHead.setDateString(getDateOnly);
            expectedList.add(mItemHead);

            for (int i = 0; i < rspvLocalList.size(); i++) {
                GetRsvpUserModal.RSPVList mItem = rspvLocalList.get(i);
                if (getDateOnly.equals(mItem.getDateString())) {
                    expectedList.add(mItem);
                }
            }
        }


        return expectedList;
    }

    private void hideView() {
        rsvBinding.rsvpRecyclerContainer.setVisibility(View.GONE);
        rsvBinding.rsvpCardView.setVisibility(View.GONE);
        rsvBinding.noDataFoundContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        currentPage = 1;
    }

    public void smoothRecyclerScrolled(int pos) {
        if (isFromAdapter) {
            if (rspvList.size() > 0)
                rspvList.clear();
            rsvBinding.recyclerRsvp.smoothScrollToPosition(pos);
        }
    }


}
