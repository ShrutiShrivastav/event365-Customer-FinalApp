package com.ebabu.event365live.httprequest;

public class APIs {

    public static final int NULL_CASE = 000;
    public static final int PRECONDITION_FAILED = 412;
    public static final int OTHER_FAILED = 406;
    public static final int SESSION_EXPIRE = 401;
    public static final int BLOCK_ADMIN = 403;
    public static final int EMAIL_NOT_VERIFIED = 432;
    public static final int PHONE_OTP_REQUEST = 437;
    public static final int BAD_GATEWAY = 502;
    public static final int PHONE_NO_VERIFIED_ = 433;
    public static final int PHONE_NO_VERIFIED = 434;
    public static final int NEED_PROFILE_UPDATE = 435;
    public static final int CHOOSE_RECOMMENDED_CATEGORY = 436;
    public static final String MESSAGE = "message";
    public static final String DATA = "data";
    public static final String AUTHORIZATION = "Authorization";

    // Base URL
    public static final String BASE_URL = "http://18.220.188.129/api/";
    //public static final String BASE_URL = "http://192.168.1.152:8000/api/";

    // API name
    public static final String VERIFY_OTP = "verifyOTP";
    public static final String SIGN_UP = "signup";
    public static final String EMAIL_OTP_VERIFY = "verifyEmail";
    public static final String PHONE_OTP_VERIFY = "verifyPhone";
    public static final String RESET_PW = "verifyResetPW";
    public static final String RESEND_OTP = "resendOTP";
    public static final String PHONE_SEND_OTP = "sendPhoneOTP ";
    public static final String RESEND_EMAIL_CODE = "againResedOTP";
    public static final String LOGIN = "login";
    public static final String SOCIAL_LOGIN = "social/login";
    public static final String CATEGORY = "category";
    public static final String EXPERIENCE = "experience";
    public static final String SKILL = "skill";


    public static final String UPDATE_DATA = "updateData";
    public static final String UPDATE_DATA_TOKEN = "UpdateDataAfterLogin";
    public static final String FORGET_PASSWORD = "forgot";
    public static final String FORGET_PASSWORD_VERIFY_OTP = "verifyEmailOTP";
    public static final String FORGET_PASSWORD_RESEND_OTP = "resendOTPForgetPassword";
    public static final String RESET_PASSWORD = "resetPassword";
    public static final String LOGOUT = "logout";
    public static final String RESEND_EMAIL_VERIFY = "resendEmailVerify";
    public static final String COMPANY = "company";
    public static final String EDIT_PROFILE = "editProfile";
    public static final String UPDATE_MOBILE = "updatePhone";
    public static final String UPDATE_MOBILE_VERIFY = "updatePhoneVerifyOtp";
    public static final String GET_FAVORITES_LIST = "getFavourite";
    public static final String UPDATE_PROFILE = "updateProfile";
    public static final String CHANGE_PASSWORD = "changePassword";
    public static final String GET_EVENT_CATEGORY = "getCategory";
    public static final String GET_EVENT_SUB_CATEGORY = "getSubCategory";
    public static final String CHOOSE_EVENT_CATEGORY = "chooseSubCategory";
    public static final String GET_ALL_RECOMMENDED_SUB_CATEGORY = "getAllSubCategory";
    public static final String NO_AUTH_NEAR_BY_EVENT = "NearBy";
    public static final String NEAR_BY_AUTH_EVENT = "NearBy/auth";
    public static final String SEARCH_NO_AUTH_API = "search";
    public static final String SEARCH_AUTH_API = "search/auth";
    public static final String USER_EVENT_DETAILS_NO_AUTH = "getUserEventDetail/{id}";
    public static final String USER_EVENT_DETAILS_AUTH = "getUserEventDetail/auth/{id}";
    public static final String CREATE_REVIEW = "createReview";
    public static final String GET_TICKET_INFO = "getTicketInfo/{event_id}";
    public static final String GET_CATEGORY = "getAllCategorys";
    public static final String GET_SUB_CATEGORY_NO_AUTH = "getSubCategorybyCategoryId";
    public static final String LANDER_SCREEN_NEAR_BY_EVENT = "publicNearBy";
    public static final String GET_SEE_MORE_REVIEW_BY_CAT_ID = "getReview/{categoryId}";
    public static final String GET_HOST_PROFILE_INFO = "organiser/getUser/{hostId}";
    public static final String SUB_CATEGORY_BY_CAT_ID = "EventByCategory";
    public static final String GET_CONTACT_US_ISSUE = "getIssues";
    public static final String POST_CONTACT_US_ISSUE = "contactUs";
    public static final String GET_POLICY = "policy ";
    public static final String GET_TERMS_CONDITION = "terms ";
    public static final String USER_LOGOUT = "logout ";
    public static final String GET_EVENT_LIST_OF_UPCOMING_ATTEND = "getEventList";
    public static final String GET_RECOMMENDED_WITHOUT_AUTH = "Recommended";
    public static final String GET_RECOMMENDED__AUTH = "getRecommend";
    public static final String GET_USER_DETAILS = "getUser";
    public static final String USER_TICKET_BOOKED = "UserTicketBooked/{eventId}";
    public static final String TICKET_PAYMENT_REQUEST = "TicketPaymentRequest";
    public static final String PAYMENT_CONFIRM = "PaymentConfirm";
    public static final String GET_USER_TICKET_BOOKED = "getUserTicketBooked";
    public static final String GET_ALL_EVENT = "getUserTicketBooked";
    public static final String MARK_FAVORITES_EVENT = "markFav";
    public static final String GET_USER_RSVP = "getUserRSVP";
    public static final String NOTIFICATION_COUNT = "countUserNotifications";
    public static final String STATUS_RSVP = "statusRSPV";
    public static final String GET_ALL_NOTIFICATION_LIST = "userAllNotification";

    public static final String EventLikeOrDislike = "isLikeAndDisLike";
    public static final String GET_EPHEMERAL_KEY = "GetEphemeralKey";






    // API input parameter
    public static final String ID = "id";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String PHONE = "phone";
    public static final String PHONE_CODE = "phoneCode";
    public static final String USER_TYPE = "userType";
    public static final String OTP = "otp";
    public static final String COMPANY_NAME = "companyName";
    public static final String DESIGNATION = "designation";
    public static final String LOCATION = "location";
    public static final String DEVICE_TYPE = "deviceType";
    public static final String IS_NOTIFY = "isNotify";
    public static final String EMAIL_VERIFY = "emailVerify";
    public static final String PROFILE_STATUS = "profileStatus";
    public static final String BLOCK_STATUS = "blockStatus";
    public static final String PROFESSIONAL_ID = "professionalId";
    public static final String BLOCK = "block";
    public static final String RECOMMENDED_PROFESSIONAL = "expert";
    public static final String TRENDING_PROFESSIONAL = "trendingExpertList";
    public static final String GET_EXPERT_PROFILE = "getExpertProfile";
    public static final String FOLLOW = "follow";
    public static final String TRENDING_EXPERT = "trendingExpertListForExpert";
    public static final String ASK_QUESTION = "askQuestion";
    public static final String FOLLOWING_LIST= "following";
    public static final String FOLLOWER_LIST= "follower";
    public static final String BLOCK_LIST = "blockList";
    public static final String REMOVE_PROFESSIONAL = "remove";
    public static final String QUESTION_EXPERT_LIST = "askedQuetionExpertList";
    public static final String QUESTION_PROFESSIONAL_LIST = "askedQuetionByProfessionalList";
    public static final String FOLLOW_STATUS = "followStatus";
    public static final String ASKED_QUESTION = "askedquestion";
    public static final String QUESTION = "question";
    public static final String PROFILE_PIC = "profilePic";
    public static final String LIMIT = "limit";
    public static final String OFFSET = "offset";
    public static final String EXPERT_ID = "expertId";
    public static final String QUESTION_ID = "questionId";
    public static final String ASKED_QUESTION_LIST = "askedQuestionList";
    public static final String GET_ANSWER_LIST = "GetAnswerList";
    public static final String GET_QUESTION_LIST = "getQuestionList";
    public static final String ANSWER_TO_PROFESSIONAL = "answerToProfessional";
    public static final String COMMENT_ON_ANSWER = "commentOnAnswer";
    public static final String ANSWER = "answer";
    public static final String ANSWER_ID = "answerId";
    public static final String EDIT_QUESTION = "editQuestion";
    public static final String DELETE_QUESTION = "deleteQuestion";
    public static final String EDIT_COMMENT = "editComment";
    public static final String EDIT_ANSWER = "editAnswer";
    public static final String IS_QUESTION_UPDATE = "IS_QUESTION_UPDATE";

    public static final String CREATE_POST = "createPost";
    public static final String PROFESSIONAL_PROFILE = "getUserDataById";

    public static final String TOP_5_EVENT = "topEvent";
    public static final String NOTIFICATION_REMINDER = "isRemindOrNotify";
    public static final String TYPE = "type";
    public static final String VALUE = "value";

}
