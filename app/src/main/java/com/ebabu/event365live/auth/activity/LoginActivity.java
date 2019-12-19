package com.ebabu.event365live.auth.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.ebabu.event365live.R;
import com.ebabu.event365live.auth.modal.LoginModal;
import com.ebabu.event365live.databinding.ActivityLoginBinding;
import com.ebabu.event365live.event.LoginEvent;
import com.ebabu.event365live.home.activity.HomeActivity;
import com.ebabu.event365live.homedrawer.activity.ChooseRecommendedCatActivity;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.userinfo.fragment.UpdateInfoFragmentDialog;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.SessionValidation;
import com.ebabu.event365live.utils.ShowToast;
import com.ebabu.event365live.utils.ValidationUtil;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import retrofit2.Call;

public class LoginActivity extends AppCompatActivity implements GetResponseData {

    private ActivityLoginBinding loginBinding;
    private boolean isClickFirstTime;
    private MyLoader myLoader;
    private GoogleSignInClient mGoogleSignInClint;
    private int RC_SIGN_IN_REQUEST = 1001;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private UpdateInfoFragmentDialog infoFragmentDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  printHashKey();
        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        myLoader = new MyLoader(this);
        callbackManager = CallbackManager.Factory.create();
        loginBinding.fbLoginBtn.setPermissions(Arrays.asList("email", "public_profile", "user_friends"));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.e("checkfabookre", "onSuccess ");
                    }

                    @Override
                    public void onCancel() {
                        Log.e("checkfabookre", "onCancel ");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.e("checkfabookre", "== " + exception.getMessage());
                    }
                });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

                if (currentAccessToken != null) {
                    if(!CommonUtils.getCommonUtilsInstance().isNetworkAvailable(LoginActivity.this)){
                        ShowToast.errorToast(LoginActivity.this,getString(R.string.no_internet_conn));
                    }
                    else {
                        loadUserFbDetails(currentAccessToken);
                    }
                }
            }
        };
        loginBinding.etEnterEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (ValidationUtil.emailValidatorWithoutToast(LoginActivity.this, charSequence.toString()))
                    loginBinding.ivShowEmailTick.setVisibility(View.VISIBLE);
                else
                    loginBinding.ivShowEmailTick.setVisibility(View.INVISIBLE);
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        loginBinding.etEnterPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    if (ValidationUtil.passwordValidatorWithoutToast(LoginActivity.this, charSequence.toString()))
                        loginBinding.ivShowPassTick.setVisibility(View.VISIBLE);
                    else
                        loginBinding.ivShowPassTick.setVisibility(View.INVISIBLE);
                        loginBinding.ivShowHidePass.setVisibility(View.VISIBLE);
                } else
                    loginBinding.ivShowHidePass.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void forgotPasswordOnClick(View view) {
        startActivity(new Intent(this, ForgotPassActivity.class));
    }

    public void loginOnClick(View view) {
        String userEmail = loginBinding.etEnterEmail.getText().toString();
        String userPass = loginBinding.etEnterPass.getText().toString();

        if(!ValidationUtil.emailValidator(LoginActivity.this,userEmail))
            return;
        else if(!ValidationUtil.passwordValidator(LoginActivity.this,userPass))
            return;

        userLoginRequest(userEmail,userPass);
    }

    public void gMailLoginOnClick(View view) {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        mGoogleSignInClint = GoogleSignIn.getClient(this, gso);

        Intent googleSignInIntent = mGoogleSignInClint.getSignInIntent();
        startActivityForResult(googleSignInIntent, RC_SIGN_IN_REQUEST);
    }

    public void fbLoginOnClick(View view) {
        loginBinding.fbLoginBtn.performClick();
    }

    public void backBtnOnClick(View view) {
        finish();
    }

    public void registerOnClick(View view) {
        Intent registerIntent= new Intent(LoginActivity.this,RegisterActivity.class);
        registerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(registerIntent);
    }

    public void showHidePassOnClick(View view) {
        if(loginBinding.ivShowHidePass.isShown() && !isClickFirstTime) {
            loginBinding.ivShowHidePass.setImageDrawable(getResources().getDrawable(R.drawable.hide_pass_icon));
            loginBinding.etEnterPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            loginBinding.etEnterPass.setSelection(loginBinding.etEnterPass.length());
            isClickFirstTime = true;
        }
        else if(loginBinding.ivShowHidePass.isShown() && isClickFirstTime){
            loginBinding.ivShowHidePass.setImageDrawable(getResources().getDrawable(R.drawable.unselect_pass_icon));
            loginBinding.etEnterPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            loginBinding.etEnterPass.setSelection(loginBinding.etEnterPass.length());
            isClickFirstTime = false;
        }
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        if(responseObj != null){
            myLoader.dismiss();

            /*swipe event slider should be show in swipe view by default*/
            CommonUtils.getCommonUtilsInstance().validateUser(responseObj);
            navigateToHome();
        }
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        Log.d("fafasfafafasfsafa", "onFailed:s "+errorBody);
        myLoader.dismiss();
        ShowToast.infoToast(LoginActivity.this,message);
            if(errorBody != null){
            CommonUtils.getCommonUtilsInstance().validateUserIdFromErrorResponse(errorBody);
            if (errorCode == APIs.EMAIL_NOT_VERIFIED) {
                navigateToEmailVerification();
            }else if(errorCode == APIs.NEED_PROFILE_UPDATE || errorCode == APIs.PHONE_OTP_REQUEST){
                navigateToUpdateProfileDialogFragment();
            }else if(errorCode == APIs.CHOOSE_RECOMMENDED_CATEGORY){
                navigateToRecommendedCategorySelect();
            }
        }
    }
    private void userLoginRequest(String getUserEmail, String getUserPass){
        myLoader.show("Please Wait...");
        Log.d("flanklfnklansl", SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.deviceToken)+" userLoginRequest: "+SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.deviceType));
        JsonObject userLoginObj = new JsonObject();
        userLoginObj.addProperty(Constants.ApiKeyName.email,getUserEmail);
        userLoginObj.addProperty(Constants.ApiKeyName.password,getUserPass);
        userLoginObj.addProperty(Constants.SharedKeyName.deviceToken,SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.deviceToken).toString());
        userLoginObj.addProperty(Constants.SharedKeyName.deviceType,SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.deviceType).toString());

        Call<JsonElement> getLoginUserObj = APICall.getApiInterface().login(userLoginObj);
        new APICall(LoginActivity.this).apiCalling(getLoginUserObj,this, APIs.LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN_REQUEST) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            //Log.d("bakbfjbafa", "onActivityResult: "+task.getResult().getEmail());
            handleSignInResult(task);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

        Log.d("bakbfjbafa", "onActivityResult: ");
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {

        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        } catch (ApiException e) {
            e.printStackTrace();
            Log.d("bakbfjbafa", "handleSignInResult: "+e.getMessage());
        }
    }
    private void loadUserFbDetails(AccessToken newAccessToken) {
        GraphRequest graphRequest = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                startActivity(new Intent(LoginActivity.this, HomeActivity.class).putExtra(getString(R.string.fb_login_details), object.toString()));
                //finish();
            }
        });
        Bundle requestBundle = new Bundle();
        requestBundle.putString("fields", "first_name,last_name,email,id");
        graphRequest.setParameters(requestBundle);
        graphRequest.executeAsync();
    }

    private void navigateToEmailVerification() {
        Intent emailVerifyIntent = new Intent(LoginActivity.this, OtpVerificationActivity.class);
        emailVerifyIntent.putExtra("activityName",  getString(R.string.is_from_login_activity));
        emailVerifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(emailVerifyIntent);
    }

    private void navigateToUpdateProfileDialogFragment() {
        if (infoFragmentDialog == null) {
            infoFragmentDialog = new UpdateInfoFragmentDialog();
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        infoFragmentDialog.show(fragmentTransaction, UpdateInfoFragmentDialog.TAG);
    }

    private void navigateToRecommendedCategorySelect() {
        Intent recommendedIntent = new Intent(LoginActivity.this, ChooseRecommendedCatActivity.class);
        recommendedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(recommendedIntent);
    }

    public void printHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("gethashcode", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {

        } catch (Exception e) {

        }
    }

    private void navigateToHome(){
        Intent intentHome = new Intent(LoginActivity.this,HomeActivity.class);
        intentHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentHome);
        finish();
    }

}
