package com.ebabu.event365live.home.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.ebabu.event365live.homedrawer.modal.rsvpmodal.RsvpBookedTicketModal;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.listener.RsvpAcceptListener;
import com.ebabu.event365live.oncelaunch.utils.EndlessRecyclerViewScrollListener;
import com.ebabu.event365live.userinfo.activity.EventDetailsActivity;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.ShowToast;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class RSVPFragment extends Fragment implements View.OnClickListener, GetResponseData , RsvpAcceptListener {

    private MyLoader myLoader;
    private  FragmentRsvBinding rsvBinding;
    private RsvpListAdapter rsvpListAdapter;
    private RsvpItemDecoration rsvpItemDecoration;
    private Activity activity;
    private Context context;
    private int rsvpId;
    private String getStatusMsg;
    private List<GetRsvpUserModal.RSPVList> datumList;

    private List<RsvpHeaderModal> rsvpHeaderModals;
    GetRsvpUserModal getRsvpUserModal ;
    private int currentPage = 1;
    private int pageSize = 15;
    private boolean isLoading = false;
    int itemCount = 0;
    private boolean isLastPage = false;
    private boolean isMoreDataAvailable;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myLoader = new MyLoader(context);
        activity = (Activity) context;
        this.context = context;
//        activity.findViewById(R.id.ivFilterBtn).setVisibility(View.INVISIBLE);
    }

    public RSVPFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rsvBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_rsv,container,false);
        rsvBinding.rsvpBtnContainer.setOnClickListener(this);
        datumList = new ArrayList<>();
        rsvpHeaderModals = new ArrayList<>();
//        if(!CommonUtils.getCommonUtilsInstance().isUserLogin()){
//            rsvBinding.noDataFoundContainer.setVisibility(View.GONE);
//
//            rsvBinding.rsvpRecyclerContainer.setVisibility(View.GONE);
//            rsvBinding.rsvpCardView.setVisibility(View.VISIBLE);
//            rsvBinding.rsvpCardView.setOnClickListener(this);
//
//        }else {
//            rsvBinding.rsvpRecyclerContainer.setVisibility(View.VISIBLE);
//            rsvBinding.rsvpCardView.setVisibility(View.GONE);
//            rsvBinding.noDataFoundContainer.setVisibility(View.GONE);
//            showRsvpRequest(currentPage);
//        }
        return rsvBinding.getRoot();
    }
    private void setupRsvpShowList(){
        rsvpItemDecoration = new RsvpItemDecoration();
        rsvpListAdapter = new RsvpListAdapter(prepareList(getRsvpUserModal.getData().getData()), RSVPFragment.this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rsvBinding.recyclerRsvp.setLayoutManager(linearLayoutManager);
        rsvBinding.recyclerRsvp.addItemDecoration(rsvpItemDecoration);
        rsvBinding.recyclerRsvp.setAdapter(rsvpListAdapter);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.rsvpCardView){
            ShowToast.infoToast(getContext(),getString(R.string.on_progress));
        }else if(v.getId() == R.id.rsvpBtnContainer){
            CommonUtils.getCommonUtilsInstance().loginAlert(activity,false);
        }
    }

    private void showRsvpRequest(int currentPage){
        myLoader.show("");
        Call<JsonElement> rsvpCall = APICall.getApiInterface().showUserRsvp(CommonUtils.getCommonUtilsInstance().getDeviceAuth(),pageSize,currentPage);
        new APICall(activity).apiCalling(rsvpCall,this, APIs.GET_USER_RSVP);
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        if(typeAPI.equalsIgnoreCase(APIs.STATUS_RSVP)){
            rsvpListAdapter.dataNotify(rsvpId,getStatusMsg);
            return;
        }
        if(datumList.size()>0)
            datumList.clear();
        getRsvpUserModal = new Gson().fromJson(responseObj.toString(),GetRsvpUserModal.class);


        Set<String> unique= new HashSet<>();

        if(getRsvpUserModal.getData().getData().size()>0){
            RsvpHeaderModal rsvpHeaderModal = null;
            for(GetRsvpUserModal.RSPVList r : getRsvpUserModal.getData().getData()){
                rsvpHeaderModal = new RsvpHeaderModal();
                unique.add(CommonUtils.getCommonUtilsInstance().getDateMonthName(r.getDateTime()));
                rsvpHeaderModals.add(rsvpHeaderModal);
            }
            datumList.addAll(getRsvpUserModal.getData().getData());
            isMoreDataAvailable = true;
            setupRsvpShowList();
            return;
        }
        isMoreDataAvailable = false;
        rsvBinding.rsvpRecyclerContainer.setVisibility(View.GONE);
        rsvBinding.rsvpCardView.setVisibility(View.GONE);
        rsvBinding.noDataFoundContainer.setVisibility(View.VISIBLE);
    }
    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
    }
    @Override
    public void acceptRejectListener(int rsvpId, int eventId, String statusMsg) {
        if(statusMsg != null){
            this.rsvpId = rsvpId;
            getStatusMsg = statusMsg;
            rsvpAcceptOrRejectRequest(rsvpId,statusMsg);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("fnklasnfklsa", "RSVP: ");
    }

    private void rsvpAcceptOrRejectRequest(int rsvpId, String getStatusMsg){
        myLoader.show("");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Constants.id,rsvpId);
        jsonObject.addProperty(Constants.ApiKeyName.status,getStatusMsg);
        Call<JsonElement> rsvpCall = APICall.getApiInterface().statusRsvp(CommonUtils.getCommonUtilsInstance().getDeviceAuth(),jsonObject);
        new APICall(activity).apiCalling(rsvpCall,this, APIs.STATUS_RSVP);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("fnklasnfklsa", "RSVP: ");
    }

//    private List<GetRsvpUserModal.RSPVList> prepareList(List<GetRsvpUserModal.RSPVList> rsvpHeaderModals){
//        HashSet<UniqueDateModal> dates = new HashSet<>();
//        for (GetRsvpUserModal.RSPVList item : rsvpHeaderModals) {
//            UniqueDateModal uniqueDateModal = new UniqueDateModal();
//            uniqueDateModal.setCompareDate(item.getDateTime().split("T")[0]);
//            uniqueDateModal.setShowDate(item.getDateTime());
//            dates.add(uniqueDateModal);
//        }
//        List<GetRsvpUserModal.RSPVList> expectedList = new ArrayList<>();
//
//        for (UniqueDateModal date: dates){
//            GetRsvpUserModal.RSPVList mItemHead = new GetRsvpUserModal.RSPVList();
//            mItemHead.setHead(true);
//            mItemHead.setDateString(date.getCompareDate());
//            mItemHead.setGetEventDate(date.getShowDate());
//            expectedList.add(mItemHead);
//            Log.d("fnaklsnflsa", "prepareList: "+mItemHead.isHead());
//            for (int i = 0; i < rsvpHeaderModals.size(); i++){
//                GetRsvpUserModal.RSPVList mItem = rsvpHeaderModals.get(i);
//                if (date.getCompareDate().equalsIgnoreCase(mItem.getDateString())) {
//                    Log.d("fnaklsnfsssslsa", "prepareList: "+i);
//                    expectedList.add(mItem);
//                }
//
//            }
//        }
//        Log.d("fnaklsnfsssslsssssa", "prepareList: "+expectedList.size());
//        return expectedList;
//    }

    private List<GetRsvpUserModal.RSPVList> prepareList(List<GetRsvpUserModal.RSPVList> rspvListList){
        HashMap<String, GetRsvpUserModal.RSPVList> dates = new HashMap<>();
        for (GetRsvpUserModal.RSPVList item : rspvListList) {
            dates.put(item.getDateTime().split("T")[0],item);
        }


        List<GetRsvpUserModal.RSPVList> expectedList = new ArrayList<>();

        for (Map.Entry<String,GetRsvpUserModal.RSPVList> item: dates.entrySet()) {

            for (int i = 0; i < rspvListList.size(); i++){
                GetRsvpUserModal.RSPVList mItem = rspvListList.get(i);
                if (item.getValue().getDateString().equals(mItem.getDateString())) {
                    expectedList.add(mItem);
                }
            }

            GetRsvpUserModal.RSPVList mItemHead = new GetRsvpUserModal.RSPVList();
            mItemHead.setHead(true);
            mItemHead.setDateString(item.getValue().getDateString());
            mItemHead.setGetEventDate(item.getValue().getGetEventDate());
            expectedList.add(mItemHead);


        }
        Collections.reverse(expectedList);
        return expectedList;
    }




}
