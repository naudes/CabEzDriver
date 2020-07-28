package com.overthere.express.fragment;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.overthere.express.MapActivity;
import com.overthere.express.RegisterActivity;
import com.overthere.express.base.UberBaseFragmentRegister;
import com.overthere.express.R;
import com.overthere.express.parse.ParseContent;
import com.overthere.express.parse.VolleyHttpRequest;
import com.overthere.express.utills.AndyUtils;
import com.overthere.express.utills.AppLog;
import com.overthere.express.utills.AndyConstants;

import com.overthere.express.utills.PreferenceHelper;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.TokenCallback;
import com.stripe.android.util.TextUtils;
//import com.stripe.android.*;
/**
 * @author Hardik A Bhalodi
 */
public class UberAddPaymentFragmentRegister extends UberBaseFragmentRegister
{
	private static final String TAG = "UberAddPaymentFragmentRegister";
	private Button btnAddPayment;
	private ImageView btnScan;
	private final int MY_SCAN_REQUEST_CODE = 111;
	private EditText etCreditCardNum, etCvc, etYear, etMonth;
	// private String patternVisa = "^4[0-9]{12}(?:[0-9]{3})?$";
	// private String patternMasterCard = "^5[1-5][0-9]{14}$";
	// private String patternAmericanExpress = "^3[47][0-9]{13}$";
	public static final String[] PREFIXES_AMERICAN_EXPRESS = { "34", "37" };
	public static final String[] PREFIXES_DISCOVER = { "60", "62", "64", "65" };
	public static final String[] PREFIXES_JCB = { "35" };
	public static final String[] PREFIXES_DINERS_CLUB = { "300", "301", "302",
			"303", "304", "305", "309", "36", "38", "37", "39" };
	public static final String[] PREFIXES_VISA = { "4" };
	public static final String[] PREFIXES_MASTERCARD = { "50", "51", "52",
			"53", "54", "55" };
	public static final String AMERICAN_EXPRESS = "American Express";
	public static final String DISCOVER = "Discover";
	public static final String JCB = "JCB";
	public static final String DINERS_CLUB = "Diners Club";
	public static final String VISA = "Visa";
	public static final String MASTERCARD = "MasterCard";
	public static final String UNKNOWN = "Unknown";
	public static final int MAX_LENGTH_STANDARD = 16;
	public static final int MAX_LENGTH_AMERICAN_EXPRESS = 15;
	public static final int MAX_LENGTH_DINERS_CLUB = 14;
	private String type;
	private String token, id;
	private EditText etHolder;
	private RequestQueue requestQueue;
    private RegisterActivity activity;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		token = getArguments().getString(AndyConstants.Params.TOKEN);
		id = getArguments().getString(AndyConstants.Params.ID);
		requestQueue = Volley.newRequestQueue(getActivity());
		// getActivity().startService(
		// new Intent(getActivity(),
		// com.mobile.connect.service.PWConnectService.class));
		// getActivity().bindService(
		// new Intent(getActivity(),
		// com.mobile.connect.service.PWConnectService.class),
		// _serviceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		activity = (RegisterActivity) getActivity();
		activity.setTitle(getResources().getString(R.string.text_payment));
		activity.setActionBarIcon(R.drawable.ic_payment);
		//activity.setTitle(
		//		R.string.text_addpayment_small);
		//activity.setIconMenu(R.drawable.ic_payment);

		//activity.setActionBarTitle(getString(R.string.text_payment));
		//setActionBarIcon(R.drawable.ic_payment);

		activity.btnNotification.setVisibility(View.INVISIBLE);
		View view = inflater.inflate(R.layout.fragment_payment, container,
				false);
		btnAddPayment = (Button) view.findViewById(R.id.btnAddPayment);
		view.findViewById(R.id.btnPaymentSkip).setOnClickListener(this);
		btnScan = (ImageView) view.findViewById(R.id.btnScan);

		etCreditCardNum = (EditText) view
				.findViewById(R.id.edtRegisterCreditCardNumber);
		etCreditCardNum.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (TextUtils.isBlank(s.toString())) {
					etCreditCardNum.setCompoundDrawablesWithIntrinsicBounds(
							null, null, null, null);
				}
				type = getType(s.toString());

				if (type.equals(VISA)) {
					etCreditCardNum.setCompoundDrawablesWithIntrinsicBounds(
							getResources().getDrawable(
									R.drawable.ub__creditcard_visa), null,
							null, null);

				} else if (type.equals(MASTERCARD)) {
					etCreditCardNum.setCompoundDrawablesWithIntrinsicBounds(
							getResources().getDrawable(
									R.drawable.ub__creditcard_mastercard),
							null, null, null);

				} else if (type.equals(AMERICAN_EXPRESS)) {
					etCreditCardNum.setCompoundDrawablesWithIntrinsicBounds(
							getResources().getDrawable(
									R.drawable.ub__creditcard_amex), null,
							null, null);

				} else if (type.equals(DISCOVER)) {
					etCreditCardNum.setCompoundDrawablesWithIntrinsicBounds(
							getResources().getDrawable(
									R.drawable.ub__creditcard_discover), null,
							null, null);

				} else if (type.equals(DINERS_CLUB)) {
					etCreditCardNum.setCompoundDrawablesWithIntrinsicBounds(
							getResources().getDrawable(
									R.drawable.ub__creditcard_discover), null,
							null, null);

				} else {
					etCreditCardNum.setCompoundDrawablesWithIntrinsicBounds(
							null, null, null, null);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		etCvc = (EditText) view.findViewById(R.id.edtRegistercvc);
		etYear = (EditText) view.findViewById(R.id.edtRegisterexpYear);
		etMonth = (EditText) view.findViewById(R.id.edtRegisterexpMonth);
		etYear.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (etYear.getText().toString().length() == 4) {
					etCvc.requestFocus();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		etMonth.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (etMonth.getText().toString().length() == 2) {
					etYear.requestFocus();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		etHolder = (EditText) view
				.findViewById(R.id.edtRegisterCreditCardHolder);
		btnScan.setOnClickListener(this);
		btnAddPayment.setOnClickListener(this);
		return view;
	}

	@Override
	public void onResume() {
		activity.currentFragment = AndyConstants.FRAGMENT_PAYMENT_REGISTER;
		activity.actionBar.setTitle(getResources().getString(R.string.text_addpayment_small));
		super.onResume();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		etCreditCardNum.requestFocus();
		//activity.showKeyboard(etCreditCardNum);


	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.btnAddPayment:
			if (isValidate()) {
				saveCreditCard();

			}
			break;
		case R.id.btnScan:
			scan();
			break;
		case R.id.btnPaymentSkip:
			OnBackPressed();
			break;
		default:
			break;
		}
	}

	@Override
	protected boolean isValidate() {
		if (etCreditCardNum.getText().length() == 0
				|| etCvc.getText().length() == 0
				|| etMonth.getText().length() == 0
				|| etYear.getText().length() == 0) {
			AndyUtils.showToast("Enter Proper data", activity);
			return false;
		}
		return true;
	}

	private void scan() {
		Intent scanIntent = new Intent(activity, CardIOActivity.class);

		// required for authentication with card.io
		// scanIntent.putExtra(CardIOActivity.EXTRA_APP_TOKEN,
		// Const.MY_CARDIO_APP_TOKEN);

		// customize these values to suit your needs.
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default:
																		// true
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default:
																		// false
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default:
																				// false
		scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, false); // default:
																				// false
		activity.startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE,
				AndyConstants.FRAGMENT_PAYMENT_REGISTER);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case MY_SCAN_REQUEST_CODE:
			if (resultCode == Activity.RESULT_OK) {
				if (data != null
						&& data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
					CreditCard scanResult = data
							.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

					etCreditCardNum.setText(scanResult.getRedactedCardNumber());
					if (scanResult.isExpiryValid()) {
						etMonth.setText(scanResult.expiryMonth + "");

						etYear.setText(scanResult.expiryYear + "");
					}
					if (scanResult.cvv != null) {
						etCvc.setText(scanResult.cvv);
					}
				} else {
					AndyUtils.showToast("Scan was canceled.", activity);
				}
			} else {
				AndyUtils.showToast("Scan was uncessfull.", activity);
			}
			break;
		}
	}

	public void saveCreditCard() {

		Card card = new Card(etCreditCardNum.getText().toString(),
				Integer.parseInt(etMonth.getText().toString()),
				Integer.parseInt(etYear.getText().toString()), etCvc.getText()
						.toString());

		boolean validation = card.validateCard();
		if (validation) {
			//AndyUtils.showCustomProgressDialog(activity,
			//		getString(R.string.adding_payment), false, null);
			AndyUtils.showToast(getResources().getString(R.string.adding_payment), activity);

			new Stripe().createToken(card, AndyConstants.PUBLISHABLE_KEY,
					new TokenCallback() {

						public void onSuccess(Token token) {

							String lastFour = etCreditCardNum.getText()
									.toString().toString();
							lastFour = lastFour.substring(lastFour.length() - 4);
							addCard(token.getId(), lastFour);
							// finishProgress();
						}

						public void onError(Exception error) {
							AndyUtils.showToast("Error", activity);
							// finishProgress();
							AndyUtils.removeCustomProgressDialog();
						}
					});
		} else if (!card.validateNumber()) {
			// handleError("The card number that you entered is invalid");
			AndyUtils.showToast("The card number that you entered is invalid",
					activity);
		} else if (!card.validateExpiryDate()) {
			// handleError("");
			AndyUtils
					.showToast(
							"The expiration date that you entered is invalid",
							activity);
		} else if (!card.validateCVC()) {
			// handleError("");
			AndyUtils.showToast("The CVC code that you entered is invalid",
					activity);

		} else {
			// handleError("");
			AndyUtils.showToast(
					"The card details that you entered are invalid", activity);
		}
	}

	public String getType(String number) {
		if (!TextUtils.isBlank(number)) {
			if (TextUtils.hasAnyPrefix(number, PREFIXES_AMERICAN_EXPRESS)) {
				return AMERICAN_EXPRESS;
			} else if (TextUtils.hasAnyPrefix(number, PREFIXES_DISCOVER)) {
				return DISCOVER;
			} else if (TextUtils.hasAnyPrefix(number, PREFIXES_JCB)) {
				return JCB;
			} else if (TextUtils.hasAnyPrefix(number, PREFIXES_DINERS_CLUB)) {
				return DINERS_CLUB;
			} else if (TextUtils.hasAnyPrefix(number, PREFIXES_VISA)) {
				return VISA;
			} else if (TextUtils.hasAnyPrefix(number, PREFIXES_MASTERCARD)) {
				return MASTERCARD;
			} else {
				return UNKNOWN;
			}
		}
		return UNKNOWN;
	}

	private void setStatusText(final String string) {
		AppLog.Log(TAG, string);
	}

	private void addCard(String stripeToken, String lastFour) {
		// AppLog.Log(TAG, "Final token : " + peachToken.substring(3));
		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(getActivity()).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL, Lserver + AndyConstants.ServiceType.ADD_CARD);
		map.put(AndyConstants.Params.ID, id);
		map.put(AndyConstants.Params.TOKEN, token);
		map.put(AndyConstants.Params.STRIPE_TOKEN, stripeToken);
		map.put(AndyConstants.Params.LAST_FOUR, lastFour);
		// map.put(Const.Params.CARD_TYPE, type);
		// new HttpRequester(activity, map, Const.ServiceCode.ADD_CARD, this);
		requestQueue.add(new VolleyHttpRequest(Method.POST, map,
				AndyConstants.ServiceCode.ADD_CARD, this, this));
	}

	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		AndyUtils.removeCustomProgressDialog();
		String[] part = response.split("\\{",2);
		response = "{" + part[1];



		switch (serviceCode) {
		case AndyConstants.ServiceCode.ADD_CARD:

			if (new ParseContent(activity).isSuccess(response)) {
				AndyUtils.showToast(getResources().getString(R.string.text_add_card_scucess),
						activity);
				activity.startActivity(new Intent(activity,
						MapActivity.class));
			} else
				AndyUtils.showToast(
						getString(R.string.text_not_add_card_unscucess),
						activity);
			activity.finish();
			break;
		default:
			break;
		}
	}



	@Override
	public void onErrorResponse(VolleyError error) {
		// TODO Auto-generated method stub
		AppLog.Log(AndyConstants.TAG, error.getMessage());
	}
}
