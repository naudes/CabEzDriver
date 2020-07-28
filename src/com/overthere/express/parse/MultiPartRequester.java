 package com.overthere.express.parse;

import java.io.File;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MultipartEntityBuilder;
//import com.overthere.express.utills.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.AsyncTask;
import android.view.KeyEvent;
import android.widget.Toast;
import com.overthere.express.utills.AndyConstants;
import com.overthere.express.utills.AndyUtils;
import com.overthere.express.utills.AppLog;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.os.AsyncTaskCompatHoneycomb;


 /**
 * @author Hardik A Bhalodi
 */
 public class MultiPartRequester {

	private Map<String, String> map;
	private AsyncTaskCompleteListener mAsynclistener;
	private int serviceCode;
	private HttpClient httpclient;
	private Activity activity;
	private AsyncHttpRequest request;

	public MultiPartRequester(Activity activity, Map<String, String> map,
			int serviceCode, AsyncTaskCompleteListener asyncTaskCompleteListener) {
		this.map = map;
		this.serviceCode = serviceCode;
		this.activity = activity;

		// is Internet Connection Available...

		if (AndyUtils.isNetworkAvailable(activity)) {
			mAsynclistener = (AsyncTaskCompleteListener) asyncTaskCompleteListener;
			request = (AsyncHttpRequest) new AsyncHttpRequest().execute(map
					.get("url"));
		} else {
			showToast("Network is not available!!!");
		}

	}

	class AsyncHttpRequest extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {
			map.remove("url");
			try {
				//HttpParams myParams = new BasicHttpParams();
				//HttpConnectionParams.setConnectionTimeout(myParams, 100000);
				//HttpConnectionParams.setSoTimeout(myParams, 100000);
				//HttpClient httpClient = new DefaultHttpClient(myParams);
				//HttpPost httppost = new HttpPost(urls[0]);

				HttpPost postMethod = new HttpPost(
						AndyConstants.ServiceType.REQUEST_LOCATION_UPDATE);


				HttpPost httppost = new HttpPost(urls[0]);
				httpclient = new DefaultHttpClient();

				HttpConnectionParams.setConnectionTimeout(
						httpclient.getParams(), 60000);
				showToast("Network is not available!!!");
				MultipartEntityBuilder builder = MultipartEntityBuilder.create();

				for (String key : map.keySet()) {
					AppLog.Log(key, map.get(key));
					if (key.equalsIgnoreCase(AndyConstants.Params.PICTURE)) {
						if (!TextUtils.isEmpty(map.get(key))) {
							File f = new File(map.get(key));

							builder.addBinaryBody(key, f,
									ContentType.MULTIPART_FORM_DATA,
									f.getName());
						}
					} else {
						builder.addTextBody(key, map.get(key), ContentType
								.create("text/plain", MIME.UTF8_CHARSET));
					}
					// System.out.println(key + "---->" + map.get(key));

				}
				httppost.setEntity(builder.build());

				ActivityManager manager = (ActivityManager) activity
						.getSystemService(Context.ACTIVITY_SERVICE);

				if (manager.getMemoryClass() < 25) {
					System.gc();
				}
				HttpResponse response = httpclient.execute(httppost);

				String responseBody = EntityUtils.toString(
						response.getEntity(), "UTF-8");

				return responseBody;

			} catch (Exception e) {
				e.printStackTrace();
			} catch (OutOfMemoryError oume) {
				System.gc();

				Toast.makeText(
						activity.getParent().getParent(),
						"Run out of memory please colse the other background apps and try again!",
						Toast.LENGTH_LONG).show();
			} finally {
				if (httpclient != null)
					httpclient.getConnectionManager().shutdown();

			}

			return null;

		}

		@Override
		protected void onPostExecute(String response) {

			if (mAsynclistener != null) {
				mAsynclistener.onTaskCompleted(response, serviceCode);
			}

		}
	}

	private void showToast(String msg) {
	//	Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
	}

	public void cancelTask() {

		request.cancel(true);
		// System.out.println("task is canelled");

	}

}



