package com.assignit.android.ui.fragments;

import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.assignit.android.utils.AppConstant;
/**
 * This is supporting to pick a time from time picker dialog class.
 * 
 * @author Innoppl
 * 
 */
public class TimePickerFragment extends DialogFragment implements
		TimePickerDialog.OnTimeSetListener {
	
	private TimePickedListener mListener;
	int viewId;
	int initialHour;
	int initialMinutes ;
	

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		viewId = getArguments().getInt(AppConstant.VIEW_ID);
		initialHour = getArguments().getInt(AppConstant.CURRENT_HOUR);
		initialMinutes =getArguments().getInt(AppConstant.CURRENT_MINUTES);
				
		
		TimePickerDialog timePickerDialog = null;
		
		if(initialHour != -1 && initialMinutes != -1){
			 timePickerDialog = new TimePickerDialog(getActivity(), this, initialHour, initialMinutes,
					DateFormat.is24HourFormat(getActivity()));}
		
		// create a new instance of TimePickerDialog and return it
		return timePickerDialog;
	}

	@Override
	public void onAttach(Activity activity) {
		// when the fragment is initially shown (i.e. attached to the activity),
		// cast the activity to the callback interface type
		super.onAttach(activity);
		try {
			mListener = (TimePickedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement " + TimePickedListener.class.getName());
		}
	}

	// when the time is selected, send it to the activity via its callback interface method
	@Override
	public void onTimeSet(TimePicker view, int currentHourOfDay, int currentMinute) {
		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, currentHourOfDay);
		c.set(Calendar.MINUTE, currentMinute);
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, currentHourOfDay);
		calendar.set(Calendar.MINUTE, currentMinute);
		calendar.add(Calendar.HOUR_OF_DAY, 1);
			if (mListener != null) {
				mListener.onTimePicked(DateFormat.format("h:mm a", calendar).toString(),DateFormat.format("h:mm a", c).toString(), viewId);
			}
		
	}

	/**
	 * @author android3
	 * callback interface to set time on view
	 */
	public static interface TimePickedListener {
		/**
		 * to set time implement this method 
		 * @param endTime
		 * @param time
		 * @param view
		 */
		public void onTimePicked(String endTime,String time, int view);
	}
}