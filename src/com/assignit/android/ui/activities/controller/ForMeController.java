package com.assignit.android.ui.activities.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.Toast;

import com.assignit.android.R;
import com.assignit.android.localstorage.UserSharedPreferences;
import com.assignit.android.net.WebServiceManager;
import com.assignit.android.net.responsebean.AddEvernoteRespose;
import com.assignit.android.net.responsebean.AddReminderRespose;
import com.assignit.android.net.responsebean.TaskForMeRespose;
import com.assignit.android.net.responsebean.UpdateTaskStatusResponse;
import com.assignit.android.pojo.MenuItems;
import com.assignit.android.pojo.TaskForMe;
import com.assignit.android.ui.activities.ComposeTaskActivity_;
import com.assignit.android.ui.activities.HomeActivity;
import com.assignit.android.ui.adapter.ForMeAdapter;
import com.assignit.android.ui.views.CustomMenuView.OnMenuItemClickListener;
import com.assignit.android.ui.views.CustomProgressbar;
import com.assignit.android.utils.AppConstant;
import com.assignit.android.utils.CommonUtils;
import com.assignit.android.utils.ExpandAnimation;
import com.assignit.android.utils.StringUtils;
import com.assignit.android.utils.ValidationUtils;
import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.android.OnClientCallback;
import com.evernote.client.conn.mobile.FileData;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Resource;
import com.evernote.edam.type.ResourceAttributes;
import com.evernote.thrift.transport.TTransportException;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

import de.timroes.swipetodismiss.SwipeDismissList;
import de.timroes.swipetodismiss.SwipeDismissList.SwipeDirection;
import de.timroes.swipetodismiss.SwipeDismissList.Undoable;

/**
 * This class is represent the ForMe module.It's control the flow of UI as well
 * business logic.
 * 
 * @author Innoppl
 * 
 */
@EBean
public class ForMeController implements OnItemClickListener,
		OnMenuItemClickListener {
	// Evernote Intent actions and extras
	String ACTION_NEW_NOTE = "com.evernote.action.CREATE_NEW_NOTE";

	String ACTION_SEARCH_NOTES = "com.evernote.action.SEARCH_NOTES";

	// Evernote variable :
	private String mSelectedNotebookGuid;

	protected EvernoteSession mEvernoteSession;
	// Evernote dialog Box callback parameter
	protected final int DIALOG_PROGRESS = 101;

	@RootContext
	public HomeActivity mActivity;

	String createdDate = "";

	@ViewById
	ListView mFriendsListView;

	@ViewById
	ListView mFromMeListView;

	@ViewById
	ListView mForMeListView;

	TaskForMe mTempTask;

	private String mTempStatus;

	private String mTemptaskId;

	public List<TaskForMe> mForMes;

	long newEventId = 0;

	long prevEventId = 0;

	String reminderfriendId;

	String remindertasktId;

	public ForMeAdapter adapter;

	public Boolean mIninitialiseForMe = Boolean.TRUE;

	String LoginUserID = "";

	private final List<View> listOfAnomaitedView = new ArrayList<View>();

	/**
	 * This callback method that is responsible for initialising views.
	 */
	@AfterViews
	public void viewInitialise() {

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		mFriendsListView.setVisibility(View.GONE);
		mFromMeListView.setVisibility(View.GONE);
		mForMeListView.setVisibility(View.VISIBLE);

		mForMes = new ArrayList<TaskForMe>();
		adapter = new ForMeAdapter(mActivity, mForMes, this);

		LoginUserID = UserSharedPreferences.getInstance(mActivity).getString(
				AppConstant.USER_ID);
		mForMeListView.setOnItemClickListener(this);
		mForMeListView.setAdapter(adapter);

		SwipeDismissList swipeList = new SwipeDismissList(mForMeListView,
				callback, SwipeDismissList.UndoMode.SINGLE_UNDO);
		swipeList.setSwipeDirection(SwipeDirection.END);

	}

	/**
	 * Open forme screen.
	 */
	public void openForMeListScreen() {
		adapter.getFilter().filter("");
		// adapter.getView(position, convertView, viewGroup)
		if (mFriendsListView != null && mFromMeListView != null
				&& mForMeListView != null) {
			mFriendsListView.setVisibility(View.GONE);
			mFromMeListView.setVisibility(View.GONE);
			mForMeListView.setVisibility(View.VISIBLE);
		}
		if (CommonUtils.isNetworkAvailable(mActivity)) {
			if (mIninitialiseForMe
					|| UserSharedPreferences.getInstance(mActivity).getBoolean(
							UserSharedPreferences.KEY_FORME_STATUS)) {
				CustomProgressbar.showProgressBar(mActivity, true, mActivity
						.getString(R.string.loading_for_me_task_message));
				doApiCallTaskListForMe();
				mIninitialiseForMe = Boolean.FALSE;
				UserSharedPreferences.getInstance(mActivity).putBoolean(
						UserSharedPreferences.KEY_FORME_STATUS, Boolean.FALSE);
			}
		} else {
			CommonUtils.showErrorToast(mActivity,
					mActivity.getResources().getString(R.string.no_internate_connetion));
		}
	}

	/**
	 * api call to server for task list for me
	 */
	@Background
	public void doApiCallTaskListForMe() {

		TaskForMeRespose response = null;

		response = WebServiceManager.getInstance(mActivity).getForMeTask(
				LoginUserID);

		updateUI(response);

	}

	/**
	 * Update UI with list of task me by server response.
	 * 
	 * @param response
	 */
	@UiThread
	public void updateUI(TaskForMeRespose response) {
		if (response != null) {
			if (ValidationUtils.isSuccessResponse(response)) {
				Collections.sort(response.tasks, new Comparator<TaskForMe>() {
					@Override
					public int compare(TaskForMe f1, TaskForMe f2) {
						String first = f1.Modified;
						String second = f2.Modified;
						return second.compareToIgnoreCase(first);
					}
				});

				if (adapter.mMenuView != null) {
					adapter.mMenuView.dismisView();
				}

				adapter.updateAdapter(response.tasks);
				
				
				if(response.tasks.size() < 1){
					if(mActivity.getSelectedTab() == R.id.tabBtnForMe)
					CommonUtils.showDialogOnEmptyList(mActivity);
				}
				
				for (View v : listOfAnomaitedView) {
					LayoutParams params = (LayoutParams) v.getLayoutParams();
					params.bottomMargin = -CommonUtils.convertToDp(mActivity,
							123);
				}
				listOfAnomaitedView.clear();

				if (StringUtils.isNotBlank(mActivity.etSearchBox.getText()
						.toString())) {
					adapter.getFilter().filter(
							mActivity.etSearchBox.getText().toString());
				}

			}
		} else {
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
	 * This is calback method when list item is clicked.
	 */
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long arg3) {

		final TaskForMe taskForMe = (TaskForMe) adapterView
				.getItemAtPosition(position);

		if (StringUtils.isNotBlank(taskForMe.Image)) {
			taskForMe.mSeletedTaskId = taskForMe.taskId;
			taskForMe.position = position;

			if (!taskForMe.mState) {
				taskForMe.mState = Boolean.TRUE;
			} else {
				taskForMe.mState = Boolean.FALSE;
			}
		} else {

			taskForMe.mSeletedTaskId = AppConstant.DUMMI_TASK_ID;
			taskForMe.position = position;
			taskForMe.mState = Boolean.FALSE;

		}
		LinearLayout photoView = (LinearLayout) view
				.findViewById(R.id.mForMePhotoLinearLayout);
		View descriptionView = view.findViewById(R.id.mForMeTaskNameView);
		ExpandAnimation expandAni = new ExpandAnimation(mActivity, photoView,
				descriptionView, 400, taskForMe.Image);
		view.startAnimation(expandAni);

		if (taskForMe.mState) {
			listOfAnomaitedView.add(photoView);
			// taskForMe.mState = false;
		}
	}

	/**
	 * Adding event to calendar
	 * 
	 * @param title
	 * @param content
	 * @param startdate
	 * @param enddate
	 */
	private void addEventToCalendar(String title, String content,
			String startdate, String enddate) {
		Intent intent = new Intent(Intent.ACTION_EDIT);
		intent.setType("vnd.android.cursor.item/event");
		intent.putExtra(Events.TITLE, content + "-" + title);
		intent.putExtra(Events.DESCRIPTION, content);
		if (!startdate.equals("0") && startdate.length() > 1) {
			intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
					System.currentTimeMillis());
		}

		if (!enddate.equals("0") && enddate.length() > 1) {
			intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
					Long.parseLong(enddate) * 1000);
		}

		mActivity.startActivity(intent);
	
	}

	/**
	 * this method is called when user press menu button
	 */
	@Override
	public void onItemClick(final MenuItems menuItem) {

		if (CommonUtils.isNetworkAvailable(mActivity)) {

			if (menuItem.itemName.equalsIgnoreCase(mActivity
					.getString(R.string.task_menu_delete))) {

				if (StringUtils.isNotBlank(menuItem.lastStatus)
						&& (menuItem.lastStatus
								.equals(AppConstant.TASK_STATUS.COMPLETED) || menuItem.lastStatus
								.equals(AppConstant.TASK_STATUS.EXPIRED))) {
					deleteTheTask(menuItem);
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							mActivity);

					builder.setTitle(
							mActivity
									.getString(R.string.delete_task_dialog_title))
							.setMessage(
									mActivity
											.getString(R.string.delete_task_dialog_message))
							.setPositiveButton(
									mActivity.getString(R.string.button_yes),
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog, int id) {
											deleteTheTask(menuItem);
										}
									})
							.setNegativeButton(
									mActivity.getString(R.string.button_no),
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog, int id) {

										}
									});

					Dialog dialog = builder.create();
					dialog.show();
				}
			} else if (menuItem.itemName.equalsIgnoreCase(mActivity
					.getString(R.string.task_menu_accept))) {

				String taskStatusId = AppConstant.TASK_STATUS_VALUE.ACCEPTED;
				updateAndRefreshView(menuItem.taskId, taskStatusId);
				doApiCallUpdateTaskStatus(LoginUserID, menuItem.taskId,
						taskStatusId);

				CommonUtils.showSuccessToast(mActivity, mActivity
						.getString(R.string.task_accepted_success_fully));
			} else if (menuItem.itemName.equalsIgnoreCase(mActivity
					.getString(R.string.task_menu_inprogress))) {

				String taskStatusId = AppConstant.TASK_STATUS_VALUE.IN_PROGRESS;
				updateAndRefreshView(menuItem.taskId, taskStatusId);
				doApiCallUpdateTaskStatus(LoginUserID, menuItem.taskId,
						taskStatusId);
				CommonUtils.showSuccessToast(mActivity, mActivity
						.getString(R.string.task_inprogress_success_fully));

			} else if (menuItem.itemName.equalsIgnoreCase(mActivity
					.getString(R.string.task_menu_complete))) {

				String taskStatusId = AppConstant.TASK_STATUS_VALUE.COMPLETED;

				updateAndRefreshView(menuItem.taskId, taskStatusId);
				doApiCallUpdateTaskStatus(LoginUserID, menuItem.taskId,
						taskStatusId);
				// added to refresh list view to show date 
				doApiCallTaskListForMe();
				CommonUtils.showSuccessToast(mActivity, mActivity
						.getString(R.string.task_completed_success_fully));

			} else if (menuItem.itemName.equalsIgnoreCase(mActivity
					.getString(R.string.task_menu_reject))) {

				String taskStatusId = AppConstant.TASK_STATUS_VALUE.REJECTED;
				updateAndRefreshView(menuItem.taskId, taskStatusId);
				doApiCallUpdateTaskStatus(LoginUserID, menuItem.taskId,
						taskStatusId);
				CommonUtils.showSuccessToast(mActivity, mActivity
						.getString(R.string.task_rejected_success_fully));
			} else if (menuItem.itemName.equalsIgnoreCase(mActivity
					.getString(R.string.task_menu_edit))) {

				Intent intent = new Intent(mActivity,
						ComposeTaskActivity_.class);
				intent.putExtra(AppConstant.COMPOSE_TASK.USER_ID,
						menuItem.friendUserId);
				intent.putExtra(AppConstant.COMPOSE_TASK.FRIEND_USER_NAME,
						menuItem.friendUserName);
				intent.putExtra(AppConstant.COMPOSE_TASK.TASK_DESCRIPTION,
						menuItem.taskDescription);
				intent.putExtra(AppConstant.COMPOSE_TASK.TASK_DUEDATE_STATUS,
						menuItem.taskStatus);
				intent.putExtra(AppConstant.COMPOSE_TASK.TASK_DUE_ENDDATE,
						menuItem.taskStartDueDate);
				intent.putExtra(AppConstant.COMPOSE_TASK.EDIT_ENABLE,
						Boolean.TRUE);
				intent.putExtra(AppConstant.COMPOSE_TASK.TASK_ID,
						menuItem.taskId);
				intent.putExtra(AppConstant.COMPOSE_TASK.TASK_IMGAGE_URI,
						menuItem.imageURI);
				intent.putExtra(AppConstant.COMPOSE_TASK.TASK_TYPE,
						menuItem.taskType);

				MenuItems menuItem2 = new MenuItems();
				menuItem2.frindsList = mActivity.mFriendListController.FinalFriendList;

				Bundle bundle = new Bundle();
				bundle.putSerializable(
						AppConstant.COMPOSE_TASK.MENU_ITEM_OBJECT, menuItem2);
				intent.putExtras(bundle);

				mActivity.startActivityForResult(intent,
						HomeActivity.UPDATED_FORME_TASK_REQUEST);
				// calender event
			} else if (menuItem.itemName.equalsIgnoreCase(mActivity
					.getString(R.string.task_menu_add_reminder))) {

				if (StringUtils.isNotBlank(menuItem.taskStatus)
						&& !menuItem.taskStatus.equals("0")) {

					// newEventId = getNewEventId();

					addEventToCalendar(menuItem.friendUserName,
							menuItem.taskDescription,
							menuItem.taskStartDueDate, menuItem.taskEndDueDate);

					/*
					 * String desc =
					 * CommonUtils.GetEventDetails(Long.toString(newEventId),
					 * mActivity); Log.e("desc", ""+desc);
					 * if(StringUtils.isNotBlank
					 * (desc)&&desc.equals(menuItem.taskDescription)){
					 * doApiCallReminderStatus
					 * (menuItem.friendUserId,menuItem.taskId,
					 * AppConstant.REMINDERSTATUS.SET_REMINDER,
					 * Long.toString(newEventId));
					 * }else{CommonUtils.hideSoftKeyboard(mActivity);}
					 */

				}
			} else if (menuItem.itemName.equalsIgnoreCase(mActivity
					.getString(R.string.task_menu_edit_reminder))) {

				if (StringUtils.isNotBlank(menuItem.taskStatus)
						&& !menuItem.taskStatus.equals("0")) {

					String eventId = menuItem.eventId;

					if (StringUtils.isNotBlank(eventId)) {
						CommonUtils.editCalendarEvent(mActivity,
								Long.parseLong(eventId),
								menuItem.friendUserName,
								menuItem.taskDescription,
								menuItem.taskStartDueDate,
								menuItem.taskEndDueDate);
					}
				}
			} // evernote click action
			else if (menuItem.itemName.equalsIgnoreCase(mActivity
					.getString(R.string.task_menu_evernote))
					|| menuItem.itemName.equalsIgnoreCase(mActivity
							.getString(R.string.task_menu_go_to_evernote))) {

				if (StringUtils.isNotBlank(menuItem.friendUserId)
						&& StringUtils.isNotBlank(menuItem.taskId)
						&& StringUtils.isNotBlank(menuItem.taskStatus)
						&& !menuItem.taskStatus.equals("0")) {
					if (menuItem.evernoteSatus.equals("0")) {

						mEvernoteSession = EvernoteSession.getInstance(
								mActivity,
								mActivity.getString(R.string.consumer_key),
								mActivity.getString(R.string.consumer_secret),
								AppConstant.EVERNOTE_SERVICE);

						if (mEvernoteSession != null
								&& !mEvernoteSession.isLoggedIn()) {
							mEvernoteSession.authenticate(mActivity);
						}

						if (StringUtils.isNotBlank(menuItem.imageURI)) {
							CustomProgressbar.showProgressBar(
									mActivity,
									false,
									mActivity.getResources().getString(
											R.string.save_note_image));
							saveImage(menuItem.friendUserId, menuItem.taskId,
									menuItem.imageURI, menuItem.friendUserName,
									menuItem.taskDescription);
						} else {
							CustomProgressbar.showProgressBar(
									mActivity,
									false,
									mActivity.getResources().getString(
											R.string.save_note));
							saveNote(menuItem.friendUserId, menuItem.taskId,
									menuItem.friendUserName,
									menuItem.taskDescription);
						}

					} else if (menuItem.evernoteSatus.equals("1")) {

						boolean installed = CommonUtils.isAppInstalled(
								mActivity, "com.evernote");

						if (installed) {
							viewNote(menuItem.friendUserName);
						} else {
							mActivity.startActivity(new Intent(
									Intent.ACTION_VIEW, Uri.parse(mActivity
											.getResources().getString(
													R.string.go_to_evernote))));
						}
					}

				}
			}

		} else {
			CommonUtils.showErrorToast(mActivity,
					mActivity.getResources().getString(R.string.no_internate_connetion));
		}

	}

	public long getNewEventId() {
		ContentResolver cr = mActivity.getContentResolver();
		Uri cal_uri = Uri.parse("content://com.android.calendar/events");
		Uri local_uri = cal_uri;
		if (cal_uri == null) {
			local_uri = Uri.parse(CalendarContract.CONTENT_URI + "events");
		}
		Cursor cursor = cr.query(local_uri,
				new String[] { "MAX(_id) as max_id" }, null, null, "_id");
		cursor.moveToFirst();
		long max_val = cursor.getLong(cursor.getColumnIndex("max_id"));
		return max_val + 1;
	}

	// Evernote intent
	public void newNote() {
		Intent intent = new Intent();
		intent.setAction(ACTION_NEW_NOTE);
		try {
			mActivity.startActivity(intent);
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(mActivity, R.string.err_activity_not_found,
					Toast.LENGTH_SHORT).show();
		}
	}

	// Evernote intent
	public void viewNote(String title) {
		Intent intent = new Intent();
		intent.setAction(ACTION_SEARCH_NOTES);
		Log.e("evernote intend", "" + intent);
		try {
			mActivity.startActivity(intent);
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(mActivity, R.string.err_activity_not_found,
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * save image to local for evernote images
	 * 
	 * @param userId
	 * @param taskId
	 * @param imageUrl
	 * @param userName
	 * @param description
	 */
	@Background
	public void saveImage(String userId, String taskId, String imageUrl,
			String userName, String description) {
		if (StringUtils.isNotBlank(imageUrl)) {
			FileOutputStream fos = null;
			InputStream inputStream = null;
			try {
				URL url = new URL(imageUrl);

				File sdCard = Environment.getExternalStorageDirectory();
				File myDir = new File(sdCard.getAbsolutePath() + "/"
						+ AppConstant.EVERNOTE_IMAGE_NAME);
				if (!myDir.exists()) {
					myDir.mkdir();

				}

				File file = new File(myDir.toString());
				if (file.exists()) {
					file.delete();
				}
				URLConnection urlConnection = url.openConnection();

				HttpURLConnection httpConn = (HttpURLConnection) urlConnection;
				httpConn.setRequestMethod("GET");
				httpConn.connect();
				try {
					if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
						inputStream = httpConn.getInputStream();
					}

					fos = new FileOutputStream(file);
					byte[] buf = new byte[inputStream.available()];
					int byteRead;
					while (((byteRead = inputStream.read(buf)) != -1)) {
						fos.write(buf, 0, byteRead);
					}
				} finally {
					if (fos != null)
						fos.close();
					if (inputStream != null)
						inputStream.close();
				}
			} catch (IOException io) {
				mActivity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						CustomProgressbar.hideProgressBar();

					}
				});

				io.printStackTrace();
			} catch (Exception e) {
				mActivity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						CustomProgressbar.hideProgressBar();

					}
				});
				e.printStackTrace();
			}
			afterDownloadImage(userId, taskId, userName, description);
		}
	}

	/**
	 * send image to evernote with notes
	 * 
	 * @param userId
	 * @param taskId
	 * @param userName
	 * @param description
	 */
	@SuppressWarnings("deprecation")
	public void afterDownloadImage(final String userId, final String taskId,
			String userName, String description) {

		if (mEvernoteSession.isLoggedIn()) {

			mActivity.showDialog(DIALOG_PROGRESS);

			File sdCardRoot = Environment.getExternalStorageDirectory();

			File myDir = new File(sdCardRoot, AppConstant.EVERNOTE_IMAGE_NAME);

			try {

				InputStream in = new BufferedInputStream(new FileInputStream(
						myDir));

				FileData data = new FileData(EvernoteUtil.hash(in), new File(
						myDir.toString()));
				in.close();

				// Create a new Resource
				Resource resource = new Resource();
				resource.setData(data);
				resource.setMime("image/jpeg");
				ResourceAttributes attributes = new ResourceAttributes();
				attributes.setFileName(new File(myDir.toString()).getName());
				resource.setAttributes(attributes);

				// Create a new Note
				Note note = new Note();
				note.setTitle(userName);
				note.setCreated(System.currentTimeMillis());
				note.setContent(EvernoteUtil.NOTE_PREFIX + description
						+ EvernoteUtil.NOTE_SUFFIX);
				note.addToResources(resource);

				mEvernoteSession.getClientFactory().createNoteStoreClient()
						.createNote(note, new OnClientCallback<Note>() {
							@Override
							public void onSuccess(Note data) {
								CommonUtils
										.showSuccessToast(
												mActivity,
												mActivity
														.getResources()
														.getString(
																R.string.evernote_save_onsuccess));
								CustomProgressbar.hideProgressBar();
								updateAndRefreshEvernoteView(taskId, "1");
								doApiCallEvernoteStatus(userId, taskId);
								mActivity.removeDialog(DIALOG_PROGRESS);

							}

							@Override
							public void onException(Exception exception) {
								CustomProgressbar.hideProgressBar();
								CommonUtils
										.showErrorToast(
												mActivity,
												mActivity
														.getResources()
														.getString(
																R.string.evernote_save_onerror));
								mActivity.removeDialog(DIALOG_PROGRESS);
							}
						});
			} catch (Exception ex) {
				CustomProgressbar.hideProgressBar();
				mActivity.removeDialog(DIALOG_PROGRESS);
			}
		} else {
			CustomProgressbar.hideProgressBar();
		}

	}

	/**
	 * update task from server
	 * 
	 * @param userId
	 * @param taskId
	 * @param taskStatusId
	 */
	@Background
	public void doApiCallUpdateTaskStatus(String userId, String taskId,
			String taskStatusId) {

		UpdateTaskStatusResponse response = null;
		response = WebServiceManager.getInstance(mActivity).updateTaskStatus(
				userId, taskId, taskStatusId);
		updateUiOnAccept(response, taskStatusId);

	}

	/**
	 * update ui after task is updated successfully
	 * 
	 * @param response
	 * @param taskStatusId
	 */
	@UiThread
	public void updateUiOnAccept(UpdateTaskStatusResponse response,
			String taskStatusId) {
		if (response != null) {
			Log.e("statsu", "" + response.httpStatusCode);
			if (!ValidationUtils.isSuccessResponse(response)) {
				if (StringUtils.isNotBlank(mTempStatus)
						&& StringUtils.isNotBlank(mTemptaskId)) {
					updateAndRefreshView(mTemptaskId, mTempStatus);

					// adapter.notifyDataSetChanged();
					if (mTempStatus
							.equals(AppConstant.TASK_STATUS_VALUE.ACCEPTED)) {

						CommonUtils.showSuccessToast(mActivity, mActivity
								.getString(R.string.task_accepted_failed));

					} else if (mTempStatus
							.equals(AppConstant.TASK_STATUS_VALUE.IN_PROGRESS)) {

						CommonUtils.showSuccessToast(mActivity, mActivity
								.getString(R.string.task_inprogress_failed));

					} else if (mTempStatus
							.equals(AppConstant.TASK_STATUS_VALUE.COMPLETED)) {

						CommonUtils.showSuccessToast(mActivity, mActivity
								.getString(R.string.task_completed_failed));

					} else if (mTempStatus
							.equals(AppConstant.TASK_STATUS_VALUE.REJECTED)) {

						CommonUtils.showSuccessToast(mActivity, mActivity
								.getString(R.string.task_rejected_failed));

					}
				}

			}
		} else {
			if (CommonUtils.isNetworkAvailable(mActivity)) {
			CommonUtils.showErrorToast(mActivity,
					mActivity.getResources().getString(R.string.time_out_connetion));
			}else{
				CommonUtils.showErrorToast(mActivity,
						mActivity.getResources().getString(R.string.no_internate_connetion));
			}
}
	}

	/**
	 * Remove task locally
	 * 
	 * @param taskId
	 */
	public void removeAndRefreshView(String taskId) {

		List<TaskForMe> forMes = adapter.getAdapterList();

		for (TaskForMe taskForMe : forMes) {
			if (taskId.equalsIgnoreCase(taskForMe.taskId)) {
				mTempTask = taskForMe;
				forMes.remove(taskForMe);
				break;
			}
		}
		// adapter.updateAdapter(forMes);
		adapter = new ForMeAdapter(mActivity, forMes, this);
		mForMeListView.setAdapter(adapter);

	}

	/**
	 * without delete the list
	 * 
	 * @param taskId
	 */
	public void removeRefreshView(String taskId) {

		List<TaskForMe> forMes = adapter.getAdapterList();

		for (TaskForMe taskForMe : forMes) {
			if (taskId.equalsIgnoreCase(taskForMe.taskId)) {
				mTempTask = taskForMe;
				break;
			}
		}
		// adapter.updateAdapter(forMes);
		adapter = new ForMeAdapter(mActivity, forMes, this);
		mForMeListView.setAdapter(adapter);

	}

	/**
	 * update task locally
	 * 
	 * @param taskId
	 * @param taskStatusId
	 */
	private void updateAndRefreshView(String taskId, String taskStatusId) {

		List<TaskForMe> forMes = adapter.getAdapterList();
		long time = System.currentTimeMillis();
		for (TaskForMe taskForMe : forMes) {
			if (taskId.equalsIgnoreCase(taskForMe.taskId)) {
				mTemptaskId = taskForMe.taskId;
				mTempStatus = taskForMe.Status;
				taskForMe.Status = taskStatusId;
				// Only For Sorting purpose
				taskForMe.Modified = String.valueOf((time / 1000)
						+ (time / 1000));
				break;
			}
		}

		Collections.sort(forMes, new Comparator<TaskForMe>() {
			@Override
			public int compare(TaskForMe f1, TaskForMe f2) {
				String first = f1.Modified;
				String second = f2.Modified;
				return second.compareToIgnoreCase(first);
			}
		});

		adapter.updateAdapter(forMes);
	}

	/**
	 * Refresh evernote menu view
	 * 
	 * @param taskId
	 * @param taskStatusId
	 */
	private void updateAndRefreshEvernoteView(String taskId, String taskStatusId) {

		List<TaskForMe> forMes = adapter.getAdapterList();

		for (TaskForMe taskForMe : forMes) {
			if (taskId.equalsIgnoreCase(taskForMe.taskId)) {
				mTemptaskId = taskForMe.taskId;

				taskForMe.evernoteStatus = taskStatusId;

				break;
			}
		}

		adapter.updateAdapter(forMes);
	}


	/**
	 * Callback class on swipe to delete the task
	 */
	SwipeDismissList.OnDismissCallback callback = new SwipeDismissList.OnDismissCallback() {
		@Override
		public Undoable onDismiss(AbsListView listView,  final int position) {
			List<TaskForMe> forMes = adapter.getAdapterList();
			TaskForMe itemeToDelete = forMes.get(position);
			if (StringUtils.isNotBlank(itemeToDelete.Status)
					&& (itemeToDelete.Status
							.equals(AppConstant.TASK_STATUS_VALUE.COMPLETED) || itemeToDelete.Status
							.equals(AppConstant.TASK_STATUS_VALUE.EXPIRED))) {
				deleteTheTask(position);
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

				builder.setTitle(
						mActivity.getString(R.string.delete_task_dialog_title))
						.setMessage(
								mActivity
										.getString(R.string.delete_task_dialog_message))
						.setPositiveButton(
								mActivity.getString(R.string.button_yes),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										deleteTheTask(position);
									}
								})
						.setNegativeButton(
								mActivity.getString(R.string.button_no),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										List<TaskForMe> forMes = adapter
												.getAdapterList();
										try {
											final TaskForMe itemeToDelete = forMes
													.get(position);
											removeRefreshView(itemeToDelete.taskId);
										} catch (Exception e) {
											e.printStackTrace();
											Log.e("", "Error on deleting task");
										}
									}
								});

				Dialog dialog = builder.create();
				dialog.show();
			}
			return null;
		}
	};

	/**
	 * Delete the task by using position while swipe delete
	 * 
	 * @param position
	 */
	private void deleteTheTask(int position) {

		// Delete the item from your adapter
		UserSharedPreferences.getInstance(mActivity).putBoolean(
				AppConstant.ACTION_REFRESH_FRIENDS, Boolean.TRUE);
		List<TaskForMe> forMes = adapter.getAdapterList();
		try {
			final TaskForMe itemeToDelete = forMes.get(position);
			removeAndRefreshView(itemeToDelete.taskId);

			String taskStatusId = "";
			if (StringUtils.isNotBlank(itemeToDelete.laststatus)
					&& itemeToDelete.laststatus
							.equals(AppConstant.TASK_STATUS.COMPLETED)) {
				taskStatusId = AppConstant.TASK_STATUS_VALUE.COMPLETE;
			} else {
				taskStatusId = AppConstant.TASK_STATUS_VALUE.DELETED;
			}
			if (StringUtils.isNotBlank(itemeToDelete.eventId)
					&& !itemeToDelete.eventId.equals("0")) {
				CommonUtils.deleteEvent(mActivity, itemeToDelete.eventId);
			}
			doApiCallUpdateTaskStatus(LoginUserID, itemeToDelete.taskId,
					taskStatusId);

			CommonUtils.showSuccessToast(mActivity, mActivity.getString(
					R.string.task_deleted_success_fully_for_me,
					itemeToDelete.fromUser.FromUsername));
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("", "Error on deleting task");
		}

	}

	/**
	 * Delete the task by MenuItem objects while menu item click press
	 * @param menuItem
	 */
	private void deleteTheTask(MenuItems menuItem) {
		UserSharedPreferences.getInstance(mActivity).putBoolean(
				AppConstant.ACTION_REFRESH_FRIENDS, Boolean.TRUE);
		removeAndRefreshView(menuItem.taskId);

		String taskStatusId = "";

		if (menuItem.lastStatus.equals(AppConstant.TASK_STATUS.COMPLETED)) {
			taskStatusId = AppConstant.TASK_STATUS_VALUE.COMPLETE;
		} else {
			taskStatusId = AppConstant.TASK_STATUS_VALUE.DELETED;
		}
		// delete event in local calender
		if (StringUtils.isNotBlank(menuItem.eventId)
				&& !menuItem.eventId.equals("0")) {
			CommonUtils.deleteEvent(mActivity, menuItem.eventId);
		}

		doApiCallUpdateTaskStatus(LoginUserID, menuItem.taskId, taskStatusId);
		CommonUtils.showSuccessToast(mActivity, mActivity.getString(
				R.string.task_deleted_success_fully_for_me,
				menuItem.friendUserName));
	}

	/**
	 * Saves text field content as note to selected notebook, or default
	 * notebook if no notebook select
	 * 
	 * @param userId
	 * @param taskId
	 * @param username
	 * @param description
	 */
	@SuppressWarnings("deprecation")
	private void saveNote(final String userId, final String taskId,
			String username, String description) {

		if (mEvernoteSession.isLoggedIn()) {
			Note note = new Note();
			note.setTitle(username);
			note.setCreated(System.currentTimeMillis());
			note.setContent(EvernoteUtil.NOTE_PREFIX + description
					+ EvernoteUtil.NOTE_SUFFIX);

			if (!TextUtils.isEmpty(mSelectedNotebookGuid)) {
				note.setNotebookGuid(mSelectedNotebookGuid);
			}
			mActivity.showDialog(DIALOG_PROGRESS);
			try {
				mEvernoteSession.getClientFactory().createNoteStoreClient()
						.createNote(note, new OnClientCallback<Note>() {

							@Override
							public void onSuccess(Note data) {
								CommonUtils
										.showSuccessToast(
												mActivity,
												mActivity
														.getResources()
														.getString(
																R.string.evernote_save_onsuccess));
								CustomProgressbar.hideProgressBar();
								updateAndRefreshEvernoteView(taskId, "1");
								doApiCallEvernoteStatus(userId, taskId);
								mActivity.removeDialog(DIALOG_PROGRESS);
							}

							@Override
							public void onException(Exception exception) {
								CustomProgressbar.hideProgressBar();
								CommonUtils
										.showErrorToast(
												mActivity,
												mActivity
														.getResources()
														.getString(
																R.string.evernote_save_onerror));
								Log.e("SimpleNote", "Error saving note",
										exception);
								mActivity.removeDialog(DIALOG_PROGRESS);
							}
						});
			} catch (TTransportException exception) {
				CustomProgressbar.hideProgressBar();
				Log.e("SimpleNote", "Error creating notestore", exception);
				mActivity.removeDialog(DIALOG_PROGRESS);
			} catch (IllegalStateException e) {
				CustomProgressbar.hideProgressBar();
				Log.e("Illegal Exception ", "" + e);
			}
		} else {
			CustomProgressbar.hideProgressBar();
		}
	}

	/**
	 * implement the dialog
	 * 
	 * @param id
	 * @param dialog
	 */
	public void onPrepareDialog(int id, Dialog dialog) {
		// TODO Auto-generated method stub
		switch (id) {
		case DIALOG_PROGRESS:
			((ProgressDialog) dialog).setIndeterminate(true);
			dialog.setCancelable(false);
			((ProgressDialog) dialog).setMessage(mActivity
					.getString(R.string.esdk__loading));
		}

	}

	/**
	 * create evernote dialog
	 * 
	 * @param id
	 * @return
	 */
	public Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub

		switch (id) {
		case DIALOG_PROGRESS:
			return new ProgressDialog(mActivity);
		}
		return null;

	}

	/**
	 * Send reminder status to the server
	 * 
	 * @param userId
	 * @param taskId
	 * @param reminderStatus
	 * @param eventId
	 */
	@Background
	public void doApiCallReminderStatus(String userId, String taskId,
			String reminderStatus, String eventId) {

		AddReminderRespose response = null;

		response = WebServiceManager.getInstance(mActivity).addReminder(userId,
				taskId, reminderStatus, eventId);
		if (response != null) {

			if (ValidationUtils.isSuccessResponse(response)) {
				doApiCallTaskListForMe();
			}

		}
	}

	/**
	 * Send evernote status to the server
	 * 
	 * @param userId
	 * @param taskId
	 */
	@Background
	public void doApiCallEvernoteStatus(String userId, String taskId) {

		AddEvernoteRespose response = null;

		response = WebServiceManager.getInstance(mActivity).addEvernote(userId,
				taskId);
		if (response != null) {
			Log.e("event", "" + response.httpStatusCode);
		}

	}

}
