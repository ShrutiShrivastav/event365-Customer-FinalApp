package com.ebabu.event365live.stripe

import com.ebabu.event365live.httprequest.APIs
import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*

interface BackendApi {

    @POST("GetEphemeralKey")
    fun createEphemeralKey(@Header(APIs.AUTHORIZATION) auth: String ,  @Body json: JsonObject): Observable<ResponseBody>
}