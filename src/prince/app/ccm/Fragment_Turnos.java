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

import prince.app.ccm.tools.Tool;
import prince.app.ccm.util.MultiSwipeRefreshLayout;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Fragment that hosts a web page showing the schedules for different departments.
 * @author Princewill Okorie
 *
 */
public class Fragment_Turnos extends Fragment{
	
	public static final String WEB_URL = "webpage";
	private static final String ENCODING = "windows-1252";
	private static String TAG = Fragment_Turnos.class.getSimpleName();
	
	// URL's
	public static String URL_CAMERA;
	public static String URL_PROYECCION;
	public static String URL_DISCOS;
	
	// WebView variables
	private String mWebViewURL;
	private WebView schedule;
	private WebListener mCallback;
	private ViewStub mStub;
	private View mStubInflatedView;
	
	private MultiSwipeRefreshLayout mSwipeRefreshLayout;
	private ProgressBar mProgress;
	
	
	public static Fragment_Turnos newInstance(String mURL){
		final Fragment_Turnos mWebPage = new Fragment_Turnos();
		Bundle mBundle = new Bundle();
		mBundle.putString(WEB_URL, mURL);
		mWebPage.setArguments(mBundle);
		return mWebPage;
	}
	
	public String getURL(){
		return getArguments().getString(WEB_URL);
	}
	
	
	// Container Activity must implement this interface
    public interface WebListener {
        
        public String onPageStarted();
    }
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        try {
            mCallback = (WebListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement WebListener");
        }
    }
	
	@Override
	  public void onCreate(Bundle savedState) {
	    super.onCreate(savedState);
	    	
	    mWebViewURL = getArguments() != null ? getArguments().getString(WEB_URL) : "";
	  }
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		FrameLayout wrapper = new FrameLayout(getActivity()); 
		View view = inflater.inflate(R.layout.layout_web, wrapper, true);
		
        setUp(view);
	    
		return view;
	}
	
	private void setUp(View view){
		// Retrieve the SwipeRefreshLayout and GridView instances
        mSwipeRefreshLayout = (MultiSwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        
        // Set the color scheme of the SwipeRefreshLayout by providing 4 color resource ids
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.swipe_color_1, R.color.swipe_color_2,
                R.color.swipe_color_3, R.color.swipe_color_5);
		
        mProgress = (ProgressBar) view.findViewById(R.id.web_progress);
        
        // Initialize the WebView
		schedule = (WebView) view.findViewById(R.id.text_audio_main);
	    schedule.getSettings().setSupportZoom(true);
	    
		WebSettings settings = schedule.getSettings();
		settings.setDefaultTextEncodingName(ENCODING);
		
		mStub = (ViewStub) view.findViewById(R.id.stub_internet);
	}
	
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Tell the MultiSwipeRefreshLayout which views are swipeable.
        mSwipeRefreshLayout.setSwipeableChildren(R.id.text_audio_main);
        
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefreshListener launched");
                
                if (Tool.getInstance().isConnection()){
                	refreshPage();
                }
                else{
                	Toast.makeText(getActivity(), getResources().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                	
    		        // Stop the refreshing indicator
    		        mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        
        schedule.setWebViewClient(new WebViewClient(){
			public void onPageStarted(WebView view, String url, Bitmap favicon){
				// hide the webView
				schedule.setVisibility(View.INVISIBLE);
				mProgress.setVisibility(View.VISIBLE);
				
				Log.e(TAG, "starting download at: " + Tool.time());
			}
			
			public void onPageFinished(WebView view, String url){	
		        // Stop the refreshing indicator
				mSwipeRefreshLayout.setRefreshing(false);
		        
		        // show the webView
				schedule.setVisibility(View.VISIBLE);
				mProgress.setVisibility(View.INVISIBLE);
				
				Log.e(TAG, "finished downloading at: " + Tool.time());
			}
			
			public void onReceivedError(WebView view, int errorCod, String description, String failingUrl) {
	           // mCallback.onPageError();
				handleError();
	        }
		});
        
    }
    
    @Override
    public void onActivityCreated (Bundle savedState) {
        super.onActivityCreated(savedState);
        
        // Load the URL Page
        mWebViewURL = mCallback.onPageStarted();
        schedule.loadUrl(mWebViewURL);
    }
	
	public void loadPage(String address){
		mWebViewURL = address;
		schedule.loadUrl(address);
	}
	
	public void refreshPage(){
		if (mStubInflatedView != null && mStubInflatedView.getVisibility() == View.VISIBLE) mStubInflatedView.setVisibility(View.GONE);
		
		schedule.reload();
	}
	
	public void handleError(){
		if (mStubInflatedView != null) mStubInflatedView.setVisibility(View.VISIBLE);
		else mStubInflatedView = mStub.inflate();
	}
}
