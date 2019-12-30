package com.ebabu.event365live.userinfo.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.GiveRatingDialogLayoutBinding;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.SessionValidation;
import com.ebabu.event365live.utils.ShowToast;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;

public class RatingDialogFragment extends DialogFragment implements View.OnClickListener, GetResponseData {
    public static String TAG = "RatingDialogFragment";
    private Context context;
    private Dialog dialog;
    private MyLoader myLoader;
    private GiveRatingDialogLayoutBinding dialogRatingBinding;
    private int eventId;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        myLoader = new MyLoader(context);
        if(getArguments() != null) {
            eventId = getArguments().getInt(Constants.ApiKeyName.eventId);
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dialogRatingBinding = DataBindingUtil.inflate(inflater, R.layout.give_rating_dialog_layout, container, false);
        dialogRatingBinding.btnSubmitRating.setOnClickListener(this);
        dialogRatingBinding.ivEventImg.setOnClickListener(this);
        if (getDialog() != null && getDialog().getWindow() != null)
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return dialogRatingBinding.getRoot();
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnSubmitRating){
             int ratingCount = (int) dialogRatingBinding.ratingbar.getRating();
             String reviewComment = dialogRatingBinding.etEnterReviewComment.getText().toString();
             if(TextUtils.isEmpty(CommonUtils.getCommonUtilsInstance().getUserId())){
                ShowToast.errorToast(getActivity(),getString(R.string.please_login));
                return;
             }
             createReviewRequest(ratingCount,reviewComment,eventId);
        }else if(view.getId() == R.id.ivEventImg)
            dialog.dismiss();
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        if(responseObj != null)
        {

        }
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        ShowToast.errorToast(context,message);
        if(errorBody != null){

        }
    }
    private void createReviewRequest(int reviewStar, String reviewComment, int eventId){
        myLoader.show("Please Wait...");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Constants.ApiKeyName.reviewStar,reviewStar);
        jsonObject.addProperty(Constants.ApiKeyName.reviewText,reviewComment);
        jsonObject.addProperty(Constants.ApiKeyName.eventId,eventId);

        Call<JsonElement> jsonElementCall = APICall.getApiInterface().createReview(CommonUtils.getCommonUtilsInstance().getDeviceAuth(), jsonObject);
        new APICall(context).apiCalling(jsonElementCall, this, APIs.CREATE_REVIEW);
    }
}
