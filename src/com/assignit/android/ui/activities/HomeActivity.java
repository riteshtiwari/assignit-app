package com.assignit.android.ui.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.assignit.android.R;
import com.assignit.android.localstorage.UserSharedPreferences;
import com.assignit.android.pojo.MenuItems;
import com.assignit.android.ui.activities.controller.ForMeController;
import com.assignit.android.ui.activities.controller.FriendListController;
import com.assignit.android.ui.activities.controller.FromMeController;
import com.assignit.android.ui.adapter.MenuAdapter;
import com.assignit.android.ui.views.CustomButton;
import com.assignit.android.ui.views.CustomEditText;
import com.assignit.android.ui.views.CustomMenuView;
import com.assignit.android.ui.views.CustomMenuView.OnMenuItemClickListener;
import com.assignit.android.ui.views.CustomProgressbar;
import com.assignit.android.ui.views.CustomTextView;
import com.assignit.android.ui.views.CustomViewConstants;
import com.assignit.android.ui.views.TabView;
import com.assignit.android.ui.views.TabView.OnTabChangeListener;
import com.assignit.android.utils.AppConstant;
import com.assignit.android.utils.AppRater;
import com.assignit.android.utils.CommonUtils;
import com.assignit.android.utils.ContactService;
import com.assignit.android.utils.MobileContactsUtils;
import com.assignit.android.utils.StringUtils;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * This is central activity that manage all controller.
 * 
 * @author Innoppl
 * 
 */
@EActivity(R.layout.activity_home)
public class HomeActivity extends BaseActivity implements OnTabChangeListener,
		TextWatcher {

	@ViewById
	TabView tabView;
	//private Boolean isSelfTabChange;
	@ViewById
	public CustomEditText etSearchBox;

	int count = 0;

	@ViewById
	CustomButton tabBtnFromMe;

	@ViewById
	CustomButton tabBtnFriends;

	@ViewById
	CustomButton tabBtnForMe;

	@ViewById
	public CustomTextView noMatchFoundView;

	@Bean
	public FriendListController mFriendListController;

	@Bean
	public FromMeController mFromMeController;

	@Bean
	public ForMeController mForMeController;

	private boolean isnotyfy;

	@ViewById
	ImageView cleareSearch;

	public CustomMenuView mMenuView;

	public boolean isSearchBoxEnable = false;

	private int mSelectedViewId;

	// update task request code callback parameter on activity result
	public static final int UPDATED_TASK_REQUEST = 500;

	// update forme request code callback parameter on activity result
	public static final int UPDATED_FORME_TASK_REQUEST = 300;

	// compose task request code callback parameter on activity result
	public static final int COMPOSE_TASK_REQUEST = 200;

	int friendsTabView = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Boolean isUploaded = UserSharedPreferences.getInstance(this)
				.getBoolean(AppConstant.ISCONTACTSAVED);
		if (!isUploaded) {
			MobileContactsUtils contactUtils = new MobileContactsUtils(this);
			contactUtils.getContactFromMobileContact();
			UserSharedPreferences.getInstance(this).putBoolean(
					AppConstant.ISCONTACTSAVED, Boolean.TRUE);
		}
		mFriendListController.uploadFriendList();
		startService(new Intent(this, ContactService.class));
	}

	/*
	 * onConfigurationChanged(android.content.res.Configuration )
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (mMenuView != null) {
			mMenuView.dismisView();
		}
		FriendListController.selectedListItem = -1;
	}

	/**
	 * this method is called after view is initialised
	 */
	@AfterViews
	public void afterInitView() {

		configImageLoader();
		showSerchView(false);
		showHomeScreenLogo(true);
		showSerchButton(true);
		tabView.setOnTabChangeListener(this);
		etSearchBox.addTextChangedListener(this);

		Bundle extra = this.getIntent().getExtras();
		String event = null;

		if (extra != null) {
			isnotyfy = extra
					.getBoolean(AppConstant.NOTIFICATION.ISNOTIFICATION);
			event = extra.getString(AppConstant.NOTIFICATION.EVENT);
			openScreenForNotification(event);
		}

		if (!isnotyfy) {
			openScreenForHomeFriends();
		}

		IntentFilter filter = new IntentFilter();
		filter.addAction(AppConstant.ACTION_REFRESH);
		this.registerReceiver(notificationReciever, filter);
	}

	/**
	 * Navigate screen to particular tab on notification click action
	 * 
	 * @param event
	 */
	private void openScreenForNotification(String event) {

		if (event.equals(AppConstant.NOTIFICATION.FOR_ME_EVENT)) {
			mFriendListController.getFriendDetails(UserSharedPreferences
					.getInstance(this).getString(AppConstant.USER_ID),
					AppConstant.FRIENDS_TAB_VALUE);
			onTabChanged(R.id.tabBtnForMe);
			tabView.getTabForMeView().performClick();
		} else if (event.equals(AppConstant.NOTIFICATION.FROM_ME_EVENT)) {
			mFriendListController.getFriendDetails(UserSharedPreferences
					.getInstance(this).getString(AppConstant.USER_ID),
					AppConstant.FRIENDS_TAB_VALUE);
			onTabChanged(R.id.tabBtnFromMe);
			tabView.getTabFromMeView().performClick();
		} else if (event.equals(AppConstant.NOTIFICATION.FRIENDS)) {

			openScreenForHomeFriends();
		}
	}

	/**
	 * this method is called ON TAB CHANGES
	 * 
	 * @see com.assignit.android.ui.views.TabView.OnTabChangeListener#onTabChanged(int)
	 */
	@Override
	public void onTabChanged(int viewId) {

		mSelectedViewId = viewId;
		
		if (R.id.tabBtnFromMe == viewId) {
			closeSerchview();
			count = 0;
			mFromMeController.openFromMeListScreen();
			tabBtnFromMe.setTextColor(getResources().getColor(
					R.color.color_black));
			tabBtnFriends.setTextColor(getResources().getColor(
					R.color.home_page_tab_colore));
			tabBtnForMe.setTextColor(getResources().getColor(
					R.color.home_page_tab_colore));
		} else if (R.id.tabBtnFriends == viewId) {

			count++;

			tabBtnFromMe.setTextColor(getResources().getColor(
					R.color.home_page_tab_colore));
			tabBtnFriends.setTextColor(getResources().getColor(
					R.color.color_black));
			tabBtnForMe.setTextColor(getResources().getColor(
					R.color.home_page_tab_colore));
			
			// refresh on delete task in forme and existing
				if (UserSharedPreferences.getInstance(this).getBoolean(
						AppConstant.ACTION_REFRESH_FRIENDS)) {
					UserSharedPreferences.getInstance(this).putBoolean(
							AppConstant.ACTION_REFRESH_FRIENDS, Boolean.FALSE);
					if (CommonUtils.isNetworkAvailable(this)) {
						mFriendListController.refreshFriendList(friendsTabView);
					} else {
						CommonUtils.showErrorToast(
								this,
								getResources().getString(
										R.string.no_internate_connetion));
					}
				}
			
			dropDownFriendsMenu();

			// refresh on tab change on notification
			if (isnotyfy) {
				isnotyfy = false;
				openScreenForHomeFriends();
			} else {
				mFriendListController.openFriendListScreen();
			}

			

		} else if (R.id.tabBtnForMe == viewId) {

			count = 0;
			mForMeController.openForMeListScreen();
			tabBtnFromMe.setTextColor(getResources().getColor(
					R.color.home_page_tab_colore));
			tabBtnFriends.setTextColor(getResources().getColor(
					R.color.home_page_tab_colore));
			tabBtnForMe.setTextColor(getResources().getColor(
					R.color.color_black));
			closeSerchview();
		}
	}

	/**
	 * THIS METHOD IS CALLED TO SHOW THE POPUP MENU ON FREINDS TAB BUTTON PRESS
	 * TO SHOW FREQUEND AND EXISTING VIEW
	 */
	@SuppressLint("DefaultLocale")
	private void dropDownFriendsMenu() {

		List<MenuItems> items = new ArrayList<MenuItems>();
		// for Friend Tab
		// All
		MenuItems allItem = new MenuItems();
		allItem.itemName = getString(R.string.friend_all);
		allItem.isFriendList = Boolean.FALSE;
		allItem.isCounterVisible = Boolean.FALSE;

		// Freguent
		MenuItems frequentItem = new MenuItems();
		frequentItem.itemName = getString(R.string.friend_frequent);
		frequentItem.isFriendList = Boolean.FALSE;
		frequentItem.isCounterVisible = Boolean.FALSE;

		// Existing
		MenuItems existingItem = new MenuItems();
		existingItem.itemName = getString(R.string.friend_existing);
		existingItem.isFriendList = Boolean.FALSE;
		existingItem.isCounterVisible = Boolean.FALSE;

		items.add(allItem);
		items.add(frequentItem);
		items.add(existingItem);
		closeSerchview();
		FriendListController.selectedListItem = -1;
		MenuAdapter adapter = new MenuAdapter(this, items);
		mMenuView = new CustomMenuView(this, adapter);
		mMenuView.setMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onItemClick(MenuItems menuItem) {
				if (menuItem.itemName.equals(getString(R.string.friend_all))) {
					friendsTabView = 0;
					tabBtnFriends.setText(getString(R.string.friend_all)
							.toUpperCase());
					if (CommonUtils.isNetworkAvailable(HomeActivity.this)) {
						mFriendListController.refreshFriendList(0);
					} else {
						CommonUtils.showErrorToast(HomeActivity.this,
								getString(R.string.no_internate_connetion));
					}
				} else if (menuItem.itemName.equals(getString(R.string.friend_frequent))) {
					friendsTabView = 1;
					tabBtnFriends.setText(getString(R.string.friend_frequent)
							.toUpperCase());
					if (CommonUtils.isNetworkAvailable(HomeActivity.this)) {
						mFriendListController.refreshFriendList(1);
					} else {
						CommonUtils.showErrorToast(HomeActivity.this,
								getString(R.string.no_internate_connetion));
					}
				} else if (menuItem.itemName.equals(getString(R.string.friend_existing))) {
					friendsTabView = 2;
					tabBtnFriends.setText(getString(R.string.friend_existing)
							.toUpperCase());
					if (CommonUtils.isNetworkAvailable(HomeActivity.this)) {
						mFriendListController.refreshFriendList(2);
					} else {
						CommonUtils.showErrorToast(HomeActivity.this,
								getString(R.string.no_internate_connetion));
					}
				}
			}
		});
		if (count > 1) {
			mMenuView.showMenu(tabBtnFriends);
		}
	}

	/**
	 * THIS METHOD IS CALLED WHEN USER PRESS REFERESS BUTTONS
	 */
	@Click
	public void btnBaseHeaderButtonRefresh() {
		closeSerchview();
		CommonUtils.hideSoftKeyboard(this);
		if (CommonUtils.isNetworkAvailable(this)) {
			if (mSelectedViewId == R.id.tabBtnFriends) {
				mFriendListController.refreshFriendList(friendsTabView);
			} else if (mSelectedViewId == R.id.tabBtnFromMe) {
				CustomProgressbar.showProgressBar(this, false,
						getString(R.string.refresh_fromMe_list_message));
				mFromMeController.doApiCallTaskListFromMe();
			} else if (mSelectedViewId == R.id.tabBtnForMe) {
				CustomProgressbar.showProgressBar(this, false,
						getString(R.string.refresh_forMe_list_message));
				mForMeController.doApiCallTaskListForMe();
			}
		} else {
			CommonUtils.showErrorToast(this,
					getResources().getString(R.string.no_internate_connetion));
		}
	}

	/**
	 * click on clear button of search box
	 */
	@Click
	public void cleareSearch() {
		etSearchBox.setText("");
	}

	/**
	 * this method call on search button press to show and hide search view
	 */
	@Click
	public void btnBaseHeaderButtonSearch() {

		cleareSearch.setVisibility(View.GONE);

		if (isSearchBoxEnable) {
			showSerchView(false);
			etSearchBox.setText("");
			isSearchBoxEnable = false;
			CommonUtils.hideSoftKeyboard(this);
			showHomeScreenLogo(true);
		} else {
			showSerchView(true);
			isSearchBoxEnable = true;
			etSearchBox.requestFocus();
			CommonUtils.showSoftKeyboard(this, etSearchBox);
			showHomeScreenLogo(false);
		}

	}

	/**
	 * this method clear search view on refresh button press
	 */
	public void closeSerchview() {
		if (cleareSearch.getVisibility() == View.VISIBLE) {
			cleareSearch.setVisibility(View.GONE);
		}
		if (isSearchBoxEnable) {
			etSearchBox.setText("");
			isSearchBoxEnable = false;
			CommonUtils.hideSoftKeyboard(this);
			showHomeScreenLogo(true);
			showSerchView(false);
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		CommonUtils.hideSoftKeyboard(this);
		return super.onTouchEvent(event);

	}

	@Override
	public void afterTextChanged(Editable arg0) {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (StringUtils.isNotBlank(s.toString())) {
			cleareSearch.setVisibility(View.VISIBLE);
		} else {
			cleareSearch.setVisibility(View.GONE);
		}
		if (mSelectedViewId == R.id.tabBtnFriends) {
			mFriendListController.adapter.getFilter().filter(s);
		} else if (mSelectedViewId == R.id.tabBtnFromMe) {
			mFromMeController.adapter.getFilter().filter(s);
		} else if (mSelectedViewId == R.id.tabBtnForMe) {
			mForMeController.adapter.getFilter().filter(s);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == UPDATED_TASK_REQUEST && resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			String name = bundle.getString(AppConstant.COMPOSE_TASK.FRIEND_USER_NAME);
			String taskStatusId = bundle.getString(AppConstant.ASSIGN_TASK.TASK_STATUS_ID);

			if (StringUtils.isNotBlank(taskStatusId)) {
				CommonUtils.showSuccessToast(this,getString(R.string.task_assigned_to) + " " + name + "!");
			} else {
				CommonUtils.showSuccessToast(this,getString(R.string.task_update_success_fully));
			}

			mForMeController.mIninitialiseForMe = Boolean.TRUE;
			mFromMeController.mIninitialiseFromMe = Boolean.TRUE;
			mFromMeController.openFromMeListScreen();
		}

		if (requestCode == UPDATED_FORME_TASK_REQUEST && resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			String name = bundle.getString(AppConstant.COMPOSE_TASK.FRIEND_USER_NAME);
			String taskStatusId = bundle.getString(AppConstant.ASSIGN_TASK.TASK_STATUS_ID);
			if (StringUtils.isNotBlank(taskStatusId)) {
				CommonUtils.showSuccessToast(this,getString(R.string.task_assigned_to) + " "+ name + "!");
			} else {
				CommonUtils.showSuccessToast(this,getString(R.string.task_update_success_fully));
			}
			mForMeController.mIninitialiseForMe = Boolean.TRUE;
			mFromMeController.mIninitialiseFromMe = Boolean.TRUE;
			mForMeController.openForMeListScreen();
		}

		if (requestCode == COMPOSE_TASK_REQUEST && resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			String name = bundle.getString(AppConstant.COMPOSE_TASK.USER_NAME);

			CommonUtils.showSuccessToast(this,
					getString(R.string.task_assigned_to) + " " + name + "!");
			mForMeController.mIninitialiseForMe = Boolean.TRUE;
			mFromMeController.mIninitialiseFromMe = Boolean.TRUE;
		}
		// Reset Search string
		etSearchBox.setText("");
		showSerchView(false);
		showHomeScreenLogo(true);
		isSearchBoxEnable = Boolean.FALSE;
	}

	/**
	 * this method opens initial friends list from server.
	 */
	private void openScreenForHomeFriends() {
		mFriendListController.openFriendListScreen();

		mSelectedViewId = R.id.tabBtnFriends;
		if (CommonUtils.isNetworkAvailable(this)) {
			mFriendListController.getFriendList(0);
		} else {
			CommonUtils.showErrorToast(this,
					getResources().getString(R.string.no_internate_connetion));
		}
	}

	/**
	 * Refresh tab on receive notification
	 * 
	 * @param messageevent
	 * @param message
	 */
	public void refreshSelectedTab(String messageevent, String message) {

		if (!messageevent.equals(AppConstant.NOTIFICATION.FRIENDS)) {

			if (messageevent.equals(AppConstant.NOTIFICATION.FOR_ME_EVENT)) {

				if (mSelectedViewId == R.id.tabBtnForMe) {
					showDialog(message, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							CustomProgressbar.showProgressBar(HomeActivity.this,false,
											getString(R.string.refresh_forMe_list_message));
							mForMeController.doApiCallTaskListForMe();
						}
					});

				} else {
					showDialog(message, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							onTabChanged(R.id.tabBtnForMe);
							tabView.getTabForMeView().performClick();
							
						}
					});
				}
			} else if (messageevent
					.equals(AppConstant.NOTIFICATION.FROM_ME_EVENT)) {
				if ( mFriendListController != null) {
					mFriendListController.hideMenu();
				}
				UserSharedPreferences.getInstance(this).putBoolean(AppConstant.ACTION_REFRESH_FRIENDS, Boolean.TRUE);
				if (mSelectedViewId == R.id.tabBtnFromMe) {
					showDialog(message, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							CustomProgressbar
									.showProgressBar(
											HomeActivity.this,
											false,
											getString(R.string.refresh_fromMe_list_message));
							mFromMeController.doApiCallTaskListFromMe();
						}
					});

				} else {
					showDialog(message, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							onTabChanged(R.id.tabBtnFromMe);
							tabView.getTabFromMeView().performClick();
							
						}
					});
				}
			}
		}
	}

	/**
	 * Callback method to show dialog on notification if not in relevant tab
	 * 
	 * @param msg
	 * @param clickListener
	 */
	private void showDialog(String msg,
			DialogInterface.OnClickListener clickListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle(getString(R.string.notification_msg))
				.setMessage(msg)
				.setPositiveButton(getString(R.string.button_ok), clickListener)
				.show();
	}

	@Override
	protected void onDestroy() {

		try {
			unregisterReceiver(notificationReciever);
		} catch (Exception e) {
			Log.e("UnRegister Receiver Error", "> " + e.getMessage());
		}

		// Clear cache
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File(sdCard.getAbsolutePath() + "/"
				+ AppConstant.TASK_IMAGE_FOLDER_NAME);
		CommonUtils.deleteDirectory(dir);
		super.onDestroy();
	}

	/**
	 * Callback method to get selected tab id
	 * 
	 * @return the selected tab 
	 */
	public int getSelectedTab(){
		return mSelectedViewId;
	}
	
	
	/**
	 * Inititialise image loader
	 */
	private void configImageLoader() {

		// Create global configuration and initialize ImageLoader with this
		// configuration
		if (!ImageLoader.getInstance().isInited()) {
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
					getApplicationContext()).defaultDisplayImageOptions(
					SplashActivity.getDisplayImageOptions()) // default
					.build();

			ImageLoader.getInstance().init(config);
			CustomViewConstants.loadFonts(this);
		}

	}

	// receiver class to refresh tabs on notification receive
	BroadcastReceiver notificationReciever = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(AppConstant.ACTION_REFRESH)) {
				String messageevent = intent.getExtras().getString(
						AppConstant.NOTIFICATION.EVENT);
				String message = intent.getExtras().getString(
						AppConstant.NOTIFICATION.MESSAGE);
			
				refreshSelectedTab(messageevent, message);
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		if (FriendListController.selectedListItem != -1) {
			FriendListController.selectedListItem = -1;
		}
		if (UserSharedPreferences.getInstance(this).getBoolean(AppConstant.ACTION_REFRESH_FRIENDS)) {
			UserSharedPreferences.getInstance(this).putBoolean(AppConstant.ACTION_REFRESH_FRIENDS, Boolean.FALSE);
			UserSharedPreferences.getInstance(this).putBoolean(UserSharedPreferences.KEY_FORME_STATUS, Boolean.TRUE);
			if (CommonUtils.isNetworkAvailable(this)) {
				if (mSelectedViewId == R.id.tabBtnFriends) {
					if(UserSharedPreferences.getInstance(this).getBoolean(AppConstant.ACTION_APP_RATER)){
						UserSharedPreferences.getInstance(this).putBoolean(AppConstant.ACTION_APP_RATER, Boolean.FALSE);
					AppRater.appLaunched(HomeActivity.this);}
					mFriendListController.refreshFriendList(friendsTabView);
					UserSharedPreferences.getInstance(this).putBoolean(UserSharedPreferences.KEY_FROMME_STATUS,Boolean.TRUE);
				} else if (mSelectedViewId == R.id.tabBtnFromMe) {
					CustomProgressbar.showProgressBar(this, false,getString(R.string.refresh_fromMe_list_message));
					mFromMeController.doApiCallTaskListFromMe();
				}
			} else {
				CommonUtils.showErrorToast(this,getResources().getString(R.string.no_internate_connetion));
			}
		}
		// Change tab to forme if task assigned for self
		if(UserSharedPreferences.getInstance(this).getBoolean(AppConstant.SELF_FORME_TAB_CHANGE)){
			//isSelfTabChange = Boolean.TRUE;
			UserSharedPreferences.getInstance(this).putBoolean(AppConstant.SELF_FORME_TAB_CHANGE, Boolean.FALSE);
			if (mSelectedViewId == R.id.tabBtnFriends||mSelectedViewId == R.id.tabBtnFromMe) {
			onTabChanged(R.id.tabBtnForMe);
			tabView.getTabForMeView().performClick();}
		}
	}
}
