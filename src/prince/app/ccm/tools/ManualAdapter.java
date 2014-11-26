package prince.app.ccm.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import prince.app.ccm.Activity_Manuals;
import prince.app.ccm.R;
import prince.app.ccm.util.InstallAPP;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ManualAdapter extends RecyclerView.Adapter<ManualAdapter.ManualViewHolder>{
	
	private ArrayList<ManualCards> manualList;
	private ArrayList<String> ongoingTasks;
	private ActionBarActivity context;
	private static final String TAG = ManualAdapter.class.getSimpleName();
	private ManualTask mManualTask;
	private String noConnection;
	private static final String TASK_ID = "Task_";
	
	private boolean mExitTaskEarly = false;
	
	public ManualAdapter(ArrayList<ManualCards> list, ArrayList<String> activeTask, ActionBarActivity ct){
		this.manualList = list;
		this.context = ct;
		noConnection = ct.getResources().getString(R.string.no_connection);
		ongoingTasks = activeTask;
	}

	@Override
	public int getItemCount() {
		return manualList.size();
	}

	@Override
	public void onBindViewHolder(ManualViewHolder viewHolder, int position) {
		final ManualCards row = manualList.get(position);
		final ManualViewHolder mHolder = viewHolder;
		final int index = position;
		Log.e(TAG, "create: " + position);
		
		mHolder.mImage.setImageDrawable(row.mManualImage);
		mHolder.mTitle.setText(row.mManualTitle);
		
		mHolder.mOpen.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if (index != 7){
					if (Activity_Manuals.fileExist(row.mFileName)){
						Log.e(TAG, "Opening - Filename: " + row.mFileName);
						openPDF(new File(Tool.APP_DIR,row.mFileName));
					} else{
						if (!ongoingTasks.contains(TASK_ID + index)){
							if (Tool.getInstance().isConnection()){
								Log.e(TAG, "Downloading - Filename: " + row.mFileName);
								mManualTask = new ManualTask(new File(Tool.APP_DIR,row.mFileName), true, mHolder.mProgress, index);
								mManualTask.execute(row.mURL);
								ongoingTasks.add(TASK_ID + index);
							}
							
							else {
								Toast.makeText(context, noConnection, Toast.LENGTH_LONG).show();
							}
						/*	DialogFragment dialog = new ManualDialog(mManualTask,row.mURL);
							dialog.show(context.getSupportFragmentManager(), TAG); */
						}
					}
				} else{
					openWeb(row.mURL);
				}
				
			}});
		
		mHolder.mUpdate.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if (Tool.getInstance().isConnection()){
					if (!ongoingTasks.contains(TASK_ID + index)){
						Log.e(TAG, "Updating - Filename: " + row.mFileName);
						mManualTask = new ManualTask(new File(Tool.APP_DIR,row.mFileName), false, mHolder.mProgress, index);
						mManualTask.execute(row.mURL);
						ongoingTasks.add(TASK_ID + index);
					}
				}
				
				else {
					Toast.makeText(context, noConnection, Toast.LENGTH_LONG).show();
				}
			}});
		
		// Show or Hide Update Button
		if (!Activity_Manuals.fileExist(row.mFileName)) mHolder.mUpdate.setVisibility(View.INVISIBLE);
		else mHolder.mUpdate.setVisibility(View.VISIBLE);
		
		// Show or hide Progress Bar
		if (ongoingTasks.contains(TASK_ID + index)) mHolder.mProgress.setVisibility(View.VISIBLE);
		else mHolder.mProgress.setVisibility(View.GONE);
	}
	
	public void notify(int position){
		
	}
	
	public ArrayList<String> getActiveTasks(){
		return ongoingTasks;
	}

	@Override
	public ManualViewHolder onCreateViewHolder(ViewGroup view, int arg1) {
		View item = LayoutInflater.from(view.getContext()).inflate(R.layout.layout_card_manuals, view, false);
		return new ManualViewHolder(item);
	}
	
	/** start intent to open PDF File */
	public void openPDF(File name){
		Intent target = new Intent(Intent.ACTION_VIEW);
		target.setDataAndType(Uri.fromFile(name),"application/pdf");
		target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		PackageManager packageManager = context.getPackageManager();
		List<ResolveInfo> activities = packageManager.queryIntentActivities(target, PackageManager.MATCH_DEFAULT_ONLY);
		boolean isIntentSafe = activities.size() > 0;
		
		if (isIntentSafe){
			try {
				context.startActivity(target);
			} catch (ActivityNotFoundException e) {
				// Instruct the user to install a PDF reader here, or something
				DialogFragment noAPP = InstallAPP.newInstance(R.string.pdf_header, R.string.pdf_message, R.string.pdf_positive, R.string.pdf_negative);
				noAPP.show(context.getSupportFragmentManager(), TAG);
			}
		} else{
			// Instruct the user to install a PDF reader here, or something
			DialogFragment noAPP = InstallAPP.newInstance(R.string.pdf_header, R.string.pdf_message, R.string.pdf_positive, R.string.pdf_negative);
			noAPP.show(context.getSupportFragmentManager(), TAG);
		}
	}
	
	public void openWeb(String URL){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData((Uri.parse(URL)));
		context.startActivity(intent);
	}
	
	/**
	 * Asynchronous Task Class to handle downloads of manuals
	 * @author Principe
	 *
	 */
	public class ManualTask extends AsyncTask<String, Void, Boolean>{
		private File mFileName;
		private boolean mOpenFile;
		private ProgressBar mTaskProgress;
		private int mIndexOfCard;
		
		public ManualTask(final File name, final boolean show, ProgressBar progress, int position){
			this.mOpenFile = show;
			this.mFileName = name;
			this.mTaskProgress = progress;
			this.mIndexOfCard = position;
		}
		
		@Override
		protected void onPreExecute(){
			mTaskProgress.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected Boolean doInBackground(String... params) {       
			boolean result = Tool.downloadPDF(params[0], mFileName);
			Log.e(TAG, "result: " + result);
			return result;
		}
				
		@Override
		protected void onPostExecute(Boolean success){
			if (!isCancelled() && !Tool.EXIT_TASK){
				if (success){
					if (mOpenFile && mFileName != null){
						openPDF(mFileName);
					}
					if (!mOpenFile) Toast.makeText(context, "Archivo actualizado", Toast.LENGTH_SHORT).show();
				}
					
				else Toast.makeText(context, "Error descargando archivo", Toast.LENGTH_SHORT).show();
			}
				
			mManualTask = null;
			boolean modified = ongoingTasks.remove(TASK_ID + mIndexOfCard);
			Log.e(TAG, "Value: " + modified);
			notifyItemChanged(mIndexOfCard);
		}
			
		@Override
		protected void onCancelled() {
			mManualTask = null;
			ongoingTasks.remove(TASK_ID + mIndexOfCard);
			notifyItemChanged(mIndexOfCard);
		}		
	}
	
	public class ManualDialog extends DialogFragment {
		private ManualTask mManualTask;
		private String URL;
		
		public ManualDialog(ManualTask task, String address){
			this.mManualTask = task;
			this.URL = address;
		}
		
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage(R.string.download_manual)
	               .setPositiveButton(R.string.pdf_positive, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       if (mManualTask != null && URL != null && !URL.isEmpty()){
	                    	   mManualTask.execute(URL);
	                       }
	                   }
	               })
	               .setNegativeButton(R.string.pdf_negative, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   mManualTask.cancel(true);
	                       getDialog().dismiss();
	                   }
	               });
	        // Create the AlertDialog object and return it
	        return builder.create();
	    }
	}
	
	public final static class ManualViewHolder extends RecyclerView.ViewHolder{
		protected ImageView mImage;
		protected TextView mTitle;
		protected Button mOpen;
		protected Button mUpdate;
		protected ProgressBar mProgress;

		public ManualViewHolder(View itemView) {
			super(itemView);
			mImage = (ImageView) itemView.findViewById(R.id.image_card);
			mTitle = (TextView)  itemView.findViewById(R.id.text_card_title);
			mOpen = (Button)     itemView.findViewById(R.id.btn_card_open);
			mUpdate = (Button)   itemView.findViewById(R.id.btn_card_update);
			mProgress = (ProgressBar) itemView.findViewById(R.id.progress_card);
			
		}
	}
	
}
