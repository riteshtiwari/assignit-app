package com.assignit.android.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.assignit.android.ui.views.AnimatedTextView;
import com.assignit.android.ui.views.AnimatedTextView.OnHeightChangeListener;

/**
 * Card expand/collapse animation class.
 * 
 * @author Innoppl
 * 
 */
public class ExpandAnimation extends Animation implements OnHeightChangeListener {
	private LayoutParams mViewLayoutParams = null;
	private int mMarginStart = 0;
	private int mMarginEnd = 0;
	private boolean mWasEndedAlready = false;
	private LinearLayout mPhotoView = null;
	private String imageURI = null;
	private Context mContext = null;
	private AnimatedTextView mCloneView = null;

	/**
	 * Initialize the animation
	 * 
	 * @param view
	 *            The layout we want to animate
	 * @param duration
	 *            The duration of the animation, in ms
	 */
	public ExpandAnimation(Context context, View photoView, View descriptionView, int duration, String imageUrl) {

		setDuration(duration);
		mContext = context;
		imageURI = imageUrl;

		mPhotoView = (LinearLayout) photoView;
		mCloneView = (AnimatedTextView) descriptionView;

		if (StringUtils.isNotBlank(imageUrl)) {
			mPhotoView.setVisibility(View.VISIBLE);
			mCloneView.setSingleLine(Boolean.FALSE);

			mViewLayoutParams = (LayoutParams) mPhotoView.getLayoutParams();
			mMarginStart = mViewLayoutParams.bottomMargin;
			mMarginEnd = (mMarginStart == 0 ? (0 - mPhotoView.getHeight()) : 0);

		} else if (CommonUtils.convertToDp(mContext, 18) < mCloneView.getHeight()) {
			mCloneView.setSingleLine(Boolean.TRUE);

		} else {
			mCloneView.setSingleLine(Boolean.FALSE);
			mCloneView.setHeight(CommonUtils.convertToDp(mContext, 78));

		}

		mViewLayoutParams = (LayoutParams) mPhotoView.getLayoutParams();
		mMarginStart = mViewLayoutParams.bottomMargin;
		mMarginEnd = (mMarginStart == 0 ? (0 - mPhotoView.getHeight()) : 0);

		if (StringUtils.isNotBlank(imageUrl) && mMarginStart != 0 && !(mMarginStart == -mPhotoView.getHeight())) {
			mMarginStart = CommonUtils.convertToDp(mContext, 2);
		}

	}
	
	
	/* 
	 * @see android.view.animation.Animation#applyTransformation(float, android.view.animation.Transformation)
	 */
	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		super.applyTransformation(interpolatedTime, t);

		if (interpolatedTime < 1.0f) {

			// Calculating the new bottom margin, and setting it
			mViewLayoutParams.bottomMargin = mMarginStart + (int) ((mMarginEnd - mMarginStart) * interpolatedTime);

			mPhotoView.requestLayout();
			mCloneView.requestLayout();

			// Making sure we didn't run the ending before (it happens!)
		} else if (!mWasEndedAlready) {

			mViewLayoutParams.bottomMargin = mMarginEnd;
			mPhotoView.requestLayout();

			if (StringUtils.isNotBlank(imageURI) && mMarginEnd != 0) {
				mPhotoView.setVisibility(View.GONE);
				mCloneView.setSingleLine(Boolean.TRUE);

			}

			mWasEndedAlready = true;
		}
	}

	@Override
	public void onChanged(int h, int oldh) {
	}
	
	public void resetAnimation() {

	}
	
	
}