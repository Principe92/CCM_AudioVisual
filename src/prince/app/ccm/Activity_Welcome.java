package prince.app.ccm;

import prince.app.ccm.tools.Tool;
import prince.app.ccm.util.Util;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Activity_Welcome extends Activity {
	private boolean mSignedIn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_welcome);
		
		Handler handler = new Handler();
		handler.postDelayed(new Runnable(){
			@Override
			public void run(){
				LaunchMain();
			}
		}, 2000);  
		
	}

	private void LaunchMain(){
		mSignedIn = Tool.getInstance().getBolPref(Util.SIGN_IN);
		
		if (!mSignedIn){
			Intent intent = new Intent(this, Activity_Log.class);
			startActivity(intent);
		}
		else{
			Intent intent = new Intent(this, Activity_Turnos.class);
			startActivity(intent);
		}
	}
}

