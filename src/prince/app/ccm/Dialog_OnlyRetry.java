package prince.app.ccm;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A Dialog with an option to retry an intended action on failure.
 * This dialog has no header
 * This dialog is positioned at the bottom of the screen
 * @author Princewill Okorie
 *
 */
public class Dialog_OnlyRetry extends DialogFragment{
	public static final String MSG = "message";
	private String msg;
	private DialogCallback mCallback;
	
	public static interface DialogCallback{
		public void onRetry();
	}
	
	public void setCallback(DialogCallback callback){
		mCallback = callback;
	}
	
	public static final Dialog_OnlyRetry newInstance(String msg){
		final Dialog_OnlyRetry df = new Dialog_OnlyRetry();
		Bundle arg = new Bundle();
		arg.putString(MSG, msg);
		df.setStyle(STYLE_NO_TITLE, 0);
		df.setArguments(arg);
		return df;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle oldState){
		super.onCreateView(inflater, parent, oldState);
		View view = inflater.inflate(R.layout.layout_dialog_onretry, parent, false);
		
		msg = getArguments().getString(MSG);
		
		((TextView) view.findViewById(R.id.text_onlyretry)).setText(msg);
		((Button) view.findViewById(R.id.btn_onlyretry)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "RETRY", Toast.LENGTH_SHORT).show();
				if (mCallback != null) mCallback.onRetry();
			}
			
		});
		
		Window window = getDialog().getWindow();
		window.setGravity(Gravity.BOTTOM);
		
		return view;
	}
}
