package prince.app.ccm;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Fragment class that shows the details of a particular facebook album
 * @author Princewill Okorie
 *
 */
public class Dialog_ShowNotes extends DialogFragment{
	private static final String TAG = Dialog_ShowNotes.class.getSimpleName();
	private static final String NOTES = "position";
	private String mNotes;
	
	public static Dialog_ShowNotes newInstance(String notes){
		Bundle args = new Bundle ();
		args.putString(NOTES, notes);
		final Dialog_ShowNotes mNewDetails = new Dialog_ShowNotes();
		mNewDetails.setArguments(args);
		return mNewDetails;
	}
	
	@Override
	public void onCreate(Bundle oldState){
		super.onCreate(oldState);
		
		mNotes = getArguments().getString(NOTES);
		
		setRetainInstance(true);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle oldState){
		super.onCreateView(inflater, parent, oldState);
		View view = inflater.inflate(R.layout.layout_notes, parent, false);
		
		// set the album name
		TextView mNotesView = (TextView) view.findViewById(R.id.text_notas);
		mNotesView.setText(mNotes);
		getDialog().setTitle("Notas");
		
		return view;
	}
	
}
