package com.ebabu.event365live.httprequest;

public class Constants {

    public static final String TXT_PLAIN = "text/plain";
    public static String userId;
    public static String favoritesList = "favoritesList";
    public static String wishList = "wishList";
    public static String eventName = "eventName";
    public static String eventStartTime = "eventStartTime";
    public static String eventEndTime = "eventEndTime";
    public static String eventTime = "eventTime";
    public static String eventDate = "eventDate";
    public static String eventAdd = "eventAdd";
    public static String currentLat = "currentLat";
    public static String currentLng = "currentLng";
    public static String hostId = "hostId";
    public static String startDate = "startDate";
    public static String endDate = "endDate";
    public static String flag = "flag";
    public static String catData = "catData";
    public static String latitude = "latitude";
    public static String longitude = "longitude";
    public static String miles = "miles";
    public static String cost = "cost";
    public static String activityName = "activityName";
    public static String nearByData = "nearByData";
    public static String categoryId = "categoryId";
    public static String subCategoryId = "subCategoryId";
    public static String type = "type";

    public static String ticketId = "ticketId";
    public static String ticketTypes = "ticketType";
    public static String totalQuantity = "totalQuantity";
    public static String pricePerTicket = "pricePerTicket";
    public static String parsonPerTable = "parsonPerTable";
    public static String createdBy = "createdBy";

    public static String eventReminder = "eventReminder";
    public static String eventNotification = "eventNotification";
    public static String api_version = "2018-09-06";




    public static final int AUTOCOMPLETE_REQUEST_CODE = 4001;
    public static final int CURRENT_FUSED_LOCATION_REQUEST = 9001;
    public static final int REQUEST_CHECK_SETTINGS = 5001;
    public static final int MOBILE_VERIFY_REQUEST_CODE = 3001;
    public static final int VIEW_TYPE_LOADING = 0;
    public static final int VIEW_TYPE_NORMAL = 1;

    public static final int FREE_TICKET_VIEW_TYPE = 1001;
    public static final int VIP_NORMAL_TICKET_VIEW_TYPE = 1002;
    public static final int VIP_SEATING_TICKET_VIEW_TYPE = 1003;
    public static final int REGULAR_NORMAL_TICKET_VIEW_TYPE = 1004;
    public static final int REGULAR_SEATING_TICKET_VIEW_TYPE = 1005;
    public static String id = "id";


    public interface ApiKeyName {
        /*register api key name*/

        String userId = "id";
        String name = "name";
        String socialId = "socialId";
        String socialLoginType = "socialLoginType";
        String email = "email";
        String password = "password";
        String newPassword = "newPassword";
        String otp = "otp";
        String state = "state";
        String countryNameCode = "countryCode";
        String countryCode = "countryCode";
        String zip = "zip";
        String phoneNo = "phoneNo";
        String address = "address";
        String city = "city";
        String url = "URL";
        String info = "info";
        String shortInfo = "shortInfo";
        String profilePic = "profilePic";
        String categoryId = "categoryId";
        String keyword = "keyword";
        String reviewStar = "reviewStar";
        String reviewText = "reviewText";
        String eventId = "eventId";
        String status = "status";
        String isFavorite = "isFavorite";
        String latitude = "latitude";
        String longitude = "longitude";
        String issueId = "issueId";
        String message = "message";
        String eventImg = "eventImg";
    }


    public interface  SharedKeyName{

        String userId = "id";
        String userName = "name";
        String userEmail = "email";
        String userType = "userType";
        String isMobileVerified = "isMobileVerified";
        String isEmailVerified = "isEmailVerified";
        String isAdminVerified = "isAdminVerified";
        String deviceToken = "deviceToken";
        String deviceAuth = "deviceAuth";
        String deviceId = "deviceId";
        String deviceType = "deviceType";
        String countryCode = "countryCode";
        String phoneNo = "phoneNo";
        String address = "address";
        String city = "city";
        String state = "state";
        String zip = "zip";
        String created_at = "created_at";
        String updated_at = "updated_at";
        String profilePic = "profilePic";
        String isNotify = "isNotify";
        String isRemind = "isRemind";
        String isActive = "isActive";
        String isUserLogin ="isUserLogin";
        String isHomeSwipeView = "isHomeSwipeView";
        String isViewSwipe = "isViewSwipe";
        String isViewList = "isViewList";
        String forStripeCustomerId = "forStripeCustomerId";
    }
}
