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
import java.util.List;

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
        ticketBinding.vipTitleContainer.setOnClickListener(this);
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

            if(typeAPI.equalsIgnoreCase(APIs.USER_TICKET_BOOKED)){
                launchSuccessTicketDialog();
                return;
            }

            selectionModal = new Gson().fromJson(responseObj.toString(), TicketSelectionModal.class);
            vipCustomJSONObj(selectionModal);
            regularCustomJSONObj(selectionModal);
            if(selectionModal.getTicketSelectionData().getFreeTicket() != null && selectionModal.getTicketSelectionData().getFreeTicket().size() >0){
                //TODO implement Free Ticket view
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
           /*if list size may be only one and it has zero quantity, it should be not visible to user*/
//            if(selectionModal.getTicketSelectionData().getVipTableSeating().getVipTicketInfo().size() == 1){
//                ticketBinding.vipTitleContainer.setVisibility(View.GONE);
//                return;
//            }
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
        Log.d("fnlaskfklsa", "vipTitleContainer: "+jsonArray);
        if( selectionModal.getTicketSelectionData().getVipTableSeating().getVipTableSeatingInfo() != null &&
                selectionModal.getTicketSelectionData().getVipTableSeating().getVipTableSeatingInfo().size() > 0){
//            if(selectionModal.getTicketSelectionData().getVipTableSeating().getVipTableSeatingInfo().size() == 1){
//                ticketBinding.vipTitleContainer.setVisibility(View.GONE);
//                return;
//            }
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
        }
        try {
            jsonObject.put("tickets",jsonArray);

            Log.d("fnlaskfklsa", "vipCustomJSONObj: "+jsonObject);
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
//            if(selectionModal.getTicketSelectionData().getRegularTableSeating().getRegularTicketInfo().size() == 1){
//                ticketBinding.regularTicketTitleContainer.setVisibility(View.GONE);
//                return;
//            }
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
//            if(selectionModal.getTicketSelectionData().getRegularTableSeating().getRegularTicketSeatingInfo().size() == 1){
//                ticketBinding.regularTicketTitleContainer.setVisibility(View.GONE);
//                return;
//            }
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
         //   return;
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
        Log.d("fsafsafsa", "setupVipShowTicket: "+modal.getTickets().size());
        tableSeatingAdapter = new VipTicketAdapter(SelectTicketActivity.this,modal.getTickets());
        ticketBinding.recyclerVipTicket.setAdapter(tableSeatingAdapter);

    }

    private void setupRegularTicket(FinalSelectTicketModal modal) {
        regularTicketAdapter = new RegularTicketAdapter(SelectTicketActivity.this, modal.getTickets());
        ticketBinding.recyclerRegularTicket.setAdapter(regularTicketAdapter);
    }

    private void userTicketBookRequest(){
        if(calculateEventPriceModals1.size() >0){
            myLoader.show("Booking...");
            Call<JsonElement> bookTicketCall = APICall.getApiInterface().userTicketBooked(CommonUtils.getCommonUtilsInstance().getDeviceAuth(),eventId,parsingTicketBookData());
            new APICall(SelectTicketActivity.this).apiCalling(bookTicketCall,this,APIs.USER_TICKET_BOOKED);
            return;
        }
        ShowToast.infoToast(SelectTicketActivity.this,getString(R.string.please_select_ticket));

    }


    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        userIsInteracting = true;
    }

    @Override
    public void getSelectedTicketListener(int ticketId, String ticketType, int ticketPrice, int ticketQty) {
        storeEventTicketDetails(ticketId,ticketType, ticketPrice,ticketQty);
        Log.d("fjaklfnlas", "getSelectedTicketListener: "+ticketId+" --- "+ticketType+" --- "+ticketPrice+" -- "+ticketQty);
    }

    public void checkOutOnClick(View view) {
        userTicketBookRequest();
    }

    private void storeEventTicketDetails(int ticketId, String ticketType, int ticketPrice, int ticketQty){
            if(calculateEventPriceModals1.size()>0){
                for(int i=0;i<calculateEventPriceModals1.size();i++){
                    if(calculateEventPriceModals1.get(i).getTicketId() == ticketId){
                        calculateEventPriceModals1.set(i,new CalculateEventPriceModal(ticketId,ticketType,ticketPrice,ticketQty));
                        ticketBinding.tvShowAllEventPrice.setText("$"+calculateTicketPrice(calculateEventPriceModals1));
                        calculateTicketPrice(calculateEventPriceModals1);
                        return;
                    }
                }
                calculateEventPriceModals1.add(new CalculateEventPriceModal(ticketId,ticketType,ticketPrice,ticketQty));
                ticketBinding.tvShowAllEventPrice.setText("$"+calculateTicketPrice(calculateEventPriceModals1));
                //calculateTicketPrice(calculateEventPriceModals1);

            }else {
                calculateEventPriceModals1.add(new CalculateEventPriceModal(ticketId,ticketType,ticketPrice,ticketQty));
            }
    }

    private int calculateTicketPrice(ArrayList<CalculateEventPriceModal> priceList){
        int freeTicketCount = 0;
        int vipNormalTicketCount = 0, regularNormalTicketCount = 0;
        int totalTicketPrice = 0;
        int vipNormalTicketPrice = 0, vipTicketCountOfPrice = 0;
        int regularNormalTicketPrice = 0, regularTicketCountOfPrice = 0;


        for(int i=0;i<priceList.size();i++){
            CalculateEventPriceModal modal = priceList.get(i);
            Log.d("fnklanfa", "calculateTicketPrice: "+modal.getTicketType());

            if(modal.getTicketType().equalsIgnoreCase(getString(R.string.free_ticket))){
                freeTicketCount = freeTicketCount + modal.getTicketQty();
            }
            else if(modal.getTicketType().equalsIgnoreCase(getString(R.string.vip_normal))){
                if(modal.getTicketPrice() !=0){
                    vipNormalTicketPrice = modal.getTicketPrice();
                    vipTicketCountOfPrice = vipTicketCountOfPrice + modal.getTicketQty();
                }
                vipNormalTicketCount = vipNormalTicketCount + modal.getTicketQty();
            }

            else if(modal.getTicketType().equalsIgnoreCase(getString(R.string.vip_table_seating))){
                if(modal.getTicketPrice() !=0){
                    vipNormalTicketPrice = vipNormalTicketPrice + modal.getTicketPrice();
                    vipTicketCountOfPrice = vipTicketCountOfPrice + modal.getTicketQty();
                }
                vipNormalTicketCount = vipNormalTicketCount + modal.getTicketQty();
            } else if(modal.getTicketType().equalsIgnoreCase(getString(R.string.regular_normal))){

                if(modal.getTicketPrice() !=0){
                    regularNormalTicketPrice = regularNormalTicketPrice + modal.getTicketPrice();
                    regularTicketCountOfPrice = regularTicketCountOfPrice + modal.getTicketQty();
                }
                regularNormalTicketCount = regularNormalTicketCount + modal.getTicketQty();


            } else if(modal.getTicketType().equalsIgnoreCase(getString(R.string.regular_table_seating))){
                if(modal.getTicketPrice() !=0){
                    regularNormalTicketPrice = regularNormalTicketPrice + modal.getTicketPrice();
                    regularTicketCountOfPrice = regularTicketCountOfPrice + modal.getTicketQty();
                }
                regularNormalTicketCount = regularNormalTicketCount + modal.getTicketQty();

            }
        }


        if(freeTicketCount>0){
            ticketBinding.freeTicketContainer.setVisibility(View.VISIBLE);
            ticketBinding.tvFreeTicket.setText(freeTicketCount+" "+getString(R.string.free_ticket));
        }else {
            ticketBinding.freeTicketContainer.setVisibility(View.GONE);
        }
        if(vipNormalTicketCount >0){

            totalTicketPrice = totalTicketPrice + vipNormalTicketPrice*vipTicketCountOfPrice;

            ticketBinding.vipTicketContainer.setVisibility(View.VISIBLE);
            ticketBinding.tvVipTicket.setText(vipNormalTicketCount+" "+getString(R.string.vip_ticket)+" $"+totalTicketPrice);
        }else {
            ticketBinding.vipTicketContainer.setVisibility(View.GONE);
        }
        if(regularNormalTicketCount >0){
            totalTicketPrice = totalTicketPrice + regularNormalTicketPrice*regularTicketCountOfPrice;
            ticketBinding.regularTicketContainer.setVisibility(View.VISIBLE);
            ticketBinding.tvRegularTicket.setText(regularNormalTicketCount+" "+getString(R.string.vip_ticket)+" $"+regularNormalTicketPrice*regularTicketCountOfPrice);
        }else {
            ticketBinding.regularTicketContainer.setVisibility(View.GONE);
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
                jsonObject.addProperty(Constants.pricePerTicket,modal.getTicketPrice());
                jsonObject.addProperty(Constants.createdBy,hostId);
                jsonArrayParsing.add(jsonObject);
            }
        }
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
                if(alertDialog.isShowing()){
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
}
