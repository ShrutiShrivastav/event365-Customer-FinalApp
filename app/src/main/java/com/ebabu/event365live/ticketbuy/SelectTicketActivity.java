package com.ebabu.event365live.ticketbuy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivitySelectTicketBinding;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.listener.SelectedVipTicketListener;
import com.ebabu.event365live.ticketbuy.adapter.FreeTicketAdapter;
import com.ebabu.event365live.ticketbuy.adapter.RegularTicketAdapter;
import com.ebabu.event365live.ticketbuy.adapter.VipTicketAdapter;
import com.ebabu.event365live.ticketbuy.modal.CalculateEventPriceModal;
import com.ebabu.event365live.ticketbuy.modal.FreeTicket;
import com.ebabu.event365live.ticketbuy.modal.RegularTicketInfo;
import com.ebabu.event365live.ticketbuy.modal.RegularTicketSeatingInfo;
import com.ebabu.event365live.ticketbuy.modal.TicketSelectionModal;
import com.ebabu.event365live.ticketbuy.modal.VipTableSeatingInfo;
import com.ebabu.event365live.ticketbuy.modal.VipTicketInfo;
import com.ebabu.event365live.ticketbuy.modal.ticketmodal.FinalSelectTicketModal;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.ShowToast;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class SelectTicketActivity extends AppCompatActivity implements GetResponseData, View.OnClickListener, SelectedVipTicketListener {
    private ActivitySelectTicketBinding ticketBinding;
    private FreeTicketAdapter freeTicketAdapter;
    private RegularTicketAdapter regularTicketAdapter;
    private VipTicketAdapter tableSeatingAdapter;
    private MyLoader myLoader;
    String eventId, eventName, eventStartTime, eventEndTime, eventDate, eventAdd;
    private int getAllEventPrice;
    ArrayAdapter freeTicketSpinnerAdapter;
    private List<FinalSelectTicketModal> finalSelectTicketModals;
    private TicketSelectionModal selectionModal;
    private boolean isUserLogin;
    List<CalculateEventPriceModal> priceList = new ArrayList<>();
    boolean isFirstTimeFlag;
    public static boolean userIsInteracting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLoader = new MyLoader(this);
        ticketBinding = DataBindingUtil.setContentView(this,R.layout.activity_select_ticket);
        ticketBinding.freeTicketTitleContainer.setOnClickListener(this);
        ticketBinding.regularTicketTitleContainer.setOnClickListener(this);
        ticketBinding.vipTitleContainer.setOnClickListener(this);
        Bundle bundle = getIntent().getExtras();
        finalSelectTicketModals = new ArrayList<>();

        if(bundle != null) {
            eventId = bundle.getString(Constants.ApiKeyName.eventId);
            eventName = bundle.getString(Constants.eventName);
            eventStartTime = bundle.getString(Constants.eventStartTime);
            eventEndTime = bundle.getString(Constants.eventEndTime);
            eventDate = bundle.getString(Constants.eventDate);
            eventAdd = bundle.getString(Constants.eventAdd);

            ticketBinding.tvShowEventName.setText(eventName);
            ticketBinding.tvShowEventTime.setText(CommonUtils.getCommonUtilsInstance().getStartEndEventTime(eventStartTime)+" - "+CommonUtils.getCommonUtilsInstance().getStartEndEventTime(eventEndTime));
            ticketBinding.tvShowEventDate.setText(CommonUtils.getCommonUtilsInstance().getDateMonthYearName(eventDate,true));
            ticketBinding.tvShowEventAdd.setText(eventAdd);
            getTicketInfoRequest();
        }

//        ticketBinding.freeTicketExpand.toggle();

    }

    public void backBtnOnClick(View view) {
        finish();
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        if(responseObj != null){
            selectionModal = new Gson().fromJson(responseObj.toString(), TicketSelectionModal.class);
            vipCustomJSONObj(selectionModal);
            regularCustomJSONObj(selectionModal);
            if(selectionModal.getTicketSelectionData().getFreeTicket() != null && selectionModal.getTicketSelectionData().getFreeTicket().size() >0 ){
                //TODO implement Free Ticket view
                setupFreeTicket(selectionModal.getTicketSelectionData().getFreeTicket());
            }else {
                ticketBinding.freeTicketTitleContainer.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        ShowToast.errorToast(SelectTicketActivity.this,message);
        Log.d("nflkanflknlan", "onFailed: ");
    }

    private void getTicketInfoRequest(){
        myLoader.show("");
            Call<JsonElement> getTicketInfoCallback = APICall.getApiInterface().getTicketInfo(CommonUtils.getCommonUtilsInstance().getDeviceAuth(),"277");
            new APICall(SelectTicketActivity.this).apiCalling(getTicketInfoCallback,this,APIs.GET_TICKET_INFO);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.freeTicketTitleContainer:{
                     if(!ticketBinding.freeTicketExpand.isExpanded()) {
                         ticketBinding.freeTicketExpand.expand();
                         Glide.with(SelectTicketActivity.this).load(R.drawable.ticket_select_icon).into(ticketBinding.ivFreeTicketHeaderIcon);
                         return;
                     }
                         ticketBinding.freeTicketExpand.collapse();
                        Glide.with(SelectTicketActivity.this).load(R.drawable.ticket_unselected_icon).into(ticketBinding.ivFreeTicketHeaderIcon);
                    break; }
            case R.id.regularTicketTitleContainer:
                if(!ticketBinding.regularTicketExpand.isExpanded()) {
                    ticketBinding.regularTicketExpand.expand();
                    Glide.with(SelectTicketActivity.this).load(R.drawable.ticket_select_icon).into(ticketBinding.ivRegularIcon);
                    return;
                }
                ticketBinding.regularTicketExpand.collapse();
                Glide.with(SelectTicketActivity.this).load(R.drawable.ticket_unselected_icon).into(ticketBinding.ivRegularIcon);
                break;

            case R.id.vipTitleContainer:
                if(!ticketBinding.vipTicketExpand.isExpanded()) {
                    ticketBinding.vipTicketExpand.expand();
                    Glide.with(SelectTicketActivity.this).load(R.drawable.ticket_select_icon).into(ticketBinding.ivVipTicketIcon);
                    return;
                }
                ticketBinding.vipTicketExpand.collapse();
                Glide.with(SelectTicketActivity.this).load(R.drawable.ticket_unselected_icon).into(ticketBinding.ivVipTicketIcon);
                break;
        }
    }

    private void setupFreeTicket(List<FreeTicket> freeTicketList) {
        freeTicketAdapter = new FreeTicketAdapter(SelectTicketActivity.this,freeTicketList);
        ticketBinding.recyclerFreeTicket.setAdapter(freeTicketAdapter);
    }

    private void vipCustomJSONObj(TicketSelectionModal selectionModal){
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject setObj1 = null;

        if (selectionModal.getTicketSelectionData().getVipTableSeating().getVipTicketInfo() != null &&
                selectionModal.getTicketSelectionData().getVipTableSeating().getVipTicketInfo().size() >0) {
            ticketBinding.vipTitleContainer.setVisibility(View.VISIBLE);
            int getRegularTicketSize = selectionModal.getTicketSelectionData().getVipTableSeating().getVipTicketInfo().size();
            Log.d("nflnalnfklan", "vipCustomJSONObj: "+getRegularTicketSize);
            for(int i=0;i<getRegularTicketSize;i++){
                try {
                    VipTicketInfo info = selectionModal.getTicketSelectionData().getVipTableSeating().getVipTicketInfo().get(i);
                    setObj1 = new JSONObject();
                    setObj1.put("regularTicketFlag",0);
                    setObj1.put("id",info.getId());
                    setObj1.put("ticketType",info.getTicketType());
                    setObj1.put("ticketName",info.getTicketName());
                    setObj1.put("totalQuantity",info.getTotalQuantity());
                    setObj1.put("description",info.getDescription());
//                    if(info.getPricePerTicket() != null)
//                    getAllEventPrice = getAllEventPrice + Integer.parseInt(info.getPricePerTicket());
                    setObj1.put("pricePerTicket",info.getPricePerTicket());
                    setObj1.put("noOfTables",0);
                    setObj1.put("pricePerTable",0);
                    setObj1.put("parsonPerTable",0);
                    jsonArray.put(setObj1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            ticketBinding.vipTitleContainer.setVisibility(View.GONE);
        }

        if( selectionModal.getTicketSelectionData().getVipTableSeating().getVipTableSeatingInfo() != null &&
                selectionModal.getTicketSelectionData().getVipTableSeating().getVipTableSeatingInfo().size() > 0){
            ticketBinding.vipTitleContainer.setVisibility(View.VISIBLE);
            int getRegularSeatingSize = selectionModal.getTicketSelectionData().getVipTableSeating().getVipTableSeatingInfo().size();
            Log.d("nfklanlfknal", "vipCustomJSONObj: "+getRegularSeatingSize);
            for(int i=0;i<getRegularSeatingSize;i++){
                try {
                    VipTableSeatingInfo info = selectionModal.getTicketSelectionData().getVipTableSeating().getVipTableSeatingInfo().get(i);

                    setObj1 = new JSONObject();
                    setObj1.put("regularTicketFlag",1);
                    setObj1.put("id",info.getId());
                    setObj1.put("ticketType",info.getTicketType());
                    setObj1.put("ticketName",info.getTicketName());
                    setObj1.put("totalQuantity",info.getTotalQuantity());
                    setObj1.put("description",info.getDescription());
                    setObj1.put("pricePerTicket",0);
                    setObj1.put("noOfTables",info.getNoOfTables());
//                    if(info.getPricePerTable() != null)
//                    getAllEventPrice = getAllEventPrice + Integer.parseInt(info.getPricePerTable());
                    setObj1.put("pricePerTable",info.getPricePerTable());
                    setObj1.put("parsonPerTable",info.getParsonPerTable());
                    Log.d("nfklanlfknal", "vipCustomJSONObj: "+getRegularSeatingSize);
                    jsonArray.put(setObj1);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("nfklanlfknal", "JSONException: "+e.getMessage());
                }
            }
        }else {
            ticketBinding.regularTicketTitleContainer.setVisibility(View.GONE);
            return;
        }
        try {
            jsonObject.put("tickets",jsonArray);
            FinalSelectTicketModal modal = new Gson().fromJson(jsonObject.toString(),FinalSelectTicketModal.class);
            setupVipShowTicket(modal);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("nfklanlfknal", "printStackTrace: "+e.getMessage());
        }
    }

    private void regularCustomJSONObj(TicketSelectionModal selectionModal){
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject setObj1 = null;

        if (selectionModal.getTicketSelectionData().getRegularTableSeating().getRegularTicketInfo() != null &&
                selectionModal.getTicketSelectionData().getRegularTableSeating().getRegularTicketInfo().size() >0) {
            ticketBinding.regularTicketTitleContainer.setVisibility(View.VISIBLE);
            int getRegularTicketSize = selectionModal.getTicketSelectionData().getRegularTableSeating().getRegularTicketInfo().size();
            Log.d("nflnalnfklan", "regularCustomJSONObj: "+getRegularTicketSize);
            for(int i=0;i<getRegularTicketSize;i++){
                try {
                    RegularTicketInfo info = selectionModal.getTicketSelectionData().getRegularTableSeating().getRegularTicketInfo().get(i);
                    setObj1 = new JSONObject();
                    setObj1.put("regularTicketFlag",0);
                    setObj1.put("id",info.getId());
                    setObj1.put("ticketType",info.getTicketType());
                    setObj1.put("ticketName",info.getTicketName());
                    setObj1.put("totalQuantity",info.getTotalQuantity());
                    setObj1.put("description",info.getDescription());
//                    if(info.getPricePerTicket() != null)
//                    getAllEventPrice = getAllEventPrice + Integer.parseInt(info.getPricePerTicket());
                    setObj1.put("pricePerTicket",info.getPricePerTicket());
                    setObj1.put("noOfTables",0);
                    setObj1.put("pricePerTable",0);
                    setObj1.put("parsonPerTable",0);
                    jsonArray.put(setObj1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }else {
            ticketBinding.regularTicketTitleContainer.setVisibility(View.GONE);
        }
        if( selectionModal.getTicketSelectionData().getRegularTableSeating().getRegularTicketSeatingInfo() != null &&
                selectionModal.getTicketSelectionData().getRegularTableSeating().getRegularTicketSeatingInfo().size() > 0){
            ticketBinding.regularTicketTitleContainer.setVisibility(View.VISIBLE);
            int getRegularSeatingSize = selectionModal.getTicketSelectionData().getRegularTableSeating().getRegularTicketSeatingInfo().size();
            Log.d("nfklanlfknal", "regularCustomJSONObj: "+getRegularSeatingSize);
            for(int i=0;i<getRegularSeatingSize;i++){
                try {
                    RegularTicketSeatingInfo info = selectionModal.getTicketSelectionData().getRegularTableSeating().getRegularTicketSeatingInfo().get(i);
                    setObj1 = new JSONObject();
                    setObj1.put("regularTicketFlag",1);
                    setObj1.put("id",info.getId());
                    setObj1.put("ticketType",info.getTicketType());
                    setObj1.put("ticketName",info.getTicketName());
                    setObj1.put("totalQuantity",info.getTotalQuantity());
                    setObj1.put("description",info.getDescription());
                    setObj1.put("pricePerTicket",0);
                    setObj1.put("noOfTables",info.getNoOfTables());
//                    if(info.getPricePerTable() != null)
//                    getAllEventPrice = getAllEventPrice + Integer.parseInt(info.getPricePerTable());
                    setObj1.put("pricePerTable",info.getPricePerTable());
                    setObj1.put("parsonPerTable",info.getParsonPerTable());
                    Log.d("nfklanlfknal", "regularCustomJSONObj: "+getRegularSeatingSize);
                    jsonArray.put(setObj1);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("nfklanlfknal", "regularCustomJSONObj: "+e.getMessage());
                }
            }
        }
        else {
            ticketBinding.regularTicketTitleContainer.setVisibility(View.GONE);
            return;
        }

        try {
            jsonObject.put("tickets",jsonArray);
            FinalSelectTicketModal modal = new Gson().fromJson(jsonObject.toString(),FinalSelectTicketModal.class);
            setupRegularTicket(modal);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("nfklanlfknal", "regularCustomJSONObj: "+e.getMessage());
        }
    }

    private void setupVipShowTicket(FinalSelectTicketModal modal){
        tableSeatingAdapter = new VipTicketAdapter(SelectTicketActivity.this,modal.getTickets());
        ticketBinding.recyclerVipTicket.setAdapter(tableSeatingAdapter);


    }
    private void setupRegularTicket(FinalSelectTicketModal modal) {
        regularTicketAdapter = new RegularTicketAdapter(SelectTicketActivity.this, modal.getTickets());
        ticketBinding.recyclerRegularTicket.setAdapter(regularTicketAdapter);
    }

    private void userTicketBookRequest(int getEventId){
        if(selectionModal.getTicketSelectionData().getFreeTicket() != null && selectionModal.getTicketSelectionData().getFreeTicket().size() >0){

        }
        Call<JsonElement> bookTicketCall = APICall.getApiInterface().userTicketBooked(CommonUtils.getCommonUtilsInstance().getDeviceAuth(),getEventId,null);
        new APICall(SelectTicketActivity.this).apiCalling(bookTicketCall,this,APIs.USER_TICKET_BOOKED);
    }

    @Override
    public void getSelectedTicketListener(int noOfTicket, int ticketPerPrice,int indexNo) {
        //Log.d("fnlanflnaklsnfkla", noOfTicket+" getSelectedTicketListener: "+ticketPerPrice);

            if(priceList.size()>0) {
                for (int i = 0; i < priceList.size(); i++) {
                    CalculateEventPriceModal eventPriceModal = priceList.get(i);
                    if (eventPriceModal.getIndexNo() == indexNo) {
                        priceList.set(i, new CalculateEventPriceModal(noOfTicket,indexNo, ticketPerPrice));
                        Log.d("nflaknfklnal", "getSelectedTicketListener: "+i);
                        break;
                    }
                }
                priceList.add(new CalculateEventPriceModal(noOfTicket,indexNo, ticketPerPrice));
                Log.d("nflaknfklnal", "getSelectedTicketListener: "+priceList.size());
            }else
                priceList.add(new CalculateEventPriceModal(noOfTicket, indexNo,ticketPerPrice));

            Log.d("fnlanflnaklsnfkla", " --"+noOfTicket+" getSelectedTicketListener: "+ticketPerPrice*noOfTicket+" === "+indexNo);

            ticketBinding.tvShowAllEventPrice.setText("$"+ getTicketPrice());
    }

    private int getTicketPrice(){
        int allTicketPrice = 0;
        if(priceList.size()>0){
            for(int i = 0;i<priceList.size();i++){
                CalculateEventPriceModal calculateEventPriceModal = priceList.get(i);
                allTicketPrice = calculateEventPriceModal.getNoOfTicket()*calculateEventPriceModal.getPricePerTicket();
                Log.d("fnlanflkanflnal", i+" getTicketPrice: "+calculateEventPriceModal.getIndexNo()*calculateEventPriceModal.getPricePerTicket());
            }
        }
        return allTicketPrice;
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        userIsInteracting = true;

    }
}
