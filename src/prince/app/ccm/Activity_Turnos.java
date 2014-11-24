package prince.app.ccm;

import prince.app.ccm.tools.ActivityBase;
import prince.app.ccm.tools.AlertDialogX;
import prince.app.ccm.tools.Tool;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class Activity_Turnos extends ActivityBase implements Fragment_Web.WebListener, OnItemSelectedListener, Network.clickListener{
	private static final String TAG = Activity_Turnos.class.getSimpleName();
	
	// URL's
	public static String sCameraURL;
	public static String sProyURL;
	public static String sDiscosURL;
	
	// View Variables
	private Spinner mSpinner;
	private Toolbar mToolBar;
	
	// Fragment Tags
	private static final String WEB_TAG = "mWebPage";
	private static final String WIFI_TAG = "mNoNetwork";
	private static final String SALIR = "warning_frag";
	
	// Saved State KEYS
	private static final String SAVED_CURRENT_URL = "current_url";
	private static final String SAVED_SPINNER_LAST_ITEM = "last_clicked";
	
	// Variables
	private String mCurrentURL;
	private String mFirstShown;
	private SharedPreferences mPreference;
	private int mSpinnerLast;
	
	@Override
	public void onCreate(Bundle savedState){
		super.onCreate(savedState);
		setContentView(R.layout.layout_turnos);
	
		// initialize default variables
		initDefaults();
		
		// Set up the tool bar
		mToolBar = (Toolbar) findViewById(R.id.toolbar_turnos);
		setSupportActionBar(mToolBar);
		
		// Restore value of variables from old state
		if (savedState != null){
			mDrawerTitle = savedState.getString(SAVED_TITLE);
			mCurrentURL = savedState.getString(SAVED_CURRENT_URL);
			mSpinnerLast = savedState.getInt(SAVED_SPINNER_LAST_ITEM);
		}
		
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		
		// initialize the navigation drawer
		initNavigationDrawer();
				
		initSpinner();
				
		if (savedState == null){
			loadPreferredSchedule(mFirstShown);
		}
		
	}
	
	@Override
	public void onSaveInstanceState(Bundle state) {
	    // Save the user's current game state
		super.onSaveInstanceState(state);
		
	    state.putString(SAVED_CURRENT_URL, mCurrentURL);
	    state.putInt(SAVED_SPINNER_LAST_ITEM, mSpinnerLast);
	}
	
	public void initSpinner(){
		mSpinner = (Spinner) findViewById(R.id.spinner_turnos);
		
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(	this, 
																	android.R.layout.simple_spinner_dropdown_item, 
																	getResources().getStringArray(R.array.array_turnos));
		
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		// Apply the adapter to the spinner
		mSpinner.setAdapter(adapter);
		mSpinner.setOnItemSelectedListener(this);
	}
	
	private void initDefaults(){
		mPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		mFirstShown = mPreference.getString(getResources().getString(R.string.pref_defaultPage_key), "");  	// get preferred first screen
		Log.e(TAG, "Default Page: " + mFirstShown);
		
		sCameraURL = mPreference.getString(getResources().getString(R.string.pref_videoUrl_key), "");		// get video url
		sProyURL = mPreference.getString(getResources().getString(R.string.pref_proyUrl_key), "");			// get projection url
		sDiscosURL = mPreference.getString(getResources().getString(R.string.pref_discosUrl_key), "");		// get disk url
	}
	
	private void loadPreferredSchedule(String key) {
		
		if (key.equalsIgnoreCase("video")){
			mCurrentURL = sCameraURL; // modify current URL
			mSpinner.setSelection(0, true);
		}
		
		if (key.equalsIgnoreCase("proyeccion")){
			mCurrentURL = sProyURL; // modify current URL
			mSpinner.setSelection(1, true);
		}
		
		if (key.equalsIgnoreCase("discos")){
			mCurrentURL = sDiscosURL; // modify current URL
			mSpinner.setSelection(2, true);
		}
	}
	
	public void spinnerSwitch(int position){

		switch(position){
		
		case 0:
			if (!mCurrentURL.equalsIgnoreCase(sCameraURL)) mCurrentURL = sCameraURL;
			break;
			
		case 1:
			if (position == 1 && !mCurrentURL.equalsIgnoreCase(sProyURL)) mCurrentURL = sProyURL;
			break;
		
		case 2:
			if (position == 2 && !mCurrentURL.equalsIgnoreCase(sDiscosURL)) mCurrentURL = sDiscosURL;
			break;
		
		default:
			break;
		}
		
		Fragment_Web webPage = (Fragment_Web) getSupportFragmentManager().findFragmentByTag(WEB_TAG);
		if (webPage != null) webPage.loadPage(mCurrentURL);
		else{
			webPage = Fragment_Web.newInstance(mCurrentURL);
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.frame_audio_main, webPage, WEB_TAG);
			ft.commit();
			getSupportFragmentManager().executePendingTransactions();
		}
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
		
		if (itemId == R.id.action_main_refresh) {
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
		else if (itemId == R.id.action_main_log) {
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
		if (Tool.getInstance().isConnection()){
			Fragment_Web webPage = (Fragment_Web) getSupportFragmentManager().findFragmentByTag(WEB_TAG);
			if (webPage != null) webPage.refreshPage();
			else {
				webPage = Fragment_Web.newInstance(mCurrentURL);
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ft.replace(R.id.frame_audio_main, webPage, WEB_TAG);
				ft.commit();
			}
		}
		
		else {
			Toast.makeText(this, "No hay conexión !", Toast.LENGTH_LONG).show();
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
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		spinnerSwitch(position);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void navigationSwitch(int position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Toolbar getToolBar() {
		return mToolBar;
	}

	@Override
	public String getActionBarTitle() {
		// TODO Auto-generated method stub
		return "Turnos";
	}

	@Override
	public void onDrawerOpen() {
		mSpinner.setVisibility(View.GONE);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
	}

	@Override
	public void onDrawerClose() {
		mSpinner.setVisibility(View.VISIBLE);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		
	}

}
