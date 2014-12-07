package prince.app.ccm;

import java.io.File;
import java.util.ArrayList;

import prince.app.ccm.tools.ManualAdapter;
import prince.app.ccm.tools.ManualHolder;
import prince.app.ccm.tools.Tool;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class Fragment_Manuals extends Fragment {
//	private static final String TAG = Fragment_Manuals.class.getSimpleName();
	private static final String ACTIVE = "active tasks";
	
	private ManualAdapter manualAdapter;
	private ArrayList<String> mActiveTasks;
	
	
	public static Fragment_Manuals newInstance(){
		final Fragment_Manuals mFragment = new Fragment_Manuals();
		return mFragment;
	}
	
	@Override
	public void onPause(){
		super.onPause();
		
		if (manualAdapter != null){
			manualAdapter.dismissDialogs();
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle oldState){
		super.onSaveInstanceState(oldState);
		oldState.putStringArrayList(ACTIVE, manualAdapter.getActiveTasks());
	}

	@Override
	public void onCreate(Bundle oldState) {
		super.onCreate(oldState);
		
		if (oldState != null) mActiveTasks = oldState.getStringArrayList(ACTIVE);
		else mActiveTasks = new ArrayList<String>();
		
		// Set up adapter
		manualAdapter = new ManualAdapter(createList(), mActiveTasks, (ActionBarActivity) getActivity());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle oldState){
		super.onCreateView(inflater, parent, oldState);
		
		View view = inflater.inflate(R.layout.layout_manuals, parent, false);
		
		setUp(view);
		
		return view;
	}
	
	private void setUp(View view){
		RecyclerView recList = (RecyclerView) view.findViewById(R.id.cardList);
		recList.setHasFixedSize(true);
		LinearLayoutManager llm = new LinearLayoutManager(getActivity());
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recList.setLayoutManager(llm);
		recList.setAdapter(manualAdapter);
	}
	
	private ArrayList<ManualHolder> createList() {
		ArrayList<ManualHolder> result = new ArrayList<ManualHolder>();
		String[] titles = getResources().getStringArray(R.array.array_manuals);
		
		for (int i=0; i < titles.length; i++) {
			ManualHolder ci = new ManualHolder();
			ci.mManualImage = getResources().getDrawable(R.drawable.manual2); // fetchImage(i);
			ci.mManualTitle = titles[i];
			ci.mURL = fetchURL(i);
			ci.mFileName = fetchFileName(ci.mURL);
			result.add(ci);
		}
		
		return result;
	}
	
	public static boolean fileExist(String name){
		// create new folder or reference existing one
		File file = new File(Tool.APP_DIR, name);
		return (file.exists() && file.length() > 0);
	}
	
	private Drawable fetchImage(int position){
		switch(position){
		case 2:
			return getResources().getDrawable(R.drawable.manual_2);
			
		default:
			return getResources().getDrawable(R.drawable.manual2);
		}
	}
	
	private String fetchURL(int position){
		Resources rs = getResources();
		
		switch(position){
		case 0:
			return rs.getString(R.string.escaladeplanos_av); 				// escaladeplanos_av.pdf
		case 1:
			return rs.getString(R.string.zonas_filmacion_pm);				// zonas_filmacion_pm.pdf
		case 2:
			return rs.getString(R.string.tomasutiles_110424);				// tomasutiles_110424.pdf
		case 3:
			return rs.getString(R.string.salle_montaje_110213);				// salle_montaje_110213.pdf
		case 4:
			return rs.getString(R.string.salle_montaje_110814);				// salle_montaje_110814.pdf
		case 5:
			return rs.getString(R.string.salle_conexionado_110814);			// salle_conexionado_110814.pdf
		case 6:
			return rs.getString(R.string.publicacion_predicaciones_web);	// publicacion_predicaciones_web.pdf
		case 7:
			return rs.getString(R.string.flash);							// index.htm
		case 8:
			return rs.getString(R.string.tomas_frecuentes_pm);				// tomas_frecuentes_pm.pdf
		case 9:
			return rs.getString(R.string.glosariodecine_av);				// glosariodecine_av.pdf
		case 10:
			return rs.getString(R.string.camara_sony);						// camara_sony.pdf
		case 11:
			return rs.getString(R.string.introduccion_xhtml);				// introduccion_xhtml.pdf
		case 12:
			return rs.getString(R.string.editor_casablanca); 				// editor_casablanca.pdf
		case 13:
			return rs.getString(R.string.proyector_lcd_xl5980u);			// XL5980U_5900U_Esp.pdf
		default:
			return "";
		}
	}
	
	private String fetchFileName(String url){
		int position = url.lastIndexOf("/");
		if (position != -1){
			String web = url.substring((position + 1), url.length());
			return web;
		}
		
		return "";
	}
	
	 @Override
	 public void onActivityCreated (Bundle savedInstanceState) {
		 super.onActivityCreated(savedInstanceState);
		 // Indicate that this fragment would like to influence the set of actions in the action bar.
		 setHasOptionsMenu(true);
	 }
	
	@Override
	public void onPrepareOptionsMenu(Menu menu){
		MenuItem refresh = menu.findItem(R.id.action_main_refresh);
		refresh.setEnabled(false)
				.setVisible(false);
	}
}
