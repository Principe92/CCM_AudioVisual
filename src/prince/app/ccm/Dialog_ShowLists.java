package prince.app.ccm;

import java.util.ArrayList;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Fragment class that shows the details of a particular facebook album
 * @author Princewill Okorie
 *
 */
public class Dialog_ShowLists extends DialogFragment{
	private static final String TAG = Dialog_ShowLists.class.getSimpleName();
	private static final String LIST = "position";
	private ArrayList<String> mWorkerList;
	
	private ListView mList;
	
	public static Dialog_ShowLists newInstance(ArrayList<String> list){
		Bundle args = new Bundle ();
		args.putStringArrayList(LIST, list);
		final Dialog_ShowLists mNewDetails = new Dialog_ShowLists();
		mNewDetails.setArguments(args);
		return mNewDetails;
	}
	
	@Override
	public void onCreate(Bundle oldState){
		super.onCreate(oldState);
		
		mWorkerList = getArguments().getStringArrayList(LIST);
		
		setRetainInstance(true);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle oldState){
		super.onCreateView(inflater, parent, oldState);
		View view = inflater.inflate(R.layout.layout_dialog_showlist, parent, false);
		
		// set the album name
		mList = (ListView) view.findViewById(R.id.list_showlist);
		mList.setFastScrollEnabled(true);
		mList.setAdapter(new ListAdapter(getActivity(), mWorkerList));
		
		getDialog().setTitle("Convocados");
		
		return view;
	}
	
	private class ListAdapter extends ArrayAdapter<String> {
		  private final Context context;

		  public ListAdapter(Context context, ArrayList<String> mWorkerList) {
		    super(context, R.layout.layout_listview, mWorkerList);
		    this.context = context;
		  }
		  
		  private class ViewHolder {
		        TextView name;
		        TextView rol;
		    }

		  @Override
		  public View getView(int position, View convertView, ViewGroup parent) {
			  ViewHolder holder = null;
			  String rowItem = getItem(position);
			  String split[] = rowItem.split(Pattern.quote("+"));
		    
			  LayoutInflater inflater = (LayoutInflater) context .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			  if (convertView == null){
				  convertView = inflater.inflate(R.layout.layout_listview, parent, false);
				  holder = new ViewHolder();
				  holder.name  = (TextView) convertView.findViewById(R.id.text_name);
				  holder.rol = (TextView) convertView.findViewById(R.id.text_roles);
				  convertView.setTag(holder);
			  } else {
				  holder = (ViewHolder) convertView.getTag();
			  }
			  
			  holder.name.setText(split[0]);
			  holder.rol.setText(split[1]);

			  return convertView;
		  }
	}
	
}
