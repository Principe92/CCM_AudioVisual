/*
 * Copyright (C) 2014 Princewill Chibututu Okorie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package prince.app.ccm.tools;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import prince.app.ccm.delete.Activity_Schedule;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Public static class to handle asynchronous tasks
 * 
 * @author Princewill Chibututu Okorie
 *
 */
public class Task {
	public static final String TABLE_OPEN = "<thead";
	public static final String TABLE_CLOSE = "</thead";
	public static final String ROW_OPEN = "<tr";
	public static final String ROW_CLOSE = "</tr>";
	public static final String COL_OPEN = "<td";
	public static final String COL_CLOSE = "</td>";
	public static final String SPAN_OPEN = "<span";
	public static final String SPAN_CLOSE = "</span>";
	public static final String CMD_OPEN = "<";
	public static final String CMD_CLOSE = ">";

	private List<String> cookies;
	private HttpURLConnection conn;

	private final String USER_AGENT = "Mozilla/5.0";
	private String log;
	private static final String MAIN_PAGE = "http://www.iglesiamallorca.com/";
	private static final String TAG = Task.class.getSimpleName();
	private Context mContext;

	public Task(Context ct) {
		this.mContext = ct;
	}

	public void execute(String url, boolean transit) {
		new ParseURL(url).execute();
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		String URL;
		Boolean transit;

		public UserLoginTask(String url, boolean value) {
			this.URL = url;
			this.transit = value;
		}

		@Override
		protected void onPreExecute() {
			ScheduleSDK.UPDATED = false;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			String mResult;
			HttpGet mHttpGet;
			HttpResponse mResponse;
			HttpClient mHttpClient;
			Boolean mDataOK = false;
			StringBuilder build = new StringBuilder();

			boolean tableOpen = false;
			boolean rowOpen = false;
			boolean stopScanning = false;
			boolean colOpen = false;
			boolean spanOpen = false;
			int mRowIndex = 0;
			int mColumnIndex = 0;
			int mRowArrayIndex = 0;
			int mColumnArrayIndex = 0;

			try {

				mHttpClient = new DefaultHttpClient();
				mHttpGet = new HttpGet(URL); // make sure the url is correct.
				HttpContext httpcontext = new BasicHttpContext();

				// Execute HTTP Post Request
				mResponse = mHttpClient.execute(mHttpGet, httpcontext);

				String responseString = EntityUtils.toString(mResponse
						.getEntity());
				InputStream mResponseStream = new ByteArrayInputStream(
						responseString.getBytes(HTTP.UTF_8));
				BufferedReader br = new BufferedReader(new InputStreamReader(
						mResponseStream));

				while ((mResult = br.readLine()) != null && !stopScanning) {
					mResult = mResult.replaceAll("[\r\n]", "");

					// If string contains <thead>
					if (mResult.contains(TABLE_OPEN)) {
						Log.v(TAG, "Table Open");
						tableOpen = true;
					}

					// TABLE is open
					if (tableOpen) {
						// If string contains <tr>
						if (mResult.contains(ROW_OPEN)) {
							rowOpen = true;
							mRowIndex = mRowIndex + 1;
						}

						if (rowOpen) { // ROW is Open
							// If string contains <td>
							if (mResult.contains(COL_OPEN)) {
								colOpen = true;
								mColumnIndex = mColumnIndex + 1;
							}

							// Scan a single column
							if (colOpen)
								build.append(mResult);

							// If string contains </td>
							if (mResult.contains(COL_CLOSE)) {
								colOpen = false;

								String split = build.toString().trim();
								// Build data
								mRowArrayIndex = buildData(split,
										mRowArrayIndex, (mRowIndex - 1),
										(mColumnIndex - 1));
								build.delete(0, build.length() - 1);
							}

							// if string contains </tr>
							if (mResult.contains(ROW_CLOSE)) {
								mColumnIndex = 0;
								rowOpen = false;

								VideoRow row = new VideoRow();
								row.mVideoTurn = Tool.mVideoColumnArray.clone();
								row.mRow = mRowIndex - 1;
								Tool.mVideoRowArray.append(mColumnArrayIndex,
										row);
								mColumnArrayIndex = mColumnArrayIndex + 1;
								Tool.mVideoColumnArray.clear();
								mRowArrayIndex = 0;
							}
						}

						// if string contains </thead>
						if (mResult.contains(TABLE_CLOSE)) {
							Log.v(TAG, "Table Close");
							tableOpen = false;
							stopScanning = true;
							continue;
						}
					}
				}

				// Log.e(TAG, build.toString().trim());

			} catch (IOException e) {
				e.printStackTrace();
				mResult = null;
			}

			return mDataOK;

		}

		@Override
		protected void onPostExecute(Boolean dataWasOK) {
			ScheduleSDK.LAST_UPDATED = Tool.getDateTime();
			ScheduleSDK.UPDATED = true;

			if (transit) {
				Intent intent = new Intent(mContext, Activity_Schedule.class);
				mContext.startActivity(intent);
			}
		}

		public int buildData(String split, int index, int mRowIndex,
				int mColumnIndex) {
			int location = split.indexOf(CMD_CLOSE);
			location = location + 1;
			String data = "";
			if (location != -1) {
				while (location < (split.length() - 1)) {
					String check = split.substring(location, (location + 1));
					while (!check.equalsIgnoreCase(CMD_OPEN)) {
						data = data + check;
						location = location + 1;
						check = split.substring(location, (location + 1));
					}

					location = split.indexOf(CMD_CLOSE, location);
					location = location + 1;
				}

				data = data.replaceAll(Pattern.quote("&nbsp;"), " ");
				data = data.trim();
				if (data.isEmpty())
					Log.e(TAG, "EMPTY COLUMN VALUE for Row: " + mRowIndex
							+ " Column: " + mColumnIndex);
				else
					Log.v(TAG, data + " for Row: " + mRowIndex + " Column: "
							+ mColumnIndex);

				// Store the data
				VideoColumn mData = new VideoColumn();
				mData.mData = (!data.isEmpty()) ? data : "";
				mData.mRow = mRowIndex;
				mData.mColumn = mColumnIndex;
				Tool.mVideoColumnArray.append(index, mData);
				return (index + 1);
			}

			return index;
		}
	}
	
	

	public class ParseURL extends AsyncTask<String, Void, String> {
		private final String webAddress;

		public ParseURL(String URL) {
			this.webAddress = URL;
		}

		@Override
		protected String doInBackground(String... strings) {
			StringBuffer buffer = new StringBuffer();
			Document turns;

			try {

				// make sure cookies is turn on
				CookieHandler.setDefault(new CookieManager());

				// 1. Send a "GET" request, so that you can extract the form's
				// data.
				
				Document doc = Jsoup.connect(webAddress).get();
				
				
				String page = GetPageContent(webAddress);
				String postParams = getFormParams(page, "audiovisual",
						"ministerio");
				
		            

			} catch (IOException e) {

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return "";
		}

		@Override
		protected void onPostExecute(String result) {

		}
	}

	private void sendPost(String url, String postParams) throws Exception {

		URL obj = new URL(url);
		conn = (HttpURLConnection) obj.openConnection();

		// Acts like a browser
		conn.setUseCaches(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Host", "www.iglesiamallorca.com");
		conn.setRequestProperty("User-Agent", USER_AGENT);
		conn.setRequestProperty("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		conn.setRequestProperty("Accept-Language",
				"en-US,en;q=0.8,es;q=0.6,ca;q=0.4");

		if (cookies != null) {
			for (String cookie : this.cookies) {
				conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
			}
		}
		conn.setRequestProperty("Connection", "keep-alive");
		conn.setRequestProperty("Referer", log);
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length",
				Integer.toString(postParams.length()));

		conn.setDoOutput(true);
		conn.setDoInput(true);

		// Send post request
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.writeBytes(postParams);
		wr.flush();
		wr.close();

		Log.d(TAG, "sendPost: " + conn.getResponseCode());
		
		int responseCode = conn.getResponseCode();

		if (conn.getHeaderField("Cache-Control") == null) {
			Log.e(TAG, "An error occurred");
		} else {
			Log.e(TAG, "Everything worked out well");
		}

		Log.e(TAG, conn.getHeaderFields().toString());

		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + postParams);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		// System.out.println(response.toString());

		Log.d(TAG, "Done in sendPost");

	}

	private String GetPageContent(String url) throws Exception {
		Log.d(TAG, "Began GetPageContent");

		URL obj = new URL(url);
		conn = (HttpURLConnection) obj.openConnection();

		// default is GET
		conn.setRequestMethod("GET");

		conn.setUseCaches(false);

		// act like a browser
		conn.setRequestProperty("User-Agent", USER_AGENT);
		conn.setRequestProperty("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		if (cookies != null) {
			for (String cookie : this.cookies) {
				conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
			}
		}
		int responseCode = conn.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// Get the response cookies
		setCookies(conn.getHeaderFields().get("Set-Cookie"));

		Log.d(TAG, "Done in GetPageContent");

		return response.toString();

	}

	public String getFormParams(String html, String username, String password)
			throws UnsupportedEncodingException {

		System.out.println("Extracting form's data...");

		Document doc = Jsoup.parse(html);

		// Google form id
		Element loginform = doc.getElementById("contenido_right");
		Elements loginaction = doc.getElementsByTag("form");
		Element form = loginaction.first();
		log = MAIN_PAGE + form.attr("action");
		Log.e(TAG, "Action: " + log);
		Elements inputElements = loginform.getElementsByTag("input");
		List<String> paramList = new ArrayList<String>();
		for (Element inputElement : inputElements) {
			String key = inputElement.attr("name");
			String value = inputElement.attr("value");

			if (key.equals("usuario")) {
				value = username;
				paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
			} else if (key.equals("contrasena")) {
				value = password;
				paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
			}
		}

		// build parameters list
		StringBuilder result = new StringBuilder();
		for (String param : paramList) {
			if (result.length() == 0) {
				result.append(param);
			} else {
				result.append("&" + param);
			}
		}

		Log.d(TAG, "Done in getFormParams: " + result.toString());
		return result.toString();
	}

	public List<String> getCookies() {
		return cookies;
	}

	public void setCookies(List<String> cookies) {
		this.cookies = cookies;
	}

}
