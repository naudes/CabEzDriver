package com.overthere.express;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.overthere.express.gcm.CommonUtilities;
import com.overthere.express.gcm.GCMRegisterHendler;
import com.overthere.express.utills.AndyUtils;
import com.overthere.express.utills.AppLog;
import com.overthere.express.utills.PreferenceHelper;
import com.overthere.express.widget.MyFontTextView;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends Activity implements OnClickListener {

	private boolean isRecieverRegister = false;
	private static final String TAG = "FirstFragment";
	private PreferenceHelper preferenceHelper;
	// private Animation topToBottomAnimation, bottomToTopAnimation;
	private RelativeLayout rlLoginRegisterLayout;
	private MyFontTextView tvExitOk, tvExitCancel;

	// private MyFontTextView tvMainBottomView;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		preferenceHelper = new PreferenceHelper(this);
		if (!TextUtils.isEmpty(preferenceHelper.getUserId())) {
			startActivity(new Intent(this, MapActivity.class));
			this.finish();
			return;
		}
		setContentView(R.layout.fragment_main);
		rlLoginRegisterLayout = (RelativeLayout) findViewById(R.id.rlLoginRegisterLayout);
		// tvMainBottomView = (MyFontTextView) mainFragmentView
		// .findViewById(R.id.tvMainBottomView);

		findViewById(R.id.btnFirstSignIn).setOnClickListener(this);
		findViewById(R.id.btnFirstRegister).setOnClickListener(this);

		if (TextUtils.isEmpty(new PreferenceHelper(MainActivity.this)
				.getDeviceToken())) {
			isRecieverRegister = true;
			//registerGcmReceiver(mHandleMessageReceiver);
		} else {

			AppLog.Log(TAG, "device already registerd with :"
					+ new PreferenceHelper(MainActivity.this).getDeviceToken());
		}
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {

		Intent startRegisterActivity = new Intent(MainActivity.this,
				RegisterActivity.class);
		switch (v.getId()) {

		case R.id.btnFirstRegister:
			if (!AndyUtils.isNetworkAvailable(MainActivity.this)) {
				AndyUtils.showToast(
						getResources().getString(R.string.toast_no_internet),
						MainActivity.this);
				return;
			}
			startRegisterActivity.putExtra("isSignin", false);

			break;

		case R.id.btnFirstSignIn:

			startRegisterActivity.putExtra("isSignin", true);

			break;

		default:
			break;
		}
		startActivity(startRegisterActivity);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		finish();

	}

	@Override
	public void onDestroy() {

		super.onDestroy();
		if (isRecieverRegister) {
			//unregisterGcmReceiver(mHandleMessageReceiver);
			isRecieverRegister = false;
		}

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

}
