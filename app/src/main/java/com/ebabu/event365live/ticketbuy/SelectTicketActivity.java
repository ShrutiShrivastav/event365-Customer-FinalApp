package com.ebabu.event365live.ticketbuy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivitySelectTicketBinding;
import com.ebabu.event365live.home.activity.HomeActivity;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.listener.SelectedVipTicketListener;
import com.ebabu.event365live.stripe.GetEphemeralKey;
import com.ebabu.event365live.stripe.StripeConnect;
import com.ebabu.event365live.stripe.StripePaymentSessionListener;
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
import com.ebabu.event365live.utils.SessionValidation;
import com.ebabu.event365live.utils.ShowToast;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.stripe.android.CustomerSession;
import com.stripe.android.EphemeralKeyUpdateListener;
import com.stripe.android.PaymentSession;
import com.stripe.android.PaymentSessionConfig;
import com.stripe.android.Stripe;
import com.stripe.android.StripeError;
import com.stripe.android.model.Address;
import com.stripe.android.model.Customer;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.ShippingInformation;
import com.stripe.android.view.BillingAddressFields;
import com.stripe.android.view.PaymentMethodsActivityStarter;
import com.stripe.android.view.ShippingInfoWidget;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

public class SelectTicketActivity extends AppCompatActivity implements GetResponseData, View.OnClickListener, SelectedVipTicketListener {

    private ActivitySelectTicketBinding ticketBinding;
    private FreeTicketAdapter freeTicketAdapter;
    private RegularTicketAdapter regularTicketAdapter;
    private VipTicketAdapter tableSeatingAdapter;
    private MyLoader myLoader;
    String eventName, eventStartTime, eventEndTime, eventDate, eventAdd;
    int eventId, hostId;
    private int getAllEventPrice;
    ArrayAdapter freeTicketSpinnerAdapter;
    private List<FinalSelectTicketModal> finalSelectTicketModals;
    private TicketSelectionModal selectionModal;
    private boolean isUserLogin;
    boolean isFirstTimeFlag;
    public static boolean userIsInteracting;
    ArrayList<CalculateEventPriceModal> calculateEventPriceModals1;
    private AlertDialog alertDialog;
    private Customer customer;
    private boolean isCardActive;
    EphemeralKeyUpdateListener keyUpdateListener;
    private Stripe mStripe;
    private PaymentSession paymentSession;
    private BuyTicketAdapter vipNormalAdapter, vipSeatingAdapter, regularNormalAdapter, regularSeatingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLoader = new MyLoader(this);
        ticketBinding = DataBindingUtil.setContentView(this, R.layout.activity_select_ticket);
        ticketBinding.freeTicketTitleContainer.setOnClickListener(this);
        ticketBinding.regularTicketTitleContainer.setOnClickListener(this);
        ticketBinding.vipInfoTitleContainer.setOnClickListener(this);
        ticketBinding.vipSeatingTitleContainer.setOnClickListener(this);
        ticketBinding.regularSeatingTicketTitleContainer.setOnClickListener(this);
        calculateEventPriceModals1 = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        finalSelectTicketModals = new ArrayList<>();

        if (bundle != null) {
            eventId = bundle.getInt(Constants.ApiKeyName.eventId);
            hostId = bundle.getInt(Constants.hostId);
            eventName = bundle.getString(Constants.eventName);
            eventStartTime = bundle.getString(Constants.eventStartTime);
            eventEndTime = bundle.getString(Constants.eventEndTime);
            eventDate = bundle.getString(Constants.eventDate);
            eventAdd = bundle.getString(Constants.eventAdd);
            ticketBinding.tvShowEventName.setText(eventName);
            ticketBinding.tvShowEventTime.setText(CommonUtils.getCommonUtilsInstance().getStartEndEventTime(eventStartTime) + " - " + CommonUtils.getCommonUtilsInstance().getStartEndEventTime(eventEndTime));
            ticketBinding.tvShowEventDate.setText(CommonUtils.getCommonUtilsInstance().getDateMonthYearName(eventDate, true));
            ticketBinding.tvShowEventAdd.setText(eventAdd);
            mStripe = StripeConnect.paymentAuth(this);
            createStripeSession();
            paymentSession =
                    new PaymentSession(
                            this, createPaymentSessionConfig());
            paymentSession.init(new StripePaymentSessionListener());
            getTicketInfoRequest();
        }

    }

    public void backBtnOnClick(View view) {
        finish();
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        if (responseObj != null){

            if (typeAPI.equalsIgnoreCase(APIs.GET_EPHEMERAL_KEY)) {
                Log.d("fnalkfnskla", "GET_EPHEMERAL_KEY: " + responseObj.toString());
                keyUpdateListener.onKeyUpdate(responseObj.toString());
                return;
            }
            if (typeAPI.equalsIgnoreCase(APIs.USER_TICKET_BOOKED)) {
                launchSuccessTicketDialog();
                return;
            }
            selectionModal = new Gson().fromJson(responseObj.toString(), TicketSelectionModal.class);

            if (selectionModal.getTicketSelectionData().getVipTableSeating().getVipTicketInfo().size() > 0) {
                showVIPTicket(selectionModal.getTicketSelectionData().getVipTableSeating().getVipTicketInfo());
                Log.d("fnklanfklas", "getVipTicketInfo: " + selectionModal.getTicketSelectionData().getVipTableSeating().getVipTicketInfo().size());
            }
            if (selectionModal.getTicketSelectionData().getVipTableSeating().getVipTableSeatingInfo().size() > 0) {
                showVIPSeatingTicket(selectionModal.getTicketSelectionData().getVipTableSeating().getVipTableSeatingInfo());
                Log.d("fnklanfklas", "getVipTableSeatingInfo: " + selectionModal.getTicketSelectionData().getVipTableSeating().getVipTableSeatingInfo().size());
            }
            if (selectionModal.getTicketSelectionData().getRegularTableSeating().getRegularTicketInfo().size() > 0) {
                showRegularTicket(selectionModal.getTicketSelectionData().getRegularTableSeating().getRegularTicketInfo());
                Log.d("fnklanfklas", "getRegularTicketInfo: " + selectionModal.getTicketSelectionData().getRegularTableSeating().getRegularTicketInfo().size());
            }
            if (selectionModal.getTicketSelectionData().getRegularTableSeating().getRegularTicketSeatingInfo().size() > 0) {
                showRegularSeatingTicket(selectionModal.getTicketSelectionData().getRegularTableSeating().getRegularTicketSeatingInfo());
                Log.d("fnklanfklas", "getRegularTicketSeatingInfo: " + selectionModal.getTicketSelectionData().getRegularTableSeating().getRegularTicketSeatingInfo().size());
            }

            if (selectionModal.getTicketSelectionData().getFreeTicket() != null && selectionModal.getTicketSelectionData().getFreeTicket().size() > 0) {
                if (selectionModal.getTicketSelectionData().getFreeTicket().size() == 1) {
                    if (selectionModal.getTicketSelectionData().getFreeTicket().get(0).getTotalQuantity() == 0) {
                        ticketBinding.freeTicketTitleContainer.setVisibility(View.GONE);
                        return;
                    }
                }
                setupFreeTicket(selectionModal.getTicketSelectionData().getFreeTicket());
            } else {
                ticketBinding.freeTicketTitleContainer.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        ShowToast.errorToast(SelectTicketActivity.this, message);
        Log.d("nflkanflknlan", "onFailed: ");
    }

    private void getTicketInfoRequest() {
        myLoader.show("");
        Log.d("fnasklfnsa", "getTicketInfoRequest: " + eventId);
        Call<JsonElement> getTicketInfoCallback = APICall.getApiInterface().getTicketInfo(CommonUtils.getCommonUtilsInstance().getDeviceAuth(), eventId);
        new APICall(SelectTicketActivity.this).apiCalling(getTicketInfoCallback, this, APIs.GET_TICKET_INFO);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.freeTicketTitleContainer: {
                if (!ticketBinding.freeTicketExpand.isExpanded()) {
                    ticketBinding.freeTicketExpand.expand();
                    Glide.with(SelectTicketActivity.this).load(R.drawable.ticket_select_icon).into(ticketBinding.ivFreeTicketHeaderIcon);
                    return;
                }
                ticketBinding.freeTicketExpand.collapse();
                Glide.with(SelectTicketActivity.this).load(R.drawable.ticket_unselected_icon).into(ticketBinding.ivFreeTicketHeaderIcon);
                break;
            }
            case R.id.regularTicketTitleContainer:
                if (!ticketBinding.regularTicketExpand.isExpanded()) {
                    ticketBinding.regularTicketExpand.expand();
                    Glide.with(SelectTicketActivity.this).load(R.drawable.ticket_select_icon).into(ticketBinding.ivRegularIcon);
                    return;
                }
                ticketBinding.regularTicketExpand.collapse();
                Glide.with(SelectTicketActivity.this).load(R.drawable.ticket_unselected_icon).into(ticketBinding.ivRegularIcon);
                break;

            case R.id.vipInfoTitleContainer:
                if (!ticketBinding.vipInfoTicketExpand.isExpanded()) {
                    ticketBinding.vipInfoTicketExpand.expand();
                    Glide.with(SelectTicketActivity.this).load(R.drawable.ticket_select_icon).into(ticketBinding.ivVipInfoTicketIcon);
                    return;
                }
                ticketBinding.vipInfoTicketExpand.collapse();
                Glide.with(SelectTicketActivity.this).load(R.drawable.ticket_unselected_icon).into(ticketBinding.ivVipInfoTicketIcon);
                break;

            case R.id.vipSeatingTitleContainer:
                if (!ticketBinding.vipTicketExpand.isExpanded()) {
                    ticketBinding.vipTicketExpand.expand();
                    Glide.with(SelectTicketActivity.this).load(R.drawable.ticket_select_icon).into(ticketBinding.ivVipSeatingTicketIcon);
                    return;
                }
                ticketBinding.vipTicketExpand.collapse();
                Glide.with(SelectTicketActivity.this).load(R.drawable.ticket_unselected_icon).into(ticketBinding.ivVipSeatingTicketIcon);
                break;

            case R.id.regularSeatingTicketTitleContainer:
                if (!ticketBinding.regularSeatingTicketExpand.isExpanded()) {
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
        freeTicketAdapter = new FreeTicketAdapter(SelectTicketActivity.this, freeTicketList);
        ticketBinding.recyclerFreeTicket.setAdapter(freeTicketAdapter);
    }

    private void setupVipShowTicket(FinalSelectTicketModal modal) {
        Log.d("fsafsafsa", "setupVipShowTicket: " + modal.getTickets().size());
//        tableSeatingAdapter = new VipTicketAdapter(SelectTicketActivity.this,modal.getTickets());
//        ticketBinding.recyclerVipTicket.setAdapter(tableSeatingAdapter);

    }

    private void setupRegularTicket(FinalSelectTicketModal modal) {
//        regularTicketAdapter = new RegularTicketAdapter(SelectTicketActivity.this, modal.getTickets());
//        ticketBinding.recyclerRegularTicket.setAdapter(regularTicketAdapter);
    }

    private void userTicketBookRequest() {
        if (calculateEventPriceModals1.size() > 0) {
            myLoader.show("Booking...");
            Call<JsonElement> bookTicketCall = APICall.getApiInterface().userTicketBooked(CommonUtils.getCommonUtilsInstance().getDeviceAuth(), eventId, parsingTicketBookData());
            new APICall(SelectTicketActivity.this).apiCalling(bookTicketCall, this, APIs.USER_TICKET_BOOKED);
            return;
        }
        ShowToast.infoToast(SelectTicketActivity.this, getString(R.string.please_select_ticket));
        parsingTicketBookData();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        userIsInteracting = true;
    }

    @Override
    public void getSelectedTicketListener(List<FinalSelectTicketModal.Ticket> ticketList, int itemPosition, int itemSelectedNumber) {

        storeEventTicketDetails(ticketList, itemPosition, itemSelectedNumber);
        Log.d("fjaklfnlas", "getSelectedTicketListener: " + ticketList.get(itemPosition).getTicketId() + " --- " + ticketList.get(itemPosition).getTicketType() + " --- " + ticketList.get(itemPosition).getPricePerTicket());
    }

    public void checkOutOnClick(View view) {
        //userTicketBookRequest();
        if (paymentSession.init(new StripePaymentSessionListener())) {
        }
        launchPaymentMethodsActivity();
    }
    private void storeEventTicketDetails(List<FinalSelectTicketModal.Ticket> ticketList, int itemPosition, int itemSelectedNumber) {

        FinalSelectTicketModal.Ticket ticketData = ticketList.get(itemPosition);

        if (calculateEventPriceModals1.size() > 0) {
            for (int i = 0; i < calculateEventPriceModals1.size(); i++) {
                if (calculateEventPriceModals1.get(i).getTicketId() == ticketData.getTicketId()) {
                    //NumberFormat.getNumberInstance(Locale.US).format(calculateTicketPrice(ticketList, itemPosition, itemSelectedNumber)).toString();
                    calculateEventPriceModals1.set(i, new CalculateEventPriceModal(ticketData.getTicketId(), ticketData.getTicketType(), ticketData.getPricePerTicket(), ticketData.getTotalQuantity(), ticketData.getPricePerTable(),itemSelectedNumber,ticketData.getDiscountedPrice(),getTotalPrice(ticketData,itemSelectedNumber)));
                    ticketBinding.tvShowAllEventPrice.setText("$" + NumberFormat.getNumberInstance(Locale.US).format(calculateTicketPrice(ticketList, itemPosition, itemSelectedNumber)).toString());
                    return;
                }
            }

            calculateEventPriceModals1.add(new CalculateEventPriceModal(ticketData.getTicketId(), ticketData.getTicketType(), ticketData.getPricePerTicket(), ticketData.getTotalQuantity(), ticketData.getPricePerTable(),itemSelectedNumber,ticketData.getDiscountedPrice(),getTotalPrice(ticketData,itemSelectedNumber)));
            NumberFormat.getNumberInstance(Locale.US).format(calculateTicketPrice(ticketList, itemPosition, itemSelectedNumber)).toString();
        } else {

            calculateEventPriceModals1.add(new CalculateEventPriceModal(ticketData.getTicketId(), ticketData.getTicketType(), ticketData.getPricePerTicket(), ticketData.getTotalQuantity(), ticketData.getPricePerTable(),itemSelectedNumber,ticketData.getDiscountedPrice(),getTotalPrice(ticketData,itemSelectedNumber)));
            ticketBinding.tvShowAllEventPrice.setText("$" + NumberFormat.getNumberInstance(Locale.US).format(calculateTicketPrice(ticketList, itemPosition, itemSelectedNumber)).toString());
        }
    }

    private double calculateTicketPrice(List<FinalSelectTicketModal.Ticket> ticketList, int getItemPosition, int getSelectedItemNumber) {
        double totalPrice = 0;

        int freeTicketCount = 0, vipNormalTicketCount = 0, vipSeatingTicketCount = 0, regularNormalTicketCount = 0, regularSeatingTicketCount = 0;
        float vipNormalTicketPrice = 0,vipSeatingTicketPrice = 0, regularNormalTicketPrice = 0,regularSeatingTicketPrice = 0;

        for(int i=0;i<calculateEventPriceModals1.size();i++){
            CalculateEventPriceModal priceModal = calculateEventPriceModals1.get(i);

            totalPrice = totalPrice + priceModal.getTotalPrice();

            if (priceModal.getTicketType().equalsIgnoreCase(getString(R.string.free_ticket))){

                freeTicketCount = freeTicketCount + priceModal.getItemSelectedQty();

            } else if (priceModal.getTicketType().equalsIgnoreCase(getString(R.string.vip_normal))) {

                if(priceModal.getItemSelectedQty()>0){
                    vipNormalTicketCount = vipNormalTicketCount + priceModal.getItemSelectedQty();
                    vipNormalTicketPrice =  vipNormalTicketPrice + priceModal.getTicketPrice()*priceModal.getItemSelectedQty();

                    Log.d("fasfsafa", priceModal.getTicketPrice()+" calculateTicketPrice: "+priceModal.getItemSelectedQty());
                }


            } else if (priceModal.getTicketType().equalsIgnoreCase(getString(R.string.vip_table_seating))) {
                vipSeatingTicketCount = vipSeatingTicketCount + priceModal.getItemSelectedQty();

                vipSeatingTicketPrice =  vipSeatingTicketPrice + priceModal.getDiscountedPrice()*priceModal.getItemSelectedQty();

            } else if (priceModal.getTicketType().equalsIgnoreCase(getString(R.string.regular_normal))) {
                regularNormalTicketCount = regularNormalTicketCount + priceModal.getItemSelectedQty();

                regularNormalTicketPrice =  regularNormalTicketPrice + priceModal.getTicketPrice()*priceModal.getItemSelectedQty();

            } else if (priceModal.getTicketType().equalsIgnoreCase(getString(R.string.regular_table_seating))) {
                regularSeatingTicketCount = regularSeatingTicketCount + priceModal.getItemSelectedQty();

                regularSeatingTicketPrice =  regularSeatingTicketPrice + priceModal.getDiscountedPrice()*priceModal.getItemSelectedQty();
            }
        }

        if (freeTicketCount > 0) {
            ticketBinding.freeTicketContainer.setVisibility(View.VISIBLE);
            ticketBinding.tvFreeTicket.setText(freeTicketCount+"".concat(freeTicketCount>1 ? "Free Ticket" : "Free Tickets"));
        } else {
            ticketBinding.freeTicketContainer.setVisibility(View.GONE);
        }
        if (vipNormalTicketCount > 0) {

            ticketBinding.vipTicketContainer.setVisibility(View.VISIBLE);
            ticketBinding.tvVipTicket.setText(vipNormalTicketCount + " " + getString(R.string.vip_ticket) + " $" + vipNormalTicketPrice);
        } else {
            ticketBinding.vipTicketContainer.setVisibility(View.GONE);
        }
        if (vipSeatingTicketCount > 0) {

            ticketBinding.vipSeatingTicketContainer.setVisibility(View.VISIBLE);
            ticketBinding.tvVipSeatingTicket.setText(vipSeatingTicketCount + " " + getString(R.string.vip_seating) + " $" + vipSeatingTicketPrice);
        } else {
            ticketBinding.vipSeatingTicketContainer.setVisibility(View.GONE);
        }

        if (regularNormalTicketCount > 0) {

            ticketBinding.regularTicketContainer.setVisibility(View.VISIBLE);
            ticketBinding.tvRegularTicket.setText(regularNormalTicketCount + " " + getString(R.string.regular_ticket) + " $" + regularNormalTicketPrice);
        } else {
            ticketBinding.regularTicketContainer.setVisibility(View.GONE);
        }
        if (regularSeatingTicketCount > 0) {

            ticketBinding.regularSeatingTicketContainer.setVisibility(View.VISIBLE);
            ticketBinding.tvRegularSeatingTicket.setText(regularSeatingTicketCount + " " + getString(R.string.regular_seating) + " $" + regularSeatingTicketPrice);
        } else {
            ticketBinding.regularSeatingTicketContainer.setVisibility(View.GONE);
        }



        return totalPrice;
    }

    private JsonArray parsingTicketBookData() {
        JsonArray jsonArrayParsing = new JsonArray();
        JsonObject jsonObject = null;
        for (int i = 0; i < calculateEventPriceModals1.size(); i++) {
            jsonObject = new JsonObject();
            CalculateEventPriceModal modal = calculateEventPriceModals1.get(i);
            if (modal.getTicketQty() != 0) {
                jsonObject.addProperty(Constants.ticketId, modal.getTicketId());
                jsonObject.addProperty(Constants.ticketTypes, modal.getTicketType());
                jsonObject.addProperty(Constants.totalQuantity, modal.getTicketQty());
                jsonObject.addProperty(Constants.parsonPerTable, modal.getPersonPerTable());
                jsonObject.addProperty(Constants.pricePerTicket, modal.getTicketPrice());
                jsonObject.addProperty(Constants.createdBy, hostId);
                jsonArrayParsing.add(jsonObject);
            }
        }
        Log.d("fsnaklfnkasl", "parsingTicketBookData: " + jsonArrayParsing);
        return jsonArrayParsing;
    }

    private void launchSuccessTicketDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.sent_ticket_dialog, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();

        ((TextView) dialogView.findViewById(R.id.btnOk)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                    navigateToHome();
                }
            }
        });

        alertDialog.show();
    }

    private void navigateToHome() {
        Intent homeIntent = new Intent(SelectTicketActivity.this, HomeActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
        finish();
    }

    private void showVIPTicket(List<VipTicketInfo> vipTicketInfoList) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject setObj1 = null;
        for (int i = 0; i < vipTicketInfoList.size(); i++) {
            ;
            setObj1 = new JSONObject();
            try {
                setObj1.put("isSeatingTable", false);
                setObj1.put("id", vipTicketInfoList.get(i).getId());
                setObj1.put("ticketType", vipTicketInfoList.get(i).getTicketType());
                setObj1.put("ticketName", vipTicketInfoList.get(i).getTicketName());
                setObj1.put("totalQuantity", vipTicketInfoList.get(i).getTotalQuantity());
                setObj1.put("description", vipTicketInfoList.get(i).getDescription());
                setObj1.put("pricePerTicket", vipTicketInfoList.get(i).getPricePerTicket());
                setObj1.put("parsonPerTable", 0);
                setObj1.put("pricePerTable", 0);
                setObj1.put("noOfTables", 0);
                setObj1.put("discountedPrice", 0);
                setObj1.put("disPercentage", 0);

                jsonArray.put(setObj1);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("fnaslkfnsklanfa", "JSONException: " + e.getMessage());
            }
        }

        try {
            jsonObject.put("tickets", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("fnaslkfnsklanfa", "jsonObject: " + e.getMessage());
        }

        Log.d("fnalfnlkanksfa", "showVIPTicket: " + jsonObject.toString());

        FinalSelectTicketModal finalSelectTicketModal = new Gson().fromJson(jsonObject.toString(), FinalSelectTicketModal.class);
        vipNormalAdapter = new BuyTicketAdapter(SelectTicketActivity.this, finalSelectTicketModal.getTickets());
        ticketBinding.recyclerInfoVipTicket.setAdapter(vipNormalAdapter);
    }

    private void showVIPSeatingTicket(List<VipTableSeatingInfo> vipTableSeatingInfos) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject setObj1 = null;
        for (int i = 0; i < vipTableSeatingInfos.size(); i++) {
            ;
            setObj1 = new JSONObject();
            try {
                setObj1.put("isSeatingTable", true);
                setObj1.put("id", vipTableSeatingInfos.get(i).getId());
                setObj1.put("ticketType", vipTableSeatingInfos.get(i).getTicketType());
                setObj1.put("ticketName", vipTableSeatingInfos.get(i).getTicketName());
                setObj1.put("totalQuantity", vipTableSeatingInfos.get(i).getTotalQuantity());
                setObj1.put("description", vipTableSeatingInfos.get(i).getDescription());
                setObj1.put("pricePerTicket", vipTableSeatingInfos.get(i).getPricePerTicket());
                setObj1.put("parsonPerTable", vipTableSeatingInfos.get(i).getParsonPerTable());
                setObj1.put("pricePerTable", vipTableSeatingInfos.get(i).getPricePerTable());
                setObj1.put("noOfTables", vipTableSeatingInfos.get(i).getNoOfTables());
                setObj1.put("discountedPrice", vipTableSeatingInfos.get(i).getDiscountedPrice());
                setObj1.put("disPercentage", vipTableSeatingInfos.get(i).getDisPercentage());
                jsonArray.put(setObj1);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("fnaslkfnsklanfa", "JSONException: " + e.getMessage());
            }
        }

        try {
            jsonObject.put("tickets", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("fnaslkfnsklanfa", "jsonObject: " + e.getMessage());
        }

        FinalSelectTicketModal finalSelectTicketModal = new Gson().fromJson(jsonObject.toString(), FinalSelectTicketModal.class);
        vipSeatingAdapter = new BuyTicketAdapter(SelectTicketActivity.this, finalSelectTicketModal.getTickets());
        ticketBinding.recyclerVipTicket.setAdapter(vipSeatingAdapter);

    }

    private void showRegularTicket(List<RegularTicketInfo> regularTicketInfos) {

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject setObj1 = null;
        for (int i = 0; i < regularTicketInfos.size(); i++) {
            ;
            setObj1 = new JSONObject();
            try {
                setObj1.put("isSeatingTable", false);
                setObj1.put("id", regularTicketInfos.get(i).getId());
                setObj1.put("ticketType", regularTicketInfos.get(i).getTicketType());
                setObj1.put("ticketName", regularTicketInfos.get(i).getTicketName());
                setObj1.put("totalQuantity", regularTicketInfos.get(i).getTotalQuantity());
                setObj1.put("description", regularTicketInfos.get(i).getDescription());
                setObj1.put("pricePerTicket", regularTicketInfos.get(i).getPricePerTicket());
                setObj1.put("parsonPerTable", 0);
                setObj1.put("pricePerTable", 0);
                setObj1.put("noOfTables", 0);
                setObj1.put("discountedPrice", 0);
                setObj1.put("disPercentage", 0);


                jsonArray.put(setObj1);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("fnaslkfnsklanfa", "JSONException: " + e.getMessage());
            }
        }

        try {
            jsonObject.put("tickets", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("fnaslkfnsklanfa", "jsonObject: " + e.getMessage());
        }

        FinalSelectTicketModal finalSelectTicketModal = new Gson().fromJson(jsonObject.toString(), FinalSelectTicketModal.class);

        Log.d("afnlkasnfl", "showRegularTicket: " + finalSelectTicketModal.getTickets().size());
        regularNormalAdapter = new BuyTicketAdapter(SelectTicketActivity.this, finalSelectTicketModal.getTickets());
        ticketBinding.recyclerRegularTicket.setAdapter(regularNormalAdapter);
    }

    private void showRegularSeatingTicket(List<RegularTicketSeatingInfo> regularTicketSeatingInfos) {

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject setObj1 = null;
        for (int i = 0; i < regularTicketSeatingInfos.size(); i++) {
            setObj1 = new JSONObject();
            try {
                setObj1.put("isSeatingTable", true);
                setObj1.put("id", regularTicketSeatingInfos.get(i).getId());
                setObj1.put("ticketType", regularTicketSeatingInfos.get(i).getTicketType());
                setObj1.put("ticketName", regularTicketSeatingInfos.get(i).getTicketName());
                setObj1.put("totalQuantity", regularTicketSeatingInfos.get(i).getTotalQuantity());
                setObj1.put("description", regularTicketSeatingInfos.get(i).getDescription());
                setObj1.put("pricePerTicket", regularTicketSeatingInfos.get(i).getPricePerTicket());
                setObj1.put("parsonPerTable", regularTicketSeatingInfos.get(i).getParsonPerTable());
                setObj1.put("pricePerTable", regularTicketSeatingInfos.get(i).getPricePerTable());
                setObj1.put("noOfTables", regularTicketSeatingInfos.get(i).getNoOfTables());
                setObj1.put("discountedPrice", regularTicketSeatingInfos.get(i).getDiscountedPrice());
                setObj1.put("disPercentage", regularTicketSeatingInfos.get(i).getDisPercentage());
                jsonArray.put(setObj1);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("fnaslkfnsklanfa", "JSONException: " + e.getMessage());
            }
        }
        try {
            jsonObject.put("tickets", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("fnaslkfnsklanfa", "jsonObject: " + e.getMessage());
        }
        FinalSelectTicketModal finalSelectTicketModal = new Gson().fromJson(jsonObject.toString(), FinalSelectTicketModal.class);
        regularSeatingAdapter = new BuyTicketAdapter(SelectTicketActivity.this, finalSelectTicketModal.getTickets());
        ticketBinding.recyclerRegularSeatingTicket.setAdapter(regularSeatingAdapter);
    }

    private void getEphemeralKeyRequest() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("api_version", Constants.api_version);
        jsonObject.addProperty("customer", CommonUtils.getCommonUtilsInstance().getStripeCustomerId());


        //  Call<JsonElement> key = APICall.getApiInterface().getEphemeralKey(jsonObject);
        //new APICall(SelectTicketActivity.this).apiCalling(key,this,APIs.GET_EPHEMERAL_KEY);
    }


    private void getCustomerSession() {


    }

    private void setupPaymentSession() {
//        final boolean paymentSessionInitialized =  paymentSession.init(new StripePaymentSessionListener(),createPaymentSessionConfig());
//        if(paymentSessionInitialized){
//
//        }
    }


    @NonNull
    private ShippingInformation getDefaultShippingInfo() {
        Address address = new Address.Builder().setPostalCode("").build();
        // optionally specify default shipping address
        return new ShippingInformation(address, "", "");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //  paymentSession.onDestroy();


    }


    private PaymentSessionConfig createPaymentSessionConfig() {
        return new PaymentSessionConfig.Builder()
                .setHiddenShippingInfoFields(
                        ShippingInfoWidget.CustomizableShippingField.PHONE_FIELD
                )

                .setBillingAddressFields(BillingAddressFields.PostalCode)
                .setShippingInfoRequired(true)
                .setShippingMethodsRequired(false)
                .build();
    }


    private void createStripeSession() {

        CustomerSession.initCustomerSession(this, new GetEphemeralKey());
        CustomerSession.getInstance().getPaymentMethods(PaymentMethod.Type.Card,
                new CustomerSession.PaymentMethodsRetrievalListener() {
                    @Override
                    public void onError(int i, @NotNull String s, @org.jetbrains.annotations.Nullable StripeError stripeError) {

                    }

                    @Override
                    public void onPaymentMethodsRetrieved(@NotNull List<PaymentMethod> list) {
                        Log.d("fanlkfnakl", "onPaymentMethodsRetrieved: " + list.size());


                    }

                });


        CustomerSession.getInstance().retrieveCurrentCustomer(new CustomerSession.CustomerRetrievalListener() {
            @Override
            public void onError(int i, @NotNull String s, @org.jetbrains.annotations.Nullable StripeError stripeError) {
                ShowToast.errorToast(SelectTicketActivity.this, s);
                Log.d("fblablfa", "onError: " + s);
            }

            @Override
            public void onCustomerRetrieved(@NotNull Customer customer) {
                try {
                    Log.d("fblablfa", "onCustomerRetrieved: ");

                    SessionValidation.getPrefsHelper().savePref(Constants.customer, new Gson().toJson(customer));

                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Log.d("fblablfa", "NullPointerException: " + e.getMessage());
                }
            }

        });

    }

    private void launchPaymentMethodsActivity() {
        if (CustomerSession.getInstance() != null)
            new PaymentMethodsActivityStarter(this).startForResult();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            paymentSession.handlePaymentData(requestCode, resultCode, data);
        }

        if (requestCode == PaymentMethodsActivityStarter.REQUEST_CODE) {
            final PaymentMethodsActivityStarter.Result result =
                    PaymentMethodsActivityStarter.Result.fromIntent(data);
            final PaymentMethod paymentMethod = result != null ?
                    result.paymentMethod : null;
            if (paymentMethod != null) {
                Log.d("fanslkfnsa", "onActivityResulssst: " + paymentMethod.id);
                paymentSession.presentPaymentMethodSelection(paymentMethod.id);
                Log.d("fanslkfnsa", "onActivityResult: " + paymentSession.getPaymentSessionData().getPaymentMethod().id);

            }
        }
    }


    private double getTotalPrice(FinalSelectTicketModal.Ticket ticketData, int itemSelectedNumber){
        double totalPrice = 0;

        if(ticketData.getDiscountedPrice() != 0){
            totalPrice = totalPrice +  ticketData.getDiscountedPrice() * itemSelectedNumber;

        }else if(ticketData.getTicketType().equalsIgnoreCase(getString(R.string.vip_table_seating)) || ticketData.getTicketType().equalsIgnoreCase(getString(R.string.regular_table_seating))) {
            totalPrice = totalPrice +  ticketData.getPricePerTable() * itemSelectedNumber;
        }else{
            totalPrice = totalPrice +  ticketData.getPricePerTicket() * itemSelectedNumber;
        }

        return totalPrice;
    }

}
