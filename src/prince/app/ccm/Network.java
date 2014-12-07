/*
 * Copyright (C) 2014 The Android Open Source Project 
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

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;


public class Network extends Fragment{
	private Button mRetryButton;
	private Button mCheckButton;
	private clickListener  mListener;
	
	public static Network newInstance(){
		return new Network();
	}
	
	
	// Container Activity must implement this interface
    public interface clickListener {
    	/**
    	 *  Method called to retry loading page
    	 */
        public void onNetworkRetry();
        
        /**
         * Method called to check for network connection
         */
        public void onNetworkCheck();
    }
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Retain this fragment across configuration changes.
		setRetainInstance(true);
		
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mListener = (clickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement LoginListener");
        }
    }


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.layout_nowifi, container, false);
		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
	        }
	    }); 
		
		mRetryButton = (Button) view.findViewById(R.id.btn_retry);
	//	mCheckButton = (Button) view.findViewById(R.id.btn_check_network);
		mRetryButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
		          mListener.onNetworkRetry();
		        }
		});
		mCheckButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
		          mListener.onNetworkCheck();
		        }
		});
		
		return view;
	}
}