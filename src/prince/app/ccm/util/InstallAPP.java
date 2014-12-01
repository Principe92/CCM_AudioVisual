package prince.app.ccm.util;

import prince.app.ccm.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

/** A class that manages network connection dialogs */
public class InstallAPP extends DialogFragment {
	private static int sPositive;
	private static int sNegative;
	private static int sHeader;
	private static int sMessage;
	
	public static InstallAPP newInstance(int header, int message, int positive, int negative){
		sHeader = header;
		sPositive = positive;
		sNegative = negative;
		sMessage = message;
		final InstallAPP dialog = new InstallAPP();
		return dialog;
	}
	
	@Override
	public void onCreate(Bundle oldState){
		super.onCreate(oldState);
		
		setRetainInstance(true);
	}
	
    @Override
    public Dialog onCreateDialog(Bundle oldState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(sHeader)
        	   .setMessage(sMessage)
               .setPositiveButton(sPositive, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // search for an application that views pdf in the play store
           			Intent myIntent = new Intent(Intent.ACTION_VIEW);
        			myIntent.setData(Uri.parse("market://search?q=pdf reader&c=apps"));
        			try{
        				startActivity(myIntent);
        			}catch(ActivityNotFoundException e){
        				Toast.makeText(getActivity(), getResources().getString(R.string.no_google_store), Toast.LENGTH_LONG).show();
        			}
                   }
               })
               .setNegativeButton(sNegative, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                	   dialog.dismiss();
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}