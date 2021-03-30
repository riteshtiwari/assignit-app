package com.assignit.android.ui.views.alphabeticalIndex.Listview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ListAdapter;
import android.widget.ListView;
/**
 *Indexable ListView Class
 * 
 * @author Innoppl
 * 
 */
public class IndexableListView extends ListView {

	private boolean mIsFastScrollEnabled = false;
	private IndexScroller mScroller = null;
	private GestureDetector mGestureDetector = null;

	/**Constructor for the class with single parameter
	 * 
	 * @param context
	 */
	public IndexableListView(Context context) {
		super(context);
	}

	/**Constructor for the class with two parameter
	 * 
	 * @param context
	 * @param attrs
	 */
	public IndexableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**Constructor for the class with three parameter
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public IndexableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean isFastScrollEnabled() {
		return mIsFastScrollEnabled;
	}

	
	
	/* 
	 * @see android.widget.AbsListView#setFastScrollEnabled(boolean)
	 */
	@Override
	public void setFastScrollEnabled(boolean enabled) {
		mIsFastScrollEnabled = enabled;
		if (mIsFastScrollEnabled) {
			if (mScroller == null)
				mScroller = new IndexScroller(getContext(), this);
		} else {
			if (mScroller != null) {
				mScroller.hide();
				mScroller = null;
			}
		}
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		if (mScroller != null)
			mScroller.draw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mScroller != null && mScroller.onTouchEvent(ev))
			return true;

		if (mGestureDetector == null && mScroller!=null) {
			mGestureDetector = new GestureDetector(getContext(),
					new GestureDetector.SimpleOnGestureListener() {
						@Override
						public boolean onFling(MotionEvent e1, MotionEvent e2,
								float velocityX, float velocityY) {
							mScroller.show();
							return super.onFling(e1, e2, velocityX, velocityY);
						}
					});
		}
		if(mScroller!=null)
		mGestureDetector.onTouchEvent(ev);
		
		return super.onTouchEvent(ev);
		
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return true;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		if (mScroller != null)
			mScroller.setAdapter(adapter);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (mScroller != null)
			mScroller.onSizeChanged(w, h, oldw, oldh);
	}

}
