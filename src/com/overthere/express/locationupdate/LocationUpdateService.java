package com.overthere.express.locationupdate;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import com.overthere.express.MapActivity;
import com.overthere.express.MyReceiver;
import com.overthere.express.R;
import com.overthere.express.SettingActivity;
import com.overthere.express.locationupdate.LocationHelper.OnLocationReceived;
import com.overthere.express.utills.AndyConstants;
import com.overthere.express.utills.AndyUtils;
import com.overthere.express.utills.AppLog;
import com.overthere.express.utills.PreferenceHelper;
import com.google.android.gms.maps.model.LatLng;

public class LocationUpdateService extends IntentService implements
		OnLocationReceived {
	private PreferenceHelper preferenceHelper;
	private LocationHelper locationHelper;
	private String id, token, latitude, longitude;
	private static Timer timer;
	private LatLng latlngPrevious, latlngCurrent;
	private static boolean isNoRequest;
	private float bearing;

	public LocationUpdateService() {
		this("MySendLocationService");
	}

	public LocationUpdateService(String name) {
		super("MySendLocationService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		locationHelper = new LocationHelper(this);
		locationHelper.setLocationReceivedLister(this);
		locationHelper.onStart();
		preferenceHelper = new PreferenceHelper(getApplicationContext());
		id = preferenceHelper.getUserId();
		token = preferenceHelper.getSessionToken();

		return START_STICKY;
	}

	@Override
	public void onCreate() {
		super.onCreate();

	}



	@Override
	public void onDestroy() {
		if (locationHelper != null) {
			locationHelper.onStop();
		}
		super.onDestroy();
	}

	@Override
	public void onLocationReceived(LatLng latlong) {

	}

	@Override
	public void onLocationReceived(Location location) {
		// TODO Auto-generated method stub
		LatLng latlong = locationHelper.getLatLng(location);
		AppLog.Log("TAG", "onLocationReceived Lat : " + latlong.latitude
				+ " , long : " + latlong.longitude);
		if (latlong != null) {
			preferenceHelper
					.putWalkerLatitude(String.valueOf(latlong.latitude));
			preferenceHelper.putWalkerLongitude(String
					.valueOf(latlong.longitude));
			latlngCurrent = new LatLng(latlong.latitude, latlong.longitude);
			if (latlngPrevious == null) {
				latlngPrevious = new LatLng(latlong.latitude, latlong.longitude);
			}
		}
		if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(token)
				&& latlong != null) {

			latitude = String.valueOf(latlong.latitude);
			longitude = String.valueOf(latlong.longitude);
			bearing = location.getBearing();
			if (!AndyUtils.isNetworkAvailable(getApplicationContext())) {
				// AndyUtils.showToast(
				// getResources().getString(R.string.toast_no_internet),
				// getApplicationContext());
				return;
			}

			if (preferenceHelper.getRequestId() == AndyConstants.NO_REQUEST) {

				isNoRequest = true;
				//new UploadDataToServer().execute();
			} else {
				isNoRequest = false;
			//	new UploadTripLocationData().execute();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.overthere.express.locationupdate.LocationHelper.OnLocationReceived
	 * #onConntected(android.location.Location)
	 */
	@Override
	public void onConntected(Location location) {
		// TODO Auto-generated method stub

	}
}
