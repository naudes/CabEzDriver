package com.overthere.express.parse;

public interface AsyncTaskCompleteListener {
	void onTaskCompleted(String response, int serviceCode);

}
