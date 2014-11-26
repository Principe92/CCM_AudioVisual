/**
 * 
 */
package prince.app.ccm;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * @author Princewill Chibututu Okorie
 *
 */
public class SettingsActivity extends ActionBarActivity{
	
	@Override
	protected void onCreate(Bundle savedState) {
		// TODO Auto-generated method stub
		super.onCreate(savedState);
		setContentView(R.layout.layout_frame);
		
		getSupportActionBar().setLogo(R.drawable.launcher);
		getSupportActionBar().setDisplayUseLogoEnabled(true);
		
		// Display the fragment as the main content.
		getFragmentManager().beginTransaction()
							.replace(R.id.frame,new SettingsFragment())
							.commit();
		
	 }
}
