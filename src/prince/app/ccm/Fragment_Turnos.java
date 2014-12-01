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
public class Fragment_Web extends Fragment{
	
	public static final String ARG = "webpage";
	private static final String ENCODING = "windows-1252";
	private static String TAG = Fragment_Web.class.getSimpleName();
	
	// WebView variables
	private static String sWebViewURL;
	private static WebView schedule;
	private static WebListener weblistener;
	
	private MultiSwipeRefreshLayout mSwipeRefreshLayout;
	private ProgressBar mProgress;
	
	
	
	public static Fragment_Web newInstance(String mURL){
		final Fragment_Web mWebPage = new Fragment_Web();
		Bundle mBundle = new Bundle();
		mBundle.putString(ARG, mURL);
		mWebPage.setArguments(mBundle);
		return mWebPage;
	}
	
	public String getURL(){
		return getArguments().getString(ARG);
	}
	
	
	// Container Activity must implement this interface
    public interface WebListener {
        /** Detects when an error occurred while downloading page*/
        public void onPageError();
    }
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        try {
            weblistener = (WebListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement WebListener");
        }
    }

	
	@Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    sWebViewURL = getArguments() != null ? getArguments().getString(ARG) : "";

	    // Retain this fragment across configuration changes.
	    setRetainInstance(true);
	  }
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		FrameLayout wrapper = new FrameLayout(getActivity()); 
		View view = inflater.inflate(R.layout.layout_web, wrapper, true);
		
        // Retrieve the SwipeRefreshLayout and GridView instances
        mSwipeRefreshLayout = (MultiSwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        
        // Set the color scheme of the SwipeRefreshLayout by providing 4 color resource ids
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.swipe_color_1, R.color.swipe_color_2,
                R.color.swipe_color_3, R.color.swipe_color_5);
		
        mProgress = (ProgressBar) view.findViewById(R.id.web_progress);
        
		schedule = (WebView) view.findViewById(R.id.text_audio_main);
		schedule.setWebViewClient(new WebViewClient(){
			public void onPageStarted(WebView view, String url, Bitmap favicon){
				// hide the webView
				schedule.setVisibility(View.INVISIBLE);
				mProgress.setVisibility(View.VISIBLE);
				
				Log.i(TAG, "starting download at: " + Tool.time());
			}
			
			public void onPageFinished(WebView view, String url){	
		        // Stop the refreshing indicator
		        mSwipeRefreshLayout.setRefreshing(false);
		        
		        // show the webView
				schedule.setVisibility(View.VISIBLE);
				mProgress.setVisibility(View.INVISIBLE);
				
				Log.i(TAG, "finished downloading at: " + Tool.time());
			}
			
			public void onReceivedError(WebView view, int errorCod, String description, String failingUrl) {
	            weblistener.onPageError();
	        }
		});
		
		
		WebSettings settings = schedule.getSettings();
		settings.setDefaultTextEncodingName(ENCODING);
		
		// Load web page
	    schedule.loadUrl(sWebViewURL);
	    
		return view;
	}
	
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Tell the MultiSwipeRefreshLayout which views are swipeable.
        mSwipeRefreshLayout.setSwipeableChildren(R.id.text_audio_main);
        
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
                
                if (Tool.getInstance().isConnection()){
                	refreshPage();
                }
                else{
                	Toast.makeText(getActivity(), "No hay conexión!", Toast.LENGTH_LONG).show();
                	
    		        // Stop the refreshing indicator
    		        mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        
    }
	
	public void loadPage(String address){
		sWebViewURL = address;
		schedule.loadUrl(address);
	}
	
	public void refreshPage(){
		schedule.reload();
	}
}
