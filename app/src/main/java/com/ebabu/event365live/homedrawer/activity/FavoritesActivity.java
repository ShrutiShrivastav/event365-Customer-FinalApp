package com.ebabu.event365live.homedrawer.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ebabu.event365live.BaseActivity;
import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityFavoritesBinding;
import com.ebabu.event365live.homedrawer.adapter.FavoritesAdapter;
import com.ebabu.event365live.homedrawer.modal.pastmodal.FavoritesEventListModal;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.listener.MarkAsFavoriteEventListener;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.ShowToast;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;


//public class FavoritesActivity extends AppCompatActivity implements GetResponseData, LikeDislikeListener {

public class FavoritesActivity extends BaseActivity implements GetResponseData, MarkAsFavoriteEventListener {

    private ActivityFavoritesBinding favoritesBinding;
    private FavoritesEventListModal favoritesEventListModal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        favoritesBinding = DataBindingUtil.setContentView(this,R.layout.activity_favorites);
        favoritesPastRequest();
    }

    private void setupFavoritesList() {
        favoritesBinding.favoritesViewpager.setAdapter(new FavoritesAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,favoritesEventListModal));
        favoritesBinding.tlFavorites.setupWithViewPager(favoritesBinding.favoritesViewpager);
    }

    public void backBtnOnClick(View view) {
        finish();
    }

    private void favoritesPastRequest(){
        myLoader.show("Loading...");

        Call<JsonElement> favObj = APICall.getApiInterface().getFavoritesList(CommonUtils.getCommonUtilsInstance().getDeviceAuth());
        new APICall(FavoritesActivity.this).apiCalling(favObj,this,APIs.GET_FAVORITES_LIST);
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        Log.d("fnkanfklasa", "onSuccess: favorites "+responseObj);
        if(responseObj != null){
            if(typeAPI.equalsIgnoreCase(APIs.MARK_FAVORITES_EVENT)){
                    ShowToast.successToast(FavoritesActivity.this,"Removed from Favorite");
                return;
            }

            favoritesEventListModal = new Gson().fromJson(responseObj.toString(), FavoritesEventListModal.class);
            setupFavoritesList();
        }
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        Log.d("fnkanfklasa", "onFailed: favorites "+errorBody);
        ShowToast.errorToast(FavoritesActivity.this,message);
    }

//    private void likeEventRequest(String eventId){

    private void likeEventRequest(Integer eventId){
        myLoader.show("");
        JsonObject obj = new JsonObject();
        obj.addProperty(Constants.ApiKeyName.eventId,eventId);
        obj.addProperty(Constants.ApiKeyName.isFavorite,false);
        Call<JsonElement> likeEventCall = APICall.getApiInterface().likeEvent(CommonUtils.getCommonUtilsInstance().getDeviceAuth(),obj);
        new APICall(FavoritesActivity.this).apiCalling(likeEventCall,this,APIs.MARK_FAVORITES_EVENT);
    }

    public void eventFavMarkListener(Integer eventId) {
        if(eventId != null){
            Log.d("fnklanfklnas", "eventFavMarkListener: "+eventId);
            likeEventRequest(eventId);

        }
    }
}
