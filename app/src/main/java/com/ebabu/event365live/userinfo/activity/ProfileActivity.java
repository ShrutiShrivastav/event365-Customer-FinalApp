package com.ebabu.event365live.userinfo.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.auth.activity.OtpVerificationActivity;
import com.ebabu.event365live.databinding.ActivityProfileBinding;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.userinfo.modal.userdetails.GetUserDetailsModal;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.FileUtil;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.ShowToast;
import com.ebabu.event365live.utils.ValidationUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hbb20.CountryCodePicker;
import com.kienht.bubblepicker.Constant;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.PartMap;

public class ProfileActivity extends AppCompatActivity implements GetResponseData, View.OnClickListener {
    private File getGalleryImgFile;
    private ActivityProfileBinding profileBinding;
    private MyLoader myLoader;
    private static int PICK_FROM_GALLERY = 1001;
    private static int PICK_FROM_CAMERA = 9001;
    Uri outputFileUri;
    private LatLng currentLatLng;
    private String getCountryCode;
    private MultipartBody.Part mPartProfile;
    private RequestBody mRBProfile;
    private boolean isProfilePicSelected;
    private Uri resultUri;
    private String getLat, getLng;
    private boolean isEnteredNoValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileBinding = DataBindingUtil.setContentView(this,R.layout.activity_profile);
        profileBinding.countryCodePicker.registerCarrierNumberEditText(profileBinding.etEnterMobile);
        profileBinding.etEnterAdd.setOnClickListener(this);

        profileBinding.countryCodePicker.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                if (isValidNumber) {
                    profileBinding.ivShowMobileTick.setVisibility(View.VISIBLE);
                    isEnteredNoValid = isValidNumber;
                } else {
                    profileBinding.ivShowMobileTick.setVisibility(View.INVISIBLE);
                    isEnteredNoValid = isValidNumber;
                }
            }
        });

        myLoader = new MyLoader(this);
        getUserDetailsRequest();
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_FROM_GALLERY);
    }

    private void openCamera(){
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    public void changePicOnClick(View view) {
        openCropper(CropImageView.CropShape.RECTANGLE, 1, 1);
    }

    public void updateProfileOnClick(View view) {
        String getUserName = profileBinding.etEnterName.getText().toString();
        String getUserEmail = profileBinding.etEnterEmail.getText().toString();
        String getUserPhone = profileBinding.etEnterMobile.getText().toString();
        String getUserURL = profileBinding.etEnterUrl.getText().toString();
        String getUserInfo = profileBinding.etEnterInfo.getText().toString();
        String getUserAdd = profileBinding.etEnterAdd.getText().toString();
        String getUserCity = profileBinding.etEnterCity.getText().toString();
        String getUserState = profileBinding.etEnterState.getText().toString();
        String getUserZip = profileBinding.etEnterZip.getText().toString();
        getCountryCode = profileBinding.countryCodePicker.getSelectedCountryCodeWithPlus();


        if(!ValidationUtil.validateName(ProfileActivity.this,getUserName)){
            profileBinding.etEnterName.requestFocus();
            return;
        }
        else if(!ValidationUtil.emailValidator(ProfileActivity.this,getUserEmail)){
            profileBinding.etEnterEmail.requestFocus();
            return;
        }else if(!isEnteredNoValid) {
            profileBinding.etEnterMobile.requestFocus();
            ShowToast.errorToast(ProfileActivity.this,getString(R.string.error_please_enter_valid_no));
            return;
        } else if(!Patterns.WEB_URL.matcher(getUserURL.toString()).matches()) {
            ShowToast.errorToast(ProfileActivity.this,getString(R.string.not_valid_url));
            profileBinding.etEnterUrl.requestFocus();
            return;
        }
        else if(getUserInfo.length() == 0){
            ShowToast.errorToast(ProfileActivity.this,getString(R.string.not_valid_info));
            profileBinding.etEnterInfo.requestFocus();
            return;
        }
        else if(getUserAdd.length() == 0){
            ShowToast.errorToast(ProfileActivity.this,getString(R.string.enter_your_add));
            profileBinding.etEnterAdd.requestFocus();
            return;
        }
        else if(getUserCity.length() == 0){
            ShowToast.errorToast(ProfileActivity.this,getString(R.string.enter_your_city));
            profileBinding.etEnterCity.requestFocus();
            return;
        }
        else if(getUserState.length() == 0){
            ShowToast.errorToast(ProfileActivity.this,getString(R.string.enter_your_state));
            profileBinding.etEnterState.requestFocus();
            return;
        }
        else if(getUserZip.length() == 0){
            ShowToast.errorToast(ProfileActivity.this,getString(R.string.enter_your_zip));
            profileBinding.etEnterZip.requestFocus();
            return;
        }else if(!isProfilePicSelected){
            ShowToast.errorToast(ProfileActivity.this,getString(R.string.please_select_img));
            return;
        }

        updateProfileRequest(getUserName,getUserEmail,getUserPhone,getUserURL,
                getUserInfo,getUserAdd,getUserCity,getUserState,getUserZip);
    }

        private void updateProfileRequest(String getUserName,String getUserEmail,String getUserPhone,String getUserURL,
        String getUserInfo,String getUserAdd,String getUserCity,String getUserState,String getUserZip) {
            myLoader.show("Updating...");


            Map<String, RequestBody> requestBodyMap = new HashMap<>();

            requestBodyMap.put(Constants.ApiKeyName.name, getRequestBody(getUserName));
            requestBodyMap.put(Constants.ApiKeyName.state, getRequestBody(getUserState));
            requestBodyMap.put(Constants.ApiKeyName.countryCode, getRequestBody(getCountryCode));
            requestBodyMap.put(Constants.ApiKeyName.zip, getRequestBody(getUserZip));
            requestBodyMap.put(Constants.ApiKeyName.url, getRequestBody(getUserURL));
            requestBodyMap.put(Constants.ApiKeyName.shortInfo, getRequestBody(getUserInfo));
            requestBodyMap.put(Constants.ApiKeyName.city, getRequestBody(getUserCity));
            requestBodyMap.put(Constants.ApiKeyName.latitude, getRequestBody(getLat));
            requestBodyMap.put(Constants.ApiKeyName.longitude, getRequestBody(getLng));
            requestBodyMap.put(Constants.ApiKeyName.phoneNo, getRequestBody(getCountryCode+getUserPhone));


            Call<JsonElement> updateObj = null;
            if(resultUri != null){
                File file = new File(resultUri.getPath());
                MultipartBody.Part
                        filePart = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
                updateObj = APICall.getApiInterface().updateProfile(CommonUtils.getCommonUtilsInstance().getDeviceAuth(),requestBodyMap,filePart);
            }
            updateObj = APICall.getApiInterface().updateProfile(CommonUtils.getCommonUtilsInstance().getDeviceAuth(),requestBodyMap,null);
            new APICall(ProfileActivity.this).apiCalling(updateObj,this, APIs.UPDATE_PROFILE);
        }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        profileBinding.profileContainer.setVisibility(View.VISIBLE);
        if (responseObj != null) {

            Log.d("anfklnaklfnlas", "onSuccess: "+responseObj);

            if (typeAPI.equalsIgnoreCase(APIs.GET_USER_DETAILS)){
                GetUserDetailsModal detailsModal = new Gson().fromJson(responseObj.toString(), GetUserDetailsModal.class);
                GetUserDetailsModal.UserDetailsData userDetailsData = detailsModal.getData().get(0);


                if(userDetailsData.getProfilePic() != null){
                    Glide.with(ProfileActivity.this).load(userDetailsData.getProfilePic()).into(profileBinding.ivShowUserImg);
                    isProfilePicSelected = true;
                }if(userDetailsData.getName() != null){
                    profileBinding.etEnterName.setText(userDetailsData.getName());
                    profileBinding.tvShowUserName.setText(userDetailsData.getName());
                }
                if(userDetailsData.getEmail() != null){
                    profileBinding.etEnterEmail.setText(userDetailsData.getEmail());
                }if(userDetailsData.getPhoneNo() != null){
                    profileBinding.etEnterMobile.setText(userDetailsData.getPhoneNo());
                }if(userDetailsData.getCountryCode() != null){
                    profileBinding.countryCodePicker.setCountryForPhoneCode(userDetailsData.getCountryCode());
                    profileBinding.etEnterCountry.setText(profileBinding.countryCodePicker.getSelectedCountryName());
                }if(userDetailsData.getURL() != null){
                    profileBinding.etEnterUrl.setText(userDetailsData.getURL());
                }if(userDetailsData.getShortInfo() != null){
                    profileBinding.etEnterInfo.setText(userDetailsData.getShortInfo());
                }if(userDetailsData.getState() != null){
                    profileBinding.etEnterState.setText(userDetailsData.getState());
                }if(userDetailsData.getCity() != null){
                    profileBinding.etEnterCity.setText(userDetailsData.getCity());
                }if(userDetailsData.getZip() != null){
                    profileBinding.etEnterZip.setText(userDetailsData.getZip());
                    profileBinding.etEnterZip.setEnabled(false);
                }if(userDetailsData.getLatitude() != null && userDetailsData.getLongitude() != null){
                    getLat = userDetailsData.getLatitude();
                    getLng = userDetailsData.getLongitude();
                    String add = CommonUtils.getCommonUtilsInstance().getAddressFromLatLng(ProfileActivity.this, getLat, getLng);
                    profileBinding.tvShowUserAdd.setText(add);
                    profileBinding.etEnterAdd.setText(add);
                }
                profileBinding.etEnterCountry.setEnabled(false);
                profileBinding.etEnterState.setEnabled(false);
                profileBinding.etEnterCity.setEnabled(false);

            }else if(typeAPI.equalsIgnoreCase(APIs.UPDATE_PROFILE)){
                ShowToast.infoToast(ProfileActivity.this,message);
                //navigateToOTPVerification();
            }
        }
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        ShowToast.errorToast(ProfileActivity.this,message);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (result != null && result.getUri() != null) {
                        isProfilePicSelected = true;
                        resultUri = result.getUri();
                        Glide.with(ProfileActivity.this).load(resultUri).into(profileBinding.ivShowUserImg);
                    }
                }else  if (requestCode == Constants.AUTOCOMPLETE_REQUEST_CODE) {
                    CommonUtils.hideSoftKeyboard(ProfileActivity.this);
                    Place place = Autocomplete.getPlaceFromIntent(data);
                    currentLatLng = place.getLatLng();
                    Geocoder geocoder = new Geocoder(ProfileActivity.this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(currentLatLng.latitude, currentLatLng.longitude, 1);
                        getLat = String.valueOf(currentLatLng.latitude);
                        getLng = String.valueOf(currentLatLng.longitude);
                        String stateName = addresses.get(0).getAdminArea();
                        String cityName = addresses.get(0).getLocality();
                        String countryName = addresses.get(0).getCountryName();
                        String countryCode = addresses.get(0).getCountryCode();
                        String postalCode = addresses.get(0).getPostalCode();
                        profileBinding.countryCodePicker.setCountryForNameCode(countryCode);
                        profileBinding.etEnterAdd.setText(place.getName());
                        profileBinding.etEnterAdd.setSelection(place.getName().length());
                        profileBinding.etEnterCountry.setText(countryName);
                        profileBinding.etEnterCountry.setEnabled(false);
                        profileBinding.etEnterState.setText(stateName);
                        profileBinding.etEnterState.setEnabled(false);
                        profileBinding.etEnterCity.setText(cityName);
                        profileBinding.etEnterCity.setEnabled(false);
                        profileBinding.etEnterZip.setText(postalCode);
                        profileBinding.etEnterZip.setEnabled(false);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if(requestCode == Constants.MOBILE_VERIFY_REQUEST_CODE){
                    if(data != null && data.getExtras() != null){
                        boolean getIsMobileVerified = data.getExtras().getBoolean(Constants.SharedKeyName.isMobileVerified);
                        if(getIsMobileVerified){
                            ShowToast.successToast(ProfileActivity.this,getString(R.string.mobile_verified));
                            profileBinding.etEnterMobile.setEnabled(false);
                        }
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        }else if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            openCamera();
        }
    }

    public void backBtnOnClick(View view) {
        finish();
    }

    private void showImageChooserPopUp(){
        PopupMenu popup = new PopupMenu(ProfileActivity.this, profileBinding.ivChangeUserImg);
        popup.getMenuInflater().inflate(R.menu.choose_image_source_popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getItemId())
                {
                    case R.id.gallery:
                        if (ActivityCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) ProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
                        }
                        else {
                            openGallery(); }
                        break;
                    case R.id.camera:

                        if (ActivityCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) ProfileActivity.this, new String[]{Manifest.permission.CAMERA}, PICK_FROM_CAMERA);
                        }
                        else {
                            openCamera();
                            }
                        break;
                }
                return false;
            }
        });

        popup.show();
    }

    private static Bitmap rotateImage(Bitmap img, int degree){
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg; }


    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null && !extension.equals("")) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        } else if (url.lastIndexOf(".") != -1) {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        } else {
            type = null;
        }

        return type;
    }
    public static RequestBody getRequestBody(String value) {
        return RequestBody.create(okhttp3.MediaType.parse(Constants.TXT_PLAIN), value);
    }

    private void getUserDetailsRequest(){
        profileBinding.profileContainer.setVisibility(View.GONE);
        myLoader.show("");
        Call<JsonElement> getUserDetailsCall = APICall.getApiInterface().getUserDetailsInfo(CommonUtils.getCommonUtilsInstance().getDeviceAuth());
        new APICall(ProfileActivity.this).apiCalling(getUserDetailsCall,this,APIs.GET_USER_DETAILS);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.etEnterAdd) {
            CommonUtils.getCommonUtilsInstance().launchSelectAddressFrag(ProfileActivity.this, null, false);
        }
    }
    public void openCropper(CropImageView.CropShape rectangle, int x, int y) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(rectangle)
                .setAspectRatio(x, y)
                .setFixAspectRatio(true)
                .start(this);
    }
    private void navigateToOTPVerification(){
        Intent loginIntent = new Intent(ProfileActivity.this, OtpVerificationActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        loginIntent.putExtra("activityName",getString(R.string.isFromProfileActivity));
        startActivityForResult(loginIntent,Constants.MOBILE_VERIFY_REQUEST_CODE);
    }
}
