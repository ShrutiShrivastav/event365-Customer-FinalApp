package com.ebabu.event365live.auth.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;

import com.ebabu.event365live.BaseActivity;
import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityLoginBinding;
import com.ebabu.event365live.databinding.DialogLoginAttemptsBinding;
import com.ebabu.event365live.homedrawer.activity.ChooseRecommendedCatActivity;
import com.ebabu.event365live.homedrawer.activity.ContactUsActivity;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.oncelaunch.LandingActivity;
import com.ebabu.event365live.userinfo.fragment.UpdateInfoFragmentDialog;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.SessionValidation;
import com.ebabu.event365live.utils.ShowToast;
import com.ebabu.event365live.utils.ValidationUtil;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Enumeration;

import retrofit2.Call;

public class LoginActivity extends BaseActivity implements GetResponseData {

    private ActivityLoginBinding loginBinding;
    private boolean isClickFirstTime;
    private GoogleSignInClient mGoogleSignInClint;
    private int RC_SIGN_IN_REQUEST = 1001;
    private CallbackManager callbackManager;
    private UpdateInfoFragmentDialog infoFragmentDialog;
    private String getUserName, getUserEmail, getSocialImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("apple.com");
//        List<String> scopes =
//                new ArrayList<String>() {
//                    {
//                        add("email");
//                        add("name");
//                    }
//                };
//        provider.setScopes(scopes);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        loginBinding.ivAppleIcon.setOnClickListener(view -> {
            mAuth.startActivityForSignInWithProvider(this, provider.build())
                    .addOnSuccessListener(
                            authResult -> {
                                Log.d("TAG", "activitySignIn:onSuccess:" + authResult.getUser());
                                FirebaseUser user = authResult.getUser();
                                String name = user.getDisplayName();
                                String email = user.getEmail();
                                String id = user.getProviderId();
                                socialLoginRequest(name, email, id, "apple");
                            })
                    .addOnFailureListener(
                            e -> Log.w("TAG", "activitySignIn:onFailure", e));
        });
        printHashKey();
        getDeviceId();
        NetworkDetect();
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
                if (charSequence.length() > 0 && !charSequence.toString().contains(" ") && !charSequence.toString().endsWith(" ")) {
                    if (ValidationUtil.passwordValidatorWithoutToast(LoginActivity.this, charSequence.toString()))
                        loginBinding.ivShowPassTick.setVisibility(View.VISIBLE);
                    else
                        loginBinding.ivShowPassTick.setVisibility(View.INVISIBLE);
                    loginBinding.ivShowHidePass.setVisibility(View.VISIBLE);
                } else {
                    if (charSequence.length() > 0) {
                        loginBinding.ivShowHidePass.setVisibility(View.VISIBLE);
                    } else {
                        loginBinding.ivShowHidePass.setVisibility(View.INVISIBLE);
                    }
                    loginBinding.ivShowPassTick.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        facebookSignIn();
    }

    private void getDeviceId() {
        String device_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("Android", "Android ID : " + device_id);
        SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.deviceId, device_id);
    }

    //Check the internet connection.
    private void NetworkDetect() {
        boolean WIFI = false;
        boolean MOBILE = false;
        ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = CM.getAllNetworkInfo();
        for (NetworkInfo netInfo : networkInfo) {
            if (netInfo.getTypeName().equalsIgnoreCase("WIFI"))
                if (netInfo.isConnected())
                    WIFI = true;
            if (netInfo.getTypeName().equalsIgnoreCase("MOBILE"))
                if (netInfo.isConnected())
                    MOBILE = true;
        }
        if (WIFI) {
            String IPaddress = GetDeviceipWiFiData();
            SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.sourceIp, IPaddress);
        }
        if (MOBILE) {
            String IPaddress = GetDeviceipMobileData();
            SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.sourceIp, IPaddress);
        }

    }

    public String GetDeviceipMobileData() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements(); ) {
                NetworkInterface networkinterface = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = networkinterface.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("Current IP", ex.toString());
        }
        return null;
    }

    public String GetDeviceipWiFiData() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }

    public void forgotPasswordOnClick(View view) {
        startActivity(new Intent(this, ForgotPassActivity.class));
    }

    public void loginOnClick(View view) {
        String userEmail = loginBinding.etEnterEmail.getText().toString();
        String userPass = loginBinding.etEnterPass.getText().toString();

        if (!ValidationUtil.emailValidator(LoginActivity.this, userEmail))
            return;
        else if (!ValidationUtil.passwordValidator(LoginActivity.this, userPass))
            return;

        userLoginRequest(userEmail, userPass);
    }

    public void gMailLoginOnClick(View view) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        mGoogleSignInClint = GoogleSignIn.getClient(this, gso);

        Intent googleSignInIntent = mGoogleSignInClint.getSignInIntent();
        startActivityForResult(googleSignInIntent, RC_SIGN_IN_REQUEST);
    }

    public void fbLoginOnClick(View view) {
        //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile", "user_friends"));
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
    }

    public void backBtnOnClick(View view) {
        finish();
    }

    public void registerOnClick(View view) {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        registerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(registerIntent);
    }

    public void showHidePassOnClick(View view) {
        if (loginBinding.ivShowHidePass.isShown() && !isClickFirstTime) {
            loginBinding.ivShowHidePass.setImageDrawable(getResources().getDrawable(R.drawable.hide_pass_icon));
            loginBinding.etEnterPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            loginBinding.etEnterPass.setSelection(loginBinding.etEnterPass.length());
            isClickFirstTime = true;
        } else if (loginBinding.ivShowHidePass.isShown() && isClickFirstTime) {
            loginBinding.ivShowHidePass.setImageDrawable(getResources().getDrawable(R.drawable.unselect_pass_icon));
            loginBinding.etEnterPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            loginBinding.etEnterPass.setSelection(loginBinding.etEnterPass.length());
            isClickFirstTime = false;
        }
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        if (responseObj != null) {
            //myLoader.dismiss();
            /*swipe event slider should be show in swipe view by default*/

            /* Registering AppLogiz*/
            try {
                String userId = responseObj.getJSONObject("data").getString("id");
                String name = responseObj.getJSONObject("data").getString("name");
                getSocialImg = responseObj.getJSONObject("data").getString("profilePic");
                boolean isRemind = responseObj.getJSONObject("data").getBoolean("isRemind");
                boolean isNotify = responseObj.getJSONObject("data").getBoolean("isNotify");
                String customerId = responseObj.getJSONObject("data").getString("customerId");
                if (getSocialImg != null) {
                    SessionValidation.getPrefsHelper().savePref(Constants.SharedKeyName.profilePic, getSocialImg);
                }
                CommonUtils.getCommonUtilsInstance().appLozicRegister(this, userId, name);
                CommonUtils.getCommonUtilsInstance().getAppLozicListener(new CommonUtils.AppLozicListener() {
                    @Override
                    public void appLozicOnSuccess() {
                        CommonUtils.getCommonUtilsInstance().validateUser(userId, name, isRemind, isNotify, customerId);

                        if (getCallingActivity() != null) {
                            if (getCallingActivity().getClassName().equalsIgnoreCase("com.ebabu.event365live.userinfo.activity.EventDetailsActivity")) {
                                backToActivityResultIntent();
                            } else if (getCallingActivity().getClassName().equalsIgnoreCase("com.ebabu.event365live.homedrawer.activity.ContactUsActivity")) {
                                backToActivityResultIntent();
                            } else if (getCallingActivity().getClassName().equalsIgnoreCase("com.ebabu.event365live.userinfo.activity.HostProfileActivity")) {
                                backToActivityResultIntent();
                            } else if (getCallingActivity().getClassName().equalsIgnoreCase("com.ebabu.event365live.home.activity.HomeActivity")) {
                                backToActivityResultIntent();
                            } else if (getCallingActivity().getClassName().equalsIgnoreCase("com.ebabu.event365live.ticketbuy.SelectTicketActivity")) {
                                backToActivityResultIntent();
                            } else if (getCallingActivity().getClassName().equalsIgnoreCase("com.ebabu.event365live.oncelaunch.LandingActivity")) {
                                Intent intent = new Intent();
                                setResult(Activity.RESULT_OK, intent);
                                finish();
                            }

                        } else
                            navigateToLanding();
                        myLoader.dismiss();
                    }

                    @Override
                    public void appLozicOnFailure() {
                        myLoader.dismiss();
                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("fasnflksan", "onSuccess: " + e.getMessage());
            }
        }
    }

    private void navigateToLanding() {
        Intent intentHome = new Intent(LoginActivity.this, LandingActivity.class);
        intentHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentHome);
        finish();
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {

        myLoader.dismiss();
        if (errorBody != null) {
            CommonUtils.getCommonUtilsInstance().validateUserIdFromErrorResponse(errorBody);
            if (errorCode == APIs.EMAIL_NOT_VERIFIED) {
                navigateToEmailVerification();
                ShowToast.infoToast(LoginActivity.this, message);
            } else if (errorCode == APIs.NEED_PROFILE_UPDATE) {
//            } else if (errorCode == APIs.NEED_PROFILE_UPDATE || errorCode == APIs.PHONE_NO_VERIFIED) {
                String msg = "";
                try {
                    JSONObject object = errorBody.getJSONObject("data");
                    String userName = object.getString("name");
                    String userEmail = object.getString("email");
                    msg = errorBody.getString("message");
                    getUserName = userName;
                    getUserEmail = userEmail;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ShowToast.infoToast(LoginActivity.this, msg);
                navigateToUpdateProfileDialogFragment();
            } else if (errorCode == APIs.PHONE_OTP_REQUEST) {
                ShowToast.infoToast(LoginActivity.this, message);
            } else if (errorCode == APIs.CHOOSE_RECOMMENDED_CATEGORY) {
                ShowToast.infoToast(LoginActivity.this, message);
                navigateToRecommendedCategorySelect();
            } else if (errorCode == APIs.OTHER_FAILED) {
                ShowToast.infoToast(LoginActivity.this, message);
                getSocialImg = null;
            } else if (errorCode == APIs.LOGIN_ATTEMPTS_FAILED) {
                loginAttemptsDialog();
            } else if (errorCode == APIs.BLOCK_FAILED) {
                loginAttemptsDialog();
            }
        }
    }

    private void userLoginRequest(String getUserEmail, String getUserPass) {
        myLoader.show("Please Wait...");
        Log.d("flanklfnklansl", SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.deviceToken) + " userLoginRequest: " + SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.deviceType));
        JsonObject userLoginObj = new JsonObject();
        userLoginObj.addProperty(Constants.ApiKeyName.email, getUserEmail);
        userLoginObj.addProperty(Constants.ApiKeyName.password, getUserPass);
        userLoginObj.addProperty(Constants.SharedKeyName.deviceToken, SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.deviceToken) == null ? FirebaseInstanceId.getInstance().getToken() : SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.deviceToken).toString());
        userLoginObj.addProperty(Constants.SharedKeyName.deviceType, SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.deviceType).toString());
        userLoginObj.addProperty("OS", "android");
        userLoginObj.addProperty("platform", "playstore");
        userLoginObj.addProperty("deviceId", SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.deviceId).toString());
        userLoginObj.addProperty("sourceIp", SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.sourceIp).toString());
        Call<JsonElement> getLoginUserObj = APICall.getApiInterface().login(userLoginObj);
        new APICall(LoginActivity.this).apiCalling(getLoginUserObj, this, APIs.LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN_REQUEST) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {

        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                String name = account.getDisplayName();
                String email = account.getEmail();
                String id = account.getId();
                if (account.getPhotoUrl() != null)
                    getSocialImg = account.getPhotoUrl().toString();
                socialLoginRequest(name, email, id, "google");
            }
        } catch (ApiException e) {
            e.printStackTrace();
            Log.d("bakbfjbafa", "ApiException: " + e.getMessage());
        }
    }

    private void navigateToEmailVerification() {
        Intent emailVerifyIntent = new Intent(LoginActivity.this, OtpVerificationActivity.class);
        emailVerifyIntent.putExtra("activityName", getString(R.string.is_from_login_activity));
        emailVerifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(emailVerifyIntent);
    }

    private void navigateToUpdateProfileDialogFragment() {
        if (infoFragmentDialog == null) {
            infoFragmentDialog = new UpdateInfoFragmentDialog();
        }
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SharedKeyName.userName, getUserName);
        bundle.putString(Constants.SharedKeyName.userEmail, getUserEmail);
        infoFragmentDialog.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
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

    private void socialLoginRequest(String name, String socialEmail, String socialId, String socialLoginType) {
        myLoader.show("Please Wait...");
        JsonObject userLoginObj = new JsonObject();
        userLoginObj.addProperty(Constants.ApiKeyName.name, name);
        userLoginObj.addProperty(Constants.ApiKeyName.email, socialEmail);
        userLoginObj.addProperty(Constants.ApiKeyName.socialUserId, socialId);
        userLoginObj.addProperty(Constants.ApiKeyName.socialLoginType, socialLoginType);
        userLoginObj.addProperty(Constants.SharedKeyName.deviceToken, SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.deviceToken) == null ? FirebaseInstanceId.getInstance().getToken() : SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.deviceToken).toString());
        userLoginObj.addProperty(Constants.SharedKeyName.deviceType, SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.deviceType).toString());
        userLoginObj.addProperty("platform", "playstore");
        userLoginObj.addProperty("deviceId", SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.deviceId).toString());
        userLoginObj.addProperty("sourceIp", SessionValidation.getPrefsHelper().getPref(Constants.SharedKeyName.sourceIp).toString());

        Call<JsonElement> getLoginUserObj = APICall.getApiInterface().socialLogin(userLoginObj);
        new APICall(LoginActivity.this).apiCalling(getLoginUserObj, this, APIs.SOCIAL_LOGIN);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonUtils.getCommonUtilsInstance().googleLogout(LoginActivity.this);

    }

    private void facebookSignIn() {
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        try {
                                            String userFirstName = object.getString("name");
                                            String fbUserEmail = object.getString("email"); /* consider email ID as a facebook id or provider ID*/
                                            String fbUserId = object.getString("id");
                                            getSocialImg = "https://graph.facebook.com/" + fbUserId + "/picture?type=normal";
                                            String fbUserName = "D Raj Pandey";
                                            //  fbUserName = fbUserName.matches("[a-zA-Z.? ]*") ? fbUserName : "";
                                            Log.d("fnaklsfnlkanflsa", fbUserEmail + " fb: " + fbUserName);

                                            socialLoginRequest(userFirstName, fbUserEmail, fbUserId, "facebook");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Log.d("bfjkanflanl", "JSONException: " + e.getMessage());
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,link,gender,birthday,email");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        ShowToast.infoToast(LoginActivity.this, "Login Cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        ShowToast.infoToastWrong(LoginActivity.this);
                        Log.d("fnbaslnfklsa", "onError: " + exception.toString());
                    }
                });


    }


    private void backToActivityResultIntent() {
        Intent intent = new Intent();
        setResult(1005, intent);
        finish();
    }


    public void loginAttemptsDialog() {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        DialogLoginAttemptsBinding dialogLogoutBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_login_attempts, null, false);
        builder.setView(dialogLogoutBinding.getRoot());

        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();

        dialogLogoutBinding.ivCross.setOnClickListener(view -> dialog.dismiss());

        dialogLogoutBinding.loaderBtn.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(LoginActivity.this, ContactUsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

}
