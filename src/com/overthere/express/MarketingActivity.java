
package com.overthere.express;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeSet;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.overthere.express.base.ActionBarBaseActivitiy;
import com.overthere.express.component.MyFontButton;
import com.overthere.express.parse.AsyncTaskCompleteListener;
import com.overthere.express.parse.ParseContent;
import com.overthere.express.parse.VolleyHttpRequest;
import com.overthere.express.utills.AndyConstants;
import com.overthere.express.utills.AndyUtils;
import com.overthere.express.utills.AppLog;
import com.overthere.express.utills.PreferenceHelper;
import com.overthere.express.widget.MyFontTextViewBold;


/**
 * @author OverHere Team
 *
 */
public class MarketingActivity extends ActionBarBaseActivitiy implements
        OnItemClickListener, AsyncTaskCompleteListener {

    private PreferenceHelper preferenceHelper;
    private ParseContent parseContent;
    private RequestQueue requestQueue;
    private MyFontTextViewBold tvHailTotal, tvHailRewarded, tvHailBalance;
    private MyFontTextViewBold tvBookingTotal, tvBookingRewarded, tvBookingBalance;
    private MyFontTextViewBold tvHelpTotal, tvHelpRewarded, tvHelpBalance;
    private MyFontTextViewBold tvReferenceCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketing);
        requestQueue = Volley.newRequestQueue(this);
        preferenceHelper = new PreferenceHelper(this);
        parseContent = new ParseContent(this);
        setActionBarTitle(getResources().getString(R.string.text_marketing));
        setActionBarIcon(R.drawable.ub__nav_history);
        tvHailTotal= (MyFontTextViewBold) this.findViewById(R.id.tvHailTotal);
        tvHailRewarded= (MyFontTextViewBold) this.findViewById(R.id.tvHailRewarded);
        tvHailBalance= (MyFontTextViewBold) this.findViewById(R.id.tvHailBalance);
        tvBookingTotal= (MyFontTextViewBold) this.findViewById(R.id.tvBookingTotal);
        tvBookingRewarded= (MyFontTextViewBold) this.findViewById(R.id.tvBookingRewarded);
        tvBookingBalance= (MyFontTextViewBold) this.findViewById(R.id.tvBookingBalance);
        tvHelpTotal= (MyFontTextViewBold) this.findViewById(R.id.tvhelpTotal);
        tvHelpRewarded= (MyFontTextViewBold) this.findViewById(R.id.tvHelpRewarded);
        tvHelpBalance= (MyFontTextViewBold) this.findViewById(R.id.tvHelpBalance);
        tvReferenceCode = (MyFontTextViewBold)findViewById(R.id.tvReferenceCode);
        MyFontButton btnShare = (MyFontButton) this.findViewById(R.id.btnShare);
        MyFontButton btnMyCustomers = (MyFontButton) this.findViewById(R.id.btnMyCustomers);
        btnShare.setOnClickListener(this);
        btnMyCustomers.setOnClickListener(this);
        getAchievement();
        tvReferenceCode.setText(preferenceHelper.getString(AndyConstants.Params.REFERRAL_CODE));
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

    private void getAchievement() {
        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(
                    getResources().getString(R.string.toast_no_internet), this);
            return;
        }
        AndyUtils.showCustomProgressDialog(this, "",
                getResources().getString(R.string.progress_getting_achievement),
                false);
        AppLog.Log("Histoy", "UserId : " + preferenceHelper.getUserId()
                + " Tocken : " + preferenceHelper.getSessionToken());
        HashMap<String, String> map = new HashMap<String, String>();
        String Lserver = new PreferenceHelper(this).getString("local_host");
        // Lserver = "http://"+Lserver+"/";
        map.put(AndyConstants.URL,
                Lserver + AndyConstants.ServiceType.ACHIEVEMENT + AndyConstants.Params.ID
                        + "=" + preferenceHelper.getUserId() + "&"
                        + AndyConstants.Params.TOKEN + "="
                        + preferenceHelper.getSessionToken());

        requestQueue.add(new VolleyHttpRequest(Method.GET, map,
                AndyConstants.ServiceCode.ACHIEVEMENT, this, this));
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {


    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        AndyUtils.removeCustomProgressDialog();
        String[] part = response.split("\\{",2);
        response = "{" + part[1];

        switch (serviceCode) {
            case AndyConstants.ServiceCode.ACHIEVEMENT:
                AppLog.Log("TAG", "Achievement Response :" + response);
                if (parseContent.parseAchievement(response)) {
                    tvHailTotal.setText(preferenceHelper.getString(AndyConstants.Params.HAIL_TOTAL));
                    tvHailRewarded.setText(preferenceHelper.getString(AndyConstants.Params.HAIL_REWARDED));
                    tvHailBalance.setText(preferenceHelper.getString(AndyConstants.Params.HAIL_BALANCE));
                    tvBookingTotal.setText(preferenceHelper.getString(AndyConstants.Params.BOOKING_TOTAL));
                    tvBookingRewarded.setText(preferenceHelper.getString(AndyConstants.Params.BOOKING_REWARDED));
                    tvBookingBalance.setText(preferenceHelper.getString(AndyConstants.Params.BOOKING_BALANCE));
                    tvHelpTotal.setText(preferenceHelper.getString(AndyConstants.Params.HELP_TOTAL));
                    tvHelpRewarded.setText(preferenceHelper.getString(AndyConstants.Params.HELP_REWARDED));
                    tvHelpBalance.setText(preferenceHelper.getString(AndyConstants.Params.HELP_BALANCE));

                }
                else {
                     AndyUtils.showToast("Failed to get marketing achievement", this);
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
            case R.id.btnShare:
                 Intent sendIntent = new Intent();
					sendIntent.setAction(Intent.ACTION_SEND);
					sendIntent
							.putExtra(
									Intent.EXTRA_TEXT,
									"I am using "
											+ "OverHere"
											+ " App ! Why don't you try it out...\nInstall "
											+ "OverHere"
											+ " now !\nhttps://play.google.com/store/apps/details?id="
											+ getPackageName()
                                            + ", please use referral code "+ preferenceHelper.getString(AndyConstants.Params.REFERRAL_CODE)
                                            + " when you register. Thank you.");
					sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
							getString(R.string.app_name) + " App !");
					sendIntent.setType("text/plain");

					startActivity(Intent.createChooser(sendIntent,
							getString(R.string.text_share_app)));
                 break;
            case R.id.btnMyCustomers:
                startActivity(new Intent(this,
                        MyCustomersActivity.class));
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
