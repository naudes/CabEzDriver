package com.overthere.express.parse;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.text.TextUtils;

import com.overthere.express.R;
import com.overthere.express.db.DBHelper;
import com.overthere.express.maputills.PolyLineUtils;
import com.overthere.express.model.ApplicationPages;
import com.overthere.express.model.BeanRoute;
import com.overthere.express.model.BeanStep;
import com.overthere.express.model.History;
import com.overthere.express.model.Booking;
import com.overthere.express.model.Customer;
import com.overthere.express.model.Post;
import com.overthere.express.model.RequestDetail;
import com.overthere.express.model.User;
import com.overthere.express.model.Card;
import com.overthere.express.model.VehicalType;
import com.overthere.express.utills.AndyConstants;
import com.overthere.express.utills.AndyUtils;
import com.overthere.express.utills.AppLog;
import com.overthere.express.utills.PreferenceHelper;
import com.overthere.express.utills.ReadFiles;
import com.google.android.gms.maps.model.LatLng;

public class ParseContent {
	private Activity activity;
	private PreferenceHelper preferenceHelper;
	private final String KEY_SUCCESS = "success";
	private final String KEY_ERROR = "error";
	private final String IS_WALKER_STARTED = "is_walker_started";
	private final String IS_WALKER_ARRIVED = "is_walker_arrived";
	private final String IS_WALK_STARTED = "is_started";
	private final String IS_DOG_RATED = "is_dog_rated";
	private final String IS_WALK_COMPLETED = "is_completed";
	private final String IS_CANCELLED = "is_cancelled";
	private final String KEY_ERROR_CODE = "error_code";

	private final String BASE_PRICE = "base_price";
	private final String TYPES = "types";
	private final String ID = "id";
	private final String ICON = "icon";
	private final String IS_DEFAULT = "is_default";
	private final String PRICE_PER_UNIT_TIME = "price_per_unit_time";
	private final String PRICE_PER_UNIT_DISTANCE = "price_per_unit_distance";
	private final String WALKER_ID = "walker_id";
	private final String CARD_TYPE = "card_type";
	private final String PAYMENTS = "payments";
	private final String LAST_FOUR = "last_four";
	private final String CUSTOMER_ID = "customer_id";
	private RequestDetail requestDetail;

	public ParseContent(Activity activity) {
		this.activity = activity;
		preferenceHelper = new PreferenceHelper(activity);
		requestDetail = new RequestDetail();
	}

	/* *
	 * @param applicationContext
	 */

	public BeanRoute parseRoute(String response, BeanRoute routeBean) {

		try {
			BeanStep stepBean;
			JSONObject jObject = new JSONObject(response);
			JSONArray jArray = jObject.getJSONArray("routes");
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject innerjObject = jArray.getJSONObject(i);
				if (innerjObject != null) {
					JSONArray innerJarry = innerjObject.getJSONArray("legs");
					for (int j = 0; j < innerJarry.length(); j++) {

						JSONObject jObjectLegs = innerJarry.getJSONObject(j);
						routeBean.setDistanceText(jObjectLegs.getJSONObject(
								"distance").getString("text"));
						routeBean.setDistanceValue(jObjectLegs.getJSONObject(
								"distance").getInt("value"));

						routeBean.setDurationText(jObjectLegs.getJSONObject(
								"duration").getString("text"));
						routeBean.setDurationValue(jObjectLegs.getJSONObject(
								"duration").getInt("value"));

						routeBean.setStartAddress(jObjectLegs
								.getString("start_address"));
						routeBean.setEndAddress(jObjectLegs
								.getString("end_address"));

						routeBean.setStartLat(jObjectLegs.getJSONObject(
								"start_location").getDouble("lat"));
						routeBean.setStartLon(jObjectLegs.getJSONObject(
								"start_location").getDouble("lng"));
						routeBean.setEndLat(jObjectLegs.getJSONObject(
								"end_location").getDouble("lat"));
						routeBean.setEndLon(jObjectLegs.getJSONObject(
								"end_location").getDouble("lng"));

						JSONArray jstepArray = jObjectLegs
								.getJSONArray("steps");
						if (jstepArray != null) {
							for (int k = 0; k < jstepArray.length(); k++) {
								stepBean = new BeanStep();
								JSONObject jStepObject = jstepArray
										.getJSONObject(k);
								if (jStepObject != null) {

									stepBean.setHtml_instructions(jStepObject
											.getString("html_instructions"));
									stepBean.setStrPoint(jStepObject
											.getJSONObject("polyline")
											.getString("points"));
									stepBean.setStartLat(jStepObject
											.getJSONObject("start_location")
											.getDouble("lat"));
									stepBean.setStartLon(jStepObject
											.getJSONObject("start_location")
											.getDouble("lng"));
									stepBean.setEndLat(jStepObject
											.getJSONObject("end_location")
											.getDouble("lat"));
									stepBean.setEndLong(jStepObject
											.getJSONObject("end_location")
											.getDouble("lng"));

									stepBean.setListPoints(new PolyLineUtils()
											.decodePoly(stepBean.getStrPoint()));
									routeBean.getListStep().add(stepBean);
								}
							}
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return routeBean;
	}

	public boolean isSuccessWithId(String response) {
		if (TextUtils.isEmpty(response)) {
			return false;
		}
		try {
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.getBoolean(KEY_SUCCESS)) {
				preferenceHelper.putUserId(jsonObject
						.getString(AndyConstants.Params.ID));
				preferenceHelper.putSessionToken(jsonObject
						.getString(AndyConstants.Params.TOKEN));
				preferenceHelper.putEmail(jsonObject
						.getString(AndyConstants.Params.EMAIL));
				preferenceHelper.putServiceType(jsonObject
						.getString(AndyConstants.Params.TYPE));
				preferenceHelper.putString(AndyConstants.Params.REFERRAL_CODE,jsonObject
						.getString(AndyConstants.Params.REFERRAL_CODE));
				preferenceHelper.putLoginBy(jsonObject
						.getString(AndyConstants.Params.LOGIN_BY));
				if (!preferenceHelper.getLoginBy().equalsIgnoreCase(
						AndyConstants.MANUAL)) {
					preferenceHelper.putSocialId(jsonObject
							.getString(AndyConstants.Params.SOCIAL_UNIQUE_ID));
				}
				return true;
			} else {
				//AndyUtils.showErrorToast(jsonObject.getInt(KEY_ERROR_CODE), activity);
				return false;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}


	public boolean isSuccess(String response) {
		if (TextUtils.isEmpty(response))
			return false;
		try {

			JSONObject jsonObject = new JSONObject(response);

			if (jsonObject.getBoolean(KEY_SUCCESS)) {
				return true;
			} else {
				//AndyUtils.showToast(jsonObject.getString(KEY_ERROR), activity);
				return false;
			}

			// else {
			//
			// }
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	public ArrayList<String> parseCountryCodes() {
		String response = "";
		ArrayList<String> list = new ArrayList<String>();
		try {
			response = ReadFiles.readRawFileAsString(activity,
					R.raw.countrycodes);

			JSONArray array = new JSONArray(response);
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				list.add(object.getString("phone-code") + " "
						+ object.getString("name"));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	public int getErrorCode(String response) {
		if (TextUtils.isEmpty(response))
			return 0;
		try {
			AppLog.Log("TAG", response);
			JSONObject jsonObject = new JSONObject(response);
			return jsonObject.getInt(KEY_ERROR_CODE);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int parseRequestInProgress(String response) {
		if (TextUtils.isEmpty(response)) {
			return AndyConstants.NO_REQUEST;
		}
		try {
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.getBoolean(KEY_SUCCESS)) {
				int requestId = jsonObject
						.getInt(AndyConstants.Params.REQUEST_ID);
				preferenceHelper.putRequestId(requestId);
				return requestId;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return AndyConstants.NO_REQUEST;
	}

	public boolean parseIsApproved(String response) {
		if (TextUtils.isEmpty(response)) {
			return false;
		}
		try {
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.getString(AndyConstants.Params.IS_APPROVED)
						.equals("0")) {
					return false;
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	public RequestDetail parseRequestStatus(String response) {
		if (TextUtils.isEmpty(response)) {
			return null;
		}
		//RequestDetail requestDetail = new RequestDetail();
		try {
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.getBoolean(KEY_SUCCESS)) {

				requestDetail.setJobStatus(AndyConstants.IS_ASSIGNED);
				JSONObject object = jsonObject
						.getJSONObject(AndyConstants.Params.REQUEST);
				if (object.getInt(IS_CANCELLED) == 1) {
					requestDetail.setJobStatus(AndyConstants.NO_REQUEST);
				} else if (object.getInt(IS_WALKER_STARTED) == 0) {
					requestDetail.setJobStatus(AndyConstants.IS_WALKER_STARTED);
					// status = AndyConstants.IS_WALKER_STARTED;
				} else if (object.getInt(IS_WALKER_ARRIVED) == 0) {
					requestDetail.setJobStatus(AndyConstants.IS_WALKER_ARRIVED);
					// status = AndyConstants.IS_WALKER_ARRIVED;
				} else if (object.getInt(IS_WALK_STARTED) == 0) {
					requestDetail.setJobStatus(AndyConstants.IS_WALK_STARTED);
					// status = AndyConstants.IS_WALK_STARTED;
				} else if (object.getInt(IS_WALK_COMPLETED) == 0) {
					preferenceHelper.putIsTripStart(true);
					requestDetail.setJobStatus(AndyConstants.IS_WALK_COMPLETED);

					// status = AndyConstants.IS_WALK_COMPLETED;
				} else if (object.getInt(IS_DOG_RATED) == 0) {
					requestDetail.setJobStatus(AndyConstants.IS_DOG_RATED);
					// status = AndyConstants.IS_DOG_RATED;
				}

				String time = object.optString(AndyConstants.Params.START_TIME);
				// "start_time": "2014-11-20 03:27:37"
				if (!TextUtils.isEmpty(time)) {
					try {
						TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
						Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
								Locale.ENGLISH).parse(time);
						AppLog.Log("TAG", "START DATE---->" + date.toString()
								+ " month:" + date.getMonth());
						preferenceHelper.putRequestTime(date.getTime());
						requestDetail.setStartTime(date.getTime());
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}

				preferenceHelper.putPaymentType(object.getInt("payment_type"));
				try {
					if (object.getString("dest_latitude").length() != 0) {
						preferenceHelper.putClientDestination(new LatLng(object
								.getDouble("dest_latitude"), object
								.getDouble("dest_longitude")));
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}

				JSONObject ownerDetailObject = object
						.getJSONObject(AndyConstants.Params.OWNER);
				requestDetail.setClientName(ownerDetailObject
						.getString(AndyConstants.Params.NAME));
				requestDetail.setClientProfile(ownerDetailObject
						.getString(AndyConstants.Params.PICTURE));
				requestDetail.setClientPhoneNumber(ownerDetailObject
						.getString(AndyConstants.Params.PHONE));
				requestDetail.setClientRating((float) ownerDetailObject
						.optDouble(AndyConstants.Params.RATING));
				requestDetail.setClientLatitude(ownerDetailObject
						.getString(AndyConstants.Params.LATITUDE));
				requestDetail.setClientLongitude(ownerDetailObject
						.getString(AndyConstants.Params.LONGITUDE));
				requestDetail.setDropoffAddress(AndyConstants.Params.DROPOFF_ADDRESS);
				requestDetail.setDestinationLatitude(ownerDetailObject
						.getString(AndyConstants.Params.DESTINATION_LATITUDE));
				requestDetail.setDestinationLongitude(ownerDetailObject
						.getString(AndyConstants.Params.DESTINATION_LONGITUDE));
				//requestDetail.setUnit(object
				//		.getString(AndyConstants.Params.UNIT));

				JSONObject jsonObjectBill = object.optJSONObject("bill");

				if (jsonObjectBill != null) {
					requestDetail.setAmount(jsonObjectBill.getString("total"));
					requestDetail.setTime(jsonObjectBill.getString("time"));
					//requestDetail.setDistance(jsonObjectBill
					//		.getString("distance"));
					requestDetail.setBasePrice(jsonObjectBill
							.getString(BASE_PRICE));
					requestDetail.setDistanceCost(jsonObjectBill
							.getString(AndyConstants.Params.DISTANCE_COST));
					requestDetail.setTimecost(jsonObjectBill
							.getString(AndyConstants.Params.TIME_COST));
					requestDetail.setReferralBonus(jsonObjectBill
							.getString(AndyConstants.Params.REFERRAL_BONUS));
					requestDetail.setPromoBonus(jsonObjectBill
							.getString(AndyConstants.Params.PROMO_BONUS));
					requestDetail.setTotal(new DecimalFormat("0.00")
							.format(Double.parseDouble(jsonObjectBill
									.getString(AndyConstants.Params.TOTAL))));

				}
			} else {
				requestDetail.setUnit(jsonObject
						.getString(AndyConstants.Params.UNIT));
				requestDetail.setDestinationLatitude(jsonObject
						.getString(AndyConstants.Params.DESTINATION_LATITUDE));
				requestDetail.setDestinationLongitude(jsonObject
						.getString(AndyConstants.Params.DESTINATION_LONGITUDE));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return requestDetail;
	}

	public RequestDetail parseAllRequests(String response) {
		if (TextUtils.isEmpty(response)) {
			return null;
		}
		try {
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.getBoolean(KEY_SUCCESS)) {
				JSONArray jsonArray = jsonObject
						.getJSONArray(AndyConstants.Params.INCOMING_REQUESTS);
				if (jsonArray.length() > 0) {
					JSONObject object = jsonArray.getJSONObject(0);

					if (object.getInt(AndyConstants.Params.REQUEST_ID) != AndyConstants.NO_REQUEST) {
						RequestDetail requestDetail = new RequestDetail();
						requestDetail.setRequestId(object
								.getInt(AndyConstants.Params.REQUEST_ID));
						requestDetail.setCredit(object
								.getInt(AndyConstants.Params.CREDIT));
						int timeto_respond = object
								.getInt(AndyConstants.Params.TIME_LEFT_TO_RESPOND);
						if (timeto_respond < 0) {
							return null;
						} else {
							requestDetail.setTimeLeft(timeto_respond);
						}
						JSONObject requestData = object
								.getJSONObject(AndyConstants.Params.REQUEST_DATA);
						JSONObject ownerDetailObject = requestData
								.getJSONObject(AndyConstants.Params.OWNER);
						requestDetail.setClientName(ownerDetailObject
								.getString(AndyConstants.Params.NAME));
						requestDetail.setClientProfile(ownerDetailObject
								.getString(AndyConstants.Params.PICTURE));
						requestDetail.setClientPhoneNumber(ownerDetailObject
								.getString(AndyConstants.Params.PHONE));
						if (!TextUtils.isEmpty(ownerDetailObject
								.getString(AndyConstants.Params.RATING))) {
							requestDetail
									.setClientRating((float) ownerDetailObject
											.getDouble(AndyConstants.Params.RATING));
						} else {
							requestDetail.setClientRating(0);
						}
						requestDetail.setClientLatitude(ownerDetailObject
								.getString(AndyConstants.Params.LATITUDE));
						requestDetail.setClientLongitude(ownerDetailObject
								.getString(AndyConstants.Params.LONGITUDE));
						requestDetail.setPickupType(ownerDetailObject
								.getString(AndyConstants.Params.PICKUP_TYPE));

						preferenceHelper.putDropoffAddress(ownerDetailObject
								.getString(AndyConstants.Params.DROPOFF_ADDRESS));
						preferenceHelper.putPickupAddress(ownerDetailObject
								.getString(AndyConstants.Params.PICKUP_ADDRESS));

						requestDetail.setReward(ownerDetailObject
								.getString(AndyConstants.Params.REWARD));
						requestDetail.setType_pos(ownerDetailObject
								.getString(AndyConstants.Params.TYPE_POS));
						preferenceHelper.putPaymentType(ownerDetailObject
								.getInt("payment_type"));
                        preferenceHelper.putString(AndyConstants.Params.REWARD,ownerDetailObject
                                .getString(AndyConstants.Params.REWARD));

						return requestDetail;
					}

				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}

	public RequestDetail parseCreateRequests(String response) {
		if (TextUtils.isEmpty(response)) {
			return null;
		}
		try {
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.getBoolean(KEY_SUCCESS)) {

					if (jsonObject.getInt(AndyConstants.Params.OVERHERE_REQUEST_ID) != AndyConstants.NO_REQUEST) {
						RequestDetail requestDetail = new RequestDetail();
						requestDetail.setOverhereRequestId(jsonObject
								.getInt(AndyConstants.Params.OVERHERE_REQUEST_ID));
						requestDetail.setPickupType(jsonObject
								.getString(AndyConstants.Params.PICKUP_TYPE));
						requestDetail.setConfirmedWalker(jsonObject
								.getInt(AndyConstants.Params.CONFIRMED_WALKER));
						requestDetail.setCurrentWalker(jsonObject
								.getInt(AndyConstants.Params.CURRENT_WALKER));

						return requestDetail;
					}


			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}

	public RequestDetail parseOverhereRequests(String response) {
		if (TextUtils.isEmpty(response)) {
			return null;
		}
		try {
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.getBoolean(KEY_SUCCESS)) {

				if (jsonObject.getInt(AndyConstants.Params.OVERHERE_REQUEST_ID) != AndyConstants.NO_REQUEST) {
					RequestDetail requestDetail = new RequestDetail();
					requestDetail.setOverhereRequestId(jsonObject
							.getInt(AndyConstants.Params.OVERHERE_REQUEST_ID));

					requestDetail.setConfirmedWalker(jsonObject
							.getInt(AndyConstants.Params.CONFIRMED_WALKER));
					requestDetail.setCurrentWalker(jsonObject
							.getInt(AndyConstants.Params.CURRENT_WALKER));

					return requestDetail;
				}


			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}


	public User parseUserAndStoreToDb(String response) {
		User user = null;
		try {
			JSONObject jsonObject = new JSONObject(response);

			if (jsonObject.getBoolean(KEY_SUCCESS)) {
				user = new User();
				DBHelper dbHelper = new DBHelper(activity);
				user.setUserId(jsonObject.getInt(AndyConstants.Params.ID));
				user.setEmail(jsonObject.optString(AndyConstants.Params.EMAIL));
				user.setFname(jsonObject
						.getString(AndyConstants.Params.FIRSTNAME));
				user.setLname(jsonObject
						.getString(AndyConstants.Params.LAST_NAME));

				user.setAddress(jsonObject
						.getString(AndyConstants.Params.ADDRESS));
				user.setBio(jsonObject.getString(AndyConstants.Params.BIO));
				user.setZipcode(jsonObject
						.getString(AndyConstants.Params.ZIPCODE));
				user.setPicture(jsonObject
						.getString(AndyConstants.Params.PICTURE));
				user.setContact(jsonObject
						.getString(AndyConstants.Params.PHONE));
				user.setCarModel(jsonObject
						.getString(AndyConstants.Params.CAR_MODEL));
				user.setCarNumber(jsonObject
						.getString(AndyConstants.Params.CAR_NUMBER));
				user.setServiceType(jsonObject
						.getString(AndyConstants.Params.TYPE));
				user.setServiceName(jsonObject
						.getString(AndyConstants.Params.TYPE_NAME));
				user.setReferralCode(jsonObject
						.getString(AndyConstants.Params.REFERRAL_CODE));
				dbHelper.createUser(user);


			} else {
				// AndyUtils.showToast(jsonObject.getString(KEY_ERROR),
				// activity);

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return user;
	}


	public RequestDetail parseNotification(String response) {
		if (TextUtils.isEmpty(response)) {
			return null;
		}
		try {

			JSONObject object = new JSONObject(response);

			if (object.getInt(AndyConstants.Params.REQUEST_ID) != AndyConstants.NO_REQUEST) {
				RequestDetail requestDetail = new RequestDetail();
				requestDetail.setRequestId(object
						.getInt(AndyConstants.Params.REQUEST_ID));
				int timeto_respond = object
						.getInt(AndyConstants.Params.TIME_LEFT_TO_RESPOND);
				if (timeto_respond < 0) {
					return null;
				} else {
					requestDetail.setTimeLeft(timeto_respond);
				}

				JSONObject requestData = object
						.getJSONObject(AndyConstants.Params.REQUEST_DATA);
				JSONObject ownerDetailObject = requestData
						.getJSONObject(AndyConstants.Params.OWNER);
				requestDetail.setClientName(ownerDetailObject
						.getString(AndyConstants.Params.NAME));
				requestDetail.setClientProfile(ownerDetailObject
						.getString(AndyConstants.Params.PICTURE));
				requestDetail.setClientPhoneNumber(ownerDetailObject
						.getString(AndyConstants.Params.PHONE));
				requestDetail.setClientRating((float) ownerDetailObject
						.getDouble(AndyConstants.Params.RATING));
				requestDetail.setClientLatitude(ownerDetailObject
						.getString(AndyConstants.Params.LATITUDE));
				requestDetail.setClientLongitude(ownerDetailObject
						.getString(AndyConstants.Params.LONGITUDE));
				preferenceHelper.putPaymentType(ownerDetailObject
						.getInt("payment_type"));
				return requestDetail;

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}

	public ArrayList<ApplicationPages> parsePages(
			ArrayList<ApplicationPages> list, String response) {
		list.clear();
		ApplicationPages applicationPages = new ApplicationPages();
		applicationPages.setId(-1);
		applicationPages.setTitle(activity.getResources().getString(
				R.string.text_profile));
		applicationPages.setData("");
		applicationPages.setIcon("");
		list.add(applicationPages);
		applicationPages = new ApplicationPages();
		applicationPages.setId(-2);
		applicationPages.setTitle(activity.getResources().getString(
				R.string.text_history));
		applicationPages.setData("");
		applicationPages.setIcon("");
		list.add(applicationPages);
		applicationPages = new ApplicationPages();
		applicationPages.setId(-3);
		applicationPages.setTitle(activity.getResources().getString(
				R.string.text_setting));
		applicationPages.setData("");
		applicationPages.setIcon("");
		list.add(applicationPages);
		applicationPages = new ApplicationPages();
		applicationPages.setId(-4);
		applicationPages.setTitle(activity.getResources().getString(
				R.string.text_booking));
		applicationPages.setData("");
		applicationPages.setIcon("");
		list.add(applicationPages);
		applicationPages = new ApplicationPages();
		applicationPages.setId(-5);
		//applicationPages.setTitle(activity.getResources().getString(
		//		R.string.text_share));
		//applicationPages.setTitle(activity.getResources().getString(R.string.text_sharing));
		applicationPages.setTitle(activity.getResources().getString(R.string.title_activity_marketing));
		applicationPages.setData("");
		applicationPages.setIcon("");
		list.add(applicationPages);
		applicationPages = new ApplicationPages();
		applicationPages.setId(-6);
		//applicationPages.setTitle(activity.getResources().getString(
			//	R.string.text_payment));
		applicationPages.setTitle("Bank Account");
		applicationPages.setData("");
		applicationPages.setIcon("");
		list.add(applicationPages);
		/* applicationPages = new ApplicationPages();
		applicationPages.setData("");
		applicationPages.setId(-9);
		applicationPages.setIcon("");
		applicationPages.setTitle("Logout1");
		list.add(applicationPages);
		applicationPages = new ApplicationPages();
		applicationPages.setData("");
		applicationPages.setId(-91);
		applicationPages.setIcon("");
		applicationPages.setTitle("Logout3");
		list.add(applicationPages); */

		if (TextUtils.isEmpty(response)) {
			return list;
		}
		try {
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.getBoolean(KEY_SUCCESS)) {
				JSONArray jsonArray = jsonObject
						.getJSONArray(AndyConstants.Params.INFORMATIONS);
				if (jsonArray.length() > 0) {
					for (int i = 1; i < jsonArray.length();) {
						applicationPages = new ApplicationPages();
						JSONObject object = jsonArray.getJSONObject(i);
						applicationPages.setId(object
								.getInt(AndyConstants.Params.ID));
						applicationPages.setTitle(object
								.getString(AndyConstants.Params.TITLE));
						applicationPages.setData(object
								.getString(AndyConstants.Params.CONTENT));
						applicationPages.setIcon(object
								.getString(AndyConstants.Params.ICON));
						list.add(applicationPages);
						i = i + 2;
						//i=0,2,4 is for overhere; i=1,3,5 is for overthere.

					}
				}

			}
            applicationPages = new ApplicationPages();
            applicationPages.setData("");
            applicationPages.setId(-10);
            applicationPages.setIcon("");
            applicationPages.setTitle("Logout");
            list.add(applicationPages);
			applicationPages = new ApplicationPages();

			applicationPages.setData("");
			applicationPages.setId(-11);
			applicationPages.setIcon("");
			applicationPages.setTitle("");
			list.add(applicationPages);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return list;
	}

	public boolean checkDriverStatus(String response) {
		if (TextUtils.isEmpty(response))
			return false;
		try {
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.getBoolean(KEY_SUCCESS)) {
				if (jsonObject.getInt(AndyConstants.Params.IS_ACTIVE) == 0) {
					return false;
				} else {
					return true;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	public ArrayList<VehicalType> parseTypes(String response,
			ArrayList<VehicalType> list) {
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(response);
			if (jsonObject.getBoolean(KEY_SUCCESS)) {
				JSONArray jsonArray = jsonObject.getJSONArray(TYPES);
				for (int i = 0; i < jsonArray.length(); i++) {
					VehicalType type = new VehicalType();
					JSONObject typeJson = jsonArray.getJSONObject(i);
					type.setBasePrice(typeJson.getString(BASE_PRICE));
					type.setIcon(typeJson.getString(ICON));
					type.setId(typeJson.getInt(ID));
					type.setName(typeJson.getString(AndyConstants.Params.NAME));
					type.setPricePerUnitDistance(typeJson
							.getString(PRICE_PER_UNIT_DISTANCE));
					type.setPricePerUnitTime(typeJson
							.getString(PRICE_PER_UNIT_TIME));
					list.add(type);

				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;

	}

	public ArrayList<History> parseHistory(String response,
			ArrayList<History> list) {
		list.clear();

		if (TextUtils.isEmpty(response)) {
			return list;
		}
		try {
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.getBoolean(KEY_SUCCESS)) {
				JSONArray jsonArray = jsonObject
						.getJSONArray(AndyConstants.Params.REQUESTS);
				if (jsonArray.length() > 0) {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject object = jsonArray.getJSONObject(i);
						History history = new History();
						history.setId(object.getInt(AndyConstants.Params.ID));
						history.setDate(object
								.getString(AndyConstants.Params.DATE));
						history.setDistance(object
								.getString(AndyConstants.Params.DISTANCE));
						history.setTime(object
								.getString(AndyConstants.Params.TIME));
						history.setBasePrice(object.getString(BASE_PRICE));
						history.setDistanceCost(object
								.getString(AndyConstants.Params.DISTANCE_COST));
						history.setTimecost(object
								.getString(AndyConstants.Params.TIME_COST));
/*
						history.setReferralBonus(object
								.getString(AndyConstants.Params.REFERRAL_BONUS));
						history.setPromoBonus(object
								.getString(AndyConstants.Params.PROMO_BONUS));
*/
						history.setTotal(new DecimalFormat("0.00").format(Double
								.parseDouble(object
										.getString(AndyConstants.Params.TOTAL))));
						JSONObject userObject = object
								.getJSONObject(AndyConstants.Params.OWNER);
						history.setFirstName(userObject
								.getString(AndyConstants.Params.FIRSTNAME));
						history.setLastName(userObject
								.getString(AndyConstants.Params.LAST_NAME));
						history.setPhone(userObject
								.getString(AndyConstants.Params.PHONE));
						history.setPicture(userObject
								.getString(AndyConstants.Params.PICTURE));
						history.setEmail(userObject
								.getString(AndyConstants.Params.EMAIL));
						history.setBio(userObject
								.getString(AndyConstants.Params.BIO));
						list.add(history);
					}
				}

			}
			// else {
			// AndyUtils.showToast(jsonObject.getString(KEY_ERROR), activity);
			// }
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	public ArrayList<Booking> parseBooking(String response,
										   ArrayList<Booking> list) {
		list.clear();

		if (TextUtils.isEmpty(response)) {
			return list;
		}
		try {
			Date currentDate = new Date();
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.getBoolean(KEY_SUCCESS)) {
				JSONArray jsonArray = jsonObject
						.getJSONArray(AndyConstants.Params.REQUESTS);
				if (jsonArray.length() > 0) {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject object = jsonArray.getJSONObject(i);
						Booking booking = new Booking();
						booking.setRequestID(object.getInt(AndyConstants.Params.REQUEST_ID));
						booking.setBookingApplied(object.getInt(AndyConstants.Params.BOOKING_APPLIED));
						String str = object.getString(AndyConstants.Params.START_TIME);
						if(!str.equalsIgnoreCase("null")) {
							booking.setDatetTime(formatTime(str));
						} else {
							booking.setDatetTime(formatTime(currentDate.toString()));
						}
						booking.setpkupArea(object
								.getString(AndyConstants.Params.PICKUP_AREA));
						str = object.getString(AndyConstants.Params.DROPOFF_AREA);
						if(!str.equalsIgnoreCase("null")) {
							booking.setdropoffArea(str);
						} else {
							booking.setdropoffArea("Unkown Area");
						}
						booking.setbookingFare(object
								.getString(AndyConstants.Params.BOOKING_FARE));

						list.add(booking);
					}
				}

			}
			// else {
			// AndyUtils.showToast(jsonObject.getString(KEY_ERROR), activity);
			// }
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList<Post> parsePost(String response,
									 ArrayList<Post> list) {
		list.clear();

		if (TextUtils.isEmpty(response)) {
			return list;
		}
		try {
			Date currentDate = new Date();
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.getBoolean(KEY_SUCCESS)) {
				JSONArray jsonArray = jsonObject
						.getJSONArray(AndyConstants.Params.REQUESTS);
				if (jsonArray.length() > 0) {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject object = jsonArray.getJSONObject(i);
						Post post = new Post();
						post.setPostID(object.getInt(AndyConstants.Params.POST_ID));
						//post.setBookingApplied(object.getInt(AndyConstants.Params.BOOKING_APPLIED));
						post.setPostTimes(object.getInt(AndyConstants.Params.POST_TIMES));
						post.setTitle(object.getString(AndyConstants.Params.POST_TITILE));
						post.setText(object.getString(AndyConstants.Params.POST_TEXT));
						post.setPosition(object.getString(AndyConstants.Params.POST_POSITION));
						post.setColor(object.getString(AndyConstants.Params.POST_COLOR));
						post.setImageName(object.getString(AndyConstants.Params.POST_IMAGE_NAME));

						list.add(post);
					}
				}

			}
			// else {
			// AndyUtils.showToast(jsonObject.getString(KEY_ERROR), activity);
			// }
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	public ArrayList<Customer> parseCustomer(String response,
										   ArrayList<Customer> list) {
		list.clear();

		if (TextUtils.isEmpty(response)) {
			return list;
		}
		try {
			Date currentDate = new Date();
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.getBoolean(KEY_SUCCESS)) {
				JSONArray jsonArray = jsonObject
						.getJSONArray(AndyConstants.Params.REQUESTS);
				if (jsonArray.length() > 0) {
					Customer customer = new Customer();
					customer.setCustomerName("Name");
					customer.setMemberDate("Joined Date");
					customer.setServiceTimes("Service Times");
					customer.setReviewRate("Rating");
					customer.setPkDate("None");
					customer.setpkupArea("None");
					list.add(customer);
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject object = jsonArray.getJSONObject(i);
						customer = new Customer();
						customer.setCustomerName(object.getString(AndyConstants.Params.CUSTOMER_FIRST_NAME)
						+" "+ object.getString(AndyConstants.Params.CUSTOMER_LAST_NAME));
						customer.setMemberDate(object.getString(AndyConstants.Params.MEMBER_DATE));
						customer.setServiceTimes(object.getString(AndyConstants.Params.SERVICE_TIMES));
						customer.setReviewRate(object.getString(AndyConstants.Params.REVIEW_RATE));
						customer.setPkDate(object.getJSONObject(AndyConstants.Params.PICKUP_DATE).getString("date"));
						customer.setpkupArea(object.getString(AndyConstants.Params.PICKUP_AREA));

						list.add(customer);
					}
				}

			}
			// else {
			// AndyUtils.showToast(jsonObject.getString(KEY_ERROR), activity);
			// }
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * @param response
	 * @return
	 */
	public boolean parseAvaibilty(String response) {
		if (TextUtils.isEmpty(response))
			return false;
		try {
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.getBoolean(KEY_SUCCESS)) {
				if (jsonObject.getInt(AndyConstants.Params.IS_ACTIVE) == 1) {
					return true;
				}
			}

			// else {
			// AndyUtils.showToast(jsonObject.getString(KEY_ERROR), activity);
			// }
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
	public boolean parseNewBooking(String response) {
		if (TextUtils.isEmpty(response))
			return false;
		try {
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.getBoolean(KEY_SUCCESS)) {
				if (jsonObject.getInt(AndyConstants.Params.NEW_BOOKING) == 1) {
					return true;
				}
			}

			// else {
			// AndyUtils.showToast(jsonObject.getString(KEY_ERROR), activity);
			// }
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * @param response
	 * @param points
	 */
	public ArrayList<LatLng> parsePathRequest(String response,
			ArrayList<LatLng> points) {
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(response);
			if (jsonObject.getBoolean(KEY_SUCCESS)) {
				JSONArray jsonArray = jsonObject
						.getJSONArray(AndyConstants.Params.LOCATION_DATA);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject json = jsonArray.getJSONObject(i);
					points.add(new LatLng(Double.parseDouble(json
							.getString(AndyConstants.Params.LATITUDE)), Double
							.parseDouble(json
									.getString(AndyConstants.Params.LONGITUDE))));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return points;
	}

	private String formatTime(String strDate) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date time = new Date();
		try {
			time = sf.parse(strDate);
			//Log.i("Return Time", "" + time.getHours() + ":" + time.getMinutes());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time.getTime());
		return sdf.format(cal.getTime());
		//return time.toString();

	}

	public ArrayList<Card> parseCards(String response, ArrayList<Card> listCards) {

		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(response);
			if (jsonObject.getBoolean(KEY_SUCCESS)) {
				JSONArray jsonArray = jsonObject.getJSONArray(PAYMENTS);
				for (int i = 0; i < jsonArray.length(); i++) {
					Card card = new Card();
					JSONObject cardJson = jsonArray.getJSONObject(i);
					// card.setStripeToken(cardJson.getString(STRIPE_TOKEN));
					card.setLastFour(cardJson.getString(LAST_FOUR));
					card.setStripeToken(cardJson.getString(CUSTOMER_ID));
					card.setId(cardJson.getInt(ID));
					// card.setCreatedAt(cardJson.getString(CREATED_AT));
					// card.setUpdatedAt(cardJson.getString(UPDATED_AT));
					card.setWalkerId(cardJson.getString(WALKER_ID));
					card.setCardType(cardJson.getString(CARD_TYPE));
					if (cardJson.getInt(IS_DEFAULT) == 1)
						card.setDefault(true);
					else
						card.setDefault(false);
					listCards.add(card);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listCards;
	}

	public boolean parseAchievement(String response) {
		if (TextUtils.isEmpty(response))
			return false;
		try {
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.getBoolean(KEY_SUCCESS)) {
				preferenceHelper.putString(AndyConstants.Params.HAIL_TOTAL,jsonObject
						.getString(AndyConstants.Params.HAIL_TOTAL));
				preferenceHelper.putString(AndyConstants.Params.HAIL_REWARDED,jsonObject
						.getString(AndyConstants.Params.HAIL_REWARDED));
				preferenceHelper.putString(AndyConstants.Params.HAIL_BALANCE,jsonObject
						.getString(AndyConstants.Params.HAIL_BALANCE));
				preferenceHelper.putString(AndyConstants.Params.BOOKING_TOTAL,jsonObject
						.getString(AndyConstants.Params.BOOKING_TOTAL));
				preferenceHelper.putString(AndyConstants.Params.BOOKING_REWARDED,jsonObject
						.getString(AndyConstants.Params.BOOKING_REWARDED));
				preferenceHelper.putString(AndyConstants.Params.BOOKING_BALANCE,jsonObject
						.getString(AndyConstants.Params.BOOKING_BALANCE));
                preferenceHelper.putString(AndyConstants.Params.HELP_TOTAL,jsonObject
                        .getString(AndyConstants.Params.HELP_TOTAL));
                preferenceHelper.putString(AndyConstants.Params.HELP_REWARDED,jsonObject
                        .getString(AndyConstants.Params.HELP_REWARDED));
                preferenceHelper.putString(AndyConstants.Params.HELP_BALANCE,jsonObject
                        .getString(AndyConstants.Params.HELP_BALANCE));
				return true;

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}


}
