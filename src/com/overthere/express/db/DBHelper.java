package com.overthere.express.db;

import android.content.Context;

import com.overthere.express.model.User;

public class DBHelper {

	private DBAdapter dbAdapter;

	public DBHelper(Context context) {
		dbAdapter = new DBAdapter(context);
	}

	public long createUser(User user) {
		long count = 0;
		dbAdapter.open();
		count = dbAdapter.createUser(user);
		dbAdapter.close();
		return count;

	}

	public User getUser() {
		User user = null;
		dbAdapter.open();
		user = dbAdapter.getUser();
		dbAdapter.close();
		return user;
	}

	public int deleteUser() {
		int count = 0;
		dbAdapter.open();
		count = dbAdapter.deleteUser();
		dbAdapter.close();
		return count;
	}

}
