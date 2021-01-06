package com.ebabu.event365live.httprequest

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface BackendApi {

    @FormUrlEncoded
    @POST("ephemeral_keys")
    fun createEphemeralKey(@FieldMap apiVersionMap: HashMap<String, String>): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("create_payment_intent")
    fun createPaymentIntent(@FieldMap params: MutableMap<String, Any>): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("create_setup_intent")
    fun createSetupIntent(@FieldMap params: HashMap<String, Any>): Observable<ResponseBody>
}