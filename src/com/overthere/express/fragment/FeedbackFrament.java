package com.overthere.express.fragment;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.androidquery.AQuery;
import com.overthere.express.MapActivity;
import com.overthere.express.R;
import com.overthere.express.base.BaseMapFragment;
import com.overthere.express.model.RequestDetail;
import com.overthere.express.parse.AsyncTaskCompleteListener;
import com.overthere.express.parse.VolleyHttpRequest;
import com.overthere.express.utills.AndyConstants;
import com.overthere.express.utills.AndyUtils;
import com.overthere.express.utills.AppLog;
import com.overthere.express.utills.PreferenceHelper;
import com.overthere.express.widget.MyFontEdittextView;
import com.overthere.express.widget.MyFontTextView;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.view.View.GONE;

/**
 * @author Kishan H Dhamat
 * 
 */
public class FeedbackFrament extends BaseMapFragment implements
		AsyncTaskCompleteListener {

	private MyFontEdittextView etFeedbackComment;
	private ImageView ivDriverImage;
	private RatingBar ratingFeedback;
	private MyFontTextView tvTime, tvDistance, tvClientName;

	private final String TAG = "FeedbackFrament";
	private AQuery aQuery;
	private MyFontTextView tvAmount;
	private String paymentMode;
	private RequestQueue requestQueue;
	private RequestDetail requestDetail;
	private Timer timerFare;
	private boolean isFareContinue;
	private Dialog mDialog = null;
	private  int fareDelay = 0;
	private  boolean iswarned = false;
	private  boolean isPaying = false;
    private  long mLastClickTime = 0;
	private Handler upHandler = new Handler();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View feedbackFragmentView = inflater.inflate(
				R.layout.fragment_feedback, container, false);
		requestQueue = Volley.newRequestQueue(mapActivity);

		etFeedbackComment = (MyFontEdittextView) feedbackFragmentView
				.findViewById(R.id.etFeedbackComment);
		tvTime = (MyFontTextView) feedbackFragmentView
				.findViewById(R.id.tvFeedBackTime);
		tvDistance = (MyFontTextView) feedbackFragmentView
				.findViewById(R.id.tvFeedbackDistance);
		tvAmount = (MyFontTextView) feedbackFragmentView
				.findViewById(R.id.tvFeedbackAmount);
		ratingFeedback = (RatingBar) feedbackFragmentView
				.findViewById(R.id.ratingFeedback);
		ivDriverImage = (ImageView) feedbackFragmentView
				.findViewById(R.id.ivFeedbackDriverImage);
		tvClientName = (MyFontTextView) feedbackFragmentView
				.findViewById(R.id.tvClientName);

		mapActivity.setActionBarTitle(getResources().getString(
				R.string.text_feedback));

		feedbackFragmentView.findViewById(R.id.tvFeedbackSubmit)
				.setOnClickListener(this);

		// feedbackFragmentView.findViewById(R.id.tvFeedbackSkip)
		// .setOnClickListener(this);
/*		SupportMapFragment f = (SupportMapFragment) getFragmentManager()
				.findFragmentById(R.id.jobMap);
		if (f != null) {
			try {
				getFragmentManager().beginTransaction().remove(f).commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Runtime.getRuntime().gc();
*/
		return feedbackFragmentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		aQuery = new AQuery(mapActivity);
		requestDetail = (RequestDetail) getArguments()
				.getSerializable(AndyConstants.REQUEST_DETAIL);
		aQuery.id(ivDriverImage).image(requestDetail.getClientProfile());
		timerFare = null;
		fareDelay = 0;
		iswarned = false;
		isPaying = false;
		
		tvTime.setText((int) (Double.parseDouble(requestDetail.getTime()))
				+ " " + getString(R.string.text_mins));
		if(requestDetail.getDistance() == null) tvDistance.setText("0 KMS");
		else {
			DecimalFormat df = new DecimalFormat("0.0");
			tvDistance.setText(df.format(preferenceHelper.getDistance()) + " KMS");
		}
        String reward = preferenceHelper.getString(AndyConstants.Params.REWARD);
		if(reward.equalsIgnoreCase("null")) tvAmount.setVisibility(GONE);
		else if (reward.isEmpty()) {
			tvAmount.setVisibility(GONE);
		} else {
			float amount = Float.parseFloat(reward);
			if(amount <= 0.01) reward = "";
			if (!reward.isEmpty()) tvAmount.setText("$"+reward);
				//else tvJobReward.setText("$0");
			else tvAmount.setVisibility(GONE);
		}
		if (preferenceHelper.getPaymentType() == AndyConstants.CASH)
			paymentMode = getString(R.string.text_type_cash);
		else {
			paymentMode = getString(R.string.text_type_card);
			//String basePrice, String total,
			//							   String distanceCost, String timeCost, String distance,
			//							   String time,
			//							   String referralBonus, String promoBonus, String btnTitle
		//	showBillDialog(requestDetail.getBasePrice(),reward,requestDetail.getDistanceCost(),
		//			requestDetail.getTimecost(),tvTime.getText().toString(),"0",
		//			"0","0","0");
		}


		tvClientName.setText(requestDetail.getClientName());


	}

	@Override
	public void onClick(View v) {
		// Preventing multiple clicks, using threshold of 1 second
		if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
			return;
		}
		mLastClickTime = SystemClock.elapsedRealtime();

		switch (v.getId()) {

		case R.id.tvFeedbackSubmit:

			if (ratingFeedback.getRating() == 0) {
				AndyUtils.showToast(
						mapActivity.getResources().getString(
								R.string.text_empty_rating), mapActivity);
			} else {
				String mbody="Thank you for feedback";
				notifications(mbody,R.raw.thank_feedback);

				giveRating();
				//prePayment();
			}
			break;

		default:
			break;
		}
	}

	// giving feedback for perticular job
	private void giveRating() {
		if (!AndyUtils.isNetworkAvailable(mapActivity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet),
					mapActivity);
			return;
		}

		AndyUtils.showCustomProgressDialog(mapActivity, "", getResources()
				.getString(R.string.progress_rating), false);

		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(getActivity()).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL, Lserver + AndyConstants.ServiceType.RATING);
		map.put(AndyConstants.Params.ID, preferenceHelper.getUserId());
		map.put(AndyConstants.Params.TOKEN, preferenceHelper.getSessionToken());
		map.put(AndyConstants.Params.REQUEST_ID,
				String.valueOf(preferenceHelper.getRequestId()));
		map.put(AndyConstants.Params.RATING,
				String.valueOf(ratingFeedback.getRating()));
		map.put(AndyConstants.Params.COMMENT, etFeedbackComment.getText()
				.toString().trim());

		// new HttpRequester(mapActivity, map, AndyConstants.ServiceCode.RATING,
		// this);

		requestQueue.add(new VolleyHttpRequest(Method.POST, map,
				AndyConstants.ServiceCode.RATING, this, this));
	}

	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		AndyUtils.removeCustomProgressDialog();
		String[] part = response.split("\\{",2);
		response = "{" + part[1];

		switch (serviceCode) {
		case AndyConstants.ServiceCode.RATING:
			//AppLog.Log(TAG, "rating response" + response);
			if (parseContent.isSuccess(response)) {
				preferenceHelper.clearRequestData();

					String msg = "";
					try {
						JSONObject jsonObject = new JSONObject(response);
						msg = jsonObject.getString("message");

					} catch (JSONException e) {
						e.printStackTrace();
					}
					AndyUtils.showToast(msg, mapActivity);
					String mbody ="Thank you for your feedback";
					notifications(mbody, R.raw.thank_feedback);
				upHandler.postDelayed(Uprunnable, 2000);
				//mapActivity.addFragment(new ClientRequestFragment(), false,
				//		AndyConstants.CLIENT_REQUEST_TAG, true);
			}

			break;

			case AndyConstants.ServiceCode.FARE_UPDATE:
				//AppLog.Log(TAG, "fare response" + response);
				if (parseContent.isSuccess(response)) {
					AndyUtils.showToastCentre(
							mapActivity.getResources().getString(
									R.string.toast_fare_sent), mapActivity);
					String mbody = "service fare has been sent";
					notifications(mbody, R.raw.fare_sent);
					//add start timer to check user confirmation on fare
					fareDelay = 0;
					startCheckingFare();
				}
			break;
			case AndyConstants.ServiceCode.GET_FARE_CONFIRMATION:
				//AppLog.Log(TAG, "fare response" + response);
				if (parseContent.isSuccess(response)) {
					AndyUtils.showToastCentre(
							mapActivity.getResources().getString(
									R.string.toast_fare_confirmed), mapActivity);
					String mbody = "service fare has been confirmed";
					notifications(mbody, R.raw.fare_confirmed);
					//if successed, stop timer to check user confirmation on fare
					//mDialog.dismiss();
					stopCheckingFare();
					if(!isPaying) walkPayment();
					isPaying = true;
				} else {
					AndyUtils.showToastCentre(
							mapActivity.getResources().getString(
									R.string.progress_please_wait), mapActivity);
				}
				fareDelay = fareDelay + 1;
				if(fareDelay > 4) {
					AndyUtils.showToastCentre(
							mapActivity.getResources().getString(
									R.string.toast_fare_confirmed), mapActivity);
					String mbody = "service fare has been confirmed";
					notifications(mbody, R.raw.fare_confirmed);
					//if successed, stop timer to check user confirmation on fare
					//mDialog.dismiss();
					stopCheckingFare();
					if (!isPaying) walkPayment();
					isPaying = true;
				}
			break;
			case AndyConstants.ServiceCode.WALK_PAYMENT:
				//AppLog.Log(TAG, "walk completed response " + response);
				if (parseContent.isSuccess(response)) {
					mDialog.dismiss();
					isPaying = true;
					AndyUtils.showToastCentre("Payment successed!", mapActivity);
					String mbody="Payment successed";
					notifications(mbody, R.raw.payment_ok);

				} else {
					//show warning message: credit payment failed
					AndyUtils.showToastCentre("Warning: payment failed!", mapActivity);
					String mbody="Payment failed";
					notifications(mbody, R.raw.payment_failed);

					if(iswarned) mDialog.dismiss();
					iswarned = true;
					isPaying = false;

				}
				break;
			case AndyConstants.ServiceCode.PRE_PAYMENT:
				//AppLog.Log(TAG, "walk completed response " + response);
				if (parseContent.isSuccess(response)) {
					//mDialog.dismiss();
					//isPaying = true;
					AndyUtils.showToastCentre("Payment successed!", mapActivity);
					String mbody="Payment successed";
					notifications(mbody, R.raw.payment_ok);

				} else {
					//show warning message: credit payment failed
					AndyUtils.showToastCentre("Warning: payment failed!", mapActivity);
					String mbody="Payment failed";
					notifications(mbody, R.raw.payment_failed);

					//if(iswarned) mDialog.dismiss();
					//iswarned = true;
					//isPaying = false;

				}
				giveRating();
				break;
		default:
			break;
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		// TODO Auto-generated method stub
		//AppLog.Log("TAG", error.getMessage());

	}

	public void showBillDialog(String basePrice, String total,
							   String distanceCost, String timeCost, String distance, String time,
							   String referralBonus, String promoBonus, String btnTitle) {
		/*final Dialog mDialog = new Dialog(this,
				android.R.style.Theme_Translucent_NoTitleBar);*/
		mDialog = new Dialog(getContext(),android.R.style.Theme_Translucent_NoTitleBar);

		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		mDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mDialog.setContentView(R.layout.bill_layout);
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		DecimalFormat perHourFormat = new DecimalFormat("0.0");
		String basePriceDouble = String.valueOf(decimalFormat.format(Double
				.parseDouble(basePrice)));
		// String basePricetmp = String.valueOf(decimalFormat.format(Double
		// .parseDouble(basePrice)));
		String totalTmp = String.valueOf(decimalFormat.format(Double
				.parseDouble(total)));
		String distCostTmp = String.valueOf(decimalFormat.format(Double
				.parseDouble(distanceCost)));
		String timeCostDouble = String.valueOf(decimalFormat.format(Double
				.parseDouble(timeCost)));
		String referralBonusDouble = String.valueOf(decimalFormat.format(Double
				.parseDouble(referralBonus)));
		String promoBonusDouble = String.valueOf(decimalFormat.format(Double
				.parseDouble(promoBonus)));

		//((TextView) mDialog.findViewById(R.id.tvBasePrice)).setText(basePriceDouble);
		if (Double.parseDouble(distanceCost) != 0) {
			((TextView) mDialog.findViewById(R.id.tvBillDistancePerMile))
					.setText(String.valueOf(perHourFormat.format((Double
							.parseDouble(distanceCost) / Double
							.parseDouble(distance))))
							+ getResources().getString(
							R.string.text_cost_per_mile));
		} else {
			((TextView) mDialog.findViewById(R.id.tvBillDistancePerMile))
					.setText(String.valueOf(perHourFormat.format(0.00))
							+ getResources().getString(
							R.string.text_cost_per_mile));
		}
		if (Double.parseDouble(timeCost) != 0) {
			((TextView) mDialog.findViewById(R.id.tvBillTimePerHour))
					.setText(String.valueOf(perHourFormat.format((Double
							.parseDouble(timeCost) / Double.parseDouble(time))))
							+ getResources().getString(
							R.string.text_cost_per_hour));
		} else {
			((TextView) mDialog.findViewById(R.id.tvBillTimePerHour))
					.setText(String.valueOf(perHourFormat.format((0.00)))
							+ getResources().getString(
							R.string.text_cost_per_hour));
		}
		((TextView) mDialog.findViewById(R.id.tvDis1)).setText(distCostTmp);

		((TextView) mDialog.findViewById(R.id.tvTime1)).setText(timeCostDouble);
         final AutoCompleteTextView tvTotal = (AutoCompleteTextView) mDialog.findViewById(R.id.tvTotal1);
		//((AutoCompleteTextView) mDialog.findViewById(R.id.tvTotal1)).setText(totalTmp);
		((TextView) mDialog.findViewById(R.id.tvReferralBonus))
				.setText(referralBonusDouble);

		((TextView) mDialog.findViewById(R.id.tvPromoBonus))
				.setText(promoBonusDouble);

		Button btnConfirm = (Button) mDialog
				.findViewById(R.id.btnBillDialogClose);
		if (!TextUtils.isEmpty(btnTitle)) {
			btnConfirm.setText(btnTitle);
		}

		btnConfirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Preventing multiple clicks, using threshold of 1 second
				if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
					return;
				}
				mLastClickTime = SystemClock.elapsedRealtime();

				if (preferenceHelper.getPaymentType() == AndyConstants.CREDIT){
					requestDetail.setTotal(tvTotal.getText().toString());
			        updateFare();
		         } else {
					mDialog.dismiss();
				}
			}
		});

		mDialog.setCancelable(true);
		mDialog.show();

	}

	public void updateFare() {
		if (!AndyUtils.isNetworkAvailable(mapActivity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet),
					mapActivity);
			return;
		}

		AndyUtils.showCustomProgressDialog(mapActivity, "", getResources()
				.getString(R.string.progress_fare), false);

		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(getActivity()).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL, Lserver + AndyConstants.ServiceType.FARE_UPDATE);
		map.put(AndyConstants.Params.ID, preferenceHelper.getUserId());
		map.put(AndyConstants.Params.TOKEN, preferenceHelper.getSessionToken());
		map.put(AndyConstants.Params.REQUEST_ID,
				String.valueOf(preferenceHelper.getRequestId()));
		map.put(AndyConstants.Params.TOTAL,
				String.valueOf(requestDetail.getTotal()));
		requestQueue.add(new VolleyHttpRequest(Method.POST, map,
				AndyConstants.ServiceCode.FARE_UPDATE, this, this));

	}

	private void startCheckingFare() {
		AppLog.Log(TAG, "start checking fareConfirmation");
		isFareContinue = true;
		if (timerFare == null) {
			timerFare = new Timer();
			timerFare.scheduleAtFixedRate(new TimerFareConfirmation(),
					AndyConstants.DELAY, AndyConstants.FARE_SCHEDULE);
		}
	}

	private void stopCheckingFare() {
		AppLog.Log(TAG, "stop checking upcomingRequests");
		isFareContinue = false;
		if (timerFare != null) {
			timerFare.cancel();
			timerFare = null;
		}
	}

	private class TimerFareConfirmation extends TimerTask {
		@Override
		public void run() {
			if (isFareContinue) {
				getFareConfirmation();
			}
		}
	}

	public void getFareConfirmation() {
		if (!AndyUtils.isNetworkAvailable(mapActivity)) {
			return;
		}

		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(getActivity()).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL,
				Lserver + AndyConstants.ServiceType.GET_FARE_CONFIRMATION
						+ AndyConstants.Params.ID + "="
						+ preferenceHelper.getUserId() + "&"
						+ AndyConstants.Params.TOKEN + "="
						+ preferenceHelper.getSessionToken() + "&"
						+ AndyConstants.Params.REQUEST_ID + "="
						+ preferenceHelper.getRequestId());

		requestQueue.add(new VolleyHttpRequest(Method.GET, map,
				AndyConstants.ServiceCode.GET_FARE_CONFIRMATION, this, this));
	}

	private void walkPayment() {
		if (!AndyUtils.isNetworkAvailable(mapActivity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet),
					mapActivity);
			return;
		}

		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(getActivity()).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL, Lserver + AndyConstants.ServiceType.WALK_PAYMENT);
		map.put(AndyConstants.Params.ID, preferenceHelper.getUserId());
		map.put(AndyConstants.Params.REQUEST_ID,
				String.valueOf(preferenceHelper.getRequestId()));
		map.put(AndyConstants.Params.TOKEN, preferenceHelper.getSessionToken());
		map.put(AndyConstants.Params.LATITUDE,
				preferenceHelper.getWalkerLatitude());
		map.put(AndyConstants.Params.LONGITUDE,
				preferenceHelper.getWalkerLongitude());
		map.put(AndyConstants.Params.DISTANCE, preferenceHelper.getDistance()
				+ "");
		map.put(AndyConstants.Params.TIME, requestDetail.getTime());

		requestQueue.add(new VolleyHttpRequest(Method.POST, map,
				AndyConstants.ServiceCode.WALK_PAYMENT, this, this));
	}

	public void notifications(CharSequence mbody, int voice){
		Uri sound = Uri.parse("android.resource://" + mapActivity.getPackageName() + "/" + voice);
		Notification noti = new Notification.Builder(mapActivity)
				.setContentTitle(getResources().getString(R.string.app_name))
				.setContentText(mbody)
				.setSmallIcon(R.drawable.ic_launcher).setAutoCancel(true)
				.setSound(sound)
				.build();
		NotificationManager notificationManager = (NotificationManager) mapActivity.getSystemService(NOTIFICATION_SERVICE);
		noti.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(1000, noti);
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		clearMemory();
	}

	private void clearMemory() {
		etFeedbackComment=null;
		ivDriverImage=null;
		ratingFeedback=null;
		tvTime=null;
		tvDistance=null;
		tvClientName=null;
		aQuery=null;
		tvAmount=null;
		requestQueue=null;
		requestDetail=null;
		timerFare=null;
		mDialog = null;
		Runtime.getRuntime().gc();
	}
    private void restartOverHere() {
        Intent mStartActivity = new Intent(mapActivity, MapActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(mapActivity, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) mapActivity.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC,System.currentTimeMillis()+100,mPendingIntent);
        System.exit(0);
    }
	private Runnable Uprunnable = new Runnable() {
		@Override
		public void run() {
			restartOverHere();
		};
	};

	private void prePayment() {
		if (!AndyUtils.isNetworkAvailable(mapActivity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet),
					mapActivity);
			return;
		}
		AndyUtils.showToast(
				"prepayment2",
				mapActivity);
		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(getActivity()).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL, Lserver + AndyConstants.ServiceType.PRE_PAYMENT);
		map.put(AndyConstants.Params.ID, preferenceHelper.getUserId());
		map.put(AndyConstants.Params.REQUEST_ID,
				String.valueOf(preferenceHelper.getRequestId()));
		map.put(AndyConstants.Params.TOKEN, preferenceHelper.getSessionToken());
		map.put(AndyConstants.Params.REWARD, preferenceHelper.getString(AndyConstants.Params.REWARD));
		map.put(AndyConstants.Params.TIME, requestDetail.getTime());
		requestQueue.add(new VolleyHttpRequest(Method.POST, map,
				AndyConstants.ServiceCode.PRE_PAYMENT, this, this));

	}


}
