package prince.app.ccm.tools;

import java.io.File;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

/**
 * Asynchronous task class to handle downloads of manuals
 * @author Principe
 *
 */
public class ManualTask extends AsyncTask<String, Void, Boolean>{
	private static final String TAG = ManualTask.class.getSimpleName();
	private File mFileName;
	private boolean mOpenFile;
	private ProgressBar mTaskProgress;
	private int mIndexOfCard;
	
	private static AsyncCallback mCallback;
	
	public static interface AsyncCallback{
		public void onPostExecute(int position, boolean openFile, File filename, boolean result, boolean cancel);
		public void onCancel(int position);
	}
	
	public static void setCallback(AsyncCallback callback){
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
		if (mCallback != null) mCallback.onPostExecute(mIndexOfCard, mOpenFile, mFileName, success, isCancelled());
	}
		
	@Override
	protected void onCancelled() {
		if (mCallback != null) mCallback.onCancel(mIndexOfCard);
	}		
}
