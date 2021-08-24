package com.ebabu.event365live.httprequest;

public class APIs {

    public static final int PRECONDITION_FAILED = 412;
    public static final int OTHER_FAILED = 406;
    public static final int LOGIN_ATTEMPTS_FAILED = 409;
    public static final int BLOCK_FAILED = 410;
    public static final int SESSION_EXPIRE = 401;
    public static final int BLOCK_ADMIN = 403;
    public static final int EMAIL_NOT_VERIFIED = 432;
    public static final int PHONE_OTP_REQUEST = 437;
    public static final int BAD_GATEWAY = 502;
    public static final int NEED_PROFILE_UPDATE = 435;
    public static final int CHOOSE_RECOMMENDED_CATEGORY = 436;
    public static final String MESSAGE = "message";
    public static final String DATA = "data";
    public static final String AUTHORIZATION = "Authorization";

    // Base URL
    //public static final String BASE_URL = "https://api.365live.com/api/";
    public static final String BASE_URL = "https://test.365live.com/api/";

    // API name
    public static final String SIGN_UP = "signup";
    public static final String EMAIL_OTP_VERIFY = "verifyEmail";
    public static final String PHONE_OTP_VERIFY = "verifyPhone";
    public static final String RESET_PW = "verifyResetPW";
    public static final String RESEND_OTP = "resendOTP";
    public static final String RESEND_EMAIL_CODE = "againResedOTP";
    public static final String LOGIN = "login";
    public static final String SOCIAL_LOGIN = "social/login";
    public static final String FORGET_PASSWORD = "forgot";
    public static final String RESET_PASSWORD = "resetPassword";
    public static final String GET_FAVORITES_LIST = "getFavourite";
    public static final String UPDATE_PROFILE = "updateProfile";
    public static final String CHANGE_PASSWORD = "changePassword";
    public static final String GET_EVENT_CATEGORY = "getCategory";
    public static final String CHOOSE_EVENT_CATEGORY = "chooseSubCategory";
    public static final String GET_ALL_SUB_CATEGORY = "getAllSubCategory";
    public static final String NO_AUTH_NEAR_BY_EVENT = "NearBy";
    public static final String NEAR_BY_AUTH_EVENT = "NearBy/auth";
    public static final String NO_AUTH_FEATURED_BY_EVENT = "featureEvent";
    public static final String FEATURED_BY_AUTH_EVENT = "featureEvent/auth";
    public static final String SEARCH_NO_AUTH_API = "search";
    public static final String SEARCH_AUTH_API = "search/auth";
    public static final String USER_EVENT_DETAILS_NO_AUTH = "getUserEventDetail/{id}";
    public static final String USER_EVENT_DETAILS_AUTH = "getUserEventDetail/auth/{id}";
    public static final String CREATE_REVIEW = "createReview";
    public static final String GET_TICKET_INFO = "getTicketInfo/{event_id}";
    public static final String GET_CATEGORY = "getAllCategorys";
    public static final String GET_SUB_CATEGORY_NO_AUTH = "getSubCategorybyCategoryId";
    public static final String GET_SEE_MORE_REVIEW_BY_CAT_ID = "getReview/{categoryId}";
    public static final String GET_HOST_PROFILE_INFO = "organiser/getUser/{hostId}";
    public static final String SUB_CATEGORY_BY_CAT_ID = "EventByCategory";
    public static final String GET_CONTACT_US_ISSUE = "getIssues";
    public static final String POST_CONTACT_US_ISSUE = "contactUs";
    public static final String GET_POLICY = "policy ";
    public static final String GET_TERMS_CONDITION = "terms ";
    public static final String USER_LOGOUT = "logout";
    public static final String GET_EVENT_LIST_OF_UPCOMING_ATTEND = "getEventList";
    public static final String GET_RECOMMENDED__AUTH = "getRecommend";
    public static final String GET_USER_DETAILS = "getUser";
    public static final String USER_TICKET_BOOKED = "UserTicketBooked/{eventId}";
    public static final String TICKET_PAYMENT_REQUEST = "TicketPaymentRequest";
    public static final String PAYMENT_CONFIRM = "PaymentConfirm";
    public static final String GET_USER_TICKET_BOOKED = "getUserTicketBooked";
    public static final String MARK_FAVORITES_EVENT = "markFav";
    public static final String GET_USER_RSVP = "getUserRSVP";
    public static final String NOTIFICATION_COUNT = "countUserNotifications";
    public static final String STATUS_RSVP = "statusRSPV";
    public static final String GET_ALL_NOTIFICATION_LIST = "userAllNotification";
    public static final String EventLikeOrDislike = "isLikeAndDisLike";
    public static final String GET_EPHEMERAL_KEY = "GetEphemeralKey";
    public static final String USER_TICKET_CANCELLED = "userTicketCancelled";
    public static final String NOTIFICATION_REMINDER = "isRemindOrNotify";
    public static final String RELATED_EVENT = "organiser/relatedEvent/{id}";
    public static final String TYPE = "type";
    public static final String VALUE = "value";
    public static final String LIMIT = "limit";
    public static final String CATEGORY_ID = "categoryId";
    public static final String SUBCATEGORY_ID = "subcategoryId";
    public static final String PAGE = "page";


}
