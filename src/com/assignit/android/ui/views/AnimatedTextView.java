package com.assignit.android.ui.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * This is custom Textview class that perform animation
 * 
 * @author Innoppl
 * 
 */
public class AnimatedTextView extends TextView {

	Context context;

	private OnHeightChangeListener mHeightChangeListener;

	public interface OnHeightChangeListener {

		public void onChanged(int h, int oldh);

	}

	/**
	 * Constructor for the class
	 * @param context
	 * @param attrs
	 */
	public AnimatedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init(attrs);
	}

	/**
	 * Constructor for the class with different in parameter
	 * @param mContext
	 */
	public AnimatedTextView(Activity mContext) {
		super(mContext);
		this.context = mContext;
	}

	/**
	 * Method to initialise the view 
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
	 * Method to set font type for the view
	 * @param FontType
	 */
	public void setFontType(String FontType) {
		setTypeface(CustomViewConstants.getFontType(context, FontType));
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {

		if (mHeightChangeListener != null) {
			mHeightChangeListener.onChanged(h, oldh);
		}
	}

	/**
	 * Methods calls on height change
	 * @param changeListener
	 */
	public void setOnHeightChangeListener(OnHeightChangeListener changeListener) {
		mHeightChangeListener = changeListener;
	}
}
