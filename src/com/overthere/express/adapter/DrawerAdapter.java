package com.overthere.express.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.overthere.express.R;
import com.overthere.express.model.ApplicationPages;

/**
 * @author Kishan H Dhamat
 * 
 */
public class DrawerAdapter extends BaseAdapter {


	private ArrayList<ApplicationPages> arrayListApplicationPages;
	private ViewHolder holder;
	private LayoutInflater inflater;
	private AQuery aQuery;
	private ImageOptions imageOptions;

	public DrawerAdapter(Context context,
			ArrayList<ApplicationPages> arrayListApplicationPages) {
		this.arrayListApplicationPages = arrayListApplicationPages;
		// items = context.getResources().getStringArray(R.array.menu_items);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		aQuery = new AQuery(context);
		imageOptions = new ImageOptions();
		imageOptions.fileCache = true;
		imageOptions.memCache = true;
		imageOptions.targetWidth = 200;
		imageOptions.fallback = R.drawable.ic_launcher;

	}

	@Override
	public int getCount() {
		return arrayListApplicationPages.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.drawer_item, parent, false);
			holder = new ViewHolder();
			holder.tvMenuItem = (TextView) convertView
					.findViewById(R.id.tvMenuItem);
			holder.ivMenuImage = (ImageView) convertView
					.findViewById(R.id.ivMenuImage);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tvMenuItem.setText(arrayListApplicationPages.get(position)
				.getTitle());
		if (position == 0) {
			aQuery.id(holder.ivMenuImage).image(R.drawable.nav_profile);
		} else if (position == 1) {
			aQuery.id(holder.ivMenuImage).image(R.drawable.ub__nav_history);
		} else if (position == 2) {
			aQuery.id(holder.ivMenuImage).image(R.drawable.promotion);
		} else if (position == 3) {
			aQuery.id(holder.ivMenuImage).image(R.drawable.nav_about);
		}  else if (position == 4) {
			aQuery.id(holder.ivMenuImage).image(R.drawable.nav_share);
		} else if (position == 5) {
			aQuery.id(holder.ivMenuImage).image(R.drawable.nav_payment);
		}
		else if (position == (arrayListApplicationPages.size() - 2)) {
			aQuery.id(holder.ivMenuImage).image(R.drawable.ub__nav_logout);
		} else if (position == (arrayListApplicationPages.size() - 1)) {
			aQuery.id(holder.ivMenuImage).image("");
		} else {
			if (TextUtils.isEmpty(arrayListApplicationPages.get(position)
					.getIcon())) {
				aQuery.id(holder.ivMenuImage).image(R.drawable.nav_about);
			} else {
				aQuery.id(holder.ivMenuImage).image(R.drawable.nav_about);
				//aQuery.id(holder.ivMenuImage).image(arrayListApplicationPages.get(position).getIcon());
			}

		}
		// holder.ivMenuImage.setImageResource(images[position]);
		return convertView;
	}

	class ViewHolder {
		public TextView tvMenuItem;
		public ImageView ivMenuImage;
	}

}
