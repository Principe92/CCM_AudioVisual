package prince.app.ccm.tools;

import prince.app.ccm.Activity_Log;
import prince.app.ccm.R;
import prince.app.ccm.util.Util;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

public abstract class ActivityBase extends ActionBarActivity implements AlertDialogX.AlertXListener{
	public static final String TAG = ActivityBase.class.getSimpleName();
	
	protected DrawerLayout mDrawerLayout;
    protected ListView mDrawerList;
    protected ActionBarDrawerToggle mDrawerToggle;
    
    protected String mDrawerTitle;
    
    protected int mLastClicked;
    
    // Saved State KEYS
    private static final String SAVED_LAST_CLICK = "last_clicked_save";
	protected static final String SAVED_TITLE = "actionbar";
    
    @Override
    protected void onStop(){
		super.onStop();
		mDrawerLayout.closeDrawers();
	}
    
	public void initNavigationDrawer(){
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_audio_main);
		mDrawerList = (ListView) findViewById(R.id.list_drawer_main);
		mDrawerList.setFastScrollEnabled(true);
	        
		//set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
	      
		ImageView drawer = (ImageView)findViewById(R.id.image_drawer_main);
		drawer.setLayoutParams(new LinearLayout.LayoutParams(
				getResources().getDimensionPixelSize(R.dimen.drawer_photo_width),
				getResources().getDimensionPixelSize(R.dimen.drawer_photo_height)));
		drawer.setScaleType(ImageView.ScaleType.CENTER_CROP);
	        
		drawer.setImageResource(R.drawable.drawer_photo);
	        
		// Set the adapter for the list view
		mDrawerList.setAdapter(new ArrayAdapter<String>(	this, 
															R.layout.month_list, 
															getResources().getStringArray(R.array.array_navigation)));
		// mDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, monthList));
	        
		// set the default action bar title
		if (mDrawerTitle == null || mDrawerTitle.isEmpty()) mDrawerTitle = getActionBarTitle();
		getSupportActionBar().setTitle(mDrawerTitle);
		
		//Set the list's click listener
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		mDrawerToggle = new ActionBarDrawerToggle(
	                this,                  /* host Activity */
	                mDrawerLayout,         /* DrawerLayout object */
	                getToolBar(),  /* nav drawer image to replace 'Up' caret */
	                R.string.drawer_open,  /* "open drawer" description for accessibility */
	                R.string.drawer_close  /* "close drawer" description for accessibility */
	                ){
	        	/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getSupportActionBar().setTitle(mDrawerTitle);
				onDrawerClose();
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getSupportActionBar().setTitle("Menu");
				onDrawerOpen();
			}

		};
	        
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	        
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	    	
	        // modify last clicked list item
	    	mLastClicked = position;
	    	
	    	// load adequate page
	    	navigationSwitch(position);
	    		
	    	// change title of action bar
	    	mDrawerTitle = Tool.getInstance().changeTitle(position);
	    	getSupportActionBar().setTitle(mDrawerTitle);
	    	
	    	mDrawerLayout.closeDrawers();
	    }
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
	
	@Override
	public void onPosClick(String type) {
		Tool.getInstance().modBolPref(Util.SIGN_IN, false);
		Intent intent = new Intent(this, Activity_Log.class);
		startActivity(intent);
		finish();
	}
	
	@Override
	public void onSaveInstanceState(Bundle state) {
	    // Save the user's current game state
		super.onSaveInstanceState(state);
		
	    state.putString(SAVED_TITLE, mDrawerTitle);
	    state.putInt(SAVED_LAST_CLICK, mLastClicked);
	}
	
	/**
	 * Method called when an item on the navigation drawer is clicked
	 * @param position
	 */
	public abstract  void navigationSwitch(int position);
	
	/**
	 * Method to fetch the inflated toolbar
	 * @return - Toolbar object
	 */
	public abstract Toolbar getToolBar();
	
	/**
	 * Method to get the title of the action bar
	 * @return - a string representation of the title
	 */
	public abstract String getActionBarTitle();
	
	public abstract void onDrawerOpen();
	
	public abstract void onDrawerClose();
}
