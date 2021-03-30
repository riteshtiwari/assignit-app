package com.assignit.android.ui.fragments;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import com.assignit.android.utils.AppConstant;
import com.assignit.android.utils.CommonUtils;
/**
 * This is supporting to picking date class.
 * 
 * @author Innoppl
 * 
 */
@SuppressLint("SimpleDateFormat")
public class DatePickerFragment extends DialogFragment implements
		DatePickerDialog.OnDateSetListener {

	DatePickerListener dateListener;
	private long mcurrentTime;
	private String MAX_LIMIT_YEAR = "2124715604000" ;
	int viewId;

	/**
	 * callback interface to set date
	 *
	 */
	public interface DatePickerListener {
		public void onDatePicked(String date, int viewId);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		mcurrentTime = new java.util.Date().getTime();
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		viewId = getArguments().getInt(AppConstant.VIEW_ID);
		dateListener = (DatePickerListener) getActivity();
		
		DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day); 
		datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
		// Limit to 2037
		datePickerDialog.getDatePicker().setMaxDate(Long.parseLong(MAX_LIMIT_YEAR));

		// Create a new instance of DatePickerDialog and return it
		return datePickerDialog;
	}
	@Override
	public void onAttach(Activity activity) {
		// when the fragment is initially shown (i.e. attached to the activity),
		// cast the activity to the callback interface type
		super.onAttach(activity);
		try {
			dateListener = (DatePickerListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement " + DatePickerListener.class.getName());
		}
	}
	
	/**
     * this method is set the value to interface for callback function
     */
	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR,year);
		calendar.set(Calendar.MONTH,month);
		calendar.set(Calendar.DAY_OF_MONTH,day);

		String formattedDate = CommonUtils.getFormatedDateMDY(Long.toString(calendar.getTimeInMillis()/1000));
		
		long selectedDate = calendar.getTimeInMillis();
		
		if (selectedDate < mcurrentTime - 1000) {
			if (dateListener != null) {
			dateListener.onDatePicked("error", viewId);
			}
		} else {
			if (dateListener != null) {
				dateListener.onDatePicked(formattedDate, viewId);
			}
		}
	}
}