package com.assignit.android.ui.views;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.assignit.android.R;

/**
 * Custom Tab View
 * 
 * @author Innoppl
 * 
 */

public class TabView extends LinearLayout implements OnClickListener {
	public final CustomButton mFromMeBtn, mFriendsBtn, mForMeBtn;
	private OnTabChangeListener mOnTabChangeListener;

	public interface OnTabChangeListener {

		public void onTabChanged(int viewId);

	}
	
	/*
	 * @see android.view.View#onConfigurationChanged(android.content.res.Configuration)
	 */
	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * Constructor for the class
	 * 
	 * @param context
	 * @param attrs
	 */
	public TabView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.tab_view, this, true);
		mFromMeBtn = (CustomButton) view.findViewById(R.id.tabBtnFromMe);
		mFriendsBtn = (CustomButton) view.findViewById(R.id.tabBtnFriends);
		mForMeBtn = (CustomButton) view.findViewById(R.id.tabBtnForMe);
		
	 
		mFromMeBtn.setOnClickListener(this);
		mFriendsBtn.setOnClickListener(this);
		mForMeBtn.setOnClickListener(this);
		mFromMeBtn.setSelected(Boolean.FALSE);
		mFriendsBtn.setSelected(Boolean.TRUE);
		mForMeBtn.setSelected(Boolean.FALSE);

	}

	
	@Override
	public void onClick(View view) {

		switch (view.getId()) {
			case R.id.tabBtnFromMe:
				mFromMeBtn.setSelected(Boolean.TRUE);
				mFriendsBtn.setSelected(Boolean.FALSE);
				mForMeBtn.setSelected(Boolean.FALSE);

				break;
			case R.id.tabBtnFriends:
			
				mFromMeBtn.setSelected(Boolean.FALSE);
				mFriendsBtn.setSelected(Boolean.TRUE);
				mForMeBtn.setSelected(Boolean.FALSE);
				break;
			case R.id.tabBtnForMe:

				mFromMeBtn.setSelected(Boolean.FALSE);
				mFriendsBtn.setSelected(Boolean.FALSE);
				mForMeBtn.setSelected(Boolean.TRUE);
				break;
		}

		if (mOnTabChangeListener != null)
			mOnTabChangeListener.onTabChanged(view.getId());

	}

	/**
	 * Method calls to set tab change listener
	 * @param changeListener
	 */
	public void setOnTabChangeListener(OnTabChangeListener changeListener) {
		mOnTabChangeListener = changeListener;
	}

	/**
	 * Callback method to perform fromme click action
	 * 
	 * @return
	 */
	public CustomButton getTabFromMeView() {
		return mFromMeBtn;

	}

	/**
	 * Callback method to perform friend button click action
	 * 
	 * @return
	 */
	public CustomButton getTabFriedsView() {
		return mFriendsBtn;
	}

	/**
	 * Callback method to perform forme click action
	 * 
	 * @return
	 */
	public CustomButton getTabForMeView() {
		return mForMeBtn;

	}
}
