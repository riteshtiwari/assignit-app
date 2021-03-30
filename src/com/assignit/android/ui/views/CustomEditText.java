package com.assignit.android.ui.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Custom Edit Text
 * 
 * @author Innoppl
 * 
 */
public class CustomEditText extends EditText {

	

	Context context;

	public CustomEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init(attrs);
	}

	/**
	 * Method to set Attrs
	 * 
	 * @param attrs
	 */
	private void init(AttributeSet attrs) {

		setTypeface(CustomViewConstants.getFontType(context, attrs));

	}

	@Override
	public void setTypeface(Typeface tf) {
		super.setTypeface(tf);
	}
}
