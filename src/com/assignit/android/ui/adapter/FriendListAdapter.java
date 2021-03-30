package com.assignit.android.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.assignit.android.R;
import com.assignit.android.pojo.Friends;
import com.assignit.android.pojo.MenuItems;
import com.assignit.android.ui.activities.ComposeTaskActivity_;
import com.assignit.android.ui.activities.ExistingTaskActivity_;
import com.assignit.android.ui.activities.HomeActivity;
import com.assignit.android.ui.activities.controller.FriendListController;
import com.assignit.android.ui.views.CustomMenuView;
import com.assignit.android.ui.views.CustomTextView;
import com.assignit.android.utils.AppConstant;
import com.assignit.android.utils.MobileContactsUtils;

/**
 * Friend task Adapter
 * 
 * @author Innoppl
 * 
 */
public class FriendListAdapter extends BaseAdapter {
	Context mContext;

	List<Friends> mFriendList;

	ArrayList<Friends> filterResultsData;
	MobileContactsUtils contactUtils;
	List<Friends> filterData = new ArrayList<Friends>();
	int height, width;

	public CustomMenuView mMenuView;
	private final FriendListController mFriendListContent;
	
	ImageView menuView;
	LinearLayout newTasklinearLayout, menuTaskLinearLayout,
			existTasklinearLayout;

	
	public FriendListAdapter(Context Context, List<Friends> friendList,
			FriendListController friendListControler) {
		super();
		this.mContext = Context;
		this.mFriendList = friendList;
		this.mFriendListContent = friendListControler;
		contactUtils = new MobileContactsUtils(mContext);

		height = Context.getResources()
				.getDrawable(R.drawable.img_friendlist_default_profile_pic)
				.getIntrinsicHeight();
		width = Context.getResources()
				.getDrawable(R.drawable.img_friendlist_default_profile_pic)
				.getIntrinsicWidth();
		filterData = friendList;
	}

	public void setData(List<Friends> friends) {
		mFriendList = friends;
		filterData = friends;
	}

	@Override
	public int getCount() {
		return mFriendList.size();
	}

	@Override
	public Object getItem(int position) {
		return mFriendList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;

		if (convertView == null) {
			view = View.inflate(mContext, R.layout.row_friendlist, null);

		} else {
			view = convertView;
		}

		setData(view, position);

		return view;
	}

	/**
	 * this method is called to initialize friends list item view
	 * @param view
	 * @param position
	 */
	private void setData(View view, final int position) {
		menuTaskLinearLayout = (LinearLayout) view.findViewById(R.id.menuviewLayout);
		newTasklinearLayout = (LinearLayout) view.findViewById(R.id.newTaskLayout);
		existTasklinearLayout = (LinearLayout) view.findViewById(R.id.existTaskLayout);
		
		CustomTextView tvexistingItemCounter = (CustomTextView) view.findViewById(R.id.existingItemCounter);
		
		
		
		if (FriendListController.selectedListItem == position) {
			menuTaskLinearLayout.setVisibility(View.VISIBLE);
		} else {
			menuTaskLinearLayout.setVisibility(View.GONE);
		}
		
		final Friends friends = mFriendList.get(position);

		CustomTextView friendName = (CustomTextView) view
				.findViewById(R.id.tv_friendlist_friend_name);
		ImageView imgProfilePic = (ImageView) view
				.findViewById(R.id.img_friendlist_profile_pic);
		
		friendName.setText(friends.Name);
		
		tvexistingItemCounter.setText("[" + friends.taskCounter + "]");
		if (friends.contactId != null) {
			Bitmap bitmap = null;
			
			 bitmap = contactUtils.openPhoto(Long
					.parseLong(friends.contactId));
			
			if (bitmap != null) {
				imgProfilePic.setImageBitmap(bitmap);
			} else {
				imgProfilePic.setImageDrawable(mContext.getResources()
						.getDrawable(
								R.drawable.img_friendlist_default_profile_pic));
			}
		}

		menuView = (ImageView) view.findViewById(R.id.btn_friendlist_menu);

		newTasklinearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
								
				  Intent intent = new Intent(mContext,ComposeTaskActivity_.class);
				  intent.putExtra(AppConstant.COMPOSE_TASK.USER_ID, friends.userId);
				  intent.putExtra(AppConstant.COMPOSE_TASK.USER_NAME,  friends.Name);
				  intent.putExtra(AppConstant.COMPOSE_TASK.EDIT_ENABLE,	  Boolean.FALSE);
				  
				  MenuItems menuItem2 = new MenuItems(); 
				  menuItem2.frindsList =  mFriendList;
				  
				  Bundle bundle = new Bundle();
				  bundle.putSerializable(AppConstant.COMPOSE_TASK.MENU_ITEM_OBJECT, menuItem2);
				  intent.putExtras(bundle);
				  
				  ((Activity) mContext).startActivityForResult(intent,  HomeActivity.COMPOSE_TASK_REQUEST);
			}
		});

		existTasklinearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
		Intent intent = new Intent(mContext, ExistingTaskActivity_.class);
		intent.putExtra(AppConstant.COMPOSE_TASK.USER_ID, friends.userId);
		intent.putExtra(AppConstant.COMPOSE_TASK.USER_NAME, friends.Name);
		intent.putExtra(AppConstant.COMPOSE_TASK.CONTACT_ID, friends.contactId);

		MenuItems menuItem2 = new MenuItems();
		menuItem2.frindsList = mFriendList;
		
		Bundle bundle = new Bundle();
		bundle.putSerializable(AppConstant.COMPOSE_TASK.MENU_ITEM_OBJECT, menuItem2);
		intent.putExtras(bundle);
		mContext.startActivity(intent);
			}
		});
		
		// To show popup view on friends list item

		menuView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (FriendListController.selectedListItem == position) {
					FriendListController.selectedListItem = -1;
					notifyDataSetChanged();
				} 

				List<MenuItems> items = new ArrayList<MenuItems>();
				// for new task
				MenuItems newTaskItem = new MenuItems();
				newTaskItem.drawable = mContext.getResources().getDrawable(	R.drawable.menu_item_new_task_logo);
				newTaskItem.itemName = mContext.getString(R.string.menu_new_task);
				newTaskItem.userId = friends.userId;
				newTaskItem.taskCounter = friends.taskCounter;
				newTaskItem.userName = friends.Name;
				newTaskItem.isCounterVisible = Boolean.FALSE;
				newTaskItem.isFriendList = Boolean.TRUE;

				// for Existing task
				MenuItems existingTaskItem = new MenuItems();
				existingTaskItem.drawable = mContext.getResources().getDrawable(R.drawable.menu_item_existing_task_logo);
				existingTaskItem.itemName = mContext.getString(R.string.menu_existing_task);
				existingTaskItem.userId = friends.userId;
				existingTaskItem.taskCounter = friends.taskCounter;
				existingTaskItem.userName = friends.Name;
				existingTaskItem.contactId = friends.contactId;
				existingTaskItem.isCounterVisible = Boolean.TRUE;
				existingTaskItem.isFriendList = Boolean.TRUE;

				items.add(newTaskItem);
				items.add(existingTaskItem);

				MenuAdapter adapter = new MenuAdapter(mContext, items);
				mMenuView = new CustomMenuView(mContext, adapter);
				mMenuView.setMenuItemClickListener(mFriendListContent);
				mMenuView.showMenu(v);
				((HomeActivity) mContext).closeSerchview();

			}

		});
	}

	/**
	 * this method is called to filter friends list from search edittext
	 */
	
	public Filter getFilter() {
		Filter filter = new Filter() {

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				Log.d("publishResults", " publishResults results.count:  "
						+ results.count);
				mFriendList = (ArrayList<Friends>) results.values;
				notifyDataSetChanged();

				if (((HomeActivity) mContext).isSearchBoxEnable
						&& mFriendList.isEmpty()) {
					((HomeActivity) mContext).noMatchFoundView
							.setVisibility(View.VISIBLE);
				} else {
					((HomeActivity) mContext).noMatchFoundView
							.setVisibility(View.GONE);
				}
			}

			
			@Override
			protected FilterResults performFiltering(CharSequence charSequence) {
				FilterResults results = new FilterResults();
				Log.d("performFiltering", " performFiltering charSequence :  "
						+ charSequence);

				Log.d("performFiltering mFriendList ",
						" performFiltering mFriendList size :  "
								+ mFriendList.size());
				// If there's nothing to filter on, return the original data for
				// your list
				if (charSequence == null || charSequence.length() == 0) {
					results.values = filterData;
					results.count = filterData.size();
				} else {
					filterResultsData = new ArrayList<Friends>();
					String filterString = charSequence.toString().toLowerCase();
					String filterableString;

					for (int i = 0; i < filterData.size(); i++) {
						filterableString = filterData.get(i).Name;
						if (filterableString.toLowerCase().contains(
								filterString)) {
							filterResultsData.add(filterData.get(i));
						}
					}

					results.values = filterResultsData;
					results.count = filterResultsData.size();
				}

				return results;
			}
		};
		return filter;
	}
}
