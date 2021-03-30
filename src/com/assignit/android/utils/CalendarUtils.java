package com.assignit.android.utils;

import java.util.Calendar;

import android.content.Context;
/**
 * This class is to get calender detials
 * 
 * @author Innoppl
 * 
 */
public class CalendarUtils {
	Context context;

	// Session session;

	public CalendarUtils(Context context) {
		this.context = context;
		// this.session = new Session(context);
	}

	/**
	 * Getting month name from numeric value
	 * 
	 * @param month
	 * @return
	 */
	public static String getMonthName(int month) {
		switch (month) {
		case 0:
			return "Jan";
		case 1:
			return "Feb";
		case 2:
			return "Mar";
		case 3:
			return "Apr";
		case 4:
			return "May";
		case 5:
			return "Jun";
		case 6:
			return "Jul";
		case 7:
			return "Aug";
		case 8:
			return "Sep";
		case 9:
			return "Oct";
		case 10:
			return "Nov";
		case 11:
			return "Dec";
		}
		return "";
	}
	
	/**
	 * Getting month integer value from name
	 * 
	 * @param month
	 * @return
	 */
	public static int getMonthName(String month) {
		if(month.equals("Jan")){
		return 1;
		}else if(month.equals("Feb")){
			return 2;
		}else if(month.equals("Mar")){
			return 3;
		}else if(month.equals("Apr")){
			return 4;
		}else if(month.equals("May")){
			return 5;
		}else if(month.equals("Jun")){
			return 6;
		}else if(month.equals("Jul")){
			return 7;
		}else if(month.equals("Aug")){
			return 8;
		}else if(month.equals("Sep")){
			return 9;
		}else if(month.equals("Oct")){
			return 10;
		}else if(month.equals("Nov")){
			return 11;
		}else if(month.equals("Dec")){
			return 12;
		}
		
		
		return 1;
	}

	/**
	 * Method calls to get current time
	 * 
	 * @return
	 */
	public static String getCurrentTime() {
		String time = "";
		int Hour = Calendar.getInstance().get(Calendar.HOUR);
		int Minute = Calendar.getInstance().get(Calendar.MINUTE);
		int Day = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		if (Hour < 10)
			time = "0";
		time += String.valueOf(Hour) + ":";
		if (Minute < 10)
			time += "0";
		time += String.valueOf(Minute);
		if (Day < 12) {
			time += " " + "am";
		} else if (Day == 12) {
			time = "12:";
			if (Minute < 10)
				time += "0";
			time += String.valueOf(Minute);
			time += " " + "pm";
		} else {
			time += " " + "pm";
		}
		return time;
	}
}
