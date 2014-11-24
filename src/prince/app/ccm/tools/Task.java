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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import prince.app.ccm.Activity_Schedule;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Public static class to handle asynchronous tasks
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
	
	private static final String TAG = Task.class.getSimpleName();
	private Context mContext;
	
	public Task(Context ct){
		this.mContext = ct;
	}
	
	public void execute(String url, boolean transit){
		new UserLoginTask(url, transit).execute();
	}
	
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		String URL;
		Boolean transit;
		
		public UserLoginTask(String url, boolean value){
			this.URL = url;
			this.transit = value;
		}
		
		@Override
		protected void onPreExecute(){
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
		    
			try{
				
				mHttpClient = new DefaultHttpClient();
	            mHttpGet = new HttpGet(URL); // make sure the url is correct.
	            HttpContext httpcontext = new BasicHttpContext();
	            
	            
	            // Execute HTTP Post Request
	            mResponse = mHttpClient.execute(mHttpGet, httpcontext);
	            
	            String responseString = EntityUtils.toString(mResponse.getEntity());
	            InputStream mResponseStream = new ByteArrayInputStream(responseString.getBytes(HTTP.UTF_8));
	            BufferedReader br = new BufferedReader(new InputStreamReader(mResponseStream));
	            
	            while ((mResult = br.readLine()) != null && !stopScanning) {
	                mResult = mResult.replaceAll("[\r\n]", "");
	                
	                // If string contains <thead>
	                if (mResult.contains(TABLE_OPEN)){
	                	Log.v(TAG, "Table Open");
	                	tableOpen = true;
	                }
	                
	                // TABLE is open
	                if (tableOpen){ 
	                	// If string contains <tr>
	                	if (mResult.contains(ROW_OPEN)){
	                		rowOpen = true;
	                		mRowIndex = mRowIndex + 1;
	                	}
	                	
	                	if (rowOpen){ // ROW is Open
	                		// If string contains <td>
	                		if (mResult.contains(COL_OPEN)){
	                			colOpen = true;
	                			mColumnIndex = mColumnIndex + 1;
	                		}
	                		
	                		// Scan a single column
	                		if (colOpen) build.append(mResult);
	                		
	                		
	                		// If string contains </td>
	                		if (mResult.contains(COL_CLOSE)){
	                			colOpen = false;
	                			
	                			String split = build.toString().trim();
	                			// Build data
	                			mRowArrayIndex = buildData(split, mRowArrayIndex, (mRowIndex-1), (mColumnIndex-1));
	                			build.delete(0, build.length()-1);
	                		}
	                		
	                		// if string contains </tr>
	                		if (mResult.contains(ROW_CLOSE)){
	                			mColumnIndex = 0;
	                			rowOpen = false;
	                			
	                			VideoRow row = new VideoRow();
	                			row.mVideoTurn = Tool.mVideoColumnArray.clone();
	                			row.mRow = mRowIndex - 1;
	                			Tool.mVideoRowArray.append(mColumnArrayIndex, row);
	                			mColumnArrayIndex = mColumnArrayIndex + 1;
	                			Tool.mVideoColumnArray.clear();
	                			mRowArrayIndex = 0;
	                		}
	                	}
	                	
	                	// if string contains </thead>
	                	if (mResult.contains(TABLE_CLOSE)){
	                		Log.v(TAG, "Table Close");
	                		tableOpen = false;
	                		stopScanning = true;
	                		continue;
	                	}
	                }
	            }
	            
	         //   Log.e(TAG, build.toString().trim());
	            
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
			
			if (transit){
				Intent intent = new Intent(mContext, Activity_Schedule.class);
				mContext.startActivity(intent);
			}
		}
		
		public int buildData(String split, int index, int mRowIndex, int mColumnIndex){
			int location = split.indexOf(CMD_CLOSE);
			location = location + 1;
			String data = "";
			if (location != -1){
				while (location < (split.length()-1)){
					String check = split.substring(location, (location+1)); 
					while (!check.equalsIgnoreCase(CMD_OPEN)){
						data = data + check;
						location = location + 1;
						check = split.substring(location, (location+1));
					}
					
					location = split.indexOf(CMD_CLOSE, location);
					location = location + 1;
				}
				
				data = data.replaceAll(Pattern.quote("&nbsp;"), " ");
				data = data.trim();
				if (data.isEmpty()) Log.e(TAG, "EMPTY COLUMN VALUE for Row: " + mRowIndex + " Column: " + mColumnIndex);
				else Log.v(TAG, data + " for Row: " + mRowIndex + " Column: " + mColumnIndex);
				
				// Store the data
				VideoColumn mData = new VideoColumn();
				mData.mData = (!data.isEmpty()) ? data : "";
				mData.mRow = mRowIndex;
				mData.mColumn = mColumnIndex;
				Tool.mVideoColumnArray.append(index, mData);
				return (index+1);
			}
			
			return index;
		}
		}
}
