package prince.app.ccm;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragment_Help extends Fragment {
	private static final String TAG = Fragment_Help.class.getSimpleName();
	
	public static Fragment_Help newInstance(){
		final Fragment_Help fragment = new Fragment_Help();
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.frag_layout_help,container, false);
		return rootView;
	}
}
