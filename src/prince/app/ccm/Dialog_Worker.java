package prince.app.ccm;

import prince.app.ccm.tools.WorkerAdapter;
import prince.app.ccm.tools.WorkerHolder;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class Dialog_Worker extends DialogFragment{
	public static final String TAG = Dialog_Worker.class.getSimpleName();
	public static final String WORKER = "worker_details";
	
	
	public static final Dialog_Worker newInstance(){
		final Dialog_Worker df = new Dialog_Worker();
		df.setStyle(STYLE_NO_TITLE, 0);
		return df;
	}
	
/*	public void setUpWorker(WorkerHolder data){
		mWorker = data;
	} */
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedState){
		super.onCreateView(inflater, parent, savedState);
		View view = inflater.inflate(R.layout.layout_worker_details, parent, false);
		
		setUpView(view);
		
		return view;
	}
	
	private void setUpView(View view){
		WorkerHolder mWorker = WorkerAdapter.workerDetails;
		
		// Insert worker image
		ImageView image = (ImageView) view.findViewById(R.id.image_worker_details);
		image.setImageDrawable(getResources().getDrawable(mWorker.workerImage));
		
		// Insert the worker name
		TextView name = (TextView) view.findViewById(R.id.text_worker_id);
		name.setText(mWorker.workerName);
		
		// Insert the worker role
		TextView role = (TextView) view.findViewById(R.id.text_worker_role);
		role.setText(mWorker.workerRole);
		
		// Insert the worker number
		TextView number = (TextView) view.findViewById(R.id.text_worker_number);
		number.setText(mWorker.workerNumbers[0]);
		
		// Insert the worker email
		TextView email = (TextView) view.findViewById(R.id.text_worker_email);
		email.setText(mWorker.workerEmails[0]);
		
		// Insert the worker address
		TextView address = (TextView) view.findViewById(R.id.text_worker_address);
		address.setText(mWorker.workerAddress);
		
		ImageButton back = (ImageButton) view.findViewById(R.id.btn_dismiss);
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				getDialog().dismiss();
			}
			
		});
	}
}
