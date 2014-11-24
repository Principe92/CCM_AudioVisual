package prince.app.ccm;

import java.util.Calendar;

import prince.app.ccm.tools.ScheduleSDK;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Fragment_Schedule extends Fragment{
	private static final String POSITION = "position";
	private static final String WORKER = "who";
	private static final int EDITION_OFFSET = 1;
	private static final int WORKER_OFFSET = 3;
	private static final String TAG = Fragment_Schedule.class.getSimpleName();
	
	private static final String FREE = "Libre";
	private static final String SPECIAL_EVENT = "Culto Especial";
	private static final String NOT_LISTED = "Sin turno";
	
	private int mWorker;
	private int position;
	
	private TextView mRol;
	private TextView mShift;
	
	private String mWorkerData;
	private String mEvent;
	
	public static Fragment_Schedule newInstance(int pos, int who){
		final Fragment_Schedule aX = new Fragment_Schedule();
		Bundle bond = new Bundle();
		bond.putInt(POSITION, pos);
		bond.putInt(WORKER, who);
		aX.setArguments(bond);
		return aX;
	}
	
	@Override
	public void onCreate(Bundle oldState){
		super.onCreate(oldState);
		
		setRetainInstance(true);
		
		position = getArguments().getInt(POSITION);
		mWorker = getArguments().getInt(WORKER);
		
		// Fetch the data for a given row/column of a worker
		mWorkerData = ScheduleSDK.fetchData(WORKER_OFFSET + mWorker, position + EDITION_OFFSET);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle oldState){
		super.onCreateView(inflater, parent, oldState);
		View mView = inflater.inflate(R.layout.layout_schedule, parent, false);
		
		initView(mView);
		
		return mView;
	}
	
	public void initView(View mView){
		
		// Set the edition
		TextView edition = (TextView) mView.findViewById(R.id.text_date_entry);
		edition.setText(ScheduleSDK.fetchEdition(position + EDITION_OFFSET));
		
		// Set Last actualization
		TextView last_up = (TextView) mView.findViewById(R.id.text_last_entry);
		last_up.setText(ScheduleSDK.LAST_UPDATED.replace(" ", ", "));
		
		// Set the Date
		TextView dateText = (TextView) mView.findViewById(R.id.text_edition_number);
	/*	String data = ScheduleSDK.fetchDate(position);
		Log.e(TAG, "date: " + data);
		String [] split = data.split(Pattern.quote("/"));
		date.setText(getMonth(split[1].trim()) + " " + split[0].trim() + ", " + Calendar.getInstance().get(Calendar.YEAR)); */
		
		String day = ScheduleSDK.fetchDay(position + EDITION_OFFSET).trim();
		String date = ScheduleSDK.fetchDate(position);
		dateText.setText(day + ",  " + date + "/" + Calendar.getInstance().get(Calendar.YEAR));
		
		// Set Special Event
		TextView special = (TextView) mView.findViewById(R.id.text_note_entry);
		mEvent = ScheduleSDK.fetchEvent(position).trim();
		special.setText(mEvent);
		
		// Set the Role
		mRol = (TextView) mView.findViewById(R.id.text_role_entry);
		mRol.setText(mWorkerData);
		if (mWorkerData.isEmpty()) mRol.setText(FREE);
		
		// Set the shift
		mShift = (TextView) mView.findViewById(R.id.text_turn);
		setShift(findShift(mWorkerData));
		
	}
	
	public String getMonth(String month){
		
		if (month.equalsIgnoreCase("01")) return "Enero";
		else if (month.equalsIgnoreCase("02")) return "Febrero";
		else if (month.equalsIgnoreCase("03")) return "Marzo";
		else if (month.equalsIgnoreCase("04")) return "Abril";
		else if (month.equalsIgnoreCase("05")) return "Mayo";
		else if (month.equalsIgnoreCase("06")) return "Junio";
		else if (month.equalsIgnoreCase("07")) return "Julio";
		else if (month.equalsIgnoreCase("08")) return "Agosto";
		else if (month.equalsIgnoreCase("09")) return "Septiembre";
		else if (month.equalsIgnoreCase("10")) return "Octubre";
		else if (month.equalsIgnoreCase("11")) return "Noviembre";
		else if (month.equalsIgnoreCase("12")) return "Diciembre";
		else return "";
	}
	
	public void refresh(int newWorker){
		mWorker = newWorker;
		mWorkerData = ScheduleSDK.fetchData(WORKER_OFFSET + mWorker, position + EDITION_OFFSET);
		
		// Update the Role
		mRol.setText(mWorkerData);
		
		// Update the Shift
		setShift(findShift(mWorkerData));
		
		if (mWorkerData.isEmpty()) mRol.setText(FREE);
	}
	
	private String findShift(String data){
		Log.e(TAG, "data: " + data);
		
		if (data.contains("1º")) return "1";
		else if (data.contains("2º")) return "2";
		else if (data.contains("3º")) return "3";
		else if (data.equalsIgnoreCase(FREE)) return FREE;
		else if (data.equalsIgnoreCase("Vídeo")) return "Vídeo";
		else if (data.isEmpty()) return FREE;
		else return SPECIAL_EVENT;
	}
	
	private void setShift(String data){
		// Modify the size of text
		if (data.equals(FREE) || data.equals("Vídeo") || data.equals(NOT_LISTED) || data.equals(SPECIAL_EVENT)){
			mShift.setTextSize(22);
		} else{
			mShift.setTextSize(100);
		}
		
		if (mEvent.equalsIgnoreCase("Culto") && data.equalsIgnoreCase("Vídeo")){
			data = "1";
			mShift.setTextSize(100);
		}
		
		else if (!mEvent.equalsIgnoreCase("Culto") && data.equalsIgnoreCase("Vídeo")) data = SPECIAL_EVENT;
		
		mShift.setText(data);
	}
	
	public int fetchPosition(){
		return position;
	}

}
