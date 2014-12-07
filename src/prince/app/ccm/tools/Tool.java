package prince.app.ccm.tools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import prince.app.ccm.R;
import android.annotation.TargetApi;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.SparseArray;
import android.widget.Toast;

public class Tool extends Application{
	protected static final String TAG = "Util";
	public static final int CONNECT = 710;
	private static Tool generic;
	public static File PROJECTION;
	public static File ZONAS;
	public static String dir = Environment.getExternalStorageDirectory().getAbsolutePath();
	public static boolean EXIT_TASK;
	/**
	 * An Array containing each column in the schedule table
	 */
	public static SparseArray<VideoColumn> mVideoColumnArray; 
	
	/**
	 * An Array containing each row in the schedule table
	 */
	public static SparseArray<VideoRow> mVideoRowArray;
	
	public static File APP_DIR;
	
	@Override
	public void onCreate(){
		super.onCreate();
		generic = this;
		
		mVideoColumnArray = new SparseArray<VideoColumn>();
		mVideoRowArray = new SparseArray<VideoRow>();
		
		APP_DIR = new File(Tool.dir, "CCM AudioVisual");
		APP_DIR.mkdirs();
		
		// set preference to default values
		PreferenceManager.setDefaultValues(this, R.xml.app_preference, false);
	}
	
	public static Tool getInstance(){
		return generic;
	}
	
	/**
	 * modifies the String value associated with the given key
	 * @param key - unique identifier
	 * @param value - new value
	 */
	public void modPref(String key, long value){
		SharedPreferences sharedpreferences;
		sharedpreferences = getSharedPreferences(key, Context.MODE_PRIVATE);
		Editor editor = sharedpreferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}
	
	/**
	 * Returns the String value associated with the given key
	 * @param key - the unique identifier
	 * @return the String value associated with the key
	 */
	public String getStringPref(String key){
		SharedPreferences sharedpreferences;
		sharedpreferences = getSharedPreferences(key, Context.MODE_PRIVATE);
		return sharedpreferences.getString(key, "");
	}
	
	public void modStringPref(String key, String string){
		SharedPreferences sharedpreferences;
		sharedpreferences = getSharedPreferences(key, Context.MODE_PRIVATE);
		Editor editor = sharedpreferences.edit();
		editor.putString(key, string);
		editor.commit();
	}
	
	public boolean getBolPref(String key){
		SharedPreferences sharedpreferences;
		sharedpreferences = getSharedPreferences(key, Context.MODE_PRIVATE);
		return sharedpreferences.getBoolean(key, false);
	}
	
	public void modBolPref(String key, Boolean string){
		SharedPreferences sharedpreferences;
		sharedpreferences = getSharedPreferences(key, Context.MODE_PRIVATE);
		Editor editor = sharedpreferences.edit();
		editor.putBoolean(key, string);
		editor.commit();
	}
	
	/**
	 * Returns the String value associated with the given key
	 * @param key - the unique identifier
	 * @return the String value associated with the key
	 */
	public long getPref(String key){
		SharedPreferences sharedpreferences;
		sharedpreferences = getSharedPreferences(key, Context.MODE_PRIVATE);
		return sharedpreferences.getLong(key, 0);
	}
	
	/**
	 * Checks if a preference is stored
	 * @param key - the unique identifier
	 * @return true - if the preference exists
	 */
	public boolean prefExist(String key){
		SharedPreferences sharedpreferences;
		sharedpreferences = getSharedPreferences(key, Context.MODE_PRIVATE);
		return sharedpreferences.contains(key);
	}
	
	public static String getDateTime(){
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault());
		dateFormatter.setLenient(false);
		Date today = new Date();
		return dateFormatter.format(today).trim();
	}
	public static String time(){
    	return DateFormat.getTimeInstance().format(new Date());
    }
	
	public String changeTitle (int position){
		String mTitle[] = getResources().getStringArray(R.array.array_navigation);
		return mTitle[position];
	}
	
	public static File createFile(String filename){
		File folder = new File(dir, "CCM AudioVisual");
		folder.mkdirs();
		File file = new File(folder,filename);
		try {
			file.createNewFile();
			return file.getAbsoluteFile();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean downloadPDF(String URL, File file){
		disableConnectionReuseIfNecessary();
        HttpURLConnection urlConnection = null;
		try {
			URL newURL = new URL(URL);
			urlConnection = (HttpURLConnection)newURL.openConnection();
			urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.connect();
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			
			FileOutputStream save = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int data;
			while ((data = in.read(buffer)) > 0) { 
			    save.write(buffer, 0, data);  
			   }


			save.close();
			return true;
		}catch (MalformedURLException e1) {
			return false;
		}catch (IOException e) {
			return false;
		}
	}
	
	public  boolean isConnection(){
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public boolean isAirplaneMode(){
		return Settings.System.getInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
	}

	/**
	 * Workaround for bug pre-Froyo, see here for more info:
	 * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
	 */
	public static void disableConnectionReuseIfNecessary() {
		// HTTP connection reuse which was buggy pre-froyo
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
			System.setProperty("http.keepAlive", "false");
		}
	}
	
	/**
	 * Method to start an intent to make a phone call
	 * @param number - The phone number to call
	 */
	public void makeCall(String number){
		Intent phoneIntent = new Intent(Intent.ACTION_CALL);
		phoneIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		phoneIntent.setData(Uri.parse("tel:" + number));
		
		try {
			startActivity(phoneIntent);
		} catch (ActivityNotFoundException ex) {
			Toast.makeText(this, getResources().getString(R.string.call_failed), Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * Method to start an intent to send mails
	 * @param emails - A array of email addresses expressed in strings
	 */
	public void sendEmail(String [] emails){
		Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
		emailIntent.setData(Uri.parse("mailto:"));
		emailIntent.putExtra(Intent.EXTRA_EMAIL, emails);
		
		final Intent intent = Intent.createChooser(emailIntent, getResources().getString(R.string.email_app_choose));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException ex) {
	         Toast.makeText(this, getResources().getString(R.string.email_failed), Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * Method to start an intent to send SMS
	 * @param numbers - A string of numbers separated by semi-colon
	 */
	public void sendSMS(String numbers){
		Intent messageIntent = new Intent(Intent.ACTION_VIEW);
		messageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		messageIntent.setData(Uri.parse("smsto:"));
		messageIntent.setType("vnd.android-dir/mms-sms");
		messageIntent.putExtra("address", numbers);
		
		try {
			startActivity(messageIntent);
		} catch (ActivityNotFoundException ex) {
			Toast.makeText(this, getResources().getString(R.string.message_failed), Toast.LENGTH_SHORT).show();
		}
	}
}
