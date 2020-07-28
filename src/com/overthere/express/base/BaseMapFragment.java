package com.overthere.express.base;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.view.View.OnClickListener;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.Volley;
import com.overthere.express.MapActivity;
import com.overthere.express.R;
import com.overthere.express.parse.AsyncTaskCompleteListener;
import com.overthere.express.parse.ParseContent;
import com.overthere.express.parse.VolleyHttpRequest;
import com.overthere.express.utills.AndyConstants;
import com.overthere.express.utills.AndyUtils;
import com.overthere.express.utills.PreferenceHelper;

import androidx.fragment.app.Fragment;

/**
 * @author Kishan H Dhamat
 * 
 */
public abstract class BaseMapFragment extends Fragment implements
		OnClickListener, AsyncTaskCompleteListener, ErrorListener {
	protected MapActivity mapActivity;
	protected PreferenceHelper preferenceHelper;
	protected ParseContent parseContent;
	private RequestQueue requestQueue;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mapActivity = (MapActivity) getActivity();
		requestQueue = Volley.newRequestQueue(mapActivity);
		preferenceHelper = new PreferenceHelper(mapActivity);
		parseContent = new ParseContent(mapActivity);
	}

	public void startActivityForResult(Intent intent, int requestCode,
			String fragmentTag) {
		mapActivity.startActivityForResult(intent, requestCode, fragmentTag);
	}

	@Override
	@Deprecated
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
	}

	private void login() {
		if (!AndyUtils.isNetworkAvailable(mapActivity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet),
					mapActivity);
			return;
		}
		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(getActivity()).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL, Lserver + AndyConstants.ServiceType.LOGIN);
		map.put(AndyConstants.Params.EMAIL, preferenceHelper.getEmail());
		map.put(AndyConstants.Params.PASSWORD, preferenceHelper.getPassword());
		map.put(AndyConstants.Params.DEVICE_TYPE,
				AndyConstants.DEVICE_TYPE_ANDROID);
		map.put(AndyConstants.Params.DEVICE_TOKEN,
				preferenceHelper.getDeviceToken());
		map.put(AndyConstants.Params.LOGIN_BY, AndyConstants.MANUAL);
		// new HttpRequester(mapActivity, map, AndyConstants.ServiceCode.LOGIN,
		// this);
		requestQueue.add(new VolleyHttpRequest(Method.POST, map,
				AndyConstants.ServiceCode.LOGIN, this, this));

	}

	private void loginSocial(String id, String loginType) {
		if (!AndyUtils.isNetworkAvailable(mapActivity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet),
					mapActivity);
			return;
		}

		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(getActivity()).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL, Lserver + AndyConstants.ServiceType.LOGIN);
		map.put(AndyConstants.Params.SOCIAL_UNIQUE_ID, id);
		map.put(AndyConstants.Params.DEVICE_TYPE,
				AndyConstants.DEVICE_TYPE_ANDROID);
		map.put(AndyConstants.Params.DEVICE_TOKEN,
				preferenceHelper.getDeviceToken());
		map.put(AndyConstants.Params.LOGIN_BY, loginType);
		// new HttpRequester(mapActivity, map, AndyConstants.ServiceCode.LOGIN,
		// this);

		requestQueue.add(new VolleyHttpRequest(Method.POST, map,
				AndyConstants.ServiceCode.LOGIN, this, this));
	}
}