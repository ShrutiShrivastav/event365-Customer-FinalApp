package com.ebabu.event365live.ticketbuy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivitySelectTicketBinding;
import com.ebabu.event365live.home.activity.HomeActivity;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.listener.SelectedVipTicketListener;
import com.ebabu.event365live.ticketbuy.adapter.BuyTicketAdapter;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dagger.MapKey;
import retrofit2.Call;

public class SelectTicketActivity extends AppCompatActivity implements GetResponseData, View.OnClickListener, SelectedVipTicketListener {
    private ActivitySelectTicketBinding ticketBinding;
    private FreeTicketAdapter freeTicketAdapter;
    private RegularTicketAdapter regularTicketAdapter;
    private VipTicketAdapter tableSeatingAdapter;
    private MyLoader myLoader;
    String  eventName, eventStartTime, eventEndTime, eventDate, eventAdd;
    int eventId,hostId;
    private int getAllEventPrice;
    ArrayAdapter freeTicketSpinnerAdapter;
    private List<FinalSelectTicketModal> finalSelectTicketModals;
    private TicketSelectionModal selectionModal;
    private boolean isUserLogin;
    List<CalculateEventPriceModal> priceList = new ArrayList<>();
    boolean isFirstTimeFlag;
    public static boolean userIsInteracting;
    ArrayList<CalculateEventPriceModal> calculateEventPriceModals1;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLoader = new MyLoader(this);
        ticketBinding = DataBindingUtil.setContentView(this,R.layout.activity_select_ticket);
        ticketBinding.freeTicketTitleContainer.setOnClickListener(this);
        ticketBinding.regularTicketTitleContainer.setOnClickListener(this);
        ticketBinding.vipInfoTitleContainer.setOnClickListener(this);
        ticketBinding.vipSeatingTitleContainer.setOnClickListener(this);
        ticketBinding.regularSeatingTicketTitleContainer.setOnClickListener(this);
        calculateEventPriceModals1 = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        finalSelectTicketModals = new ArrayList<>();
        if(bundle != null) {
            eventId = bundle.getInt(Constants.ApiKeyName.eventId);
            hostId = bundle.getInt(Constants.hostId);
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

            if(typeAPI.equalsIgnoreCase(APIs.GET_EMPHEMERAL_KEY)){
                Log.d("fnalkfnskla", "GET_EMPHEMERAL_KEY: "+responseObj.toString());
                return;
            }

            if(typeAPI.equalsIgnoreCase(APIs.USER_TICKET_BOOKED)){
                launchSuccessTicketDialog();
                return;
            }
            selectionModal = new Gson().fromJson(responseObj.toString(), TicketSelectionModal.class);

            if (selectionModal.getTicketSelectionData().getVipTableSeating().getVipTicketInfo().size() > 0) {
                showVIPTicket(selectionModal.getTicketSelectionData().getVipTableSeating().getVipTicketInfo());
                Log.d("fnklanfklas", "getVipTicketInfo: "+selectionModal.getTicketSelectionData().getVipTableSeating().getVipTicketInfo().size());
            }
            if (selectionModal.getTicketSelectionData().getVipTableSeating().getVipTableSeatingInfo().size() > 0) {
                showVIPSeatingTicket(selectionModal.getTicketSelectionData().getVipTableSeating().getVipTableSeatingInfo());
                Log.d("fnklanfklas", "getVipTableSeatingInfo: "+selectionModal.getTicketSelectionData().getVipTableSeating().getVipTableSeatingInfo().size());
            }
            if (selectionModal.getTicketSelectionData().getRegularTableSeating().getRegularTicketInfo().size() > 0) {
                showRegularTicket(selectionModal.getTicketSelectionData().getRegularTableSeating().getRegularTicketInfo());
                Log.d("fnklanfklas", "getRegularTicketInfo: "+selectionModal.getTicketSelectionData().getRegularTableSeating().getRegularTicketInfo().size());
            }
            if (selectionModal.getTicketSelectionData().getRegularTableSeating().getRegularTicketSeatingInfo().size() > 0) {
                showRegularSeatingTicket(selectionModal.getTicketSelectionData().getRegularTableSeating().getRegularTicketSeatingInfo());
                Log.d("fnklanfklas", "getRegularTicketSeatingInfo: "+selectionModal.getTicketSelectionData().getRegularTableSeating().getRegularTicketSeatingInfo().size());
            }

            if(selectionModal.getTicketSelectionData().getFreeTicket() != null && selectionModal.getTicketSelectionData().getFreeTicket().size() >0){
                if(selectionModal.getTicketSelectionData().getFreeTicket().size() == 1){
                    if(selectionModal.getTicketSelectionData().getFreeTicket().get(0).getTotalQuantity() == 0){
                        ticketBinding.freeTicketTitleContainer.setVisibility(View.GONE);
                        return;
                    }
                }
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
        Log.d("fnasklfnsa", "getTicketInfoRequest: "+eventId);
            Call<JsonElement> getTicketInfoCallback = APICall.getApiInterface().getTicketInfo(CommonUtils.getCommonUtilsInstance().getDeviceAuth(),eventId);
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

            case R.id.vipInfoTitleContainer:
                if(!ticketBinding.vipInfoTicketExpand.isExpanded()) {
                    ticketBinding.vipInfoTicketExpand.expand();
                    Glide.with(SelectTicketActivity.this).load(R.drawable.ticket_select_icon).into(ticketBinding.ivVipInfoTicketIcon);
                    return;
                }
                ticketBinding.vipInfoTicketExpand.collapse();
                Glide.with(SelectTicketActivity.this).load(R.drawable.ticket_unselected_icon).into(ticketBinding.ivVipInfoTicketIcon);
                break;

            case R.id.vipSeatingTitleContainer:
                if(!ticketBinding.vipTicketExpand.isExpanded()) {
                    ticketBinding.vipTicketExpand.expand();
                    Glide.with(SelectTicketActivity.this).load(R.drawable.ticket_select_icon).into(ticketBinding.ivVipSeatingTicketIcon);
                    return;
                }
                ticketBinding.vipTicketExpand.collapse();
                Glide.with(SelectTicketActivity.this).load(R.drawable.ticket_unselected_icon).into(ticketBinding.ivVipSeatingTicketIcon);
                break;

            case R.id.regularSeatingTicketTitleContainer:
                if(!ticketBinding.regularSeatingTicketExpand.isExpanded()) {
                    ticketBinding.regularSeatingTicketExpand.expand();
                    Glide.with(SelectTicketActivity.this).load(R.drawable.ticket_select_icon).into(ticketBinding.ivRegularSeatingIcon);
                    return;
                }
                ticketBinding.regularSeatingTicketExpand.collapse();
                Glide.with(SelectTicketActivity.this).load(R.drawable.ticket_unselected_icon).into(ticketBinding.ivRegularSeatingIcon);
                break;
        }
    }

    private void setupFreeTicket(List<FreeTicket> freeTicketList) {
        freeTicketAdapter = new FreeTicketAdapter(SelectTicketActivity.this,freeTicketList);
        ticketBinding.recyclerFreeTicket.setAdapter(freeTicketAdapter);
    }



    private void setupVipShowTicket(FinalSelectTicketModal modal){
        Log.d("fsafsafsa", "setupVipShowTicket: "+modal.getTickets().size());
//        tableSeatingAdapter = new VipTicketAdapter(SelectTicketActivity.this,modal.getTickets());
//        ticketBinding.recyclerVipTicket.setAdapter(tableSeatingAdapter);

    }

    private void setupRegularTicket(FinalSelectTicketModal modal) {
//        regularTicketAdapter = new RegularTicketAdapter(SelectTicketActivity.this, modal.getTickets());
//        ticketBinding.recyclerRegularTicket.setAdapter(regularTicketAdapter);
    }

    private void userTicketBookRequest(){
        if(calculateEventPriceModals1.size() >0){
            myLoader.show("Booking...");
            Call<JsonElement> bookTicketCall = APICall.getApiInterface().userTicketBooked(CommonUtils.getCommonUtilsInstance().getDeviceAuth(),eventId,parsingTicketBookData());
            new APICall(SelectTicketActivity.this).apiCalling(bookTicketCall,this,APIs.USER_TICKET_BOOKED);
            return;
        }
        ShowToast.infoToast(SelectTicketActivity.this,getString(R.string.please_select_ticket));
        parsingTicketBookData();

    }


    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        userIsInteracting = true;
    }

    @Override
    public void getSelectedTicketListener(int ticketId, String ticketType, float ticketPrice, int ticketQty, int pricePerTable) {


        storeEventTicketDetails(ticketId,ticketType, ticketPrice,ticketQty, pricePerTable);


        Log.d("fjaklfnlas", "getSelectedTicketListener: "+ticketId+" --- "+ticketType+" --- "+ticketPrice+" -- "+ticketQty);
    }

    public void checkOutOnClick(View view) {
        //userTicketBookRequest();
        //parsingTicketBookData();
        getEphemeralKeyRequest();
    }

    private void storeEventTicketDetails(int ticketId, String ticketType, float ticketPrice, int ticketQty, int pricePerTable){
            if(calculateEventPriceModals1.size()>0){
                for(int i=0;i<calculateEventPriceModals1.size();i++){
                    if(calculateEventPriceModals1.get(i).getTicketId() == ticketId){
                        calculateEventPriceModals1.set(i,new CalculateEventPriceModal(ticketId,ticketType,ticketPrice,ticketQty,pricePerTable));
                        ticketBinding.tvShowAllEventPrice.setText("$"+calculateTicketPrice(calculateEventPriceModals1));
                        calculateTicketPrice(calculateEventPriceModals1);
                        return;
                    }
                }
                calculateEventPriceModals1.add(new CalculateEventPriceModal(ticketId,ticketType,ticketPrice,ticketQty,pricePerTable));
                ticketBinding.tvShowAllEventPrice.setText("$"+calculateTicketPrice(calculateEventPriceModals1));
                //calculateTicketPrice(calculateEventPriceModals1);

            }else {
                calculateEventPriceModals1.add(new CalculateEventPriceModal(ticketId,ticketType,ticketPrice,ticketQty,pricePerTable));
            }
    }

    private double calculateTicketPrice(ArrayList<CalculateEventPriceModal> priceList){
        int freeTicketCount = 0;
        int vipNormalTicketCount = 0,vipSeatingTicketCount = 0, regularNormalTicketCount = 0, regularSeatingTicketCount = 0;
        double totalTicketPrice = 0;
        double vipNormalTicketPrice = 0, vipSeatingTicketPrice = 0, vipTicketCountOfPrice = 0;
        double regularNormalTicketPrice = 0,regularSeatingTicketPrice = 0, regularTicketCountOfPrice = 0;


        for(int i=0;i<priceList.size();i++){
            CalculateEventPriceModal modal = priceList.get(i);
            Log.d("fnklanfa", "calculateTicketPrice: "+modal.getTicketType());

            if(modal.getTicketType().equalsIgnoreCase(getString(R.string.free_ticket))){
                freeTicketCount = freeTicketCount + modal.getTicketQty();
            }
            else if(modal.getTicketType().equalsIgnoreCase(getString(R.string.vip_normal))){
                if(modal.getTicketPrice() !=0){
                    vipNormalTicketPrice = vipNormalTicketPrice + modal.getTicketPrice();
                    vipNormalTicketCount = vipNormalTicketCount + modal.getTicketQty();
                }
                else
                    vipNormalTicketCount = vipNormalTicketCount + modal.getTicketQty();
            }

            else if(modal.getTicketType().equalsIgnoreCase(getString(R.string.vip_table_seating))){
                if(modal.getTicketPrice() !=0){
                    vipSeatingTicketPrice = vipSeatingTicketPrice + modal.getTicketPrice();
                    vipSeatingTicketCount = vipSeatingTicketCount + modal.getTicketQty();
                }else
                vipSeatingTicketCount = vipSeatingTicketCount + modal.getTicketQty();
            } else if(modal.getTicketType().equalsIgnoreCase(getString(R.string.regular_normal))){

                if(modal.getTicketPrice() !=0){
                    regularNormalTicketPrice = regularNormalTicketPrice + modal.getTicketPrice();
                    regularNormalTicketCount = regularNormalTicketCount + modal.getTicketQty();
                }else
                regularNormalTicketCount = regularNormalTicketCount + modal.getTicketQty();


            } else if(modal.getTicketType().equalsIgnoreCase(getString(R.string.regular_table_seating))){
                if(modal.getTicketPrice() !=0){
                    regularSeatingTicketPrice = regularSeatingTicketPrice + modal.getTicketPrice();
                    regularSeatingTicketCount = regularSeatingTicketCount + modal.getTicketQty();
                }else
                regularSeatingTicketCount = regularSeatingTicketCount + modal.getTicketQty();

            }
        }
        if(freeTicketCount>0){
            ticketBinding.freeTicketContainer.setVisibility(View.VISIBLE);
            ticketBinding.tvFreeTicket.setText(freeTicketCount+" "+getString(R.string.free_ticket));
        }else {
            ticketBinding.freeTicketContainer.setVisibility(View.GONE);
        }
        if(vipNormalTicketCount >0){

            totalTicketPrice = totalTicketPrice + vipNormalTicketPrice*vipNormalTicketCount;

            ticketBinding.vipTicketContainer.setVisibility(View.VISIBLE);
            ticketBinding.tvVipTicket.setText(vipNormalTicketCount+" "+getString(R.string.vip_ticket)+" $"+vipNormalTicketPrice*vipNormalTicketCount);
        }else {
            ticketBinding.vipTicketContainer.setVisibility(View.GONE);
        }
        if(vipSeatingTicketCount >0){
            totalTicketPrice = totalTicketPrice + vipSeatingTicketPrice*vipSeatingTicketCount;
            ticketBinding.vipSeatingTicketContainer.setVisibility(View.VISIBLE);
            ticketBinding.tvVipSeatingTicket.setText(vipSeatingTicketCount+" "+getString(R.string.vip_seating)+" $"+vipSeatingTicketPrice*vipSeatingTicketCount);
        }else {
            ticketBinding.vipSeatingTicketContainer.setVisibility(View.GONE);
        }

        if(regularNormalTicketCount >0){
            totalTicketPrice = totalTicketPrice + regularNormalTicketPrice*regularNormalTicketCount;
            ticketBinding.regularTicketContainer.setVisibility(View.VISIBLE);
            ticketBinding.tvRegularTicket.setText(regularNormalTicketCount+" "+getString(R.string.regular_ticket)+" $"+regularNormalTicketPrice*regularNormalTicketCount);
        }else {
            ticketBinding.regularTicketContainer.setVisibility(View.GONE);
        }
        if(regularSeatingTicketCount > 0){
            totalTicketPrice = totalTicketPrice + regularSeatingTicketPrice*regularSeatingTicketCount;
            ticketBinding.regularSeatingTicketContainer.setVisibility(View.VISIBLE);
            ticketBinding.tvRegularSeatingTicket.setText(regularSeatingTicketCount+" "+getString(R.string.regular_seating)+" $"+regularSeatingTicketPrice*regularSeatingTicketCount);
        }else {
            ticketBinding.regularSeatingTicketContainer.setVisibility(View.GONE);
        }

        return totalTicketPrice;
    }

    private JsonArray parsingTicketBookData(){
        JsonArray jsonArrayParsing =  new JsonArray();
        JsonObject jsonObject = null;
        for(int i=0;i<calculateEventPriceModals1.size();i++){
            jsonObject = new JsonObject();
            CalculateEventPriceModal modal = calculateEventPriceModals1.get(i);
            if(modal.getTicketQty() != 0){
                jsonObject.addProperty(Constants.ticketId,modal.getTicketId());
                jsonObject.addProperty(Constants.ticketTypes,modal.getTicketType());
                jsonObject.addProperty(Constants.totalQuantity,modal.getTicketQty());
                jsonObject.addProperty(Constants.parsonPerTable,modal.getPersonPerTable());
                jsonObject.addProperty(Constants.pricePerTicket,modal.getTicketPrice());
                jsonObject.addProperty(Constants.createdBy,hostId);
                jsonArrayParsing.add(jsonObject);
            }
        }
        Log.d("fsnaklfnkasl", "parsingTicketBookData: "+jsonArrayParsing);
        return jsonArrayParsing;
    }

    private void launchSuccessTicketDialog(){
        View dialogView = LayoutInflater.from(this).inflate(R.layout.sent_ticket_dialog,null,false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();

        ((TextView)dialogView.findViewById(R.id.btnOk)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alertDialog.isShowing()) {
                    alertDialog.dismiss();
                    navigateToHome();
                }
            }
        });

        alertDialog.show();
    }
    private void navigateToHome(){
        Intent homeIntent =  new Intent(SelectTicketActivity.this, HomeActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
        finish();
    }
    private void showVIPTicket(List<VipTicketInfo> vipTicketInfoList){
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject setObj1 = null;
        for(int i=0;i<vipTicketInfoList.size();i++){
            ;
            setObj1 = new JSONObject();
            try {
                setObj1.put("isSeatingTable",false);
                setObj1.put("id",vipTicketInfoList.get(i).getId());
                setObj1.put("ticketType",vipTicketInfoList.get(i).getTicketType());
                setObj1.put("ticketName",vipTicketInfoList.get(i).getTicketName());
                setObj1.put("totalQuantity",vipTicketInfoList.get(i).getTotalQuantity());
                setObj1.put("description",vipTicketInfoList.get(i).getDescription());
                setObj1.put("pricePerTicket",vipTicketInfoList.get(i).getPricePerTicket());
                setObj1.put("parsonPerTable",0);
                setObj1.put("noOfTables",0);
                setObj1.put("discountedPrice",0);
                setObj1.put("disPercentage",0);

                jsonArray.put(setObj1);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("fnaslkfnsklanfa", "JSONException: "+e.getMessage());
            }
        }

        try {
            jsonObject.put("tickets",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("fnaslkfnsklanfa", "jsonObject: "+e.getMessage());
        }

        Log.d("fnalfnlkanksfa", "showVIPTicket: "+jsonObject.toString());

        FinalSelectTicketModal finalSelectTicketModal = new Gson().fromJson(jsonObject.toString(),FinalSelectTicketModal.class);
        BuyTicketAdapter buyTicketAdapter = new BuyTicketAdapter(SelectTicketActivity.this,finalSelectTicketModal.getTickets());
        ticketBinding.recyclerInfoVipTicket.setAdapter(buyTicketAdapter);
    }
    private void showVIPSeatingTicket(List<VipTableSeatingInfo> vipTableSeatingInfos){
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject setObj1 = null;
        for(int i=0;i<vipTableSeatingInfos.size();i++){
            ;
            setObj1 = new JSONObject();
            try {
                setObj1.put("isSeatingTable",true);
                setObj1.put("id",vipTableSeatingInfos.get(i).getId());
                setObj1.put("ticketType",vipTableSeatingInfos.get(i).getTicketType());
                setObj1.put("ticketName",vipTableSeatingInfos.get(i).getTicketName());
                setObj1.put("totalQuantity",vipTableSeatingInfos.get(i).getTotalQuantity());
                setObj1.put("description",vipTableSeatingInfos.get(i).getDescription());
                setObj1.put("pricePerTicket",vipTableSeatingInfos.get(i).getPricePerTicket());
                setObj1.put("parsonPerTable",vipTableSeatingInfos.get(i).getParsonPerTable());
                setObj1.put("noOfTables",vipTableSeatingInfos.get(i).getNoOfTables());
                setObj1.put("discountedPrice",vipTableSeatingInfos.get(i).getDiscountedPrice());
                setObj1.put("disPercentage",vipTableSeatingInfos.get(i).getDisPercentage());
                jsonArray.put(setObj1);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("fnaslkfnsklanfa", "JSONException: "+e.getMessage());
            }
        }

        try {
            jsonObject.put("tickets",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("fnaslkfnsklanfa", "jsonObject: "+e.getMessage());
        }

        FinalSelectTicketModal finalSelectTicketModal = new Gson().fromJson(jsonObject.toString(),FinalSelectTicketModal.class);
        BuyTicketAdapter buyTicketAdapter = new BuyTicketAdapter(SelectTicketActivity.this,finalSelectTicketModal.getTickets());
        ticketBinding.recyclerVipTicket.setAdapter(buyTicketAdapter);

    }

    private void showRegularTicket(List<RegularTicketInfo> regularTicketInfos){

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject setObj1 = null;
        for(int i=0;i<regularTicketInfos.size();i++){
            ;
            setObj1 = new JSONObject();
            try {
                setObj1.put("isSeatingTable",false);
                setObj1.put("id",regularTicketInfos.get(i).getId());
                setObj1.put("ticketType",regularTicketInfos.get(i).getTicketType());
                setObj1.put("ticketName",regularTicketInfos.get(i).getTicketName());
                setObj1.put("totalQuantity",regularTicketInfos.get(i).getTotalQuantity());
                setObj1.put("description",regularTicketInfos.get(i).getDescription());
                setObj1.put("pricePerTicket",regularTicketInfos.get(i).getPricePerTicket());
                setObj1.put("parsonPerTable",0);
                setObj1.put("noOfTables",0);
                setObj1.put("discountedPrice",0);
                setObj1.put("disPercentage",0);


                jsonArray.put(setObj1);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("fnaslkfnsklanfa", "JSONException: "+e.getMessage());
            }
        }

        try {
            jsonObject.put("tickets",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("fnaslkfnsklanfa", "jsonObject: "+e.getMessage());
        }

        FinalSelectTicketModal finalSelectTicketModal = new Gson().fromJson(jsonObject.toString(),FinalSelectTicketModal.class);

        Log.d("afnlkasnfl", "showRegularTicket: "+finalSelectTicketModal.getTickets().size());
        BuyTicketAdapter buyTicketAdapter = new BuyTicketAdapter(SelectTicketActivity.this,finalSelectTicketModal.getTickets());

        ticketBinding.recyclerRegularTicket.setAdapter(buyTicketAdapter);
    }
    private void showRegularSeatingTicket(List<RegularTicketSeatingInfo> regularTicketSeatingInfos){

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject setObj1 = null;
        for(int i=0;i<regularTicketSeatingInfos.size();i++){
            setObj1 = new JSONObject();
            try {
                setObj1.put("isSeatingTable",true);
                setObj1.put("id",regularTicketSeatingInfos.get(i).getId());
                setObj1.put("ticketType",regularTicketSeatingInfos.get(i).getTicketType());
                setObj1.put("ticketName",regularTicketSeatingInfos.get(i).getTicketName());
                setObj1.put("totalQuantity",regularTicketSeatingInfos.get(i).getTotalQuantity());
                setObj1.put("description",regularTicketSeatingInfos.get(i).getDescription());
                setObj1.put("pricePerTicket",regularTicketSeatingInfos.get(i).getPricePerTicket());
                setObj1.put("parsonPerTable",regularTicketSeatingInfos.get(i).getParsonPerTable());
                setObj1.put("noOfTables",regularTicketSeatingInfos.get(i).getNoOfTables());
                setObj1.put("discountedPrice",regularTicketSeatingInfos.get(i).getDiscountedPrice());
                setObj1.put("disPercentage",regularTicketSeatingInfos.get(i).getDisPercentage());
                jsonArray.put(setObj1);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("fnaslkfnsklanfa", "JSONException: "+e.getMessage());
            }
        }
        try {
            jsonObject.put("tickets",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("fnaslkfnsklanfa", "jsonObject: "+e.getMessage());
        }
        FinalSelectTicketModal finalSelectTicketModal = new Gson().fromJson(jsonObject.toString(),FinalSelectTicketModal.class);
        BuyTicketAdapter buyTicketAdapter = new BuyTicketAdapter(SelectTicketActivity.this,finalSelectTicketModal.getTickets());
        ticketBinding.recyclerRegularSeatingTicket.setAdapter(buyTicketAdapter);
    }

    private void getEphemeralKeyRequest(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("api_version",Constants.api_version);
        jsonObject.addProperty("customer",CommonUtils.getCommonUtilsInstance().getStripeCustomerId());


        Call<JsonElement> key = APICall.getApiInterface().getEphemeralKey(jsonObject);
        new APICall(SelectTicketActivity.this).apiCalling(key,this,APIs.GET_EMPHEMERAL_KEY);
    }
}
