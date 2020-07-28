
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
import com.overthere.express.adapter.BookingAdapter;
import com.overthere.express.base.ActionBarBaseActivitiy;
import com.overthere.express.model.Booking;
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
public class BookingActivity extends ActionBarBaseActivitiy implements
        OnItemClickListener, AsyncTaskCompleteListener {

    private BookingAdapter bookingAdapter;
    private ArrayList<Booking> bookingList;
    private PreferenceHelper preferenceHelper;
    private ParseContent parseContent;
    private ImageView tvEmptyBooking;
    private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();
    private PinnedSectionListView lvBooking;
    private ArrayList<Date> dateList = new ArrayList<Date>();
    private ArrayList<Booking> bookingListOrg;
    private RequestQueue requestQueue;
    private int Baction = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        // getSupportActionBar().setTitle(getString(R.string.text_history));
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setHomeButtonEnabled(true);

        requestQueue = Volley.newRequestQueue(this);
        lvBooking = (PinnedSectionListView) findViewById(R.id.lvBooking);
        tvEmptyBooking = (ImageView) findViewById(R.id.tvBookingEmpty);
        lvBooking.setOnItemClickListener(this);
        bookingList = new ArrayList<Booking>();
        preferenceHelper = new PreferenceHelper(this);
        dateList = new ArrayList<Date>();
        parseContent = new ParseContent(this);
        bookingListOrg = new ArrayList<Booking>();
        setActionBarTitle(getResources().getString(R.string.text_booking));
        setActionBarIcon(R.drawable.nav_about);

        getBooking();
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

    private void getBooking() {
        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(
                    getResources().getString(R.string.toast_no_internet), this);
            return;
        }
        AndyUtils.showCustomProgressDialog(this, "",
                getResources().getString(R.string.progress_getting_booking),
                false);
        AppLog.Log("Histoy", "UserId : " + preferenceHelper.getUserId()
                + " Tocken : " + preferenceHelper.getSessionToken());
        HashMap<String, String> map = new HashMap<String, String>();
        String Lserver = new PreferenceHelper(this).getString("local_host");
        // Lserver = "http://"+Lserver+"/";
        map.put(AndyConstants.URL,
                Lserver + AndyConstants.ServiceType.BOOKING + AndyConstants.Params.ID
                        + "=" + preferenceHelper.getUserId() + "&"
                        + AndyConstants.Params.TOKEN + "="
                        + preferenceHelper.getSessionToken());

        requestQueue.add(new VolleyHttpRequest(Method.GET, map,
                AndyConstants.ServiceCode.BOOKING, this, this));
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {
        if (mSeparatorsSet.contains(position))
            return;

        //Booking booking = bookingListOrg.get(position);
        Booking booking = bookingList.get(position);
        Baction = booking.getBookingApplied();

        showBookingDialog(booking.getpkupArea(), booking.getDatetTime(),
                booking.getdropoffArea(), booking.getbookingFare(),booking.getRequestID(),
                booking.getBookingApplied(), getString(R.string.text_close));

    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        AndyUtils.removeCustomProgressDialog();
        String[] part = response.split("\\{",2);
        response = "{" + part[1];


        switch (serviceCode) {
            case AndyConstants.ServiceCode.BOOKING:
                AppLog.Log("TAG", "Booking Response :" + response);
                if (!parseContent.isSuccess(response)) {
                    return;
                }
                bookingListOrg.clear();
                bookingList.clear();
                dateList.clear();
                parseContent.parseBooking(response, bookingList);
                    if (bookingList.size() > 0) {
                        lvBooking.setVisibility(View.VISIBLE);
                        tvEmptyBooking.setVisibility(View.GONE);

                    } else {
                        lvBooking.setVisibility(View.GONE);
                        tvEmptyBooking.setVisibility(View.VISIBLE);
                    }
                    //bookingAdapter = new BookingAdapter(this, bookingListOrg,mSeparatorsSet);
                    bookingAdapter = new BookingAdapter(this, bookingList,mSeparatorsSet);
                    lvBooking.setAdapter(bookingAdapter);

                break;
            case AndyConstants.ServiceCode.BOOKING_APPLY:
                if (!parseContent.isSuccess(response)) {
                    AndyUtils.showToast("Booking application failed.", this);
                } else {
                    if(Baction > 0) AndyUtils.showToast("Booking application cacelled.", this);
                    else AndyUtils.showToast("Booking application succeeded.", this);
                    onBackPressed();
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

    public void showBookingDialog(String PkArea, String PkTime,
                                  String DfArea, String BookingFare, final int ReqId, final int Applied, String btnTitle) {
        final Dialog mDialog = new Dialog(this,
                android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        mDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.booking_layout);
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        DecimalFormat perHourFormat = new DecimalFormat("0.0");
        String bookingFare = String.valueOf(decimalFormat.format(Double
                .parseDouble(BookingFare)));

//        String totalTmp = String.valueOf(decimalFormat.format(Double
//                .parseDouble(total)));



        ((TextView) mDialog.findViewById(R.id.tvPkupArea)).setText(PkArea);

        ((TextView) mDialog.findViewById(R.id.tvPkTime)).setText(PkTime);

        //((AutoCompleteTextView) mDialog.findViewById(R.id.tvTotal1)).setText(totalTmp);
        ((TextView) mDialog.findViewById(R.id.tvDfArea))
                .setText(DfArea);

       // ((TextView) mDialog.findViewById(R.id.tvBkFare)).setVisibility(View.GONE);
                //setText("$" + bookingFare);

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
        Button btnApply = (Button) mDialog
                .findViewById(R.id.btnBookingApply);
        if(Applied > 0) btnApply.setText("CANCEL");
        else btnApply.setText("APPLY");

        btnApply.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(Applied>0) {BookingCancel(ReqId);}
                else {BookingApply(ReqId);}
                mDialog.dismiss();
            }
        });

        mDialog.setCancelable(true);
        mDialog.show();


    }
    private void BookingApply(int ReqId) {

        String Lserver = new PreferenceHelper(this).getString("local_host");
        // Lserver = "http://"+Lserver+"/";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL, Lserver + AndyConstants.ServiceType.BOOKING_APPLY);
        map.put(AndyConstants.Params.ID, preferenceHelper.getUserId());
        map.put(AndyConstants.Params.REQUEST_ID,
                Integer.toString(ReqId));
        map.put(AndyConstants.Params.TOKEN, preferenceHelper.getSessionToken());

        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                AndyConstants.ServiceCode.BOOKING_APPLY, this, this));
    }
    private void BookingCancel(int ReqId) {

        String Lserver = new PreferenceHelper(this).getString("local_host");
        // Lserver = "http://"+Lserver+"/";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL, Lserver + AndyConstants.ServiceType.BOOKING_APPLY);
        map.put(AndyConstants.Params.ID, preferenceHelper.getUserId());
        map.put(AndyConstants.Params.REQUEST_ID,
                Integer.toString(ReqId));
        map.put(AndyConstants.Params.TOKEN, preferenceHelper.getSessionToken());

        requestQueue.add(new VolleyHttpRequest(Method.POST, map,
                AndyConstants.ServiceCode.BOOKING_APPLY, this, this));
    }


}
