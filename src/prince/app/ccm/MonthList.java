package prince.app.ccm;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/** A List Adapter */
public class MonthList extends ArrayAdapter<String> {
	Context context;
	int layoutId;
	private List<String> albumList;
	
	public MonthList(Context context, int resourceID, List<String> albumNames) {
		super(context, resourceID, albumNames);
		this.context = context;
		this.layoutId = resourceID;
		this.albumList = albumNames;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        String rowItem = albumList.get(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(layoutId, parent, false);
            
        }        

        TextView textViewItem = (TextView) convertView.findViewById(R.id.month_name);
        textViewItem.setText(rowItem);
        
        return convertView;
    }

}

