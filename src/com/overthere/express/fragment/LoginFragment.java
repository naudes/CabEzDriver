package com.overthere.express.fragment;

import java.util.HashMap;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.overthere.express.MapActivity;
import com.overthere.express.R;
import com.overthere.express.base.BaseRegisterFragment;
import com.overthere.express.parse.AsyncTaskCompleteListener;
import com.overthere.express.parse.ParseContent;
import com.overthere.express.parse.VolleyHttpRequest;
import com.overthere.express.utills.AndyConstants;
import com.overthere.express.utills.AndyUtils;
import com.overthere.express.utills.AppLog;
import com.overthere.express.utills.PreferenceHelper;
import com.overthere.express.widget.MyFontEdittextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;

import org.json.JSONObject;

import static android.content.Context.NOTIFICATION_SERVICE;


/**
 * @author Kishan H Dhamat
 * 
 */
public class LoginFragment extends BaseRegisterFragment implements
		OnClickListener, ConnectionCallbacks, OnConnectionFailedListener,
		AsyncTaskCompleteListener {
	private MyFontEdittextView etLoginEmail, etLoginPassword;
	private ImageButton btnActionMenu;
	private ConnectionResult mConnectionResult;
	private GoogleApiClient mGoogleApiClient;
	private ParseContent parseContent;
	private boolean mSignInClicked, mIntentInProgress;
	private final String TAG = "LoginFragment";
	private static final int RC_SIGN_IN = 0;
	private RequestQueue requestQueue;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View loginFragmentView = inflater.inflate(R.layout.fragment_login,
				container, false);
		requestQueue = Volley.newRequestQueue(registerActivity);
		etLoginEmail = (MyFontEdittextView) loginFragmentView
				.findViewById(R.id.etLoginEmail);
		etLoginPassword = (MyFontEdittextView) loginFragmentView
				.findViewById(R.id.etLoginPassword);

		btnActionMenu = (ImageButton) loginFragmentView
				.findViewById(R.id.btnActionMenu);

		loginFragmentView.findViewById(R.id.tvLoginForgetPassword)
				.setOnClickListener(this);
		loginFragmentView.findViewById(R.id.tvLoginSignin).setOnClickListener(
				this);

		return loginFragmentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerActivity.actionBar.show();
		registerActivity.setActionBarTitle(getResources().getString(
				R.string.text_signin));
		registerActivity.btnActionInfo.setVisibility(View.INVISIBLE);
		registerActivity.setActionBarIcon(R.drawable.taxi);
		parseContent = new ParseContent(registerActivity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.tvLoginForgetPassword:
			registerActivity.addFragment(new ForgetPasswordFragment(), true,
					AndyConstants.FOREGETPASS_FRAGMENT_TAG, true);
			break;

		case R.id.tvLoginSignin:
			if (etLoginEmail.getText().length() == 0) {
				AndyUtils.showToast(
						getResources().getString(R.string.error_empty_email),
						registerActivity);
				return;
			} else if (!AndyUtils.eMailValidation(etLoginEmail.getText()
					.toString())) {
				AndyUtils.showToast(
						getResources().getString(R.string.error_valid_email),
						registerActivity);
				return;
			} else if (etLoginPassword.getText().length() == 0) {
				AndyUtils
						.showToast(
								getResources().getString(
										R.string.error_empty_password),
								registerActivity);
				return;
			} else {
				login();
			}

			break;

		default:
			break;
		}
	}

	private void login() {

		//check preference, if local server is empty,
		// login to master aerver, then login to local server
		new PreferenceHelper(getActivity()).putString("local_host",AndyConstants.ServiceType.LOCAL_URL);
		String Lserver = new PreferenceHelper(getActivity()).getString("local_host");
	    //Login2Master();
	     //if (Lserver.isEmpty()==true) {Login2Master();} else {Login2Local();}
		Login2Local();
        /*

			if (!AndyUtils.isNetworkAvailable(registerActivity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet),
					registerActivity);
			return;
		}

		AndyUtils.showCustomProgressDialog(registerActivity, "", getResources()
				.getString(R.string.progress_dialog_sign_in), false);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(AndyConstants.URL, AndyConstants.ServiceType.LOGIN);
		map.put(AndyConstants.Params.EMAIL, etLoginEmail.getText().toString());
		map.put(AndyConstants.Params.PASSWORD, etLoginPassword.getText()
				.toString());
		map.put(AndyConstants.Params.DEVICE_TYPE,
				AndyConstants.DEVICE_TYPE_ANDROID);
		map.put(AndyConstants.Params.DEVICE_TOKEN, new PreferenceHelper(
					registerActivity).getDeviceToken());
		map.put(AndyConstants.Params.LOGIN_BY, AndyConstants.MANUAL);
		// new HttpRequester(registerActivity, map,
		// AndyConstants.ServiceCode.LOGIN, this);
		requestQueue.add(new VolleyHttpRequest(Method.POST, map,
				AndyConstants.ServiceCode.LOGIN, this, this));
//*/
	}

	private void loginSocial(String id, String loginType) {
		if (!AndyUtils.isNetworkAvailable(registerActivity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet),
					registerActivity);
			return;
		}

		AndyUtils.showCustomProgressDialog(registerActivity, "", getResources()
				.getString(R.string.progress_dialog_sign_in), false);
		String Lserver = new PreferenceHelper(getActivity()).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(AndyConstants.URL, Lserver + AndyConstants.ServiceType.LOGIN);
		map.put(AndyConstants.Params.SOCIAL_UNIQUE_ID, id);
		map.put(AndyConstants.Params.DEVICE_TYPE,
				AndyConstants.DEVICE_TYPE_ANDROID);
		map.put(AndyConstants.Params.DEVICE_TOKEN, new PreferenceHelper(
				registerActivity).getDeviceToken());
		map.put(AndyConstants.Params.LOGIN_BY, loginType);
		// new HttpRequester(registerActivity, map,
		// AndyConstants.ServiceCode.LOGIN, this);

		requestQueue.add(new VolleyHttpRequest(Method.POST, map,
				AndyConstants.ServiceCode.LOGIN, this, this));
	}



	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				registerActivity.startIntentSenderForResult(mConnectionResult
						.getResolution().getIntentSender(), RC_SIGN_IN, null,
						0, 0, 0, AndyConstants.LOGIN_FRAGMENT_TAG);
			} catch (SendIntentException e) {
				// The intent was canceled before it was sent. Return to the
				// default
				// state and attempt to connect to get an updated
				// ConnectionResult.
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!mIntentInProgress) {
			// Store the ConnectionResult so that we can use it later when the
			// user clicks
			// 'sign-in'.
			mConnectionResult = result;

			if (mSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to
				// resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RC_SIGN_IN) {

			if (resultCode != registerActivity.RESULT_OK) {
				mSignInClicked = false;
			}

			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		// String email = Plus.AccountApi.getAccountName(mGoogleApiClient);


		// String personName = currentPerson.getDisplayName();
		// String personPhoto = currentPerson.getImage().toString();
		// String personGooglePlusProfile = currentPerson.getUrl();
		// Toast.makeText(
		// registerActivity,
		// "email: " + email + "\nName:" + personName + "\n Profile URL:"
		// + personGooglePlusProfile + "\nPhoto:" + personPhoto
		// + "\nBirthday:" + currentPerson.getBirthday()
		// + "\n GENDER: " + currentPerson.getGender(),
		// Toast.LENGTH_LONG).show();

		AndyUtils.removeCustomProgressDialog();


	}

	@Override
	public void onConnectionSuspended(int arg0) {

	}

	@Override
	public void onStop() {
		super.onStop();

	}

	@Override
	public void onResume() {
		super.onResume();
		registerActivity.currentFragment = AndyConstants.LOGIN_FRAGMENT_TAG;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		AndyUtils.removeCustomProgressDialog();
	}

	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		// TODO Auto-generated method stub
		AndyUtils.removeCustomProgressDialog();
		String[] part = response.split("\\{",2);
		response = "{" + part[1];

		AppLog.Log(TAG, response);
		response = response.replace("e{","{");
		switch (serviceCode) {
		case AndyConstants.ServiceCode.LOGIN:
			if (!parseContent.isSuccess(response)) {

				return;
			}
			if (parseContent.isSuccessWithId(response)) {
				//String mbody="Login successed";
				//notifications(mbody,R.raw.welcome_to_overthere);

				parseContent.parseUserAndStoreToDb(response);
				new PreferenceHelper(getActivity()).putPassword(etLoginPassword
						.getText().toString());
				startActivity(new Intent(registerActivity, MapActivity.class));
				registerActivity.finish();
			}
			break;
		case AndyConstants.ServiceCode.LOGIN_MASTER:
			if (!parseContent.isSuccess(response)) {
				AndyUtils.showToast("Not a registered user.",
						registerActivity);

				return;
			}
			else {
				try {
					JSONObject jsonObject = new JSONObject(response);
					String localServer = null;
						if (jsonObject.getString("local_host")!=null) {
							localServer = jsonObject.getString("local_host");
							new PreferenceHelper(getActivity()).putString(AndyConstants.Params.LOCAL_HOST, localServer);
						}

						Login2Local();

				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
            break;
		default:
			break;
		}

	}

	@Override
	public void onErrorResponse(VolleyError error) {
		// TODO Auto-generated method stub

	}

	public void notifications(CharSequence mbody, int voice){
		Uri sound = Uri.parse("android.resource://" + registerActivity.getPackageName() + "/" + voice);
		Notification noti = new Notification.Builder(registerActivity)
				.setContentTitle(getResources().getString(R.string.app_name))
				.setContentText(mbody)
				.setSmallIcon(R.drawable.ic_launcher).setAutoCancel(true)
				.setSound(sound)
				.build();
		NotificationManager notificationManager = (NotificationManager) registerActivity.getSystemService(NOTIFICATION_SERVICE);
		noti.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(1000, noti);
	}

	private void Login2Master() {
		if (!AndyUtils.isNetworkAvailable(registerActivity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet),
					registerActivity);
			return;
		}

		AndyUtils.showCustomProgressDialog(registerActivity, "", getResources()
				.getString(R.string.progress_dialog_sign_in), false);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(AndyConstants.URL, AndyConstants.ServiceType.LOGIN_TO_MASTER);
		map.put(AndyConstants.Params.EMAIL, etLoginEmail.getText().toString());
		map.put(AndyConstants.Params.PASSWORD, etLoginPassword.getText()
				.toString());
		map.put(AndyConstants.Params.DEVICE_TYPE,
				AndyConstants.DEVICE_TYPE_ANDROID);
		map.put(AndyConstants.Params.DEVICE_TOKEN, new PreferenceHelper(
				registerActivity).getDeviceToken());
		map.put(AndyConstants.Params.LOGIN_BY, AndyConstants.MANUAL);
		// new HttpRequester(registerActivity, map,
		// AndyConstants.ServiceCode.LOGIN, this);
		requestQueue.add(new VolleyHttpRequest(Method.POST, map,
				AndyConstants.ServiceCode.LOGIN_MASTER, this, this));

	}

	private void Login2Local() {
		if (!AndyUtils.isNetworkAvailable(registerActivity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet),
					registerActivity);
			return;
		}

		AndyUtils.showCustomProgressDialog(registerActivity, "", getResources()
				.getString(R.string.progress_dialog_sign_in), false);
		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(getActivity()).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL, Lserver + AndyConstants.ServiceType.LOGIN);
		map.put(AndyConstants.Params.EMAIL, etLoginEmail.getText().toString());
		map.put(AndyConstants.Params.PASSWORD, etLoginPassword.getText()
				.toString());
		map.put(AndyConstants.Params.DEVICE_TYPE,
				AndyConstants.DEVICE_TYPE_ANDROID);
		map.put(AndyConstants.Params.DEVICE_TOKEN, new PreferenceHelper(
				registerActivity).getDeviceToken());
		map.put(AndyConstants.Params.LOGIN_BY, AndyConstants.MANUAL);
		// new HttpRequester(registerActivity, map,
		// AndyConstants.ServiceCode.LOGIN, this);
		requestQueue.add(new VolleyHttpRequest(Method.POST, map,
				AndyConstants.ServiceCode.LOGIN, this, this));

	}



}
