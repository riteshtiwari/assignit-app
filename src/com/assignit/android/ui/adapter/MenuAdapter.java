package com.assignit.android.ui.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.assignit.android.R;
import com.assignit.android.pojo.MenuItems;
import com.assignit.android.ui.views.CustomTextView;

/**Menu View Adapter
 * 
 * @author Innoppl
 * 
 */
public class MenuAdapter extends BaseAdapter {

	private final List<MenuItems> mMenuItems;
	private final Context mContext;

	public MenuAdapter(Context Context, List<MenuItems> menuItems) {
		super();
		this.mContext = Context;
		this.mMenuItems = menuItems;
	}

	@Override
	public int getCount() {
		return mMenuItems.size();
	}

	@Override
	public Object getItem(int position) {

		return mMenuItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		
		if (convertView == null) {
			if(mMenuItems.get(0).itemName.equals(mContext.getResources().getString(R.string.friend_all))){
				view = View.inflate(mContext, R.layout.row_friendstab_menuitem_layout, null);
			}else{
				view = View.inflate(mContext, R.layout.row_menuitem_layout, null);
			}
			
		} else {
			view = convertView;
		}
		setData(view, position);

		return view;
	}

	/**
	 * Set data for menu view adapter
	 * 
	 * @param view
	 * @param position
	 */
	private void setData(View view, int position) {

		MenuItems menuItem = mMenuItems.get(position);

		CustomTextView menuIteName;
		CustomTextView menuItemCounter;
		ImageView menuItemImageView;
		
		menuIteName = (CustomTextView) view.findViewById(R.id.menuIteName);
		menuItemCounter = (CustomTextView) view.findViewById(R.id.menuItemCounter);
		menuItemImageView = (ImageView) view.findViewById(R.id.menuItemImageView);
		menuIteName.setText(menuItem.itemName);
		
		if(!mMenuItems.get(0).itemName.equals(mContext.getResources().getString(R.string.friend_all))){
		menuItemImageView.setImageDrawable(menuItem.drawable);

		if (menuItem.isCounterVisible) {
			menuItemCounter.setVisibility(View.VISIBLE);
			
			menuItemCounter.setText("  [" + menuItem.taskCounter + "]");
			
		} else {
			menuItemCounter.setVisibility(View.INVISIBLE);
		}
		
		if (menuItem.isEvernoteVisible) {
						
			menuIteName.setTypeface(Typeface.DEFAULT,Typeface.ITALIC);
			
		}else{
			menuIteName.setTypeface(Typeface.DEFAULT);
		}
		}
		if (menuItem.isFriendList) {
			
			if (position == 0) {
				view.setBackgroundResource(R.drawable.selector_menu_item_from_me_top);
			} else {
				view.setBackgroundResource(R.drawable.selctor_menu_item_from_me_bottom);
			}
		} else {
			if (mMenuItems.size() == 6) {
				
				if (position == 0) {
					view.setBackgroundResource(R.drawable.selector_menu_item_from_me_top);
				} else if (position == 1) {
					view.setBackgroundResource(R.drawable.selector_menu_item_middle);
				} else if (position == 2) {
					view.setBackgroundResource(R.drawable.selector_menu_item_middle);
				} else if (position == 3) {
					view.setBackgroundResource(R.drawable.selector_menu_item_middle);
				} else if (position == 4) {
					if (!menuItem.isEvernoteVisible) {
					view.setBackgroundResource(R.drawable.selector_menu_item_middle);
					}else{
						view.setBackgroundResource(R.drawable.menu_from_me_middle);
					}
				} else {
					if (!menuItem.isEvernoteVisible) {
					view.setBackgroundResource(R.drawable.selctor_menu_item_from_me_bottom);
					}else{
						view.setBackgroundResource(R.drawable.menu_from_me_bottom);
					}
				}
			}else if (mMenuItems.size() == 5) {
				if (position == 0) {
					view.setBackgroundResource(R.drawable.selector_menu_item_from_me_top);
				} else if (position == 1) {
					view.setBackgroundResource(R.drawable.selector_menu_item_middle);
				} else if (position == 2) {
					view.setBackgroundResource(R.drawable.selector_menu_item_middle);
				} else if (position == 3) {
					view.setBackgroundResource(R.drawable.menu_from_me_middle);
				} else {
					view.setBackgroundResource(R.drawable.menu_from_me_bottom);
				}
			}else if (mMenuItems.size() == 4) {
				if (position == 0) {
					view.setBackgroundResource(R.drawable.selector_menu_item_from_me_top);
				} else if (position == 1) {
					view.setBackgroundResource(R.drawable.selector_menu_item_middle);
				} else if (position == 2) {
					view.setBackgroundResource(R.drawable.selector_menu_item_middle);
				} else {
					view.setBackgroundResource(R.drawable.selctor_menu_item_from_me_bottom);
				}
			}else if (mMenuItems.size() == 3) {
								
				if (position == 0) {
					view.setBackgroundResource(R.drawable.selector_menu_item_from_me_top);
				} else if (position == 1) {
					view.setBackgroundResource(R.drawable.selector_menu_item_middle);
				} else {
					view.setBackgroundResource(R.drawable.selctor_menu_item_from_me_bottom);
				} 
				
				if(menuItem.itemName.equals(mContext.getResources().getString(R.string.one_time)))
				{
					menuIteName.setBackgroundResource(R.drawable.compose_task_cb_bg);
				}
					
				
			} else if (mMenuItems.size() == 2) {
				
				if (position == 0) {
					view.setBackgroundResource(R.drawable.selector_menu_item_from_me_top);
				} else {
					view.setBackgroundResource(R.drawable.selctor_menu_item_from_me_bottom);
				}
			} else {
				view.setBackgroundResource(R.drawable.selector_menu_item_single);
			}
		}
		}
}
