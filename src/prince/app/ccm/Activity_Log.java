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

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class Activity_Log extends FragmentActivity {
	
	@Override
	protected void onCreate(Bundle oldState) {
		super.onCreate(oldState);
		setContentView(R.layout.layout_frame);

		if (oldState == null){
			getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.frame, Fragment_Log.newInstance())
				.commit();
		}
	}
	
	@Override
	public void onBackPressed(){
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_empty, menu);
		return true;
	}
}
