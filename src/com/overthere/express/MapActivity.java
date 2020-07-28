package com.overthere.express;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
//import android.provider.SyncStateContract;
//import android.support.v4.app.ActionBarDrawerToggle;
//import android.support.v4.widget.DrawerLayout;
//import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.overthere.express.adapter.DrawerAdapter;
import com.overthere.express.base.ActionBarBaseActivitiy;
import com.overthere.express.db.DBHelper;
import com.overthere.express.fragment.ClientRequestFragment;
import com.overthere.express.fragment.FeedbackFrament;
import com.overthere.express.fragment.JobFragment;
//import com.overthere.express.fragment.RegisterFragment;
import com.overthere.express.model.ApplicationPages;
import com.overthere.express.model.RequestDetail;
import com.overthere.express.model.User;
import com.overthere.express.parse.AsyncTaskCompleteListener;
import com.overthere.express.parse.ParseContent;
import com.overthere.express.parse.VolleyHttpRequest;
import com.overthere.express.utills.AndyConstants;
import com.overthere.express.utills.AndyUtils;
import com.overthere.express.utills.AppLog;
import com.overthere.express.utills.PreferenceHelper;
import com.overthere.express.widget.MyFontTextView;
import com.overthere.express.widget.MyFontTextViewDrawer;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.legacy.app.ActionBarDrawerToggle;
/**
 * @author Jack Zeng
 * 
 */

	public class MapActivity extends ActionBarBaseActivitiy implements
			OnItemClickListener, AsyncTaskCompleteListener {
	// Drawer Initialization
	private DrawerLayout drawerLayout;
	private DrawerAdapter adapter;
	private ListView drawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	private MyFontTextView tvLogoutOk, tvLogoutCancel, tvExitOk, tvExitCancel,
			tvApprovedClose;

	private PreferenceHelper preferenceHelper;
	private ParseContent parseContent;
	private static final String TAG = "MapActivity";
	private ArrayList<ApplicationPages> arrayListApplicationPages;
	private boolean isDataRecieved = false, isRecieverRegistered = false,
			isNetDialogShowing = false, isGpsDialogShowing = false;
	private AlertDialog internetDialog, gpsAlertDialog;
	private LocationManager manager;
	// private MenuDrawer mMenuDrawer;
	private DBHelper dbHelper;
	private AQuery aQuery;
	private ImageOptions imageOptions;
	private ImageView ivMenuProfile;
	private MyFontTextViewDrawer tvMenuName;
	private boolean isLogoutCheck = true, isApprovedCheck = true;
	private BroadcastReceiver mReceiver;
	private Dialog mDialog;
	private View headerView;
	private RequestQueue requestQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		preferenceHelper = new PreferenceHelper(this);
		requestQueue = Volley.newRequestQueue(this);
		arrayListApplicationPages = new ArrayList<ApplicationPages>();
		parseContent = new ParseContent(this);
		mTitle = mDrawerTitle = getTitle();

		btnActionMenu.setVisibility(View.VISIBLE);
		btnActionMenu.setOnClickListener(this);
		tvTitle.setOnClickListener(this);
		btnNotification.setVisibility(View.GONE);
		setActionBarIcon(R.drawable.menu);

		moveDrawerToTop();
		initActionBar();
		initDrawer();

		manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		aQuery = new AQuery(this);
		imageOptions = new ImageOptions();
		imageOptions.memCache = true;
		imageOptions.fileCache = true;
		imageOptions.targetWidth = 200;
		imageOptions.fallback = R.drawable.user;

		dbHelper = new DBHelper(getApplicationContext());

		// if (savedInstanceState == null) {
		// selectItem(-1);
		// }
		if (preferenceHelper.getIsApproved() != null
				&& preferenceHelper.getIsApproved().equals("1")) {
			if (mDialog != null && mDialog.isShowing()) {
				mDialog.dismiss();
			}
		}
	}

	public void initDrawer() {
		//menu icon is set in drawer adapter
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerList = (ListView) findViewById(R.id.left_drawer);
		drawerLayout.addDrawerListener(createDrawerToggle());
		drawerList.setOnItemClickListener(this);
		adapter = new DrawerAdapter(this, arrayListApplicationPages);
		headerView = getLayoutInflater().inflate(R.layout.menu_drawer, null);
		drawerList.addHeaderView(headerView);
		drawerList.setAdapter(adapter);
		ivMenuProfile = (ImageView) headerView.findViewById(R.id.ivMenuProfile);
		tvMenuName = (MyFontTextViewDrawer) headerView
				.findViewById(R.id.tvMenuName);

	}

	private void initActionBar() {
		actionBar = getSupportActionBar();
		// actionBar.setDisplayHomeAsUpEnabled(true);
		// actionBar.setHomeButtonEnabled(true);
	}

	private DrawerLayout.DrawerListener createDrawerToggle() {
		mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.menu, R.string.drawer_open, R.string.drawer_close) {

			@Override
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
			}

			@Override
			public void onDrawerStateChanged(int state) {
			}
		};
		return mDrawerToggle;
	}

	private void moveDrawerToTop() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		DrawerLayout drawer = (DrawerLayout) inflater.inflate(
				R.layout.activity_map, null); // "null" is important.

		// HACK: "steal" the first child of decor view
		ViewGroup decor = (ViewGroup) getWindow().getDecorView();
		View child = decor.getChildAt(0);
		decor.removeView(child);
		LinearLayout container = (LinearLayout) drawer
				.findViewById(R.id.llContent); // This is the container we
												// defined just now.
		container.addView(child, 0);
		drawer.findViewById(R.id.left_drawer).setPadding(0,
				(actionBar.getHeight() + getStatusBarHeight()), 0, 0);

		// Make the drawer replace the first child
		decor.addView(drawer);
	}

	public int getStatusBarHeight() {
		int result = 0;
		int resourceId = getResources().getIdentifier("status_bar_height",
				"dimen", "android");
		if (resourceId > 0) {
			result = getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// MenuInflater inflater = getMenuInflater();
		// inflater.inflate(R.menu.main, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// if (mDrawerToggle.onOptionsItemSelected(item)) {
		// return true;
		// }
		return super.onOptionsItemSelected(item);
	}

	public void checkStatus() {
		if (preferenceHelper.getRequestId() == AndyConstants.NO_REQUEST) {
			AppLog.Log(TAG, "onResume getreuest in progress");
			getRequestsInProgress();
		} else {
			AppLog.Log(TAG, "onResume check request status");
			checkRequestStatus();
		}
	}

	private void getMenuItems() {
		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(this).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL, Lserver + AndyConstants.ServiceType.APPLICATION_PAGES);
		// new HttpRequester(this, map,
		// AndyConstants.ServiceCode.APPLICATION_PAGES, true, this);

		requestQueue.add(new VolleyHttpRequest(Method.GET, map,
				AndyConstants.ServiceCode.APPLICATION_PAGES, this, this));
	}

	public BroadcastReceiver GpsChangeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			AppLog.Log(TAG, "On recieve GPS provider broadcast");
			final LocationManager manager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				// do something
				removeGpsDialog();
			} else {
				// do something else
				if (isGpsDialogShowing) {
					return;
				}
				ShowGpsDialog();
			}

		}
	};
	public BroadcastReceiver internetConnectionReciever = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo activeWIFIInfo = connectivityManager
					.getNetworkInfo(connectivityManager.TYPE_WIFI);

			if (activeWIFIInfo.isConnected() || activeNetInfo.isConnected()) {
				removeInternetDialog();
			} else {
				if (isNetDialogShowing) {
					return;
				}
				showInternetDialog();
			}
		}
	};

	private void registerIsApproved() {
		IntentFilter intentFilter = new IntentFilter("IS_APPROVED");
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				AppLog.Log("MapFragment", "IS_APPROVED");
				if (preferenceHelper.getIsApproved() != null
						&& preferenceHelper.getIsApproved().equals("1")) {
					// startActivity(new Intent(MapActivity.this,
					// MapActivity.class));
					// mDialog.dismiss();
					if (mDialog != null && mDialog.isShowing()) {
						mDialog.dismiss();
						getRequestsInProgress();
					}
				}

			}
		};
		registerReceiver(mReceiver, intentFilter);
	}

	private void unregisterIsApproved() {
		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
		}
	}

	private void ShowGpsDialog() {
		AndyUtils.removeCustomProgressDialog();
		isGpsDialogShowing = true;
		AlertDialog.Builder gpsBuilder = new AlertDialog.Builder(
				MapActivity.this);
		gpsBuilder.setCancelable(false);
		gpsBuilder
				.setTitle(getResources().getString(R.string.dialog_no_gps))
				.setMessage(getResources().getString(R.string.dialog_no_gps_messgae))
				.setPositiveButton(getResources().getString(R.string.dialog_enable_gps),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// continue with delete
								Intent intent = new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivity(intent);
								removeGpsDialog();
							}
						})

				.setNegativeButton(getResources().getString(R.string.dialog_exit),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing
								removeGpsDialog();
								finish();
							}
						});
		gpsAlertDialog = gpsBuilder.create();
		gpsAlertDialog.show();
	}

	private void removeGpsDialog() {
		if (gpsAlertDialog != null && gpsAlertDialog.isShowing()) {
			gpsAlertDialog.dismiss();
			isGpsDialogShowing = false;
			gpsAlertDialog = null;

		}
	}

	private void removeInternetDialog() {
		if (internetDialog != null && internetDialog.isShowing()) {
			internetDialog.dismiss();
			isNetDialogShowing = false;
			internetDialog = null;

		}
	}

	private void showInternetDialog() {
		AndyUtils.removeCustomProgressDialog();
		isNetDialogShowing = true;
		AlertDialog.Builder internetBuilder = new AlertDialog.Builder(
				MapActivity.this);
		internetBuilder.setCancelable(false);
		internetBuilder
				.setTitle(getResources().getString(R.string.dialog_no_internet))
				.setMessage(getResources().getString(R.string.dialog_no_inter_message))
				.setPositiveButton(getResources().getString(R.string.dialog_enable_3g),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// continue with delete
								Intent intent = new Intent(
										android.provider.Settings.ACTION_SETTINGS);
								startActivity(intent);
								removeInternetDialog();
							}
						})
				.setNeutralButton(getResources().getString(R.string.dialog_enable_wifi),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// User pressed Cancel button. Write
								// Logic Here
								startActivity(new Intent(
										Settings.ACTION_WIFI_SETTINGS));
								removeInternetDialog();
							}
						})
				.setNegativeButton(getResources().getString(R.string.dialog_exit),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing
								removeInternetDialog();
								finish();
							}
						});
		internetDialog = internetBuilder.create();
		internetDialog.show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			ShowGpsDialog();
		} else {
			removeGpsDialog();
		}
		registerReceiver(internetConnectionReciever, new IntentFilter(
				"android.net.conn.CONNECTIVITY_CHANGE"));
		registerReceiver(GpsChangeReceiver, new IntentFilter(
				LocationManager.PROVIDERS_CHANGED_ACTION));
		isRecieverRegistered = true;

		if (AndyUtils.isNetworkAvailable(this)
				&& manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			if (!isDataRecieved) {

				checkStatus();
				startLocationUpdateService();

			}
		}

		registerIsApproved();
		User user = dbHelper.getUser();
		if (user != null) {
			aQuery.id(ivMenuProfile).progress(R.id.pBar)
					.image(user.getPicture(), imageOptions);
			tvMenuName.setText(user.getFname() + " " + user.getLname());
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		Mint.closeSession(this);
		AndyUtils.removeCustomProgressDialog();
		// Mint.closeSession(this);
		if (isRecieverRegistered) {
			unregisterReceiver(internetConnectionReciever);
			unregisterReceiver(GpsChangeReceiver);

		}
		unregisterIsApproved();
		stopLocationUpdateService();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, final int position,
			long arg3) {
		// AndyUtils.showToast("Postion :" + arg2, this);
		if (position == 0) {
			return;
		}
		drawerLayout.closeDrawer(drawerList);
		// mMenuDrawer.closeMenu();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (position == 1) {
					startActivity(new Intent(MapActivity.this,
							ProfileActivity.class));
				} else if (position == 2) {
					startActivity(new Intent(MapActivity.this,
							HistoryActivity.class));
				} else if (position == 3) {
					startActivity(new Intent(MapActivity.this,
							SettingActivity.class));
				} else if (position == 4) {
					startActivity(new Intent(MapActivity.this,
							BookingActivity.class));

				} else if (position == 5) {
					 startActivity(new Intent(MapActivity.this,
							MarketingActivity.class));
					/*
					Intent sendIntent = new Intent();
					sendIntent.setAction(Intent.ACTION_SEND);
					sendIntent
							.putExtra(
									Intent.EXTRA_TEXT,
									"I am using "
											+ getString(R.string.app_name)
											+ " App ! Why don't you try it out...\nInstall "
											+ getString(R.string.app_name)
											+ " now !\nhttps://play.google.com/store/apps/details?id="
											+ getPackageName());
					sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
							getString(R.string.app_name) + " App !");
					sendIntent.setType("text/plain");

					startActivity(Intent.createChooser(sendIntent,
							getString(R.string.text_share_app)));
                     */
				} else if (position == 6) {
					//startActivity(new Intent(MapActivity.this,
						//	UberViewPaymentActivity.class));
					startActivity(new Intent(MapActivity.this,
							BankAccountActivity.class));
				} else if (position == (arrayListApplicationPages.size() - 1)) {
					if (isLogoutCheck) {
						openLogoutDialog();
						isLogoutCheck = false;
						return;
					}

				} else if (position == (arrayListApplicationPages.size())) {
					//fake menu for flush logout
						return;


				} else {
					Intent intent = new Intent(MapActivity.this,
							MenuDescActivity.class);
					intent.putExtra(AndyConstants.Params.TITLE,
							arrayListApplicationPages.get(position - 1)
									.getTitle());
					intent.putExtra(AndyConstants.Params.CONTENT,
							arrayListApplicationPages.get(position - 1)
									.getData());
					startActivity(intent);
				}
			}
		}, 350);

	}

	public void openLogoutDialog() {
		final Dialog mDialog = new Dialog(this,
				android.R.style.Theme_Translucent_NoTitleBar);
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		mDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mDialog.setContentView(R.layout.logout);
		tvLogoutOk = (MyFontTextView) mDialog.findViewById(R.id.tvLogoutOk);
		tvLogoutCancel = (MyFontTextView) mDialog
				.findViewById(R.id.tvLogoutCancel);
		tvLogoutOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				logout();
				mDialog.dismiss();

			}
		});
		tvLogoutCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isLogoutCheck = true;
				mDialog.dismiss();
			}
		});
		mDialog.show();
	}

	// @Override
	// public void setTitle(CharSequence title) {
	// mTitle = title;
	// getSupportActionBar().setTitle(mTitle);
	// }

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btnActionMenu:
			drawerLayout.openDrawer(drawerList);
			// mMenuDrawer.toggleMenu();
			break;

		case R.id.tvTitle:
			drawerLayout.openDrawer(drawerList);
			// mMenuDrawer.toggleMenu();

		default:
			break;
		}
	}

	public void logout() {
		if (!AndyUtils.isNetworkAvailable(this)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet), this);
			return;
		}
		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(this).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL, Lserver + AndyConstants.ServiceType.LOGOUT);
		map.put(AndyConstants.Params.ID, preferenceHelper.getUserId());
		map.put(AndyConstants.Params.TOKEN, preferenceHelper.getSessionToken());
		// new HttpRequester(this, map, AndyConstants.ServiceCode.LOGOUT, false,
		// this);

		requestQueue.add(new VolleyHttpRequest(Method.POST, map,
				AndyConstants.ServiceCode.LOGOUT, this, this));
	}

	public void getRequestsInProgress() {
		if (!AndyUtils.isNetworkAvailable(this)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet), this);
			return;
		}

		AndyUtils.showCustomProgressDialog(this, "",
				getResources().getString(R.string.progress_dialog_loading),
				false);
		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(this).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		if(Lserver.isEmpty()) Lserver = AndyConstants.ServiceType.LOCAL_URL;
		map.put(AndyConstants.URL,
				Lserver + AndyConstants.ServiceType.REQUEST_IN_PROGRESS
						+ AndyConstants.Params.ID + "="
						+ preferenceHelper.getUserId() + "&"
						+ AndyConstants.Params.TOKEN + "="
						+ preferenceHelper.getSessionToken()+"&"
						+ AndyConstants.Params.DEVICE_TOKEN + "="
						+ new PreferenceHelper(this).getDeviceToken());
		// new HttpRequester(this, map,
		// AndyConstants.ServiceCode.REQUEST_IN_PROGRESS, true, this);

		requestQueue.add(new VolleyHttpRequest(Method.GET, map,
				AndyConstants.ServiceCode.REQUEST_IN_PROGRESS, this, this));
	}

	public void checkRequestStatus() {
		if (!AndyUtils.isNetworkAvailable(this)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet), this);
			return;
		}
		AndyUtils.showCustomProgressDialog(this, "",
				getResources().getString(R.string.progress_dialog_request),
				false);
		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(this).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL,
				Lserver + AndyConstants.ServiceType.CHECK_REQUEST_STATUS
						+ AndyConstants.Params.ID + "="
						+ preferenceHelper.getUserId() + "&"
						+ AndyConstants.Params.TOKEN + "="
						+ preferenceHelper.getSessionToken() + "&"
						+ AndyConstants.Params.REQUEST_ID + "="
						+ preferenceHelper.getRequestId());
		// new HttpRequester(this, map,
		// AndyConstants.ServiceCode.CHECK_REQUEST_STATUS, true, this);

		requestQueue.add(new VolleyHttpRequest(Method.GET, map,
				AndyConstants.ServiceCode.CHECK_REQUEST_STATUS, this, this));
	}

	public void openApprovedDialog() {
		mDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		mDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mDialog.setContentView(R.layout.provider_approve_dialog);
		mDialog.setCancelable(false);
		tvApprovedClose = (MyFontTextView) mDialog
				.findViewById(R.id.tvApprovedClose);
		tvApprovedClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDialog.dismiss();
				finish();
			}
		});
		mDialog.show();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		openExitDialog();
	}

	public void openExitDialog() {
		final Dialog mDialog = new Dialog(this,
				android.R.style.Theme_Translucent_NoTitleBar);
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		mDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mDialog.setContentView(R.layout.exit_layout);
		tvExitOk = (MyFontTextView) mDialog.findViewById(R.id.tvExitOk);
		tvExitCancel = (MyFontTextView) mDialog.findViewById(R.id.tvExitCancel);
		tvExitOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDialog.dismiss();
				finish();
			}
		});
		tvExitCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDialog.dismiss();
			}
		});
		mDialog.show();
	}

	@Override
	public void onTaskCompleted(String response, int serviceCode) {

		String[] part = response.split("\\{",2);
		response = "{" + part[1];

		super.onTaskCompleted(response, serviceCode);
		switch (serviceCode) {
		case AndyConstants.ServiceCode.REQUEST_IN_PROGRESS:
			AndyUtils.removeCustomProgressDialog();
			AppLog.Log(TAG, "requestInProgress Response :" + response);

			if (!parseContent.isSuccess(response)) {
				if (parseContent.getErrorCode(response) == AndyConstants.REQUEST_ID_NOT_FOUND) {
					AndyUtils.removeCustomProgressDialog();
					CharSequence mbody = "No Request found";
					//notifications(mbody,R.raw.welcome_to_overthere);
					preferenceHelper.clearRequestData();

					getMenuItems();
					addFragment(new ClientRequestFragment(), false,
							AndyConstants.CLIENT_REQUEST_TAG, true);
				} else if (parseContent.getErrorCode(response) == AndyConstants.TOKEN_EXPIRED || parseContent.getErrorCode(response) == AndyConstants.INVALID_TOKEN) {
							logout();
				}
				if (!parseContent.parseIsApproved(response)) {
				   if (isApprovedCheck) {
					 openApprovedDialog();
					 isApprovedCheck = false;
				   }

			    }
				return;
			}
			AndyUtils.removeCustomProgressDialog();
			int requestId = parseContent.parseRequestInProgress(response);
			if (requestId == AndyConstants.NO_REQUEST) {
				CharSequence mbody = "Welcome to OverThere";
				//notifications(mbody,R.raw.welcome_to_overthere);
				getMenuItems();
				addFragment(new ClientRequestFragment(), false,
						AndyConstants.CLIENT_REQUEST_TAG, true);
			} else {
				checkRequestStatus();
			}


			break;
		case AndyConstants.ServiceCode.CHECK_REQUEST_STATUS:
			AppLog.Log(TAG, "checkrequeststatus Response :" + response);
			if (!parseContent.isSuccess(response)) {
				if (parseContent.getErrorCode(response) == AndyConstants.REQUEST_ID_NOT_FOUND) {
					preferenceHelper.clearRequestData();
					AndyUtils.removeCustomProgressDialog();
					addFragment(new ClientRequestFragment(), false,
							AndyConstants.CLIENT_REQUEST_TAG, true);
				} else if (parseContent.getErrorCode(response) == AndyConstants.INVALID_TOKEN) {
					if (preferenceHelper.getLoginBy().equalsIgnoreCase(
							AndyConstants.MANUAL))
						login();
					else
						loginSocial(preferenceHelper.getUserId(),
								preferenceHelper.getLoginBy());
				}
				return;
			}
			AndyUtils.removeCustomProgressDialog();
			Bundle bundle = new Bundle();
			JobFragment jobFragment = new JobFragment();
			RequestDetail requestDetail = parseContent
					.parseRequestStatus(response);
			if (requestDetail == null) {
				return;
			}
			getMenuItems();
			switch (requestDetail.getJobStatus()) {

			case AndyConstants.NO_REQUEST:
				preferenceHelper.clearRequestData();
				Intent i = new Intent(this, MapActivity.class);
				startActivity(i);
				break;

			case AndyConstants.IS_WALKER_STARTED:
				bundle.putInt(AndyConstants.JOB_STATUS,
						AndyConstants.IS_WALKER_STARTED);
				bundle.putSerializable(AndyConstants.REQUEST_DETAIL,
						requestDetail);
				jobFragment.setArguments(bundle);
				addFragment(jobFragment, false, AndyConstants.JOB_FRGAMENT_TAG,
						true);
				break;
			case AndyConstants.IS_WALKER_ARRIVED:
				bundle.putInt(AndyConstants.JOB_STATUS,
						AndyConstants.IS_WALKER_ARRIVED);
				bundle.putSerializable(AndyConstants.REQUEST_DETAIL,
						requestDetail);
				jobFragment.setArguments(bundle);
				addFragment(jobFragment, false, AndyConstants.JOB_FRGAMENT_TAG,
						true);
				break;
			case AndyConstants.IS_WALK_STARTED:
				bundle.putInt(AndyConstants.JOB_STATUS,
						AndyConstants.IS_WALK_STARTED);
				bundle.putSerializable(AndyConstants.REQUEST_DETAIL,
						requestDetail);
				jobFragment.setArguments(bundle);
				addFragment(jobFragment, false, AndyConstants.JOB_FRGAMENT_TAG,
						true);
				break;
			case AndyConstants.IS_WALK_COMPLETED:
				bundle.putInt(AndyConstants.JOB_STATUS,
						AndyConstants.IS_WALK_COMPLETED);
				bundle.putSerializable(AndyConstants.REQUEST_DETAIL,
						requestDetail);
				jobFragment.setArguments(bundle);
				addFragment(jobFragment, false, AndyConstants.JOB_FRGAMENT_TAG,
						true);
				break;
			case AndyConstants.IS_DOG_RATED:
				FeedbackFrament feedbackFrament = new FeedbackFrament();
				bundle.putSerializable(AndyConstants.REQUEST_DETAIL,
						requestDetail);
				bundle.putString(AndyConstants.Params.TIME, 0 + " "
						+ getResources().getString(R.string.text_mins));
				bundle.putString(AndyConstants.Params.DISTANCE, 0 + " "
						+ getResources().getString(R.string.text_miles));
				feedbackFrament.setArguments(bundle);
				addFragment(feedbackFrament, false,
						AndyConstants.FEEDBACK_FRAGMENT_TAG, true);
				break;

			case AndyConstants.ServiceCode.APPLICATION_PAGES:

				break;

			default:
				break;
			}

			break;
		case AndyConstants.ServiceCode.LOGIN:
			AndyUtils.removeCustomProgressDialog();
			if (parseContent.isSuccessWithId(response)) {
				checkStatus();
				CharSequence mbody="welcome to OverTere";
				notifications(mbody,R.raw.welcome_to_overthere);

			}
			break;
		case AndyConstants.ServiceCode.APPLICATION_PAGES:

			AppLog.Log(TAG, "Menuitems Response::" + response);
			arrayListApplicationPages = parseContent.parsePages(
					arrayListApplicationPages, response);
			adapter.notifyDataSetChanged();
			isDataRecieved = true;
			break;

		case AndyConstants.ServiceCode.LOGOUT:
			AppLog.Log("Logout Response", response);
			if (parseContent.isSuccess(response)) {
				String mbody="Log Out";
				notifications(mbody, R.raw.thank_overthere);
				preferenceHelper.Logout();
				goToMainActivity();
				// Change by hardik bhalodi
				stopLocationUpdateService();

			}
			break;
		default:
			break;
		}
	}

	private void login() {
		if (!AndyUtils.isNetworkAvailable(this)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet), this);
			return;
		}
		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(this).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL, Lserver + AndyConstants.ServiceType.LOGIN);
		map.put(AndyConstants.Params.EMAIL, preferenceHelper.getEmail());
		map.put(AndyConstants.Params.PASSWORD, preferenceHelper.getPassword());
		map.put(AndyConstants.Params.DEVICE_TYPE,
				AndyConstants.DEVICE_TYPE_ANDROID);
		map.put(AndyConstants.Params.DEVICE_TOKEN,
				preferenceHelper.getDeviceToken());
		map.put(AndyConstants.Params.LOGIN_BY, AndyConstants.MANUAL);
		// new HttpRequester(this, map, AndyConstants.ServiceCode.LOGIN, this);

		requestQueue.add(new VolleyHttpRequest(Method.POST, map,
				AndyConstants.ServiceCode.LOGIN, this, this));
	}

	private void loginSocial(String id, String loginType) {
		if (!AndyUtils.isNetworkAvailable(this)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet), this);
			return;
		}

		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(this).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL, Lserver + AndyConstants.ServiceType.LOGIN);
		map.put(AndyConstants.Params.SOCIAL_UNIQUE_ID, id);
		map.put(AndyConstants.Params.DEVICE_TYPE,
				AndyConstants.DEVICE_TYPE_ANDROID);
		map.put(AndyConstants.Params.DEVICE_TOKEN,
				preferenceHelper.getDeviceToken());
		map.put(AndyConstants.Params.LOGIN_BY, loginType);
		// new HttpRequester(this, map, AndyConstants.ServiceCode.LOGIN, this);

		requestQueue.add(new VolleyHttpRequest(Method.POST, map,
				AndyConstants.ServiceCode.LOGIN, this, this));

	}

	@Override
	public void onErrorResponse(VolleyError error) {
		// TODO Auto-generated method stub
		AppLog.Log("TAG", error.getMessage());
	}
    public void notifications(CharSequence mbody, int voice){
		Uri sound = Uri.parse("android.resource://" + this.getPackageName() + "/" + voice);
		Notification noti = new Notification.Builder(this)
				.setContentTitle(getResources().getString(R.string.app_name))
				.setContentText(mbody)
				.setSmallIcon(R.drawable.ic_launcher).setAutoCancel(true)
				.setSound(sound)
				.build();
		NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
		noti.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(1000, noti);
	}
}