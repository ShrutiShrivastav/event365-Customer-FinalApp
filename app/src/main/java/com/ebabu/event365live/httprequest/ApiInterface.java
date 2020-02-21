package com.ebabu.event365live.httprequest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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

public interface ApiInterface{

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

        @POST(APIs.PHONE_SEND_OTP)
        Call<JsonElement> phoneSendOtp(@Body JsonObject obj);

        @POST(APIs.RESEND_EMAIL_CODE)
        Call<JsonElement> resendEmailCode(@Body JsonObject obj);

        @POST(APIs.LOGIN)
        Call<JsonElement> login(@Body JsonObject OBJ);

        @POST(APIs.SOCIAL_LOGIN)
        Call<JsonElement> socialLogin(@Body JsonObject OBJ);

        @POST(APIs.UPDATE_DATA)
        Call<JsonElement> updateData(@Body JsonObject OBJ);

        @POST(APIs.FORGET_PASSWORD)
        Call<JsonElement> forgetPassword(@Body JsonObject OBJ);

        @POST(APIs.FORGET_PASSWORD_VERIFY_OTP)
        Call<JsonElement> forgetPasswordVerifyOtp(@Body JsonObject OBJ);

        @POST(APIs.FORGET_PASSWORD_RESEND_OTP)
        Call<JsonElement> forgetPasswordResendOtp(@Body JsonObject OBJ);

        @POST(APIs.RESET_PASSWORD)
        Call<JsonElement> resetPassword(@Body JsonObject OBJ);

        @POST(APIs.SEARCH_NO_AUTH_API)
        Call<JsonElement> searchNoAuth(@Query("limit") int limit, @Query("page") int page, @Body JsonObject obj);

        @POST(APIs.SEARCH_AUTH_API)
        Call<JsonElement> searchAuth(@Header(APIs.AUTHORIZATION) String auth,@Query("limit") int limit, @Query("page") int page, @Body JsonObject obj);

        @GET(APIs.TOP_5_EVENT)
        Call<JsonElement> topFiveEvent(@Header(APIs.AUTHORIZATION) String token);

        @POST(APIs.NOTIFICATION_REMINDER)
        Call<JsonElement> notificationReminder (@Header(APIs.AUTHORIZATION) String token, @Body JsonObject obj);

        @GET(APIs.LANDER_SCREEN_NEAR_BY_EVENT)
        Call<JsonElement> getLanderScreenNearByEvent(@Query("limit") int limit, @Query("offset") int page);

        @POST(APIs.SUB_CATEGORY_BY_CAT_ID)
        Call<JsonElement> getSubCategoryByCatId(@Query("limit") int limit, @Query("offset") int page, @Body JsonObject object);

        @GET(APIs.GET_SEE_MORE_REVIEW_BY_CAT_ID)
        Call<JsonElement> getSeeMoreReviewByCatId(@Header(APIs.AUTHORIZATION) String token, @Path("categoryId") int eventId);

        @GET(APIs.GET_HOST_PROFILE_INFO)
        Call<JsonElement> getHostProfileInfo(@Path("hostId") int hostId);

        @POST(APIs.USER_TICKET_BOOKED)
        Call<JsonElement> userTicketBooked(@Header(APIs.AUTHORIZATION) String token, @Path("eventId")  int eventId , @Body JsonArray obj);

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

        @GET(APIs.GET_ALL_EVENT)
        Call<JsonElement> getAllEvent(@Header(APIs.AUTHORIZATION) String token);

        @PUT(APIs.MARK_FAVORITES_EVENT)
        Call<JsonElement> likeEvent(@Header(APIs.AUTHORIZATION) String token, @Body JsonObject obj);

        @GET(APIs.GET_USER_RSVP)
        Call<JsonElement> showUserRsvp(@Header(APIs.AUTHORIZATION) String token,@Query("limit") int limit, @Query("page") int page);

        @GET(APIs.NOTIFICATION_COUNT)
        Call<JsonElement> notificationCount(@Header(APIs.AUTHORIZATION) String token);

        @POST(APIs.GET_EPHEMERAL_KEY)
        Call<JsonElement> getEphemeralKey(@Header(APIs.AUTHORIZATION) String token, @Body JsonObject obj);


        @PUT(APIs.STATUS_RSVP)
        Call<JsonElement> statusRsvp(@Header(APIs.AUTHORIZATION) String token, @Body JsonObject obj);

        @POST(APIs.EventLikeOrDislike)
        Call<JsonElement> eventLikeDislike(@Header(APIs.AUTHORIZATION) String token, @Body JsonObject obj);

        @POST(APIs.USER_LOGOUT)
        Call<JsonElement> userLogout(@Header(APIs.AUTHORIZATION) String token);

        @GET(APIs.GET_POLICY)
        Call<JsonElement> getPolicy();

        @GET(APIs.GET_TERMS_CONDITION)
        Call<JsonElement> getTermsCondition();

        @GET(APIs.GET_EVENT_LIST_OF_UPCOMING_ATTEND)
        Call<JsonElement> getEventListOfUpcomingAttend(@Header(APIs.AUTHORIZATION) String token);

        @GET(APIs.GET_RECOMMENDED_WITHOUT_AUTH)
        Call<JsonElement> getRecommendedWithoutAuth(@Header(APIs.AUTHORIZATION) String token);

        @GET(APIs.GET_RECOMMENDED__AUTH)
        Call<JsonElement> getRecommendedAuth(@Header(APIs.AUTHORIZATION) String token,@Query("limit") int limit, @Query("page") int page);

        @GET(APIs.GET_ALL_NOTIFICATION_LIST)
        Call<JsonElement> getNotificationList(@Header(APIs.AUTHORIZATION) String token,@Query("limit") int limit, @Query("page") int page);


        @GET(APIs.GET_USER_DETAILS)
        Call<JsonElement> getUserDetailsInfo(@Header(APIs.AUTHORIZATION) String token);



        @GET(APIs.GET_EVENT_CATEGORY)
        Call<JsonElement> getEventCategory(@Header(APIs.AUTHORIZATION) String token);

        @POST(APIs.GET_SUB_CATEGORY_NO_AUTH)
        Call<JsonElement> getEventSubCategory(@Body JsonObject obj);

        @POST(APIs.CHOOSE_EVENT_CATEGORY)
        Call<JsonElement> chooseEventCategory(@Header(APIs.AUTHORIZATION) String token,@Body JsonArray obj);


        @GET(APIs.GET_ALL_RECOMMENDED_SUB_CATEGORY)
        Call<JsonElement> getAllRecommendedCategory();

        @GET(APIs.GET_CATEGORY)
        Call<JsonElement> getCategory();

        @POST(APIs.GET_SUB_CATEGORY_NO_AUTH)
        Call<JsonElement> getSubCategoryNoAuth(@Body JsonObject obj);

        @POST(APIs.NO_AUTH_NEAR_BY_EVENT)
        Call<JsonElement> noAuthNearByEvent(@Body JsonObject obj);

        @POST(APIs.NEAR_BY_AUTH_EVENT)
        Call<JsonElement> nearByWithAuthEvent(@Header(APIs.AUTHORIZATION) String token, @Body JsonObject obj);

        @GET(APIs.CATEGORY)
        Call<JsonElement> categoryList();

        @GET(APIs.EXPERIENCE)
        Call<JsonElement> experienceList();

        @GET(APIs.SKILL)
        Call<JsonElement> skillList(@QueryMap Map<String, Integer> map);


        @GET(APIs.GET_FAVORITES_LIST)
        Call<JsonElement> getFavoritesList(@Header(APIs.AUTHORIZATION) String token);


        @POST(APIs.RESEND_EMAIL_VERIFY)
        Call<JsonElement> resendEmailVerify(@Body JsonObject OBJ);






        @POST(APIs.UPDATE_DATA_TOKEN)
        Call<JsonElement> updateDataToken(@Body JsonObject OBJ, @Header(APIs.AUTHORIZATION) String token);

        @GET(APIs.COMPANY)
        Call<JsonElement> getCompanyList(@QueryMap Map<String, String> params);


        @POST(APIs.EDIT_PROFILE)
        Call<JsonElement> editProfile(@Header(APIs.AUTHORIZATION) String token, @Body JsonObject Obj);

        //    Call<JsonElement> updateProfile(@Header(APIs.AUTHORIZATION) String token, @Part MultipartBody.Part file , @PartMap Map<String, RequestBody> partMap);

        @Multipart
        @POST(APIs.UPDATE_PROFILE)
        Call<JsonElement> updateProfile(@Header(APIs.AUTHORIZATION) String token, @PartMap Map<String, RequestBody> partMap, @Part MultipartBody.Part filePart);

        @POST(APIs.UPDATE_MOBILE)
        Call<JsonElement> updateMobile(@Body JsonObject OBJ, @Header(APIs.AUTHORIZATION) String token);

        @POST(APIs.UPDATE_MOBILE_VERIFY)
        Call<JsonElement> updateMobileVerify(@Body JsonObject OBJ, @Header(APIs.AUTHORIZATION) String token);

        @GET(APIs.USER_EVENT_DETAILS_NO_AUTH)
        Call<JsonElement> getEventDetailsNoAuth(@Path("id") int eventId);

        @GET(APIs.USER_EVENT_DETAILS_AUTH)
        Call<JsonElement> getEventDetailsAuth(@Header(APIs.AUTHORIZATION) String token,@Path("id") int eventId);

        @GET(APIs.GET_TICKET_INFO)
        Call<JsonElement> getTicketInfo(@Header(APIs.AUTHORIZATION) String token, @Path("event_id") int eventId);

        @POST(APIs.CREATE_REVIEW)
        Call<JsonElement> createReview(@Header(APIs.AUTHORIZATION) String token, @Body JsonObject obj);

        @GET(APIs.RECOMMENDED_PROFESSIONAL)
        Call<JsonElement> recommendedProfessional(@Header(APIs.AUTHORIZATION) String token, @QueryMap Map<String, Object> map);

        @GET(APIs.TRENDING_PROFESSIONAL)
        Call<JsonElement> trendingProfessional(@Header(APIs.AUTHORIZATION) String token, @QueryMap Map<String, Object> map);

        @GET(APIs.GET_EXPERT_PROFILE)
        Call<JsonElement> getExpertProfile(@Header(APIs.AUTHORIZATION) String token, @QueryMap Map<String, Integer> map);

        @POST(APIs.FOLLOW)
        Call<JsonElement> follow(@Header(APIs.AUTHORIZATION) String token, @Body JsonObject OBJ);

        @GET(APIs.TRENDING_EXPERT)
        Call<JsonElement> trendingExpert(@Header(APIs.AUTHORIZATION) String token, @QueryMap Map<String, Object> map);

        @POST(APIs.BLOCK)
        Call<JsonElement> block(@Header(APIs.AUTHORIZATION) String token, @Body JsonObject OBJ);

        @GET(APIs.BLOCK_LIST)
        Call<JsonElement> blockList(@Header(APIs.AUTHORIZATION) String token, @QueryMap Map<String, Integer> map);

        @POST(APIs.REMOVE_PROFESSIONAL)
        Call<JsonElement> removeProfessional(@Header(APIs.AUTHORIZATION) String token, @Body JsonObject OBJ);

        @GET(APIs.QUESTION_EXPERT_LIST)
        Call<JsonElement> questionExpertlList(@Header(APIs.AUTHORIZATION) String token, @QueryMap Map<String, Object> map);

        @GET(APIs.QUESTION_PROFESSIONAL_LIST)
        Call<JsonElement> questionProfessionalList(@Header(APIs.AUTHORIZATION) String token, @QueryMap Map<String, Object> map);

        @POST(APIs.ASK_QUESTION)
        Call<JsonElement> askQuestion(@Header(APIs.AUTHORIZATION) String token, @Body JsonObject OBJ);

        @GET(APIs.FOLLOWING_LIST)
        Call<JsonElement> followingList(@Header(APIs.AUTHORIZATION) String token, @QueryMap Map<String, Integer> map);

        @GET(APIs.FOLLOWER_LIST)
        Call<JsonElement> followerList(@Header(APIs.AUTHORIZATION) String token, @QueryMap Map<String, Integer> map);

        @GET(APIs.ASKED_QUESTION_LIST)
        Call<JsonElement> getQuestionListPro(@Header(APIs.AUTHORIZATION) String token, @QueryMap Map<String, Integer> map);

        @GET(APIs.GET_ANSWER_LIST)
        Call<JsonElement> getAnswerList(@Header(APIs.AUTHORIZATION) String token, @QueryMap Map<String, Integer> map);

        @GET(APIs.GET_QUESTION_LIST)
        Call<JsonElement> getQuestionListExp(@Header(APIs.AUTHORIZATION) String token, @QueryMap Map<String, Integer> map);

        @POST(APIs.ANSWER_TO_PROFESSIONAL)
        Call<JsonElement> answerToProfessional(@Header(APIs.AUTHORIZATION) String token, @Body JsonObject OBJ);

        @POST(APIs.COMMENT_ON_ANSWER)
        Call<JsonElement> commentOnAnswer(@Header(APIs.AUTHORIZATION) String token, @Body JsonObject OBJ);

        @POST(APIs.EDIT_QUESTION)
        Call<JsonElement> editQuestion(@Header(APIs.AUTHORIZATION) String token, @Body JsonObject OBJ);

        @POST(APIs.DELETE_QUESTION)
        Call<JsonElement> deleteQuestion(@Header(APIs.AUTHORIZATION) String token, @Body JsonObject OBJ);

        @POST(APIs.EDIT_COMMENT)
        Call<JsonElement> editComment(@Header(APIs.AUTHORIZATION) String token, @Body JsonObject OBJ);

        @POST(APIs.EDIT_ANSWER)
        Call<JsonElement> editAnswer(@Header(APIs.AUTHORIZATION) String token, @Body JsonObject OBJ);

        Call<JsonElement> getHomePostByExpert(@Header(APIs.AUTHORIZATION) String token, @Body JsonObject OBJ);

        @POST(APIs.CHANGE_PASSWORD)
        Call<JsonElement> changePassword(@Header(APIs.AUTHORIZATION) String token, @Body JsonObject OBJ);

        @Multipart
        @POST(APIs.CREATE_POST)
        Call<JsonElement> createPost(@Part MultipartBody.Part[] file, @PartMap Map<String, RequestBody> body, @Header(APIs.AUTHORIZATION) String token, @Part MultipartBody.Part[] thumbnail);

        @POST(APIs.PROFESSIONAL_PROFILE)
        Call<JsonElement> professionalProfile(@Header(APIs.AUTHORIZATION) String token, @Body JsonObject OBJ);
}