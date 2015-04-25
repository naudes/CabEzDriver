package com.automated.taxinow.driver;

import java.util.HashMap;

import org.jraf.android.backport.switchwidget.Switch;

import com.automated.taxinow.driver.base.ActionBarBaseActivitiy;
import com.automated.taxinow.driver.parse.AsyncTaskCompleteListener;
import com.automated.taxinow.driver.parse.HttpRequester;
import com.automated.taxinow.driver.parse.ParseContent;
import com.automated.taxinow.driver.utills.AndyConstants;
import com.automated.taxinow.driver.utills.AndyUtils;
import com.automated.taxinow.driver.utills.AppLog;
import com.automated.taxinow.driver.utills.PreferenceHelper;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * @author Kishan H Dhamat
 * 
 */
public class SettingActivity extends ActionBarBaseActivitiy implements
		OnCheckedChangeListener, AsyncTaskCompleteListener {
	private Switch switchSetting;
	private PreferenceHelper preferenceHelper;
	private ParseContent parseContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		preferenceHelper = new PreferenceHelper(this);
		parseContent = new ParseContent(this);
		switchSetting = (Switch) findViewById(R.id.switchAvaibility);
		setActionBarTitle(getString(R.string.text_setting));
		setActionBarIcon(R.drawable.promotion);
		// getSupportActionBar().setTitle(
		// getResources().getString(R.string.text_setting));
		// getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// getSupportActionBar().setHomeButtonEnabled(true);
		checkState();
	}

	private void checkState() {
		if (!AndyUtils.isNetworkAvailable(this)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet), this);
			return;
		}
		AndyUtils.showCustomProgressDialog(this, "",
				getResources().getString(R.string.progress_getting_avaibility),
				false);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(AndyConstants.URL,
				AndyConstants.ServiceType.CHECK_STATE + AndyConstants.Params.ID
						+ "=" + preferenceHelper.getUserId() + "&"
						+ AndyConstants.Params.TOKEN + "="
						+ preferenceHelper.getSessionToken());
		new HttpRequester(this, map, AndyConstants.ServiceCode.CHECK_STATE,
				true, this);
	}

	private void changeState() {
		if (!AndyUtils.isNetworkAvailable(this)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet), this);
			return;
		}

		AndyUtils.showCustomProgressDialog(this, "",
				getResources().getString(R.string.progress_changing_avaibilty),
				false);

		HashMap<String, String> map = new HashMap<String, String>();
		map.put(AndyConstants.URL, AndyConstants.ServiceType.TOGGLE_STATE);
		map.put(AndyConstants.Params.ID, preferenceHelper.getUserId());
		map.put(AndyConstants.Params.TOKEN, preferenceHelper.getSessionToken());

		new HttpRequester(this, map, AndyConstants.ServiceCode.TOGGLE_STATE,
				this);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		AppLog.Log("TAG", "On checked change listener");
		switch (buttonView.getId()) {
		case R.id.switchAvaibility:
			changeState();
			break;

		default:
			break;
		}
	}

	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		AndyUtils.removeCustomProgressDialog();
		switch (serviceCode) {
		case AndyConstants.ServiceCode.CHECK_STATE:
		case AndyConstants.ServiceCode.TOGGLE_STATE:
			if (!parseContent.isSuccess(response)) {
				return;
			}
			AppLog.Log("TAG", "toggle state:" + response);
			if (parseContent.parseAvaibilty(response)) {
				switchSetting.setOnCheckedChangeListener(null);
				switchSetting.setChecked(true);

			} else {
				switchSetting.setOnCheckedChangeListener(null);
				switchSetting.setChecked(false);
			}
			switchSetting.setOnCheckedChangeListener(this);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		AndyUtils.removeCustomProgressDialog();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.btnActionNotification:
			onBackPressed();
			overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_right);
			break;
		}

	}
}
