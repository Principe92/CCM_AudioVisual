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
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.SparseArray;

public class Tool extends Application{
	protected static final String TAG = "Util";
	public static final int CONNECT = 710;
	private static Tool generic;
	public static File PROJECTION;
	public static File ZONAS;
	public static String dir = Environment.getExternalStorageDirectory().getAbsolutePath();
	
	/**
	 * An Array containing each column in the schedule table
	 */
	public static SparseArray<VideoColumn> mVideoColumnArray; 
	
	/**
	 * An Array containing each row in the schedule table
	 */
	public static SparseArray<VideoRow> mVideoRowArray;
	
	@Override
	public void onCreate(){
		super.onCreate();
		generic = this;
		
		mVideoColumnArray = new SparseArray<VideoColumn>();
		mVideoRowArray = new SparseArray<VideoRow>();
		
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
	    
	    
}
