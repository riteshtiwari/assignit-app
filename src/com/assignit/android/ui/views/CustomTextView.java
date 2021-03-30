package com.assignit.android.ui.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Custom Text View
 * 
 * @author Innoppl
 * 
 */
public class CustomTextView extends TextView {

	Context context;

	public CustomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init(attrs);
	}

	public CustomTextView(Activity mContext) {
		super(mContext);
		this.context = mContext;
	}

	/**
	 * Initialise the attrs
	 * @param attrs
	 */
	private void init(AttributeSet attrs) {

		setTypeface(CustomViewConstants.getFontType(context, attrs));
		

	}

	@Override
	public void setTypeface(Typeface tf) {
		super.setTypeface(tf);
	}

	/**
	 * Method to set font by string
	 * 
	 * @param FontType
	 */
	public void setFontType(String FontType) {
		setTypeface(CustomViewConstants.getFontType(context, FontType));
	}

}
