package prince.app.ccm;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

import prince.app.ccm.tools.ContactAdapter;
import prince.app.ccm.tools.ContactHolder;
import prince.app.ccm.tools.Tool;
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

public class Fragment_Contacts extends Fragment {
	private static final String TAG = Fragment_Contacts.class.getSimpleName();
	private ContactAdapter contactAdapter;
	
	public static final Fragment_Contacts newInstance(){
		final Fragment_Contacts fc = new Fragment_Contacts();
		return fc;
	}

	@Override
	public void onCreate(Bundle oldState) {
		super.onCreate(oldState);
		
		setRetainInstance(true);
		
		contactAdapter = new ContactAdapter(createList(),(ActionBarActivity) getActivity());
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
		recList.setAdapter(contactAdapter);
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
	
	public static boolean fileExist(String name){
		// create new folder or reference existing one
		File file = new File(Tool.APP_DIR, name);
		return (file.exists() && file.length() > 0);
	}
}
