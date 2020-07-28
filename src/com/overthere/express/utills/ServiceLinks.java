package com.overthere.express.utills;

/**
 * Created by Jack on 31/08/2017.
 */

public class ServiceLinks {

    // Local server for Taxi Now Makeover version
    //private static final String HOST_URL = "http://taxi.overhere.one/";
    //private static final String HOST_URL = "http://overhere.overhere.one/";
    public static final String MASTER_URL = "http://128.199.246.239/";
    public static final String REGISTER_TO_MASTER = MASTER_URL + "provider/"+"register_master";
    public static final String LOGIN_TO_MASTER = MASTER_URL + "provider/"+"login_master";
    //public String HOST_URL = "http://188.166.242.235/";
    public String HOST_URL = "http://188.166.0.1/";
    public  String BASE_URL = HOST_URL + "provider/";
    public  String LOGIN = BASE_URL + "login";
    public  String REGISTER = BASE_URL + "register";
    public  String GET_ALL_REQUESTS = BASE_URL + "getrequests?";
    public  String RESPOND_REQUESTS = BASE_URL + "respondrequest";
    public  String UPDATE_PROVIDER_LOCATION = BASE_URL + "location";
    public  String CHECK_REQUEST_STATUS = BASE_URL + "getrequest?";
    public  String REQUEST_IN_PROGRESS = BASE_URL + "requestinprogress?";
    public  String WALKER_STARTED = BASE_URL + "requestwalkerstarted";
    public  String WALK_ARRIVED = BASE_URL + "requestwalkerarrived";
    public  String WALK_STARTED = BASE_URL + "requestwalkstarted";
    public  String WALK_COMPLETED = BASE_URL + "requestwalkcompleted";
    public  String WALK_PAYMENT = BASE_URL + "walkpayment";
    public String RATING = BASE_URL + "rating";
    public String FARE_UPDATE = BASE_URL + "fareupdate?";
    public String GET_FARE_CONFIRMATION = BASE_URL + "getfareconfirmation?";
    public String UPDATE_PROFILE = BASE_URL + "update";
    public String HISTORY = BASE_URL + "history?";
    public String BOOKING = BASE_URL + "bookingList?";
    public String CUSTOMERS = BASE_URL + "customerList?";
    public String ACHIEVEMENT = BASE_URL + "achievement?";
    public String PATH_REQUEST = BASE_URL + "requestpath?";
    public String REQUEST_LOCATION_UPDATE = HOST_URL
            + "request/location";
    public String CHECK_STATE = BASE_URL + "checkstate?";
    public String TOGGLE_STATE = BASE_URL + "togglestate";
    public  String FORGET_PASSWORD = HOST_URL
            + "application/forgot-password";
    public String GET_VEHICAL_TYPES = HOST_URL
            + "application/types";
    public String APPLICATION_PAGES = HOST_URL
            + "application/pages";
    public String LOGOUT = BASE_URL + "logout";
    public String CREATE_REQUEST = BASE_URL + "createrequest";
    public String OVERHERE_REQUEST = BASE_URL + "overhererequest";
    public String CANCEL_WALKER = BASE_URL + "cancelwalker";
    public String SAVE_SETTINGS = BASE_URL + "savesettings";
    public String DEFAULT_CARD = BASE_URL + "card_selection";
    public String GET_CARDS = BASE_URL + "cards?";
    public String ADD_CARD = BASE_URL + "addcardtoken";


    public void update() {
        BASE_URL = HOST_URL + "provider/";
        LOGIN = BASE_URL + "login";
        REGISTER = BASE_URL + "register";
        GET_ALL_REQUESTS = BASE_URL + "getrequests?";
        RESPOND_REQUESTS = BASE_URL + "respondrequest";
        UPDATE_PROVIDER_LOCATION = BASE_URL + "location";
        CHECK_REQUEST_STATUS = BASE_URL + "getrequest?";
        REQUEST_IN_PROGRESS = BASE_URL + "requestinprogress?";
        WALKER_STARTED = BASE_URL + "requestwalkerstarted";
        WALK_ARRIVED = BASE_URL + "requestwalkerarrived";
        WALK_STARTED = BASE_URL + "requestwalkstarted";
        WALK_COMPLETED = BASE_URL + "requestwalkcompleted";
        WALK_PAYMENT = BASE_URL + "walkpayment";
        RATING = BASE_URL + "rating";
        FARE_UPDATE = BASE_URL + "fareupdate?";
        GET_FARE_CONFIRMATION = BASE_URL + "getfareconfirmation?";
        UPDATE_PROFILE = BASE_URL + "update";
        HISTORY = BASE_URL + "history?";
        BOOKING = BASE_URL + "bookingList?";
        CUSTOMERS = BASE_URL + "customerList?";
        ACHIEVEMENT = BASE_URL + "achievement?";
        PATH_REQUEST = BASE_URL + "requestpath?";
        REQUEST_LOCATION_UPDATE = HOST_URL + "request/location";
        CHECK_STATE = BASE_URL + "checkstate?";
        TOGGLE_STATE = BASE_URL + "togglestate";
        FORGET_PASSWORD = HOST_URL + "application/forgot-password";
        GET_VEHICAL_TYPES = HOST_URL + "application/types";
        APPLICATION_PAGES = HOST_URL + "application/pages";
        LOGOUT = BASE_URL + "logout";
        CREATE_REQUEST = BASE_URL + "createrequest";
        OVERHERE_REQUEST = BASE_URL + "overhererequest";
        CANCEL_WALKER = BASE_URL + "cancelwalker";
        SAVE_SETTINGS = BASE_URL + "savesettings";
        DEFAULT_CARD = BASE_URL + "card_selection";
        GET_CARDS = BASE_URL + "cards?";
        ADD_CARD = BASE_URL + "addcardtoken";

        return;
    }

}
