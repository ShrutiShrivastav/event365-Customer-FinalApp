package com.ebabu.event365live.home.modal;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ebabu.event365live.auth.modal.LoginModal;

public class LoginViewModal extends ViewModel {

    private LoginModal loginModal;
    private MutableLiveData<LoginModal> mProfile;
    
    public MutableLiveData<LoginModal> getLoginProfile(){
        if(mProfile == null)
        {
            mProfile = new MutableLiveData<>();
        }
        //  setLoginProfile();

        return mProfile;
    }

//    public void setLoginProfile(){
//
//        //loginProfile.setValue(modal);
//      //  Log.d("nlnlanfklnalksnfklsa", loginProfile.getValue().getEventListSwipe()+" setLoginProfile: "+loginProfile.getValue().getUserLogin());
//    }


}
