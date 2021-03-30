package com.assignit.android.ui.activities;

import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.assignit.android.R;
import com.assignit.android.localstorage.UserSharedPreferences;
import com.assignit.android.net.WebServiceManager;
import com.assignit.android.net.responsebean.AssignTaskResponse;
import com.assignit.android.net.responsebean.UpdateTaskResponse;
import com.assignit.android.pojo.MenuItems;
import com.assignit.android.ui.adapter.CustomSpinnerAdapter;
import com.assignit.android.ui.fragments.DatePickerFragment;
import com.assignit.android.ui.fragments.DatePickerFragment.DatePickerListener;
import com.assignit.android.ui.fragments.TimePickerFragment;
import com.assignit.android.ui.fragments.TimePickerFragment.TimePickedListener;
import com.assignit.android.ui.views.CommonAlertDialog;
import com.assignit.android.ui.views.CustomCheckBox;
import com.assignit.android.ui.views.CustomEditText;
import com.assignit.android.ui.views.CustomMenuView;
import com.assignit.android.ui.views.CustomProgressbar;
import com.assignit.android.ui.views.CustomTextView;
import com.assignit.android.ui.views.EndRepeatAlertDialog;
import com.assignit.android.ui.views.EndRepeatAlertDialog.EndDeuDateListener;
import com.assignit.android.ui.views.RepeatAlertDialog;
import com.assignit.android.ui.views.RepeatAlertDialog.DeuDateListener;
import com.assignit.android.utils.AppConstant;
import com.assignit.android.utils.CalendarUtils;
import com.assignit.android.utils.CommonUtils;
import com.assignit.android.utils.MobileContactsUtils;
import com.assignit.android.utils.StringUtils;
import com.assignit.android.utils.ValidationUtils;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.StringArrayRes;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * This class is used for composing the task as well updating the task with
 * multiple case coverages.
 * 
 * @author Innoppl
 * 
 */


@EActivity(R.layout.activity_compose_task)
public class ComposeTaskActivity extends BaseActivity implements
		TimePickedListener, DatePickerListener, DeuDateListener,
		EndDeuDateListener {

	@ViewById
	public CustomCheckBox cbNoDueDate, cbDoItToday, cbDoItAnyTime;

	String tastType = "0";

	public CustomMenuView mMenuView;

	boolean isShown = Boolean.FALSE;

	boolean isEdit = Boolean.FALSE;

	@ViewById
	public CustomCheckBox cbSetDueDate;

	@ViewById
	CustomEditText taskDescription;

	@ViewById
	ImageView selectImageView, removeImageView;

	@ViewById
	ImageView composeTaskPprofilePicImageView;

	@ViewById
	CustomTextView compostaskSelectImageTextView;

	@ViewById
	LinearLayout llmaincompose;

	@ViewById
	RelativeLayout rlmainComposeTask;

	@ViewById
	LinearLayout llReapetDeuDate;

	@ViewById
	LinearLayout llEndReapetDeuDate;

	@ViewById
	LinearLayout llSetdeudate;

	boolean isChanged = Boolean.TRUE;

	@ViewById
	TextView tvStartDatePicker;

	@ViewById
	TextView tvDeudateRepeat;

	@ViewById
	TextView tvDeudateEndRepeat;

	String selectedItem;

	@ViewById
	TextView tvStartTimePicker;

	@ViewById
	TextView tvEndDatePicker;

	@ViewById
	TextView tvEndTimePicker;

	RepeatAlertDialog commonAlertDialog;

	EndRepeatAlertDialog endAlertDialog;

	@StringArrayRes
	String[] repeat_item;
	
	private ImageLoader imageLoader;
	
	DisplayImageOptions options;
	
	public Spinner spinnerList;

	public MenuItems menuItem;

	public boolean isDueDateSet = false;

	int height, width, descHeight, descWidth;

	String toUserId, userName;

	String LoginUserId = "";

	// callback for gallery picker
	private static final int GALARY_PHOTO = 100;

	// callback for camera picture taken
	private static final int CAMERA_PHOTO = 0;

	CommonAlertDialog alertDialog;

	String task, dueDate, dueStatus, description, imageuri;

	boolean isDisplayCancelPopup = false;

	private int year;
	private int month;
	private int day;

	String imageDelete = "";

	MobileContactsUtils contactUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	/**
	 * This is api callback method that is used for initializing the views.
	 */
	@AfterViews
	public void afterInitView() {
		showSerchView(false);
		showBackArrow(true);
		showProfilePic(true);
		showFriendName(true);
		showDeleteButton(true);
		showNextButton(true);
		showRefereshButton(Boolean.FALSE);
		CommonUtils.hideSoftKeyboard(this);
		contactUtils = new MobileContactsUtils(this);
		alertDialog = new CommonAlertDialog(this);
		commonAlertDialog = new RepeatAlertDialog(this);
		endAlertDialog = new EndRepeatAlertDialog(this);
		spinnerList = (Spinner) findViewById(R.id.base_header_spinner_select_frd);
		imageLoader = ImageLoader.getInstance();
		options = SplashActivity.getDisplayImageOptionsWithUserImage();

		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);

		LoginUserId = UserSharedPreferences.getInstance(this).getString(
				AppConstant.USER_ID);

		tvStartTimePicker.setText(getCurrentTimeStamp().toString());
		tvEndTimePicker.setText(getEndTimeStamp().toString());
		tvStartDatePicker.setText(new StringBuilder()
				.append(CalendarUtils.getMonthName(month)).append(" ")
				.append(day).append(", ").append(year).append(" "));
		tvEndDatePicker.setText(new StringBuilder()
				.append(CalendarUtils.getMonthName(month)).append(" ")
				.append(day).append(", ").append(year).append(" "));

		Bundle extras = this.getIntent().getExtras();
		menuItem = new MenuItems();
		if (extras != null) {
			toUserId = extras.getString(AppConstant.COMPOSE_TASK.USER_ID);

			userName = extras.getString(AppConstant.COMPOSE_TASK.USER_NAME);

			menuItem = (MenuItems) extras
					.getSerializable(AppConstant.COMPOSE_TASK.MENU_ITEM_OBJECT);

			isEdit = extras.getBoolean(AppConstant.COMPOSE_TASK.EDIT_ENABLE);

			if (!extras.getBoolean(AppConstant.COMPOSE_TASK.EDIT_ENABLE)) {
				displaySpinnerData();
			} 
		}

		dueDate = "0";
		dueStatus = "1";
		cbNoDueDate.setChecked(true);

		descHeight = this.getResources()
				.getDrawable(R.drawable.et_copmpose_task_detail)
				.getIntrinsicHeight();
		descWidth = this.getResources()
				.getDrawable(R.drawable.et_copmpose_task_detail)
				.getIntrinsicWidth();

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, descHeight);
		taskDescription.setLayoutParams(params);

		// Load Task data to view for update task
		loadData(extras.getBoolean(AppConstant.COMPOSE_TASK.EDIT_ENABLE));

	}

	/**
	 * THIS METHOD IS LOAD DATA FROM SERVER TO EDITE TASK
	 * 
	 * @param isEditMode
	 */
	private void loadData(Boolean isEditMode) {

		if (isEditMode) {

			taskDescription.setText(getIntent().getExtras().getString(
					AppConstant.COMPOSE_TASK.TASK_DESCRIPTION));

			String taskStatus = getIntent().getExtras().getString(
					AppConstant.COMPOSE_TASK.TASK_DUEDATE_STATUS);

			if (taskStatus
					.equalsIgnoreCase(AppConstant.TASK_STATUS_VALUE.EXPIRED)) {

			} else {

				Integer status = Integer.parseInt(getIntent().getExtras()
						.getString(AppConstant.COMPOSE_TASK.TASK_TYPE));

				switch (status) {
				case 3:

					cbNoDueDate.setChecked(false);
					cbDoItToday.setChecked(false);
					cbDoItAnyTime.setChecked(false);
					cbSetDueDate.setChecked(true);
					tastType = "3";
					isDueDateSet = true;

					long StartDateInEdit = Long.parseLong(getIntent().getExtras().getString(
											AppConstant.COMPOSE_TASK.TASK_DUE_STARTDATE));
					long EndDateInEdit = Long.parseLong(getIntent().getExtras().getString(
									AppConstant.COMPOSE_TASK.TASK_DUE_ENDDATE));

					tvStartDatePicker.setText(getDateAndTimeStr(StartDateInEdit).split("-")[0]);
					tvStartTimePicker.setText(getDateAndTimeStr(StartDateInEdit).split("-")[1]);

					tvEndDatePicker.setText(getDateAndTimeStr(EndDateInEdit).split("-")[0]);
					tvEndTimePicker.setText(getDateAndTimeStr(EndDateInEdit).split("-")[1]);

					String isRepeated = getIntent().getExtras().getString(AppConstant.COMPOSE_TASK.TASK_IS_REPEATEND);

					if (isRepeated.equals("1")) {
						int repeatFreq = Integer.parseInt(getIntent().getExtras().getString(AppConstant.COMPOSE_TASK.TASK_DUEDATE_REPEAT));
						tvDeudateRepeat.setText(repeat_item[repeatFreq]);
						if (!tvDeudateRepeat.getText().toString().equals(repeat_item[0])) {

							if (llEndReapetDeuDate.getVisibility() == View.GONE) {
								llEndReapetDeuDate.setVisibility(View.VISIBLE);
							}

							String endRepeat = getIntent().getExtras().getString(AppConstant.COMPOSE_TASK.TASK_DUEDATE_REPEATEND);
							if (endRepeat.equals(repeat_item[0])) {
								tvDeudateEndRepeat.setText(endRepeat);
							} else {

								long EndRepeatDateInEdit = Long
										.parseLong(endRepeat);

								tvDeudateEndRepeat.setText(getDateAndTimeStr(
										EndRepeatDateInEdit).split("-")[0]);
							}

						} else {
							if (llEndReapetDeuDate.getVisibility() == View.VISIBLE) {
								llEndReapetDeuDate.setVisibility(View.GONE);
							}
							tvDeudateEndRepeat.setText(repeat_item[0]);

						}
					} else {
						tvDeudateRepeat.setText(repeat_item[0]);
						if (llEndReapetDeuDate.getVisibility() == View.VISIBLE) {
							llEndReapetDeuDate.setVisibility(View.GONE);
						}
					}

					break;

				case 0:
					cbNoDueDate();
					break;

				case 1:
					cbDoItToday();
					break;
				case 2:
					cbDoItAnyTime();
					break;
				}
			}

			displaySpinnerData();

			if (StringUtils.isNotBlank(getIntent().getExtras().getString(
					AppConstant.COMPOSE_TASK.TASK_IMGAGE_URI))) {
				imageLoader.displayImage(
						getIntent().getExtras().getString(
								AppConstant.COMPOSE_TASK.TASK_IMGAGE_URI),
						selectImageView, options);
				compostaskSelectImageTextView.setVisibility(View.GONE);
			}

		}

	}

	/**
	 * Method to get formated date from long value
	 * 
	 * @param timaStampValue
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	private String getDateAndTimeStr(long timaStampValue) {
		SimpleDateFormat formatter = new SimpleDateFormat("MMM-d-yyyy h:mm a");
		String dateString = formatter.format(timaStampValue * 1000);

		String date = dateString.split(" ")[0].split("-")[0] + " "
				+ dateString.split(" ")[0].split("-")[1] + ", "
				+ dateString.split(" ")[0].split("-")[2];

		String editTime = dateString.split(" ")[1] + " "
				+ dateString.split(" ")[2];

		return date + "-" + editTime;
	}

	/**
	 * THIS METHOD ID SET CURRENT USER IN HEADER OF COMPOSE TASK
	 */
	private void displaySpinnerData() {

		List<String> list = new ArrayList<String>();
		for (int i = 0; i < menuItem.frindsList.size(); i++) {

			if (menuItem.frindsList.get(i).userId != null) {
				list.add(menuItem.frindsList.get(i).Name);
			}
		}
Log.e("list value", ""+list);
		CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, list,
				false, "hi");
		spinnerList.setAdapter(adapter);

		for (int i = 0; i < menuItem.frindsList.size(); i++) {

			if (menuItem.frindsList.get(i).userId.equals(toUserId)) {
				spinnerList.setSelection(i);
			}
		}

		spinnerList.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

				toUserId = menuItem.frindsList.get(position).userId;

				userName = menuItem.frindsList.get(position).Name;

				Bitmap bitmap = contactUtils.openPhoto(Long
						.parseLong(menuItem.frindsList.get(position).contactId));
				if (bitmap != null) {
					composeTaskPprofilePicImageView.setImageBitmap(bitmap);
				} else {
					composeTaskPprofilePicImageView
							.setImageResource(R.drawable.compose_task_profile_pic);
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});
	}

	/**
	 * This Methos is call when user press Back Arrow
	 */
	@Click
	public void baseHeaderBackArrow() {

		if (llSetdeudate.getVisibility() == View.VISIBLE) {
			llSetdeudate.setVisibility(View.GONE);
			llmaincompose.setVisibility(View.VISIBLE);
			// cbNoDueDate();
		} else {

			finish();

		}
	}

	/**
	 * This Methos is call to show date picker dailog for start date settings
	 */
	@Click
	public void tvStartDatePicker() {

		DialogFragment newFragment = new DatePickerFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(AppConstant.VIEW_ID, R.id.tvStartDatePicker);
		newFragment.setArguments(bundle);
		newFragment.show(this.getFragmentManager(), AppConstant.DATE_PICKER);

	}

	/**
	 * This Methos is call to show date picker dailog for end date settings
	 */
	@Click
	public void tvEndDatePicker() {

		DialogFragment newFragment = new DatePickerFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(AppConstant.VIEW_ID, R.id.tvEndDatePicker);
		newFragment.setArguments(bundle);
		newFragment.show(this.getFragmentManager(), AppConstant.DATE_PICKER);

	}

	/**
	 * This Methos is call to show time picker dailog for start time settings
	 */
	@Click
	public void tvStartTimePicker() {
		String startTime = tvStartTimePicker.getText().toString();

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR,
				Integer.parseInt(startTime.split(" ")[0].split(":")[0]));
		calendar.set(Calendar.MINUTE,
				Integer.parseInt(startTime.split(" ")[0].split(":")[1]));
		if (startTime.split(" ")[1].equalsIgnoreCase("pm")) {
			calendar.set(Calendar.AM_PM, Calendar.PM);
		} else {
			calendar.set(Calendar.AM_PM, Calendar.AM);
		}

		DialogFragment newFragment = new TimePickerFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(AppConstant.VIEW_ID, R.id.tvStartTimePicker);
		bundle.putInt(AppConstant.CURRENT_HOUR,
				calendar.get(Calendar.HOUR_OF_DAY));
		bundle.putInt(AppConstant.CURRENT_MINUTES,
				calendar.get(Calendar.MINUTE));
		newFragment.setArguments(bundle);
		newFragment.show(this.getFragmentManager(), AppConstant.TIME_PICKER);
		isShown = Boolean.FALSE;
	}

	/**
	 * This Methos is call to show time picker dailog for end time settings
	 */
	@Click
	public void tvEndTimePicker() {
		String endTime = tvEndTimePicker.getText().toString();

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR,
				Integer.parseInt(endTime.split(" ")[0].split(":")[0]));
		calendar.set(Calendar.MINUTE,
				Integer.parseInt(endTime.split(" ")[0].split(":")[1]));
		if (endTime.split(" ")[1].equalsIgnoreCase("pm")) {
			calendar.set(Calendar.AM_PM, Calendar.PM);
		} else {
			calendar.set(Calendar.AM_PM, Calendar.AM);
		}

		DialogFragment newFragment = new TimePickerFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(AppConstant.VIEW_ID, R.id.tvEndTimePicker);
		bundle.putInt(AppConstant.CURRENT_HOUR,
				calendar.get(Calendar.HOUR_OF_DAY));
		bundle.putInt(AppConstant.CURRENT_MINUTES,
				calendar.get(Calendar.MINUTE));
		newFragment.setArguments(bundle);
		newFragment.show(this.getFragmentManager(), AppConstant.TIME_PICKER);
		isShown = Boolean.FALSE;
	}

	/**
	 * This Method is callback when the date set on dialog
	 * 
	 * @param date
	 * @param viewId
	 */
	@Override
	public void onDatePicked(String date, int viewId) {
		if (StringUtils.isNotBlank(date)) {
			if (date.equals("error")) {
				alertDialog.createDefaultDialog(
						getResources().getString(
								R.string.dialog_box_error_title),
						getString(R.string.time_picker_error));
			} else {
				if (viewId == R.id.tvStartDatePicker) {
					tvStartDatePicker.setText(date);
				} else if (viewId == R.id.tvEndDatePicker) {
					tvEndDatePicker.setText(date);
				} else if (viewId == R.id.tvDeudateEndRepeat) {
					// tvDeudateEndRepeat.setText(date);
					endAlertDialog.setDate(date);
				}
			}
		}
	}

	/**
	 * This Methos is callback when the set time on dialog
	 * @param endTime
	 * @param time
	 * @param viewId
	 */
	@Override
	public void onTimePicked(String endTime, String time, int viewId) {

		// display the selected time in the TextView
		if (StringUtils.isNotBlank(time)) {
			if (time.equals("error")) {
				if (!isShown) {
					isShown = Boolean.TRUE;
					alertDialog.createDefaultDialog(
							getResources().getString(
									R.string.dialog_box_error_title),
							getString(R.string.time_picker_error));
				}
			} else {
				if (viewId == R.id.tvStartTimePicker) {

					tvStartTimePicker.setText(time);
					tvEndTimePicker.setText(endTime);

				} else if (viewId == R.id.tvEndTimePicker) {

					tvEndTimePicker.setText(time);

				}
			}
		}
	}

	/**
	 * This Methos is call when user press Cancel button
	 */
	@Click
	public void btnBaseHeaderButtonDelete() {
		baseHeaderBackArrow();
	}

	/**
	 * This Methos is call when user press save and exit button
	 */
	@Click
	public void btnBaseHeaderButtonNext() {
		sendAssignTaskRequest();
	}

	/**
	 * SEND ASSIGN TASK REQUEST TO SERVER
	 */
	@UiThread
	public void sendAssignTaskRequest() {

		description = taskDescription.getText().toString();

		if (CommonUtils.isNetworkAvailable(this)) {

			if (StringUtils.isNotBlank(description)) {

				long startDate = getDateTimeFormat(tvStartDatePicker.getText()
						.toString(), tvStartTimePicker.getText().toString());
				long enddate = getDateTimeFormat(tvEndDatePicker.getText()
						.toString(), tvEndTimePicker.getText().toString());

				String repeat = tvDeudateRepeat.getText().toString();
				String endrepeat = tvDeudateEndRepeat.getText().toString();

				JSONObject object = getTaskforServer(
						startDate,
						enddate,
						repeat,
						endrepeat,
						getIntent().getExtras().getString(
								AppConstant.COMPOSE_TASK.TASK_DUEDATE_STATUS),
						getIntent().getExtras().getString(
								AppConstant.COMPOSE_TASK.TASK_ID));

				if (tastType.equals("3")
						&& !repeat.equalsIgnoreCase(repeat_item[0])) {

					if (!endrepeat.equals(repeat_item[0])
							&& enddate > getDateTimeFormat(endrepeat, "")) {

						alertDialog.createDefaultDialog(getResources()
								.getString(R.string.dialog_box_error_title),
								getString(R.string.time_picker_error));
					} else if (enddate < startDate) {
						alertDialog.createDefaultDialog(getResources()
								.getString(R.string.dialog_box_error_title),
								getString(R.string.time_picker_error));
					} else {
						CustomProgressbar.showProgressBar(this, false,
								getString(R.string.loading_assign_task));
						AssignATask(LoginUserId, object, imageuri);

					}
				} else if (tastType.equals("3")
						&& repeat.equalsIgnoreCase(repeat_item[0])) {
					if (enddate < startDate
							&& repeat.equalsIgnoreCase(repeat_item[0])) {
						alertDialog.createDefaultDialog(getResources()
								.getString(R.string.dialog_box_error_title),
								getString(R.string.time_picker_error));
					} else {
						CustomProgressbar.showProgressBar(this, false,
								getString(R.string.loading_assign_task));
						AssignATask(LoginUserId, object, imageuri);
					}
				} else if (!tastType.equals("3")) {
					CustomProgressbar.showProgressBar(this, false,
							getString(R.string.loading_assign_task));
					AssignATask(LoginUserId, object, imageuri);
				}
			} else {
				CustomProgressbar.hideProgressBar();
				alertDialog.createDefaultDialog(
						getResources().getString(
								R.string.dialog_box_error_title),
						getResources().getString(R.string.enter_description));
			}
		} else {	CommonUtils.showErrorToast(this,
				getResources().getString(R.string.time_out_connetion));}
	}

	/**
	 * Task in jsonObject format to send in server
	 * 
	 * @param startDate
	 * @param enddate
	 * @param repeat
	 * @param endrepeat
	 * @param status
	 * @param taskId
	 * @return
	 */
	private JSONObject getTaskforServer(long startDate, long enddate,
			String repeat, String endrepeat, String status, String taskId) {

		JSONObject object = new JSONObject();
		try {
			if (StringUtils.isBlank(taskId)) {
				object.put(AppConstant.ASSIGN_TASK.TASK_ID, "");
			} else {
				object.put(AppConstant.ASSIGN_TASK.TASK_ID, taskId);
			}
			object.put(AppConstant.ASSIGN_TASK.DESCRIPTION,
					CommonUtils.urlEncode(description));
			object.put(AppConstant.ASSIGN_TASK.TO_USER_ID, toUserId);
			object.put(AppConstant.ASSIGN_TASK.TASK_TYPE, tastType);
			if (StringUtils.isBlank(status)) {
				object.put(AppConstant.ASSIGN_TASK.DAU_STATUS, "0");
			} else {
				object.put(AppConstant.ASSIGN_TASK.DAU_STATUS, status);
			}
			if (tastType
					.equals(AppConstant.TASK_DUE_DATE_STATUS_VALUES.SET_DUE_DATE)) {
				if (startDate > 0) {
					object.put(AppConstant.ASSIGN_TASK.DAU_START_DATE,
							Long.toString(startDate));
				} else {
					object.put(AppConstant.ASSIGN_TASK.DAU_START_DATE, "");
				}
				if (enddate > 0) {
					object.put(AppConstant.ASSIGN_TASK.DAU_END_DATE,
							Long.toString(enddate));
				} else {
					object.put(AppConstant.ASSIGN_TASK.DAU_END_DATE, "");
				}
				if (repeat.equals(repeat_item[0])) {
					object.put(AppConstant.ASSIGN_TASK.REPEAT_FREQ,
							AppConstant.TASK_REPEAT_FREQ.NEVER);
				} else if (repeat.equals(repeat_item[1])) {
					object.put(AppConstant.ASSIGN_TASK.REPEAT_FREQ,
							AppConstant.TASK_REPEAT_FREQ.EVERYDAY);
				} else if (repeat.equals(repeat_item[2])) {
					object.put(AppConstant.ASSIGN_TASK.REPEAT_FREQ,
							AppConstant.TASK_REPEAT_FREQ.EVERYWEEK);
				} else if (repeat.equals(repeat_item[3])) {
					object.put(AppConstant.ASSIGN_TASK.REPEAT_FREQ,
							AppConstant.TASK_REPEAT_FREQ.EVERYTWOWEEK);
				} else if (repeat.equals(repeat_item[4])) {
					object.put(AppConstant.ASSIGN_TASK.REPEAT_FREQ,
							AppConstant.TASK_REPEAT_FREQ.EVERYMONTH);
				} else if (repeat.equals(repeat_item[5])) {
					object.put(AppConstant.ASSIGN_TASK.REPEAT_FREQ,
							AppConstant.TASK_REPEAT_FREQ.EVERYYEAR);
				} else {
					object.put(AppConstant.ASSIGN_TASK.REPEAT_FREQ, "");
				}
				if (endrepeat.equals(repeat_item[0])) {
					object.put(AppConstant.ASSIGN_TASK.REPEAT_UNTIL, endrepeat);
				} else {
					object.put(AppConstant.ASSIGN_TASK.REPEAT_UNTIL,
							Long.toString(getDateTimeFormat(endrepeat, "")));
				}
				if (!repeat.equalsIgnoreCase(repeat_item[0])) {
					object.put(AppConstant.ASSIGN_TASK.REPEATED,
							AppConstant.REPEATEDTYPE.REPEATED);
				} else {
					object.put(AppConstant.ASSIGN_TASK.REPEATED,
							AppConstant.REPEATEDTYPE.NOTREPEATD);
				}
			} else {
				object.put(AppConstant.ASSIGN_TASK.DAU_START_DATE, "");
				object.put(AppConstant.ASSIGN_TASK.DAU_END_DATE, "");
				object.put(AppConstant.ASSIGN_TASK.REPEAT_FREQ, "");
				object.put(AppConstant.ASSIGN_TASK.REPEAT_UNTIL, "");
				object.put(AppConstant.ASSIGN_TASK.REPEATED,
						AppConstant.REPEATEDTYPE.NOTREPEATD);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return object;

	}

	/**
	 * To get formatted date
	 * 
	 * @param dateStr
	 * @param timeStr
	 * @return
	 */
	private long getDateTimeFormat(String dateStr, String timeStr) {

		long timeMilliSeconds = 0;
		try {

			if (StringUtils.isNotBlank(dateStr)) {

				int monthValue = CalendarUtils
						.getMonthName(dateStr.split(" ")[0]);
				int dateValue = Integer.parseInt(dateStr.split(" ")[1]
						.replace(",", "").trim().toString());
				int yearValue = Integer.parseInt(dateStr.split(" ")[2].trim()
						.toString());

				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.MONTH, monthValue - 1);
				calendar.set(Calendar.YEAR, yearValue);
				calendar.set(Calendar.DATE, dateValue);

				if (StringUtils.isNotBlank(timeStr)) {
					calendar.set(Calendar.HOUR, Integer.parseInt(timeStr
							.split(" ")[0].split(":")[0]));
					calendar.set(Calendar.MINUTE, Integer.parseInt(timeStr
							.split(" ")[0].split(":")[1]));
					if (timeStr.split(" ")[1].equalsIgnoreCase("am")) {
						calendar.set(Calendar.AM_PM, Calendar.AM);
					} else {
						calendar.set(Calendar.AM_PM, Calendar.PM);
					}
				}
				timeMilliSeconds = calendar.getTimeInMillis() / 1000;

			}
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return timeMilliSeconds;
	}

	/**
	 * DO API CALL FOR aSSIGN TASK TO ANY USER
	 * 
	 * @param loginUserId
	 * @param task
	 * @param image
	 */
	@Background
	public void AssignATask(String loginUserId, JSONObject task, String image) {

		AssignTaskResponse response = null;
		if (loginUserId != null) {
			if (StringUtils.isNotBlank(image)) {
				try{
				response = WebServiceManager.getInstance(this).assignNewTask(
						loginUserId, task, image);
				}catch(ConnectTimeoutException e){
							CommonUtils.showTimoutError(this);
				}catch(SocketTimeoutException e){
					CommonUtils.showTimoutError(this);
				}
			} else {
		
				response = WebServiceManager.getInstance(this)
						.assignTaskWithoutImage(loginUserId, task);
		
			}

		} else {
			CustomProgressbar.hideProgressBar();
		}
		
		
		afterAssignTask(response);

	}
	


	/**
	 * AFTER GETTING RESPONSE FROM ASSIGN TASK
	 * 
	 * @param response
	 */
	@UiThread
	public void afterAssignTask(AssignTaskResponse response) {

		CustomProgressbar.hideProgressBar();
		if (response != null) {

			if (ValidationUtils.isSuccessResponse(response)) {
				//to refresh friends tab for changes in existing task count
				// Added for navigation to for me when task assigned for self
				UserSharedPreferences.getInstance(this).putBoolean(AppConstant.ACTION_REFRESH_FRIENDS, Boolean.TRUE);
				UserSharedPreferences.getInstance(this).putBoolean(AppConstant.ACTION_APP_RATER, Boolean.TRUE);
				if(LoginUserId.equalsIgnoreCase(toUserId)){
					UserSharedPreferences.getInstance(this).putBoolean(AppConstant.SELF_FORME_TAB_CHANGE, Boolean.TRUE);
					finish();
				}else{
				alertDialog.createDefaulSuccestDialog(
						getResources().getString(R.string.task_created_msg),
						response.message, new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							
								finish();
							}
						});
				}
			} else{
				alertDialog.createDefaultDialog(getResources().getString(R.string.dialog_box_error_title),
						response.message);
			}

		} else if(response == null){
			if (CommonUtils.isNetworkAvailable(this)) {
			CommonUtils.showErrorToast(this,
					getResources().getString(R.string.time_out_connetion));
			}else{
				CommonUtils.showErrorToast(this,
						getResources().getString(R.string.no_internate_connetion));
			}
}
	}

	/**
	 * UPDATE UI AFTER GETTING RESPONSE FROM UPDATE TASK
	 * 
	 * @param response
	 * @param task_status_Id
	 */
	@UiThread
	public void updateUITask(UpdateTaskResponse response, String task_status_Id) {
		CustomProgressbar.hideProgressBar();
		if (ValidationUtils.isSuccessResponse(response)) {
			Intent intent = new Intent();
			intent.putExtra(AppConstant.COMPOSE_TASK.FRIEND_USER_NAME, userName);
			intent.putExtra(AppConstant.ASSIGN_TASK.TASK_STATUS_ID,
					task_status_Id);
			setResult(Activity.RESULT_OK, intent);
			finish();
		} 
	}

	/**
	 * when user click on no due date checkbox
	 */
	@Click
	public void cbNoDueDate() {
		tastType = "0";
		isDueDateSet = false;
		isChanged = Boolean.FALSE;
		cbNoDueDate.setChecked(true);
		cbDoItToday.setChecked(false);
		cbDoItAnyTime.setChecked(false);
		cbSetDueDate.setChecked(false);
		cbSetDueDate.setText(this.getResources().getString(
				R.string.cb_set_a_due_date));
		dueDate = "0";
		dueStatus = AppConstant.TASK_DUE_DATE_STATUS_VALUES.NO_DUE_DATE;
	
		showNextButton(true);
		

	}

	/**
	 * when user click on Do it Today checkbox
	 */
	@Click
	public void cbDoItToday() {
		tastType = "1";
		isDueDateSet = false;
		isChanged = Boolean.FALSE;
		cbNoDueDate.setChecked(false);
		cbDoItToday.setChecked(true);
		cbDoItAnyTime.setChecked(false);
		cbSetDueDate.setChecked(false);
		cbSetDueDate.setText(this.getResources().getString(
				R.string.cb_set_a_due_date));
		dueDate = getCurrentTime();
		dueStatus = AppConstant.TASK_DUE_DATE_STATUS_VALUES.DO_IT_TODAY;
		
		showNextButton(true);
		
	}

	/**
	 * when user click on do it any time checkbox
	 */
	@Click
	public void cbDoItAnyTime() {
		tastType = "2";
		isDueDateSet = false;
		isChanged = Boolean.FALSE;
		cbNoDueDate.setChecked(false);
		cbDoItToday.setChecked(false);
		cbDoItAnyTime.setChecked(true);
		cbSetDueDate.setChecked(false);
		cbSetDueDate.setText(this.getResources().getString(
				R.string.cb_set_a_due_date));
		dueDate = getWeekEndTime();
		dueStatus = AppConstant.TASK_DUE_DATE_STATUS_VALUES.DO_IT_ANY_TIME_THIS_WEEK;

		showNextButton(true);

	}

	/**
	 * when user click on Set Due Date checkbox
	 */
	@Click
	public void cbSetDueDate() {

		cbNoDueDate.setChecked(false);
		cbDoItToday.setChecked(false);
		cbDoItAnyTime.setChecked(false);
		cbSetDueDate.setChecked(true);
		if (StringUtils.isBlank(taskDescription.getText().toString())) {
			alertDialog.createDefaultDialog(
					getResources().getString(R.string.dialog_box_error_title),
					getResources().getString(R.string.enter_description));
		} else {
			tastType = "3";
			isDueDateSet = true;

			setDeuDatePopUp();

			if (!isChanged) {

				isChanged = true;

				tvStartTimePicker.setText(getCurrentTimeStamp().toString());
				tvEndTimePicker.setText(getEndTimeStamp().toString());
				tvDeudateRepeat.setText(repeat_item[0]);

				tvStartDatePicker.setText(new StringBuilder()
						.append(CalendarUtils.getMonthName(month)).append(" ")
						.append(day).append(", ").append(year).append(" "));

				tvEndDatePicker.setText(new StringBuilder()
						.append(CalendarUtils.getMonthName(month)).append(" ")
						.append(day).append(", ").append(year).append(" "));

				llEndReapetDeuDate.setVisibility(View.GONE);
				tvDeudateEndRepeat.setText(repeat_item[0]);
			}
		}
		showNextButton(true);
	}

	// popmenu for setting deu date
	private void setDeuDatePopUp() {

		llmaincompose.setVisibility(View.GONE);
		llSetdeudate.setVisibility(View.VISIBLE);

	}

	/**
	 * THIS METHOD IS CALLED WHEN USER PRESS REPEAT TASK ROW TO SHOW OPTIONS.
	 */
	@Click
	public void llReapetDeuDate() {

		commonAlertDialog.createRepeatDialog(tvDeudateRepeat.getText()
				.toString());
		commonAlertDialog.show();
	}

	/**
	 * THIS METHOD IS CALLED WHEN USER PRESS END REPEAT TASK ROW TO SHOW OPTIONS
	 * WITH DATE PICKER.
	 */
	@Click
	public void llEndReapetDeuDate() {

		endAlertDialog.createEndRepeatDialog(this.getFragmentManager(),
				tvDeudateEndRepeat.getText().toString());
		endAlertDialog.show();
	}

	/**
	 * THIS METHOD IS CALLED WHEN USER PRESS SELECT POHOTO .
	 */
	@Click
	public void selectImageView() {
		CommonUtils.hideSoftKeyboard(this);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select image");
		builder.setItems(R.array.choose_image,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						if (which == 0) {
							takePhoto();

						} else if (which == 1) {
							Intent mediaintent = new Intent(
									Intent.ACTION_PICK,
									android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							mediaintent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
							startActivityForResult(mediaintent, GALARY_PHOTO);

						}
					}
				});
		builder.create();
		builder.show();
	}

	/**
	 * THIS METHOD IS CALLED WHEN USER PRESS SELECT POHOTO .
	 */
	@Click
	public void removeImageView() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(getString(R.string.unattached_title))
				.setMessage(getString(R.string.unattached_message))
				.setPositiveButton(this.getString(R.string.button_yes),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								selectImageView.setBackgroundResource(0);
								compostaskSelectImageTextView
										.setVisibility(View.VISIBLE);
								ComposeTaskActivity.this.imageuri = "";
								selectImageView
										.setImageDrawable(getResources()
												.getDrawable(
														R.drawable.selector_select_image));
								selectImageView.refreshDrawableState();
								removeImageView.setVisibility(View.GONE);
								showNextButton(true);
								imageDelete = "1";

							}
						})
				.setNegativeButton(this.getString(R.string.button_no),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {

							}
						});
		Dialog dialog = builder.create();
		dialog.show();
	}

	/**
	 * THIS METHOD IS CALLED AFTER CAPURE OR SELECT PHOTO.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		isDisplayCancelPopup = true;

		if (requestCode == CAMERA_PHOTO && resultCode == RESULT_OK) {
			String imagePath = UserSharedPreferences.getInstance(this)
					.getString(AppConstant.KEY_CAMERA_IMAGE_URI);
			Uri path = Uri.parse(imagePath);
			String photoUri = uriToSringConvert(path);

			Bitmap photo = CommonUtils.decodeSampledBitmapFromFile(photoUri,
					AppConstant.UPLOAD_FILE_WIDTH,
					AppConstant.UPLOAD_FILE_HEIGHT);

			showNextButton(true);

			compostaskSelectImageTextView.setVisibility(View.GONE);

			this.imageuri = photoUri;
			if (photo != null) {
				selectImageView.setImageBitmap(photo);

			} else {
				selectImageView
						.setBackgroundResource(R.drawable.photo_frame_bg);
			}

		} else if (requestCode == GALARY_PHOTO && resultCode == RESULT_OK) {

			Uri imageUri = data.getData();
			String photoUri = uriToSringConvert(imageUri);
			Bitmap imgSource = CommonUtils.decodeSampledBitmapFromFile(
					photoUri, AppConstant.UPLOAD_FILE_WIDTH,
					AppConstant.UPLOAD_FILE_HEIGHT);

			// this will check image size in byte
			showNextButton(true);
			// selectImage.setBackgroundResource(R.drawable.photo_frame_bg);
			compostaskSelectImageTextView.setVisibility(View.GONE);
			this.imageuri = photoUri;
			Drawable drawable = new BitmapDrawable(getResources(), imgSource);
			selectImageView.setImageDrawable(drawable);
		}

	}

	/**
	 * THIS METHOD RETURN CAPTURED OR SELECTED IMAGE URI
	 * 
	 * @param uri
	 * @return
	 */
	public String uriToSringConvert(Uri uri) {

		try {
			String[] projection = { MediaStore.Images.Media.DATA };
			Cursor cursor = this.getContentResolver().query(uri, projection,
					null, null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} catch (Exception e) {
			return uri.getPath();

		}
	}

	/**
	 * This method is used to show popup dialog
	 *
	 * @param title
	 * @param message
	 */
	public void showPopUpBox(String title, String message) {
		CommonUtils.hideSoftKeyboard(this);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(title)
				.setMessage(message)
				.setPositiveButton(this.getString(R.string.button_yes),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								finish();
							}
						})
				.setNegativeButton(this.getString(R.string.button_no),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {

							}
						});
		Dialog dialog = builder.create();
		dialog.show();
	}

	/**
	 * This method is to call get current time
	 * 
	 * @return
	 */
	public static String getCurrentTimeStamp() {

		Date now = new Date();
		String strDate = DateFormat.format("h:mm a", now).toString();
		return strDate;
	}

	/**
	 * This method is to call get current time with 1 hour append
	 * 
	 * @return
	 */
	public static String getEndTimeStamp() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR_OF_DAY, 1);
		String strDate = DateFormat.format("h:mm a", cal.getTime()).toString();
		return strDate;
	}

	/**
	 * THIS METHOOD RETURNS CURRENT TIME IN SECONDS
	 * 
	 * @return
	 */
	public static String getCurrentTime() {
		Calendar c = Calendar.getInstance();
		Long mcurrentTime = (c.getTimeInMillis()) / 1000;
		return mcurrentTime.toString();
	}

	/**
	 * Method to get weekend time for task do it any time this week options 
	 * 
	 * @return
	 */
	public static String getWeekEndTime() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		c.add(Calendar.DATE, 1);
		Long weektime = c.getTimeInMillis() / 1000;
		return weektime.toString();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		CommonUtils.hideSoftKeyboard(this);
		return super.onTouchEvent(event);
	}

	/**
	 * THIS METHOD RETURNS CAPUTRED IMAGE PATH IN CASE OF DATA IS GETTING NULL
	 * 
	 * @return
	 */
	public String getCameraPhotoStorePath() {

		String cameraPhotoPath = "";
		try {
			String[] projection = { MediaStore.Images.Thumbnails.DATA };
			String selection = MediaStore.Images.Thumbnails.KIND + "=" + // Select
					// only mini's
					MediaStore.Images.Thumbnails.MINI_KIND;

			String sort = MediaStore.Images.Thumbnails._ID + " DESC";

			Cursor myCursor = this.getContentResolver().query(
					MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
					projection, selection, null, sort);

			// Create new Cursor to obtain the file Path for the large image
			String[] largeFileProjection = {
			// MediaStore.Images.ImageColumns._ID,
			MediaStore.Images.ImageColumns.DATA };

			String largeFileSort = MediaStore.Images.ImageColumns._ID + " DESC";
			myCursor = this.getContentResolver().query(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					largeFileProjection, null, null, largeFileSort);
			try {
				myCursor.moveToFirst();
				// This will actually give you the file path location of the
				// image.
				cameraPhotoPath = myCursor
						.getString(myCursor
								.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA));
			} finally {
				myCursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cameraPhotoPath;
	}

	/**
	 * While taking photo using camera
	 * 
	 */
	private void takePhoto() {
		try {
			final ContentValues values = new ContentValues();
			values.put(MediaStore.Images.Media.TITLE, "AssignIt_photo.jpeg");
			Uri cameraImageUri = this.getContentResolver().insert(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

			UserSharedPreferences.getInstance(this)
					.putString(AppConstant.KEY_CAMERA_IMAGE_URI,
							cameraImageUri.toString());

			final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
			intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
			try {
				this.startActivityForResult(intent, CAMERA_PHOTO);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onBackPressed() {
		baseHeaderBackArrow();
	}

	/**
	 * hide keyboard on click the compose task screen
	 */
	@Click
	public void rlmainComposeTask() {
		CommonUtils.hideSoftKeyboard(this);
	}

	/*
	 * callback method date picked
	 * 
	 * @param  deuDateStatus
	 */
	@Override
	public void onDeuDatePicked(String deuDateStatus) {
		if (StringUtils.isNotBlank(deuDateStatus)) {

			tvDeudateRepeat.setText(deuDateStatus);

			if (tvDeudateRepeat.getText().toString().equals(repeat_item[0])) {
				if (llEndReapetDeuDate.getVisibility() == View.VISIBLE) {
					llEndReapetDeuDate.setVisibility(View.GONE);
				}
			}
			if (!tvDeudateRepeat.getText().toString().equals(repeat_item[0])) {
				if (llEndReapetDeuDate.getVisibility() == View.GONE) {
					llEndReapetDeuDate.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	/*
	 * callback method end date picked
	 *
	 * @param  deuEndDateStatus
	 */
	@Override
	public void onEndDeuDatePicked(String deuEndDateStatus) {

		if (StringUtils.isNotBlank(deuEndDateStatus)) {
			if (!deuEndDateStatus.equals("error")) {

				tvDeudateEndRepeat.setText(deuEndDateStatus);
			}
		}
	}
}
