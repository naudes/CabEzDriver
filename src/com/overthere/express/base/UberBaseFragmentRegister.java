package com.overthere.express.base;

import com.android.volley.Response.ErrorListener;
import com.overthere.express.MapActivity;
import com.overthere.express.RegisterActivity;
import com.overthere.express.parse.AsyncTaskCompleteListener;

import android.content.Intent;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.view.View.OnClickListener;

import androidx.fragment.app.Fragment;

/**
 * @author Hardik A Bhalodi
 */
abstract public class UberBaseFragmentRegister extends Fragment implements
		OnClickListener, AsyncTaskCompleteListener, ErrorListener {
	RegisterActivity activity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		activity = (RegisterActivity) getActivity();
	}

	protected abstract boolean isValidate();

	public boolean OnBackPressed() {
		// activity.removeAllFragment(new UberMainFragment(), false,
		// Const.FRAGMENT_MAIN);
		activity.startActivity(new Intent(activity, MapActivity.class));
		activity.finish();
		return true;
	}

	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		// TODO Auto-generated method stub

		super.onResume();
		activity.actionBar.show();

	}

}
