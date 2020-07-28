package com.overthere.express;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.overthere.express.base.ActionBarBaseActivitiy;
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

/**
 * @author Hardik A Bhalodi
 */
public class UberAddPaymentActivity extends ActionBarBaseActivitiy
// implements PWTokenObtainedListener, PWTransactionListener
{
	private static final String TAG = "UberAddPaymentActivity";
	private Button btnAddPayment, btnPaymentSkip;
	//private ImageView btnScan;
	private final int MY_SCAN_REQUEST_CODE = 111;
	private EditText etCreditCardNum, etCvc, etYear, etMonth;
	private String patternVisa = "^4[0-9]{12}(?:[0-9]{3})?$";
	private String patternMasterCard = "^5[1-5][0-9]{14}$";
	private String patternAmericanExpress = "^3[47][0-9]{13}$";
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

	private EditText etHolder;
	private RequestQueue requestQueue;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_payment);
		setActionBarTitle(getResources().getString(R.string.text_payment));
		setActionBarIcon(R.drawable.ic_payment);

		requestQueue = Volley.newRequestQueue(this);
		btnAddPayment = (Button) findViewById(R.id.btnAddPayment);
		btnPaymentSkip = (Button) findViewById(R.id.btnPaymentSkip);
		btnPaymentSkip.setVisibility(View.GONE);
		//btnScan = (ImageView) findViewById(R.id.btnScan);
		etCreditCardNum = (EditText) findViewById(R.id.edtRegisterCreditCardNumber);
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
				if (etCreditCardNum.getText().toString().length() == 16) {
					etMonth.requestFocus();
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
		etCvc = (EditText) findViewById(R.id.edtRegistercvc);
		etYear = (EditText) findViewById(R.id.edtRegisterexpYear);
		etMonth = (EditText) findViewById(R.id.edtRegisterexpMonth);
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
		etHolder = (EditText) findViewById(R.id.edtRegisterCreditCardHolder);
		//btnScan.setOnClickListener(this);
		btnAddPayment.setOnClickListener(this);
		findViewById(R.id.btnPaymentSkip).setOnClickListener(this);
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

			case R.id.btnAddPayment:
				if (isValidate()) {
					saveCreditCard();
				}
				break;
			//case R.id.btnScan:
			//	scan();
			//	break;
			case R.id.btnActionNotification:
				onBackPressed();
				break;
			default:
				break;
		}
	}


	protected boolean isValidate() {
		if (etCreditCardNum.getText().length() == 0
				|| etCvc.getText().length() == 0
				|| etMonth.getText().length() == 0
				|| etYear.getText().length() == 0) {
			AndyUtils.showToast("Enter Proper data", this);
			return false;
		}
		return true;
	}

	private void scan() {
		Intent scanIntent = new Intent(this, CardIOActivity.class);

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

		// hides the manual entry button
		// if set, developers should provide their own manual entry
		// mechanism in
		// the app
		scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, false); // default:
		// false

		// MY_SCAN_REQUEST_CODE is arbitrary and is only used within this
		// activity.
		startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
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

						// Never log a raw card number. Avoid displaying it, but if
						// necessary use getFormattedCardNumber()
						// resultStr = "Card Number: " +
						// scanResult.getRedactedCardNumber()
						// + "\n";
						etCreditCardNum.setText(scanResult.getRedactedCardNumber());

						// Do something with the raw number, e.g.:
						// myService.setCardNumber( scanResult.cardNumber );

						if (scanResult.isExpiryValid()) {
							// resultStr += "Expiration Date: " +
							// scanResult.expiryMonth
							// +
							// "/"
							// + scanResult.expiryYear + "\n";
							etMonth.setText(scanResult.expiryMonth + "");
							etYear.setText(scanResult.expiryYear + "");
						}

						if (scanResult.cvv != null) {
							// Never log or display a CVV
							// resultStr += "CVV has " + scanResult.cvv.length()
							// + " digits.\n";
							etCvc.setText(scanResult.cvv);
						}

						// if (scanResult.postalCode != null) {
						// resultStr += "Postal Code: " + scanResult.postalCode +
						// "\n";
						// }
					} else {
						// resultStr = "Scan was canceled.";
						AndyUtils.showToast("Scan was canceled.", this);
					}
				} else {
					AndyUtils.showToast("Scan was uncessfull.", this);
				}
				break;

		}

	}

	public void saveCreditCard() {


		Card card = new Card(
				etCreditCardNum.getText().toString(),
				Integer.parseInt(etMonth.getText().toString()),
				Integer.parseInt(etYear.getText().toString()), etCvc.getText()
				.toString());

		//card.setCurrency(form.getCurrency());

		boolean validation = card.validateCard();
		if (validation)  {
			//AndyUtils.showCustomProgressDialog(this,
				//	@"ADDING CARD", false, null);
			AndyUtils.showToast("Adding Card", this);
			new Stripe().createToken(card, AndyConstants.PUBLISHABLE_KEY,
					new TokenCallback() {
						public void onSuccess(Token token) {
							// getTokenList().addToList(token);
							// AndyUtils.showToast(token.getId(), activity);
							String lastFour = etCreditCardNum.getText()
									.toString().toString();
							lastFour = lastFour.substring(lastFour.length() - 4);
							addCard(token.getId(), lastFour);
							// finishProgress();
						}

						public void onError(Exception error) {
							AndyUtils.showToast("Error",
									UberAddPaymentActivity.this);
							// finishProgress();
							AndyUtils.removeCustomProgressDialog();
						}
					});
		} else if (!card.validateNumber()) {
			// handleError("The card number that you entered is invalid");
			AndyUtils.showToast("The card number that you entered is invalid",
					this);
		} else if (!card.validateExpiryDate()) {
			// handleError("");
			AndyUtils.showToast(
					"The expiration date that you entered is invalid", this);
		} else if (!card.validateCVC()) {
			// handleError("");
			AndyUtils.showToast("The CVC code that you entered is invalid",
					this);

		} else {
			// handleError("");
			AndyUtils.showToast(
					"The card details that you entered are invalid", this);
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

	private void addCard(String stripeToken, String lastFour) {
		// AppLog.Log(TAG, "Final token : " + peachToken.substring(3));
		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(this).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL, Lserver + AndyConstants.ServiceType.ADD_CARD);
		map.put(AndyConstants.Params.ID, new PreferenceHelper(this).getUserId());
		map.put(AndyConstants.Params.TOKEN,
				new PreferenceHelper(this).getSessionToken());
		map.put(AndyConstants.Params.STRIPE_TOKEN, stripeToken);
		map.put(AndyConstants.Params.LAST_FOUR, lastFour);
		map.put(AndyConstants.Params.CARD_TYPE, type);
		// new HttpRequester(this, map, Const.ServiceCode.ADD_CARD, this);
		requestQueue.add(new VolleyHttpRequest(Method.POST, map,
				AndyConstants.ServiceCode.ADD_CARD, this, this));
	}

	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		AppLog.Log(AndyConstants.TAG, response);
		String[] part = response.split("\\{",2);
		response = "{" + part[1];

		//AndyUtils.removeCustomProgressDialog();
		//super.onTaskCompleted(response, serviceCode);
		switch (serviceCode) {
			case AndyConstants.ServiceCode.ADD_CARD:

				if (new ParseContent(this).isSuccess(response)) {
					AndyUtils.showToast(getResources().getString(R.string.text_add_card_scucess),
							this);
					setResult(Activity.RESULT_OK);
				} else {
					AndyUtils.showToast(
							getString(R.string.text_not_add_card_unscucess), this);
					setResult(Activity.RESULT_CANCELED);
				}
				finish();
				break;
			default:
				break;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	private void setStatusText(final String string) {
		AppLog.Log(TAG, string);
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		// TODO Auto-generated method stub
		AppLog.Log(AndyConstants.TAG, error.getMessage());

	}



}
