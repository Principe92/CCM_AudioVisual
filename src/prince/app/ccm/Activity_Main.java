package prince.app.ccm;

import prince.app.ccm.tools.ActivityBase;
import prince.app.ccm.tools.Tool;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class Activity_Main extends ActivityBase implements Fragment_Turnos.WebListener, OnItemSelectedListener, 
		Fragment_NavigationDrawer.NavigationDrawerCallbacks{
	private static final String TAG = Activity_Main.class.getSimpleName();
	
	// URL's
	public static String URL_CAMERA;
	public static String URL_PROYECCION;
	public static String URL_DISCOS;
	
	// View Variables
	private Spinner mSpinner;
	private Toolbar mToolBar;
	private Fragment_NavigationDrawer mNavigationDrawerFragment;
	
	// Fragment Tags
	public static final String MANUAL_FRAGMENT = "manual_fragment";
	public static final String CONTACT_FRAGMENT = "contact_fragment";
	public static final String TURNS_FRAGMENT = "turns_fragment";
	public static final String WORKERS_FRAGMENT = "worker_fragment";
	
	// Saved State KEYS
	private static final String SAVED_CURRENT_URL = "current_url";
	private static final String SAVED_SPINNER_LAST_ITEM = "last_clicked";
	private static final String SAVED_SPINNER_VISIBILITY = "spinner_visible";
	private static final String SAVED_TITLE_VISIBILITY = "title_visible";
	
	// Variables
	private String mPageURL;
	private int mSpinnerLast = 0;
	private boolean mShowSpinner;
	private boolean mShowTitle;
	private int mNavigationLastPosition = -1;
	private String defaultURLKey;
	private static String TITLE;
	private boolean mURLoaded;
	
	@Override
	public void onCreate(Bundle savedState){
		super.onCreate(savedState);
		setContentView(R.layout.layout_turnos);
		
		TITLE = getResources().getStringArray(R.array.array_navigation)[0];
	
		// initialize default variables
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		URL_CAMERA = sp.getString(getResources().getString(R.string.pref_videoUrl_key), "");			// get video url
		URL_PROYECCION = sp.getString(getResources().getString(R.string.pref_proyUrl_key), "");			// get projection url
		URL_DISCOS = sp.getString(getResources().getString(R.string.pref_discosUrl_key), "");			// get disk url
		defaultURLKey = sp.getString(getResources().getString(R.string.pref_defaultPage_key), "");  	// get preferred first screen
		
	//	Task task = new Task(this);
	//	task.execute(URL_CAMERA, false);
		
		// Restore value of variables from old state
		if (savedState != null){
			mPageURL = savedState.getString(SAVED_CURRENT_URL);
			mSpinnerLast = savedState.getInt(SAVED_SPINNER_LAST_ITEM);
			mShowSpinner = savedState.getBoolean(SAVED_SPINNER_VISIBILITY, true);
			mShowTitle = savedState.getBoolean(SAVED_TITLE_VISIBILITY, true);
		} else{
			loadPrefSchedule(defaultURLKey);
			mShowSpinner = true;
			mShowTitle = false;
			mURLoaded = false;
		}
		
		// Set up the tool bar
		mToolBar = (Toolbar) findViewById(R.id.toolbar_turnos);
		setSupportActionBar(mToolBar);
		
        // Add the navigation drawer
		mNavigationDrawerFragment = (Fragment_NavigationDrawer)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout_turnos));
        
        // Add the spinner
        initSpinner();
		
        // Disable action bar title
		if (!mShowSpinner) mSpinner.setVisibility(View.GONE);
		else getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (!mShowTitle) getSupportActionBar().setDisplayShowTitleEnabled(false);
	}
	
	@Override
	public void onSaveInstanceState(Bundle state) {
	    // Save the user's current game state
		super.onSaveInstanceState(state);
		
	    state.putString(SAVED_CURRENT_URL, mPageURL);
	    state.putInt(SAVED_SPINNER_LAST_ITEM, mSpinnerLast);
	    state.putBoolean(SAVED_SPINNER_VISIBILITY, (mSpinner.getVisibility() == View.VISIBLE));
	}
	
	public void initSpinner(){
		mSpinner = (Spinner) findViewById(R.id.spinner_turnos);
		
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(	this, 
				R.array.array_turnos, android.R.layout.simple_spinner_item);
		
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		mSpinner.setOnItemSelectedListener(this);
		
		// Apply the adapter to the spinner
		mSpinner.setAdapter(adapter);
		mSpinner.setSelection(mSpinnerLast);
	}
	
	private void loadPrefSchedule(String key) {
		
		if (key.equalsIgnoreCase("video")){
			mSpinnerLast = 0;
			mPageURL = URL_CAMERA;
		}
		
		else if (key.equalsIgnoreCase("proyeccion")){
			mSpinnerLast = 1;
			mPageURL = URL_PROYECCION;
		}
		
		else if (key.equalsIgnoreCase("discos")){
			mSpinnerLast = 2;
			mPageURL = URL_DISCOS;
		}
	}
	
	public void spinnerSwitch(int position){
		
		if (mURLoaded){
			switch(position){
			
			case 0:
				mPageURL = URL_CAMERA;
				break;
				
			case 1:
				mPageURL = URL_PROYECCION;
				break;
			
			case 2:
				mPageURL = URL_DISCOS;
				break;
			}
			
			// Load new page
			Fragment_Turnos webPage = (Fragment_Turnos) getSupportFragmentManager().findFragmentByTag(TURNS_FRAGMENT);
			// Load page if fragment is visible (inflated). Else it will be loaded once the fragment has been inflated
			if (webPage != null) webPage.loadPage(mPageURL);
		}
	} 
	
	/** task to undergo when refresh button is pressed */
	@Override
	public void actionBarRefresh(){
		if (Tool.getInstance().isConnection()){
			Fragment_Turnos webPage = (Fragment_Turnos) getSupportFragmentManager().findFragmentByTag(TURNS_FRAGMENT);
			if (webPage != null) webPage.refreshPage();
			else {
				webPage = Fragment_Turnos.newInstance(mPageURL);
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ft.replace(R.id.frame_layout_turnos, webPage, TURNS_FRAGMENT);
				ft.commit();
			}
		}
		
		else {
			Toast.makeText(this, getResources().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
		}
	}
	
	public void retryConnection(View view){
		Fragment_Turnos webPage = (Fragment_Turnos) getSupportFragmentManager().findFragmentByTag(TURNS_FRAGMENT);
		if (webPage != null) webPage.refreshPage();
		else {
			webPage = Fragment_Turnos.newInstance(mPageURL);
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.frame_layout_turnos, webPage, TURNS_FRAGMENT);
			ft.commit();
		}
	}
	
	@Override
	public String onPageStarted(){
		mURLoaded = true;
		return mPageURL;
	}
	

	@Override
	public Toolbar getToolBar() {
		return mToolBar;
	}

	@Override
	public String getActionBarTitle() {
		// TODO Auto-generated method stub
		return TITLE;
	}

	@Override
	public void onDrawerOpen() {
		showSpinner(false);
	}

	@Override
	public void onDrawerClose() {
		if (fragVisible(TURNS_FRAGMENT)){ 
			showSpinner(true);
		}
		
	}
	
	private void showSpinner(boolean val){
		if (val){
			mShowTitle = false;
			mShowSpinner = true;
			mSpinner.setVisibility(View.VISIBLE);
			getSupportActionBar().setDisplayShowTitleEnabled(false);
		} else {
			mShowTitle = true;
			mShowSpinner = false;
			mSpinner.setVisibility(View.GONE);
			getSupportActionBar().setDisplayShowTitleEnabled(true);
		}
	}
	private boolean fragVisible(String TAG){
		Fragment fg = getSupportFragmentManager().findFragmentByTag(TAG);
		if (fg != null) return fg.isVisible();
		else return false;
	}
	
	@Override
	public void navigationSwitch(int position) {
		FragmentManager mg = getSupportFragmentManager();
		FragmentTransaction ft = mg.beginTransaction();
		
		switch(position){
		
		case 0:
			if (mNavigationLastPosition != position){
				Fragment_Turnos fg = (Fragment_Turnos) mg.findFragmentByTag(TURNS_FRAGMENT);
				if (fg == null) fg = Fragment_Turnos.newInstance(mPageURL);
				ft.replace(R.id.frame_layout_turnos, fg, TURNS_FRAGMENT);
				ft.commit();
			}
			
			break;
		
		case 1:
			if (mNavigationLastPosition != position){
				if (mSpinner != null) mSpinner.setVisibility(View.GONE);
				
				Fragment_Manuals fg = (Fragment_Manuals) mg.findFragmentByTag(MANUAL_FRAGMENT);
				if (fg == null) fg = Fragment_Manuals.newInstance();
				ft.replace(R.id.frame_layout_turnos, fg, MANUAL_FRAGMENT);
				ft.commit();
			}
			break;
		
		case 2:
			if (mNavigationLastPosition != position){
				if (mSpinner != null) mSpinner.setVisibility(View.GONE);
				
				Fragment_Contacts fg = (Fragment_Contacts) mg.findFragmentByTag(CONTACT_FRAGMENT);
				if (fg == null) fg = Fragment_Contacts.newInstance();
				ft.replace(R.id.frame_layout_turnos, fg, CONTACT_FRAGMENT);
				ft.commit();
			}
			break;
		
		case 3:
			if (mNavigationLastPosition != position){
				if (mSpinner != null) mSpinner.setVisibility(View.GONE);
				
				Fragment_Workers fg = (Fragment_Workers) mg.findFragmentByTag(WORKERS_FRAGMENT);
				if (fg == null) fg = Fragment_Workers.newInstance();
				ft.replace(R.id.frame_layout_turnos, fg, WORKERS_FRAGMENT);
				ft.commit();
			}
			break;
		}
		
		mNavigationLastPosition = position;
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if(mSpinnerLast != position){
			mSpinnerLast = position;
			spinnerSwitch(position);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}

}
