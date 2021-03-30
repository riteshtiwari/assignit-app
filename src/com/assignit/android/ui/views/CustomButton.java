package com.assignit.android.ui.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Custom Buttom view
 * 
 * @author Innoppl
 * 
 */
public class CustomButton extends Button {

	Context context;

	public CustomButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init(attrs);
	}

	public CustomButton(Context context) {
		super(context);
		this.context = context;
	}

	/**
	 * This method to set attrs
	 * 
	 * @param attrs
	 */
	private void init(AttributeSet attrs) {

		setTypeface(CustomViewConstants.getFontType(context, attrs));

		String textColor = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "textColor");

		if (textColor == null) {
			setTextColor(context.getResources().getColor(android.R.color.black));
		}

	}

	@Override
	public void setTypeface(Typeface tf) {
		super.setTypeface(tf);
	}
}
