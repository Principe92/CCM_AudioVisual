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

package prince.app.ccm;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import prince.app.ccm.tools.AsyncSession;
import prince.app.ccm.tools.AsyncSession.AsyncCallback;
import prince.app.ccm.tools.Tool;
import prince.app.ccm.util.Util;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Fragment_Log extends Fragment{
	
	/**
	 * Method to create a new instance of the fragment
	 * @return - a new and final instance of the fragment class
	 */
	public static Fragment_Log newInstance(){
		final Fragment_Log aX = new Fragment_Log();
		return aX;
	}
	
	// Values for email and password at the time of the login attempt.
	private String mUserName;
	private String mPassword;

	// UI references.
	private EditText mUserEditText;
	private EditText mPasswordEditText;
	private View mLoginFormView;
	private View mLoginStatusView;
	private CheckBox mRemind;
	private CheckBox mShowPassword;
	
	private static final String TAG = Fragment_Log.class.getSimpleName();
	private static final String URL = "http://www.iglesiamallorca.com/av_contrasena.php";
	
	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private AsyncSession mAuthTask = null;
	
	@Override
	public void onCreate(Bundle oldState){
		super.onCreate(oldState);
		
		setRetainInstance(true);
		
		setUpListener();
	}
	
	private void setUpListener(){
		AsyncSession.setCallback(new AsyncCallback(){

			@Override
			public void onPostExecute(boolean result) {
				if (result){
					saveCredentials();
					Tool.getInstance().modBolPref(Util.SIGN_IN, true);
						Intent intent = new Intent(getActivity(), Activity_Main.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
				}
				
				else{
					if (!Tool.getInstance().isConnection()){
						Toast.makeText(getActivity(), getResources().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
					} 
					
					else {
						Toast.makeText(getActivity(), getResources().getString(R.string.user_passwd_error), Toast.LENGTH_LONG).show();	
					}
					
					onCancel();
					
				}
				
			}

			@Override
			public void onCancel() {
				mAuthTask = null;
				showProgress(false);
				
			}
			
		});
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle oldState){
		super.onCreateView(inflater, parent, oldState);
		View view = inflater.inflate(R.layout.layout_log, parent, false);
		
		initView(view);
		
		return view;
	}
	
	private void initView(View view){
		// Set up the login form.
		mUserEditText = (EditText) view.findViewById(R.id.username);
	//	addFocusListener(mUserEditText);

		mPasswordEditText = (EditText) view.findViewById(R.id.password);
	//	addFocusListener(mPasswordEditText);
		
		mPasswordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if (id == R.id.user_login || id == EditorInfo.IME_NULL) {
					if (!Tool.getInstance().isConnection())
						Toast.makeText(getActivity(), "No network connection!", Toast.LENGTH_SHORT).show();
					else{
						attemptLogin();
						return true;
					}
				}
				return false;
			}
		});

		mLoginFormView = view.findViewById(R.id.login_form);
		mLoginStatusView = view.findViewById(R.id.login_progress);

		view.findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
						attemptLogin();
					}
				});
				
		mRemind = (CheckBox) view.findViewById(R.id.box_remember);
		if (Tool.getInstance().getBolPref(Util.REMIND)){
			mRemind.setChecked(true);
			mUserEditText.setText("");
			mPasswordEditText.setText("");
			mUserEditText.append(Tool.getInstance().getStringPref(Util.USER));
			mPasswordEditText.append(Tool.getInstance().getStringPref(Util.PASSWORD));
		}
		
		mShowPassword = (CheckBox) view.findViewById(R.id.box_reveal);
		mShowPassword.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked){
					mPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
				} else{
					mPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}
				
				if (mPasswordEditText.hasFocus()){
					String temp = mPasswordEditText.getText().toString().trim();
					mPasswordEditText.setText("");
					mPasswordEditText.append(temp);
				}
				
			}
			
		});
	}
	
	public void addFocusListener(EditText xT){
		xT.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                	InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                
                else {
                	getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                }

            }
        });
	}
	
	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		} 
		
		Log.e(TAG, "Attempting Login");

		// Reset errors.
		mUserEditText.setError(null);
		mPasswordEditText.setError(null);

		// Store values at the time of the login attempt.
		mUserName = mUserEditText.getText().toString();
		mPassword = mPasswordEditText.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordEditText.setError(getString(R.string.error_field_required));
			focusView = mPasswordEditText;
			cancel = true;
		} 
		else if (mPassword.length() < 4) {
			mPasswordEditText.setError(getString(R.string.error_incorrect_password));
			focusView = mPasswordEditText;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mUserName)) {
			mUserEditText.setError(getString(R.string.error_field_required));
			focusView = mUserEditText;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			showProgress(true);
			mAuthTask = new AsyncSession( mUserEditText.getText().toString().trim(),  mPasswordEditText.getText().toString().trim());
			mAuthTask.execute();
		}
	}
	
	/** save the user name and password */
	private void saveCredentials(){
		if (mRemind.isChecked()){
			Tool.getInstance().modStringPref(Util.USER, mUserName);
			Tool.getInstance().modStringPref(Util.PASSWORD, mPassword);
			Tool.getInstance().modBolPref(Util.REMIND, true);
		}
		
		else{
			if (Tool.getInstance().prefExist(Util.REMIND)){
				Tool.getInstance().modBolPref(Util.REMIND, false);
			}
		}
	}
	
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
		
	//	setTimer();
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			String mResult;
		    HttpPost mHttpPost;
		    HttpResponse mResponse;
		    HttpClient mHttpClient;
		    List<NameValuePair> nameValuePairs;
		    Boolean mDataOK = false;
		    
			try{
				
				mHttpClient = new DefaultHttpClient();
	            mHttpPost= new HttpPost(URL); // make sure the url is correct.
	            HttpContext httpcontext = new BasicHttpContext();
	            
	            // Add your data
	            nameValuePairs = new ArrayList<NameValuePair>(2);
	            nameValuePairs.add(new BasicNameValuePair("usuario", mUserEditText.getText().toString().trim()));  
	            nameValuePairs.add(new BasicNameValuePair("contrasena", mPasswordEditText.getText().toString().trim()));
	            mHttpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	            
	            // Execute HTTP Post Request
	            mResponse = mHttpClient.execute(mHttpPost, httpcontext);
	            
	            Log.e(TAG, mResponse.getAllHeaders().toString());
	            
	            String responseString = EntityUtils.toString(mResponse.getEntity());
	            InputStream mResponseStream = new ByteArrayInputStream(responseString.getBytes(HTTP.UTF_8));
	            BufferedReader br = new BufferedReader(new InputStreamReader(mResponseStream));
	            
	            while ((mResult = br.readLine()) != null) {
	                mResult = mResult.replaceAll("[\r\n]", "");
	                if (mResult.contains(getActivity().getResources().getString(R.string.pref_videoUrl_defaultValue))){
	                	Log.v("Response", mResult);
	                	mDataOK = true;
	                }
	            }
	            
			} catch (IOException e) {
		        e.printStackTrace();
		        mResult = null;
		    }
	 
	    return mDataOK;

		}

		@Override
		protected void onPostExecute(Boolean dataWasOK) {
			if (dataWasOK){
				saveCredentials();
				Tool.getInstance().modBolPref(Util.SIGN_IN, true);
					Intent intent = new Intent(getActivity(), Activity_Main.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
			}
			
			else{
				if (!Tool.getInstance().isConnection()){
					Toast.makeText(getActivity(), getResources().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
				} 
				
				else {
					Toast.makeText(getActivity(), getResources().getString(R.string.user_passwd_error), Toast.LENGTH_LONG).show();	
				}
				
				onCancelled();
				
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}

}
