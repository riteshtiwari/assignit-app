package com.assignit.android.ui.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.assignit.android.R;
import com.assignit.android.pojo.MenuItems;
import com.assignit.android.ui.adapter.MenuAdapter;
import com.assignit.android.utils.CommonUtils;

/**
 * Custom Menu View
 * 
 * @author Innoppl
 * 
 */
public class CustomMenuView implements OnItemClickListener {

	private final Context mContext;

	private PopupWindow mMenuItemView = null;

	private MenuAdapter mMenuAdapter = null;

	private ListView mListView = null;

	int width, tabwidth;
	
	private OnMenuItemClickListener mMenuItemClickListener;

	public interface OnMenuItemClickListener {

		public void onItemClick(MenuItems menuItem);

	}

	public CustomMenuView(Context context, MenuAdapter adapter) {
		mMenuAdapter = adapter;
		mContext = context;
	}

	public void setMenuItemClickListener(
			OnMenuItemClickListener menuItemClickListener) {
		mMenuItemClickListener = menuItemClickListener;
	}
	
	/**
	 * Method to show menuview options
	 * 
	 * @param view
	 */
	public void showMenu(View view) {

		// initialise a pop up window type
		mMenuItemView = new PopupWindow(mContext);

		LayoutInflater layoutInflater = (LayoutInflater) mContext
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View popupView = layoutInflater.inflate(R.layout.menu_layout, null);
		
		
		width = mContext.getResources().getDrawable(R.drawable.menu_top_bg)
				.getIntrinsicWidth();
		
		
		tabwidth = mContext.getResources().getDrawable(R.drawable.tab_bg)
				.getIntrinsicWidth();
		

		
		
		mListView = (ListView) popupView.findViewById(R.id.mMenuListview);
		
		mListView.setOnItemClickListener(this);

		// set our adapter and pass our pop up window contents
		mListView.setAdapter(mMenuAdapter);

		// some other visual settings
		mMenuItemView.setFocusable(true);
		if (view == view.findViewById(R.id.tabBtnFriends)) {

			DisplayMetrics displaymetrics = new DisplayMetrics();
			((Activity) mContext).getWindowManager().getDefaultDisplay()
					.getMetrics(displaymetrics);
			//int width = displaymetrics.widthPixels;
			Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int width = size.x;
			int height = size.y;
			if(width>height){
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					width/3,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			mListView.setLayoutParams(layoutParams);
			}else{
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				mListView.setLayoutParams(layoutParams);
			}
			mMenuItemView.setWidth(width/3);
			
		} else {
			mMenuItemView.setWidth(width + CommonUtils.convertToDp(mContext, 8));
		}
		mMenuItemView.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		mMenuItemView.setBackgroundDrawable(mContext.getResources()
				.getDrawable(android.R.color.transparent));

		//int[] multi = new int[2];
		//view.getLocationInWindow(multi);
		mMenuItemView.setContentView(popupView);
		
		if (view == view.findViewById(R.id.tabBtnFriends)) {
			mMenuItemView.showAsDropDown(view, 0, 0);
		}else {
			mMenuItemView.showAsDropDown(view, 0, CommonUtils.convertToDp(mContext, 11));
		}

	}

	public void dismisView() {
		if (mMenuItemView != null) {
			mMenuItemView.dismiss();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View arg1, int position,
			long arg3) {

		if (mMenuItemClickListener != null) {
			mMenuItemClickListener.onItemClick((MenuItems) adapter
					.getItemAtPosition(position));
		}

		dismisView();
	}
}
