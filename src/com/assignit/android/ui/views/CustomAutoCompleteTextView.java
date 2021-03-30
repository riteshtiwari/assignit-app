package com.assignit.android.ui.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

/**
 * This is custom CompeleteTextView.It's uses are auto event capture by view when user input.
 * 
 * @author Innoppl
 * 
 */
public class CustomAutoCompleteTextView extends AutoCompleteTextView {

	Context context;

	public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init(attrs);
	}

	/**
	 * Method to initialise the view
	 * @param attrs
	 */
	private void init(AttributeSet attrs) {

	}

	@Override
	public void setTypeface(Typeface tf) {
		super.setTypeface(tf);
	}
}
