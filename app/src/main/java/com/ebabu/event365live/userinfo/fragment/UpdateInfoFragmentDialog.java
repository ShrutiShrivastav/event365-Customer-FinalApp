package com.ebabu.event365live.userinfo.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.ebabu.event365live.R;
import com.ebabu.event365live.auth.activity.OtpVerificationActivity;
import com.ebabu.event365live.databinding.UpdateInfoDialogFragLayoutBinding;
import com.ebabu.event365live.homedrawer.activity.ChooseRecommendedCatActivity;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.ShowToast;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;

public class UpdateInfoFragmentDialog extends DialogFragment implements TextWatcher, GetResponseData, View.OnClickListener {

    public static String TAG = "UpdateInfoFragmentDialog";
    private Context context;
    private Activity activity;
    private Dialog dialog;
    private UpdateInfoDialogFragLayoutBinding dialogFragLayoutBinding;
    private MyLoader myLoader;
    private static LatLng currentLatLng;
    private String getMobile = "", getCountryCode = "";
    private boolean isEnteredNoValid;
    private boolean getIsMobileVerified;
    private String getUserName;
    private Bundle bundle;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        activity = (Activity) context;
        myLoader = new MyLoader(context);
        bundle = getArguments();
        if (bundle != null) {
            getUserName = bundle.getString(Constants.SharedKeyName.userName);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dialogFragLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.update_info_dialog_frag_layout, container, false);
        dialogFragLayoutBinding.countryCodePicker.registerCarrierNumberEditText(dialogFragLayoutBinding.etEnterMobile);
        dialogFragLayoutBinding.etEnterName.addTextChangedListener(this);
        dialogFragLayoutBinding.etEnterCity.addTextChangedListener(this);
        dialogFragLayoutBinding.btnSubmit.setOnClickListener(this);
        dialogFragLayoutBinding.etEnterAdd.setOnClickListener(this);
        dialogFragLayoutBinding.ivBackBtn.setOnClickListener(this);

        dialogFragLayoutBinding.etEnterCounty.setOnClickListener(this);
        dialogFragLayoutBinding.etEnterState.setOnClickListener(this);
        dialogFragLayoutBinding.etEnterCity.setOnClickListener(this);
        dialogFragLayoutBinding.etEnterZip.setOnClickListener(this);

        dialogFragLayoutBinding.tvSkip.setOnClickListener(view -> {
            editProfileRequest();
        });

        if (getDialog() != null && getDialog().getWindow() != null)
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        if (getUserName != null) {
            dialogFragLayoutBinding.etEnterName.setText(getUserName);
            dialogFragLayoutBinding.etEnterName.setSelection(getUserName.length());
        }
        dialogFragLayoutBinding.countryCodePicker.setPhoneNumberValidityChangeListener(isValidNumber -> {
            if (isValidNumber) {
                dialogFragLayoutBinding.ivShowMobileTick.setVisibility(View.VISIBLE);
                isEnteredNoValid = isValidNumber;
            } else {
                dialogFragLayoutBinding.ivShowMobileTick.setVisibility(View.INVISIBLE);
                isEnteredNoValid = isValidNumber;
            }
        });

        dialogFragLayoutBinding.etEnterAdd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    activity.getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }

                Log.d("fsnafnaskl", "onTextChanged: " + s.length());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return dialogFragLayoutBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (dialogFragLayoutBinding.etEnterName.getId() == R.id.etEnterName) {
            Log.d("nfanfbjbasfbbaf", "etEnterName: " + charSequence.toString());
        } else if (dialogFragLayoutBinding.etEnterCity.getId() == R.id.etEnterCity) {
            Log.d("nfanfbjbasfbbaf", "etCitySearch: " + charSequence.toString());
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        if (responseObj != null) {
            ShowToast.infoToast(context, message);
            if (typeAPI.equalsIgnoreCase(APIs.UPDATE_PROFILE)) {
                navigateToRecommendedCategorySelect();
            }
        }
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        ShowToast.infoToast(context, message);
        if (errorCode != null && errorCode == APIs.PHONE_OTP_REQUEST) {
            ShowToast.infoToast(context, getString(R.string.please_enter_otp));
            navigateToVerifyOtpScreen();
        }
    }

    private void navigateToRecommendedCategorySelect() {
        Intent catIntent = new Intent(requireContext(), ChooseRecommendedCatActivity.class);
        catIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(catIntent);
        ((Activity) context).finish();
    }

    private void updateProfileRequest(String name, String mobile, String state, String city, String zip) {
        myLoader.show("updating...");

        Map<String, RequestBody> requestBodyMap = new HashMap<>();
        if (!name.isEmpty())
            requestBodyMap.put(Constants.ApiKeyName.name, getRequestBody(name));
        if (!state.isEmpty())
            requestBodyMap.put(Constants.ApiKeyName.state, getRequestBody(state));
        requestBodyMap.put(Constants.ApiKeyName.countryCode, getRequestBody(getCountryCode));
        if (!zip.isEmpty())
            requestBodyMap.put(Constants.ApiKeyName.zip, getRequestBody(zip));
        if (!city.isEmpty())
            requestBodyMap.put(Constants.ApiKeyName.city, getRequestBody(city));
        if (currentLatLng != null) {
            requestBodyMap.put(Constants.ApiKeyName.latitude, getRequestBody(String.valueOf(currentLatLng.latitude)));
            requestBodyMap.put(Constants.ApiKeyName.longitude, getRequestBody(String.valueOf(currentLatLng.longitude)));
        }
        requestBodyMap.put("isFromProfile", getRequestBody("true"));
        if (!TextUtils.isEmpty(mobile)) {
            if (mobile.contains(" ")) {
                mobile = mobile.replace(" ", "");
            }
            requestBodyMap.put(Constants.ApiKeyName.phoneNo, getRequestBody(mobile.trim()));
        }

//        Log.d("fnslakfna", "updateProfileRequest: " + requestBodyMap.toString());

//        Log.d("flaskfnskanfklasna", CommonUtils.getCommonUtilsInstance().getDeviceAuth() + " updateProfileRequest: "
//                + name + "\n" + "\n" + mobile + "\n" + state + "\n" + city + "\n" + zip + "\n" + currentLatLng.latitude + "\n" + currentLatLng.longitude + "\n" + getCountryCode);
        Call<JsonElement> updateObj = APICall.getApiInterface().updateProfile(CommonUtils.getCommonUtilsInstance().getDeviceAuth(), requestBodyMap, null);
        new APICall(activity).apiCalling(updateObj, this, APIs.UPDATE_PROFILE);
    }

    private void editProfileRequest() {
        String getUserName = dialogFragLayoutBinding.etEnterName.getText().toString().trim();
        String getUserAdd = dialogFragLayoutBinding.etEnterAdd.getText().toString().trim();
        String getCountryName = dialogFragLayoutBinding.etEnterCounty.getText().toString().trim();
        String getState = dialogFragLayoutBinding.etEnterState.getText().toString().trim();
        String getCity = dialogFragLayoutBinding.etEnterCity.getText().toString().trim();
        String getZip = dialogFragLayoutBinding.etEnterZip.getText().toString().trim();
        getMobile = dialogFragLayoutBinding.etEnterMobile.getText().toString().trim();
        getCountryCode = dialogFragLayoutBinding.countryCodePicker.getSelectedCountryCodeWithPlus();
        dialogFragLayoutBinding.etEnterState.setEnabled(false);
        dialogFragLayoutBinding.etEnterCity.setEnabled(false);

//        if (!ValidationUtil.validateName(context, getUserName)) {
//            dialogFragLayoutBinding.etEnterName.requestFocus();
//            return;
//        } else if (TextUtils.isEmpty(getUserAdd)) {
//            ShowToast.infoToast(context, context.getString(R.string.please_enter_add));
//            dialogFragLayoutBinding.etEnterAdd.requestFocus();
//            return;
//        } else if (TextUtils.isEmpty(getCountryName)) {
//            ShowToast.infoToast(context, context.getString(R.string.please_enter_country));
//            dialogFragLayoutBinding.etEnterCounty.requestFocus();
//            return;
//        } else if (TextUtils.isEmpty(getUserAdd)) {
//            ShowToast.infoToast(context, context.getString(R.string.please_select_add));
//            dialogFragLayoutBinding.etEnterAdd.requestFocus();
//            return;
//        } else if (TextUtils.isEmpty(getState)) {
//            ShowToast.infoToast(context, context.getString(R.string.please_enter_state));
//            dialogFragLayoutBinding.etEnterState.requestFocus();
//            return;
//        } else if (TextUtils.isEmpty(getCity)) {
//            ShowToast.infoToast(context, context.getString(R.string.please_enter_city));
//            dialogFragLayoutBinding.etEnterCity.requestFocus();
//            return;
//        } else if (TextUtils.isEmpty(getZip)) {
//            ShowToast.infoToast(context, getString(R.string.please_enter_zip_code));
//            dialogFragLayoutBinding.etEnterZip.requestFocus();
//            return;
//        } else
//
//        if (!isEnteredNoValid) {
//            ShowToast.infoToast(activity, getString(R.string.error_please_enter_valid_no));
//            return;
//        }

        getMobile = getMobile.replaceAll("\\s+", "").trim();
        updateProfileRequest(getUserName, getMobile, getState, getCity, getZip);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnSubmit:
                editProfileRequest();
                break;

            case R.id.etEnterAdd:
                CommonUtils.getCommonUtilsInstance().launchSelectAddressFrag((Activity) context, this, true);
                break;

            case R.id.ivBackBtn:
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                break;

            case R.id.etEnterCity:
                shakeText();
                Log.d("fasfsafsa", "onClick: ");
                break;
            case R.id.etEnterCounty:
                shakeText();
                break;
            case R.id.etEnterState:
                shakeText();
                break;
            case R.id.etEnterZip:
                shakeText();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                CommonUtils.hideSoftKeyboard((AppCompatActivity) context);
                Place place = Autocomplete.getPlaceFromIntent(data);
                currentLatLng = place.getLatLng();
                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(currentLatLng.latitude, currentLatLng.longitude, 1);
                    String stateName = addresses.get(0).getAdminArea();
                    String cityName = addresses.get(0).getLocality();
                    String countryName = addresses.get(0).getCountryName();
                    String countryCode = addresses.get(0).getCountryCode();
                    String postalCode = addresses.get(0).getPostalCode();
                    dialogFragLayoutBinding.countryCodePicker.setCountryForNameCode(countryCode);
                    dialogFragLayoutBinding.etEnterAdd.setText(place.getName());
                    dialogFragLayoutBinding.etEnterAdd.setSelection(place.getName().length());
                    dialogFragLayoutBinding.etEnterCounty.setText(countryName);
                    dialogFragLayoutBinding.etEnterState.setText(stateName);
                    dialogFragLayoutBinding.etEnterCity.setText(cityName);
                    dialogFragLayoutBinding.etEnterZip.setText(postalCode);
                    dialogFragLayoutBinding.etEnterZip.setEnabled(postalCode != null ? false : true);


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == Constants.MOBILE_VERIFY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            dialogFragLayoutBinding.etEnterMobile.setText(getMobile);
            dialogFragLayoutBinding.etEnterMobile.setEnabled(false);
            dialogFragLayoutBinding.etEnterMobileTIL.setError("Mobile Verify Successfully");
            if (data != null && data.getExtras() != null)
                getIsMobileVerified = data.getExtras().getBoolean(Constants.SharedKeyName.isMobileVerified);
            //editProfileRequest();
        }
    }


    private void navigateToVerifyOtpScreen() {
        Intent smsVerifyIntent = new Intent(activity, OtpVerificationActivity.class);
        smsVerifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        smsVerifyIntent.putExtra("activityName", getString(R.string.is_from_update_dialog_fragment));
        smsVerifyIntent.putExtra(Constants.ApiKeyName.countryCode, getCountryCode);
        smsVerifyIntent.putExtra(Constants.ApiKeyName.phoneNo, getMobile.trim());
        context.startActivity(smsVerifyIntent);
    }

    private static RequestBody getRequestBody(String value) {
        return RequestBody.create(okhttp3.MediaType.parse("text/plain"), value);
    }

    private void shakeText() {
        dialogFragLayoutBinding.tvShowInfo.setAnimation(AnimationUtils.loadAnimation(context, R.anim.shake));
    }

}

