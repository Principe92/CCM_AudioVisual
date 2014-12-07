package prince.app.ccm;

import prince.app.ccm.tools.Tool;
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

/**
 * Fragment class that shows the details of a particular facebook album
 * @author Princewill Okorie
 *
 */
public class Dialog_Call extends DialogFragment{
//	private static final String TAG = Dialog_Call.class.getSimpleName();
	private static final String LIST = "numbers";
	private String[] numberList;
	private int selection = -1;
	
	private ListView mList;
	
	public static Dialog_Call newInstance(String[] list){
		Bundle args = new Bundle ();
		args.putStringArray(LIST, list);
		final Dialog_Call mNewDetails = new Dialog_Call();
		mNewDetails.setArguments(args);
		return mNewDetails;
	}
	
	@Override
	public void onCreate(Bundle oldState){
		super.onCreate(oldState);
		
		numberList = getArguments().getStringArray(LIST);
		
		setRetainInstance(true);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle oldState){
		super.onCreateView(inflater, parent, oldState);
		View view = inflater.inflate(R.layout.layout_dialog_call, parent, false);
		
		getDialog().setTitle(getResources().getString(R.string.number_choose));
		
		// set the album name
		mList = (ListView) view.findViewById(R.id.list_calldialog);
		mList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mList.setFastScrollEnabled(true);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_single_choice, numberList);
		mList.setAdapter(adapter);
		mList.setSelection(0);
		
		mList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
            	selection = position;
            }});
		
		Button cancel = (Button) view.findViewById(R.id.btn_cancel);
		cancel.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				getDialog().dismiss();
			}
		});
		
		Button call = (Button) view.findViewById(R.id.btn_call);
		call.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				if (selection != -1){
					Tool.getInstance().makeCall(numberList[selection]);
					getDialog().dismiss();
				}			}
		});
		
		
		return view;
	}
	
}
