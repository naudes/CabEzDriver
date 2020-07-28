package com.overthere.express;

import java.io.IOException;
import java.util.HashMap;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.overthere.express.base.ActionBarBaseActivitiy;
import com.overthere.express.component.MyFontButton;
import com.overthere.express.db.DBHelper;
import com.overthere.express.model.User;
import com.overthere.express.parse.AsyncTaskCompleteListener;
import com.overthere.express.parse.ParseContent;
import com.overthere.express.parse.VolleyHttpRequest;
import com.overthere.express.utills.AndyConstants;
import com.overthere.express.utills.AndyUtils;
import com.overthere.express.utills.AppLog;
import com.overthere.express.utills.PreferenceHelper;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Switch;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * @author Kishan H Dhamat
 * 
 */
public class SettingActivity extends ActionBarBaseActivitiy implements
		 OnCheckedChangeListener, AsyncTaskCompleteListener {
	private Switch switchSetting, switchSound;
	private PreferenceHelper preferenceHelper;
	private ParseContent parseContent;
	private RequestQueue requestQueue;
	private DBHelper dbHelper;
	private Spinner  carMarker, seatNumber, vehicleType, loadWeight, loadLength;
	static final String[] Types = new String[] {"Sedan","Silver Service", "Station Wagon", "Wheel Chair", "Mini Van","Others"};
//	static final String[] Types = new String[] {"Sedan","Silver Service", "Station Wagon", "Wheel Chair", "Mini Van","Business Partner","Others"};
	static final String[] CarMarkers = new String[] {"Toyota","Ford", "Holden", "Mitsubishi", "Others"};
	static final String[] SeatNumber = new String[] {"4", "7", "11", "Others"};
	static final String[] RemovalVehicles = new String[] {"Pickup","Van", "Truck"};
	static final String[] TowVehicles = new String[] {"Fork Truck","Flat Truck", "Havey Truck"};
	static final String[] LoadWeight = new String[] {"1", "2", "3", "5", "10", "more"};
	static final String[] LoadLength = new String[] {"1.5", "2", "2.5", "3", "3.5", "4", "more"};
	//TextView	tvServiceType;
	ArrayAdapter<String> adapterVehicleArray;

	Switch SWcigrates, SWlighter, SWjumpLeads, SWtyreChange, SWfuelBottle,
	       SWchgBattery, SWchgTyre, SWchgBelt, SWwaterLeaking, SWoilLeaking,
	       SWengin, SWnotMoving;
	AutoCompleteTextView regoNumber;
	String serviceType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbHelper = new DBHelper(getApplicationContext());
		User user = dbHelper.getUser();
		setActionBarTitle(getResources().getString(R.string.text_setting));
		setActionBarIcon(R.drawable.promotion);
		preferenceHelper = new PreferenceHelper(this);
		parseContent = new ParseContent(this);
		requestQueue = Volley.newRequestQueue(this);

        String serviceName = user.getServiceName();
		serviceType = user.getServiceType();
		//serviceType = "1";

		setContentView(R.layout.taxiservice_settings);
		/*if(pos_type == 5) {
			setContentView(R.layout.activity_setting_ads);
			return;
		} else {
		        setContentView(R.layout.taxiservice_settings);}
		        */
		//RelativeLayout relayout = (RelativeLayout) this.findViewById(R.id.Relayout);
		TextView tv1 = (TextView) this.findViewById(R.id.textViewmaker);
		Spinner tv2 = (Spinner) this.findViewById(R.id.carMaker);
		TextView tv3 = (TextView) this.findViewById(R.id.textViewnumber);
		AutoCompleteTextView tv4 = (AutoCompleteTextView) this.findViewById(R.id.regoNumber);
		TextView tv5 = (TextView) this.findViewById(R.id.textViewseat);
		Spinner tv6 = (Spinner) this.findViewById(R.id.seatNumber);
		TextView tv7 = (TextView) this.findViewById(R.id.textViewlead);
		Switch tv8 = (Switch) this.findViewById(R.id.jumpLeads);
		TextView tv9 = (TextView) this.findViewById(R.id.textViewtyre);
		Switch tv10 = (Switch) this.findViewById(R.id.tyreChange);
		final ArrayAdapter<String> adapterVehicleArray = new ArrayAdapter<String>(this, R.layout.spinner_servicetype, Types);

		vehicleType = (Spinner) this.findViewById(R.id.serviceType);
		vehicleType.setAdapter(adapterVehicleArray);

		//tvServiceType.setText(serviceName);

		vehicleType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
									   int position, long id) {
				if (position==15) {
					//setContentView(R.layout.activity_setting_ads);
					tv1.setVisibility(View.INVISIBLE);
					tv2.setVisibility(View.INVISIBLE);
					tv3.setVisibility(View.INVISIBLE);
					tv4.setVisibility(View.INVISIBLE);
					tv5.setVisibility(View.INVISIBLE);
					tv6.setVisibility(View.INVISIBLE);
					tv7.setVisibility(View.INVISIBLE);
					tv8.setVisibility(View.INVISIBLE);
					tv9.setVisibility(View.INVISIBLE);
					tv10.setVisibility(View.INVISIBLE);
					//return false;
				} else {
					tv1.setVisibility(View.VISIBLE);
					tv2.setVisibility(View.VISIBLE);
					tv3.setVisibility(View.VISIBLE);
					tv4.setVisibility(View.VISIBLE);
					tv5.setVisibility(View.VISIBLE);
					tv6.setVisibility(View.VISIBLE);
					tv7.setVisibility(View.VISIBLE);
					tv8.setVisibility(View.VISIBLE);
					tv9.setVisibility(View.VISIBLE);
					tv10.setVisibility(View.VISIBLE);
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
				// TODO Auto-generated method stub
			}
		});
		int pos_type = preferenceHelper.getInt(AndyConstants.Params.POS_VEHICLE_TYPE);
		vehicleType.setSelection(pos_type);
		final ArrayAdapter<String> adapterCarMarkerArray = new ArrayAdapter<String>(this, R.layout.spinner_carmaker, CarMarkers);
		carMarker = (Spinner) this.findViewById(R.id.carMaker);
		carMarker.setAdapter(adapterCarMarkerArray);
		carMarker.setSelection(preferenceHelper.getInt(AndyConstants.Params.POS_CAR_MAKER));
		final ArrayAdapter<String> adapterSeatNumberArray = new ArrayAdapter<String>(this, R.layout.spinner_servicetype, SeatNumber);
		regoNumber = (AutoCompleteTextView) this.findViewById(R.id.regoNumber);
		regoNumber.setText(preferenceHelper.getString(AndyConstants.Params.REGO_NUMBER));
		seatNumber = (Spinner) this.findViewById(R.id.seatNumber);
		seatNumber.setAdapter(adapterSeatNumberArray);
		seatNumber.setSelection(preferenceHelper.getInt(AndyConstants.Params.POS_SEAT_NUMBER));

		SWcigrates = (Switch) this.findViewById(R.id.cigrates);
		//SWstationWagon.setVisibility(View.INVISIBLE);
		SWcigrates.setChecked(preferenceHelper.getBoolean(AndyConstants.Params.CIGRATES));
		SWcigrates.setOnCheckedChangeListener(this);
		SWlighter = (Switch) this.findViewById(R.id.lighter);
		//SWwheelChair.setVisibility(View.INVISIBLE);
		SWlighter.setChecked(preferenceHelper.getBoolean(AndyConstants.Params.LIGHTER));
		SWlighter.setOnCheckedChangeListener(this);
		SWjumpLeads = (Switch) this.findViewById(R.id.jumpLeads);
		SWjumpLeads.setChecked(preferenceHelper.getBoolean(AndyConstants.Params.JUMP_LEADS));
		SWjumpLeads.setOnCheckedChangeListener(this);
		SWtyreChange = (Switch) this.findViewById(R.id.tyreChange);
		SWtyreChange.setChecked(preferenceHelper.getBoolean(AndyConstants.Params.CHG_TYRE));
		SWtyreChange.setOnCheckedChangeListener(this);

		/*switch (serviceType) {
			case AndyConstants.ServiceTypes.TAXI_SERVICE:
				serviceName = "Taxi Service";
				break;
			case AndyConstants.ServiceTypes.TAXI_EXPRESS:
				serviceName = "Taxi Service&Express";
				break;
			case AndyConstants.ServiceTypes.MOBILE_MECHANIC:
				serviceName = "Mobile Mechanic";
				break;
			case AndyConstants.ServiceTypes.REMOVAL_SERVICE:
				serviceName = "Removal Service";
				adapterVehicleArray = new ArrayAdapter<String>(this, R.layout.spinner_carmaker, RemovalVehicles);
				break;
			case AndyConstants.ServiceTypes.TOW_SERVICE:
				serviceName = "Tow Service";
				adapterVehicleArray = new ArrayAdapter<String>(this, R.layout.spinner_carmaker, TowVehicles);
				break;
		} */
		/* switch (serviceType) {
			case AndyConstants.ServiceTypes.TAXI_EXPRESS:
			case AndyConstants.ServiceTypes.TAXI_SERVICE:
				setContentView(R.layout.taxiservice_settings);
			    tvServiceType = (TextView) this.findViewById(R.id.serviceType);
				tvServiceType.setText(serviceName);
				final ArrayAdapter<String> adapterCarMarkerArray = new ArrayAdapter<String>(this, R.layout.spinner_carmaker, CarMarkers);
				carMarker = (Spinner) this.findViewById(R.id.carMaker);
				carMarker.setAdapter(adapterCarMarkerArray);
				carMarker.setSelection(preferenceHelper.getInt(AndyConstants.Params.POS_CAR_MAKER));
				final ArrayAdapter<String> adapterSeatNumberArray = new ArrayAdapter<String>(this, R.layout.spinner_servicetype, SeatNumber);
			    regoNumber = (AutoCompleteTextView) this.findViewById(R.id.regoNumber);
				regoNumber.setText(preferenceHelper.getString(AndyConstants.Params.REGO_NUMBER));
				seatNumber = (Spinner) this.findViewById(R.id.seatNumber);
				seatNumber.setAdapter(adapterSeatNumberArray);
				seatNumber.setSelection(preferenceHelper.getInt(AndyConstants.Params.POS_SEAT_NUMBER));

				SWstationWagon = (Switch) this.findViewById(R.id.stationWagon);
				SWstationWagon.setVisibility(View.INVISIBLE);
				//SWstationWagon.setChecked(preferenceHelper.getBoolean(AndyConstants.Params.STATION_WAGON));
				//SWstationWagon.setOnCheckedChangeListener(this);
				SWwheelChair = (Switch) this.findViewById(R.id.wheelChair);
				SWwheelChair.setVisibility(View.INVISIBLE);
				//SWwheelChair.setChecked(preferenceHelper.getBoolean(AndyConstants.Params.WHEEL_CHAIR));
				//SWwheelChair.setOnCheckedChangeListener(this);
				SWjumpLeads = (Switch) this.findViewById(R.id.jumpLeads);
				SWjumpLeads.setChecked(preferenceHelper.getBoolean(AndyConstants.Params.JUMP_LEADS));
				SWjumpLeads.setOnCheckedChangeListener(this);

				break;
			case AndyConstants.ServiceTypes.MOBILE_MECHANIC:
				setContentView(R.layout.mobilemechanic_settings);
				tvServiceType = (TextView) this.findViewById(R.id.serviceType);
				tvServiceType.setText(serviceName);
				regoNumber = (AutoCompleteTextView) this.findViewById(R.id.regoNumber);
				regoNumber.setText(preferenceHelper.getString(AndyConstants.Params.REGO_NUMBER));
				SWjumpLeads = (Switch) this.findViewById(R.id.jumpLeads);
				SWjumpLeads.setChecked(preferenceHelper.getBoolean(AndyConstants.Params.JUMP_LEADS));
				SWjumpLeads.setOnCheckedChangeListener(this);
				SWfuelBottle = (Switch) this.findViewById(R.id.fuelBottle);
				SWfuelBottle.setChecked(preferenceHelper.getBoolean(AndyConstants.Params.FUEL_BOTTLE));
				SWfuelBottle.setOnCheckedChangeListener(this);
				SWchgBattery = (Switch) this.findViewById(R.id.changeBattery);
				SWchgBattery.setChecked(preferenceHelper.getBoolean(AndyConstants.Params.CHG_BATTERY));
				SWchgBattery.setOnCheckedChangeListener(this);
				SWchgTyre = (Switch) this.findViewById(R.id.changeTyre);
				SWchgTyre.setChecked(preferenceHelper.getBoolean(AndyConstants.Params.CHG_TYRE));
				SWchgTyre.setOnCheckedChangeListener(this);
				SWchgBelt = (Switch) this.findViewById(R.id.changeBelt);
				SWchgBelt.setChecked(preferenceHelper.getBoolean(AndyConstants.Params.CHG_BELT));
				SWchgBelt.setOnCheckedChangeListener(this);
				SWwaterLeaking = (Switch) this.findViewById(R.id.waterLeaking);
				SWwaterLeaking.setChecked(preferenceHelper.getBoolean(AndyConstants.Params.WATER_LEAKING));
				SWwaterLeaking.setOnCheckedChangeListener(this);
				SWoilLeaking = (Switch) this.findViewById(R.id.oilLeaking);
				SWoilLeaking.setChecked(preferenceHelper.getBoolean(AndyConstants.Params.OIL_LEAKING));
				SWoilLeaking.setOnCheckedChangeListener(this);
				SWengin = (Switch) this.findViewById(R.id.enginNotStart);
				SWengin.setChecked(preferenceHelper.getBoolean(AndyConstants.Params.ENGIN_NOT_START));
				SWengin.setOnCheckedChangeListener(this);
				SWnotMoving = (Switch) this.findViewById(R.id.changeBelt);
				SWnotMoving.setChecked(preferenceHelper.getBoolean(AndyConstants.Params.CAR_NOT_MOVING));
				SWnotMoving.setOnCheckedChangeListener(this);
				break;

			case AndyConstants.ServiceTypes.REMOVAL_SERVICE:
			case AndyConstants.ServiceTypes.TOW_SERVICE:
				setContentView(R.layout.removalservice_settings);
				regoNumber = (AutoCompleteTextView) this.findViewById(R.id.regoNumber);
				regoNumber.setText(preferenceHelper.getString(AndyConstants.Params.REGO_NUMBER));
				tvServiceType = (TextView) this.findViewById(R.id.serviceType);
				tvServiceType.setText(serviceName);
				vehicleType = (Spinner) this.findViewById(R.id.vehicleType);
				//int pos = preferenceHelper.getInt(AndyConstants.Params.POS_VEHICLE_TYPE);
				vehicleType.setAdapter(adapterVehicleArray);
				vehicleType.setSelection(preferenceHelper.getInt(AndyConstants.Params.POS_VEHICLE_TYPE));
				final ArrayAdapter<String> adapterLoadWeightArray = new ArrayAdapter<String>(this, R.layout.spinner_carmaker, LoadWeight);
				loadWeight = (Spinner) this.findViewById(R.id.loadWeight);
				//pos = preferenceHelper.getInt(AndyConstants.Params.POS_LOAD_WEIGHT);
				loadWeight.setAdapter(adapterLoadWeightArray);
				loadWeight.setSelection(preferenceHelper.getInt(AndyConstants.Params.POS_LOAD_WEIGHT));
				final ArrayAdapter<String> adapterLoadLengthArray = new ArrayAdapter<String>(this, R.layout.spinner_carmaker, LoadLength);
				loadLength = (Spinner) this.findViewById(R.id.loadLength);
				//pos = preferenceHelper.getInt(AndyConstants.Params.POS_LOAD_LENGTH);
				loadLength.setAdapter(adapterLoadLengthArray);
				loadLength.setSelection(preferenceHelper.getInt(AndyConstants.Params.POS_LOAD_LENGTH));

				break;

			default:
				break;
		} */


		MyFontButton btnCancel = (MyFontButton) this.findViewById(R.id.btnCancelRequest);
		MyFontButton btnSave = (MyFontButton) this.findViewById(R.id.btnSendRequest);
		btnCancel.setOnClickListener(this);
		btnSave.setOnClickListener(this);

		//switchSetting = (Switch) findViewById(R.id.switchAvaibility);
		//switchSetting.setVisibility(View.GONE);
		//switchSound = (Switch) findViewById(R.id.switchSound);


		//switchSound.setChecked(preferenceHelper.getSoundAvailability());
		//switchSound.setOnCheckedChangeListener(this);


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
		String Lserver = new PreferenceHelper(this).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL,
				Lserver + AndyConstants.ServiceType.CHECK_STATE + AndyConstants.Params.ID
						+ "=" + preferenceHelper.getUserId() + "&"
						+ AndyConstants.Params.TOKEN + "="
						+ preferenceHelper.getSessionToken());
		// new HttpRequester(this, map, AndyConstants.ServiceCode.CHECK_STATE,
		// true, this);

		requestQueue.add(new VolleyHttpRequest(Method.GET, map,
				AndyConstants.ServiceCode.CHECK_STATE, this, this));
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
		String Lserver = new PreferenceHelper(this).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL, Lserver + AndyConstants.ServiceType.TOGGLE_STATE);
		map.put(AndyConstants.Params.ID, preferenceHelper.getUserId());
		map.put(AndyConstants.Params.TOKEN, preferenceHelper.getSessionToken());
		requestQueue.add(new VolleyHttpRequest(Method.POST, map,
				AndyConstants.ServiceCode.TOGGLE_STATE, this, this));

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

		case R.id.cigrates:
			preferenceHelper.putBoolean(AndyConstants.Params.CIGRATES,isChecked);
			break;
		case R.id.lighter:
			preferenceHelper.putBoolean(AndyConstants.Params.LIGHTER,isChecked);
			break;
		case R.id.jumpLeads:
			preferenceHelper.putBoolean(AndyConstants.Params.JUMP_LEADS,isChecked);
			break;
		case R.id.tyreChange:
				preferenceHelper.putBoolean(AndyConstants.Params.CHG_TYRE,isChecked);
				break;

		/*case R.id.changeBattery:
			preferenceHelper.putBoolean(AndyConstants.Params.CHG_BATTERY,isChecked);
			break;

		case R.id.changeBelt:
			preferenceHelper.putBoolean(AndyConstants.Params.CHG_BELT,isChecked);
			break;
		case R.id.waterLeaking:
			preferenceHelper.putBoolean(AndyConstants.Params.WATER_LEAKING,isChecked);
			break;
		case R.id.oilLeaking:
			preferenceHelper.putBoolean(AndyConstants.Params.OIL_LEAKING,isChecked);
			break;
		case R.id.enginNotStart:
			preferenceHelper.putBoolean(AndyConstants.Params.ENGIN_NOT_START,isChecked);
			break;
		case R.id.carNotMoving:
			preferenceHelper.putBoolean(AndyConstants.Params.CAR_NOT_MOVING,isChecked);
			break;
		case R.id.switchSound:
			AppLog.Log("Setting Activity Sound switch",
					"" + switchSound.isChecked());
			preferenceHelper.putSoundAvailability(switchSound.isChecked());

			break;
			*/
		default:
			break;
		}
	}

	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		AndyUtils.removeCustomProgressDialog();
		String[] part = response.split("\\{",2);
		response = "{" + part[1];

		switch (serviceCode) {
			case AndyConstants.ServiceCode.SAVE_SETTINGS:
				if(parseContent.isSuccess(response)) {
				AndyUtils.removeCustomProgressDialog();
				AndyUtils.showToast("Settings Saved", this);
				} else {
					AndyUtils.showToast("Settings Not Saved. Internet Problem", this);
				}
                int pos_type = preferenceHelper.getInt(AndyConstants.Params.POS_VEHICLE_TYPE);
				onBackPressed();
				if(pos_type == 15) {
                    startActivity(new Intent(SettingActivity.this,
                            BookingActivity.class));
                } else {

				}
				break;
		    case AndyConstants.ServiceCode.CHECK_STATE:
		    case AndyConstants.ServiceCode.TOGGLE_STATE:

			preferenceHelper.putIsActive(false);
			preferenceHelper.putDriverOffline(false);
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
			case R.id.btnCancelRequest:
		    case R.id.btnActionNotification:
			     onBackPressed();
				 overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_right);
			     break;
			case R.id.btnSendRequest:
				/* switch (serviceType) {
					case AndyConstants.ServiceTypes.TAXI_SERVICE:
					case AndyConstants.ServiceTypes.TAXI_EXPRESS:
						preferenceHelper.putString(AndyConstants.Params.CAR_MAKER, carMarker.getSelectedItem().toString());
						preferenceHelper.putString(AndyConstants.Params.REGO_NUMBER, regoNumber.getText().toString());
						preferenceHelper.putString(AndyConstants.Params.SEAT_NUMBER, seatNumber.getSelectedItem().toString());
						preferenceHelper.putInt(AndyConstants.Params.POS_CAR_MAKER, carMarker.getSelectedItemPosition());
						preferenceHelper.putInt(AndyConstants.Params.POS_SEAT_NUMBER, seatNumber.getSelectedItemPosition());

						break;

					case AndyConstants.ServiceTypes.MOBILE_MECHANIC:
						preferenceHelper.putString(AndyConstants.Params.REGO_NUMBER, regoNumber.getText().toString());
						break;
					case AndyConstants.ServiceTypes.REMOVAL_SERVICE:
					case AndyConstants.ServiceTypes.TOW_SERVICE:
						preferenceHelper.putString(AndyConstants.Params.REGO_NUMBER, regoNumber.getText().toString());
						preferenceHelper.putString(AndyConstants.Params.VEHICLE_TYPE, vehicleType.getSelectedItem().toString());
						preferenceHelper.putString(AndyConstants.Params.LOAD_WEIGHT, loadWeight.getSelectedItem().toString());
						preferenceHelper.putString(AndyConstants.Params.LOAD_LENGTH, loadLength.getSelectedItem().toString());
						preferenceHelper.putInt(AndyConstants.Params.POS_VEHICLE_TYPE, vehicleType.getSelectedItemPosition());
						preferenceHelper.putInt(AndyConstants.Params.POS_LOAD_WEIGHT, loadWeight.getSelectedItemPosition());
						preferenceHelper.putInt(AndyConstants.Params.POS_LOAD_LENGTH, loadLength.getSelectedItemPosition());

						break;
					default:
						break;
				} */
				preferenceHelper.putString(AndyConstants.Params.CAR_MAKER, carMarker.getSelectedItem().toString());
				preferenceHelper.putString(AndyConstants.Params.REGO_NUMBER, regoNumber.getText().toString());
				preferenceHelper.putString(AndyConstants.Params.SEAT_NUMBER, seatNumber.getSelectedItem().toString());
				preferenceHelper.putInt(AndyConstants.Params.POS_CAR_MAKER, carMarker.getSelectedItemPosition());
				preferenceHelper.putInt(AndyConstants.Params.POS_SEAT_NUMBER, seatNumber.getSelectedItemPosition());
				preferenceHelper.putInt(AndyConstants.Params.POS_VEHICLE_TYPE, vehicleType.getSelectedItemPosition());


				saveSettings();
				break;
		}

	}

	private void saveSettings() {
		if (!AndyUtils.isNetworkAvailable(this)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet), this);
			return;
		}

		AndyUtils.showCustomProgressDialog(this, "",
				getResources().getString(R.string.text_saving_settings),
				false);

		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(this).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL, Lserver + AndyConstants.ServiceType.SAVE_SETTINGS);
		map.put(AndyConstants.Params.ID, preferenceHelper.getUserId());
		map.put(AndyConstants.Params.TOKEN, preferenceHelper.getSessionToken());

		map.put(AndyConstants.Params.CAR_MAKER, preferenceHelper.getString(AndyConstants.Params.CAR_MAKER));
		map.put(AndyConstants.Params.REGO_NUMBER, preferenceHelper.getString(AndyConstants.Params.REGO_NUMBER));
		map.put(AndyConstants.Params.SEAT_NUMBER, preferenceHelper.getString(AndyConstants.Params.SEAT_NUMBER));
        //selected position start from 0, any taxi id is 1.
		map.put(AndyConstants.Params.VEHICLE_TYPE, Integer.toString(vehicleType.getSelectedItemPosition()+1));
		//map.put(AndyConstants.Params.LOAD_WEIGHT, preferenceHelper.getString(AndyConstants.Params.LOAD_WEIGHT));
		//map.put(AndyConstants.Params.LOAD_LENGTH, preferenceHelper.getString(AndyConstants.Params.LOAD_LENGTH));

		//map.put(AndyConstants.Params.BABY_SEAT, preferenceHelper.getBoolean(AndyConstants.Params.BABY_SEAT).toString());
		//map.put(AndyConstants.Params.STATION_WAGON, preferenceHelper.getBoolean(AndyConstants.Params.STATION_WAGON).toString());
		//map.put(AndyConstants.Params.WHEEL_CHAIR, preferenceHelper.getBoolean(AndyConstants.Params.WHEEL_CHAIR).toString());
		map.put(AndyConstants.Params.JUMP_LEADS, preferenceHelper.getBoolean(AndyConstants.Params.JUMP_LEADS).toString());
		//map.put(AndyConstants.Params.FUEL_BOTTLE, preferenceHelper.getBoolean(AndyConstants.Params.FUEL_BOTTLE).toString());

		//map.put(AndyConstants.Params.CHG_BATTERY, preferenceHelper.getBoolean(AndyConstants.Params.CHG_BATTERY).toString());
		map.put(AndyConstants.Params.CHG_TYRE, preferenceHelper.getBoolean(AndyConstants.Params.CHG_TYRE).toString());
		//map.put(AndyConstants.Params.CHG_BELT, preferenceHelper.getBoolean(AndyConstants.Params.CHG_BELT).toString());
		//map.put(AndyConstants.Params.WATER_LEAKING, preferenceHelper.getBoolean(AndyConstants.Params.WATER_LEAKING).toString());
		//map.put(AndyConstants.Params.OIL_LEAKING, preferenceHelper.getBoolean(AndyConstants.Params.OIL_LEAKING).toString());
		//map.put(AndyConstants.Params.ENGIN_NOT_START, preferenceHelper.getBoolean(AndyConstants.Params.ENGIN_NOT_START).toString());
		//map.put(AndyConstants.Params.CAR_NOT_MOVING, preferenceHelper.getBoolean(AndyConstants.Params.CAR_NOT_MOVING).toString());
		map.put(AndyConstants.Params.CIGRATES, preferenceHelper.getBoolean(AndyConstants.Params.CIGRATES).toString());
		map.put(AndyConstants.Params.LIGHTER, preferenceHelper.getBoolean(AndyConstants.Params.LIGHTER).toString());

		requestQueue.add(new VolleyHttpRequest(Method.POST, map,
				AndyConstants.ServiceCode.SAVE_SETTINGS, this, this));

	}
	@Override
	public void onErrorResponse(VolleyError error) {
		// TODO Auto-generated method stub
		AppLog.Log("TAG", error.getMessage());
	}

}
