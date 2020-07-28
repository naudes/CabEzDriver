package com.overthere.express.utills;

public class AndyConstants {
	public static final String TAG = "OVER THERE";
	public static final String DIRECTION_API_KEY = "AIzaSyAto7SNCNVJ4gFCciytelh9vTv_w5qrhAQ";
	// fragment constants
	public static final String MAIN_FRAGMENT_TAG = "mainFragment";
	public static final String LOGIN_FRAGMENT_TAG = "loginFragment";
	public static final String REGISTER_FRAGMENT_TAG = "registerFragment";
	public static final String CLIENT_REQUEST_TAG = "clientRequestFragment";
	public static final String FEEDBACK_FRAGMENT_TAG = "feedbackFragment";
	public static final String JOB_FRGAMENT_TAG = "jobDoneFragment";
	public static final String ARRIVED_FRAGMENT_TAG = "arrivedFragment";
	public static String FRAGMENT_PAYMENT_REGISTER = "ADD_FRAGMENT_PAYMENT_REGISTER";
	public static final String FOREGETPASS_FRAGMENT_TAG = "FoegetPassFragment";
	public static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	public static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	public static final String OUT_JSON = "/json";
	public static final String PLACES_AUTOCOMPLETE_API_KEY = "AIzaSyCqcD3tNdTc8uUpa8j1D4_BXUi1Vtr5QF0";
	public static final String PUBLISHABLE_KEY = "pk_test_51H3fY5AUoldBkI2tP8cT3GekGuX0elq0W8hk6CKyn1BDRdSoR8JbglD3UakS6hH9e9H5GG0mbpzjxBJlnos7e79500kSmzAg40";

	public static final int CHOOSE_PHOTO = 112;
	public static final int TAKE_PHOTO = 113;

	public static final String PREF_NAME = "OverThere";
	public static final String URL = "url";
	public static final String LINK ="url";
	public static final String DEVICE_TYPE_ANDROID = "android";
	public static final String SOCIAL_FACEBOOK = "facebook";
	public static final String SOCIAL_GOOGLE = "google";
	public static final String MANUAL = "manual";
	public static final String GOOGLE_API_SCOPE_URL = "https://www.googleapis.com/auth/plus.login";
	public static final long DELAY = 0;
	public static final long TIME_SCHEDULE = 10 * 1000;
	public static final long FARE_SCHEDULE = 10 * 1000;
	public static final long DELAY_OFFLINE = 15 * 60 * 1000;
	public static final long TIME_SCHEDULE_OFFLINE = 15 * 60 * 1000;

	public static final int IS_ASSIGNED = 0;
	public static final int IS_WALKER_STARTED = 1;
	public static final int IS_WALKER_ARRIVED = 2;
	public static final int IS_WALK_STARTED = 3;
	public static final int IS_WALK_COMPLETED = 4;
	public static final int IS_DOG_RATED = 5;

	public static final String JOB_STATUS = "jobstatus";
	public static final String REQUEST_DETAIL = "requestDetails";

	// error code
	public static final int TOKEN_EXPIRED = 405;
	public static final int INVALID_TOKEN = 406;
	public static final int REQUEST_ID_NOT_FOUND = 408;

	// no request
	public static final int NO_REQUEST = -1;
	public static final int NO_TIME = -1;
	public static final String NEW_REQUEST = "new_request";
	public static final int NOTIFICATION_ID = 0;
	// payment mode
	public static final int CASH = 1;
	public static final int CREDIT = 0;
	public static final String CHANNEL_NAME ="OverThere";
	public static final String CHANNEL_SIREN_DESCRIPTION ="OverThere Notifications";
	// web service url constants

	public class ServiceType {

		// Local server for Taxi Now Makeover version
		//private static final String HOST_URL = "http://taxi.overhere.one/";
		//private static final String HOST_URL = "http://overhere.overhere.one/";
		//public static final String HOST_URL = "http://188.166.242.235/";
		public static final String HOST_URL = "";

		public static final String MASTER_URL = "http://cabez.com.au/";
		public static final String LOCAL_URL = "http://cabez.com.au/";
		public static final String REGISTER_TO_MASTER = MASTER_URL + "provider/"+"register_master";
		public static final String LOGIN_TO_MASTER = MASTER_URL + "provider/"+"login_master";



		private static final String BASE_URL = HOST_URL + "api/provider/";
		public static final String LOGIN = BASE_URL + "login";
		public static final String REGISTER = BASE_URL + "register";
		public static final String GET_ALL_REQUESTS = BASE_URL + "getrequests?";
		public static final String RESPOND_REQUESTS = BASE_URL
				+ "respondrequest";
		public static final String UPDATE_PROVIDER_LOCATION = BASE_URL
				+ "location";
		public static final String CHECK_REQUEST_STATUS = BASE_URL
				+ "getrequest?";
		public static final String REQUEST_IN_PROGRESS = BASE_URL
				+ "requestinprogress?";
		public static final String WALKER_STARTED = BASE_URL
				+ "requestwalkerstarted";
		public static final String WALK_ARRIVED = BASE_URL
				+ "requestwalkerarrived";
		public static final String WALK_STARTED = BASE_URL
				+ "requestwalkstarted";
		public static final String WALK_COMPLETED = BASE_URL
				+ "requestwalkcompleted";
		public static final String WALK_PAYMENT = BASE_URL
				+ "walkpayment";
		public static final String PRE_PAYMENT = BASE_URL + "prepayment";
		//public static final String PRE_PAYMENT = BASE_URL + "rating1";
		public static final String RATING = BASE_URL + "rating";
		public static final String FARE_UPDATE = BASE_URL + "fareupdate?";
		public static final String GET_FARE_CONFIRMATION = BASE_URL + "getfareconfirmation?";
		public static final String UPDATE_PROFILE = BASE_URL + "update";
		public static final String HISTORY = BASE_URL + "history?";
		public static final String BOOKING = BASE_URL + "bookingList?";
		public static final String POST = BASE_URL + "postList?";
		public static final String CUSTOMERS = BASE_URL + "customerList?";
		public static final String ACHIEVEMENT = BASE_URL + "achievement?";
		public static final String PATH_REQUEST = BASE_URL + "requestpath?";
		public static final String REQUEST_LOCATION_UPDATE = BASE_URL
				+ "request/location";
		public static final String CHECK_STATE = BASE_URL + "checkstate?";
		public static final String TOGGLE_STATE = BASE_URL + "togglestate";
		public static final String FORGET_PASSWORD = HOST_URL
				+ "api/application/forgot-password";
		public static final String GET_VEHICAL_TYPES = HOST_URL
				+ "api/application/types";
		public static final String APPLICATION_PAGES = HOST_URL
				+ "api/application/pages";
		public static final String LOGOUT = BASE_URL + "logout";
		public static final String CREATE_REQUEST = BASE_URL + "createrequest";
		public static final String OVERHERE_REQUEST = BASE_URL + "overhererequest";
		public static final String CANCEL_WALKER = BASE_URL + "cancelwalker";
		public static final String SAVE_SETTINGS = BASE_URL + "savesettings";
		public static final String DEFAULT_CARD = BASE_URL + "card_selection";
		public static final String GET_CARDS = BASE_URL + "cards?";
		public static final String GET_POST_TEXT = BASE_URL + "get_ads_text?";
		public static final String ADD_CARD = BASE_URL + "addcardtoken";
		public static final String BOOKING_APPLY = BASE_URL + "booking_apply";
		public static final String POST_SAVE = BASE_URL + "post_save";

	}

	public class ServiceCode {
		public static final int REGISTER = 1;
		public static final int LOGIN = 2;
		public static final int GET_ALL_REQUEST = 3;
		public static final int RESPOND_REQUEST = 4;
		public static final int CHECK_REQUEST_STATUS = 5;
		public static final int REQUEST_IN_PROGRESS = 6;
		public static final int WALKER_STARTED = 7;
		public static final int WALKER_ARRIVED = 8;
		public static final int WALK_STARTED = 9;
		public static final int WALK_COMPLETED = 10;
		public static final int RATING = 11;
		public static final int GET_ROUTE = 12;
		public static final int APPLICATION_PAGES = 13;
		public static final int UPDATE_PROFILE = 14;
		public static final int GET_VEHICAL_TYPES = 16;
		public static final int FORGET_PASSWORD = 17;
		public static final int HISTORY = 18;
		public static final int CHECK_STATE = 19;
		public static final int TOGGLE_STATE = 20;
		public static final int PATH_REQUEST = 21;
		public static final int DRAW_PATH_ROAD = 22;
		public static final int DRAW_PATH = 23;
		public static final int LOGOUT = 24;
		public static final int DRAW_PATH_CLIENT = 25;
		public static final int CREATE_REQUEST = 28;
		public static final int OVERHERE_REQUEST = 29;
		public static final int CANCEL_WALKER = 30;
		public static final int FARE_UPDATE = 31;
		public static final int GET_FARE_CONFIRMATION = 32;
		public static final int BOOKING = 33;
		public static final int SAVE_SETTINGS = 34;
		public static final int WALK_PAYMENT = 35;
		public static final int DEFAULT_CARD = 36;
		public static final int GET_CARDS = 37;
		public static final int ADD_CARD = 38;
		public static final int CUSTOMERS = 39;
		public static final int ACHIEVEMENT = 40;
		public static final int UPDATE_PROVIDER_LOCATION = 41;
		public static final int REQUEST_LOCATION_UPDATE = 42;
		public static final int LOGIN_MASTER = 43;
		public static final int REGISTER_MASTER = 44;
		public static final int BOOKING_APPLY = 45;
		public static final int POST = 46;
		public static final int POST_SAVE = 47;
		public static final int GET_POST_TEXT= 48;
		public static final int PRE_PAYMENT = 49;
	}

	// webservice key constants
	public class Params {
		public static final String LOCAL_HOST = "local_host";
		public static final String EMAIL = "email";
		public static final String PASSWORD = "password";
		public static final String FIRSTNAME = "first_name";
		public static final String LAST_NAME = "last_name";
		public static final String PHONE = "phone";
		public static final String DEVICE_TOKEN = "device_token";
		public static final String DEVICE_TYPE = "device_type";
		public static final String BIO = "bio";
		public static final String ADDRESS = "address";
		public static final String STATE = "state";
		public static final String COUNTRY = "country";
		public static final String ZIPCODE = "zipcode";
		public static final String AREA = "area";
		public static final String OPERATOR = "operator";
		public static final String LOGIN_BY = "login_by";
		public static final String SOCIAL_UNIQUE_ID = "social_unique_id";
		public static final String PICTURE = "picture";
		public static final String ID = "id";
		public static final String TOKEN = "token";
		public static final String REQUEST_ID = "request_id";
		public static final String ACCEPTED = "accepted";
		public static final String LATITUDE = "latitude";
		public static final String LONGITUDE = "longitude";
		public static final String DISTANCE = "distance";
		public static final String BEARING = "bearing";
		public static final String COMMENT = "comment";
		public static final String RATING = "rating";
		public static final String INCOMING_REQUESTS = "incoming_requests";
		public static final String TIME_LEFT_TO_RESPOND = "time_left_to_respond";
		public static final String REQUEST = "request";
		public static final String REQUESTS = "requests";
		public static final String REQUEST_DATA = "request_data";
		public static final String NAME = "name";
		public static final String NUM_RATING = "num_rating";
		public static final String OWNER = "owner";
		public static final String WALKER = "walker";
		public static final String UNIQUE_ID = "unique_id";
		public static final String TITLE = "title";
		public static final String CONTENT = "content";
		public static final String INFORMATIONS = "informations";
		public static final String IS_ACTIVE = "is_active";
		public static final String IS_AVAILABLE = "is_available";
		public static final String ICON = "icon";
		public static final String TYPE = "type";
		public static final String TYPE_NAME = "type_name";
		public static final String CARD_TYPE = "card_type";
		public static final String DISTANCE_COST = "distance_cost";
		public static final String TIME_COST = "time_cost";
		public static final String TOTAL = "total";
		public static final String IS_PAID = "is_paid";
		public static final String TIME = "time";
		public static final String DATE = "date";
		public static final String LOCATION_DATA = "locationdata";
		public static final String START_TIME = "start_time";
		public static final String PAYMENT_TYPE = "payment_type";
		public static final String IS_APPROVED = "is_approved";
		public static final String OLD_PASSWORD = "old_password";
		public static final String NEW_PASSWORD = "new_password";
		public static final String CAR_NUMBER = "car_number";
		public static final String CAR_MODEL = "car_model";
		public static final String REFERRAL_CODE = "referral_code";
		public static final String REFERRAL_BONUS = "referral_bonus";
		public static final String PROMO_BONUS = "promo_bonus";
		public static final String UNIT = "unit";
		public static final String DESTINATION_LATITUDE = "dest_latitude";
		public static final String DESTINATION_LONGITUDE = "dest_longitude";
		public static final String OVERHERE_REQUEST_ID = "overhere_request_id";
		public static final String REQUEST_TIME = "request_time";
		public static final String PICKUP_AREA = "pickup_area";
		public static final String DROPOFF_AREA = "dropoff_area";
		public static final String PICKUP_ADDRESS = "pickup_address";
		public static final String DROPOFF_ADDRESS = "dropoff_address";
		public static final String REQUEST_SOURCE = "request_source";
		public static final String PICKUP_TYPE = "pickup_type";
		public static final String CONFIRMED_WALKER = "confirmed_walker";
		public static final String CURRENT_WALKER = "current_walker";
		public static final String BOOKING_FARE = "booking_fare";
		//driver settings
		public static final String CAR_MAKER = "car_maker";
		public static final String REGO_NUMBER = "rego_number";
		public static final String SEAT_NUMBER = "seat_number";
		public static final String VEHICLE_TYPE = "vehicle_type";
		public static final String LOAD_WEIGHT = "load_weight";
		public static final String LOAD_LENGTH = "load_length";
		public static final String POS_CAR_MAKER = "pos_car_maker";
		public static final String POS_SEAT_NUMBER = "pos_seat_number";
		public static final String POS_VEHICLE_TYPE = "pos_vehicle_type";
		public static final String POS_LOAD_WEIGHT = "pos_load_weight";
		public static final String POS_LOAD_LENGTH = "pos_load_length";

		public static final String BABY_SEAT = "baby_seat";
		public static final String STATION_WAGON = "station_wagon";
		public static final String WHEEL_CHAIR = "wheel_chair";
		public static final String JUMP_LEADS = "jump_leads";
		public static final String FUEL_BOTTLE = "fuel_bottle";
		public static final String CHG_BATTERY = "change_battery";
		public static final String CHG_TYRE = "change_tyre";
		public static final String CHG_BELT = "change_belt";
		public static final String WATER_LEAKING = "water_leaking";
		public static final String OIL_LEAKING = "oil_leaking";
		public static final String ENGIN_NOT_START = "engin_not_start";
		public static final String CAR_NOT_MOVING = "car_not_moving";
		public static final String VISA = "visa";
		public static final String MASTERCARD = "mastercard";
		public static final String AMERICAN_EXPRESS = "americanexpress";
		public static final String DISCOVER = "discover";
		public static final String DINERS_CLUB = "dinersclub";
		public static final String TAG = "tag";
		public static final String DEFAULT_CARD_ID = "default_card_id";
		public static final String STRIPE_TOKEN = "payment_token";
		public static final String LAST_FOUR = "last_four";
		public static final String HAIL_TOTAL = "hail_total";
		public static final String HAIL_REWARDED = "hail_rewarded";
		public static final String HAIL_BALANCE = "hail_balance";
		public static final String BOOKING_TOTAL = "booking_total";
		public static final String BOOKING_REWARDED = "booking_rewarded";
		public static final String BOOKING_BALANCE = "booking_balance";
		public static final String HELP_TOTAL = "help_total";
		public static final String HELP_REWARDED = "help_rewarded";
		public static final String HELP_BALANCE = "help_balance";
		public static final String BOOKING_APPLIED = "applied";
		public static final String CUSTOMER_FIRST_NAME = "customer_first_name";
		public static final String CUSTOMER_LAST_NAME = "customer_last_name";
		public static final String MEMBER_DATE = "member_date";
		public static final String SERVICE_TIMES = "service_times";
		public static final String REVIEW_RATE = "review_rate";
		public static final String PICKUP_DATE = "pickup_date";
		public static final String NEW_BOOKING = "new_booking";
		public static final String TYPE_POS = "type_pos";
		public static final String REWARD = "reward";
        public static final String CPHONE = "cphone";
		public static final String CIGRATES = "cigrates";
		public static final String LIGHTER = "lighter";
		public static final String POST_ID = "postId";
		public static final String POST_TIMES = "postTimes";
		public static final String POST_TITILE = "postTitile";
		public static final String POST_TEXT = "postText";
		public static final String POST_POSITION = "postPosition";
		public static final String POST_COLOR = "postColor";
		public static final String POST_IMAGE_NAME = "postImageName";
        public static final String CREDIT = "credit";
	}

	public class ServiceTypes {
		public static final String SEDAN = "1";
		public static final String SILVER_SERVICE = "2";
		public static final String STATION_WAGON = "3";
		public static final String WHEEL_CHAIR = "4";
		public static final String MINI_VAN = "5";
		/*public static final String TAXI_SERVICE = "1";
		public static final String TAXI_EXPRESS = "2";
		public static final String MOBILE_MECHANIC = "3";
		public static final String REMOVAL_SERVICE = "4";
		public static final String TOW_SERVICE = "5"; */
	}

}
