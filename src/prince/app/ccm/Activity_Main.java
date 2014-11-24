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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import prince.app.ccm.tools.ActivityBase;
import prince.app.ccm.tools.AlertDialogX;
import prince.app.ccm.tools.ScheduleSDK;
import prince.app.ccm.tools.Task;
import prince.app.ccm.tools.Tool;
import prince.app.ccm.util.InstallAPP;
import prince.app.ccm.util.Util;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Activity class that hosts the fragment that displays the schedule for each department
 * @author Princewill Okorie
 *
 */
public class Activity_Main extends ActivityBase implements 
Network.clickListener, LoadPDF.LoadListener, Fragment_Web.WebListener{
	private static final int WEB = 0;
	private static final int WIFI = 1;
	private static final int PDF = 2;
	private static final int LOADING = 3;
	
	private static final String WEB_TAG = "mWebPage";
	private static final String WIFI_TAG = "mNoNetwork";
	private static final String PDF_TAG = "mPDF";
	private static final String WAIT_TAG = "mLoading";
	private static final String TITLE = "save_title";
	private static final String CURRENT_URL = "last_visited";
	private static final String LAST_CLICKED = "last_viewed";
	private static final String INSTALL_APP = "get_app";
	
	private ArrayList<String>mTagArray;
	
	private SharedPreferences mPreference;
	
	// URL's
	public static String sCameraURL;
	public static String sProyURL;
	public static String sDiscosURL;
	public static String sZonasURL;
	
	
	private String mCurrentURL;
	private static final String TAG = Activity_Main.class.getSimpleName();
	private static final String SALIR = "warning_frag";
	private boolean mSignedIn;
	private String mFirstShown;
	
	// View Variables
	private Toolbar mToolBar;
	private Spinner mSpinner;
	
    
	@Override
	public void onCreate(Bundle savedState){
		super.onCreate(savedState);
		setContentView(R.layout.layout_main);
		
		// initialize default variables
		initDefaults();
		
		if (!ScheduleSDK.UPDATED) new Task(this).execute(getResources().getString(R.string.pref_videoUrl_defaultValue), false);
		
		// Set up the tool bar
		mToolBar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(mToolBar);
		
		if (savedState != null){
			mDrawerTitle = savedState.getString(TITLE);
			mCurrentURL = savedState.getString(CURRENT_URL);
			mLastClicked = savedState.getInt(LAST_CLICKED);
			mToolBar.setTitle(mDrawerTitle);
		}
		
		if (savedState == null){
			loadPreferredSchedule(mFirstShown);
		}
		
		// initialize the navigation drawer
		initNavigationDrawer();
		
	}
	
	@Override
	public void onSaveInstanceState(Bundle state) {
	    // Save the user's current game state
	    state.putString(TITLE, mDrawerTitle);
	    state.putString(CURRENT_URL, mCurrentURL);
	    state.putInt(LAST_CLICKED, mLastClicked);
	    
	    super.onSaveInstanceState(state);
	}
	
	private void initDefaults(){
		mPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		mFirstShown = mPreference.getString(getResources().getString(R.string.pref_defaultPage_key), "");  	// get preferred first screen
		Log.e(TAG, "Default Page: " + mFirstShown);
		
		sCameraURL = mPreference.getString(getResources().getString(R.string.pref_videoUrl_key), "");		// get video url
		sProyURL = mPreference.getString(getResources().getString(R.string.pref_proyUrl_key), "");			// get projection url
		sDiscosURL = mPreference.getString(getResources().getString(R.string.pref_discosUrl_key), "");		// get disk url
		sZonasURL = mPreference.getString(getResources().getString(R.string.pref_zonasUrl_key), "");		// get zone url
		
		mSignedIn = Tool.getInstance().getBolPref(Util.SIGN_IN);
		
		mTagArray = new ArrayList<String>();
		mTagArray.add(WEB, WEB_TAG);
		mTagArray.add(WIFI, WIFI_TAG);
		mTagArray.add(PDF, PDF_TAG);
		mTagArray.add(LOADING, WAIT_TAG);
		
	}
	
	/** start intent to open PDF File */
	public void openPDF(File name){
		Intent target = new Intent(Intent.ACTION_VIEW);
		target.setDataAndType(Uri.fromFile(name),"application/pdf");
		target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		PackageManager packageManager = getPackageManager();
		List<ResolveInfo> activities = packageManager.queryIntentActivities(target, PackageManager.MATCH_DEFAULT_ONLY);
		boolean isIntentSafe = activities.size() > 0;
		
		if (isIntentSafe){
			try {
				startActivity(target);
			} catch (ActivityNotFoundException e) {
				// Instruct the user to install a PDF reader here, or something
				InstallAPP noAPP = InstallAPP.newInstance(R.string.pdf_header, R.string.pdf_message, R.string.pdf_positive, R.string.pdf_negative);
				noAPP.show(getSupportFragmentManager(), INSTALL_APP);
			}
		} else{
			// Instruct the user to install a PDF reader here, or something
			InstallAPP noAPP = InstallAPP.newInstance(R.string.pdf_header, R.string.pdf_message, R.string.pdf_positive, R.string.pdf_negative);
			noAPP.show(getSupportFragmentManager(), INSTALL_APP);
		}
	}
	
	
	private void loadPreferredSchedule(String key) {
		
		if (key.equalsIgnoreCase("video")){
			mCurrentURL = sCameraURL; // modify current URL
			mDrawerTitle = Tool.getInstance().changeTitle(0);
		}
		
		if (key.equalsIgnoreCase("proyeccion")){
			mCurrentURL = sProyURL; // modify current URL
			mDrawerTitle = Tool.getInstance().changeTitle(1);
		}
		
		if (key.equalsIgnoreCase("discos")){
			mCurrentURL = sDiscosURL; // modify current URL
			mDrawerTitle = Tool.getInstance().changeTitle(2);
		}
		
		// set the adequate action bar name
		getSupportActionBar().setTitle(mDrawerTitle);
		
		// load the adequate page
		Fragment_Web webPage = Fragment_Web.newInstance(mCurrentURL);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.frame_audio_main, webPage, WEB_TAG);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();
		getSupportFragmentManager().executePendingTransactions();
	}
	
	@Override
	public void navigationSwitch(int position) {
		
		// Grabacion
		if (position == 0 && !mCurrentURL.equalsIgnoreCase(sCameraURL)){
			mCurrentURL = sCameraURL; // modify current URL
			Fragment_Web webPage = Fragment_Web.newInstance(mCurrentURL);
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.frame_audio_main, webPage, WEB_TAG);
			ft.commit();
		}
		
		// Proyeccion
		else if (position == 1 && !mCurrentURL.equalsIgnoreCase(sProyURL)){
			mCurrentURL = sProyURL; // modify current URL
			Fragment_Web webPage = Fragment_Web.newInstance(mCurrentURL);
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.frame_audio_main, webPage, WEB_TAG);
			ft.commit();
		}
		
		// Discos
		else if (position == 2 && !mCurrentURL.equalsIgnoreCase(sDiscosURL)){
			mCurrentURL = sDiscosURL; // modify current URL
			Fragment_Web webPage = Fragment_Web.newInstance(mCurrentURL);
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.frame_audio_main, webPage, WEB_TAG);
			ft.commit();
		}
		
		// Zonas
		else if (position == 3 && !mCurrentURL.equalsIgnoreCase(sZonasURL)){
			mCurrentURL = sZonasURL;
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			LoadPDF zones = LoadPDF.newInstance();
			ft.replace(R.id.frame_audio_main, zones, PDF_TAG);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			ft.commit();
		}
		
		getSupportFragmentManager().executePendingTransactions();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_turnos, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		
		int itemId = item.getItemId();
		
		if (itemId == R.id.action_main_refresh && mSignedIn) {
			actionBarRefresh();
			return true;
		}
		
		if (itemId == R.id.action_settings) {
			// Open the settings menu
			Intent intent = new Intent();
	        intent.setClass(this, SettingsActivity.class);
	        startActivityForResult(intent, 0); 
	        Log.d(TAG, "settings called");
			return true;
		}
		else if (itemId == R.id.action_main_log && mSignedIn) {
			//TODO: Salir
	    	DialogFragment aT = AlertDialogX.newInstance(	"Cerrar sessión", 
															getResources().getString(R.string.action_warning_salir), 
															R.string.action_logout, 
															R.string.cancel,
															SALIR);
	    	
	    	aT.show(getSupportFragmentManager(), SALIR);
			return true;
		}
		
		else {
			return super.onOptionsItemSelected(item);
		}
	}
	
	
	
	/** task to undergo when refresh button is pressed */
	private void actionBarRefresh(){
		if (mLastClicked == 3){
			// create a pdf file
			Tool.ZONAS = Tool.createFile("zonas.pdf");
			updateShowPDF(mCurrentURL, Tool.ZONAS, false);
		} else{
			Fragment_Web webPage = Fragment_Web.newInstance(mCurrentURL);
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.frame_audio_main, webPage, WEB_TAG);
			ft.commit();
		}
	}
	
	@Override
	public void onNetworkRetry() {
		if (Tool.getInstance().isConnection()){
			Fragment_Web webPage = Fragment_Web.newInstance(mCurrentURL);
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.frame_audio_main, webPage, WEB_TAG);
			ft.commit();
		}
	}

	@Override
	public void onNetworkCheck() {
		startActivityForResult(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK), Tool.CONNECT);
	}

	@Override
	public void onOpenPDF() {
		if (mLastClicked == 3){
			mCurrentURL = sZonasURL;  // modify currently viewed page
			
			// create new folder or reference existing one
			File folder = new File(Tool.dir, "CCM AudioVisual");
			folder.mkdirs();
			File file = new File(folder,"zonas.pdf");
			
			// Open pdf file if it exists; else download, store and open
			if (file.exists() && file.length() > 0){
				Toast.makeText(getApplicationContext(), "Opening file", Toast.LENGTH_LONG).show();
				Tool.ZONAS = file.getAbsoluteFile();
				openPDF(Tool.ZONAS);
			}
			
			else{
				// create a pdf file
				Tool.ZONAS = Tool.createFile("zonas.pdf");
				
				// Download, store and open pdf
				updateShowPDF(sZonasURL, Tool.ZONAS, true);
			}
		}
	}
	
	/** Downloads a pdf file and/or shows it */
	private void updateShowPDF(String URL, final File name, final boolean show){
		// AsyncTask
		AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>(){
			@Override
			protected Boolean doInBackground(String... params) {
				boolean result = Tool.downloadPDF(params[0], name);
				Log.d(TAG, "result: " + result);
				return result;
			}
				
			@Override
			protected void onPostExecute(Boolean res){
				if (res){
					if (show){
						if (name!= null){
							openPDF(name);
						}
					}
					else{
						Toast.makeText(getApplicationContext(), "Archivo actualizado", Toast.LENGTH_SHORT).show();
					}
				}
				else{
					Toast.makeText(getApplicationContext(), "Error descargando archivo", Toast.LENGTH_SHORT).show();
				}
			}
			
		};
		task.execute(URL);
	}

	@Override
	public void onPageError() {
		Network wifi = (Network) getSupportFragmentManager().findFragmentByTag(WIFI_TAG);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		
		if (wifi == null){
			wifi = Network.newInstance();
		}
		
		ft.replace(R.id.frame_audio_main, wifi, WIFI_TAG);
		ft.commit();
	}

	@Override
	public Toolbar getToolBar() {
		// TODO Auto-generated method stub
		return mToolBar;
	}

	@Override
	public String getActionBarTitle() {
		// TODO Auto-generated method stub
		return "Video/Sonido";
	}

	@Override
	public void onDrawerOpen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDrawerClose() {
		// TODO Auto-generated method stub
		
	}
}
