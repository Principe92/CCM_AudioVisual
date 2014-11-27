package prince.app.ccm;

import java.util.ArrayList;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Fragment class that shows the details of a particular facebook album
 * @author Princewill Okorie
 *
 */
public class Dialog_Email extends DialogFragment{
	private static final String TAG = Dialog_Email.class.getSimpleName();
	private static final String LIST = "numbers";
	private String[] emailList;
	private ArrayList<String> selected;
	
	private ListView mList;
	
	public static Dialog_Email newInstance(String[] list){
		Bundle args = new Bundle ();
		args.putStringArray(LIST, list);
		final Dialog_Email mNewDetails = new Dialog_Email();
		mNewDetails.setArguments(args);
		return mNewDetails;
	}
	
	@Override
	public void onCreate(Bundle oldState){
		super.onCreate(oldState);
		
		emailList = getArguments().getStringArray(LIST);
		selected = new ArrayList<String>();
		
		setRetainInstance(true);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle oldState){
		super.onCreateView(inflater, parent, oldState);
		View view = inflater.inflate(R.layout.layout_dialog_call, parent, false);
		
		getDialog().setTitle(getResources().getString(R.string.number_choose));
		
		// set the album name
		mList = (ListView) view.findViewById(R.id.list_calldialog);
		mList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		mList.setFastScrollEnabled(true);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_multiple_choice, emailList);
		mList.setAdapter(adapter);
		mList.setSelection(0);
		
		 mList.setOnItemClickListener(new OnItemClickListener() {
	            @Override
	            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
	            	if(selected.contains(emailList[position])) selected.remove(emailList[position]);
	            	else selected.add(emailList[position]);
	            }});
		 
		
		Button cancel = (Button) view.findViewById(R.id.btn_cancel);
		cancel.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				getDialog().dismiss();
			}
		});
		
		Button email = (Button) view.findViewById(R.id.btn_call);
		email.setText(getResources().getString(R.string.btn_email)); 
		email.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				if (!selected.isEmpty()){
					Intent emailIntent = new Intent(Intent.ACTION_SEND);
					emailIntent.setData(Uri.parse("mailto:"));
					emailIntent.setType("text/plain");
					
					String emails[] = new String[selected.size()];
					int index = 0;
					for (String arr:selected){
						emails[index] = arr;
						index++;
					}
					emailIntent.putExtra(Intent.EXTRA_EMAIL, emails);
					
					try {
				         startActivity(Intent.createChooser(emailIntent, "Send mail..."));
				         getDialog().dismiss();
				      } catch (android.content.ActivityNotFoundException ex) {
				         Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.email_failed), Toast.LENGTH_SHORT).show();
				      }
				}
			}
		});
		
		
		return view;
	}
	
}
