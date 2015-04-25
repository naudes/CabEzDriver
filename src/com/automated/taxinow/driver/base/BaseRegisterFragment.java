package com.automated.taxinow.driver.base;

import com.automated.taxinow.driver.RegisterActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View.OnClickListener;

/**
 * @author Kishan H Dhamat
 * 
 */
public abstract class BaseRegisterFragment extends Fragment implements
		OnClickListener {
	protected RegisterActivity registerActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerActivity = (RegisterActivity) getActivity();
	}

	public void startActivityForResult(Intent intent, int requestCode,
			String fragmentTag) {
		registerActivity.startActivityForResult(intent, requestCode,
				fragmentTag);
	}

	@Override
	@Deprecated
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
	}

}
