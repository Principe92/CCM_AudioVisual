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


import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v4.preference.PreferenceFragment;

/**
 * Fragment showing the settings of the application
 * @author Princewill
 *
 */
public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener{
	public static final String ENABLED = "Activado";
	public static final String DISABLED = "Desactivado";
	public static final String DEFAULT_PAGE = "Page to launch on start: ";
	
	
	@Override
	public void onCreate(Bundle oldState){
		super.onCreate(oldState);
		
		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.app_preference);
		
		
		attachListener(findPreference(getResources().getString(R.string.pref_defaultPage_key)));
		attachListener(findPreference(getResources().getString(R.string.pref_videoUrl_key)));
		attachListener(findPreference(getResources().getString(R.string.pref_proyUrl_key)));
		attachListener(findPreference(getResources().getString(R.string.pref_discosUrl_key)));
		
		Preference pref = findPreference(getResources().getString(R.string.pref_app_version_key));
		try {
			String versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
			pref.setSummary(versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Preference.OnPreferenceChangeListener sPreferenceListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			String stringValue = newValue.toString();
			// TODO Auto-generated method stub
			if (preference instanceof ListPreference){
				
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);

				// Set the summary to reflect the new value.
				preference
						.setSummary(/*DEFAULT_PAGE + */(index >= 0 ? listPreference.getEntries()[index]
								: ""));
			}
			
			else if (preference instanceof SwitchPreference){
				preference.setSummary((Boolean) (newValue)? ENABLED : DISABLED);
			}
			
			else if (preference instanceof EditTextPreference){
				preference.setSummary(stringValue);
				updateEditTextVars(preference, (String) newValue);
			}
			
			return true;
		}
	};
	
	private void attachListener(Preference preference) {
		// Set the listener to watch for value changes.
		preference
				.setOnPreferenceChangeListener(sPreferenceListener);

		// Trigger the listener immediately with the preference's
		// current value.
		if (preference instanceof SwitchPreference){
			sPreferenceListener.onPreferenceChange(
					preference,
					PreferenceManager.getDefaultSharedPreferences(
							preference.getContext()).getBoolean(preference.getKey(),
							false));
		}
		else if (preference instanceof ListPreference){
			sPreferenceListener.onPreferenceChange(
					preference,
					PreferenceManager.getDefaultSharedPreferences(
							preference.getContext()).getString(preference.getKey(),
							""));
		}
		else if (preference instanceof EditTextPreference){
			sPreferenceListener.onPreferenceChange(
					preference,
					PreferenceManager.getDefaultSharedPreferences(
							preference.getContext()).getString(preference.getKey(),
							""));
		}
		
		
	}
	
	public void updateEditTextVars(Preference pref, String newValue){
		if (pref.getKey().equalsIgnoreCase(getResources().getString(R.string.pref_videoUrl_key))){
			Activity_Main.URL_CAMERA = newValue;
		}
		
		else if (pref.getKey().equalsIgnoreCase(getResources().getString(R.string.pref_proyUrl_key))){
			Activity_Main.URL_PROYECCION = newValue;
		}
		
		else if (pref.getKey().equalsIgnoreCase(getResources().getString(R.string.pref_discosUrl_key))){
			Activity_Main.URL_DISCOS = newValue;
		}
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    getPreferenceScreen().getSharedPreferences()
	            .registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    getPreferenceScreen().getSharedPreferences()
	            .unregisterOnSharedPreferenceChangeListener(this);
	}


	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		// TODO Auto-generated method stub
		
	}
}
