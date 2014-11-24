package prince.app.ccm;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;


public class NoInternet_Fragment extends Fragment{
	private Button rButton;
	private Button nButton;
	RetryListener  retry;
	NetworkListener network;
	
	// Container Activity must implement this interface
    public interface RetryListener {
        public void onRetryClick();
    }
    
 // Container Activity must implement this interface
    public interface NetworkListener {
        public void onNetworkClick();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            retry = (RetryListener) activity;
            network = (NetworkListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement LoginListener");
        }
    }
    
    @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    // Retain this fragment across configuration changes.
	    setRetainInstance(true);
	  }
    

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.test, container, false);
		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
	        }
	    }); 
		
		rButton = (Button) view.findViewById(R.id.retry);
		nButton = (Button) view.findViewById(R.id.check_network);
		rButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
		          retry.onRetryClick();
		        }
		});
		nButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
		          network.onNetworkClick();
		        }
		});
		
		return view;
	}
	

}