package prince.app.ccm.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import prince.app.ccm.Dialog_OnlyRetry;
import prince.app.ccm.Dialog_OnlyRetry.DialogCallback;
import prince.app.ccm.R;
import prince.app.ccm.delete.Activity_Manuals;
import prince.app.ccm.tools.ManualTask.AsyncCallback;
import prince.app.ccm.util.InstallAPP;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
	
	private ArrayList<ManualHolder> manualList;
	private ArrayList<String> ongoingTasks;
	private ActionBarActivity context;
	
	private static final String TAG = ManualAdapter.class.getSimpleName();
	private static final String NETWORK_TAG = "net_error";
	
	private ManualTask mManualTask;
	private static final String TASK_ID = "Task_";
	
	public ManualAdapter(ArrayList<ManualHolder> list, ArrayList<String> activeTask, ActionBarActivity ct){
		this.manualList = list;
		this.context = ct;
		ongoingTasks = activeTask;
		setUpListener();
	}
	
	public ManualAdapter(ArrayList<ManualHolder> list, ActionBarActivity ct){
		this.manualList = list;
		this.context = ct;
		ongoingTasks = new ArrayList<String>();
		setUpListener();
	}

	@Override
	public int getItemCount() {
		return manualList.size();
	}
	
	public void setUpListener(){
		ManualTask.setCallback(new AsyncCallback(){

			@Override
			public void onPostExecute(int position, boolean mOpenFile, File mFileName, boolean success, boolean cancel) {
				if (!cancel && !Tool.EXIT_TASK){
					if (success){
						if (mOpenFile && mFileName != null){
							openPDF(mFileName);
						}
						if (!mOpenFile) Toast.makeText(context, "Archivo actualizado", Toast.LENGTH_SHORT).show();
					}
						
					else errorHandler();
				}

				boolean modified = ongoingTasks.remove(TASK_ID + position);
				Log.e(TAG, "Value: " + modified);
				notifyItemChanged(position);
				
			}

			@Override
			public void onCancel(int position) {
				mManualTask = null;
				ongoingTasks.remove(TASK_ID + position);
				notifyItemChanged(position);
				
			}
			
		});
	}

	@Override
	public void onBindViewHolder(ManualViewHolder viewHolder, int position) {
		final ManualHolder row = manualList.get(position);
		final ManualViewHolder mHolder = viewHolder;
		final int index = position;
		
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
							Log.e(TAG, "Downloading - Filename: " + row.mFileName);
							mManualTask = new ManualTask(new File(Tool.APP_DIR,row.mFileName), true, mHolder.mProgress, index);
							mManualTask.execute(row.mURL);
							ongoingTasks.add(TASK_ID + index);
						}
					}
				} else{
					openWeb(row.mURL);
				}
				
			}});
		
		mHolder.mUpdate.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if (!ongoingTasks.contains(TASK_ID + index)){
					Log.e(TAG, "Updating - Filename: " + row.mFileName);
					mManualTask = new ManualTask(new File(Tool.APP_DIR,row.mFileName), false, mHolder.mProgress, index);
					mManualTask.execute(row.mURL);
					ongoingTasks.add(TASK_ID + index);
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
	
	private void errorHandler(){
		String msg;
		if (!Tool.getInstance().isConnection()){
			msg = context.getResources().getString(R.string.no_connection);
		} else {
			msg = context.getResources().getString(R.string.connection_error);
		}
		
		FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
		Fragment prev = context.getSupportFragmentManager().findFragmentByTag(NETWORK_TAG);
		if (prev != null) ft.remove(prev);
		
		
		Dialog_OnlyRetry df = Dialog_OnlyRetry.newInstance(msg);
		df.setCallback(new DialogCallback(){

			@Override
			public void onRetry() {
				Toast.makeText(context, "RETRY", Toast.LENGTH_SHORT).show();
				
			}
			
		});
		
		df.show(ft, NETWORK_TAG);
		// TODO: Still have to work on asynctask showing dialog when the activity have passed its onSavedState
	}
	
	public void dismissDialogs(){
		FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
		Fragment prev = context.getSupportFragmentManager().findFragmentByTag(NETWORK_TAG);
		if (prev != null){
			Log.e(TAG, "fragment removed");
			ft.remove(prev);
		}
		
		ft.commit();
	}
	
	/**
	 * Asynchronous task class to handle downloads of manuals
	 * @author Principe
	 *
	 */
/*	private class ManualTask extends AsyncTask<String, Void, Boolean>{
		private File mFileName;
		private boolean mOpenFile;
		private ProgressBar mTaskProgress;
		private int mIndexOfCard;
		
		private DialogCallback mCallback;
		
		public static interface DialogCallback{
			public void onRetry();
		}
		
		public void setCallback(DialogCallback callback){
			mCallback = callback;
		}
		
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
					
				else errorHandler();
			}

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
	} */
	
	/**
	 * ViewHolder class for the each manual
	 * @author Prince O.
	 *
	 */
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
