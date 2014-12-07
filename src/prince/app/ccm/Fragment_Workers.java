package prince.app.ccm;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

import prince.app.ccm.tools.Tool;
import prince.app.ccm.tools.WorkerAdapter;
import prince.app.ccm.tools.WorkerHolder;
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

public class Fragment_Workers extends Fragment {
//	private static final String TAG = Fragment_Workers.class.getSimpleName();
	private WorkerAdapter workerAdapter;
	
	public static final Fragment_Workers newInstance(){
		final Fragment_Workers fc = new Fragment_Workers();
		return fc;
	}
	
	@Override
	public void onPause(){
		super.onPause();
		
		if (workerAdapter != null){
			workerAdapter.dismissDialogs();
		}
	}

	@Override
	public void onCreate(Bundle oldState) {
		super.onCreate(oldState);
		
		workerAdapter = new WorkerAdapter(createList(),(ActionBarActivity) getActivity());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle oldState){
		super.onCreateView(inflater, parent, oldState);
		View view = inflater.inflate(R.layout.layout_workers, parent, false);
		
		setUp(view);
		
		return view;
	}
	
	private void setUp(View view){
		RecyclerView recList = (RecyclerView) view.findViewById(R.id.list_workers);
		recList.setHasFixedSize(true);
		LinearLayoutManager llm = new LinearLayoutManager(getActivity());
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recList.setLayoutManager(llm);
		recList.setAdapter(workerAdapter);
	}
	
	private ArrayList<WorkerHolder> createList() {
		ArrayList<WorkerHolder> result = new ArrayList<WorkerHolder>();
		String[] names = getResources().getStringArray(R.array.array_contact);
		String[] numbers = getResources().getStringArray(R.array.array_numbers);
		String[] emails = getResources().getStringArray(R.array.array_emails);
		String[] roles = getResources().getStringArray(R.array.array_role);
		
		for (int i=0; i < names.length; i++) {
			WorkerHolder contact = new WorkerHolder();
			contact.workerImage = R.drawable.manuals_1;
			contact.workerName = names[i];
			
			String num[] = numbers[i].split(Pattern.quote("+"));
			contact.workerNumbers = num;
			
			String emaf[] = emails[i].split(Pattern.quote("+"));
			contact.workerEmails = emaf;
			
			contact.workerRole = roles[i];
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
