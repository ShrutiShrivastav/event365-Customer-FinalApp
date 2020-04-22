package com.ebabu.event365live.auth.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.ebabu.event365live.BaseActivity;
import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityRegisterBinding;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.ApiInterface;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.ShowToast;
import com.ebabu.event365live.utils.ValidationUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;

public class RegisterActivity extends BaseActivity implements GetResponseData {

    private ActivityRegisterBinding registerBinding;
    private GoogleSignInClient mGoogleSignInClint;
    private GoogleSignInAccount account;
    private boolean isClickFirstTime;
    private ApiInterface apiInterface;
    private String userFullName, userEmail, userImg,userFirstName,userLastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        registerBinding.etEnterName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > 1 && !charSequence.toString().startsWith(" ") && !charSequence.toString().endsWith(" "))
                    registerBinding.ivShowNameTick.setVisibility(View.VISIBLE);
                else
                    registerBinding.ivShowNameTick.setVisibility(View.INVISIBLE);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        registerBinding.etEnterEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (ValidationUtil.emailValidatorWithoutToast(RegisterActivity.this, charSequence.toString()))
                    registerBinding.ivShowEmailTick.setVisibility(View.VISIBLE);
                else
                    registerBinding.ivShowEmailTick.setVisibility(View.INVISIBLE);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        registerBinding.etEnterPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0)
                    registerBinding.ivShowHidePass.setVisibility(View.VISIBLE);
                else
                    registerBinding.ivShowHidePass.setVisibility(View.INVISIBLE);


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    public void backBtnOnClick(View view) {
        finish();
    }

    public void clickOnRegisterBtn(View view) {
        String getEnteredName = registerBinding.etEnterName.getText().toString();
        String getEnteredEmail = registerBinding.etEnterEmail.getText().toString();
        String getEnteredPass = registerBinding.etEnterPass.getText().toString();

        if (!ValidationUtil.validateName(RegisterActivity.this, getEnteredName))
            return;
        else if (!ValidationUtil.emailValidator(RegisterActivity.this, getEnteredEmail))
            return;
        else if (!ValidationUtil.passwordValidator(RegisterActivity.this, getEnteredPass))
            return;
        registerUserRequest(getEnteredName, getEnteredEmail, getEnteredPass);
    }

    public void showPassOnClick(View view) {
        if (registerBinding.ivShowHidePass.isShown() && !isClickFirstTime) {
            registerBinding.ivShowHidePass.setImageDrawable(getResources().getDrawable(R.drawable.hide_pass_icon));
            registerBinding.etEnterPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            registerBinding.etEnterPass.setSelection(registerBinding.etEnterPass.length());
            isClickFirstTime = true;


        } else if (registerBinding.ivShowHidePass.isShown() && isClickFirstTime) {
            registerBinding.ivShowHidePass.setImageDrawable(getResources().getDrawable(R.drawable.unselect_pass_icon));
            registerBinding.etEnterPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            registerBinding.etEnterPass.setSelection(registerBinding.etEnterPass.length());
            isClickFirstTime = false;
        }

    }

    private void registerUserRequest(String name, String email, String pass) {
        myLoader.show("");
        JsonObject signUpObj = new JsonObject();
        signUpObj.addProperty(Constants.ApiKeyName.name, name);
        signUpObj.addProperty(Constants.ApiKeyName.email, email);
        signUpObj.addProperty(Constants.ApiKeyName.password, pass);

        Call<JsonElement> getSignUpObj = APICall.getApiInterface().signUp(signUpObj);
        new APICall(RegisterActivity.this).apiCalling(getSignUpObj, this, APIs.SIGN_UP);
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        CommonUtils.getCommonUtilsInstance().validateUserIdFromErrorResponse(responseObj);

        Intent intent = new Intent(RegisterActivity.this, OtpVerificationActivity.class);
        intent.putExtra("activityName", getString(R.string.is_from_register_activity));
        intent.putExtra(Constants.SharedKeyName.userName, registerBinding.etEnterName.getText().toString().trim());
        intent.putExtra(Constants.SharedKeyName.userEmail,registerBinding.etEnterEmail.getText().toString().trim() );
        startActivity(intent);
        finish();


    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        CommonUtils.getCommonUtilsInstance().validateUserIdFromErrorResponse(errorBody);
        ShowToast.infoToast(this,message);
        if (errorCode == APIs.EMAIL_NOT_VERIFIED) {
        }
    }

    private void showGmailProfileDetails() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClint = GoogleSignIn.getClient(this, gso);
        account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            userFullName = account.getDisplayName();
            userEmail = account.getEmail();
            userImg = account.getPhotoUrl().toString();
//            fromLoginGoogle = true;
            ShowToast.successToast(RegisterActivity.this, userFullName + " " + userEmail);

        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (account != null) {
            mGoogleSignInClint.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    ShowToast.successToast(RegisterActivity.this, "logout successfully");
                    finish();

                }
            });
        }
    }
}
