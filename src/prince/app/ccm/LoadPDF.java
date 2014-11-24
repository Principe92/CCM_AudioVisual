package prince.app.ccm;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A Fragment that shows a button to load a specific pdf file
 * @author Princewill Okorie
 *
 */
public class LoadPDF extends Fragment{
	private Button mButton;
	private LoadListener  mListener;
	
	
	public static LoadPDF newInstance(){
		return new LoadPDF();
	}
	
	/**
	 * A Listener that notifies the attached activity to load a pdf
	 * @author Princewill Okorie
	 *
	 */
    public interface LoadListener {
    	/**
    	 * A method that notifies the activity to load a pdf
    	 */
        public void onOpenPDF();
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mListener = (LoadListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement LoginListener");
        }
    }
    
    @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    // Retain this fragment across configuration changes.
	 //   setRetainInstance(true);
	  }


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.layout_pdf, container, false); 
		
		mButton = (Button) view.findViewById(R.id.load_pdf);
		
		mButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
		          mListener.onOpenPDF();
		        }
		});
		
		return view;
	}
	

}