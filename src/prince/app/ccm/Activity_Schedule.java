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

import java.util.ArrayList;

import prince.app.ccm.tools.ActivityBase;
import prince.app.ccm.tools.AlertDialogX;
import prince.app.ccm.tools.ScheduleSDK;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class Activity_Schedule extends ActivityBase implements OnItemSelectedListener, OnMenuItemClickListener{
	private static final String TAG = Activity_Schedule.class.getSimpleName();
	private static final String LOG_OUT = "salir";
	
	private Toolbar mToolBar;
	
	private ViewPager mPager;
	private PageAdapter mAdapter;
	private Spinner mSpinner;
	
	@Override
	public void onCreate(Bundle oldState){
		super.onCreate(oldState);
		setContentView(R.layout.layout_viewpager);
		
		mToolBar = (Toolbar) findViewById(R.id.bar_viewpager);
		setSupportActionBar(mToolBar);
		
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		
		
		initSpinner();
		initPager();
		initNavigationDrawer();
	}
	
	public void initPager(){
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setOffscreenPageLimit(2); 
		mPager.setPageMargin(2);
		mPager.setPageMarginDrawable(R.color.primary_material_light);
		mAdapter = new PageAdapter(getSupportFragmentManager(), ScheduleSDK.fetchEditionCount(), 0);
		mPager.setAdapter(mAdapter);
	}
	
	public void initSpinner(){
		mSpinner = (Spinner) findViewById(R.id.spinner_viewpager);
		
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ScheduleSDK.fetchWorkers());
		
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		// Apply the adapter to the spinner
		mSpinner.setAdapter(adapter);
		mSpinner.setOnItemSelectedListener(this);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_schedule, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
	/*	if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		} */
		
		int itemId = item.getItemId();
		
		if (itemId == R.id.action_main_refresh) {
		//	actionBarRefresh();
			return true;
		}
		
		if (itemId == R.id.action_web) {
			Intent intent = new Intent(this, Activity_Main.class);
			startActivity(intent);
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
															LOG_OUT);
	    	
	    	aT.show(getSupportFragmentManager(), LOG_OUT);
			return true;
		}
		
		else {
			return super.onOptionsItemSelected(item);
		}
	}
	
	public void showPopup(View view){
		PopupMenu popup = new PopupMenu(this, view);
		popup.setOnMenuItemClickListener(this);
	    popup.inflate(R.menu.menu_pop);
	    popup.show();
	}
	
	/**
	 * Menu shown when the popup items are clicked
	 */
	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
		
        case R.id.action_pop_note:
            // Show Notes
        	DialogFragment aT = Fragment_ShowNotes.newInstance(ScheduleSDK.fetchNote());
        	aT.show(getSupportFragmentManager(), TAG);
            return true;
        
        case R.id.action_pop_list:
        	// Show List
        	int position = mPager.getCurrentItem();
        	ArrayList<String> workerList = ScheduleSDK.fetchWorkerList(position + 1);
        	DialogFragment list = Fragment_ShowLists.newInstance(workerList);
        	list.show(getSupportFragmentManager(), TAG);
        default:
            return false;
    }

	}
	
	public static class PageAdapter extends FragmentStatePagerAdapter {
        private final int mSize;
        private int mWho;
        SparseArray<Fragment_Schedule> mPages;
        
        public PageAdapter(FragmentManager fm, int size, int mWorker) {
            super(fm);
            mSize = size;
            mWho = mWorker;
            mPages = new SparseArray<Fragment_Schedule>();
        }

        @Override
        public int getCount() {
        	return mSize;
        }
        
      /*  @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
            //TODO Not the Best Solution
        } */

        @Override
        public Fragment getItem(int pos) {
        	Fragment_Schedule page = Fragment_Schedule.newInstance(pos, mWho);
        //	mPages.put(pos, page);
            return page;
        }
        
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
        	Fragment_Schedule page = (Fragment_Schedule) super.instantiateItem(container, position);
            mPages.put(position, page);
            return page;
        } 
        
        @Override
        public void destroyItem(ViewGroup collection, int position, Object o) {
        	super.destroyItem(collection, position, o);
            mPages.remove(position);
        }
        
        @Override
        public void notifyDataSetChanged() {
            int key = 0;
            for(int i = 0; i < mPages.size(); i++) {
               key = mPages.keyAt(i);
               Fragment_Schedule page = mPages.get(key);
               page.refresh(mWho);
            }
            super.notifyDataSetChanged();
        }
        
        public void changeWorker(int newWorker){
        	mWho = newWorker;
        	notifyDataSetChanged();
        }
    }

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
		mAdapter.changeWorker(pos);
		
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDrawerClose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionBarRefresh() {
		// TODO Auto-generated method stub
		
	}

}
