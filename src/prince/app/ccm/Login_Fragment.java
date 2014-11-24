package prince.app.ccm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import prince.app.ccm.tools.Tool;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class Login_Fragment extends Fragment {

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String userName;
	private String password;

	// UI references.
	private EditText mUserNameView;
	private EditText mPasswordView;
	private CheckBox mRemind;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private static HttpURLConnection connection;
	private static String URL = "http://www.iglesiamallorca.com/av_login.php";
//	private static String URL = "http://www.iglesiamallorca.com/av_contrasena.php";
	private static String TAG = "Login";
	private static String USER = "username";
	private static String PASSWORD = "password";
	private static String REMIND = "remember";
	public static LaunchListener launch;
	
	
	// Container Activity must implement this interface
    public interface LaunchListener {
    	/** Detects when the user has successfully signed in */
        public void onSignedIn();
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            launch = (LaunchListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement LoginListener");
        }
    }
    
    @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    // Retain this fragment across configuration changes.
	    setRetainInstance(true);
	  }
    
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		LinearLayout wrapper = new LinearLayout(getActivity()); 
		View view = inflater.inflate(R.layout.activity_login, wrapper, true);
		
		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
	        }
	    }); 
		
		// Set up the login form.
		mUserNameView = (EditText) view.findViewById(R.id.edit_email);
	
		mPasswordView = (EditText) view.findViewById(R.id.edit_password);
		mPasswordView
		.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id,
					KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptLogin();
					return true;
				}
				return false;
			}
		});
		
		mLoginFormView = view.findViewById(R.id.login_form);
		mLoginStatusView = view.findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) view.findViewById(R.id.login_status_message);
		
		
		mRemind = (CheckBox) view.findViewById(R.id.checkBox1);
		if (Tool.getInstance().getBolPref(REMIND)){
			mRemind.setChecked(true);
			mUserNameView.setText(Tool.getInstance().getStringPref(USER));
			mPasswordView.setText(Tool.getInstance().getStringPref(PASSWORD));
		}
	
		view.findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						if (!isNetworkAvailable())
							Toast.makeText(getActivity(), "No network connection!", Toast.LENGTH_SHORT).show();
						else
							attemptLogin();
					}
				});
	    
		return view;
	}
	
	protected  boolean isNetworkAvailable(){
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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

		// Reset errors.
		mUserNameView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		userName = mUserNameView.getText().toString();
		password = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(password)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (password.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(userName)) {
			mUserNameView.setError(getString(R.string.error_field_required));
			focusView = mUserNameView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
		/*	showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null); */
			
			saveUserPassword();
			
			if (userName.equalsIgnoreCase("audiovisual") && password.equalsIgnoreCase("ministerio"))
				launch.onSignedIn();
			else
				mPasswordView
				.setError(getString(R.string.error_incorrect_password));
		mPasswordView.requestFocus();
		}
	}
	
	/** save the user name and password */
	private void saveUserPassword(){
		if (mRemind.isChecked()){
			Tool.getInstance().modStringPref(USER, userName);
			Tool.getInstance().modStringPref(PASSWORD, password);
			Tool.getInstance().modBolPref(REMIND, true);
		}
		
		else{
			if (Tool.getInstance().prefExist(REMIND)){
				Tool.getInstance().modBolPref(REMIND, false);
			}
		}
		
		
	}
	/** Establish http connection and retrieve response*/
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private boolean connect(String URL){
		try {
			
		/*	Authenticator.setDefault (new Authenticator() {
			    protected PasswordAuthentication getPasswordAuthentication() {
			        return new PasswordAuthentication (userName, password.toCharArray());
			    }
			});*/
			
			URL newURL = new URL(URL);
			connection = (HttpURLConnection)newURL.openConnection();
			connection.addRequestProperty("id", "contenido_right");
			connection.addRequestProperty("usuario", userName);
			connection.addRequestProperty("contrasena", password);
			connection.addRequestProperty("Enviar", "si");
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			
			Log.d(TAG, "Username: " + userName);
			Log.d(TAG, "Password: " + password);
	
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
				InputStream result = connection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(result));
				StringBuilder query = new StringBuilder();
				String response;
				
				while ((response = reader.readLine()) != null) {
					query.append(response);
				}
				Log.d(TAG, "Response: " + query);
				return true;
			}else{
				return false;
			}
			
		}catch (FileNotFoundException e) {
			Log.d(TAG, "The resource was not cached!" + e);
			return false;
		}catch (IOException e) {
			Log.d(TAG, "Error" + e);
			return false;
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
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			// make http call and retrieve result
			
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(URL);
			
			BasicNameValuePair id = new BasicNameValuePair("id","contenido_right");
			BasicNameValuePair usernameBasic = new BasicNameValuePair("usuario", userName);
            BasicNameValuePair passwordBasic = new BasicNameValuePair("contrasena", password);

            // We add the content that we want to pass with the POST request to as name-value pairs
            //Now we put those sending details to an ArrayList with type safe of NameValuePair
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(id);
            list.add(usernameBasic);
            list.add(passwordBasic);
            
            
            try {
                // UrlEncodedFormEntity is an entity composed of a list of url-encoded pairs. 
                //This is typically useful while sending an HTTP POST request. 
                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(list);

                // setEntity() hands the entity (here it is urlEncodedFormEntity) to the request.
                post.setEntity(urlEncodedFormEntity);

                try {
                    // HttpResponse is an interface just like HttpPost.
                    //Therefore we can't initialize them
                    HttpResponse httpResponse = client.execute(post);

                    // According to the JAVA API, InputStream constructor do nothing. 
                    //So we can't initialize InputStream although it is not an interface
                    InputStream result = httpResponse.getEntity().getContent();
                    if (httpResponse.getStatusLine().getStatusCode() == 200){
        				BufferedReader reader = new BufferedReader(new InputStreamReader(result));
        				StringBuilder query = new StringBuilder();
        				String response;
        				
        				while ((response = reader.readLine()) != null) {
        					query.append(response);
        				}
        				Log.d(TAG, "Response: " + query);
        				return true;
                    } 
                }catch (ClientProtocolException cpe) {
                        System.out.println("First Exception caz of HttpResponese :" + cpe);
                        cpe.printStackTrace();
                    } catch (IOException ioe) {
                        System.out.println("Second Exception caz of HttpResponse :" + ioe);
                        ioe.printStackTrace();
                    }

                } catch (UnsupportedEncodingException uee) {
                    System.out.println("An Exception given because of UrlEncodedFormEntity argument :" + uee);
                    uee.printStackTrace();
                }
                    
                    
		//	return connect(URL);
			return false;

		
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				saveUserPassword();
				launch.onSignedIn();
			} else {
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
