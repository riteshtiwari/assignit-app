package com.assignit.android.ui.activities.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.assignit.android.R;
import com.assignit.android.localstorage.UserSharedPreferences;
import com.assignit.android.net.WebServiceManager;
import com.assignit.android.net.responsebean.DeleteTaskResponse;
import com.assignit.android.net.responsebean.SendReminderResponse;
import com.assignit.android.net.responsebean.TaskFromMeRespose;
import com.assignit.android.pojo.MenuItems;
import com.assignit.android.pojo.TaskFromMe;
import com.assignit.android.ui.activities.ComposeTaskActivity_;
import com.assignit.android.ui.activities.HomeActivity;
import com.assignit.android.ui.activities.SplashActivity;
import com.assignit.android.ui.adapter.FromMeAdapter;
import com.assignit.android.ui.views.CustomMenuView.OnMenuItemClickListener;
import com.assignit.android.ui.views.CustomProgressbar;
import com.assignit.android.utils.AppConstant;
import com.assignit.android.utils.CommonUtils;
import com.assignit.android.utils.ExpandAnimation;
import com.assignit.android.utils.StringUtils;
import com.assignit.android.utils.ValidationUtils;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.timroes.swipetodismiss.SwipeDismissList;
import de.timroes.swipetodismiss.SwipeDismissList.SwipeDirection;
import de.timroes.swipetodismiss.SwipeDismissList.Undoable;

/**
 * This class is represent Frome screen.It's responsibility to controll view flow and business
 * logic.
 * 
 * @author Innoppl 
 * 
 */
@EBean
public class FromMeController implements OnItemClickListener, OnMenuItemClickListener {

	private final String TAG = FromMeController.class.getSimpleName();

	@RootContext
	public HomeActivity mActivity;

	@ViewById
	ListView mFriendsListView;

	@ViewById
	public ListView mFromMeListView;

	@ViewById
	ListView mForMeListView;
	
	String LoginUserId = "";

	private TaskFromMe mTempTask;

	public List<TaskFromMe> mFromMes;

	public FromMeAdapter adapter;

	ImageLoader imageLoader;
	
	DisplayImageOptions options;
	
	public Boolean mIninitialiseFromMe = Boolean.TRUE;
	
	ImageView menuView;
	
	private final List<View> listOfAnomaitedView = new ArrayList<View>();


	/**
	 * Method to Initialise ui views
	 */
	@AfterViews
	public void viewInitialise() {
		mFriendsListView.setVisibility(View.GONE);
		mFromMeListView.setVisibility(View.VISIBLE);
		mForMeListView.setVisibility(View.GONE);

		mFromMes = new ArrayList<TaskFromMe>();
		adapter = new FromMeAdapter(mActivity, mFromMes, this);
		LoginUserId  = UserSharedPreferences.getInstance(mActivity).getString(AppConstant.USER_ID);
		imageLoader = ImageLoader.getInstance();
		options = SplashActivity.getDisplayImageOptionsWithUserImage();

		mFromMeListView.setOnItemClickListener(this);
		mFromMeListView.setAdapter(adapter);
		SwipeDismissList swipeList = new SwipeDismissList(mFromMeListView, callback,
				SwipeDismissList.UndoMode.SINGLE_UNDO);
		swipeList.setSwipeDirection(SwipeDirection.END);

		//onScrollListView();
	}

	/**
	 * Initial friend list update from server
	 */
	public void openFromMeListScreen() {
		adapter.getFilter().filter("");
		if (mFriendsListView != null && mFromMeListView != null && mForMeListView != null) {
			mFriendsListView.setVisibility(View.GONE);
			mFromMeListView.setVisibility(View.VISIBLE);
			mForMeListView.setVisibility(View.GONE);
		}

		if (CommonUtils.isNetworkAvailable(mActivity)) {
			if (mIninitialiseFromMe
					|| UserSharedPreferences.getInstance(mActivity).getBoolean(UserSharedPreferences.KEY_FROMME_STATUS)) {
				CustomProgressbar.showProgressBar(mActivity, true,
						mActivity.getString(R.string.loading_from_task_message));
				doApiCallTaskListFromMe();
				mIninitialiseFromMe = Boolean.FALSE;
				UserSharedPreferences.getInstance(mActivity).putBoolean(UserSharedPreferences.KEY_FROMME_STATUS,
						Boolean.FALSE);
			}
		} else {
			CommonUtils.showErrorToast(mActivity, mActivity.getResources().getString(R.string.no_internate_connetion));
		}
	}

	
	// on list item click
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {

		final TaskFromMe taskFromMe = (TaskFromMe) adapter.getItemAtPosition(position);
		if (StringUtils.isNotBlank(taskFromMe.Image)) {
			taskFromMe.mSeletedTaskId = taskFromMe.taskId;
			taskFromMe.position = position;

			if (!taskFromMe.mState) {
				taskFromMe.mState = Boolean.TRUE;
			} else {
				taskFromMe.mState = Boolean.FALSE;
			}
		} else {
			taskFromMe.mSeletedTaskId = AppConstant.DUMMI_TASK_ID;
			taskFromMe.position = position;
			taskFromMe.mState = Boolean.FALSE;

		}

		LinearLayout photoView = (LinearLayout) view.findViewById(R.id.mPhotoLinearLayout);
		View descriptionView = view.findViewById(R.id.mTaskNameView);
		ExpandAnimation expandAni = new ExpandAnimation(mActivity, photoView, descriptionView, 400, taskFromMe.Image);
		view.startAnimation(expandAni);
		if (taskFromMe.mState) {
			listOfAnomaitedView.add(photoView);
		}
	}
	
	/**
	 * Get list of task from server by logged in user.
	 * 
	 */
	@Background
	public void doApiCallTaskListFromMe() {
		TaskFromMeRespose response = null;

		response = WebServiceManager.getInstance(mActivity).getFromMeTask(UserSharedPreferences.getInstance(mActivity).getString(AppConstant.USER_ID));

		updateUI(response);
	}

	/**
	 * Update UI with list of task me by server response.
	 * 
	 * @param response
	 */
	@UiThread
	public void updateUI(TaskFromMeRespose response) {
		if(response!=null){
		if (ValidationUtils.isSuccessResponse(response)) {
			
			Collections.sort(response.tasks, new Comparator<TaskFromMe>() {
				@Override
				public int compare(TaskFromMe f1, TaskFromMe f2) {
					String first = f1.Modified;
					String second = f2.Modified;
					return second.compareToIgnoreCase(first);
				}
			});

			if (adapter.mMenuView != null) {
				adapter.mMenuView.dismisView();
			}
			adapter.updateAdapter(response.tasks);
			//Display message on empty list
			if(response.tasks.size() < 1){
					if(mActivity.getSelectedTab() == R.id.tabBtnFromMe)
					CommonUtils.showDialogOnEmptyList(mActivity);
				}
			
			for (View v : listOfAnomaitedView) {
				LayoutParams params = (LayoutParams) v.getLayoutParams();
				params.bottomMargin = -CommonUtils.convertToDp(mActivity, 123);
			}
			listOfAnomaitedView.clear();
			if (StringUtils.isNotBlank(mActivity.etSearchBox.getText().toString())) {
				adapter.getFilter().filter(mActivity.etSearchBox.getText().toString());
			}
		} 
		}else{
			if (CommonUtils.isNetworkAvailable(mActivity)) {
			CommonUtils.showErrorToast(mActivity,
					mActivity.getResources().getString(R.string.time_out_connetion));
			}else{
				CommonUtils.showErrorToast(mActivity,
						mActivity.getResources().getString(R.string.no_internate_connetion));
			}
}
		CustomProgressbar.hideProgressBar();
	}

	/**
	 * This is call-back method to get called when user has clicked on menu item.
	 */
	@Override
	public void onItemClick(final MenuItems menuItem) {
		if (CommonUtils.isNetworkAvailable(mActivity)) {
			if (menuItem.itemName.equalsIgnoreCase(mActivity.getString(R.string.task_menu_delete))) {

				AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

				builder.setTitle(mActivity.getString(R.string.delete_task_dialog_title))
						.setMessage(mActivity.getString(R.string.delete_task_dialog_message))
						.setPositiveButton(mActivity.getString(R.string.button_yes),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int id) {
										UserSharedPreferences
										.getInstance(mActivity)
										.putBoolean(
												AppConstant.ACTION_REFRESH_FRIENDS,
												Boolean.TRUE);
										removeAndRefreshView(menuItem.taskId);
										doApiCallDeleteTask(menuItem.taskId);
										CommonUtils.showSuccessToast(mActivity, mActivity.getString(
												R.string.task_deleted_success_fully, menuItem.friendUserName));
									}
								})
						.setNegativeButton(mActivity.getString(R.string.button_no),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int id) {

									}
								});

				Dialog dialog = builder.create();
				dialog.show();

			} else if (menuItem.itemName.equalsIgnoreCase(mActivity.getString(R.string.task_menu_edit))) {
			
				
				Intent intent = new Intent(mActivity, ComposeTaskActivity_.class);
				intent.putExtra(AppConstant.COMPOSE_TASK.USER_ID, menuItem.friendUserId);
				intent.putExtra(AppConstant.COMPOSE_TASK.FRIEND_USER_NAME, menuItem.friendUserName);
				intent.putExtra(AppConstant.COMPOSE_TASK.TASK_DESCRIPTION, menuItem.taskDescription);
				intent.putExtra(AppConstant.COMPOSE_TASK.TASK_DUEDATE_STATUS, menuItem.taskStatus);
				intent.putExtra(AppConstant.COMPOSE_TASK.TASK_DUE_STARTDATE, menuItem.taskStartDueDate);
				intent.putExtra(AppConstant.COMPOSE_TASK.TASK_DUE_ENDDATE, menuItem.taskEndDueDate);
				intent.putExtra(AppConstant.COMPOSE_TASK.TASK_DUEDATE_REPEATEND, menuItem.taskEndRepeatDueDate);
				intent.putExtra(AppConstant.COMPOSE_TASK.TASK_DUEDATE_REPEAT, menuItem.taskRepeatDueDate);
				intent.putExtra(AppConstant.COMPOSE_TASK.TASK_IS_REPEATEND, menuItem.isRepeated);
				intent.putExtra(AppConstant.COMPOSE_TASK.EDIT_ENABLE, Boolean.TRUE);
				intent.putExtra(AppConstant.COMPOSE_TASK.TASK_ID, menuItem.taskId);
				intent.putExtra(AppConstant.COMPOSE_TASK.TASK_IMGAGE_URI, menuItem.imageURI);
				intent.putExtra(AppConstant.COMPOSE_TASK.TASK_TYPE, menuItem.taskType);
				
				MenuItems menuItem2 = new MenuItems();
				menuItem2.frindsList = mActivity.mFriendListController.spinnerList;
				
				Bundle bundle = new Bundle();
				bundle.putSerializable(AppConstant.COMPOSE_TASK.MENU_ITEM_OBJECT, menuItem2);
				intent.putExtras(bundle);
				
				mActivity.startActivityForResult(intent, HomeActivity.UPDATED_TASK_REQUEST);

			} else if (menuItem.itemName.equalsIgnoreCase(mActivity.getString(R.string.task_menu_reminder))) {

				showPopUp(menuItem.taskId, menuItem.friendUserName);

			}
		} else {
			CommonUtils.showErrorToast(mActivity, mActivity.getResources().getString(R.string.no_internate_connetion));
		}

	}

	/**
	 * Physically remove task from server.
	 * 
	 * @param taskId
	 */
	@Background
	public void doApiCallDeleteTask(String taskId) {
		DeleteTaskResponse response = null;
		response = WebServiceManager.getInstance(mActivity).deleteTask(LoginUserId, taskId);
		
		updateUIONDelete(response);
	}

	/**
	 * Update View if deletion has successfully completed.
	 * 
	 * @param response
	 */
	@UiThread
	public void updateUIONDelete(DeleteTaskResponse response) {
		if(response!=null){
			Log.d(TAG, " deletTask Status: " + response.httpStatusCode);
		if (adapter.mMenuView != null) {
			adapter.mMenuView.dismisView();
		}
		if (!ValidationUtils.isSuccessResponse(response)) {
			if (mTempTask != null) {
				adapter.getAdapterList().add(mTempTask);
				adapter.notifyDataSetChanged();
			}
			CommonUtils.showErrorToast(mActivity, mActivity.getString(R.string.task_deleted_failed));
		} }
	}

	/**
	 * Remove task locally
	 * 
	 * @param taskId
	 */
	private void removeAndRefreshView(String taskId) {

		List<TaskFromMe> fromMes = adapter.getAdapterList();

		for (TaskFromMe taskFromMe : fromMes) {
			if (taskId.equalsIgnoreCase(taskFromMe.taskId)) {
				mTempTask = taskFromMe;
				fromMes.remove(taskFromMe);
				break;
			}
		}
		adapter.updateAdapter(fromMes);
	}

	/**
	 * Remove task locally
	 * 
	 * @param taskId
	 */
	private void removeAndRefreshViewFromSwipe(String taskId) {

		List<TaskFromMe> fromMes = adapter.getAdapterList();

		for (TaskFromMe taskFromMe : fromMes) {
			if (taskId.equalsIgnoreCase(taskFromMe.taskId)) {
				mTempTask = taskFromMe;
				fromMes.remove(taskFromMe);
				break;
			}
		}
		adapter = new FromMeAdapter(mActivity, fromMes, this);
		mFromMeListView.setAdapter(adapter);
	}
	/**
	 * without delete the list
	 * 
	 * @param taskId
	 */
	public void removeRefreshView(String taskId) {

		List<TaskFromMe> fromMes = adapter.getAdapterList();

		for (TaskFromMe taskFromMe : fromMes) {
			if (taskId.equalsIgnoreCase(taskFromMe.taskId)) {
				mTempTask = taskFromMe;
				break;
			}
		}
		adapter = new FromMeAdapter(mActivity, fromMes, this);
		mFromMeListView.setAdapter(adapter);
	}
	/**
	 * Send Reminder to users.
	 * 
	 * @param taskId
	 */
	@Background
	public void doApiCallSendReminderTask(String taskId) {
		SendReminderResponse response = null;
		response = WebServiceManager.getInstance(mActivity).remindTask(LoginUserId,taskId);
		
		showToastMessage(response);
	}

	/**
	 * Show Toast message.
	 * 
	 * @param response
	 */
	@UiThread
	public void showToastMessage(SendReminderResponse response) {
		if(response!=null){
			Log.d(TAG, " Task reminder Status: " + response.httpStatusCode);
		if (ValidationUtils.isSuccessResponse(response)) {

			CommonUtils.showSuccessToast(mActivity, mActivity.getString(R.string.task_send_reminder_sucess_fully));

		} else {
			CommonUtils.showErrorToast(mActivity, mActivity.getString(R.string.task_send_reminder_already));
		}
		}
		}

	/**
	 * show popup on reminder set
	 * 
	 * @param taskId
	 * @param friendUserName
	 */
	private void showPopUp(final String taskId, String friendUserName) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

		builder.setTitle(R.string.task_reminder_title).setMessage(R.string.task_reminder_message)
				.setPositiveButton(mActivity.getString(R.string.button_yes), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						doApiCallSendReminderTask(taskId);
					}
				}).setNegativeButton(mActivity.getString(R.string.button_no), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {

					}
				});

		;

		Dialog dialog = builder.create();
		dialog.show();
	}

	
	/**
	 * Callback Class to delete task on swipe
	 */
	SwipeDismissList.OnDismissCallback callback = new SwipeDismissList.OnDismissCallback() {
		@Override
		public Undoable onDismiss(AbsListView listView, final int position) {

			AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

			builder.setTitle(mActivity.getString(R.string.delete_task_dialog_title))
					.setMessage(mActivity.getString(R.string.delete_task_dialog_message))
					.setPositiveButton(mActivity.getString(R.string.button_yes), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							UserSharedPreferences
							.getInstance(mActivity)
							.putBoolean(
									AppConstant.ACTION_REFRESH_FRIENDS,
									Boolean.TRUE);
							// Delete the item from your adapter (sample code):
							List<TaskFromMe> fromMes = adapter.getAdapterList();
							try {
								final TaskFromMe itemeToDelete = fromMes.get(position);
								removeAndRefreshViewFromSwipe(itemeToDelete.taskId);
								doApiCallDeleteTask(itemeToDelete.taskId);
								CommonUtils.showSuccessToast(mActivity, mActivity.getString(
										R.string.task_deleted_success_fully, itemeToDelete.ToUser.ToUsername));
							} catch (Exception e) {
								e.printStackTrace();
								Log.e(TAG, "Error on deleting from me task");
							}

						}
					})
					.setNegativeButton(mActivity.getString(R.string.button_no), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							// Delete the item from your adapter (sample code):
							List<TaskFromMe> fromMes = adapter.getAdapterList();
							try {
								final TaskFromMe itemeToDelete = fromMes.get(position);
								removeRefreshView(itemeToDelete.taskId);
							} catch (Exception e) {
								e.printStackTrace();
								Log.e(TAG, "Error on deleting from me task");
							}
						}
					});

			Dialog dialog = builder.create();
			dialog.show();

			return null;
		}
	};

}
