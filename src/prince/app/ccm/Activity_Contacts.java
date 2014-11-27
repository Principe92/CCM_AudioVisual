package prince.app.ccm;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

import prince.app.ccm.tools.ActivityBase;
import prince.app.ccm.tools.ContactAdapter;
import prince.app.ccm.tools.ContactHolder;
import prince.app.ccm.tools.Tool;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class Activity_Contacts extends ActivityBase {
	private static final String TAG = Activity_Contacts.class.getSimpleName();
	private static final String ACTIVE = "active tasks";
	private static String TITLE;
	private ContactAdapter ca;
	private Toolbar mToolBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_manuals);
		TITLE = getResources().getStringArray(R.array.array_navigation)[2];
		
		// Set up the tool bar
		mToolBar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(mToolBar);
		
		RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
		recList.setHasFixedSize(true);
		LinearLayoutManager llm = new LinearLayoutManager(this);
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recList.setLayoutManager(llm);
		
		ca = new ContactAdapter(createList(),this);
		recList.setAdapter(ca);
		
		initNavigationDrawer();
	}
	
	private ArrayList<ContactHolder> createList() {
		ArrayList<ContactHolder> result = new ArrayList<ContactHolder>();
		String[] names = getResources().getStringArray(R.array.array_contact);
		String[] numbers = getResources().getStringArray(R.array.array_numbers);
		String[] emails = getResources().getStringArray(R.array.array_emails);
		String[] roles = getResources().getStringArray(R.array.array_role);
		
		for (int i=0; i < names.length; i++) {
			ContactHolder contact = new ContactHolder();
			contact.image = getResources().getDrawable(R.drawable.ic_person_black_48dp);
			contact.name = names[i];
			
			String num[] = numbers[i].split(Pattern.quote("+"));
			contact.numbers = num;
			
			String emaf[] = emails[i].split(Pattern.quote("+"));
			contact.emails = emaf;
			
			contact.role = roles[i];
			result.add(contact);
		}
		
		return result;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		MenuItem refresh = menu.findItem(R.id.action_main_refresh);
		refresh.setEnabled(false)
				.setVisible(false);
		
		return true;
	}
	
	@Override
	public void onSaveInstanceState(Bundle oldState){
		super.onSaveInstanceState(oldState);
	}
	
	@Override
	protected void onStop(){
		super.onStop();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
	}
	
	public static boolean fileExist(String name){
		// create new folder or reference existing one
		File file = new File(Tool.APP_DIR, name);
		return (file.exists() && file.length() > 0);
	}
	
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
