package com.overthere.express.fragment;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.androidquery.AQuery;
import com.google.android.gms.maps.Projection;
import com.overthere.express.MapActivity;
import com.overthere.express.R;
import com.overthere.express.base.BaseMapFragment;
import com.overthere.express.db.DBHelper;
import com.overthere.express.locationupdate.LocationHelper;
import com.overthere.express.locationupdate.LocationHelper.OnLocationReceived;
import com.overthere.express.model.BeanRoute;
import com.overthere.express.model.BeanStep;
import com.overthere.express.model.RequestDetail;
import com.overthere.express.parse.AsyncTaskCompleteListener;
import com.overthere.express.parse.HttpRequester;
import com.overthere.express.parse.ParseContent;
import com.overthere.express.parse.VolleyHttpRequest;
import com.overthere.express.utills.AndyConstants;
import com.overthere.express.utills.AndyUtils;
import com.overthere.express.utills.AppLog;
import com.overthere.express.utills.PreferenceHelper;
import com.overthere.express.widget.MyFontTextView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import androidx.core.content.ContextCompat;

import static android.Manifest.permission.CALL_PHONE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.view.View.GONE;


/**
 * @author Overhere Team
 * 
 */
public class JobFragment extends BaseMapFragment implements
		AsyncTaskCompleteListener, OnLocationReceived {
	private GoogleMap googleMap;
	private PolylineOptions lineOptions;
	private BeanRoute route;
	private ArrayList<LatLng> points;
	private MyFontTextView tvJobTime, tvJobDistance, tvJobReward, tvJobStatus, tvClientName,
			tvDestinationAddress;
	private ImageView ivClientProfilePicture;
	private RatingBar tvClientRating;
	private ParseContent parseContent;

	private Location location, myOldLocation,myLocation,OldLoc;
	private LocationHelper locationHelper;
	private AQuery aQuery;
	private RequestDetail requestDetail, requestDetails;
	private Marker markerDriverLocation, markerClientLocation;
	private Timer timer, elapsedTimer;
	private DBHelper dbHelper;
	private int jobStatus = 0;
	private String time, distance = "0";
	private final String TAG = "JobFragment";
	DecimalFormat decimalFormat;
	private BroadcastReceiver mReceiver;
	private MyFontTextView tvPaymentType;
	private BroadcastReceiver modeReceiver;
	private View jobFragmentView;
	private MapView mMapView;
	public final long ELAPSED_TIME_SCHEDULE = 60 * 1000;
	private Bundle mBundle;
	private BroadcastReceiver destReceiver;
	private BeanRoute routeDest;
	private ArrayList<LatLng> pointsDest;
	private PolylineOptions lineOptionsDest;
	private Polyline polyLineDest;
	private Marker markerDestination;
	private RequestQueue requestQueue;
	private ImageButton btnNavigate;
	private ImageButton btnMyLocation;
	private boolean cameraFollow = true;
	private boolean isAddMarker = false;
	private BeanRoute routeClient;
	private ArrayList<LatLng> pointsClient;
	private PolylineOptions lineOptionsClient;
	private Polyline polyLineClient;
	private boolean iswarned = false;
	private String latitude, longitude;
	private float bearing;
	private float avgBearing =0;
	private Handler upHandler = new Handler();
	private  long mLastClickTime = 0;
	private boolean gotDest = false;
	private long startTime = 0;
	private float dist = 0;
	private boolean origin = true;
	private boolean GPSJumping = false;
	private boolean isPathDestination = false, isPathClient = false;
	private Polyline gpsTrack;
	private LatLng bottom;
	private int yMatrix = 200, xMatrix =40;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		jobFragmentView = inflater.inflate(R.layout.fragment_job, container,
				false);
		try {
			MapsInitializer.initialize(getActivity());
		} catch (Exception e) {
		}
		btnMyLocation = (ImageButton) jobFragmentView.findViewById(R.id.btnMyLocation);
		preferenceHelper = new PreferenceHelper(getActivity());
		tvJobTime = (MyFontTextView) jobFragmentView
				.findViewById(R.id.tvJobTime);
		tvJobDistance = (MyFontTextView) jobFragmentView
				.findViewById(R.id.tvJobDistance);
		tvJobReward = (MyFontTextView) jobFragmentView
				.findViewById(R.id.tvReward);
		tvJobStatus = (MyFontTextView) jobFragmentView
				.findViewById(R.id.tvJobStatus);
		tvClientName = (MyFontTextView) jobFragmentView
				.findViewById(R.id.tvClientName);
		tvPaymentType = (MyFontTextView) jobFragmentView
				.findViewById(R.id.tvPaymentType);
		// tvClientPhoneNumber = (MyFontTextView) jobFragmentView
		// .findViewById(R.id.tvClientNumber);
		tvClientRating = (RatingBar) jobFragmentView
				.findViewById(R.id.tvClientRating);
		ivClientProfilePicture = (ImageView) jobFragmentView
				.findViewById(R.id.ivClientImage);
		btnNavigate = (ImageButton) jobFragmentView
				.findViewById(R.id.btnNavigate);
		tvDestinationAddress = (MyFontTextView) jobFragmentView
				.findViewById(R.id.tvDestinationAddress);

		tvJobStatus.setOnClickListener(this);
		btnMyLocation.setOnClickListener(this);
		if (preferenceHelper.isNavigate()) {
			btnNavigate.setVisibility(GONE);
		} else {
			//btnNavigate.setVisibility(View.VISIBLE);
			//btnNavigate.setOnClickListener(this);
		}
		jobFragmentView.findViewById(R.id.tvJobCallClient).setOnClickListener(
				this);
        iswarned = false;
		setPaymentType();
        startTime = preferenceHelper.getStartTime();
        dist = preferenceHelper.getDistance();
		return jobFragmentView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBundle = savedInstanceState;
		requestQueue = Volley.newRequestQueue(mapActivity);
        iswarned = false;
		origin = true;
		upHandler.postDelayed(Uprunnable, 10000);

		DisplayMetrics metrics1 = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics1);
		switch(metrics1.densityDpi)
		{
			case DisplayMetrics.DENSITY_LOW:
				yMatrix = 100;
				xMatrix = 10;
				break;
			case DisplayMetrics.DENSITY_MEDIUM:
				yMatrix = 150;
				xMatrix = 10;
				break;
			case DisplayMetrics.DENSITY_HIGH:
				//yMatrix = 150;
				yMatrix = 225;
				xMatrix = 10;
				break;
			case DisplayMetrics.DENSITY_XHIGH:
				//yMatrix = 200;
				yMatrix = 300;
				xMatrix = 10;
				break;
			case DisplayMetrics.DENSITY_XXHIGH:
				//yMatrix = 200;
				yMatrix = 400;
				xMatrix = 10;
				break;
		}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        iswarned = false;
        gotDest = false;
		parseContent = new ParseContent(mapActivity);
		decimalFormat = new DecimalFormat("0.00");
		points = new ArrayList<LatLng>();
		aQuery = new AQuery(mapActivity);
		dbHelper = new DBHelper(mapActivity);
		jobStatus = getArguments().getInt(AndyConstants.JOB_STATUS,
				AndyConstants.IS_WALKER_ARRIVED);
		requestDetail = (RequestDetail) getArguments().getSerializable(
				AndyConstants.REQUEST_DETAIL);
		preferenceHelper = new PreferenceHelper(getActivity());
		if (jobStatus >= AndyConstants.IS_WALK_STARTED) {
			if(preferenceHelper.getDropoffAddress() != null) {
				if (preferenceHelper.getDropoffAddress().contains("unknown")) {
                    tvDestinationAddress.setVisibility(GONE);
                    tvClientRating.setVisibility(View.VISIBLE);
                    gotDest = false;
				} else {
					tvDestinationAddress.setVisibility(View.VISIBLE);
                    tvClientRating.setVisibility(GONE);
                    tvDestinationAddress.setText(preferenceHelper.getDropoffAddress());
                    //gotDest = true;
				}
			} else {
				tvDestinationAddress.setVisibility(GONE);
				tvClientRating.setVisibility(View.VISIBLE);
				gotDest = false;
			}

		} else {
			if(!preferenceHelper.getPickupAddress().contains("unknown")) {
				tvDestinationAddress.setVisibility(View.VISIBLE);
				tvClientRating.setVisibility(GONE);
				tvDestinationAddress.setText(preferenceHelper.getPickupAddress());
				//gotDest = true;
				//it is actually pickup address
			}
		}
		if (jobStatus == AndyConstants.IS_WALK_COMPLETED) {
			startElapsedTimer();
			getPathFromServer();
		}
        startCheckingRequestStatus();
		setClientDetails(requestDetail);
		setjobStatus(jobStatus);
		String reward = preferenceHelper.getString(AndyConstants.Params.REWARD);
		if(reward.equalsIgnoreCase("null")) tvJobReward.setVisibility(GONE);
		else if (reward.isEmpty()) {
			tvJobReward.setVisibility(GONE);
		} else {
			float amount = Float.parseFloat(reward);
			if(amount <= 0.01) reward = "";
			if (!reward.isEmpty()) tvJobReward.setText("$"+reward);
				//else tvJobReward.setText("$0");
			else tvJobReward.setVisibility(GONE);
		}


		mMapView = (MapView) jobFragmentView.findViewById(R.id.jobMap);
		mMapView.onCreate(mBundle);
		setUpMap();

		locationHelper = new LocationHelper(getActivity());
		locationHelper.setLocationReceivedLister(this);
		locationHelper.onStart();

	}

	/**
	 * 
	 */
	private void getPathFromServer() {
		if (!AndyUtils.isNetworkAvailable(mapActivity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet),
					mapActivity);
			return;
		}
		AndyUtils.showCustomProgressDialog(mapActivity, "", getResources()
				.getString(R.string.progress_loading), false);
		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(mapActivity).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL,
				Lserver + AndyConstants.ServiceType.PATH_REQUEST
						+ AndyConstants.Params.ID + "="
						+ preferenceHelper.getUserId() + "&"
						+ AndyConstants.Params.TOKEN + "="
						+ preferenceHelper.getSessionToken() + "&"
						+ AndyConstants.Params.REQUEST_ID + "="
						+ preferenceHelper.getRequestId());
		// new HttpRequester(mapActivity, map,
		// AndyConstants.ServiceCode.PATH_REQUEST, true, this);

		requestQueue.add(new VolleyHttpRequest(Method.GET, map,
				AndyConstants.ServiceCode.PATH_REQUEST, this, this));
	}

	private void setClientDetails(RequestDetail reqDetail) {
		tvClientName.setText(reqDetail.getClientName());
		// tvClientPhoneNumber.setText(reqDetail.getClientPhoneNumber());
		if (reqDetail.getClientRating() != 0) {
			tvClientRating.setRating(reqDetail.getClientRating());
		}
		if (TextUtils.isEmpty(reqDetail.getClientProfile())) {
			aQuery.id(ivClientProfilePicture).progress(R.id.pBar)
					.image(R.drawable.user);
		} else {
			aQuery.id(ivClientProfilePicture).progress(R.id.pBar)
					.image(reqDetail.getClientProfile());

		}

	}

	public void getDestinationAddress(String latitude, String longitude) {

		if (latitude.contains("0.000")) {
			tvDestinationAddress.setVisibility(GONE);
			tvClientRating.setVisibility(View.VISIBLE);
		} else {
			LatLng dest = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
			drawPath(markerClientLocation.getPosition(), dest);

			if(preferenceHelper.getDropoffAddress().contains("unknown")) {
					Geocoder geocoder;
					List<Address> addresses;
					geocoder = new Geocoder(mapActivity, Locale.getDefault());

					try {
						addresses = geocoder.getFromLocation(
								Double.parseDouble(latitude),
								Double.parseDouble(longitude), 1);
						if (addresses == null || addresses.size() == 0)
							return;
						String address = addresses.get(0).getAddressLine(0);

						tvDestinationAddress.setVisibility(View.VISIBLE);
						tvClientRating.setVisibility(GONE);

					preferenceHelper.putDropoffAddress(address);
					tvDestinationAddress.setText(address);
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			} else {
				    tvDestinationAddress.setVisibility(View.VISIBLE);
				    tvClientRating.setVisibility(GONE);
					tvDestinationAddress.setText(preferenceHelper.getDropoffAddress());
			}

			tvDestinationAddress.clearFocus();

			gotDest = true;


		}

	}

	/**
	 * it is used for seeting text for jobstatus on textview
	 */
	private void setjobStatus(int jobStatus) {

		switch (jobStatus) {
		case AndyConstants.IS_WALKER_STARTED:
			tvJobStatus.setText(mapActivity.getResources().getString(
					R.string.text_walker_started));
			break;
		case AndyConstants.IS_WALKER_ARRIVED:
			tvJobStatus.setText(mapActivity.getResources().getString(
					R.string.text_walker_arrived));
			break;
		case AndyConstants.IS_WALK_STARTED:
			tvJobStatus.setText(mapActivity.getResources().getString(
					R.string.text_walk_started));
			break;
		case AndyConstants.IS_WALK_COMPLETED:
			tvJobStatus.setText(mapActivity.getResources().getString(
					R.string.text_walk_completed));
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		// Preventing multiple clicks, using threshold of 1 second
		if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
			return;
		}
		mLastClickTime = SystemClock.elapsedRealtime();

		switch (v.getId()) {
		case R.id.tvJobStatus:
			switch (jobStatus) {
			case AndyConstants.IS_WALKER_STARTED:
				mapActivity.clearAll();
				walkerStarted();
				break;
			case AndyConstants.IS_WALKER_ARRIVED:
				mapActivity.clearAll();
				walkerArrived();
				break;
			case AndyConstants.IS_WALK_STARTED:
				//mapActivity.clearAll();
				walkStarted();
				break;
			case AndyConstants.IS_WALK_COMPLETED:
				mapActivity.clearAll();
				walkCompleted();

				break;
			default:
				break;
			}

			break;
		case R.id.tvJobCallClient:
			if (!TextUtils.isEmpty(requestDetail.getClientPhoneNumber())) {
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:"
						+ requestDetail.getClientPhoneNumber()));
				if (ContextCompat.checkSelfPermission(mapActivity, CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
					startActivity(callIntent);
				} else {
					requestPermissions(new String[]{CALL_PHONE}, 1);
				}

			} else {
				Toast.makeText(
						mapActivity,
						mapActivity.getResources().getString(
								R.string.toast_number_not_found),
						Toast.LENGTH_SHORT).show();
			}
			break;

		case R.id.btnNavigate:
			if (markerClientLocation != null && markerDriverLocation != null) {
				preferenceHelper.putIsNavigate(true);
				animateCamera(markerDriverLocation.getPosition());
				v.setVisibility(GONE);
				drawPathToClient(markerDriverLocation.getPosition(),
						markerClientLocation.getPosition());
			} else {
				AndyUtils.showToast("Wating for location", getActivity());
			}
			break;
			case R.id.btnMyLocation:
				if(location!=null) {
					LatLng latLng = new LatLng(location.getLatitude(),
							location.getLongitude());
					CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(
							latLng);
					if(googleMap!=null) googleMap.animateCamera(cameraUpdate);
				}
				if(cameraFollow) {cameraFollow = false;
					AndyUtils.showToast("Stop tracking on map",mapActivity );
					String mbody = "Stop tracking on map";
					notifications(mbody,R.raw.stop_tracking);
				}
				else {cameraFollow = true;
					AndyUtils.showToast("Start tracking on map", mapActivity);
					String mbody = "Start tracking on map";
					notifications(mbody,R.raw.start_tracking);
				}
			break;
		default:
			break;
		}
	}

	private void animateCamera(LatLng latlng) {
		if (markerClientLocation != null) {
			try {
				Location dest = new Location("dest");
				dest.setLatitude(markerClientLocation.getPosition().latitude);
				dest.setLongitude(markerClientLocation.getPosition().longitude);

				//CameraPosition cameraPosition = new CameraPosition.Builder()
				//		.target(latlng).bearing(location.bearingTo(dest))
				//		.zoom(googleMap.getCameraPosition().zoom).tilt(45)
				//		.build();
				CameraPosition cameraPosition = new CameraPosition.Builder()
						.target(latlng).bearing(location.bearingTo(dest))
						.tilt(45)
						.build();
				googleMap.animateCamera(CameraUpdateFactory
						.newCameraPosition(cameraPosition));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void drawPathToClient(LatLng source, LatLng destination) {
		if (!AndyUtils.isNetworkAvailable(mapActivity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet),
					mapActivity);
			return;
		}
		if (source == null || destination == null) {
			return;
		}
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(AndyConstants.URL,
				"https://maps.googleapis.com/maps/api/directions/json?origin="
						+ source.latitude + "," + source.longitude
						+ "&destination=" + destination.latitude + ","
						+ destination.longitude + "&sensor=false&key="
						+ AndyConstants.DIRECTION_API_KEY);

		// new HttpRequester(mapActivity, map,
		// AndyConstants.ServiceCode.DRAW_PATH_CLIENT, true, this);
		requestQueue.add(new VolleyHttpRequest(Method.GET, map,
				AndyConstants.ServiceCode.DRAW_PATH_CLIENT, this, this));
	}

	/**
	 * send this when walk completed
	 */
	private void walkCompleted() {
		if (!AndyUtils.isNetworkAvailable(mapActivity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet),
					mapActivity);
			return;
		}

		AndyUtils.showCustomProgressDialog(mapActivity, "", getResources()
				.getString(R.string.progress_send_request), false);

		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(mapActivity).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL, Lserver + AndyConstants.ServiceType.WALK_COMPLETED);
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
		map.put(AndyConstants.Params.TIME, time);
		// new HttpRequester(mapActivity, map,
		// AndyConstants.ServiceCode.WALK_COMPLETED, this);

		requestQueue.add(new VolleyHttpRequest(Method.POST, map,
				AndyConstants.ServiceCode.WALK_COMPLETED, this, this));
	}

	/**
	 * send this when job started
	 */
	private void walkStarted() {
		if (!AndyUtils.isNetworkAvailable(mapActivity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet),
					mapActivity);
			return;
		}
		AndyUtils.showCustomProgressDialog(mapActivity, "", getResources()
				.getString(R.string.progress_send_request), false);

		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(mapActivity).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL, Lserver + AndyConstants.ServiceType.WALK_STARTED);
		map.put(AndyConstants.Params.ID, preferenceHelper.getUserId());
		map.put(AndyConstants.Params.REQUEST_ID,
				String.valueOf(preferenceHelper.getRequestId()));
		map.put(AndyConstants.Params.TOKEN, preferenceHelper.getSessionToken());
		map.put(AndyConstants.Params.LATITUDE,
				preferenceHelper.getWalkerLatitude());
		map.put(AndyConstants.Params.LONGITUDE,
				preferenceHelper.getWalkerLongitude());
		// new HttpRequester(mapActivity, map,
		// AndyConstants.ServiceCode.WALK_STARTED, this);

		requestQueue.add(new VolleyHttpRequest(Method.POST, map,
				AndyConstants.ServiceCode.WALK_STARTED, this, this));
	}

	/**
	 * send this when walker arrived client's location
	 */
	private void walkerArrived() {
		if (!AndyUtils.isNetworkAvailable(mapActivity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet),
					mapActivity);
			return;
		}

		AndyUtils.showCustomProgressDialog(mapActivity, "", getResources()
				.getString(R.string.progress_send_request), false);

		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(mapActivity).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL, Lserver + AndyConstants.ServiceType.WALK_ARRIVED);
		map.put(AndyConstants.Params.ID, preferenceHelper.getUserId());
		map.put(AndyConstants.Params.REQUEST_ID,
				String.valueOf(preferenceHelper.getRequestId()));
		map.put(AndyConstants.Params.TOKEN, preferenceHelper.getSessionToken());
		map.put(AndyConstants.Params.LATITUDE,
				preferenceHelper.getWalkerLatitude());
		map.put(AndyConstants.Params.LONGITUDE,
				preferenceHelper.getWalkerLongitude());
		// new HttpRequester(mapActivity, map,
		// AndyConstants.ServiceCode.WALKER_ARRIVED, this);

		requestQueue.add(new VolleyHttpRequest(Method.POST, map,
				AndyConstants.ServiceCode.WALKER_ARRIVED, this, this));
	}

	/**
	 * send this when walker started his/her run
	 */
	private void walkerStarted() {
		if (!AndyUtils.isNetworkAvailable(mapActivity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet),
					mapActivity);
			return;
		}

		AndyUtils.showCustomProgressDialog(mapActivity, "", getResources()
				.getString(R.string.progress_send_request), false);

		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(mapActivity).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL, Lserver + AndyConstants.ServiceType.WALKER_STARTED);
		map.put(AndyConstants.Params.ID, preferenceHelper.getUserId());
		map.put(AndyConstants.Params.REQUEST_ID,
				String.valueOf(preferenceHelper.getRequestId()));
		map.put(AndyConstants.Params.TOKEN, preferenceHelper.getSessionToken());
		map.put(AndyConstants.Params.LATITUDE,
				preferenceHelper.getWalkerLatitude());
		map.put(AndyConstants.Params.LONGITUDE,
				preferenceHelper.getWalkerLongitude());
		// new HttpRequester(mapActivity, map,
		// AndyConstants.ServiceCode.WALKER_STARTED, this);

		requestQueue.add(new VolleyHttpRequest(Method.POST, map,
				AndyConstants.ServiceCode.WALKER_STARTED, this, this));
	}

	private void setUpMap() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (googleMap == null) {
			((MapView) jobFragmentView.findViewById(R.id.jobMap)).getMapAsync(new OnMapReadyCallback() {
				@Override
				public void onMapReady(GoogleMap map) {
					//map.setMyLocationEnabled(true);
					map.getUiSettings().setMyLocationButtonEnabled(false);
					map.getUiSettings().setZoomControlsEnabled(true);
					map.getUiSettings().setZoomGesturesEnabled(true);
					map.getUiSettings().setScrollGesturesEnabled(true);
                    map.setPadding(0,0,0,100);
					googleMap = map;


			initPreviousDrawPath();

			googleMap.setInfoWindowAdapter(new InfoWindowAdapter() {

				// Use default InfoWindow frame

				@Override
				public View getInfoWindow(Marker marker) {
					View v = mapActivity.getLayoutInflater().inflate(
							R.layout.info_window_layout, null);

					((TextView) v).setText(marker.getTitle());
					return v;
				}

				// Defines the contents of the InfoWindow

				@Override
				public View getInfoContents(Marker marker) {

					// Getting view from the layout file info_window_layout View

					// Getting reference to the TextView to set title TextView

					// Returning the view containing InfoWindow contents return
					return null;

				}

			});

			googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {
				@Override
				public boolean onMarkerClick(Marker marker) {
					marker.showInfoWindow();
					return true;
				}
			});

			addMarker();
				}
			});

		} else {

			//googleMap.setMyLocationEnabled(true);
			googleMap.getUiSettings().setMyLocationButtonEnabled(false);
			googleMap.getUiSettings().setZoomControlsEnabled(true);
		}
	}

	// It will add marker on map of walker location
	private void addMarker() {
		if (googleMap == null) {
			setUpMap();
			return;
		}

	}

	public void onDestroyView() {
		mMapView.onDestroy();
		googleMap= null;
		//jobFragmentView = null;
		mMapView = null;
		ivClientProfilePicture = null;
        clearMemory();
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopElapsedTimer();
		requestDetail = null;
		requestDetails = null;
		timer = null;

		ubregisterCancelReceiver();
		ubregisterPaymentModeReceiver();
		unRegisterDestinationReceiver();
		Log.v("DestroyingFragment","Destroying Job Map");
		//System.gc();
	}

	private void initPreviousDrawPath() {
		// points = dbHelper.getLocations();
		lineOptions = new PolylineOptions();
		lineOptions.addAll(points);
		lineOptions.width(10);
		lineOptions.geodesic(true);
		lineOptions.color(getResources().getColor(R.color.skyblue));
		if(googleMap!=null) gpsTrack = googleMap.addPolyline(lineOptions);
		//points.clear();
	}

	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		AndyUtils.removeCustomProgressDialog();
		if (!this.isVisible())
			return;
		LatLng latlong;
		String[] part = response.split("\\{",2);
		response = "{" + part[1];

		switch (serviceCode) {

		case AndyConstants.ServiceCode.CHECK_REQUEST_STATUS:
          //in case request is cancelled by passenger during job precedure
			if (parseContent.isSuccess(response)) {
				requestDetails = parseContent.parseRequestStatus(response);
                setPaymentType();
                if(!gotDest && jobStatus >=AndyConstants.IS_WALK_STARTED) {
                	if(markerClientLocation != null) {
						getDestinationAddress(requestDetails.getDestinationLatitude(), requestDetails.getDestinationLongitude());
					}
                }
				if(requestDetails.getJobStatus()==AndyConstants.NO_REQUEST) {
					stopCheckingRequestStatus();
					preferenceHelper.clearRequestData();
					SupportMapFragment f = (SupportMapFragment) getFragmentManager()
							.findFragmentById(R.id.jobMap);
					if (f != null) {
						try {
							getFragmentManager().beginTransaction().remove(f).commit();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					//Runtime.getRuntime().gc();

					mapActivity.addFragment(new ClientRequestFragment(), false,
							AndyConstants.CLIENT_REQUEST_TAG, true);
					//walkCompleted();
				}

			}
			else {
				//mapActivity.clearAll();
				stopCheckingRequestStatus();
				preferenceHelper.clearRequestData();
				mapActivity.addFragment(new ClientRequestFragment(), false,
						AndyConstants.CLIENT_REQUEST_TAG, true);
			}
			break;
		case AndyConstants.ServiceCode.WALKER_STARTED:
			AppLog.Log(TAG, "walker started response " + response);
			if (parseContent.isSuccess(response)) {
				jobStatus = AndyConstants.IS_WALKER_ARRIVED;
				setjobStatus(jobStatus);
				String mbody = "job started";
				notifications(mbody, R.raw.job_started);

			}

			break;
		case AndyConstants.ServiceCode.WALKER_ARRIVED:
			AppLog.Log(TAG, "walker arrived response " + response);
			tvDestinationAddress.setVisibility(GONE);
			tvClientRating.setVisibility(View.VISIBLE);
			if (parseContent.isSuccess(response)) {
				jobStatus = AndyConstants.IS_WALK_STARTED;
				setjobStatus(jobStatus);
				String mbody = "arrived to pickup location";
				notifications(mbody, R.raw.job_arrived);
			}
			break;
		case AndyConstants.ServiceCode.WALK_STARTED:
			AppLog.Log(TAG, "walk started response " + response);
			if (parseContent.isSuccess(response)) {
				preferenceHelper.putIsTripStart(true);
				jobStatus = AndyConstants.IS_WALK_COMPLETED;
				setjobStatus(jobStatus);
				String mbody ="service started";
				notifications(mbody, R.raw.service_started);
				// getDistance();
				preferenceHelper.putRequestTime(Calendar.getInstance()
						.getTimeInMillis());
				if (markerClientLocation != null) {
					markerClientLocation.setTitle(mapActivity.getResources()
							.getString(R.string.job_start_location));
				}
				startElapsedTimer();
				if(!preferenceHelper.getDropoffAddress().contains("unknown")) {
					tvDestinationAddress.setText(preferenceHelper.getDropoffAddress());
					tvDestinationAddress.setVisibility(View.VISIBLE);
					tvClientRating.setVisibility(GONE);
				}
               if(preferenceHelper.getPaymentType() == AndyConstants.CREDIT) prePayment();
			}

			break;

		case AndyConstants.ServiceCode.WALK_PAYMENT:
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
				break;

		case AndyConstants.ServiceCode.WALK_COMPLETED:
			AppLog.Log(TAG, "walk completed response " + response);
			if (parseContent.isSuccess(response)|| iswarned) {
				if(iswarned) AndyUtils.removeCustomProgressDialog();
				String mbody ="service completed";
				notifications(mbody, R.raw.service_completed);
				stopCheckingRequestStatus();
				SupportMapFragment f = (SupportMapFragment) getFragmentManager()
						.findFragmentById(R.id.jobMap);
				if (f != null) {
					try {
						getFragmentManager().beginTransaction().remove(f).commit();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				//Runtime.getRuntime().gc();
				requestDetails = parseContent.parseRequestStatus(response);
				DecimalFormat df = new DecimalFormat("0.0");
				String dist1 = String.valueOf(df.format(dist))+ "";
				requestDetails.setDistance(dist1);

				FeedbackFrament feedbackFrament = new FeedbackFrament();
				Bundle bundle = new Bundle();
				bundle.putSerializable(AndyConstants.REQUEST_DETAIL,
						requestDetails);

				requestDetails.setTime(time);
				//requestDetails.setDistance(" " + preferenceHelper.getDistance());
				requestDetails.setUnit(preferenceHelper.getUnit());
				feedbackFrament.setArguments(bundle);
				if (this.isVisible())
					mapActivity.addFragment(feedbackFrament, false,
							AndyConstants.FEEDBACK_FRAGMENT_TAG, true);
			} else {
				//show progressbar payment failed
				AndyUtils.showToast( "Warning: No credit payment is made!", mapActivity);
				iswarned= true;

			}
			break;

		case AndyConstants.ServiceCode.GET_ROUTE:
			// if (parseContent.isSuccess(response)) {
			// jobStatus = AndyConstants.;
			// setjobStatus(jobStatus);
			// }
		case AndyConstants.ServiceCode.PATH_REQUEST:
			AppLog.Log(TAG, "Path request :" + response);
			if (parseContent.isSuccess(response)) {
				parseContent.parsePathRequest(response, points);
				initPreviousDrawPath();
			}
			break;
		case AndyConstants.ServiceCode.DRAW_PATH:
			if (!TextUtils.isEmpty(response)) {
				routeDest = new BeanRoute();
				parseContent.parseRoute(response, routeDest);

				final ArrayList<BeanStep> step = routeDest.getListStep();
				System.out.println("step size=====> " + step.size());
				pointsDest = new ArrayList<LatLng>();
				lineOptionsDest = null;
				lineOptionsDest = new PolylineOptions();

				for (int i = 0; i < step.size(); i++) {
					List<LatLng> path = step.get(i).getListPoints();
					System.out.println("step =====> " + i + " and "
							+ path.size());
					pointsDest.addAll(path);
				}
				if (polyLineDest != null)
					polyLineDest.remove();
				lineOptionsDest.addAll(pointsDest);
				lineOptionsDest.width(10);
				lineOptionsDest.geodesic(true);
				lineOptionsDest.color(getResources().getColor(
						R.color.color_blue)); // #00008B rgb(0,0,139)

				if (lineOptionsDest != null && googleMap != null) {
					polyLineDest = googleMap.addPolyline(lineOptionsDest);
					//boundLatLang();
					isPathDestination = true;

				}
			}
			break;

		case AndyConstants.ServiceCode.DRAW_PATH_CLIENT:
			AppLog.Log("JobFragment", "PATH Response : " + response);
			if (!TextUtils.isEmpty(response)) {
				routeClient = new BeanRoute();
				parseContent.parseRoute(response, routeClient);

				final ArrayList<BeanStep> step = routeClient.getListStep();
				pointsClient = new ArrayList<LatLng>();
				lineOptionsClient = null;
				lineOptionsClient = new PolylineOptions();

				for (int i = 0; i < step.size(); i++) {
					List<LatLng> path = step.get(i).getListPoints();
					pointsClient.addAll(path);
				}
				if (polyLineClient != null)
					polyLineClient.remove();
				lineOptionsClient.addAll(pointsClient);
				lineOptionsClient.width(17);
				lineOptionsClient.color(Color.BLUE);
				isPathClient = true;
				if (lineOptionsClient != null && googleMap != null) {
					polyLineClient = googleMap.addPolyline(lineOptionsClient);
				}
			}
            break;
			case AndyConstants.ServiceCode.REQUEST_LOCATION_UPDATE:
				try {
					JSONObject jsonObject = new JSONObject(response);
					if (jsonObject.getBoolean("success")) {
						Log.v("upload","update job location");
						//preferenceHelper.putDistance(Float.parseFloat(jsonObject
						//.getString(AndyConstants.Params.DISTANCE)));

						preferenceHelper.putUnit(jsonObject
						.getString(AndyConstants.Params.UNIT));
						preferenceHelper
								.putDropoffAddress(jsonObject
										.getString(AndyConstants.Params.DROPOFF_ADDRESS));
						preferenceHelper
						.putDestinationLatitude(jsonObject
								.getString(AndyConstants.Params.DESTINATION_LATITUDE));
							preferenceHelper
						.putDestinationLongitude(jsonObject
								.getString(AndyConstants.Params.DESTINATION_LONGITUDE));

					} else {
							preferenceHelper.putUnit(jsonObject
							.getString(AndyConstants.Params.UNIT));
						    preferenceHelper
								.putDropoffAddress(jsonObject
										.getString(AndyConstants.Params.DROPOFF_ADDRESS));
							preferenceHelper
							.putDestinationLatitude(jsonObject
								.getString(AndyConstants.Params.DESTINATION_LATITUDE));
							preferenceHelper
							.putDestinationLongitude(jsonObject
								.getString(AndyConstants.Params.DESTINATION_LONGITUDE));
					}
					if (!jsonObject.getBoolean("success"))
						if (jsonObject.getInt("is_cancelled") == 1) {
							stopCheckingRequestStatus();
							preferenceHelper.clearRequestData();
							SupportMapFragment f = (SupportMapFragment) getFragmentManager()
									.findFragmentById(R.id.jobMap);
							if (f != null) {
								try {
									getFragmentManager().beginTransaction().remove(f).commit();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							//Runtime.getRuntime().gc();
							mapActivity.addFragment(new ClientRequestFragment(), false,
									AndyConstants.CLIENT_REQUEST_TAG, true);
							//walkCompleted();
					}

				} catch (Exception exception) {
					exception.printStackTrace();
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
				};
				//walkStarted();
				break;

		    default:
			break;
		}
	}

	/**
	 * 
	 */
	private void startElapsedTimer() {
		stopElapsedTimer();
		elapsedTimer = new Timer();
		elapsedTimer.scheduleAtFixedRate(new TimerRequestStatus(),
				AndyConstants.DELAY, ELAPSED_TIME_SCHEDULE);
	}

	private void stopElapsedTimer() {
		if (elapsedTimer != null) {
			elapsedTimer.cancel();
			elapsedTimer = null;
		}
	}

	private class TimerRequestStatus extends TimerTask {
		@Override
		public void run() {
			// isContinueRequest = false;
			AppLog.Log(TAG, "In elapsed time timer");
			mapActivity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (isVisible()) {
						if (preferenceHelper.getRequestTime() == AndyConstants.NO_TIME) {
							preferenceHelper.putRequestTime(System
									.currentTimeMillis());
						}
						time = String.valueOf((System.currentTimeMillis() - startTime)
								/ (1000 * 60));
						tvJobTime.setText(time + "'");

					}
				}
			});

		}
	}

	@Override
	public void onLocationReceived(LatLng latlong) {
		if (googleMap == null) {
			return;
		}

		latitude = String.valueOf(latlong.latitude);
		longitude = String.valueOf(latlong.longitude);
		bearing = myLocation.getBearing();

		if (markerClientLocation == null) {
			markerClientLocation = googleMap.addMarker(new MarkerOptions()
					.position(
							new LatLng(Double.parseDouble(requestDetail
									.getClientLatitude()), Double
									.parseDouble(requestDetail
											.getClientLongitude()))).icon(
							BitmapDescriptorFactory
									.fromResource(R.drawable.pin_client)));

			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 15));

			if (jobStatus == AndyConstants.IS_WALK_COMPLETED) {
				markerClientLocation.setTitle(mapActivity.getResources()
						.getString(R.string.job_start_location));
			} else {
				markerClientLocation.setTitle(mapActivity.getResources()
						.getString(R.string.client_location));
			}

		}

		if (latlong != null) {
			if (googleMap != null) {

				if (markerDriverLocation == null) {

					markerDriverLocation = googleMap
							.addMarker(new MarkerOptions()
									.position(latlong)
									.icon(BitmapDescriptorFactory
											.fromResource(R.drawable.pin_driver))
									.title(getResources().getString(
											R.string.my_location)));

					googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 15));

					markerDriverLocation.setPosition(new LatLng(
							latlong.latitude, latlong.longitude));
					markerDriverLocation.setRotation(myLocation.getBearing());

					if (jobStatus == AndyConstants.IS_WALK_COMPLETED) {
						drawTrip(new LatLng(latlong.latitude, latlong.longitude));
					}

				} else {
					double DD = AndyUtils.distance(OldLoc.getLatitude(),
							OldLoc.getLongitude(),myLocation.getLatitude(),
							myLocation.getLongitude(),'K');
					if(DD > 0.03) {
						OldLoc = myLocation;

						if(DD < 0.3 ) {
							//if gps jumping, ignore it
							GPSJumping = false;
							markerDriverLocation.setPosition(latlong);
							markerDriverLocation.setAnchor(0.5f,0.5f);
							if(avgBearing == 0) avgBearing = bearing;
							markerDriverLocation.setRotation(myLocation.getBearing()- avgBearing);

							if (jobStatus == AndyConstants.IS_WALK_COMPLETED) {
								drawTrip(latlong);
							}
							//set marker on the bottom of map, rotate the map
							if(cameraFollow&&!GPSJumping) {
								Projection projection = googleMap.getProjection();
								Point point = projection.toScreenLocation(latlong);
								Point point2 = new Point(point.x+xMatrix,point.y-yMatrix);
								bottom = projection.fromScreenLocation(point2);
								if(bottom == null) bottom = latlong;
								avgBearing = (float)(0.75*avgBearing + 0.25*bearing);

								CameraPosition camPos = CameraPosition
										.builder()
										.tilt(45)
										.target(bottom)
										.bearing(avgBearing)
										.zoom(googleMap.getCameraPosition().zoom)
										.build();
								googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
							}

						} else {
							GPSJumping = true;
						}



					}

				}

			}
		}

	}



	private void drawTrip(LatLng latlng) {

		if (googleMap != null) {

			// setMarker(latlng);
			points.add(latlng);
			if(lineOptions == null) {
				lineOptions = new PolylineOptions();
				lineOptions.addAll(points);
				lineOptions.width(10);
				lineOptions.geodesic(true);
				lineOptions.color(mapActivity.getResources().getColor(R.color.skyblue));

				gpsTrack = googleMap.addPolyline(lineOptions);
			} else {
				gpsTrack.setPoints(points);
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();
		registerCancelReceiver();
		registerPaymentModeReceiver();
		registerDestinationReceiver();
		if (isAddMarker && preferenceHelper.isNavigate()) {
			if (jobStatus == AndyConstants.IS_WALKER_ARRIVED) {
				drawPathToClient(markerDriverLocation.getPosition(),
						markerClientLocation.getPosition());
			}

		}

	}

	@Override
	public void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	private void registerCancelReceiver() {
		IntentFilter intentFilter = new IntentFilter("CANCEL_REQUEST");
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				AppLog.Log("JobFragment", "CANCEL_REQUEST");
				stopElapsedTimer();
				mapActivity.startActivity(new Intent(mapActivity,
						MapActivity.class));
				mapActivity.finish();
			}
		};
		mapActivity.registerReceiver(mReceiver, intentFilter);
	}

	private void ubregisterCancelReceiver() {
		if (mReceiver != null) {
			mapActivity.unregisterReceiver(mReceiver);
		}
	}

	private void registerPaymentModeReceiver() {
		IntentFilter intentFilter = new IntentFilter("PAYMENT_MODE");
		modeReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				AppLog.Log("JobFragment", "PAYMENT_MODE");
				if (JobFragment.this.isVisible()) {
					setPaymentType();
				}
			}
		};
		mapActivity.registerReceiver(modeReceiver, intentFilter);
	}

	private void ubregisterPaymentModeReceiver() {
		if (modeReceiver != null) {
			mapActivity.unregisterReceiver(modeReceiver);
		}
	}

	private void unRegisterDestinationReceiver() {
		if (destReceiver != null) {
			mapActivity.unregisterReceiver(destReceiver);
		}
	}

	private void registerDestinationReceiver() {
		IntentFilter intentFilter = new IntentFilter("CLIENT_DESTINATION");
		destReceiver = new BroadcastReceiver() {
			private LatLng destLatLng;

			@Override
			public void onReceive(Context context, Intent intent) {
				AppLog.Log("JobFragment", "CLIENT_DESTINATION");
				//destLatLng = preferenceHelper.getClientDestination();
				//drawPath(markerClientLocation.getPosition(), destLatLng);
			}
		};
		mapActivity.registerReceiver(destReceiver, intentFilter);
	}

	private void setPaymentType() {
		tvPaymentType.setVisibility(View.VISIBLE);
		if (preferenceHelper.getPaymentType() == AndyConstants.CASH)
			tvPaymentType.setText(getString(R.string.text_type_cash));
		else
			tvPaymentType.setText(getString(R.string.text_type_card));
	}

	private void drawPath(LatLng source, LatLng destination) {
		if (source == null || destination == null) {
			return;
		}
		if (destination.latitude != 0) {

			setDestinationMarker(destination);
			//boundLatLang();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(AndyConstants.URL,
					"https://maps.googleapis.com/maps/api/directions/json?origin="
							+ source.latitude + "," + source.longitude
							+ "&destination=" + destination.latitude + ","
							+ destination.longitude + "&sensor=false"
							+ "&key=" + AndyConstants.PLACES_AUTOCOMPLETE_API_KEY);
			// new HttpRequester(mapActivity, map,
			// AndyConstants.ServiceCode.DRAW_PATH, true, this);

			requestQueue.add(new VolleyHttpRequest(Method.GET, map,
					AndyConstants.ServiceCode.DRAW_PATH, this, this));
		}
	}

	private void setDestinationMarker(LatLng latLng) {
		if (latLng != null) {
			if (googleMap != null && this.isVisible()) {
				if (markerDestination == null) {
					MarkerOptions opt = new MarkerOptions();
					opt.position(latLng);
					opt.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.pin_client_destination));
					opt.title(getResources().getString(R.string.text_destination));
					markerDestination = googleMap.addMarker(opt);
				} else {
					markerDestination.setPosition(latLng);
				}
			}
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		// TODO Auto-generated method stub
		AppLog.Log("TAG", error.getMessage());
	}


	@Override
	public void onLocationReceived(Location location) {
		// TODO Auto-generated method stub
		if (location != null) {
			this.location = location;
			myLocation = location;
			if(origin) {
				myOldLocation = myLocation;
				OldLoc = myLocation;
				origin = false;
			}
		}
		if (jobStatus == AndyConstants.IS_WALK_COMPLETED) UpdateDistace();
		if(!isPathDestination&&jobStatus == AndyConstants.IS_WALK_COMPLETED) {
			if (markerClientLocation != null && markerDriverLocation != null) {
				if(!preferenceHelper.getDropoffAddress().contains("unknown")) {
					LatLng destLatlng = getLocationFromAddress(preferenceHelper.getDropoffAddress());
					drawPath(markerClientLocation.getPosition(), destLatlng);
				}
			}

		}
		if(!isPathClient&&jobStatus != AndyConstants.IS_WALK_COMPLETED) {
			if (markerClientLocation != null && markerDriverLocation != null) {

				drawPathToClient(markerDriverLocation.getPosition(),
						markerClientLocation.getPosition());
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.overthere.express.locationupdate.LocationHelper.OnLocationReceived
	 * #onConntected(android.os.Bundle)
	 */
	@Override
	public void onConntected(Bundle bundle) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onConntected(Location location) {
		// TODO Auto-generated method stub
		this.location = location;
		LatLng latLng = new LatLng(location.getLatitude(),
				location.getLongitude());
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
				latLng,15);
		if(googleMap!=null) googleMap.animateCamera(cameraUpdate);

	}

	private void boundLatLang() {

		try {
			if (markerDriverLocation != null && markerClientLocation != null
					&& markerDestination != null) {
				LatLngBounds.Builder bld = new LatLngBounds.Builder();
				bld.include(new LatLng(
						markerDriverLocation.getPosition().latitude,
						markerDriverLocation.getPosition().longitude));
				bld.include(new LatLng(
						markerClientLocation.getPosition().latitude,
						markerClientLocation.getPosition().longitude));
				bld.include(new LatLng(
						markerDestination.getPosition().latitude,
						markerDestination.getPosition().longitude));
				LatLngBounds latLngBounds = bld.build();

				googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
						latLngBounds, 50));
			} else if (markerDriverLocation != null
					&& markerClientLocation != null) {
				LatLngBounds.Builder bld = new LatLngBounds.Builder();
				bld.include(new LatLng(
						markerDriverLocation.getPosition().latitude,
						markerDriverLocation.getPosition().longitude));
				bld.include(new LatLng(
						markerClientLocation.getPosition().latitude,
						markerClientLocation.getPosition().longitude));
				LatLngBounds latLngBounds = bld.build();

				googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
						latLngBounds, 100));
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	private class TimerCheckRequestStatus extends TimerTask {
		@Override
		public void run() {
			checkRequestStatus();
		}
	}

	private void startCheckingRequestStatus() {
		stopCheckingRequestStatus();
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerCheckRequestStatus(),
				AndyConstants.DELAY, AndyConstants.TIME_SCHEDULE);
	}

	private void stopCheckingRequestStatus() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	public void checkRequestStatus() {
		if (!AndyUtils.isNetworkAvailable(mapActivity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet),
					mapActivity);
			return;
		}
        //System.gc();

		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(mapActivity).getString("local_host");
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

	private void UploadTripLocationData() {
		if (!AndyUtils.isNetworkAvailable(mapActivity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet),
					mapActivity);
			return;
		}
		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(mapActivity).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL, Lserver + AndyConstants.ServiceType.REQUEST_LOCATION_UPDATE);
		map.put(AndyConstants.Params.ID, preferenceHelper.getUserId());
		map.put(AndyConstants.Params.TOKEN, preferenceHelper.getSessionToken());
		map.put(AndyConstants.Params.LATITUDE, latitude);
		map.put(AndyConstants.Params.LONGITUDE, longitude);
		map.put(AndyConstants.Params.BEARING, bearing + "");
		map.put(AndyConstants.Params.REQUEST_ID, preferenceHelper
				.getRequestId() + "");
		requestQueue.add(new VolleyHttpRequest(Method.POST, map,
				AndyConstants.ServiceCode.REQUEST_LOCATION_UPDATE, this, this));

	}

	private Runnable Uprunnable = new Runnable() {
		@Override
		public void run() {
			upHandler.postDelayed(Uprunnable, 10000);
			if(!GPSJumping) {
				UploadTripLocationData();
				/* and here comes the "trick" */

				//if (jobStatus == AndyConstants.IS_WALK_COMPLETED) UpdateDistace();
			}
		};
	};

	private void UpdateDistace() {
     if(myOldLocation == null) return;
		 double DD = AndyUtils.distance(myOldLocation.getLatitude(),
				myOldLocation.getLongitude(),myLocation.getLatitude(),
				myLocation.getLongitude(),'K');
		if(DD > 0.1) {
		    if(DD > 1.0) {
		        //job resume or jump, myoldlocation is lost;
                myOldLocation = myLocation;
            } else {
                dist = dist + (float) DD;
                preferenceHelper.putDistance(dist);
                myOldLocation = myLocation;
                DecimalFormat df = new DecimalFormat("0.0");
                tvJobDistance.setText(df.format(dist)
                        + " " + "km");
            }
		}

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
	private void clearMemory() {
		//googleMap=null;
		lineOptions=null;
		route=null;
		points=null;
		tvJobTime=null;
		tvJobDistance=null;
		tvJobReward=null;
		tvJobStatus=null;
		tvClientName=null;
		tvDestinationAddress=null;
		ivClientProfilePicture=null;
		tvClientRating=null;
		parseContent=null;
		locationHelper=null;
		aQuery=null;
		requestDetail=null;
		markerDriverLocation=null;
		markerClientLocation=null;
		timer=null;
		elapsedTimer=null;
		dbHelper=null;
		decimalFormat=null;
		mReceiver=null;
		tvPaymentType=null;
		modeReceiver=null;
		jobFragmentView=null;
		mMapView=null;
		mBundle=null;
		destReceiver=null;
		routeDest=null;
		pointsDest=null;
		lineOptionsDest=null;
		polyLineDest=null;
		markerDestination=null;
		//requestQueue=null;
		btnNavigate=null;
		btnMyLocation=null;
		routeClient=null;
		pointsClient=null;
		lineOptionsClient=null;
		polyLineClient=null;
		//upHandler = null;
		Runtime.getRuntime().gc();
	}

	private void prePayment() {
		if (!AndyUtils.isNetworkAvailable(mapActivity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet),
					mapActivity);
			return;
		}
        AndyUtils.showToast(
                "Payment processing ...",
                mapActivity);
		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(mapActivity).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL, Lserver + AndyConstants.ServiceType.PRE_PAYMENT);
		map.put(AndyConstants.Params.ID, preferenceHelper.getUserId());
		map.put(AndyConstants.Params.REQUEST_ID,
				String.valueOf(preferenceHelper.getRequestId()));
		map.put(AndyConstants.Params.TOKEN, preferenceHelper.getSessionToken());
		map.put(AndyConstants.Params.REWARD, preferenceHelper.getString(AndyConstants.Params.REWARD));
		map.put(AndyConstants.Params.TIME, requestDetail.getTime());
         new HttpRequester(mapActivity, map, AndyConstants.ServiceCode.PRE_PAYMENT,
         this);

        //requestQueue.add(new VolleyHttpRequest(Method.POST, map,
		//		AndyConstants.ServiceCode.PRE_PAYMENT, this, this));

	}

}