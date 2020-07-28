
package com.overthere.express.adapter;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.overthere.express.R;
import com.overthere.express.model.Booking;
import com.overthere.express.widget.MyFontTextView;
import com.overthere.express.widget.MyFontTextViewBold;
import com.overthere.express.widget.MyFontTextViewTitle;
import com.hb.views.PinnedSectionListView.PinnedSectionListAdapter;


public class BookingAdapter extends BaseAdapter implements
        PinnedSectionListAdapter {

    private Activity activity;
    private ArrayList<Booking> list;
    private AQuery aQuery;
    private LayoutInflater inflater;
    private ViewHolder holder;
    private ImageOptions imageOptions;
    public static final int TYPE_ITEM = 0;
    public static final int TYPE_SEPARATOR = 1;
    private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;
    private DecimalFormat df;
    TreeSet<Integer> mSeparatorsSet;
    SimpleDateFormat simpleDateFormat;
    private SectionViewHolder sectionHolder;
    private Date currentDate = new Date();
    private Date returnDate = new Date();

    /* *
     * @param BookingActivity
     * @param bookingList
     */
    public BookingAdapter(Activity activity, ArrayList<Booking> bookingList,
                          TreeSet<Integer> mSeparatorsSet) {
        // TODO Auto-generated constructor stub
        this.activity = activity;
        this.list = bookingList;
        df = new DecimalFormat("00");
        this.mSeparatorsSet = mSeparatorsSet;

        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageOptions = new ImageOptions();
        imageOptions.fileCache = true;
        imageOptions.memCache = true;
        imageOptions.targetWidth = 200;
        imageOptions.fallback = R.drawable.user;
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return list.size();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @SuppressWarnings("deprecation")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case TYPE_ITEM:
                    convertView = inflater.inflate(R.layout.booking_item, parent,
                            false);
                    holder = new ViewHolder();
                    holder.tvPkArea = (MyFontTextViewBold) convertView
                            .findViewById(R.id.tvPkupArea);
                    holder.tvPkTime = (MyFontTextViewTitle) convertView
                            .findViewById(R.id.tvPkTime);
                    holder.tvDfArea = (MyFontTextViewBold) convertView
                            .findViewById(R.id.tvDfArea);
                   // holder.tvBkFare = (MyFontTextViewTitle) convertView
                      //      .findViewById(R.id.tvBkFare);

                    convertView.setTag(holder);
                    break;
                case TYPE_SEPARATOR:
                    sectionHolder = new SectionViewHolder();
                    convertView = inflater.inflate(R.layout.history_date_layout,
                            parent, false);

                    sectionHolder.tv = (TextView) convertView
                            .findViewById(R.id.tvDate);
                    convertView.setTag(sectionHolder);
                    break;
            }

        } else {
            switch (type) {
                case TYPE_ITEM:
                    holder = (ViewHolder) convertView.getTag();
                    break;
                case TYPE_SEPARATOR:
                    sectionHolder = (SectionViewHolder) convertView.getTag();
                    break;

            }
        }

        switch (type) {
            case TYPE_ITEM:
                aQuery = new AQuery(convertView);
                // Collections.sort(list.get(position).getDate(), myComparator);
                Booking booking = list.get(position);

                holder.tvPkArea.setText(booking.getpkupArea());
                holder.tvPkTime.setText(booking.getDatetTime());
                holder.tvDfArea.setText( booking.getdropoffArea());
                if(booking.getBookingApplied()>0) {
                    holder.tvPkArea.setTextColor(R.color.green);
                    holder.tvPkTime.setTextColor(R.color.green);
                    holder.tvDfArea.setTextColor(R.color.green);
                }
               // holder.tvBkFare.setText( "$" + booking.getbookingFare());

                break;
            case TYPE_SEPARATOR:

                Log.i("Return date", "" + list.get(position).getDatetTime());
                SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
                String date = df1.format(currentDate);

                if (list.get(position).getDatetTime().equals(date)) {
                    sectionHolder.tv
                            .setBackgroundResource(R.drawable.history_header_line_blue);
                    sectionHolder.tv.setTextColor(activity.getResources().getColor(
                            R.color.white));
                    sectionHolder.tv.setText(activity
                            .getString(R.string.text_today));
                } else if (list.get(position).getDatetTime()
                        .equals(getYesterdayDateString())) {
                    sectionHolder.tv
                            .setBackgroundResource(R.drawable.history_header_line_white);
                    sectionHolder.tv.setTextColor(activity.getResources().getColor(
                            R.color.color_blue));
                    sectionHolder.tv.setText(activity
                            .getString(R.string.text_yesterday));
                }

                else {
                    sectionHolder.tv
                            .setBackgroundResource(R.drawable.history_header_line_white);
                    sectionHolder.tv.setTextColor(activity.getResources().getColor(
                            R.color.color_blue));

                    try {
                        returnDate = df1.parse(list.get(position).getDatetTime());

                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    SimpleDateFormat pinnedDate = new SimpleDateFormat(
                            "dd-MMM-yyyy");
                    Log.i("New Date", pinnedDate.format(returnDate));
                    sectionHolder.tv.setText(pinnedDate.format(returnDate));

                }

                break;

        }

        return convertView;
    }

    private class ViewHolder {
       // MyFontTextView tvBio;
        MyFontTextViewBold tvPkArea, tvDfArea;
        MyFontTextViewTitle tvBkFare, tvPkTime;
       // ImageView ivIcon;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.hb.views.PinnedSectionListView.PinnedSectionListAdapter#
     * isItemViewTypePinned(int)
     */
    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == TYPE_SEPARATOR;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    class SectionViewHolder {
        TextView tv;
    }



    private String getYesterdayDateString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return dateFormat.format(cal.getTime());
    }

    Comparator<Date> myComparator = new Comparator<Date>() {
        public int compare(Date currentDate, Date returnDate) {
            return currentDate.compareTo(returnDate);
        }
    };

}
