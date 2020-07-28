package com.overthere.express.fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.NotificationCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.androidquery.AQuery;
import com.overthere.express.MapActivity;
import com.overthere.express.R;
import com.overthere.express.base.BaseMapFragment;
import com.overthere.express.locationupdate.LocationHelper;
import com.overthere.express.locationupdate.LocationHelper.OnLocationReceived;
import com.overthere.express.model.RequestDetail;
import com.overthere.express.parse.AsyncTaskCompleteListener;
import com.overthere.express.parse.VolleyHttpRequest;
import com.overthere.express.utills.AndyConstants;
import com.overthere.express.utills.AndyUtils;
import com.overthere.express.utills.AppLog;
import com.overthere.express.widget.MyFontButton;
import com.overthere.express.widget.MyFontTextView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.widget.ImageButton;
import android.location.Geocoder;
import android.location.Address;
import com.overthere.express.adapter.PlacesAutoCompleteAdapter;
import android.widget.AdapterView.OnItemClickListener;
import com.overthere.express.utills.PreferenceHelper;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.ViewSwitcher;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.overthere.express.R.drawable.img1;


/**
 * @author OverHere Team
 * 
 */

public class ClientRequestFragment extends BaseMapFragment implements
	AsyncTaskCompleteListener, OnLocationReceived {
	private GoogleMap mMap;
	private final String TAG = "ClientRequestFragment";
	private LinearLayout llAcceptReject;
	private View llUserDetailView;
	// private ProgressBar pbTimeLeft;
	private MyFontButton btnClientAccept, btnClientReject,
			btnClientReqRemainTime;
    private MyFontButton btnCancelJob, btnAcceptJob;
	// private RelativeLayout rlTimeLeft;
	private boolean isContinueRequest, isAccepted = false;
	private Timer timer;
	private Timer overhereTimer = null;
	private SeekbarTimer seekbarTimer;
	private RequestDetail requestDetail;
	private RequestDetail overhereDetail;
	private Marker markerDriverLocation, markerClientLocation;
	private Location location;
	private LocationHelper locationHelper;
	private MyFontTextView tvClientName;// , tvClientPhoneNumber;
	private RatingBar tvClientRating;
	private ImageView ivClientProfilePicture;
	private AQuery aQuery;
	private newRequestReciever requestReciever;
	private View clientRequestView;
	private MapView mMapView;
	private Bundle mBundle;
	private MyFontTextView tvApprovedClose;
	private boolean isApprovedCheck = true, loaded = false;
	private Dialog mDialog;
	private SoundPool soundPool;
	private int soundid;
	private RequestQueue requestQueue;
	private int paymentMode;

	private Button btnGoOffline;
	private RelativeLayout relMap;
	private LinearLayout linearOffline;
	private LinearLayout linearPhone;
	private boolean cameraFollow = true;
	private ImageButton btnMyLocation;
	private ImageButton btnOverHere;
	private Dialog dialog, jobDialog;
	int walker_id = 0;
	int T_NotAccept = 0;
	int currentWalker = 0;
	int confirmedWalker = 0;
	private Spinner pickupdate;
	private String pickuphour;
	private String pickupminute;
	private Spinner pickupserviceTypes;
	private AutoCompleteTextView pickupAddress;
	private AutoCompleteTextView dropoffAddress;
	private AutoCompleteTextView cPhone;
	private AutoCompleteTextView serviceReward;
	private Location myLocation, myOldLocation;
	private Address address;
	private String strAddress = null;
	private LatLng currentLatLng;
	private EditText et = null;
	private long mLastClickTime = 0, vLastTime = 0;

	private int overHereRequestID = 0;
	private String pickupType = "overhere";
	private boolean isOverhereRequest = false;

	private final String[] Dates = new String[]{"Now", "Today", "Tomorrow"};
	static final String[] DriverServiceTypes = new String[] {"|| TRAVEL ||","General Taxi", "|| EXPRESS ||", "Gifts", "Documents", "Business Orders", "Shopping Items", "Borrow/Return","|| HELP ||",
			"Jump Leads", "Tyre Change", "Buy Petrol"};
    static final String[] ServiceTypes = new String[] {"|| TRAVEL ||","General Taxi", "Silver Taxi", "Station Wagon", "Wheel Chair",
            "Mini Van", "|| EXPRESS ||", "Gifts", "Documents", "Business Orders", "Shopping Items", "Borrow/Return","|| HELP ||",
            "Jump Leads", "Tyre Change", "Buy Petrol"};
    static final String[] oldDriverServiceTypes = new String[] {"|| TRAVEL ||","General Taxi", "Silver Taxi", "Station Wagon", "Wheel Chair",
            "Mini Van", "|| EXPRESS ||", "Gifts", "Documents", "Business Orders", "Shopping Items", "Borrow/Return","|| HELP ||",
            "Jump Leads", "Tyre Change", "Buy Petrol"};
	private ImageSwitcher imgSlider;
	//private VideoView mVideoView = null;
	private boolean isSliderOn = false;
	private Integer[] mImageIds = {img1,
			R.drawable.img2, R.drawable.img3};
	private int imgIndex = 0;
	private Handler adshandler = new Handler();
	private WebView adsView;
	private int nAds = 19;
	private int[] switchTimes = new int[]{10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10};
	private String[] Ads = new String [] {"ads1","ads2","ads3","ads4","ads5","ads6",
	                       "ads7","ads8","ads9","ads10","ads12","ads13","ads14","ads15","ads16",
			"ads17","ads18","ads19","ads20"};
	private final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
	private final int MY_PERMISSION_ACCESS_FINE_LOCATION = 200;
	private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 100;
	private ArrayList<Bitmap> adsBitmaps = new ArrayList<Bitmap>(nAds);
	private ArrayList<BitmapDrawable> adsBitmapDrawables = new ArrayList<BitmapDrawable>(nAds);
	private ArrayList<Canvas> adsCanvas = new ArrayList<>(nAds);
	private ArrayList<Paint> adsPaint = new ArrayList<>(nAds);
	private ImageButton buttonPlay, buttonStopPlay;
	private MediaPlayer player = null;
	private int adsRepeat = 0;
	private String latitude, longitude;
	private float bearing;
	private boolean is_moving = false, origin = true;
    private int[] IMG = new int[] {R.drawable.img1,R.drawable.img2,R.drawable.img3};
//	private int[] VID = new int[] {R.raw.videoads1,R.raw.videoads2,R.raw.videoads3};
    private AutoCompleteTextView tvJobtitle,tvPickupAddress,tvDropOffAddress,tvReward;
    private TimePickerDialog mTimePicker;
    private Button btnTimePicker;
    private long fileLength = 0;
    private int postDuration = 0;
	private ArrayList<String> postTexts = new ArrayList<String>(nAds);
	private TextView tvPost;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		//clientRequestView = inflater.inflate(R.layout.fragment_client_requests,
		//		container, false);
		clientRequestView = inflater.inflate(R.layout.fragment_client_request_webview,
				container, false);
		if (ContextCompat.checkSelfPermission(mapActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

			ActivityCompat.requestPermissions(mapActivity, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_ACCESS_COARSE_LOCATION);
		}
		if (ContextCompat.checkSelfPermission(mapActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(mapActivity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE);
		}

		try {
			MapsInitializer.initialize(getActivity());
		} catch (Exception e) {
			e.printStackTrace();
		}
		//clientRequestView.findViewById(R.id.buttonPlay).setVisibility(View.INVISIBLE);

		adsView = (WebView) clientRequestView
				.findViewById(R.id.adsView);
		adsView.setWebViewClient(new WebViewClient());
		adsView.getSettings().setAppCacheEnabled(false);
		//adsView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		//adsView.getSettings().setJavaScriptEnabled(true);
		//adsView.setScrollbarFadingEnabled(true);
		//image size: 750x1080
		adsView.setInitialScale(150);
		//initial webview
		//adsView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		//mVideoView = (VideoView)clientRequestView.findViewById(R.id.videoView);
		imgSlider = (ImageSwitcher) clientRequestView
				.findViewById(R.id.imageSlider);
		imgSlider.setFactory(new ViewSwitcher.ViewFactory() {
			@Override
			public View makeView() {
				ImageView myView = new ImageView(getActivity());
				myView.setScaleType(ImageView.ScaleType.FIT_XY);
				myView.setLayoutParams(new
						ImageSwitcher.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
						FrameLayout.LayoutParams.MATCH_PARENT));
				return myView;
			}
		});
		Animation in = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left);
		Animation out = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_out_right);
		imgSlider.setInAnimation(in);
		imgSlider.setOutAnimation(out);

		/* mVideoView.setOnTouchListener(new View.OnTouchListener() {
			adsView.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				adshandler.removeCallbacks(adsrunnable);
				adshandler.postDelayed(adsrunnable, 30000);
				mVideoView.setVisibility(GONE);
				relMap.setVisibility(VISIBLE);
				btnGoOffline.setVisibility(VISIBLE);
				mapActivity.getSupportActionBar().show();
				return false;
			}
		}); */

		imgSlider.setOnTouchListener(new View.OnTouchListener() {
			//adsView.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				adshandler.removeCallbacks(adsrunnable);
				adshandler.postDelayed(adsrunnable, 30000);
				imgSlider.setVisibility(GONE);
				adsView.setVisibility(GONE);
				tvPost.setVisibility(GONE);
				relMap.setVisibility(VISIBLE);
				btnGoOffline.setVisibility(VISIBLE);
				mapActivity.getSupportActionBar().show();
				return false;
			}
		});


		llAcceptReject = (LinearLayout) clientRequestView
				.findViewById(R.id.llAcceptReject);
		llUserDetailView = (View) clientRequestView
				.findViewById(R.id.clientDetailView);
		btnClientAccept = (MyFontButton) clientRequestView
				.findViewById(R.id.btnClientAccept);
		btnClientReject = (MyFontButton) clientRequestView
				.findViewById(R.id.btnClientReject);
		linearOffline = (LinearLayout) clientRequestView
				.findViewById(R.id.linearOffline);

		btnClientReqRemainTime = (MyFontButton) clientRequestView
				.findViewById(R.id.btnClientReqRemainTime);
		tvClientName = (MyFontTextView) clientRequestView
				.findViewById(R.id.tvClientName);


		tvClientRating = (RatingBar) clientRequestView
				.findViewById(R.id.tvClientRating);

		ivClientProfilePicture = (ImageView) clientRequestView
				.findViewById(R.id.ivClientImage);

		btnClientAccept.setOnClickListener(this);
		btnClientReject.setOnClickListener(this);
		btnMyLocation = (ImageButton) clientRequestView.findViewById(R.id.btnMyLocation);
		btnMyLocation.setOnClickListener(this);
		btnOverHere = (ImageButton) clientRequestView.findViewById(R.id.btnOverHere);
		btnOverHere.setOnClickListener(this);
		btnGoOffline = (Button) clientRequestView.findViewById(R.id.btnOffline);
		relMap = (RelativeLayout) clientRequestView.findViewById(R.id.relMap);
		tvPost = (TextView) clientRequestView.findViewById(R.id.tvPost);
		linearOffline.setVisibility(GONE);
		relMap.setVisibility(VISIBLE);
		btnGoOffline.setOnClickListener(this);
		btnGoOffline.setSelected(true);
		setComponentInvisible();
		return clientRequestView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		aQuery = new AQuery(mapActivity);
		mMapView = (MapView) clientRequestView.findViewById(R.id.clientReqMap);
		mMapView.onCreate(mBundle);

		if (ContextCompat.checkSelfPermission(mapActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(mapActivity, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_ACCESS_COARSE_LOCATION);
		}
		vLastTime = SystemClock.elapsedRealtime();
		setUpMap();

		locationHelper = new LocationHelper(getActivity());
		locationHelper.setLocationReceivedLister(this);
		locationHelper.onStart();
		checkState();
		//get post texts
		getPostText();
		if(preferenceHelper.getLong("lasttime-postupdate") == 0)
			preferenceHelper.putLong("lasttime-postupdate",(new Date()).getTime());
		origin = true;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBundle = savedInstanceState;
		requestQueue = Volley.newRequestQueue(mapActivity);
		IntentFilter filter = new IntentFilter(AndyConstants.NEW_REQUEST);
		requestReciever = new newRequestReciever();
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				requestReciever, filter);
		soundPool = new SoundPool(5, AudioManager.STREAM_ALARM, 100);
		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId,
									   int status) {
				loaded = true;
			}
		});
		soundid = soundPool.load(mapActivity, R.raw.beep, 1);
		adshandler.postDelayed(adsrunnable, 10000);
		//initializeMediaPlayer();
		adsRepeat = 0;
		if (ContextCompat.checkSelfPermission(mapActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(mapActivity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
		}
		setUserVisibleHint(false);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			Uri sound = null;
			sound = Uri.parse("android.resource://" + mapActivity.getPackageName() + "/" + R.raw.start_tracking);
			createNotificationChn("start_tracking", sound);
			sound = Uri.parse("android.resource://" + mapActivity.getPackageName() + "/" + R.raw.stop_tracking);
			createNotificationChn("stop_tracking", sound);
		}

	}

	private void addMarker() {
		if (mMap == null) {
			setUpMap();
			return;
		}

	}

	public void showLocationOffDialog() {

		AlertDialog.Builder gpsBuilder = new AlertDialog.Builder(mapActivity);
		gpsBuilder.setCancelable(false);
		gpsBuilder
				.setTitle(R.string.dialog_no_location_service_title)
				.setMessage(R.string.dialog_no_location_service)
				.setPositiveButton(
						(R.string.dialog_enable_location_service),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
												int which) {
								// continue with delete
								dialog.dismiss();
								Intent viewIntent = new Intent(
										Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivity(viewIntent);

							}
						})

				.setNegativeButton(R.string.dialog_exit,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
												int which) {
								// do nothing
								dialog.dismiss();
								mapActivity.finish();
							}
						});
		gpsBuilder.create();
		gpsBuilder.show();
	}

	private void setUpMap() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {

			((MapView) clientRequestView.findViewById(R.id.clientReqMap)).getMapAsync(new OnMapReadyCallback() {
				@Override
				public void onMapReady(GoogleMap map) {
					//map.setMyLocationEnabled(true);
					map.getUiSettings().setMyLocationButtonEnabled(false);
					map.getUiSettings().setZoomControlsEnabled(true);
					map.getUiSettings().setZoomGesturesEnabled(true);
					map.getUiSettings().setScrollGesturesEnabled(true);
					map.setPadding(0, 0, 0, 100);
					mMap = map;


					mMap.setInfoWindowAdapter(new InfoWindowAdapter() {
						@Override
						public View getInfoWindow(Marker marker) {
							View v = mapActivity.getLayoutInflater().inflate(
									R.layout.info_window_layout, null);

							((TextView) v).setText(marker.getTitle());
							return v;
						}

						@Override
						public View getInfoContents(Marker marker) {

							// Getting view from the layout file info_window_layout View

							// Getting reference to the TextView to set title TextView

							// Returning the view containing InfoWindow contents return
							return null;

						}

					});

					mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
						@Override
						public boolean onMarkerClick(Marker marker) {
							marker.showInfoWindow();
							return true;
						}
					});
					addMarker();
				}
			});
		}
	}

	@Override
	public void onClick(View v) {
		// Preventing multiple clicks, using threshold of 1 second
		if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
			return;
		}
		mLastClickTime = SystemClock.elapsedRealtime();
		/* if (v == buttonPlay) {
			startPlaying();
		} else if (v == buttonStopPlay) {
			stopPlaying();
		} */
		switch (v.getId()) {
			case R.id.btnClientAccept:
            case R.id.btnAcceptJob:
            	jobDialog.dismiss();
				mapActivity.clearAll();
				String mbody = "job accepted";
				notifications(mbody, R.raw.job_accepted);
				isAccepted = true;
				cancelSeekbarTimer();
				respondRequest(1);
				break;
			case R.id.btnClientReject:
            case R.id.btnCancelJob:
            	jobDialog.dismiss();
				mapActivity.clearAll();
				isAccepted = false;
				cancelSeekbarTimer();
				soundPool.stop(soundid);
				mbody = "job rejected";
				notifications(mbody, R.raw.job_rejected);
				respondRequest(0);
				break;

			case R.id.btnMyLocation:
				if (myLocation != null) {
					LatLng latLng = new LatLng(myLocation.getLatitude(),
							myLocation.getLongitude());
					CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(
							latLng);
					if (mMap != null) mMap.animateCamera(cameraUpdate);
				}
				if (cameraFollow) {
					cameraFollow = false;
					AndyUtils.showToast("Stop tracking on map", mapActivity);
					mbody = "Stop tracking on map";
					notifications(mbody, R.raw.stop_tracking);
				} else {
					cameraFollow = true;
					AndyUtils.showToast("Start tracking on map", mapActivity);
					mbody = "Start tracking on map";
					notifications(mbody, R.raw.start_tracking);
				}

				break;
			case R.id.btnOverHere:
				showOverHereDialog();
				break;
			case R.id.btnOffline:
				mapActivity.clearAll();
				changeState();
				break;
			case R.id.imgClearPickup:
				pickupAddress.setText("");
				break;
			case R.id.imgClearDropoff:
				dropoffAddress.setText("");
				break;
			case R.id.btnCancelRequest:
				dialog.dismiss();
				break;
			case R.id.btnSendRequest:
				dialog.dismiss();
				sendRequest();
				break;
			case R.id.btnTimePicker:
				showTimePicker();
				break;
			default:
				break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();
		if (btnGoOffline.isSelected()) {
			if (preferenceHelper.getRequestId() == AndyConstants.NO_REQUEST) {
				startCheckingUpcomingRequests();
			}
		}
		mapActivity.setActionBarTitle(mapActivity.getString(R.string.app_name));

	}

	@Override
	public void onPause() {
		super.onPause();
		if (preferenceHelper.getRequestId() == AndyConstants.NO_REQUEST) {
			stopCheckingUpcomingRequests();
		}
		mMapView.onPause();
	}

	@Override
	public void onDestroy() {
		//mMapView.onDestroy();
		super.onDestroy();
		stopCheckingUpcomingRequests();
		cancelSeekbarTimer();
		AndyUtils.removeCustomProgressDialog();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
				requestReciever);
		//clearMemory();
	}


	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		String[] part = response.split("\\{",2);
		response = "{" + part[1];

		switch (serviceCode) {
			case AndyConstants.ServiceCode.GET_ALL_REQUEST:
				AndyUtils.removeCustomProgressDialog();
				AppLog.Log(TAG, "getAllRequests Response :" + response);
				if (!parseContent.parseIsApproved(response)) {
					if (isApprovedCheck) {
						mapActivity.openApprovedDialog();
						isApprovedCheck = false;
						return;
					}
				} else if (mDialog != null && mDialog.isShowing()) {
					mDialog.dismiss();
					isApprovedCheck = true;
				}
				if (!parseContent.isSuccess(response)) {
					if (parseContent.getErrorCode(response) == AndyConstants.REQUEST_ID_NOT_FOUND) {
						AndyUtils.removeCustomProgressDialog();
						CharSequence mbody = "No Request found";
						//notifications(mbody,R.raw.welcome_to_overthere);
						preferenceHelper.clearRequestData();

					} else if (parseContent.getErrorCode(response) == AndyConstants.TOKEN_EXPIRED) {
                        AndyUtils.showToast("Token expired. Please re-login.", mapActivity);
                    }
					return;
				}
				if (!parseContent.parseAvaibilty(response)) {
					btnGoOffline.setText(mapActivity.getString(R.string.text_go_online));
					linearOffline.setVisibility(VISIBLE);
					relMap.setVisibility(GONE);
				}
				if (parseContent.parseNewBooking(response)) {
					String mbody = "New booking is available";
					//notifications(mbody, R.raw.new_booking);
				}
				requestDetail = parseContent.parseAllRequests(response);
				if (requestDetail != null && mMap != null) {
					try {
						stopCheckingUpcomingRequests();
						// startTimerForRespondRequest(requestDetail.getTimeLeft());
						setComponentVisible();
						//imgIndex = 0;
						adshandler.removeCallbacks(adsrunnable);
						imgSlider.setVisibility(GONE);
						tvPost.setVisibility(GONE);
						//mVideoView.setVisibility(GONE);
						//mVideoView.stopPlayback();
						mapActivity.getSupportActionBar().show();
						relMap.setVisibility(VISIBLE);
						btnGoOffline.setVisibility(GONE);
						// pbTimeLeft.setMax(requestDetail.getTimeLeft());
						btnClientReqRemainTime.setText("" + requestDetail.getTimeLeft());
                        //btnClientReqRemainTime.setText("20");
						// pbTimeLeft.setProgress(requestDetail.getTimeLeft());
						tvClientName.setText(requestDetail.getClientName());
						// tvClientPhoneNumber.setText(requestDetail
						// .getClientPhoneNumber());
						if (requestDetail.getClientRating() != 0) {
							tvClientRating.setRating(requestDetail
									.getClientRating());
							Log.i("Rating", "" + requestDetail.getClientRating());
						}
						if (TextUtils.isEmpty(requestDetail.getClientProfile())) {
							aQuery.id(ivClientProfilePicture).progress(R.id.pBar)
									.image(R.drawable.user);
						} else {
							aQuery.id(ivClientProfilePicture).progress(R.id.pBar)
									.image(requestDetail.getClientProfile());
						}
						//AndyUtils.showToast(requestDetail.getPickupType()+":Near by "
						//		+preferenceHelper.getPickupAddress(), mapActivity);
						showJobDetails(requestDetail);

						markerClientLocation = null;
						markerClientLocation = mMap.addMarker(new MarkerOptions()
								.position(
										new LatLng(Double.parseDouble(requestDetail
												.getClientLatitude()), Double
												.parseDouble(requestDetail
														.getClientLongitude())))
								.icon(BitmapDescriptorFactory
										.fromResource(R.drawable.pin_client))
								.title(mapActivity.getResources().getString(
										R.string.client_location)));
						if (seekbarTimer == null) {
							//seekbarTimer = new SeekbarTimer(
							//	requestDetail.getTimeLeft() * 1000, 1000);
							seekbarTimer = new SeekbarTimer(
							requestDetail.getTimeLeft() * 1000, 1000);
                            //seekbarTimer = new SeekbarTimer(
                              //      20 * 1000, 1000);
							//15 seconds countdown
							seekbarTimer.start();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			case AndyConstants.ServiceCode.CHECK_STATE:
			case AndyConstants.ServiceCode.TOGGLE_STATE:
				AndyUtils.removeCustomProgressDialog();
				//preferenceHelper.putIsActive(false);
				//preferenceHelper.putDriverOffline(false);
				if (!parseContent.isSuccess(response)) {
					return;
				}
				AppLog.Log("TAG", "toggle state:" + response);
				if (parseContent.parseAvaibilty(response)) {
					preferenceHelper.putIsActive(true);
					preferenceHelper.putDriverOffline(true);
					updateButtonUi(true);
					if (preferenceHelper.getRequestId() == AndyConstants.NO_REQUEST) {
						startCheckingUpcomingRequests();
						adshandler.removeCallbacks(adsrunnable);
						adshandler.postDelayed(adsrunnable, 30000);
					}

				} else {
					preferenceHelper.putIsActive(false);
					preferenceHelper.putDriverOffline(false);
					stopCheckingUpcomingRequests();
					adshandler.removeCallbacks(adsrunnable);
					updateButtonUi(false);
				}

				break;
			case AndyConstants.ServiceCode.RESPOND_REQUEST:
				AppLog.Log(TAG, "respond Request Response :" + response);
				removeNotification();
				AndyUtils.removeCustomProgressDialog();
				if (parseContent.isSuccess(response)) {
					if (isAccepted) {

						preferenceHelper.putRequestId(requestDetail.getRequestId());
						preferenceHelper.putStartTime(System.currentTimeMillis());
						float dist3 = 0;
						preferenceHelper.putDistance(dist3);
						JobFragment jobFragment = new JobFragment();
						Bundle bundle = new Bundle();
						bundle.putInt(AndyConstants.JOB_STATUS,
								AndyConstants.IS_WALKER_STARTED);
						bundle.putSerializable(AndyConstants.REQUEST_DETAIL,
								requestDetail);

						SupportMapFragment f = (SupportMapFragment) getFragmentManager()
								.findFragmentById(R.id.clientReqMap);
						if (f != null) {
							try {
								getFragmentManager().beginTransaction().remove(f).commit();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						jobFragment.setArguments(bundle);
						if (this.isVisible())
							mapActivity.addFragment(jobFragment, false,
									AndyConstants.JOB_FRGAMENT_TAG, true);
					} else {
						cancelSeekbarTimer();
						setComponentInvisible();
						if (markerClientLocation != null
								&& markerClientLocation.isVisible()) {
							markerClientLocation.remove();
						}
						preferenceHelper.putRequestId(AndyConstants.NO_REQUEST);
						startCheckingUpcomingRequests();
					}
				} else {
					if (!isAccepted) {
						cancelSeekbarTimer();
						setComponentInvisible();
						adshandler.postDelayed(adsrunnable, 30000);
						if (markerClientLocation != null
								&& markerClientLocation.isVisible()) {
							markerClientLocation.remove();
						}
						preferenceHelper.putRequestId(AndyConstants.NO_REQUEST);
						startCheckingUpcomingRequests();
					}
				}


				break;

			case AndyConstants.ServiceCode.CREATE_REQUEST:
				if (parseContent.isSuccess(response)) {
					AndyUtils.showToast("Thanks. Your Overhere request has been sent.", mapActivity);
					overhereDetail = parseContent.parseCreateRequests(response);
					String mbody = "OverHere request sent";
					notifications(mbody, R.raw.overhere_sent);
					overHereRequestID = overhereDetail.getOverhereRequestId();
					currentWalker = overhereDetail.getCurrentWalker();
					pickupType = overhereDetail.getPickupType();
					T_NotAccept = 0;
					if(pickupType.equals("overhere")) updateOverhereRequests();
				} else {
					AndyUtils.showToast("Sorry. Your Overhere request is failed.", mapActivity);
					String mbody = "OverHere request failed";
					notifications(mbody, R.raw.overhere_failed);
				}
				break;
			case AndyConstants.ServiceCode.CANCEL_WALKER:
				overhereDetail = parseContent.parseOverhereRequests(response);
				overHereRequestID = overhereDetail.getOverhereRequestId();
				pickupType = overhereDetail.getPickupType();
				currentWalker = overhereDetail.getCurrentWalker();
				confirmedWalker = overhereDetail.getConfirmedWalker();
				T_NotAccept = 0;
				if (currentWalker == 0) {
					isOverhereRequest = false;
					stopOverhereRequests();
						AndyUtils.showToast("Sorry. no driver accepts the request.", mapActivity);
						String mbody = "No driver accept the request";
						notifications(mbody, R.raw.no_driver);

				}
				break;
			case AndyConstants.ServiceCode.OVERHERE_REQUEST:
				overhereDetail = parseContent.parseOverhereRequests(response);
				overHereRequestID = overhereDetail.getOverhereRequestId();
				pickupType = overhereDetail.getPickupType();

				if (currentWalker != overhereDetail.getCurrentWalker()) {
					T_NotAccept = 0;
					currentWalker = overhereDetail.getCurrentWalker();
				}
				confirmedWalker = overhereDetail.getConfirmedWalker();
				if (currentWalker == 0) {
					isOverhereRequest = false;
					stopOverhereRequests();
						AndyUtils.showToast("Sorry. no driver accepts the request.", mapActivity);
						String mbody = "No driver accept the request";
						notifications(mbody, R.raw.no_driver);

				}
				if (confirmedWalker == 0) {
					T_NotAccept = T_NotAccept + 1;
				} else {
					isOverhereRequest = false;
					AndyUtils.showToast(" Your OverHere request is successed.", mapActivity);
					String mbody = "OverHere request is successed";
					notifications(mbody, R.raw.overhere_successed);
					stopOverhereRequests();
				}
				if (T_NotAccept > 5) {
					cancelwalker(currentWalker);
				}
				break;
			case  AndyConstants.ServiceCode.UPDATE_PROVIDER_LOCATION:
			    try {
					JSONObject jsonObject = new JSONObject(response);
					if (jsonObject.getBoolean("success")) {
						if (jsonObject.getString("is_active").equals("1"))
							preferenceHelper.putIsActive(true);
						else
							preferenceHelper.putIsActive(false);
						Log.v("upload","update provider location");
					}
		        	} catch (Exception exception) {
			          exception.printStackTrace();
			    	}
				break;
			case  AndyConstants.ServiceCode.GET_POST_TEXT:
				try {
					JSONObject jsonObject = new JSONObject(response);
					if (jsonObject.getBoolean("success")) {
						postDuration = jsonObject.getInt("post_update_duration");
						JSONArray jsonArray = jsonObject.getJSONArray("posts");
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject postJson = jsonArray.getJSONObject(i);
							String postText = postJson.getString("ads_text");
							postTexts.add(i,postText);
						}
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				break;

			default:
				break;

		}
	}

	private class SeekbarTimer extends CountDownTimer {

		public SeekbarTimer(long startTime, long interval) {
			super(startTime, interval);
			// pbTimeLeft.setProgressDrawable(getResources().getDrawable(
			// R.drawable.customprogress));
		}

		@Override
		public void onFinish() {
			if (seekbarTimer == null) {
				return;
			}
			dialog.dismiss();
			//cancel request
			respondRequest(0);
			setComponentInvisible();
			preferenceHelper.clearRequestData();
			mapActivity.clearAll();
			if (markerClientLocation != null
					&& markerClientLocation.isVisible()) {
				markerClientLocation.remove();
			}
			removeNotification();
			startCheckingUpcomingRequests();
			this.cancel();
			seekbarTimer = null;

		}

		@Override
		public void onTick(long millisUntilFinished) {
			int time = (int) (millisUntilFinished / 1000);

			if (!isVisible()) {
				return;
			}
			//if (preferenceHelper.getSoundAvailability())
			{
				if (time <= 15) {
					AppLog.Log("ClientRequest Timer Beep", "Beep started");
					if (loaded) {
						soundPool.play(soundid, 1, 1, 0, 0, 1);
					}

				}
			}

			btnClientReqRemainTime.setText("" + time);


		}
	}

	// if status = 1 then accept if 0 then reject
	private void respondRequest(int status) {
		if (!AndyUtils.isNetworkAvailable(mapActivity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet),
					mapActivity);
			return;
		}

		AndyUtils.showCustomProgressDialog(mapActivity, "", getResources()
				.getString(R.string.progress_respond_request), false);
		String Lserver = new PreferenceHelper(getActivity()).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(AndyConstants.URL, Lserver + AndyConstants.ServiceType.RESPOND_REQUESTS);
		map.put(AndyConstants.Params.ID, preferenceHelper.getUserId());
		map.put(AndyConstants.Params.REQUEST_ID,
				String.valueOf(requestDetail.getRequestId()));
		map.put(AndyConstants.Params.TOKEN, preferenceHelper.getSessionToken());
		map.put(AndyConstants.Params.ACCEPTED, String.valueOf(status));

		requestQueue.add(new VolleyHttpRequest(Method.POST, map,
				AndyConstants.ServiceCode.RESPOND_REQUEST, this, this));
	}

	public void checkRequestStatus() {
		if (!AndyUtils.isNetworkAvailable(mapActivity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet),
					mapActivity);
			return;
		}
		AndyUtils.showCustomProgressDialog(mapActivity, "", getResources()
				.getString(R.string.progress_dialog_request), false);
		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(getActivity()).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL,
				Lserver + AndyConstants.ServiceType.CHECK_REQUEST_STATUS
						+ AndyConstants.Params.ID + "="
						+ preferenceHelper.getUserId() + "&"
						+ AndyConstants.Params.TOKEN + "="
						+ preferenceHelper.getSessionToken() + "&"
						+ AndyConstants.Params.REQUEST_ID + "="
						+ preferenceHelper.getRequestId());
		// new HttpRequester(mapActivity, map,
		// AndyConstants.ServiceCode.CHECK_REQUEST_STATUS, true, this);

		requestQueue.add(new VolleyHttpRequest(Method.GET, map,
				AndyConstants.ServiceCode.CHECK_REQUEST_STATUS, this, this));
	}

	public void getAllRequests() {
		if (!AndyUtils.isNetworkAvailable(mapActivity)) {
			return;
		}

		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = preferenceHelper.getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL,
				Lserver + AndyConstants.ServiceType.GET_ALL_REQUESTS
						+ AndyConstants.Params.ID + "="
						+ preferenceHelper.getUserId() + "&"
						+ AndyConstants.Params.TOKEN + "="
						+ preferenceHelper.getSessionToken());

		requestQueue.add(new VolleyHttpRequest(Method.GET, map,
				AndyConstants.ServiceCode.GET_ALL_REQUEST, this, this));
	}

	private class TimerRequestStatus extends TimerTask {
		@Override
		public void run() {
			if (isContinueRequest) {
				getAllRequests();
				UploadDataToServer();
				CheckMoving();
			}
		}
	}

	private void startCheckingUpcomingRequests() {
		AppLog.Log(TAG, "start checking upcomingRequests");
		isContinueRequest = true;
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (timer == null) {
			timer = new Timer();
			timer.scheduleAtFixedRate(new TimerRequestStatus(),
					AndyConstants.DELAY, AndyConstants.TIME_SCHEDULE);
		}
	}

	private void stopCheckingUpcomingRequests() {
		AppLog.Log(TAG, "stop checking upcomingRequests");
		isContinueRequest = false;
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	private void removeNotification() {
		try {
			NotificationManager manager = (NotificationManager) mapActivity
					.getSystemService(NOTIFICATION_SERVICE);
			manager.cancel(AndyConstants.NOTIFICATION_ID);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onLocationReceived(LatLng latlong) {
		if (latlong != null) {

				preferenceHelper
						.putWalkerLatitude(String.valueOf(latlong.latitude));
				preferenceHelper.putWalkerLongitude(String
						.valueOf(latlong.longitude));
			latitude = String.valueOf(latlong.latitude);
			longitude = String.valueOf(latlong.longitude);
			bearing = myLocation.getBearing();
			preferenceHelper.putLatitude(Double.parseDouble(latitude));
			preferenceHelper.putLongitude(Double.parseDouble(longitude));

			if (mMap != null) {
				if (markerDriverLocation == null) {
					markerDriverLocation = mMap.addMarker(new MarkerOptions()
							.position(
									new LatLng(latlong.latitude,
											latlong.longitude))
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.pin_driver))
							.title(mapActivity.getResources().getString(
									R.string.my_location)));
					//mMap.animateCamera(CameraUpdateFactory
					//		.newLatLngZoom(new LatLng(latlong.latitude,
					//				latlong.longitude), 15));
					mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 15));

				} else {
					markerDriverLocation.setPosition(new LatLng(
							latlong.latitude, latlong.longitude));
					markerDriverLocation.setRotation(bearing);
				}
				if (cameraFollow) {
					CameraUpdate center = CameraUpdateFactory.newLatLng(latlong);
					mMap.animateCamera(center);
				}
			}
		}
	}

	public void setComponentVisible() {
		btnGoOffline.setVisibility(GONE);
		llAcceptReject.setVisibility(VISIBLE);
		btnClientReqRemainTime.setVisibility(VISIBLE);
		// rlTimeLeft.setVisibility(View.VISIBLE);
		llUserDetailView.setVisibility(VISIBLE);
		imgSlider.setVisibility(VISIBLE);
		tvPost.setVisibility(VISIBLE);
	}

	public void setComponentInvisible() {
		btnGoOffline.setVisibility(VISIBLE);
		llAcceptReject.setVisibility(GONE);
		btnClientReqRemainTime.setVisibility(GONE);
		// rlTimeLeft.setVisibility(View.GONE);
		llUserDetailView.setVisibility(GONE);
		imgSlider.setVisibility(GONE);
		tvPost.setVisibility(GONE);
	}

	public void cancelSeekbarTimer() {
		if (seekbarTimer != null) {
			seekbarTimer.cancel();
			seekbarTimer = null;
			soundPool.stop(soundid);
		}
	}

	public void onDestroyView() {
		SupportMapFragment f = (SupportMapFragment) getFragmentManager()
				.findFragmentById(R.id.clientReqMap);
		if (f != null) {
			try {
				getFragmentManager().beginTransaction().remove(f).commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		//mMap = null;
		super.onDestroyView();
	}

	private class newRequestReciever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String response = intent.getStringExtra(AndyConstants.NEW_REQUEST);
			AppLog.Log(TAG, "FROM BROAD CAST-->" + response);
			try {
				JSONObject jsonObject = new JSONObject(response);
				if (jsonObject.getInt(AndyConstants.Params.UNIQUE_ID) == 1) {
					requestDetail = parseContent.parseNotification(response);
					if (requestDetail != null) {
						stopCheckingUpcomingRequests();
						// startTimerForRespondRequest(requestDetail.getTimeLeft());

						setComponentVisible();
						// pbTimeLeft.setMax(requestDetail.getTimeLeft());
						btnClientReqRemainTime.setText(""
								+ requestDetail.getTimeLeft());
						// pbTimeLeft.setProgress(requestDetail.getTimeLeft());
						tvClientName.setText(requestDetail.getClientName());
						// tvClientPhoneNumber.setText(requestDetail
						// .getClientPhoneNumber());
						if (requestDetail.getClientRating() != 0) {
							tvClientRating.setRating(requestDetail
									.getClientRating());
							Log.i("Rating",
									"" + requestDetail.getClientRating());
						}
						if (TextUtils.isEmpty(requestDetail.getClientProfile())) {
							aQuery.id(ivClientProfilePicture)
									.progress(R.id.pBar).image(R.drawable.user);
						} else {
							aQuery.id(ivClientProfilePicture)
									.progress(R.id.pBar)
									.image(requestDetail.getClientProfile());
						}

						markerClientLocation = null;
						markerClientLocation = mMap
								.addMarker(new MarkerOptions()
										.position(
												new LatLng(
														Double.parseDouble(requestDetail
																.getClientLatitude()),
														Double.parseDouble(requestDetail
																.getClientLongitude())))
										.icon(BitmapDescriptorFactory
												.fromResource(R.drawable.pin_client))
										.title(mapActivity
												.getResources()
												.getString(
														R.string.client_location)));
						if (seekbarTimer == null) {
							seekbarTimer = new SeekbarTimer(
									requestDetail.getTimeLeft() * 1000, 1000);
                            //seekbarTimer = new SeekbarTimer(
                             //       20* 1000, 1000);
							seekbarTimer.start();
						}
						AppLog.Log(TAG, "From broad cast recieved request");
					}
				} else {
					setComponentInvisible();
					preferenceHelper.clearRequestData();
					if (markerClientLocation != null
							&& markerClientLocation.isVisible()) {
						markerClientLocation.remove();
					}
					cancelSeekbarTimer();
					removeNotification();
					startCheckingUpcomingRequests();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		// TODO Auto-generated method stub
		AppLog.Log("TAG", error.getMessage());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.overthere.express.locationupdate.LocationHelper.OnLocationReceived
	 * #onLocationReceived(android.location.Location)
	 */
	@Override
	public void onLocationReceived(Location location) {
		// TODO Auto-generated method stub
		if (location != null) {
			// drawTrip(latlong);
			myLocation = location;
			if(origin) {
				myOldLocation = myLocation;
				origin = false;
			}
		}

	}


	@Override
	public void onConntected(Bundle bundle) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onConntected(Location location) {
		// TODO Auto-generated method stub
		this.location = location;
		if (location != null) {
			if (mMap != null) {
				if (markerDriverLocation == null) {
					markerDriverLocation = mMap.addMarker(new MarkerOptions()
							.position(
									new LatLng(location.getLatitude(), location
											.getLongitude()))
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.pin_driver))
							.title(getResources().getString(
									R.string.my_location)));
					//mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
					//		new LatLng(location.getLatitude(), location
					//				.getLongitude()), 15));
					mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location
							.getLongitude()), 15));

				} else {
					markerDriverLocation.setPosition(new LatLng(location
							.getLatitude(), location.getLongitude()));
				}
			}
		} else {
			showLocationOffDialog();
		}
	}

	private void updateButtonUi(boolean state) {
		btnGoOffline.setVisibility(VISIBLE);
		btnGoOffline.setSelected(state);
		if (btnGoOffline.isSelected()) {
			btnGoOffline.setText(mapActivity.getString(R.string.text_go_offline));
			linearOffline.setVisibility(GONE);
			relMap.setVisibility(VISIBLE);
		} else {
			btnGoOffline.setText(mapActivity.getString(R.string.text_go_online));
			linearOffline.setVisibility(VISIBLE);
			relMap.setVisibility(GONE);
		}
	}

	private void checkState() {
		if (!AndyUtils.isNetworkAvailable(mapActivity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet),
					mapActivity);
			return;
		}
//		AndyUtils.showCustomProgressDialog(mapActivity, "", getResources()
//				.getString(R.string.progress_getting_avaibility), false);
		String Lserver = new PreferenceHelper(getActivity()).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(AndyConstants.URL,
				Lserver + AndyConstants.ServiceType.CHECK_STATE + AndyConstants.Params.ID
						+ "=" + preferenceHelper.getUserId() + "&"
						+ AndyConstants.Params.TOKEN + "="
						+ preferenceHelper.getSessionToken());
		// new HttpRequester(this, map, AndyConstants.ServiceCode.CHECK_STATE,
		// true, this);

		requestQueue.add(new VolleyHttpRequest(Method.GET, map,
				AndyConstants.ServiceCode.CHECK_STATE, this, this));
	}

	private void changeState() {
		if (!AndyUtils.isNetworkAvailable(mapActivity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet),
					mapActivity);
			return;
		}

		AndyUtils.showCustomProgressDialog(mapActivity, "", getResources()
				.getString(R.string.progress_changing_avaibilty), false);
		String Lserver = new PreferenceHelper(getActivity()).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(AndyConstants.URL, Lserver + AndyConstants.ServiceType.TOGGLE_STATE);
		map.put(AndyConstants.Params.ID, preferenceHelper.getUserId());
		map.put(AndyConstants.Params.TOKEN, preferenceHelper.getSessionToken());

		// new HttpRequester(this, map, AndyConstants.ServiceCode.TOGGLE_STATE,
		// this);

		requestQueue.add(new VolleyHttpRequest(Method.POST, map,
				AndyConstants.ServiceCode.TOGGLE_STATE, this, this));

	}

	private void showOverHereDialog() {
		dialog = new Dialog(getActivity());
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.u_overhere_timepicker);
		dialog.getWindow().getDecorView().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		SimpleDateFormat sdf1 = new SimpleDateFormat("HH");
		SimpleDateFormat sdf2 = new SimpleDateFormat("mm");
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String currentdHour = sdf1.format(date);
		String currentdMinute = sdf2.format(date);
		String currentdDate = sdf3.format(date);

		final ArrayAdapter<String> adapterDatesArray = new ArrayAdapter<String>(mapActivity, R.layout.spinner_overhere, Dates);
		final ArrayAdapter<String> adapterServiceTypesArray = new ArrayAdapter<String>(mapActivity, R.layout.spinner_overhere, DriverServiceTypes) {
			@Override
			public boolean isEnabled(int position) {
				//travel,express,help and ask direction is not clickable
				if (position ==0||position==2||position ==8) {
					return false;
				}
				return true;
			}
		};


		pickupdate = (Spinner) dialog.findViewById(R.id.pickupdate);
		pickupserviceTypes = (Spinner) dialog.findViewById(R.id.servicetype);
		btnTimePicker = (Button)dialog.findViewById(R.id.btnTimePicker);
		pickupdate.setAdapter(adapterDatesArray);
		pickupserviceTypes.setAdapter(adapterServiceTypesArray);

		pickupdate.setSelection(0);
		btnTimePicker.setVisibility(INVISIBLE);
		pickupserviceTypes.setSelection(1);
		serviceReward = (AutoCompleteTextView) dialog.findViewById(R.id.servicereward);
		pickupserviceTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
									   int position, long id) {

				if(position>1&&position<12)
				{
					serviceReward.setVisibility(VISIBLE);
				} else {
					serviceReward.setVisibility(INVISIBLE);
				}

			}
			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
				// TODO Auto-generated method stub
			}
		});
		linearPhone = (LinearLayout) dialog.findViewById(R.id.linearphone);
		cPhone = (AutoCompleteTextView) dialog.findViewById(R.id.cphone);
		pickupdate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
									   int position, long id) {
				if(position==0)
				{// if slect now, do not show timepicker
					btnTimePicker.setVisibility(INVISIBLE);
				} else {
					btnTimePicker.setVisibility(VISIBLE);
					linearPhone.setVisibility(VISIBLE);
				}

			}
			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
				// TODO Auto-generated method stub
			}
		});
		pickupAddress = (AutoCompleteTextView) dialog.findViewById(R.id.etPickupAddress);
		if (myLocation != null) {
			currentLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
			getAddressFromLocation(currentLatLng, pickupAddress);
		}
		dropoffAddress = (AutoCompleteTextView) dialog.findViewById(R.id.etDropOffAddress);
		final PlacesAutoCompleteAdapter adapterPickupAddress = new PlacesAutoCompleteAdapter(mapActivity,
				R.layout.autocomplete_list_text);
		final PlacesAutoCompleteAdapter adapterDropoffAddress = new PlacesAutoCompleteAdapter(mapActivity,
				R.layout.autocomplete_list_text);
		pickupAddress.setAdapter(adapterPickupAddress);
		dropoffAddress.setAdapter(adapterDropoffAddress);
		pickupAddress.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				final String selectedpickupPlace = adapterPickupAddress.getItem(arg2);
				pickupAddress.setText(selectedpickupPlace);
			}
		});
		dropoffAddress.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				final String selecteddropoffPlace = adapterDropoffAddress.getItem(arg2);
				LatLng destatlng = getLocationFromAddress(selecteddropoffPlace);
				preferenceHelper.putClientDestination(destatlng);
				preferenceHelper.putDropoffAddress(selecteddropoffPlace);
				dropoffAddress.setText(selecteddropoffPlace);
			}
		});


		dialog.findViewById(R.id.imgClearDropoff).setOnClickListener(this);
		dialog.findViewById(R.id.btnCancelRequest).setOnClickListener(this);
		dialog.findViewById(R.id.btnSendRequest).setOnClickListener(this);
		dialog.findViewById(R.id.btnTimePicker).setOnClickListener(this);
		paymentMode = AndyConstants.CASH;
		new PreferenceHelper(getActivity()).putPaymentType(paymentMode);
		dialog.show();
	}

	private void getAddressFromLocation(final LatLng latlng, final EditText et) {
		et.setText("Waiting for Address");
		//et.setTextColor(Color.GRAY);
		new Thread(new Runnable() {
			@Override
			public void run() {
				Geocoder gCoder = new Geocoder(getActivity());
				try {
					final List<Address> list = gCoder.getFromLocation(
							latlng.latitude, latlng.longitude, 1);
					if (list != null && list.size() > 0) {
						address = list.get(0);
						StringBuilder sb = new StringBuilder();
						if (address.getAddressLine(0) != null) {
							if (address.getMaxAddressLineIndex() > 0) {
								for (int i = 0; i < address
										.getMaxAddressLineIndex(); i++) {
									sb.append(address.getAddressLine(i))
											.append("\n");
								}
								sb.append(",");
								sb.append(address.getCountryName());
							} else {
								sb.append(address.getAddressLine(0));
							}
						}

						strAddress = sb.toString();
						strAddress = strAddress.replace(",null", "");
						strAddress = strAddress.replace("null", "");
						strAddress = strAddress.replace("Unnamed", "");
					}
					if (getActivity() == null)
						return;

					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (!TextUtils.isEmpty(strAddress)) {
								et.setFocusable(false);
								et.setFocusableInTouchMode(false);
								et.setText(strAddress);
								et.setTextColor(getResources().getColor(
										android.R.color.black));
								et.setFocusable(true);
								et.setFocusableInTouchMode(true);
							} else {
								et.setText("");
								et.setTextColor(getResources().getColor(
										android.R.color.black));
							}
							pickupAddress.setEnabled(true);
						}
					});
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		}).start();
	}

	private void sendRequest() {
		if (!AndyUtils.isNetworkAvailable(mapActivity)) {
			AndyUtils.showToast(getResources().getString(R.string.no_internet),
					mapActivity);
			return;
		}
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String currentDate = sdf3.format(date);
		//String selectedDate = pickupdate.getSelectedItem().toString();

		String pickupDateTime = currentDate + " " + pickuphour + ":" + pickupminute;
		Date d2 = null;
		Date d1 = new Date();
		long reqtime = 0;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			d2 = dateFormat.parse(pickupDateTime);
		} catch (Exception e) {
			d2 = d1;
		}
		String selectedDate = pickupdate.getSelectedItem().toString();
		if (selectedDate == "Tomorrow") {
			reqtime = d2.getTime() + 24 * 3600 * 1000;
		}
		if (selectedDate == "Today") {
			reqtime = d2.getTime();
		}
		if (selectedDate == "Now") {
			reqtime = d1.getTime();
		} else if(TextUtils.isEmpty(cPhone.getText())){
            AndyUtils.showToast("Please input valid customer phone number.",
                    mapActivity);
            return;
        }
		Date d3 = new Date(reqtime);
		pickupDateTime = dateFormat.format(d3);
		long diff = reqtime - d1.getTime();
		long diffMinutes = diff / (60 * 1000);
		if (diffMinutes < 0) {
			AndyUtils.showToast("Pickup time invalid", mapActivity);
			return;
		}

		int postype = pickupserviceTypes.getSelectedItemPosition();
		//deleted 4 services types
		if(postype > 1) postype = postype + 4;
		if(postype>12&&!selectedDate.contains("Now")){
			//help must be at now,i.e. overhere, not booking
			AndyUtils.showToast("Sorry, you can not book for HELP.",
					mapActivity);
			return;
		}
		//express need dropoff address and reward not zero
		if(postype>6&&postype<12){
			if(TextUtils.isEmpty(dropoffAddress.getText())){
				AndyUtils.showToast("Please give dropoff address.",
						mapActivity);
				return;
			}
		}
		if(postype>=6&&postype<12) {

			if(TextUtils.isEmpty(serviceReward.getText())){
				AndyUtils.showToast("Please give some reward.",
						mapActivity);
				return;
			}
		}

		int serviceTypePos = postype;
		//express and help jobs are for any taxi
		if(postype>5) serviceTypePos = 1;
		dialog.dismiss();
		AndyUtils.showToast("Creating Request", mapActivity);

		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(getActivity()).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL, Lserver + AndyConstants.ServiceType.CREATE_REQUEST);
		map.put(AndyConstants.Params.TOKEN,
				new PreferenceHelper(mapActivity).getSessionToken());
		map.put(AndyConstants.Params.ID, new PreferenceHelper(mapActivity).getUserId());
		map.put(AndyConstants.Params.LATITUDE, String.valueOf(currentLatLng.latitude));
		map.put(AndyConstants.Params.LONGITUDE, String.valueOf(currentLatLng.longitude));
		map.put(AndyConstants.Params.TYPE, String.valueOf(serviceTypePos));
		map.put(AndyConstants.Params.TYPE_POS, String.valueOf(postype));
		map.put(AndyConstants.Params.REWARD, String.valueOf(serviceReward.getText()));
        map.put(AndyConstants.Params.CPHONE, String.valueOf(cPhone.getText()));
		if (selectedDate != "Now") {
			//the request is booking, ie, pickup_type is booking.
			if (dropoffAddress.getText().length() == 0) {
				AndyUtils.showToast("Please provide dropoff address", mapActivity);
				return;
			}
			new PreferenceHelper(getActivity()).putDropoffAddress(dropoffAddress.getText().toString());
			new PreferenceHelper(getActivity()).putRequestSource("DB");
			new PreferenceHelper(getActivity()).putPickupType("booking");
		} else {
			if (dropoffAddress.getText().length() == 0) {
				dropoffAddress.setText("unknown street, unknown area, NSW, Australia");
				new PreferenceHelper(getActivity()).putDropoffAddress("");
			} else {
				new PreferenceHelper(getActivity()).putDropoffAddress(dropoffAddress.getText().toString());
			}
			if(postype>=16) {
				new PreferenceHelper(getActivity()).putRequestSource("DH");
				new PreferenceHelper(getActivity()).putPickupType("helping");
			} else {
				if (postype >= 12) {
					new PreferenceHelper(getActivity()).putRequestSource("DH");
					new PreferenceHelper(getActivity()).putPickupType("overhere");
				} else {
					new PreferenceHelper(getActivity()).putRequestSource("DO");
					new PreferenceHelper(getActivity()).putPickupType("overhere");
				}
			}
		}
		String sections = pickupAddress.getText().toString();
		String pickupArea = "";
		try {
			String[] str = sections.split(",");
			pickupArea = str[1];
		} catch (Exception e) {
		}

		new PreferenceHelper(getActivity()).putPickupArea(pickupArea);
		sections = dropoffAddress.getText().toString();
		String dropoffArea = "";
		try {
			String[] str1 = sections.split(",");
			dropoffArea = str1[1];
		} catch (Exception e) {
		}

		new PreferenceHelper(getActivity()).putDropoffArea(dropoffArea);

		String requestSource = new PreferenceHelper(getActivity()).getRequestSource();
		String pickupType = new PreferenceHelper(getActivity()).getPickupType();
		map.put(AndyConstants.Params.REQUEST_TIME, String.valueOf(pickupDateTime));
		map.put(AndyConstants.Params.PICKUP_ADDRESS, String.valueOf(pickupAddress.getText()));
		map.put(AndyConstants.Params.DROPOFF_ADDRESS, String.valueOf(dropoffAddress.getText()));
		map.put(AndyConstants.Params.PICKUP_AREA, String.valueOf(pickupArea));
		map.put(AndyConstants.Params.DROPOFF_AREA, String.valueOf(dropoffArea));
		map.put(AndyConstants.Params.REQUEST_SOURCE, String.valueOf(requestSource));
		map.put(AndyConstants.Params.PICKUP_TYPE, String.valueOf(pickupType));

		requestQueue.add(new VolleyHttpRequest(Method.POST, map,
				AndyConstants.ServiceCode.CREATE_REQUEST, this, this));
	}

	private void getOverhereRequests() {
		if (!AndyUtils.isNetworkAvailable(mapActivity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet),
					mapActivity);
			return;
		}

		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(getActivity()).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL, Lserver + AndyConstants.ServiceType.OVERHERE_REQUEST);
		map.put(AndyConstants.Params.ID, preferenceHelper.getUserId());
		map.put(AndyConstants.Params.OVERHERE_REQUEST_ID,
				String.valueOf(overhereDetail.getOverhereRequestId()));
		map.put(AndyConstants.Params.TOKEN, preferenceHelper.getSessionToken());

		requestQueue.add(new VolleyHttpRequest(Method.POST, map,
				AndyConstants.ServiceCode.OVERHERE_REQUEST, this, this));
	}

	private void updateOverhereRequests() {
		stopOverhereRequests();
		isOverhereRequest = true;
		overhereTimer = new Timer();
		overhereTimer.scheduleAtFixedRate(new TimerOvehereStatus(),
				AndyConstants.DELAY, AndyConstants.TIME_SCHEDULE);
	}


	private void stopOverhereRequests() {
		isOverhereRequest = false;
		if (overhereTimer != null) {
			overhereTimer.cancel();
			overhereTimer = null;
		}
	}


	private class TimerOvehereStatus extends TimerTask {
		@Override
		public void run() {
			if (isOverhereRequest) {
				getOverhereRequests();
			}
		}
	}

	private void cancelwalker(int currentWalker) {
		if (!AndyUtils.isNetworkAvailable(mapActivity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet),
					mapActivity);
			return;
		}

		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(getActivity()).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL, Lserver + AndyConstants.ServiceType.CANCEL_WALKER);
		map.put(AndyConstants.Params.ID, preferenceHelper.getUserId());
		map.put(AndyConstants.Params.OVERHERE_REQUEST_ID,
				String.valueOf(overhereDetail.getOverhereRequestId()));
		map.put(AndyConstants.Params.TOKEN, preferenceHelper.getSessionToken());

		requestQueue.add(new VolleyHttpRequest(Method.POST, map,
				AndyConstants.ServiceCode.CANCEL_WALKER, this, this));
	}

	private Bitmap getBitmapOfWebView(WebView webView) {

		webView.measure(View.MeasureSpec.makeMeasureSpec(
				View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
				View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		webView.layout(0, 0, webView.getMeasuredWidth(),
				webView.getMeasuredHeight());
		//webView.setDrawingCacheEnabled(true);
		//webView.buildDrawingCache();
		Bitmap bm = null;
		//float scale = getResources().getDisplayMetrics().density;
		if (adsBitmaps.size() >= nAds) {
			bm = adsBitmaps.get(imgIndex);
		} else if (webView.getMeasuredWidth() > 0 && webView.getMeasuredWidth() < 2000) {
			bm = bm.createBitmap(webView.getMeasuredWidth(),
					webView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

		} else {

			return null;
		}
		Canvas bigcanvas = null;
		Paint paint = null;
		if (adsBitmaps.size() >= nAds) {
			bigcanvas = adsCanvas.get(imgIndex);
			paint = adsPaint.get(imgIndex);
		} else {
			bigcanvas = new Canvas(bm);
			paint = new Paint();
			adsCanvas.add(bigcanvas);
			adsPaint.add(paint);
		}
		int iHeight = bm.getHeight();
		bigcanvas.drawBitmap(bm, 0, iHeight, paint);
		webView.draw(bigcanvas);
		webView.clearCache(true);

		return bm;

	}


	public void notifications(String mbody, int voice){

		Uri sound = Uri.parse("android.resource://" + mapActivity.getPackageName() + "/" + voice);
		String Channel_ID = "";
		if (voice == R.raw.start_tracking) {
			Channel_ID = "start_tracking";
		} else if (voice == R.raw.stop_tracking) {
			Channel_ID = "stop_tracking";
		} else {
			Channel_ID = "";
		}
		NotificationManager notificationManager = (NotificationManager) mapActivity.getSystemService(NOTIFICATION_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationCompat.Builder status = new NotificationCompat.Builder(mapActivity, Channel_ID)
					.setAutoCancel(true)
					.setWhen(System.currentTimeMillis())
					.setSmallIcon(R.drawable.ic_launcher)
					//.setOnlyAlertOnce(true)
					.setContentTitle(getResources().getString(R.string.app_name))
					.setContentText((String)mbody)
					.setVibrate(new long[]{0, 500, 1000})
					.setDefaults(Notification.DEFAULT_LIGHTS )
					.setSound(sound);
			// .setContentIntent(pendingIntent)
			// .setContent(views);
			notificationManager.notify(1000, status.build());
		} else {
			Notification noti = new Notification.Builder(mapActivity)
					.setContentTitle(getResources().getString(R.string.app_name))
					.setContentText(mbody)
					.setSmallIcon(R.drawable.ic_launcher).setSound(sound)
					.build();

			noti.flags |= Notification.FLAG_AUTO_CANCEL;
			if (sound == null) {
				noti.defaults |= Notification.DEFAULT_SOUND;
			}
			notificationManager.notify(1000, noti);
		}
	}

	/*
	private void startPlaying() {
		buttonStopPlay.setEnabled(true);
		buttonPlay.setEnabled(false);
		buttonPlay.setVisibility(GONE);
		buttonStopPlay.setVisibility(VISIBLE);
		if (player != null) {
			player.prepareAsync();

			player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					player.start();
				}
			});

		}

	}

	private void stopPlaying() {
		if (player != null) {
			player.stop();
			player.release();
			player = null;
			initializeMediaPlayer();
		}
		buttonPlay.setVisibility(VISIBLE);
		buttonStopPlay.setVisibility(GONE);
		buttonPlay.setEnabled(true);
		buttonStopPlay.setEnabled(false);

	}


	private void initializeMediaPlayer() {
		if (player == null) {
			player = new MediaPlayer();
			try {
				player.setAudioStreamType(AudioManager.STREAM_MUSIC);
				//player.setDataSource("http://cms.stream.publicradio.org/cms.mp3");
				player.setDataSource("http://classicalstream1.publicradio.org:80/");
				//player.setDataSource("http://radio.overhere.one:8000/stream.mp3");
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {

				public void onBufferingUpdate(MediaPlayer mp, int percent) {
					Log.i("Buffering", "" + percent);
				}
			});
		}
	}
     */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			//you are visible to user now - so set whatever you need
			//initResources();
		} else {
			//you are no longer visible to the user so cleanup whatever you need
			//cleanupResources();
			adsBitmaps.clear();
			adsBitmapDrawables.clear();
		}
	}

	private Runnable adsrunnable = new Runnable() {
		@Override
		public void run() {
      /* do what you need to do */

			if (isSliderOn) {

				// Update your UI
				//Log.v("Slider", "isSliderOn=" + isSliderOn + "&Index=" + imgIndex +
				//		"&RepeatTimes=" + switchTimes[imgIndex] + "&ArrySize=" + adsBitmaps.size());
				isSliderOn = false;
				//Uri vuri = Uri.parse("android.resource://" + mapActivity.getPackageName() + "/" + VID[imgIndex]);
				//mVideoView.setVideoURI(vuri);
				//imgSlider.setImageResource(IMG[imgIndex]);
				//mapActivity.getSupportActionBar().hide();
				//relMap.setVisibility(GONE);
				//btnGoOffline.setVisibility(GONE);
				//imgSlider.setVisibility(VISIBLE);
				//mVideoView.setVisibility(VISIBLE);
				//mVideoView.start();
				//imgIndex = (imgIndex + 1) % 18;
				if (switchTimes[imgIndex] >= 10) {
					//need download photo and already load in webview
					Bitmap bitmap = null;
					bitmap = getBitmapOfWebView(adsView);
					saveBitmap(bitmap, Ads[imgIndex]);
					if (bitmap != null) {
						if (adsBitmaps.size() < nAds) {
							adsBitmaps.add(imgIndex,bitmap);
							BitmapDrawable drawable = new BitmapDrawable(mapActivity.getResources(), adsBitmaps.get(imgIndex));
							adsBitmapDrawables.add(imgIndex,drawable);
						}
						switchTimes[imgIndex] = 0;
					}
				}

				if (adsBitmaps.size() > imgIndex) {
					if (adsBitmaps.get(imgIndex) != null) {
						//first drawable is alwas blank
						if (imgIndex > 0 ) {
							mapActivity.getSupportActionBar().hide();
							relMap.setVisibility(GONE);
							btnGoOffline.setVisibility(GONE);
							linearOffline.setVisibility(GONE);
							imgSlider.setImageDrawable(adsBitmapDrawables.get(imgIndex));
							imgSlider.setVisibility(VISIBLE);
							tvPost.setText(postTexts.get(imgIndex));
							tvPost.setVisibility(VISIBLE);
						}
						switchTimes[imgIndex] = switchTimes[imgIndex] + 1;
						imgIndex = (imgIndex + 1) % nAds;
					}
				} else {
				    //sometime save image is interrupted
                    switchTimes[imgIndex] = 10;
                }

				System.gc();
				Runtime.getRuntime().gc();
			} else {
				// Update your UI
				//Log.v("Slider", "isSliderOn=" + isSliderOn + "&Index=" + imgIndex +
				//		"&RepeatTimes=" + switchTimes[imgIndex] + "&ArrySize=" + adsBitmaps.size());
				//if(is_moving) isSliderOn = true;
				isSliderOn = true;
				if (switchTimes[imgIndex] >= 10) {
					Bitmap bitmap = null;
					bitmap = getBitmapfromFile(Ads[imgIndex]);
					Log.v("postDuration","=" + postDuration + " adsTexts"+"=" + postTexts.get(imgIndex));
					long tdiff = (new Date()).getTime() - preferenceHelper.getLong("lasttime-postupdate");
	                //update post photos in duration
					//24*3600*1000 one one update
					if(tdiff > postDuration*24*3600*1000) {
						bitmap = null;
						//update lasttime-postupdate
						if(imgIndex == (nAds - 1)) preferenceHelper.putLong("lasttime-postupdate",(new Date()).getTime() );
					}
					if (bitmap == null ) {
						adsView.loadUrl(AndyConstants.ServiceType.LOCAL_URL + "ads/" + Ads[imgIndex] + ".jpg");
					} else if (fileLength < 100000) {
						adsView.loadUrl(AndyConstants.ServiceType.LOCAL_URL + "ads/" + Ads[imgIndex] + ".jpg");
					} else {
						if (adsBitmaps.size() < nAds) {
							adsBitmaps.add(imgIndex, bitmap);
							BitmapDrawable drawable = new BitmapDrawable(mapActivity.getResources(), adsBitmaps.get(imgIndex));
							adsBitmapDrawables.add(imgIndex, drawable);
						}
						switchTimes[imgIndex] = 0;
					}
				}
				//adsView.loadUrl(AndyConstants.ServiceType.LOCAL_URL + "ads/" + Ads[imgIndex] + ".jpg");
				if (switchTimes[imgIndex] >= 10) {
					//after shown 10 times, get a new slider
					//restart app to clear memory
					if(adsBitmaps.size()>= nAds &&imgIndex == 0) {
						restartOverHere();
					}
				}
				if(imgSlider != null) imgSlider.setVisibility(GONE);
				//if(mVideoView.isShown())
				//if(adsView.isShown())
				{
					//mVideoView.setVisibility(GONE);
				  //mVideoView.stopPlayback();
					//adsView.setVisibility(GONE);
				    mapActivity.getSupportActionBar().show();
				    relMap.setVisibility(VISIBLE);
				    btnGoOffline.setVisibility(VISIBLE);
					tvPost.setVisibility(GONE);
				   // if(adsView !=null) adsView.destroyDrawingCache();
				//add next statement, memory leaking happens.
				//imgSlider.setImageDrawable(null);
				//call gc before free memory used up.
				  //Runtime.getRuntime().gc();
				}




			}

      /* and here comes the "trick" */
			if(adshandler !=null) adshandler.postDelayed(this, 10000);
		}

		;
	};

	private void restartOverHere() {
		Intent mStartActivity = new Intent(mapActivity, MapActivity.class);
		int mPendingIntentId = 123456;
		PendingIntent mPendingIntent = PendingIntent.getActivity(mapActivity, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager mgr = (AlarmManager) mapActivity.getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC,System.currentTimeMillis()+100,mPendingIntent);
		System.exit(0);
    }

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		restartOverHere();
	}

	private void UploadDataToServer() {
		if (!AndyUtils.isNetworkAvailable(mapActivity)) {
			return;
		}
		if(latitude == null) return;
		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(getActivity()).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL, Lserver + AndyConstants.ServiceType.UPDATE_PROVIDER_LOCATION);
		map.put(AndyConstants.Params.ID, preferenceHelper.getUserId());
		map.put(AndyConstants.Params.TOKEN, preferenceHelper.getSessionToken());
		map.put(AndyConstants.Params.LATITUDE, latitude);
		map.put(AndyConstants.Params.LONGITUDE, longitude);
		map.put(AndyConstants.Params.BEARING, bearing + "");
		requestQueue.add(new VolleyHttpRequest(Method.POST, map,
				AndyConstants.ServiceCode.UPDATE_PROVIDER_LOCATION, this, this));

	}

	private void CheckMoving() {
		if (SystemClock.elapsedRealtime() - vLastTime < 30000) {
			return;
		}
		if(myOldLocation == null) return;
		vLastTime = SystemClock.elapsedRealtime();
		double DD = AndyUtils.distance(myOldLocation.getLatitude(),
				myOldLocation.getLongitude(),myLocation.getLatitude(),
				myLocation.getLongitude(),'K');
		//if(DD > 0.1) is_moving = true;
		//if(DD < 0.05) is_moving = false;
		//for demo only
		is_moving = true;
		myOldLocation = myLocation;
		//only when car is moving, ads video will show. andtutil.distance();
		//if driver run 100 metres in 30 second , start to show ads video; if driver run less than 50 metres in 30 seconds, then stop show ads videos

	}

    private void showJobDetails(RequestDetail requestDetail) {
        jobDialog = new Dialog(getActivity());
		jobDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		jobDialog.setContentView(R.layout.u_jobdetails);
		jobDialog.getWindow().getDecorView().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        tvJobtitle = (AutoCompleteTextView) jobDialog.findViewById(R.id.tvJobtitle);
        tvPickupAddress = (AutoCompleteTextView) jobDialog.findViewById(R.id.tvPickupAddress);
        tvDropOffAddress = (AutoCompleteTextView) jobDialog.findViewById(R.id.tvDropOffAddress);
        tvReward = (AutoCompleteTextView) jobDialog.findViewById(R.id.tvReward);
		jobDialog.findViewById(R.id.btnCancelJob).setOnClickListener(this);
		jobDialog.findViewById(R.id.btnAcceptJob).setOnClickListener(this);

		String Ptype = requestDetail.getPickupType();
		String Reward = requestDetail.getReward();
		Reward = Reward.replace(".00","");
		Reward = Reward.replace("null","");
		int Type_pos = Integer.parseInt(requestDetail.getType_pos());
		String rating = String.format("%.02f", requestDetail.getClientRating());
		String t1 = "Driver"+"("+rating + ")";;
		if(Ptype.contains("U")) t1 = "User"+"("+rating + ")";
		String t2 = "OverHere";
		if(Ptype.contains("B")) t2 = "Booking";
		String t3 = ServiceTypes[Type_pos];
		//overhere -30 credit points, booking -50 pts, free help + 20 point, from Settings
        String credit =   Integer.toString(requestDetail.getCredit()) + " pts";
		String t4 ="";
		if(Ptype.contains("H") && Reward =="" )  t4 = " +" + credit;
		else t4 = " -" + credit;
        if(Type_pos>12&&Reward.equalsIgnoreCase("0.00")) t4 = " +" + credit;
        if(Reward.equals("")) Reward = "By Meter";
		tvJobtitle.setText(t1 + "**" + t2);
		String t5 = preferenceHelper.getPickupAddress();
		t5 = t5.replaceAll("[0-9]","x");
		tvPickupAddress.setText(t5);
		String t6 = preferenceHelper.getDropoffAddress();
		t6 = t6.replaceAll("[0-9]","x");
		tvDropOffAddress.setText(t6);
		tvReward.setText(t3+ ": $"+Reward + t4);
		jobDialog.show();
    }

	private LatLng getLocationFromAddress(final String place) {
		AppLog.Log("Address", "" + place);
		LatLng loc = null;
		Geocoder gCoder = new Geocoder(mapActivity);
		try {
			final List<Address> list = gCoder.getFromLocationName(place, 1);
			if (list != null && list.size() > 0) {
				loc = new LatLng(list.get(0).getLatitude(), list.get(0)
						.getLongitude());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return loc;
	}


	private void showTimePicker () {

        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        mTimePicker = new TimePickerDialog(mapActivity, R.style.myTimePickerStyle, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                String time = selectedHour + ":" + selectedMinute;
                btnTimePicker.setText(time);
                pickuphour = selectedHour + "";
                pickupminute = selectedMinute + "";
                }
        }, hour, minute, true);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

	private void saveBitmap(Bitmap bitmap,String filename ) {
		if (bitmap != null) {
			String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
					"/OverThere";
			File dir = new File(file_path);
			if(!dir.exists()) dir.mkdirs();
			File file = new File(dir, filename + ".jpg");
			 if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {

				file.delete();
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {

				FileOutputStream out = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
				out.flush();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private Bitmap getBitmapfromFile (String filename) {
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/OverThere";
        File dir = new File(file_path);
        File file = new File(dir, filename + ".jpg");
        Bitmap bitmap = null;
        if (file.exists()) {
        	fileLength = file.length();
            bitmap = BitmapFactory.decodeFile(file.getPath());
        } else {
        	fileLength = 0;
		}
        return bitmap;
    }

	private void getPostText() {

		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(mapActivity).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL,
				Lserver + AndyConstants.ServiceType.GET_POST_TEXT + AndyConstants.Params.ID + "="
						+ new PreferenceHelper(mapActivity).getUserId() + "&"
						+ AndyConstants.Params.TOKEN + "="
						+ new PreferenceHelper(mapActivity).getSessionToken());
		requestQueue.add(new VolleyHttpRequest(Method.GET, map,
				AndyConstants.ServiceCode.GET_POST_TEXT, this, this));
	}

	private void clearMemory() {
		//mMap=null;
		llAcceptReject=null;
		llUserDetailView=null;
		btnClientAccept=null;
		btnClientReject=null;
		btnClientReqRemainTime=null;
		timer=null;
		overhereTimer = null;
		seekbarTimer=null;
		requestDetail=null;
		overhereDetail=null;
		markerDriverLocation=null;
		markerClientLocation=null;

		locationHelper=null;
		tvClientName=null;
		tvClientRating=null;
		ivClientProfilePicture=null;
		aQuery=null;
		requestReciever=null;
		clientRequestView=null;
		mMapView=null;
		mBundle=null;
		tvApprovedClose=null;
		mDialog=null;
		soundPool=null;
		requestQueue=null;
		btnGoOffline=null;
		relMap=null;
		linearOffline=null;
		btnMyLocation=null;
		btnOverHere=null;
		dialog=null;
		pickupdate=null;
		pickupserviceTypes=null;
		pickupAddress=null;
		dropoffAddress=null;
		serviceReward=null;
		address=null;
		et = null;
		//mVideoView = null;
		adshandler = null;
		switchTimes = null;
		tvJobtitle=null;
		tvPickupAddress=null;
		tvDropOffAddress=null;
		tvReward = null;
		mTimePicker = null;
		btnTimePicker=null;
		Runtime.getRuntime().gc();
	}

	private void createNotificationChn(String ChnId, Uri voice) {
		NotificationChannel mChannel;
		mChannel = new NotificationChannel(ChnId, AndyConstants.CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
		mChannel.setLightColor(Color.GRAY);
		mChannel.enableLights(true);
		mChannel.setDescription(AndyConstants.CHANNEL_SIREN_DESCRIPTION);
		AudioAttributes audioAttributes = new AudioAttributes.Builder()
				.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
				.setUsage(AudioAttributes.USAGE_NOTIFICATION)
				.build();
		mChannel.setSound(voice, audioAttributes);
		NotificationManager notificationManager = (NotificationManager) mapActivity.getSystemService(NOTIFICATION_SERVICE);
		if (notificationManager != null) {
			notificationManager.createNotificationChannel( mChannel );
		}

	}



}