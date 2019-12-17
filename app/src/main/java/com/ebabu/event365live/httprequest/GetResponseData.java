package com.ebabu.event365live.httprequest;

import org.json.JSONObject;

public interface GetResponseData {

    void onSuccess(JSONObject responseObj,String message, String typeAPI);
    void onFailed(JSONObject errorBody,String message,Integer errorCode, String typeAPI);
}
