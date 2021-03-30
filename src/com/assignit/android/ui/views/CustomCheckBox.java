package com.assignit.android.ui.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.CheckBox;

/**
 * Custom Checkbox view
 * 
 * @author Innoppl
 * 
 */
public class CustomCheckBox extends CheckBox {

	Context context;

	public CustomCheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init(attrs);
	}

	public CustomCheckBox(Context context) {
		super(context);
		this.context = context;
	}

	/**
	 * Method to set Attrbs
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
