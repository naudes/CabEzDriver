package com.overthere.express;

import java.util.ArrayList;
import java.util.HashMap;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.overthere.express.adapter.PaymentListAdapter;
import com.overthere.express.base.ActionBarBaseActivitiy;
import com.overthere.express.model.Card;
import com.overthere.express.parse.HttpRequester;
import com.overthere.express.parse.ParseContent;
import com.overthere.express.parse.VolleyHttpRequest;
import com.overthere.express.utills.AndyUtils;
import com.overthere.express.utills.AppLog;
import com.overthere.express.utills.AndyConstants;
import com.overthere.express.utills.PreferenceHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Jack Zeng
 * 
 */
public class UberViewPaymentActivity extends ActionBarBaseActivitiy {

	private ListView listViewPayment;
	private PaymentListAdapter adapter;
	private ArrayList<Card> listCards;
	private int REQUEST_ADD_CARD = 1;
	private ImageView tvNoHistory, ivCredit, ivCash;
	private TextView tvHeaderText;
	private View v;
	private TextView btnAddNewPayment;
	private int paymentMode;
	private LinearLayout llPaymentList;
	private RequestQueue requestQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_payment);
		setActionBarTitle(getResources().getString(R.string.text_payment));
		setActionBarIcon(R.drawable.ic_payment);
		requestQueue = Volley.newRequestQueue(this);
		listViewPayment = (ListView) findViewById(R.id.listViewPayment);
		llPaymentList = (LinearLayout) findViewById(R.id.llPaymentList);
		tvNoHistory = (ImageView) findViewById(R.id.ivEmptyView);
		tvHeaderText = (TextView) findViewById(R.id.tvHeaderText);
		btnAddNewPayment = (TextView) findViewById(R.id.tvAddNewPayment);

		ivCash = (ImageView) findViewById(R.id.ivCash);
		ivCredit = (ImageView) findViewById(R.id.ivCredit);

		btnAddNewPayment.setOnClickListener(this);
		paymentMode = (int) new PreferenceHelper(this).getPaymentType();
		v = findViewById(R.id.line);
		listCards = new ArrayList<Card>();
		adapter = new PaymentListAdapter(this, listCards, new PreferenceHelper(
				this).getDefaultCard());
		listViewPayment.setAdapter(adapter);
		getCards();
		//AndyUtils.showToast("Sorry, payment not available yet.", this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnActionNotification:
			onBackPressed();
			break;
		case R.id.tvAddNewPayment:
			startActivityForResult(new Intent(this,
					UberAddPaymentActivity.class), REQUEST_ADD_CARD);
			break;
		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.uberorg.ActionBarBaseActivitiy#isValidate()
	 */
	protected boolean isValidate() {
		// TODO Auto-generated method stub
		return false;
	}

	private void getCards() {
		//AndyUtils.showCustomProgressDialog(this,
		//		getString(R.string.progress_loading), false, null);
		AndyUtils.showToast("Get Credit Card", this);
		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(this).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL,
				Lserver + AndyConstants.ServiceType.GET_CARDS + AndyConstants.Params.ID + "="
						+ new PreferenceHelper(this).getUserId() + "&"
						+ AndyConstants.Params.TOKEN + "="
						+ new PreferenceHelper(this).getSessionToken());
		requestQueue.add(new VolleyHttpRequest(Method.GET, map,
				AndyConstants.ServiceCode.GET_CARDS, this, this));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uberorg.ActionBarBaseActivitiy#onTaskCompleted(java.lang.String,
	 * int)
	 */
	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		AndyUtils.removeCustomProgressDialog();
		String[] part = response.split("\\{",2);
		response = "{" + part[1];

		switch (serviceCode) {
		case AndyConstants.ServiceCode.GET_CARDS:
			if (new ParseContent(this).isSuccess(response)) {
				listCards.clear();
				new ParseContent(this).parseCards(response, listCards);
				AppLog.Log("UberViewPayment", "listCards : " + listCards.size());
				if (listCards.size() > 0) {
					llPaymentList.setVisibility(View.VISIBLE);
					tvNoHistory.setVisibility(View.GONE);
					paymentMode = 1;
					tvHeaderText.setVisibility(View.VISIBLE);
				} else {
					llPaymentList.setVisibility(View.GONE);
					tvNoHistory.setVisibility(View.VISIBLE);
					tvHeaderText.setVisibility(View.GONE);
					paymentMode = 0;
				}
				adapter.notifyDataSetChanged();
			}
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uberorg.ActionBarBaseActivitiy#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case Activity.RESULT_OK:
			getCards();
			break;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		// TODO Auto-generated method stub
		AppLog.Log(AndyConstants.Params.TAG, error.getMessage());
	}
}
