package com.assignit.android.ui.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
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
import com.assignit.android.net.responsebean.ExistingTaskRespose;
import com.assignit.android.net.responsebean.SendReminderResponse;
import com.assignit.android.pojo.MenuItems;
import com.assignit.android.pojo.TaskFromMe;
import com.assignit.android.ui.adapter.ExistingTaskAdapter;
import com.assignit.android.ui.views.CustomEditText;
import com.assignit.android.ui.views.CustomMenuView.OnMenuItemClickListener;
import com.assignit.android.ui.views.CustomProgressbar;
import com.assignit.android.ui.views.CustomTextView;
import com.assignit.android.ui.views.CustomViewConstants;
import com.assignit.android.utils.AppConstant;
import com.assignit.android.utils.CommonUtils;
import com.assignit.android.utils.ExpandAnimation;
import com.assignit.android.utils.MobileContactsUtils;
import com.assignit.android.utils.StringUtils;
import com.assignit.android.utils.ValidationUtils;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import de.timroes.swipetodismiss.SwipeDismissList;
import de.timroes.swipetodismiss.SwipeDismissList.SwipeDirection;
import de.timroes.swipetodismiss.SwipeDismissList.Undoable;

/**
 * View/manage task[s] to assign a single user/friend.
 * 
 * @author Innoppl
 * 
 */
@EActivity(R.layout.activity_existing_task)
public class ExistingTaskActivity extends BaseActivity implements
		OnMenuItemClickListener, OnItemClickListener {

	private final String TAG = HomeActivity.class.getSimpleName();

	@ViewById
	ListView mExitingTaskView;

	@ViewById
	CustomTextView mFriendsNameView;

	@ViewById
	public CustomEditText etSearchBox;

	public List<TaskFromMe> mExitingTask;

	@ViewById
	ImageView composeTaskPprofilePicImageView;

	// to restrict to refresh if loading occurs on first time
	boolean isLoaded = Boolean.TRUE;

	String LoginUserId = "";

	public ExistingTaskAdapter adapter;

	private TaskFromMe mTempTask;

	ImageLoader imageLoader;

	// Result code callback parameter on activity result
	public static final int UPDATED_TASK_REQUEST = 501;

	DisplayImageOptions options;

	private final List<View> listOfAnomaitedView = new ArrayList<View>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	/**
	 * This is callback method it's call when view initialized.
	 */
	@AfterViews
	public void viewInitialise() {
		configImageLoader();
		showSerchView(false);
		showBackArrow(true);
		showProfilePic(true);
		showHomeScreenFriendsName(Boolean.TRUE);
		mExitingTask = new ArrayList<TaskFromMe>();
		adapter = new ExistingTaskAdapter(this, mExitingTask);

		imageLoader = ImageLoader.getInstance();
		options = SplashActivity.getDisplayImageOptionsWithUserImage();
		View headerFooter = getLayoutInflater().inflate(
				R.layout.header_footer_view, null);
		mExitingTaskView.addHeaderView(headerFooter);
		mExitingTaskView.addFooterView(headerFooter);
		mExitingTaskView.setAdapter(adapter);
		mExitingTaskView.setOnItemClickListener(this);
		LoginUserId = UserSharedPreferences.getInstance(this).getString(
				AppConstant.USER_ID);

		MobileContactsUtils contactUtils = new MobileContactsUtils(this);
		Bitmap bitmap = contactUtils.openPhoto(Long.parseLong(getIntent()
				.getExtras().getString(AppConstant.COMPOSE_TASK.CONTACT_ID)));
		if (bitmap != null) {
			composeTaskPprofilePicImageView.setImageBitmap(bitmap);
		} else {
			composeTaskPprofilePicImageView
					.setImageResource(R.drawable.compose_task_profile_pic);
		}

		if (CommonUtils.isNetworkAvailable(this)) {
			CustomProgressbar.showProgressBar(this, true,
					getString(R.string.loading_exist_task_message));
			doApiCallTaskListFromMe();
			isLoaded = Boolean.FALSE;
		} else {
			CommonUtils.showErrorToast(this,
					getResources().getString(R.string.no_internate_connetion));
		}

		String text = "<font color=#333434>"
				+ getString(R.string.existing_task_facebook_name)
				+ "</font> <font color=#222222>"
				+ "<b>"
				+ getIntent().getExtras().getString(
						AppConstant.COMPOSE_TASK.USER_NAME) + "</b>"
				+ "</font>";
		mFriendsNameView.setText(Html.fromHtml(text));
		SwipeDismissList swipeList = new SwipeDismissList(mExitingTaskView,
				callback, SwipeDismissList.UndoMode.SINGLE_UNDO);
		swipeList.setSwipeDirection(SwipeDirection.END);

		// onScrollListView();
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

	/**
	 * Get list of task from server by logged in user.
	 * 
	 */
	@Background
	public void doApiCallTaskListFromMe() {
		ExistingTaskRespose response = null;

		response = WebServiceManager.getInstance(this).getExitingTask(
				LoginUserId,
				getIntent().getExtras().getString(
						AppConstant.COMPOSE_TASK.USER_ID));

		updateUI(response);
	}

	/**
	 * Update UI with list of task me by server response.
	 * 
	 * @param response
	 */
	@UiThread
	public void updateUI(ExistingTaskRespose response) {
		if (response != null) {
			Log.d(TAG, " getFriendDetails Status: " + response.httpStatusCode);
			if (ValidationUtils.isSuccessResponse(response)) {
				Collections.sort(response.tasks, new Comparator<TaskFromMe>() {
					@Override
					public int compare(TaskFromMe f1, TaskFromMe f2) {
						String first = f1.Modified;
						String second = f2.Modified;
						return second.compareToIgnoreCase(first);
					}
				});

				if (response.tasks.size() > 0) {
					adapter.updateAdapter(response.tasks);
					mExitingTask = response.tasks;
					for (View v : listOfAnomaitedView) {
						LayoutParams params = (LayoutParams) v
								.getLayoutParams();
						params.bottomMargin = -CommonUtils.convertToDp(this,
								123);
					}
					listOfAnomaitedView.clear();

				} else {
					CommonUtils.showDialogOnEmptyList(this);
				}
			}
			
		}else{
			if (CommonUtils.isNetworkAvailable(this)) {
			CommonUtils.showErrorToast(this,
					getResources().getString(R.string.time_out_connetion));
			}else{
				CommonUtils.showErrorToast(this,
						getResources().getString(R.string.no_internate_connetion));
			}
}
		CustomProgressbar.hideProgressBar();
	}

	/**
	 * This is call-back method to get called when user has clicked on menu
	 * item.
	 * 
	 * @param menuItem
	 */
	@Override
	public void onItemClick(final MenuItems menuItem) {
		if (CommonUtils.isNetworkAvailable(this)) {
			if (menuItem.itemName.equalsIgnoreCase(this
					.getString(R.string.task_menu_delete))) {

				AlertDialog.Builder builder = new AlertDialog.Builder(this);

				builder.setTitle(
						this.getString(R.string.delete_task_dialog_title))
						.setMessage(
								this.getString(R.string.delete_task_dialog_message))
						.setPositiveButton(this.getString(R.string.button_yes),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										UserSharedPreferences
												.getInstance(
														ExistingTaskActivity.this)
												.putBoolean(
														AppConstant.ACTION_REFRESH_FRIENDS,
														Boolean.TRUE);
										removeAndRefreshView(menuItem.taskId);
										doApiCallDeleteTask(menuItem.taskId);
										CommonUtils
												.showSuccessToast(
														ExistingTaskActivity.this,
														getString(
																R.string.task_deleted_success_fully,
																menuItem.friendUserName));

									}
								})
						.setNegativeButton(
								ExistingTaskActivity.this
										.getString(R.string.button_no),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int id) {

									}
								});

				Dialog dialog = builder.create();
				dialog.show();

			} else if (menuItem.itemName
					.equalsIgnoreCase(getString(R.string.task_menu_edit))) {

				Intent intent = new Intent(ExistingTaskActivity.this,ComposeTaskActivity_.class);
				intent.putExtra(AppConstant.COMPOSE_TASK.USER_ID,menuItem.friendUserId);
				intent.putExtra(AppConstant.COMPOSE_TASK.FRIEND_USER_NAME,menuItem.friendUserName);
				intent.putExtra(AppConstant.COMPOSE_TASK.TASK_DESCRIPTION,menuItem.taskDescription);
				intent.putExtra(AppConstant.COMPOSE_TASK.TASK_DUEDATE_STATUS,menuItem.taskStatus);
				intent.putExtra(AppConstant.COMPOSE_TASK.TASK_DUE_STARTDATE,menuItem.taskStartDueDate);
				intent.putExtra(AppConstant.COMPOSE_TASK.TASK_DUE_ENDDATE,menuItem.taskEndDueDate);
				intent.putExtra(AppConstant.COMPOSE_TASK.TASK_DUEDATE_REPEATEND,menuItem.taskEndRepeatDueDate);
				intent.putExtra(AppConstant.COMPOSE_TASK.TASK_DUEDATE_REPEAT,menuItem.taskRepeatDueDate);
				intent.putExtra(AppConstant.COMPOSE_TASK.TASK_IS_REPEATEND,menuItem.isRepeated);
				intent.putExtra(AppConstant.COMPOSE_TASK.EDIT_ENABLE,Boolean.TRUE);
				intent.putExtra(AppConstant.COMPOSE_TASK.TASK_ID,menuItem.taskId);
				intent.putExtra(AppConstant.COMPOSE_TASK.TASK_IMGAGE_URI,menuItem.imageURI);
				intent.putExtra(AppConstant.COMPOSE_TASK.TASK_TYPE,menuItem.taskType);

				MenuItems menuItem2 = (MenuItems) getIntent().getSerializableExtra(AppConstant.COMPOSE_TASK.MENU_ITEM_OBJECT);

				Bundle bundle = new Bundle();
				bundle.putSerializable(AppConstant.COMPOSE_TASK.MENU_ITEM_OBJECT, menuItem2);
				intent.putExtras(bundle);

				ExistingTaskActivity.this.startActivityForResult(intent,HomeActivity.UPDATED_TASK_REQUEST);

			} else if (menuItem.itemName
					.equalsIgnoreCase(getString(R.string.task_menu_reminder))) {

				showPopUp(menuItem.taskId, menuItem.friendUserName);

			}
		} else {
			CommonUtils.showErrorToast(ExistingTaskActivity.this,
					getString(R.string.no_internate_connetion));
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
		response = WebServiceManager.getInstance(this).deleteTask(LoginUserId,
				taskId);
		updateUIONDelete(response);
	}

	/**
	 * Update View if deletion has successfully completed.
	 * 
	 * @param response
	 */
	@UiThread
	public void updateUIONDelete(DeleteTaskResponse response) {
		if (response != null) {
			Log.e(TAG + " delete task", "" + response.httpStatusCode);
			if (!ValidationUtils.isSuccessResponse(response)) {
				if (mTempTask != null) {
					adapter.getAdapterList().add(mTempTask);
					adapter.notifyDataSetChanged();
				}
				CommonUtils.showErrorToast(this,
						getString(R.string.task_deleted_failed));
			}
		}
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
		adapter = new ExistingTaskAdapter(this, fromMes);
		mExitingTaskView.setAdapter(adapter);
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
		adapter = new ExistingTaskAdapter(this, fromMes);
		mExitingTaskView.setAdapter(adapter);
	}

	/**
	 * Send Reminder to users.
	 * 
	 * @param taskId
	 */
	@Background
	public void doApiCallSendReminderTask(String taskId) {
		SendReminderResponse response = null;

		response = WebServiceManager.getInstance(this).remindTask(LoginUserId,
				taskId);

		showToastMessage(response);
	}

	/**
	 * Show Toast message.
	 * 
	 * @param response
	 */
	@UiThread
	public void showToastMessage(SendReminderResponse response) {
		if (response != null) {
			Log.d(TAG, " Task reminder Status: " + response.httpStatusCode);
			if (ValidationUtils.isSuccessResponse(response)) {

				CommonUtils.showSuccessToast(this, this
						.getString(R.string.task_send_reminder_sucess_fully));

			} else {
				CommonUtils.showErrorToast(this,
						this.getString(R.string.task_send_reminder_already));
			}
		}
	}

	/**
	 * Shows popup on send reminder menu select
	 * 
	 * @param taskId
	 * @param friendUserName
	 */
	private void showPopUp(final String taskId, String friendUserName) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(R.string.task_reminder_title)
				.setMessage(R.string.task_reminder_message)
				.setPositiveButton(getString(R.string.button_yes),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								doApiCallSendReminderTask(taskId);
							}
						})
				.setNegativeButton(getString(R.string.button_no),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {

							}
						});
		Dialog dialog = builder.create();
		dialog.show();
	}

	/**
	 * Method calls refresh button select
	 */
	@Click
	public void btnBaseHeaderButtonRefresh() {
		if (CommonUtils.isNetworkAvailable(this)) {
			CustomProgressbar.showProgressBar(this, true,
					getString(R.string.refresh_exist_list_message));
			doApiCallTaskListFromMe();
		} else {
			CommonUtils.showErrorToast(this,
					getResources().getString(R.string.no_internate_connetion));
		}
	}

	/**
	 * This Methos is call when user press Back Arrow
	 */
	@Click
	public void baseHeaderBackArrow() {
		finish();
	}

	/*
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == UPDATED_TASK_REQUEST && resultCode == RESULT_OK) {
			CommonUtils.showSuccessToast(this,
					getString(R.string.task_update_success_fully));
			if (CommonUtils.isNetworkAvailable(this)) {
				CustomProgressbar.showProgressBar(this, true,
						getString(R.string.refresh_fromMe_list_message));
				doApiCallTaskListFromMe();
			} else {
				CommonUtils.showErrorToast(
						this,
						getResources().getString(
								R.string.no_internate_connetion));
			}
		}

	}

	/**
	 * On swipe to delete the task
	 */
	SwipeDismissList.OnDismissCallback callback = new SwipeDismissList.OnDismissCallback() {
		@Override
		public Undoable onDismiss(AbsListView listView, int position) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					ExistingTaskActivity.this);

			List<TaskFromMe> existingTask = adapter.getAdapterList();
			final TaskFromMe itemeToDelete = existingTask.get(position - 1);

			builder.setTitle(getString(R.string.delete_task_dialog_title))
					.setMessage(getString(R.string.delete_task_dialog_message))
					.setPositiveButton(getString(R.string.button_yes),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									UserSharedPreferences
											.getInstance(
													ExistingTaskActivity.this)
											.putBoolean(
													AppConstant.ACTION_REFRESH_FRIENDS,
													Boolean.TRUE);
									removeAndRefreshView(itemeToDelete.taskId);
									doApiCallDeleteTask(itemeToDelete.taskId);
									CommonUtils
											.showSuccessToast(
													ExistingTaskActivity.this,
													getString(
															R.string.task_deleted_success_fully,
															itemeToDelete.ToUser.ToUsername));
								}
							})
					.setNegativeButton(getString(R.string.button_no),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									removeRefreshView(itemeToDelete.taskId);
								}
							});

			Dialog dialog = builder.create();
			dialog.show();
			return null;
		}
	};

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position,
			long arg3) {
		final TaskFromMe taskFromMe = (TaskFromMe) adapter
				.getItemAtPosition(position);

		if (StringUtils.isNotBlank(taskFromMe.Image)) {
			taskFromMe.mSeletedTaskId = taskFromMe.taskId;
			taskFromMe.position = position - 1;

			if (!taskFromMe.mState) {
				taskFromMe.mState = Boolean.TRUE;
			} else {
				taskFromMe.mState = Boolean.FALSE;
			}
		} else {
			taskFromMe.mSeletedTaskId = "10000";
			taskFromMe.position = position - 1;
			taskFromMe.mState = Boolean.FALSE;
		}

		LinearLayout photoView = (LinearLayout) view
				.findViewById(R.id.mPhotoLinearLayout);
		View descriptionView = view.findViewById(R.id.mTaskNameView);
		ExpandAnimation expandAni = new ExpandAnimation(this, photoView,
				descriptionView, 400, taskFromMe.Image);
		view.startAnimation(expandAni);
		if (taskFromMe.mState) {
			listOfAnomaitedView.add(photoView);

		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		isLoaded = Boolean.TRUE;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (isLoaded) {
			if (UserSharedPreferences.getInstance(this).getBoolean(
					AppConstant.ACTION_REFRESH_FRIENDS)) {
				UserSharedPreferences.getInstance(this).putBoolean(
						AppConstant.ACTION_REFRESH_FRIENDS, Boolean.FALSE);
				UserSharedPreferences.getInstance(this).putBoolean(
						UserSharedPreferences.KEY_FORME_STATUS, Boolean.TRUE);
				if (CommonUtils.isNetworkAvailable(this)) {
					CustomProgressbar.showProgressBar(this, true,
							getString(R.string.refresh_friend_list_message));

					doApiCallTaskListFromMe();
				} else {
					CommonUtils.showErrorToast(
							this,
							getResources().getString(
									R.string.no_internate_connetion));
				}
			}
		}
	}

}
