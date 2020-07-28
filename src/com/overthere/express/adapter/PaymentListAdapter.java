/**
 * 
 */
package com.overthere.express.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import android.text.TextUtils;
import com.overthere.express.R;
import com.overthere.express.model.Card;
import com.overthere.express.parse.AsyncTaskCompleteListener;
import com.overthere.express.parse.HttpRequester;
import com.overthere.express.parse.ParseContent;
import com.overthere.express.parse.VolleyHttpRequest;
import com.overthere.express.utills.AndyUtils;
import com.overthere.express.utills.AppLog;
import com.overthere.express.utills.AndyConstants;
import com.overthere.express.utills.PreferenceHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * @author Hardik A Bhalodi
 * 
 */
public class PaymentListAdapter extends BaseAdapter implements
		AsyncTaskCompleteListener, ErrorListener {
	private LayoutInflater inflater;
	private ViewHolder holder;
	private ArrayList<Card> listCard;
	private int selectedPosition;
	private Context context;
	private PreferenceHelper pHelper;
	private int positionKey = 21;
	private RequestQueue requestQueue;

	public PaymentListAdapter(Context context, ArrayList<Card> listCard,
			int defaultCard) {
		this.listCard = listCard;
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		selectedPosition = defaultCard;
		pHelper = new PreferenceHelper(context);
		requestQueue = Volley.newRequestQueue(context);
	}

	@Override
	public int getCount() {
		return listCard.size();
	}

	@Override
	public Object getItem(int position) {
		return listCard.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.view_payment_list_item,
					parent, false);
			holder = new ViewHolder();
			holder.ivCard = (ImageView) convertView.findViewById(R.id.ivCard);
			holder.tvNo = (TextView) convertView.findViewById(R.id.tvNo);
			holder.rdCardSelection = (RadioButton) convertView
					.findViewById(R.id.rdCardSelection);
			convertView.setTag(holder);
			holder.rdCardSelection.setTag(position);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final Card card = listCard.get(position);
		final int cardId = card.getId();

		holder.tvNo.setText("*****" + card.getLastFour());
		String type = card.getCardType();

		if (type.equalsIgnoreCase(AndyConstants.Params.VISA)) {
			holder.ivCard.setImageResource(R.drawable.ub__creditcard_visa);
		} else if (type.equalsIgnoreCase(AndyConstants.Params.MASTERCARD)) {
			holder.ivCard
					.setImageResource(R.drawable.ub__creditcard_mastercard);
		} else if (type.equalsIgnoreCase(AndyConstants.Params.AMERICAN_EXPRESS)) {
			holder.ivCard.setImageResource(R.drawable.ub__creditcard_amex);
		} else if (type.equalsIgnoreCase(AndyConstants.Params.DISCOVER)) {
			holder.ivCard.setImageResource(R.drawable.ub__creditcard_discover);
		} else if (type.equalsIgnoreCase(AndyConstants.Params.DINERS_CLUB)) {
			holder.ivCard.setImageResource(R.drawable.ub__creditcard_discover);
		} else {
			holder.ivCard.setImageResource(R.drawable.nav_payment);
		}

		if (selectedPosition == cardId)
			holder.rdCardSelection.setChecked(true);
		else
			holder.rdCardSelection.setChecked(false);

		if (card.isDefault()) {
			holder.rdCardSelection.setChecked(true);
			PreferenceHelper pref = new PreferenceHelper(context);
			pref.putDefaultCard(cardId);
			pref.putDefaultCardNo(card.getLastFour());
			pref.putDefaultCardType(card.getCardType());
		} else
			holder.rdCardSelection.setChecked(false);

		holder.rdCardSelection.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				RadioButton rd = (RadioButton) v;
				if (rd.isChecked()) {
					AppLog.Log("PaymentAdapater", "checked Id " + cardId);
					selectedPosition = cardId;
					PreferenceHelper pref = new PreferenceHelper(context);
					pref.putDefaultCard(cardId);
					pref.putDefaultCardNo(card.getLastFour());
					pref.putDefaultCardType(card.getCardType());
					notifyDataSetChanged();
					setDefaultCard(cardId);
				} else {
					AppLog.Log("PaymentAdapater", "unchecked Id " + cardId);
				}
				Intent i = new Intent("card_change_receiver");
				context.sendBroadcast(i);
			}
		});

		return convertView;
	}

	private class ViewHolder {
		public ImageView ivCard;
		public TextView tvNo;
		public RadioButton rdCardSelection;
	}

	private void setDefaultCard(int cardId) {
		// AndyUtils.showCustomProgressDialog(this,
			//	getString(R.string.text_changing_default_card), true,
			//	null);
		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(context).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL, Lserver + AndyConstants.ServiceType.DEFAULT_CARD);
		map.put(AndyConstants.Params.ID, String.valueOf(pHelper.getUserId()));
		map.put(AndyConstants.Params.TOKEN, String.valueOf(pHelper.getSessionToken()));
		map.put(AndyConstants.Params.DEFAULT_CARD_ID, String.valueOf(cardId));

		// new HttpRequester((Activity) context, map,
		// Const.ServiceCode.DEFAULT_CARD, this);
		requestQueue.add(new VolleyHttpRequest(Method.POST, map,
				AndyConstants.ServiceCode.DEFAULT_CARD, this, this));
	}

	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		AndyUtils.removeCustomProgressDialog();
		listCard.clear();
		String[] part = response.split("\\{",2);
		response = "{" + part[1];

		AppLog.Log("PaymentAdapter", "CardSelection reponse : " + response);
		new ParseContent((Activity) context).parseCards(response, listCard);
		notifyDataSetChanged();
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		// TODO Auto-generated method stub
		AppLog.Log(AndyConstants.Params.TAG, error.getMessage());
	}
}
