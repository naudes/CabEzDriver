
package com.overthere.express;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeSet;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.overthere.express.adapter.CustomerAdapter;
import com.overthere.express.base.ActionBarBaseActivitiy;
import com.overthere.express.model.Customer;
import com.overthere.express.parse.AsyncTaskCompleteListener;
import com.overthere.express.parse.ParseContent;
import com.overthere.express.parse.VolleyHttpRequest;
import com.overthere.express.utills.AndyConstants;
import com.overthere.express.utills.AndyUtils;
import com.overthere.express.utills.AppLog;
import com.overthere.express.utills.PreferenceHelper;
import com.overthere.express.widget.MyFontTextView;
import com.overthere.express.widget.MyFontTextViewBold;
import com.hb.views.PinnedSectionListView;


public class MyCustomersActivity extends ActionBarBaseActivitiy implements
        OnItemClickListener, AsyncTaskCompleteListener {

    private CustomerAdapter customerAdapter;
    private ArrayList<Customer> customerList;
    private PreferenceHelper preferenceHelper;
    private ParseContent parseContent;
    private ImageView tvEmptyCustomer;
    private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();
    private PinnedSectionListView lvCustomer;
    private ArrayList<Date> dateList = new ArrayList<Date>();
    private ArrayList<Customer> customerListOrg;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_customers);
        requestQueue = Volley.newRequestQueue(this);
        lvCustomer = (PinnedSectionListView) findViewById(R.id.lvCustomer);
        tvEmptyCustomer = (ImageView) findViewById(R.id.tvCustomerEmpty);

        lvCustomer.setOnItemClickListener(this);
        customerList = new ArrayList<Customer>();
        preferenceHelper = new PreferenceHelper(this);

        dateList = new ArrayList<Date>();
        parseContent = new ParseContent(this);
        customerListOrg = new ArrayList<Customer>();
        setActionBarTitle(getResources().getString(R.string.text_my_customers));
        setActionBarIcon(R.drawable.ub__nav_history);

        getCustomers();
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

    private void getCustomers() {
        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(
                    getResources().getString(R.string.toast_no_internet), this);
            return;
        }
        AndyUtils.showCustomProgressDialog(this, "",
                getResources().getString(R.string.progress_getting_customers),
                false);
        AppLog.Log("Histoy", "UserId : " + preferenceHelper.getUserId()
                + " Tocken : " + preferenceHelper.getSessionToken());
        HashMap<String, String> map = new HashMap<String, String>();
        String Lserver = new PreferenceHelper(this).getString("local_host");
        // Lserver = "http://"+Lserver+"/";
        map.put(AndyConstants.URL,
                Lserver + AndyConstants.ServiceType.CUSTOMERS + AndyConstants.Params.ID
                        + "=" + preferenceHelper.getUserId() + "&"
                        + AndyConstants.Params.TOKEN + "="
                        + preferenceHelper.getSessionToken());

        requestQueue.add(new VolleyHttpRequest(Method.GET, map,
                AndyConstants.ServiceCode.CUSTOMERS, this, this));
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {
        if (mSeparatorsSet.contains(position))
            return;

        Customer customer = customerList.get(position);

        showCustomerDialog(customer.getpkupArea(), customer.getPkDate(),
                customer.getCustomerName(),
                getString(R.string.text_close));

    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        AndyUtils.removeCustomProgressDialog();
        String[] part = response.split("\\{",2);
        response = "{" + part[1];


        switch (serviceCode) {
            case AndyConstants.ServiceCode.CUSTOMERS:
                AppLog.Log("TAG", "Booking Response :" + response);
                if (!parseContent.isSuccess(response)) {
                    return;
                }
                customerListOrg.clear();
                customerList.clear();
                dateList.clear();
                parseContent.parseCustomer(response, customerList);
                if (customerList.size() > 0) {
                    lvCustomer.setVisibility(View.VISIBLE);
                    tvEmptyCustomer.setVisibility(View.GONE);

                } else {
                    lvCustomer.setVisibility(View.GONE);
                    tvEmptyCustomer.setVisibility(View.VISIBLE);
                }
                customerAdapter = new CustomerAdapter(this, customerList,mSeparatorsSet);
                lvCustomer.setAdapter(customerAdapter);

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

    public void showCustomerDialog(String PkArea, String PkTime,
                                  String customerName, String btnTitle) {
        final Dialog mDialog = new Dialog(this,
                android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        mDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.customer_layout);

        ((TextView) mDialog.findViewById(R.id.tvPkupArea)).setText(PkArea);

        ((TextView) mDialog.findViewById(R.id.tvPkDate)).setText(PkTime);

        ((TextView) mDialog.findViewById(R.id.tvCustomerName))
                .setText(customerName);

        Button btnConfirm = (Button) mDialog
                .findViewById(R.id.btnBookingDialogClose);
        if (!TextUtils.isEmpty(btnTitle)) {
            btnConfirm.setText(btnTitle);
        }

        btnConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        mDialog.setCancelable(true);
        mDialog.show();


    }



}
