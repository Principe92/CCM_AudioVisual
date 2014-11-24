/*
 * Copyright (C) 2014 Princewill Chibututu Okorie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package prince.app.ccm.tools;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class ScheduleSDK {
	private static final String TAG = ScheduleSDK.class.getSimpleName();
	
	public static String LAST_UPDATED = "";
	public static boolean UPDATED = false;
	
	private ScheduleSDK(){}
	
	/**
	 * Method to fetch the data in particular row and column
	 * @param mRow
	 * @param mColumn
	 * @return
	 */
	public static String fetchData(int mRow, int mColumn){
		if (!isTableEmpty()){
			VideoRow row = Tool.mVideoRowArray.get(mRow);
			VideoColumn column = row.mVideoTurn.get(mColumn);
			String hold = column.mData;
			return hold.trim();
		}
		
		return "";
	}
	
	/**
	 * Method to fetch the working shift
	 * @param mData
	 * @return
	 */
	public static String fetchShift(String mData){
		if (mData != null && !mData.isEmpty()){
			
		}
		
		return "";
	}
	
	/**
	 * Method to fetch the role of the worker
	 * @param mData
	 * @return
	 */
	public static String fetchDuty(String mData){
		if (mData != null && !mData.isEmpty()){
			
		}
		
		return "";
	}
	
	/**
	 * Method to fetch additional information on the table
	 * @return - string - The extra information
	 */
	public static String fetchNote(){
		if (!isTableEmpty()){
			String hold = fetchData((getTableSize() - 2), 0);
			return hold.trim().replace("NOTA:", "").trim();
		}
		return "";
	}
	
	/**
	 * Method to return the number of schedules in the table
	 * @return - Schedule Count
	 */
	public static int fetchEditionCount(){
		if (!isTableEmpty()){
			int i = 1;
			int size = 0;
			String hold = "empty";
			while(!hold.isEmpty()){
				hold = fetchData(0, i);
				
				if (!hold.isEmpty()){
					if (hold.contains("0")  || hold.contains("1") 
											|| hold.contains("2") || hold.contains("3") 
											|| hold.contains("4") || hold.contains("5") 
											|| hold.contains("6") || hold.contains("7") 
											|| hold.contains("9") || hold.contains("9")){
						size = size + 1;
					} else hold = "";
				 
				i = i + 1;
				}
			}
			
			return size;
		}
		
		return 0;
	}
	
	/**
	 * Method to fetch the current month from retrieved data
	 * @return
	 */
	public static String fetchMonth(){
		if (!isTableEmpty()){
			return fetchData(0,0);		
		}
		
		return "";
	}
	
	/**
	 * Method to fetch the current day from retrieved data
	 * @return - The day of the week
	 */
	public static String fetchDay(int position){
		if (!isTableEmpty()){
			return fetchData(1, position);
		}
		
		return "";
	}
	
	/**
	 * Method to fetch the edition number of the current service
	 * @return - The edition number
	 */
	public static String fetchEdition(int position){
		if (!isTableEmpty()){
			return fetchData(0, position);
		}
		
		return "";
	}
	
	public static ArrayList<String> fetchWorkers(){
		if (!isTableEmpty()){
			ArrayList<String> names = new ArrayList<String>();
			String hold = "hold";
			int i = 3;
			while(!hold.isEmpty()){
				hold = fetchData(i, 0); 
				if (!hold.isEmpty()) names.add(hold);
				i = i + 1;
			}
			
			return names;
		}
		
		return null;
	}
	
	/**
	 * Method to fetch the date of a given service
	 * @param position - the position in the array
	 * @return - The date in format date/month
	 */
	public static String fetchDate(int position){
		if (!isTableEmpty()){
			String data = fetchData(2, position);
			int space = data.indexOf(" ");
			if (space != -1){
				String [] split = data.split(Pattern.quote("  "));
				return split[1].trim();
			}
			return data;
		}
		return "";
	}
	
	/**
	 * Method to fetch the date of a given service
	 * @param position - the position in the array
	 * @return - The date in format date/month
	 */
	public static String fetchEvent(int position){
		if (!isTableEmpty()){			
			String data = fetchData(2, position);
			int space = data.indexOf(" ");
			if (space != -1){
				String [] split = data.split(Pattern.quote("  "));
				return split[0].trim();
			}
		}
		return "Culto";
	}
	
	/**
	 * Method to return the size of the Table
	 * @return - The size of the table
	 */
	public static int getTableSize(){
		synchronized(Tool.mVideoRowArray){
			return Tool.mVideoRowArray.size();
		}
	}
	
	public static boolean isTableEmpty(){
		synchronized(Tool.mVideoRowArray){
			return getTableSize() == 0;
		}
	}

	public static ArrayList<String> fetchWorkerList(int position) {
		if (!isTableEmpty()){
			ArrayList<String> list = new ArrayList<String>();
			String hold = "hold";
			String data;
			int i = 3;
			while(!hold.isEmpty()){
				hold = fetchData(i, 0);
				data = fetchData(i, position);
				if (!hold.isEmpty() && !data.isEmpty()) list.add(hold + "+" + data);
				i = i + 1;
			}
			return list;
		}
		return null;
	}
}
