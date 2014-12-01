package prince.app.ccm;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class Fragment_NavigationDrawer extends Fragment {
	private static final String TAG = Fragment_NavigationDrawer.class.getSimpleName();

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private ListAdapter listAdapter;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    protected String mDrawerTitle;
    private String onDrawerOpen;
    
    protected static final String SAVED_TITLE = "actionbar";

    public static final Fragment_NavigationDrawer newInstance() {
    	final Fragment_NavigationDrawer fg = new Fragment_NavigationDrawer();
    	return fg;
    }

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
        onDrawerOpen = getResources().getString(R.string.drawer_open_title);

        if (savedState != null) {
            mCurrentSelectedPosition = savedState.getInt(STATE_SELECTED_POSITION);
            mDrawerTitle = savedState.getString(SAVED_TITLE);
            mFromSavedInstanceState = true;
        }
        
        int [] menuIcons = {R.drawable.ic_today_grey600_24dp, 
        					R.drawable.ic_assignment_grey600_24dp,
        					R.drawable.ic_person_grey600_24dp};
        
        listAdapter = new ListAdapter(	getActivity(), 
        								R.layout.layout_nav_listitem,
        								getResources().getStringArray(R.array.array_navigation),
        								menuIcons);

        Log.e(TAG, "create called");
        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
    	super.onCreateView(inflater, container, savedState);
    	View view = inflater.inflate(R.layout.layout_navigation, container, false);
    	
    	mDrawerListView = (ListView) view.findViewById(R.id.list_drawer_main);
		mDrawerListView.setFastScrollEnabled(true);
		
		mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
		
		// Set the adapter for the list view
        mDrawerListView.setAdapter(listAdapter);
        
        ImageView drawer = (ImageView) view.findViewById(R.id.image_drawer_main);
        drawer.setImageResource(R.drawable.drawer_photo);
    	
        return view;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
    	mFragmentContainerView = getActivity().findViewById(fragmentId);
    	mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        android.support.v7.app.ActionBar actionBar = getActionBar(); 
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(mDrawerTitle);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    	/* host Activity */
                mDrawerLayout,                    	/* DrawerLayout object */
                mCallbacks.getToolBar(),			/* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  				/* "open drawer" description for accessibility */
                R.string.drawer_close  				/* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
				getActionBar().setTitle(mDrawerTitle);
				mCallbacks.onDrawerClose();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
				getActionBar().setTitle(getResources().getString(R.string.drawer_open_title));
				mCallbacks.onDrawerOpen();

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        mDrawerTitle = getResources().getStringArray(R.array.array_navigation)[position];
        
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.navigationSwitch(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
    
    private android.support.v7.app.ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        
        outState.putString(SAVED_TITLE, mDrawerTitle);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        public void navigationSwitch(int position);
        
    	/**
    	 * Method to fetch the inflated toolbar
    	 * @return - Toolbar object
    	 */
    	public Toolbar getToolBar();
    	
    	public void onDrawerOpen();
    	
    	public void onDrawerClose();
    }
    
    public class ListAdapter extends ArrayAdapter<String> {
    	Context context;
    	private int [] drawables;

    	public ListAdapter(Context context, int resourceId, String[] strings, int [] menuImages) {
    		super(context, resourceId, strings);
    		this.context = context;
    		this.drawables = menuImages;
    	}
    	
    	private class ViewHolder {
            ImageView menuImage;
            TextView menuItem;
        }
    	
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            String menuItem = getItem(position);
            int menuImage = drawables[position];

     
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.layout_nav_listitem, parent, false);
                holder = new ViewHolder();
                holder.menuImage = (ImageView) convertView.findViewById(R.id.image_nav_list);
                holder.menuItem = (TextView) convertView.findViewById(R.id.text_nav_list);
                convertView.setTag(holder);
            } else
                holder = (ViewHolder) convertView.getTag();
            
            
            holder.menuImage.setImageDrawable(getResources().getDrawable(menuImage));
            holder.menuItem.setText(menuItem);
     
            return convertView;
        }
    }
}
