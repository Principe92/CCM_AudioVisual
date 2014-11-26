package prince.app.ccm;

import java.io.File;
import java.util.ArrayList;

import prince.app.ccm.tools.ActivityBase;
import prince.app.ccm.tools.ManualAdapter;
import prince.app.ccm.tools.ManualCards;
import prince.app.ccm.tools.Tool;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

public class Activity_Manuals extends ActivityBase {
	private static final String TAG = Activity_Manuals.class.getSimpleName();
	private static final String ACTIVE = "active tasks";
	private static String TITLE;
	private ManualAdapter ca;
	private ArrayList<String> mActiveTasks;
	private Toolbar mToolBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_manuals);
		TITLE = getResources().getStringArray(R.array.array_navigation)[1];
		
		// Set up the tool bar
		mToolBar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(mToolBar);
		
		RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
		recList.setHasFixedSize(true);
		LinearLayoutManager llm = new LinearLayoutManager(this);
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recList.setLayoutManager(llm);
		
		mActiveTasks = new ArrayList<String>();
		if (savedInstanceState != null) mActiveTasks = savedInstanceState.getStringArrayList(ACTIVE);
		ca = new ManualAdapter(createList(), mActiveTasks, this);
		recList.setAdapter(ca);
		
		initNavigationDrawer();
	}
	
	private ArrayList<ManualCards> createList() {
		ArrayList<ManualCards> result = new ArrayList<ManualCards>();
		String[] titles = getResources().getStringArray(R.array.array_manuals);
		
		for (int i=0; i < titles.length; i++) {
			ManualCards ci = new ManualCards();
			ci.mManualImage = getResources().getDrawable(R.drawable.manual2); // fetchImage(i);
			ci.mManualTitle = titles[i];
			ci.mURL = fetchURL(i);
			ci.mFileName = fetchFileName(ci.mURL);
			result.add(ci);
		}
		
		return result;
	}
	
	@Override
	public void onSaveInstanceState(Bundle oldState){
		super.onSaveInstanceState(oldState);
		oldState.putStringArrayList(ACTIVE, ca.getActiveTasks());
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		
		// Don't refresh the UI with new data from Async Task
		Tool.EXIT_TASK = true;
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		
		// Refresh the UI with new data from Async Task
		Tool.EXIT_TASK = false;
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

/*	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_help, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	} */

	@Override
	public Toolbar getToolBar() {
		// TODO Auto-generated method stub
		return mToolBar;
	}

	@Override
	public String getActionBarTitle() {
		// TODO Auto-generated method stub
		return TITLE;
	}

	@Override
	public void onDrawerOpen() {}

	@Override
	public void onDrawerClose() {}

	@Override
	public void actionBarRefresh() {
		// TODO Auto-generated method stub
		
	}
}
