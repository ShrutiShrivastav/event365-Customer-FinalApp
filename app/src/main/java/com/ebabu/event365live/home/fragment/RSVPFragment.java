package com.ebabu.event365live.home.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.FragmentRsvBinding;
import com.ebabu.event365live.home.adapter.RsvpListAdapter;
import com.ebabu.event365live.home.modal.rsvp.GetRsvpUserModal;
import com.ebabu.event365live.home.utils.RsvpItemDecoration;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.listener.RsvpAcceptListener;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.ShowToast;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
    private int getEventId;
    private String getStatusMsg;
    private List<GetRsvpUserModal.Datum> datumList;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myLoader = new MyLoader(context);
        activity = (Activity) context;
        this.context = context;
        activity.findViewById(R.id.ivFilterBtn).setVisibility(View.INVISIBLE);

    }

    public RSVPFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rsvBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_rsv,container,false);
        datumList = new ArrayList<>();
        if(!CommonUtils.getCommonUtilsInstance().isUserLogin()){
            rsvBinding.noDataFoundContainer.setVisibility(View.GONE);

            rsvBinding.rsvpRecyclerCardView.setVisibility(View.GONE);
            rsvBinding.rsvpCardView.setVisibility(View.VISIBLE);
            rsvBinding.rsvpCardView.setOnClickListener(this);

        }else {
            rsvBinding.rsvpRecyclerCardView.setVisibility(View.VISIBLE);
            rsvBinding.rsvpCardView.setVisibility(View.GONE);
            rsvBinding.noDataFoundContainer.setVisibility(View.GONE);
            showRsvpRequest();
        }
        return rsvBinding.getRoot();
    }
    private void setupRsvpShowList(){
        rsvpItemDecoration = new RsvpItemDecoration();
        rsvpListAdapter = new RsvpListAdapter(datumList, RSVPFragment.this);
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

    private void showRsvpRequest(){
        myLoader.show("");
        Call<JsonElement> rsvpCall = APICall.getApiInterface().showUserRsvp(CommonUtils.getCommonUtilsInstance().getDeviceAuth(),10,1);
        new APICall(activity).apiCalling(rsvpCall,this, APIs.GET_USER_RSVP);
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        if(typeAPI.equalsIgnoreCase(APIs.STATUS_RSVP)){
            for(int i=0;i<datumList.size();i++){
                if(datumList.get(i).getEventId() == getEventId){
                    if(getStatusMsg.equalsIgnoreCase("accept")){
                        datumList.get(i).setStatus("accepted");
                        break;
                    }
                    else if(getStatusMsg.equalsIgnoreCase("reject")){
                        datumList.remove(i);
                        //datumList.get(i).setStatus("rejected");
                        break;
                    }
                }
            }
            rsvpListAdapter.notifyDataSetChanged();
            return;
        }
        if(datumList.size()>0)
            datumList.clear();
        GetRsvpUserModal getRsvpUserModal = new Gson().fromJson(responseObj.toString(),GetRsvpUserModal.class);
        if(getRsvpUserModal.getData().size()>0){
            datumList.addAll(getRsvpUserModal.getData());
            setupRsvpShowList();
            return;
        }
        rsvBinding.rsvpRecyclerCardView.setVisibility(View.GONE);
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
            getEventId = eventId;
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
}
