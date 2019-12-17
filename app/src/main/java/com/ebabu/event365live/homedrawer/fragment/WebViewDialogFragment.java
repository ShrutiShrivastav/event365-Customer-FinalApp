package com.ebabu.event365live.homedrawer.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.WebViewDialogFragmentLayoutBinding;
import com.ebabu.event365live.homedrawer.activity.SettingsActivity;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.ShowToast;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;

public class WebViewDialogFragment extends DialogFragment implements GetResponseData {
    public static String TAG = "WebViewDialogFragment";
    private Context context;
    private Activity activity;
    private Dialog dialog;
    private MyLoader myLoader;
    private WebViewDialogFragmentLayoutBinding dialogFragmentLayoutBinding;
    private int PRIVACY_POLICY = 1;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        activity = (Activity) context;
        myLoader = new MyLoader(context);
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
        dialogFragmentLayoutBinding  = DataBindingUtil.inflate(inflater,R.layout.web_view_dialog_fragment_layout,container,false);
        initView();
        return dialogFragmentLayoutBinding.getRoot();
    }
    private void initView() {
        if(getArguments()!= null){
            int flag = getArguments().getInt(Constants.flag);
            if(flag == PRIVACY_POLICY){
                privacyPolicyRequest();
                return;
            }
            termsConditionRequest();
        }
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

    public void backBtnOnClick() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    private void privacyPolicyRequest(){
        myLoader.show("");
        Call<JsonElement> policyCallBack = APICall.getApiInterface().getPolicy();
        new APICall(activity).apiCalling(policyCallBack,this, APIs.GET_POLICY);
    }

    private void termsConditionRequest(){
        myLoader.show("");
        Call<JsonElement> termsCallBack = APICall.getApiInterface().getTermsCondition();
        new APICall(activity).apiCalling(termsCallBack,this,APIs.GET_TERMS_CONDITION);
    }


    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        Log.d(TAG, "onSuccess: "+responseObj);
        if(responseObj != null){
            try {
                JSONArray jsonArray = responseObj.getJSONArray("data");
                JSONObject obj = jsonArray.getJSONObject(0);
                String title = obj.getString("heading");
                String description = obj.getString("description");
                setupWebView(title,description);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        ShowToast.errorToast(activity,message);
    }

    private void setupWebView(String title,String description){
        dialogFragmentLayoutBinding.webView.getSettings().setJavaScriptEnabled(true);
        dialogFragmentLayoutBinding.webView.getSettings().setTextSize(WebSettings.TextSize.NORMAL);
        dialogFragmentLayoutBinding.webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        dialogFragmentLayoutBinding.webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        dialogFragmentLayoutBinding.webView.getSettings().setAppCacheEnabled(false);
        dialogFragmentLayoutBinding.webView.getSettings().setBlockNetworkImage(true);
        dialogFragmentLayoutBinding.webView.getSettings().setLoadsImagesAutomatically(true);
        dialogFragmentLayoutBinding.webView.getSettings().setGeolocationEnabled(false);
        dialogFragmentLayoutBinding.webView.getSettings().setNeedInitialFocus(false);
        dialogFragmentLayoutBinding.webView.getSettings().setSaveFormData(false);
        dialogFragmentLayoutBinding.webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        dialogFragmentLayoutBinding.webView.loadData(description,"text/html","UTF-8");

        dialogFragmentLayoutBinding.tvShowTitle.setText(title);

    }
}
