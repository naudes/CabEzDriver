
package com.overthere.express;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.overthere.express.adapter.HistoryAdapter;
import com.overthere.express.base.ActionBarBaseActivitiy;
import com.overthere.express.model.History;
import com.overthere.express.parse.AsyncTaskCompleteListener;
import com.overthere.express.parse.ParseContent;
import com.overthere.express.parse.VolleyHttpRequest;
import com.overthere.express.utills.AndyConstants;
import com.overthere.express.utills.AndyUtils;
import com.overthere.express.utills.AppLog;
import com.overthere.express.utills.PreferenceHelper;
import com.hb.views.PinnedSectionListView;

/**
 * @author Kishan H Dhamat
 * 
 */
public class HistoryActivity extends ActionBarBaseActivitiy implements
		OnItemClickListener, AsyncTaskCompleteListener {

	private HistoryAdapter historyAdapter;
	private ArrayList<History> historyList;
	private PreferenceHelper preferenceHelper;
	private ParseContent parseContent;
	private ImageView tvEmptyHistory;
	private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();
	private PinnedSectionListView lvHistory;
	private ArrayList<Date> dateList = new ArrayList<Date>();
	private ArrayList<History> historyListOrg;
	private RequestQueue requestQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		// getSupportActionBar().setTitle(getString(R.string.text_history));
		// getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// getSupportActionBar().setHomeButtonEnabled(true);

		requestQueue = Volley.newRequestQueue(this);
		lvHistory = (PinnedSectionListView) findViewById(R.id.lvHistory);
		tvEmptyHistory = (ImageView) findViewById(R.id.tvHistoryEmpty);
		//lvHistory.setOnItemClickListener(this);
		/* no payment yet, no invoice shown*/
		historyList = new ArrayList<History>();
		preferenceHelper = new PreferenceHelper(this);
		dateList = new ArrayList<Date>();
		parseContent = new ParseContent(this);
		historyListOrg = new ArrayList<History>();
		setActionBarTitle(getResources().getString(R.string.text_history));
		setActionBarIcon(R.drawable.ub__nav_history);

		getHistory();
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

	private void getHistory() {
		if (!AndyUtils.isNetworkAvailable(this)) {
			AndyUtils.showToast(
					getResources().getString(R.string.toast_no_internet), this);
			return;
		}
		AndyUtils.showCustomProgressDialog(this, "",
				getResources().getString(R.string.progress_getting_history),
				false);
		AppLog.Log("Histoy", "UserId : " + preferenceHelper.getUserId()
				+ " Tocken : " + preferenceHelper.getSessionToken());
		HashMap<String, String> map = new HashMap<String, String>();
		String Lserver = new PreferenceHelper(this).getString("local_host");
		// Lserver = "http://"+Lserver+"/";
		map.put(AndyConstants.URL,
				Lserver + AndyConstants.ServiceType.HISTORY + AndyConstants.Params.ID
						+ "=" + preferenceHelper.getUserId() + "&"
						+ AndyConstants.Params.TOKEN + "="
						+ preferenceHelper.getSessionToken());

		requestQueue.add(new VolleyHttpRequest(Method.GET, map,
				AndyConstants.ServiceCode.HISTORY, this, this));
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		if (mSeparatorsSet.contains(position)) return;

		History history = historyListOrg.get(position);

		/*showBillDialog(history.getBasePrice(), history.getTotal(),
				history.getDistanceCost(), history.getTimecost(),
				history.getDistance(), history.getTime(),
				history.getReferralBonus(), history.getPromoBonus(),
				getString(R.string.text_close)); */
		showBillDialog(history.getBasePrice(), history.getTotal(),
				history.getDistanceCost(), history.getTimecost(),
				history.getDistance(), history.getTime(), "0","0",
				getString(R.string.text_close));
	}

	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		AndyUtils.removeCustomProgressDialog();
		String[] part = response.split("\\{",2);
		response = "{" + part[1];


		switch (serviceCode) {
		case AndyConstants.ServiceCode.HISTORY:
			AppLog.Log("TAG", "History Response :" + response);
			if (!parseContent.isSuccess(response)) {
				return;
			}
			historyListOrg.clear();
			historyList.clear();
			dateList.clear();
			parseContent.parseHistory(response, historyList);

			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				final Calendar cal = Calendar.getInstance();

				parseContent.parseHistory(response, historyList);

				/* Collections.sort(historyList, new Comparator<History>() {
					@Override
					public int compare(History o1, History o2) {

						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"yyyy-MM-dd hh:mm:ss");
						try {
							// date1 = dateFormat.parse(o1.getDate());
							// date2 = dateFormat.parse(o2.getDate());

							String firstStrDate = o1.getDate();
							String secondStrDate = o2.getDate();

							Log.i("firstStrDate 1", "" + firstStrDate);
							Log.i("secondStrDate 2", "" + secondStrDate);
							Date date2 = dateFormat.parse(secondStrDate);
							Date date1 = dateFormat.parse(firstStrDate);
							Log.i("Date 1", "" + date1);
							Log.i("Date 2", "" + date2);
							int value = date2.compareTo(date1);
							Log.i("Value", "" + value);
							return value;
						} catch (ParseException e) {
							e.printStackTrace();
						}
						return 0;
					}
				}); */
				HashSet<Date> listToSet = new HashSet<Date>();

				for (int i = 0; i < historyList.size(); i++) {
					AppLog.Log("date", historyList.get(i).getDate() + "");
					if (listToSet.add(sdf.parse(historyList.get(i).getDate()))) {
						dateList.add(sdf.parse(historyList.get(i).getDate()));
					}

				}

				for (int i = 0; i < dateList.size(); i++) {

					cal.setTime(dateList.get(i));
					History item = new History();
					item.setDate(sdf.format(dateList.get(i)));
					historyListOrg.add(item);

					mSeparatorsSet.add(historyListOrg.size() - 1);
					for (int j = 0; j < historyList.size(); j++) {
						Calendar messageTime = Calendar.getInstance();
						messageTime.setTime(sdf.parse(historyList.get(j)
								.getDate()));
						if (cal.getTime().compareTo(messageTime.getTime()) == 0) {
							historyListOrg.add(historyList.get(j));
						}
					}
				}
				if (historyList.size() > 0) {
					lvHistory.setVisibility(View.VISIBLE);
					tvEmptyHistory.setVisibility(View.GONE);

				} else {
					lvHistory.setVisibility(View.GONE);
					tvEmptyHistory.setVisibility(View.VISIBLE);
				}
				historyAdapter = new HistoryAdapter(this, historyListOrg,
						mSeparatorsSet);
				lvHistory.setAdapter(historyAdapter);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnActionNotification:
			onBackPressed();
			overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_right);
			break;

		default:
			break;
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		// TODO Auto-generated method stub
		AppLog.Log("TAG", error.getMessage());

	}
}
