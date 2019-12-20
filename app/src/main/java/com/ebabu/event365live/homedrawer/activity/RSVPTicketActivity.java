package com.ebabu.event365live.homedrawer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityRsvpticketBinding;
import com.ebabu.event365live.homedrawer.adapter.RsvpTicketAdapter;
import com.ebabu.event365live.homedrawer.modal.rsvpmodal.PaymentUser;
import com.ebabu.event365live.homedrawer.modal.rsvpmodal.RsvpBookedTicketModal;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.utils.CarouselEffectTransformer;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;

public class RSVPTicketActivity extends AppCompatActivity implements GetResponseData {
    private ActivityRsvpticketBinding rsvpTicketBinding;
    private RsvpTicketAdapter rsvpTicketAdapter;
    private MyLoader myLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rsvpTicketBinding  = DataBindingUtil.setContentView(this,R.layout.activity_rsvpticket);
        myLoader = new MyLoader(this);
        showBookedTicketRequest();
    }

    private void setupRsvpShowTicket(List<PaymentUser> paymentUserList){
        //LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        rsvpTicketAdapter = new RsvpTicketAdapter(this,paymentUserList);
        rsvpTicketBinding.rsvpViewpager.setPageMargin(40);
        rsvpTicketBinding.rsvpViewpager.setClipToPadding(false);
        rsvpTicketBinding.rsvpViewpager.setPadding(90, 0, 90, 0);
        rsvpTicketBinding.rsvpViewpager.setAdapter(rsvpTicketAdapter);
        rsvpTicketBinding.rsvpViewpager.setPageTransformer(false, new CarouselEffectTransformer(this));
    }

    public void backBtnOnClick(View view) {
        finish();
    }

    private void showBookedTicketRequest(){
        myLoader.show("");
        Call<JsonElement> bookedTicketCall = APICall.getApiInterface().getUserBookedTicket(CommonUtils.getCommonUtilsInstance().getDeviceAuth());
        new APICall(RSVPTicketActivity.this).apiCalling(bookedTicketCall,this, APIs.GET_USER_TICKET_BOOKED);
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        rsvpTicketBinding.noDataFoundContainer.setVisibility(View.GONE);
        rsvpTicketBinding.rsvpViewpager.setVisibility(View.VISIBLE);
        if(responseObj != null){
            RsvpBookedTicketModal bookedTicketModal = new Gson().fromJson(responseObj.toString(), RsvpBookedTicketModal.class);
            Log.d("nfaklnfklnaslf", responseObj+" onSuccess: "+bookedTicketModal.getData().getPaymentUser().size());
            setupRsvpShowTicket(bookedTicketModal.getData().getPaymentUser());
        }
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        if(errorCode != null && errorCode == 406){
            rsvpTicketBinding.noDataFoundContainer.setVisibility(View.VISIBLE);
            rsvpTicketBinding.rsvpViewpager.setVisibility(View.GONE);
            ((TextView)rsvpTicketBinding.noDataFoundContainer.findViewById(R.id.tvShowNoDataFound)).setText(getString(R.string.no_ticket_you_booked_yet));
        }
    }
}
