package com.ebabu.event365live.httprequest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiInterface {

    @POST(APIs.SIGN_UP)
    Call<JsonElement> signUp(@Body JsonObject OBJ);

    @POST(APIs.EMAIL_OTP_VERIFY)
    Call<JsonElement> emailOtpVerify(@Body JsonObject OBJ);

    @POST(APIs.PHONE_OTP_VERIFY)
    Call<JsonElement> phoneOtpVerify(@Body JsonObject OBJ);

    @POST(APIs.RESEND_OTP)
    Call<JsonElement> resendPhoneOtp(@Body JsonObject obj);

    @POST(APIs.RESET_PW)
    Call<JsonElement> resendOTP(@Body JsonObject OBJ);

    @POST(APIs.RESEND_EMAIL_CODE)
    Call<JsonElement> resendEmailCode(@Body JsonObject obj);

    @POST(APIs.LOGIN)
    Call<JsonElement> login(@Body JsonObject OBJ);

    @POST(APIs.SOCIAL_LOGIN)
    Call<JsonElement> socialLogin(@Body JsonObject OBJ);

    @POST(APIs.FORGET_PASSWORD)
    Call<JsonElement> forgetPassword(@Body JsonObject OBJ);

    @POST(APIs.RESET_PASSWORD)
    Call<JsonElement> resetPassword(@Body JsonObject OBJ);

    @POST(APIs.SEARCH_NO_AUTH_API)
    Call<JsonElement> searchNoAuth(@Query("limit") int limit, @Query("page") int page, @Body JsonObject obj);

    @POST(APIs.SEARCH_AUTH_API)
    Call<JsonElement> searchAuth(@Header(APIs.AUTHORIZATION) String auth, @Query("limit") int limit, @Query("page") int page, @Body JsonObject obj);

    @POST(APIs.NOTIFICATION_REMINDER)
    Call<JsonElement> notificationReminder(@Header(APIs.AUTHORIZATION) String token, @Body JsonObject obj);

    @POST(APIs.SUB_CATEGORY_BY_CAT_ID)
    Call<JsonElement> getSubCategoryByCatId(@Query("limit") int limit, @Query("offset") int page, @Body JsonObject object);

    @GET(APIs.GET_SEE_MORE_REVIEW_BY_CAT_ID)
    Call<JsonElement> getSeeMoreReviewByCatId(@Header(APIs.AUTHORIZATION) String token, @Path("categoryId") int eventId);

    @GET(APIs.GET_HOST_PROFILE_INFO)
    Call<JsonElement> getHostProfileInfo(@Path("hostId") int hostId);

    @POST(APIs.USER_TICKET_BOOKED)
    Call<JsonElement> userTicketBooked(@Header(APIs.AUTHORIZATION) String token, @Path("eventId") int eventId, @Body JsonArray obj);

    @POST(APIs.TICKET_PAYMENT_REQUEST)
    Call<JsonElement> ticketPaymentRequest(@Header(APIs.AUTHORIZATION) String token, @Body JsonObject obj);

    @POST(APIs.PAYMENT_CONFIRM)
    Call<JsonElement> paymentConfirm(@Header(APIs.AUTHORIZATION) String token, @Body JsonObject obj);

    @GET(APIs.GET_CONTACT_US_ISSUE)
    Call<JsonElement> getIssue();

    @POST(APIs.POST_CONTACT_US_ISSUE)
    Call<JsonElement> postIssue(@Header(APIs.AUTHORIZATION) String token, @Body JsonObject obj);

    @GET(APIs.GET_USER_TICKET_BOOKED)
    Call<JsonElement> getUserBookedTicket(@Header(APIs.AUTHORIZATION) String token);

    @PUT(APIs.MARK_FAVORITES_EVENT)
    Call<JsonElement> likeEvent(@Header(APIs.AUTHORIZATION) String token, @Body JsonObject obj);

    @GET(APIs.GET_USER_RSVP)
    Call<JsonElement> showUserRsvp(@Header(APIs.AUTHORIZATION) String token, @Query("limit") int limit, @Query("page") int page);

    @GET(APIs.NOTIFICATION_COUNT)
    Call<JsonElement> notificationCount(@Header(APIs.AUTHORIZATION) String token);

    @PUT(APIs.STATUS_RSVP)
    Observable<ResponseBody> statusRsvp(@Header(APIs.AUTHORIZATION) String token, @Body JsonObject obj);

    @POST(APIs.EventLikeOrDislike)
    Observable<ResponseBody> eventLikeDislike(@Header(APIs.AUTHORIZATION) String token, @Body JsonObject obj);

    @POST(APIs.USER_LOGOUT)
    Call<JsonElement> userLogout(@Header(APIs.AUTHORIZATION) String token);

    @GET(APIs.GET_POLICY)
    Call<JsonElement> getPolicy();

    @GET(APIs.GET_TERMS_CONDITION)
    Call<JsonElement> getTermsCondition();

    @GET(APIs.GET_EVENT_LIST_OF_UPCOMING_ATTEND)
    Call<JsonElement> getEventListOfUpcomingAttend(@Header(APIs.AUTHORIZATION) String token);

    @GET(APIs.GET_RECOMMENDED__AUTH)
    Call<JsonElement> getRecommendedAuth(@Header(APIs.AUTHORIZATION) String token, @Query("limit") int limit, @Query("page") int page);

    @GET(APIs.GET_ALL_NOTIFICATION_LIST)
    Call<JsonElement> getNotificationList(@Header(APIs.AUTHORIZATION) String token, @Query("limit") int limit, @Query("page") int page, @Query("notificationType") String notificationType, @Query("notificationTab") int notificationTab);

    @GET(APIs.GET_USER_DETAILS)
    Call<JsonElement> getUserDetailsInfo(@Header(APIs.AUTHORIZATION) String token);

    @GET(APIs.GET_EVENT_CATEGORY)
    Call<JsonElement> getEventCategory(@Header(APIs.AUTHORIZATION) String token);

    @POST(APIs.GET_SUB_CATEGORY_NO_AUTH)
    Call<JsonElement> getEventSubCategory(@Body JsonObject obj);

    @POST(APIs.CHOOSE_EVENT_CATEGORY)
    Call<JsonElement> chooseEventCategory(@Header(APIs.AUTHORIZATION) String token, @Body JsonArray obj);

    @GET(APIs.GET_CATEGORY)
    Call<JsonElement> getCategory();

    @POST(APIs.GET_SUB_CATEGORY_NO_AUTH)
    Call<JsonElement> getSubCategoryNoAuth(@Body JsonObject obj);

    @POST(APIs.NO_AUTH_NEAR_BY_EVENT)
    Call<JsonElement> noAuthNearByEvent(@Body JsonObject obj);

    @POST(APIs.NEAR_BY_AUTH_EVENT)
    Call<JsonElement> nearByWithAuthEvent(@Header(APIs.AUTHORIZATION) String token, @Body JsonObject obj);

    @POST(APIs.NO_AUTH_FEATURED_BY_EVENT)
    Call<JsonElement> noAuthFeatureByEvent(@Body JsonObject obj);

    @POST(APIs.FEATURED_BY_AUTH_EVENT)
    Call<JsonElement> FeatureByWithAuthEvent(@Header(APIs.AUTHORIZATION) String token, @Body JsonObject obj);

    @GET(APIs.GET_FAVORITES_LIST)
    Call<JsonElement> getFavoritesList(@Header(APIs.AUTHORIZATION) String token);

    @Multipart
    @POST(APIs.UPDATE_PROFILE)
    Call<JsonElement> updateProfile(@Header(APIs.AUTHORIZATION) String token, @PartMap Map<String, RequestBody> partMap, @Part MultipartBody.Part filePart);

    @GET(APIs.USER_EVENT_DETAILS_NO_AUTH)
    Call<JsonElement> getEventDetailsNoAuth(@Path("id") int eventId);

    @GET(APIs.USER_EVENT_DETAILS_AUTH)
    Call<JsonElement> getEventDetailsAuth(@Header(APIs.AUTHORIZATION) String token, @Path("id") int eventId);

    @GET(APIs.GET_TICKET_INFO)
    Call<JsonElement> getTicketInfo(@Header(APIs.AUTHORIZATION) String token, @Path("event_id") int eventId);

    @POST(APIs.CREATE_REVIEW)
    Call<JsonElement> createReview(@Header(APIs.AUTHORIZATION) String token, @Body JsonObject obj);

    @POST(APIs.CHANGE_PASSWORD)
    Call<JsonElement> changePassword(@Header(APIs.AUTHORIZATION) String token, @Body JsonObject OBJ);

    @PUT(APIs.USER_TICKET_CANCELLED)
    Call<JsonElement> cancelTicket(@Header(APIs.AUTHORIZATION) String token, @Body JsonObject OBJ);

    @GET(APIs.RELATED_EVENT)
    Call<JsonElement> getRelatedEvent(@Path("id") int eventId,@QueryMap Map<String, String> DATA);
}