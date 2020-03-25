package com.ebabu.event365live.ticketbuy;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import com.stripe.android.ApiResultCallback;
import com.stripe.android.CustomerSession;
import com.stripe.android.EphemeralKeyUpdateListener;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.PaymentSession;
import com.stripe.android.PaymentSessionConfig;
import com.stripe.android.PaymentSessionData;
import com.stripe.android.Stripe;
import com.stripe.android.StripeError;
import com.stripe.android.model.Address;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.Customer;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.ShippingInformation;
import com.stripe.android.view.AddPaymentMethodActivity;
import com.stripe.android.view.AddPaymentMethodActivityStarter;
import com.stripe.android.view.BillingAddressFields;
import com.stripe.android.view.PaymentMethodsActivityStarter;
import com.stripe.android.view.ShippingInfoWidget;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    private boolean isPaymentMethodAvailable;
    //  private PaymentMethod getPaymentMethod;
    private String deviceAuth;
    private int freeTicketCount, anotherTicketCount;
    private PaymentMethod getPaymentMethod;
    private String qrCode;

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
        deviceAuth = CommonUtils.getCommonUtilsInstance().getDeviceAuth();
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

            getTicketInfoRequest();
        }
    }

    public void backBtnOnClick(View view) {
        finish();
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {

        if (responseObj != null) {
            if (typeAPI.equalsIgnoreCase(APIs.GET_EPHEMERAL_KEY)) {
                myLoader.dismiss();
                Log.d("fnalkfnskla", "GET_EPHEMERAL_KEY: " + responseObj.toString());
                keyUpdateListener.onKeyUpdate(responseObj.toString());
                return;
            }
            if (typeAPI.equalsIgnoreCase(APIs.USER_TICKET_BOOKED)) {
                myLoader.dismiss();
                //TODO fire ticketPaymentRequest Api to get client secret code
                //if user is booking only free ticket, it should not follow payment flow
                if (freeTicketCount > 0 && anotherTicketCount == 0) {
                    CommonUtils.getCommonUtilsInstance().loginAlert(SelectTicketActivity.this, true, "Ticket Booked");
                    return;
                }

                if (responseObj.has("data")) {
                    try {
                        qrCode = responseObj.getString("data");
                        ticketPaymentRequest(qrCode, .50 * 100, getPaymentMethod.id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ShowToast.errorToast(SelectTicketActivity.this,getString(R.string.something_wrong));
                        finish();
                    }
                }
                return;
            } else if (typeAPI.equalsIgnoreCase(APIs.TICKET_PAYMENT_REQUEST)) {
                myLoader.dismiss();
                //TODO hit confirm payment API
                if (responseObj.has("data")) {
                    try {
                        String clientSecretId = responseObj.getJSONObject("data").getString("client_secret");
                        createPaymentIntent(clientSecretId, getPaymentMethod.id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ShowToast.errorToast(SelectTicketActivity.this,getString(R.string.something_wrong));
                        finish();
                    }
                }
                return;
            } else if (typeAPI.equalsIgnoreCase(APIs.PAYMENT_CONFIRM)) {
                myLoader.dismiss();
                launchSuccessTicketDialog();
                return;
            }
            myLoader.dismiss();
            selectionModal = new Gson().fromJson(responseObj.toString(), TicketSelectionModal.class);
            if (selectionModal.getTicketSelectionData().getFreeTicket() != null && selectionModal.getTicketSelectionData().getFreeTicket().size() > 0) {
                ticketBinding.freeTicketTitleContainer.setVisibility(View.VISIBLE);
                setupFreeTicket(selectionModal.getTicketSelectionData().getFreeTicket());
            }
            if (selectionModal.getTicketSelectionData().getVipTableSeating().getVipTicketInfo().size() > 0) {
                ticketBinding.vipInfoTitleContainer.setVisibility(View.VISIBLE);
                showVIPTicket(selectionModal.getTicketSelectionData().getVipTableSeating().getVipTicketInfo());
                Log.d("fnklanfklas", "getVipTicketInfo: " + selectionModal.getTicketSelectionData().getVipTableSeating().getVipTicketInfo().size());
            }
            if (selectionModal.getTicketSelectionData().getVipTableSeating().getVipTableSeatingInfo().size() > 0) {
                ticketBinding.vipSeatingTitleContainer.setVisibility(View.VISIBLE);
                showVIPSeatingTicket(selectionModal.getTicketSelectionData().getVipTableSeating().getVipTableSeatingInfo());
                Log.d("fnklanfklas", "getVipTableSeatingInfo: " + selectionModal.getTicketSelectionData().getVipTableSeating().getVipTableSeatingInfo().size());
            }
            if (selectionModal.getTicketSelectionData().getRegularTableSeating().getRegularTicketInfo().size() > 0) {
                ticketBinding.regularTicketTitleContainer.setVisibility(View.VISIBLE);
                showRegularTicket(selectionModal.getTicketSelectionData().getRegularTableSeating().getRegularTicketInfo());
                Log.d("fnklanfklas", "getRegularTicketInfo: " + selectionModal.getTicketSelectionData().getRegularTableSeating().getRegularTicketInfo().size());
            }
            if (selectionModal.getTicketSelectionData().getRegularTableSeating().getRegularTicketSeatingInfo().size() > 0) {
                ticketBinding.regularSeatingTicketTitleContainer.setVisibility(View.VISIBLE);
                showRegularSeatingTicket(selectionModal.getTicketSelectionData().getRegularTableSeating().getRegularTicketSeatingInfo());
                Log.d("fnklanfklas", "getRegularTicketSeatingInfo: " + selectionModal.getTicketSelectionData().getRegularTableSeating().getRegularTicketSeatingInfo().size());
            }

        }
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        ShowToast.errorToast(SelectTicketActivity.this, message);
        finish();
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

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject setObj1 = null;
        for (int i = 0; i < freeTicketList.size(); i++) {
            setObj1 = new JSONObject();
            try {
                setObj1.put("isSeatingTable", true);
                setObj1.put("id", freeTicketList.get(i).getId());
                setObj1.put("ticketType", freeTicketList.get(i).getTicketType());
                setObj1.put("ticketName", freeTicketList.get(i).getTicketName());
                setObj1.put("totalQuantity", freeTicketList.get(i).getTotalQuantity());
                setObj1.put("description", freeTicketList.get(i).getDescription());
                setObj1.put("pricePerTicket", 0);
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
        freeTicketAdapter = new FreeTicketAdapter(SelectTicketActivity.this, finalSelectTicketModal.getTickets());
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

    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        userIsInteracting = true;
    }

    @Override
    public void getSelectedTicketListener(List<FinalSelectTicketModal.Ticket> ticketList, int itemPosition, int itemSelectedNumber) {
        storeEventTicketDetails(ticketList, itemPosition, itemSelectedNumber);

    }

    public void checkOutOnClick(View view) {

        String tvTotalPrice = ticketBinding.tvShowAllEventPrice.getText().toString();
        if (tvTotalPrice.equalsIgnoreCase("$0") && freeTicketCount == 0) {
            ShowToast.infoToast(SelectTicketActivity.this, "Please make sure at least a selection another  to continue or complete booking.");
            return;
        } else if (!tvTotalPrice.equalsIgnoreCase("$0")) {
            String getActualTotalPrice = tvTotalPrice.replace("$", "");
            double getPrice = Double.parseDouble(getActualTotalPrice);
            if (getPrice < 1.0) {
                ShowToast.infoToast(SelectTicketActivity.this, "Please make sure total price should be equal or more than of 1$");
                return;
            }
        }

        for (int i = 0; i < calculateEventPriceModals1.size(); i++) {
            String ticketType = calculateEventPriceModals1.get(i).getTicketType();
            int ticketQty = calculateEventPriceModals1.get(i).getItemSelectedQty();

            Log.d("fnasklnflsa", ticketQty + " checkOutOnClick: " + ticketType);

            if (ticketType.equalsIgnoreCase(getString(R.string.free_normal))) {
                if (ticketQty > 3) {
                    ShowToast.infoToast(SelectTicketActivity.this, getString(R.string.max_ticket_book_msg));
                    return;
                }

            } else if (ticketType.equalsIgnoreCase(getString(R.string.vip_normal))) {
                if (ticketQty > 3) {
                    ShowToast.infoToast(SelectTicketActivity.this, getString(R.string.max_ticket_book_msg));
                    return;
                }
            } else if (ticketType.equalsIgnoreCase(getString(R.string.vip_table_seating))) {
                if (ticketQty > 9) {
                    ShowToast.infoToast(SelectTicketActivity.this, getString(R.string.max_ticket_book_msg));
                    return;
                }
            } else if (ticketType.equalsIgnoreCase(getString(R.string.regular_normal))) {
                if (ticketQty > 3) {
                    ShowToast.infoToast(SelectTicketActivity.this, getString(R.string.max_ticket_book_msg));
                    return;
                }
            } else if (ticketType.equalsIgnoreCase(getString(R.string.regular_table_seating))) {
                if (ticketQty > 9) {
                    ShowToast.infoToast(SelectTicketActivity.this, getString(R.string.max_ticket_book_msg));
                    return;
                }
            }

        }
        if (freeTicketCount > 0 && anotherTicketCount == 0) {
            userTicketBookRequest();
            return;
        }
        parsingTicketBookData();
        launchPaymentMethodsActivity();
    }

    private void storeEventTicketDetails(List<FinalSelectTicketModal.Ticket> ticketList, int itemPosition, int itemSelectedNumber) {

        FinalSelectTicketModal.Ticket ticketData = ticketList.get(itemPosition);
        if (calculateEventPriceModals1.size() > 0) {
            for (int i = 0; i < calculateEventPriceModals1.size(); i++) {
                if (calculateEventPriceModals1.get(i).getTicketId() == ticketData.getTicketId()) {
                    //NumberFormat.getNumberInstance(Locale.US).format(calculateTicketPrice(ticketList, itemPosition, itemSelectedNumber)).toString();
                    calculateEventPriceModals1.set(i, new CalculateEventPriceModal(ticketData.getTicketId(), ticketData.getTicketType(), ticketData.getPricePerTicket(), ticketData.getTotalQuantity(), ticketData.getPricePerTable(), itemSelectedNumber, ticketData.getDiscountedPrice(), getTotalPrice(ticketData, itemSelectedNumber)));

                    double getTotalPrice = calculateTicketPrice(ticketList, itemPosition, itemSelectedNumber);
                    double chargedPrice = getTotalPrice * 5 / 100;

                    ticketBinding.tvShowAllEventPrice.setText("$" + NumberFormat.getNumberInstance(Locale.US).format(getTotalPrice + chargedPrice).toString());
                    ticketBinding.tvShowFivePerCharged.setText("$" + chargedPrice);
                    return;
                }
            }
            calculateEventPriceModals1.add(new CalculateEventPriceModal(ticketData.getTicketId(), ticketData.getTicketType(), ticketData.getPricePerTicket(), ticketData.getTotalQuantity(), ticketData.getPricePerTable(), itemSelectedNumber, ticketData.getDiscountedPrice(), getTotalPrice(ticketData, itemSelectedNumber)));
            NumberFormat.getNumberInstance(Locale.US).format(calculateTicketPrice(ticketList, itemPosition, itemSelectedNumber)).toString();
        } else {
            double getTotalPrice = calculateTicketPrice(ticketList, itemPosition, itemSelectedNumber);
            double chargedPrice = getTotalPrice * 5 / 100;

            calculateEventPriceModals1.add(new CalculateEventPriceModal(ticketData.getTicketId(), ticketData.getTicketType(), ticketData.getPricePerTicket(), ticketData.getTotalQuantity(), ticketData.getPricePerTable(), itemSelectedNumber, ticketData.getDiscountedPrice(), getTotalPrice(ticketData, itemSelectedNumber)));
            ticketBinding.tvShowAllEventPrice.setText("$" + NumberFormat.getNumberInstance(Locale.US).format(getTotalPrice + chargedPrice).toString());
            ticketBinding.tvShowFivePerCharged.setText("$" + chargedPrice);
        }
    }

    private double calculateTicketPrice(List<FinalSelectTicketModal.Ticket> ticketList, int getItemPosition, int getSelectedItemNumber) {
        double totalPrice = 0;

        int freeTicketCount = 0, vipNormalTicketCount = 0, vipSeatingTicketCount = 0, regularNormalTicketCount = 0, regularSeatingTicketCount = 0;
        float vipNormalTicketPrice = 0, vipSeatingTicketPrice = 0, regularNormalTicketPrice = 0, regularSeatingTicketPrice = 0;

        for (int i = 0; i < calculateEventPriceModals1.size(); i++) {
            CalculateEventPriceModal priceModal = calculateEventPriceModals1.get(i);

            totalPrice = totalPrice + priceModal.getTotalPrice();

            if (priceModal.getTicketType().equalsIgnoreCase(getString(R.string.free_normal))) {
                this.freeTicketCount = priceModal.getItemSelectedQty();
                freeTicketCount = freeTicketCount + priceModal.getItemSelectedQty();


            } else if (priceModal.getTicketType().equalsIgnoreCase(getString(R.string.vip_normal))) {
                anotherTicketCount = priceModal.getItemSelectedQty();
                if (priceModal.getItemSelectedQty() > 0) {
                    vipNormalTicketCount = vipNormalTicketCount + priceModal.getItemSelectedQty();
                    vipNormalTicketPrice = vipNormalTicketPrice + priceModal.getTicketPrice() * priceModal.getItemSelectedQty();

                    Log.d("fasfsafa", priceModal.getTicketPrice() + " calculateTicketPrice: " + priceModal.getItemSelectedQty());
                }

            } else if (priceModal.getTicketType().equalsIgnoreCase(getString(R.string.vip_table_seating))) {
                anotherTicketCount = priceModal.getItemSelectedQty();
                vipSeatingTicketCount = vipSeatingTicketCount + priceModal.getItemSelectedQty();

                vipSeatingTicketPrice = vipSeatingTicketPrice + priceModal.getDiscountedPrice() * priceModal.getItemSelectedQty();

            } else if (priceModal.getTicketType().equalsIgnoreCase(getString(R.string.regular_normal))) {
                anotherTicketCount = priceModal.getItemSelectedQty();
                regularNormalTicketCount = regularNormalTicketCount + priceModal.getItemSelectedQty();

                regularNormalTicketPrice = regularNormalTicketPrice + priceModal.getTicketPrice() * priceModal.getItemSelectedQty();

            } else if (priceModal.getTicketType().equalsIgnoreCase(getString(R.string.regular_table_seating))) {
                anotherTicketCount = priceModal.getItemSelectedQty();
                regularSeatingTicketCount = regularSeatingTicketCount + priceModal.getItemSelectedQty();

                regularSeatingTicketPrice = regularSeatingTicketPrice + priceModal.getDiscountedPrice() * priceModal.getItemSelectedQty();
            }
        }

        if (freeTicketCount > 0) {
            ticketBinding.freeTicketContainer.setVisibility(View.VISIBLE);
            ticketBinding.tvFreeTicket.setText(freeTicketCount + " ".concat(freeTicketCount > 1 ? "Free Tickets" : "Free Ticket"));
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
                jsonObject.addProperty(Constants.totalQuantity, modal.getItemSelectedQty());
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
        CustomerSession.initCustomerSession(this, new GetEphemeralKey(SelectTicketActivity.this));

        CustomerSession.PaymentMethodsRetrievalListener listener = new CustomerSession.PaymentMethodsRetrievalListener() {
            @Override
            public void onError(int i, @NotNull String s, @org.jetbrains.annotations.Nullable StripeError stripeError) {
                Log.d("fnalkfnskla", "PaymentMethodsRetrievalListener: " + stripeError);
            }

            @Override
            public void onPaymentMethodsRetrieved(@NotNull List<PaymentMethod> list) {
                myLoader.dismiss();
                Log.d("fnalkfnskla", "onPaymentMethodsRetrieved: " + list.size());
                if (list.size() > 0) {
                    isPaymentMethodAvailable = true;
                } else {
                    isPaymentMethodAvailable = false;
                }
            }
        };


        CustomerSession.CustomerRetrievalListener customerRetrievalListener = new CustomerSession.CustomerRetrievalListener() {
            @Override
            public void onError(int i, @NotNull String s, @org.jetbrains.annotations.Nullable StripeError stripeError) {
                ShowToast.errorToast(SelectTicketActivity.this, s);
                Log.d("fanlkfnakl", "retrieveCurrentCustomer: " + s);
            }

            @Override
            public void onCustomerRetrieved(@NotNull Customer customer) {
                try {
                    SessionValidation.getPrefsHelper().savePref(Constants.customer, new Gson().toJson(customer));

                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Log.d("fnalkfnskla", "NullPointerException: " + e.getMessage());
                }
            }
        };
        CustomerSession customerSession = CustomerSession.getInstance();
        customerSession.getPaymentMethods(PaymentMethod.Type.Card, listener);
        customerSession.retrieveCurrentCustomer(customerRetrievalListener);
        customerSession.setCustomerShippingInformation(new ShippingInformation(), customerRetrievalListener);


    }

    private void launchPaymentMethodsActivity() {

        if (isPaymentMethodAvailable) {
            new PaymentMethodsActivityStarter(this).startForResult();
            return;
        }
        new AddPaymentMethodActivityStarter(this).startForResult();
//        new AddPaymentMethodActivityStarter(this)
//                .startForResult(new AddPaymentMethodActivityStarter.Args.Builder()
//                        .setShouldAttachToCustomer(true)
//                        .build()
//                );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            paymentSession.handlePaymentData(requestCode, resultCode, data);
        }

        mStripe.onPaymentResult(requestCode, data, new ApiResultCallback<PaymentIntentResult>() {
            @Override
            public void onSuccess(PaymentIntentResult paymentIntentResult) {
                myLoader.dismiss();
               // String paymentMethodId = paymentIntentResult.getIntent().getPaymentMethodId();
                String paymentId = paymentIntentResult.getIntent().getId();
                paymentConfirmRequest(paymentId, getPaymentMethod.id);
            }

            @Override
            public void onError(@NotNull Exception e) {
                myLoader.dismiss();
                CommonUtils.getCommonUtilsInstance().loginAlert(SelectTicketActivity.this, false, "Payment failed");
            }
        });
        if (requestCode == PaymentMethodsActivityStarter.REQUEST_CODE) {
            final PaymentMethodsActivityStarter.Result result =
                    PaymentMethodsActivityStarter.Result.fromIntent(data);
            PaymentMethod paymentMethod = result != null ?
                    result.paymentMethod : null;
            setupPaymentSession();
            if (paymentMethod != null) {
            }
        } else if (requestCode == AddPaymentMethodActivityStarter.REQUEST_CODE) {
            AddPaymentMethodActivityStarter.Result result = AddPaymentMethodActivityStarter.Result.fromIntent(data);
            PaymentMethod paymentMethod = result != null ? result.getPaymentMethod() : null;
            setupPaymentSession();
            if (paymentMethod != null) {
            }
        }
    }

    private void setupPaymentSession() {
        paymentSession.init(new PaymentSession.PaymentSessionListener() {
            @Override
            public void onCommunicatingStateChanged(boolean b) {
                if (b) {
                    myLoader.show("Please Wait...");
                } else {
                    myLoader.dismiss();
                }
            }

            @Override
            public void onError(int i, @NotNull String s) {
                myLoader.dismiss();
                ShowToast.infoToastWrong(SelectTicketActivity.this); }

            @Override
            public void onPaymentSessionDataChanged(@NotNull PaymentSessionData paymentSessionData) {
                myLoader.dismiss();
                getPaymentMethod = paymentSessionData.getPaymentMethod();
                if (getPaymentMethod != null) {
                    //TODO hit book ticket api and get qr code along with post it to ticketPaymentRequest API
                    userTicketBookRequest();
                }
            }
        });


    }

    private void createPaymentIntent(String clientSecret, String paymentMethodId) {
        myLoader.show("Please Wait...");
        mStripe.confirmPayment(this,
                ConfirmPaymentIntentParams.createWithPaymentMethodId(paymentMethodId, clientSecret));


    }

    private double getTotalPrice(FinalSelectTicketModal.Ticket ticketData, int itemSelectedNumber) {
        double totalPrice = 0;

        if (ticketData.getDiscountedPrice() != 0) {
            totalPrice = totalPrice + ticketData.getDiscountedPrice() * itemSelectedNumber;

        } else if (ticketData.getTicketType().equalsIgnoreCase(getString(R.string.vip_table_seating)) || ticketData.getTicketType().equalsIgnoreCase(getString(R.string.regular_table_seating))) {
            totalPrice = totalPrice + ticketData.getPricePerTable() * itemSelectedNumber;
        } else {
            totalPrice = totalPrice + ticketData.getPricePerTicket() * itemSelectedNumber;
        }

        return totalPrice;
    }

    private void ticketPaymentRequest(String qrCode, Double amount, String paymentId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Constants.QRkey, qrCode);
        jsonObject.addProperty(Constants.amount, amount);
        jsonObject.addProperty(Constants.currency, "usd");
        jsonObject.addProperty(Constants.customer, CommonUtils.getCommonUtilsInstance().getStripeCustomerId());
        jsonObject.addProperty(Constants.paymentMethod, paymentId);

        Call<JsonElement> paymentRequestCall = APICall.getApiInterface().ticketPaymentRequest(deviceAuth, jsonObject);
        new APICall(SelectTicketActivity.this).apiCalling(paymentRequestCall, this, APIs.TICKET_PAYMENT_REQUEST);
    }


    private void paymentConfirmRequest(String paymentId, String paymentMethod) {
        myLoader.show("Please Wait...");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Constants.QRkey, qrCode);
        jsonObject.addProperty(Constants.paymentId, paymentId);
        jsonObject.addProperty(Constants.payment_method, paymentMethod);

        Call<JsonElement> paymentConfirmCall = APICall.getApiInterface().paymentConfirm(deviceAuth, jsonObject);
        new APICall(SelectTicketActivity.this).apiCalling(paymentConfirmCall, this, APIs.PAYMENT_CONFIRM);
    }


}
