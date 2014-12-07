package prince.app.ccm.tools;

import java.util.ArrayList;

import prince.app.ccm.Dialog_Call;
import prince.app.ccm.Dialog_Email;
import prince.app.ccm.Dialog_Message;
import prince.app.ccm.Dialog_Worker;
import prince.app.ccm.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WorkerAdapter extends RecyclerView.Adapter<WorkerAdapter.WorkerViewHolder> implements OnMenuItemClickListener{
//	private static final String TAG = WorkerAdapter.class.getSimpleName();
	private static final String DETAILS_TAG = "details";
	private static final String CALL_TAG = "call_dialog";
	private static final String NETWORK_TAG = "network_dialog";
	private static final String EMAIL_TAG = "email_dialog";
	private static final String SMS_TAG ="sms_dialog";
	
	private ArrayList<WorkerHolder> workerList;
	private ActionBarActivity context;
	public static WorkerHolder workerDetails;

	
	private static final int CORNER_RADIUS = 24; // dips
	private static final int MARGIN = 12; // dips

	private final int mCornerRadius;
	private final int mMargin;
	private int mOptionPosition = -1;
	private PopupMenu mOptions;
	
	public WorkerAdapter(ArrayList<WorkerHolder> list, ActionBarActivity ct){
		this.workerList = list;
		this.context = ct;
		
		final float density = context.getResources().getDisplayMetrics().density;
		mCornerRadius = (int) (CORNER_RADIUS * density + 0.5f);
		mMargin = (int) (MARGIN * density + 0.5f);
	}

	@Override
	public int getItemCount() {
		return workerList.size();
	}

	@Override
	public void onBindViewHolder(WorkerViewHolder viewHolder, int position) {
		final WorkerHolder worker = workerList.get(position);
		final WorkerViewHolder mHolder = viewHolder;
		final int index = position;
		
		mHolder.workerName.setText(worker.workerName);
		mHolder.workerImage.setImageDrawable(context.getResources().getDrawable(worker.workerImage));
		
		// Call button click listener
		mHolder.moreOptions.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				showPopUp(worker, v);
				mOptionPosition = index;
			}});
		
		mHolder.workerImage.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
				Fragment prev = context.getSupportFragmentManager().findFragmentByTag(DETAILS_TAG);
				if (prev != null) ft.remove(prev);
				
				Dialog_Worker df = Dialog_Worker.newInstance();
				workerDetails = worker;
				df.show(ft, DETAILS_TAG);
				
			}
			
		});
	}
	
	public Bitmap getRoundedPic(int resid){
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resid);
		Bitmap output = Bitmap.createScaledBitmap(bitmap, 40, 40, true);
        Canvas canvas = new Canvas(output);
        
        BitmapShader shader;
        shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        RectF rect = new RectF(0.0f, 0.0f, bitmap.getWidth(), bitmap.getHeight());

        // rect contains the bounds of the shape
        // radius is the radius in pixels of the rounded corners
        // paint contains the shader that will texture the shape
        canvas.drawRoundRect(rect, 40, 40, paint);
        return output;
	}
		

	@Override
	public WorkerViewHolder onCreateViewHolder(ViewGroup view, int arg1) {
		View item = LayoutInflater.from(view.getContext()).inflate(R.layout.layout_workerlistview, view, false);
		return new WorkerViewHolder(item);
	}
	
	/**
	 * ViewHolder class for each worker in the view
	 * @author Princewill Okorie
	 *
	 */
	public final static class WorkerViewHolder extends RecyclerView.ViewHolder{
		protected ImageView workerImage;
		protected TextView workerName;
		protected ImageButton moreOptions;

		public WorkerViewHolder(View itemView) {
			super(itemView);
			workerImage = (ImageView) itemView.findViewById(R.id.image_worker);
			workerName = (TextView)  itemView.findViewById(R.id.text_worker_name);
			moreOptions = (ImageButton)   itemView.findViewById(R.id.btn_menu_worker);
		}
	}
	
	/**
	 * Dialog class to indicate network error or non-connectivity
	 * @author Prince O.
	 *
	 */
	private static class DialogNoConnection extends DialogFragment{
		public static final String MSG = "message";
		private String msg;
		
		public static final DialogNoConnection newInstance(String msg){
			final DialogNoConnection df = new DialogNoConnection();
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
					
				}
				
			});
			
			Window window = getDialog().getWindow();
			window.setGravity(Gravity.BOTTOM);
			
			return view;
		}
	}
	
	public void showPopUp(WorkerHolder worker, View view){
		mOptions = new PopupMenu(context,view);
		MenuInflater inflater = mOptions.getMenuInflater();
		inflater.inflate(R.menu.menu_pop_worker, mOptions.getMenu());
		mOptions.setOnMenuItemClickListener(this);
		disableMenu(worker, mOptions.getMenu());
		mOptions.show();
	}
	
	public void disableMenu(WorkerHolder worker, Menu menu){
		// Disable call and sms option
		if (worker.workerNumbers.length == 0){
			MenuItem item = menu.findItem(R.id.action_pop_call);
			item.setEnabled(false);
			
			MenuItem item_2 = menu.findItem(R.id.action_pop_sms);
			item_2.setEnabled(false);
		}
		
		// Disable location
		if (worker.workerAddress == null || worker.workerAddress.isEmpty()){
			MenuItem item = menu.findItem(R.id.action_pop_locate);
			item.setEnabled(false);
		}
		
		// Disable email
		if (worker.workerEmails.length == 0){
			MenuItem item = menu.findItem(R.id.action_pop_email);
			item.setEnabled(false);
		}
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		// TODO Auto-generated method stub
		int menuId = item.getItemId();
		
		switch(menuId){
		case R.id.action_pop_call:
			makeCall(mOptionPosition);
			return true;
			
		case R.id.action_pop_sms:
			sendSMS(mOptionPosition);
			return true;
			
		case R.id.action_pop_email:
			sendEmail(mOptionPosition);
			return true;
			
		case R.id.action_pop_locate:
			Toast.makeText(context, "Locate", Toast.LENGTH_SHORT).show();
			return true;
			
		default:
			return false;
		}
	}
	
	private void makeCall(int position){
		WorkerHolder worker = workerList.get(position);
		
		if (!Tool.getInstance().isAirplaneMode()){
			if (worker.workerNumbers.length > 1){
				// Inflate Dialog_Call
				FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
				Fragment prev = context.getSupportFragmentManager().findFragmentByTag(CALL_TAG);
				if (prev != null) ft.remove(prev);
				
				
				DialogFragment dialog = Dialog_Call.newInstance(worker.workerNumbers);
				dialog.show(ft, CALL_TAG);
			}
			
			else if (worker.workerNumbers.length == 1){
				Tool.getInstance().makeCall(worker.workerNumbers[0]);
			}
		}else{
			FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
			Fragment prev = context.getSupportFragmentManager().findFragmentByTag(NETWORK_TAG);
			if (prev != null) ft.remove(prev);
			
		
			DialogFragment df = DialogNoConnection.newInstance(context.getResources().getString(R.string.airplane_mode_on));
			df.show(ft, NETWORK_TAG);
		}
	}
	
	private void sendSMS(int position){
		WorkerHolder worker = workerList.get(position);
		
		if (worker.workerNumbers.length > 1){
			// Inflate Dialog_Call
			FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
			Fragment prev = context.getSupportFragmentManager().findFragmentByTag(SMS_TAG);
			if (prev != null) ft.remove(prev);
			
			
			DialogFragment dialog = Dialog_Message.newInstance(worker.workerNumbers);
			dialog.show(ft, SMS_TAG);
		}
		
		else if (worker.workerNumbers.length == 1){
			String num = worker.workerNumbers[0];
			Tool.getInstance().sendSMS(num);
		}
	}
	
	private void sendEmail(int position){
		WorkerHolder worker = workerList.get(position);
		
		if (worker.workerEmails.length > 1){
			// Inflate Dialog_Email
			FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
			Fragment prev = context.getSupportFragmentManager().findFragmentByTag(EMAIL_TAG);
			if (prev != null) ft.remove(prev);
			
			
			DialogFragment dialog = Dialog_Email.newInstance(worker.workerEmails);
			dialog.show(ft, EMAIL_TAG);
		}
			
		else if (worker.workerEmails.length == 1){
			Tool.getInstance().sendEmail(worker.workerEmails);
		}
	}
	
	public void dismissDialogs(){
		
		// Dismiss any visible popUp
		if (mOptions != null){
			mOptions.dismiss();
		}
		
		// Dismiss any
		FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
		
		
		Fragment call = context.getSupportFragmentManager().findFragmentByTag(CALL_TAG);
		if (call != null) ft.remove(call);
		
		Fragment sms = context.getSupportFragmentManager().findFragmentByTag(SMS_TAG);
		if (sms != null) ft.remove(sms);
		
		Fragment email = context.getSupportFragmentManager().findFragmentByTag(EMAIL_TAG);
		if (email != null) ft.remove(email);
		
		Fragment net = context.getSupportFragmentManager().findFragmentByTag(NETWORK_TAG);
		if (net != null) ft.remove(net);
		
		Fragment details = context.getSupportFragmentManager().findFragmentByTag(DETAILS_TAG);
		if (details != null) ft.remove(details);
		
		ft.commit();
	}
	
}
