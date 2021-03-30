package com.assignit.android.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.assignit.android.R;
import com.assignit.android.localstorage.UserSharedPreferences;
import com.assignit.android.pojo.MenuItems;
import com.assignit.android.pojo.TaskForMe;
import com.assignit.android.ui.activities.HomeActivity;
import com.assignit.android.ui.activities.SplashActivity;
import com.assignit.android.ui.activities.controller.ForMeController;
import com.assignit.android.ui.views.AnimatedTextView;
import com.assignit.android.ui.views.CustomMenuView;
import com.assignit.android.ui.views.CustomProgressbar;
import com.assignit.android.ui.views.CustomTextView;
import com.assignit.android.utils.AppConstant;
import com.assignit.android.utils.CommonAsyncTask;
import com.assignit.android.utils.CommonUtils;
import com.assignit.android.utils.StringUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * For me Adapter class to display ui list of task assigned for me
 * 
 * @author Innoppl
 * 
 */
public class ForMeAdapter extends BaseAdapter {

	Context mContext;

	private List<TaskForMe> taskForMesList;

	private final ForMeController mForMeController;

	private final ImageLoader imageLoader;
	
	private final DisplayImageOptions displayImageOptions;

	public CustomMenuView mMenuView;
	
	CustomTextView dueStatus;
	
	// to maintain filter list
	List<TaskForMe> taskForMesFilterList;
	
	AnimatedTextView taskDescription;
	
	private List<TaskForMe> filterData = new ArrayList<TaskForMe>();

	/**
	 * constructor for adapter
	 * 
	 * @param Context
	 * @param forMes
	 * @param controller
	 */
	public ForMeAdapter(Context Context, List<TaskForMe> forMes,
			ForMeController controller) {
		super();
		this.mContext = Context;
		this.taskForMesList = forMes;
		this.mForMeController = controller;
		imageLoader = ImageLoader.getInstance();
		displayImageOptions = SplashActivity.getDisplayImageOptions();
		filterData = taskForMesList;

	}

	@Override
	public int getCount() {
		return taskForMesList.size();
	}

	@Override
	public Object getItem(int position) {
		return taskForMesList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		View view = null;

		if (convertView == null) {
			view = View.inflate(mContext, R.layout.row_for_melist, null);
		} else {
			view = convertView;
		}
		taskDescription = (AnimatedTextView) view.findViewById(R.id.mForMeTaskNameView);
		taskDescription.setHeight(CommonUtils.convertToDp(mContext, 18));
		taskDescription.setSingleLine(true);
		View photo = view.findViewById(R.id.mForMePhotoLinearLayout);
		if(photo.getVisibility() == View.VISIBLE){
		photo.setVisibility(View.GONE);
		}
		
		setData(view, position);

		return view;
	}

	/**
	 * callback method for controller to update task list
	 * @param taskForMes
	 */
	public void updateAdapter(List<TaskForMe> taskForMes) {
		taskForMesList = taskForMes;
		filterData = taskForMes;
		notifyDataSetChanged();
	}

	/**
	 * this method returns adapter list
	 * 
	 * @return
	 */
	public List<TaskForMe> getAdapterList() {
		return taskForMesList;
	}

	/**
	 * set data for menus
	 * 
	 * @param view
	 * @param position
	 */
	@SuppressLint("ResourceAsColor")
	private void setData(View view, int position) {
		showMenu(view, position);
		final TaskForMe task = taskForMesList.get(position);

		dueStatus = (CustomTextView) view.findViewById(R.id.mForMeDueDateView);

		if (task.Description != null) {

			String text = "<font color=#FFA800><b>" + (position + 1) + ":"
					+ "</b></font> <font color=#222222>" + " "
					+ task.Description + "</font>";
			taskDescription.setText(Html.fromHtml(text));
		}

		if (task.fromUser.FromUsername != null) {
			CustomTextView assignedName = (CustomTextView) view
					.findViewById(R.id.mForMeAssignedView);
			boolean isLand = mContext.getResources().getBoolean(
					R.bool.isLandscape);

			if (!isLand) {
				assignedName.setWidth(CommonUtils.convertToDp(mContext, 85));
			} else {

				assignedName.setWidth(CommonUtils.convertToDp(mContext, 300));
			}

			if (task.fromUser.FromUsername
					.equals(UserSharedPreferences.getInstance(mContext)
							.getString(AppConstant.LOGIN.USER_ID))) {
				assignedName.setText(mContext.getResources().getString(
						R.string.self_user));
				task.fromUser.FromUsername = mContext.getResources().getString(
						R.string.self_user);

			} else {
				if (task.fromUser.FromUsername != null) {
					assignedName.setText(task.fromUser.FromUsername);

				}
			}
		}

		ImageView photo = (ImageView) view.findViewById(R.id.mForMePhotoFrame);

		if (StringUtils.isNotBlank(task.Image)) {
			imageLoader.displayImage(task.Image, photo, displayImageOptions);
			photo.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					new CommonAsyncTask(mContext).execute(task.Image);
				}
			});
		}

		if (taskForMesList.get(position).Type != null) {
			switch (Integer.parseInt(taskForMesList.get(position).Type)) {
			case 0:
				if (taskForMesList.get(position).Status
						.equals(AppConstant.TASK_STATUS_VALUE.COMPLETED)
						&& !task.DauEndDate.equals("0")) {
					dueStatus.setText(CommonUtils.getFormatedDateMDY(task.DauEndDate));
				} else {
					dueStatus.setText(AppConstant.TASK_DUE_DATE_STATUS.NO_DUE_DATE);
				}
				break;
			case 1:
				if (!task.DauEndDate.equals("0")&&StringUtils.isNotBlank(task.DauEndDate)
						&&(taskForMesList.get(position).Status
						.equals(AppConstant.TASK_STATUS_VALUE.EXPIRED)||taskForMesList.get(position).Status
						.equals(AppConstant.TASK_STATUS_VALUE.COMPLETED))) {
					dueStatus.setText(CommonUtils.getFormatedDateMDY(task.DauEndDate));
				} else {
					dueStatus.setText(AppConstant.TASK_DUE_DATE_STATUS.DO_IT_TODAY);
				}
				break;
			case 2:
				if (!task.DauEndDate.equals("0")&&StringUtils.isNotBlank(task.DauEndDate)
						&&(taskForMesList.get(position).Status
						.equals(AppConstant.TASK_STATUS_VALUE.EXPIRED)||taskForMesList.get(position).Status
						.equals(AppConstant.TASK_STATUS_VALUE.COMPLETED))) {
					dueStatus.setText(CommonUtils.getFormatedDateMDY(task.DauEndDate));
				} else {
					dueStatus.setText(AppConstant.TASK_DUE_DATE_STATUS.DO_IT_ANY_TIME_THIS_WEEK);
				}
				break;
			case 3:
				dueStatus.setText(CommonUtils.getFormatedDateMDY(task.DauEndDate));
				break;
			}
		}

		CustomTextView statusView = (CustomTextView) view.findViewById(R.id.mForMeStatusView);
		if (taskForMesList.get(position).Status != null) {
			switch (Integer.parseInt(taskForMesList.get(position).Status)) {
			case 0:
				statusView.setText(AppConstant.TASK_STATUS.NEW);
				statusView.setTextColor(mContext.getResources().getColor(
						R.color.color_new));
				break;
			case 1:
				statusView.setText(AppConstant.TASK_STATUS.ACCEPTED);
				statusView.setTextColor(mContext.getResources().getColor(
						R.color.color_accepted));
				taskForMesList.get(position).laststatus = AppConstant.TASK_STATUS.ACCEPTED;
				break;
			case 2:
				statusView.setText(AppConstant.TASK_STATUS.IN_PROGRESS);
				statusView.setTextColor(mContext.getResources().getColor(
						R.color.color_inprogress));
				taskForMesList.get(position).laststatus = AppConstant.TASK_STATUS.IN_PROGRESS;
				break;
			case 3:
				statusView.setText(AppConstant.TASK_STATUS.COMPLETED);
				statusView.setTextColor(mContext.getResources().getColor(
						R.color.color_copeleted));
				taskForMesList.get(position).laststatus = AppConstant.TASK_STATUS.COMPLETED;
				break;
			case 4:
				statusView.setText(AppConstant.TASK_STATUS.EXPIRED);
				statusView.setTextColor(mContext.getResources().getColor(
						R.color.color_expired));
				break;
			case 5:
				statusView.setText(AppConstant.TASK_STATUS.REJECTED);
				statusView.setTextColor(mContext.getResources().getColor(
						R.color.color_rejected));
				taskForMesList.get(position).laststatus = AppConstant.TASK_STATUS.REJECTED;
				break;
			case 6:
				StrikethroughSpan STRIKE_THROUGH_SPAN = new StrikethroughSpan();

				statusView.setText(AppConstant.TASK_STATUS.DELETED,
						TextView.BufferType.SPANNABLE);
				Spannable spannable = (Spannable) statusView.getText();
				spannable.setSpan(STRIKE_THROUGH_SPAN, 0,
						AppConstant.TASK_STATUS.DELETED.length(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				statusView.setTextColor(mContext.getResources().getColor(
						R.color.color_deleted));
				break;
			case 7:
				statusView.setText(AppConstant.TASK_STATUS.COMPLETED);
				statusView.setTextColor(mContext.getResources().getColor(
						R.color.color_copeleted));
				taskForMesList.get(position).laststatus = AppConstant.TASK_STATUS.COMPLETED;
				break;
			}
		}
	}

	/**
	 * Method call on Search view filter data
	 * 
	 * @return
	 */
	public Filter getFilter() {
		Filter filter = new Filter() {
			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				taskForMesList = (ArrayList<TaskForMe>) results.values;
				notifyDataSetChanged();

				if (((HomeActivity) mContext).isSearchBoxEnable
						&& !filterData.isEmpty() && taskForMesList.isEmpty()) {
					((HomeActivity) mContext).noMatchFoundView
							.setVisibility(View.VISIBLE);
				} else {
					((HomeActivity) mContext).noMatchFoundView
							.setVisibility(View.GONE);
				}
			}

			@SuppressLint("DefaultLocale")
			@Override
			protected FilterResults performFiltering(CharSequence charSequence) {
				FilterResults results = new FilterResults();

				// If there's nothing to filter on, return the original data for
				// your list
				if (charSequence == null || charSequence.length() == 0) {
					results.values = filterData;
					results.count = filterData.size();
				} else {
					taskForMesFilterList = new ArrayList<TaskForMe>();
					String filterString = charSequence.toString().toLowerCase();
					String filterableString;

					for (int i = 0; i < filterData.size(); i++) {
						filterableString = filterData.get(i).fromUser.FromUsername;
						if (filterableString.toLowerCase().contains(
								filterString)) {
							taskForMesFilterList.add(filterData.get(i));
						}
					}

					results.values = taskForMesFilterList;
					results.count = taskForMesFilterList.size();
				}

				return results;
			}
		};
		return filter;
	}

	
	
	/**
	 * MenuView list and data passed to controller
	 * 
	 * @param view
	 * @param position
	 */
	private void showMenu(View view, int position) {
		final TaskForMe task = taskForMesList.get(position);
		ImageView menuView = (ImageView) view.findViewById(R.id.mForMeTaskStatusView);
		final CustomTextView statusView = (CustomTextView) view.findViewById(R.id.mForMeStatusView);
		menuView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				List<MenuItems> items = new ArrayList<MenuItems>();
			
				if(task.reminderStatus.equals("0") && task.eventId.equals("")){
						String eventId = CommonUtils.getEventID(mContext,task.Description);
					if (StringUtils.isNotBlank(eventId)&&!eventId.equals("0")) {
						CustomProgressbar.showProgressBar(mContext, false);			
						mForMeController.doApiCallReminderStatus(task.fromUser.FromUserId, task.taskId, AppConstant.REMINDERSTATUS.SET_REMINDER, eventId);
						}
				}
				
				if (task.fromUser.FromUsername.equalsIgnoreCase(mContext
						.getResources().getString(R.string.self_user))) {
					MenuItems edititem = new MenuItems();
					edititem.drawable = mContext.getResources().getDrawable(R.drawable.ic_edit);
					edititem.itemName = mContext.getString(R.string.task_menu_edit);
					edititem.isCounterVisible = Boolean.FALSE;
					edititem.friendUserName = task.fromUser.FromUsername;
					edititem.friendUserId = task.fromUser.FromUserId;
					edititem.taskDescription = task.Description;
					edititem.taskEndDueDate = task.DauEndDate;
					edititem.taskType = task.Type;
					edititem.taskStatus = task.Status;
					edititem.taskId = task.taskId;
					edititem.imageURI = task.Image;
					items.add(edititem);
				}

				if (task.Status != null	&& (task.Status.equalsIgnoreCase(AppConstant.TASK_STATUS_VALUE.NEW))) {
					MenuItems acceptitem = new MenuItems();
					acceptitem.drawable = mContext.getResources().getDrawable(R.drawable.ic_accept);
					acceptitem.itemName = mContext.getString(R.string.task_menu_accept);
					acceptitem.isCounterVisible = Boolean.FALSE;
					acceptitem.friendUserName = task.fromUser.FromUsername;
					acceptitem.friendUserId = task.fromUser.FromUserId;
					acceptitem.taskDescription = task.Description;
					acceptitem.taskEndDueDate = task.DauEndDate;
					acceptitem.taskStartDueDate = task.DauStartDate;
					acceptitem.taskType = task.Type;
					acceptitem.taskStatus = task.Status;
					acceptitem.taskId = task.taskId;
					acceptitem.imageURI = task.Image;
					items.add(acceptitem);

					MenuItems rejectitem = new MenuItems();
					rejectitem.drawable = mContext.getResources().getDrawable(R.drawable.ic_reject);
					rejectitem.itemName = mContext.getString(R.string.task_menu_reject);
					rejectitem.isCounterVisible = Boolean.FALSE;
					rejectitem.friendUserId = task.fromUser.FromUserId;
					rejectitem.friendUserName = task.fromUser.FromUsername;
					rejectitem.taskId = task.taskId;
					rejectitem.taskStatus = task.Status;
					items.add(rejectitem);

					MenuItems deleteItem = new MenuItems();
					deleteItem.drawable = mContext.getResources().getDrawable(R.drawable.ic_delete);
					deleteItem.itemName = mContext.getString(R.string.task_menu_delete);
					deleteItem.isCounterVisible = Boolean.FALSE;
					deleteItem.friendUserId = task.fromUser.FromUserId;
					deleteItem.friendUserName = task.fromUser.FromUsername;
					deleteItem.taskId = task.taskId;
					deleteItem.lastStatus = statusView.getText().toString();
					items.add(deleteItem);

					MenuItems evernoteItem = new MenuItems();
					evernoteItem.drawable = mContext.getResources().getDrawable(R.drawable.evernote);
					evernoteItem.itemName = mContext.getString(R.string.task_menu_evernote);
					evernoteItem.isCounterVisible = Boolean.FALSE;
					evernoteItem.isEvernoteVisible = Boolean.TRUE;
					items.add(evernoteItem);

					MenuItems reminderItem = new MenuItems();
					reminderItem.drawable = mContext.getResources().getDrawable(R.drawable.bell);
					reminderItem.itemName = mContext.getString(R.string.task_menu_add_reminder);
					reminderItem.isCounterVisible = Boolean.FALSE;
					reminderItem.isEvernoteVisible = Boolean.TRUE;
					items.add(reminderItem);

				} else if (task.Status != null
						&& (task.Status
								.equalsIgnoreCase(AppConstant.TASK_STATUS_VALUE.ACCEPTED))) {
					MenuItems inpregressitem = new MenuItems();
					inpregressitem.drawable = mContext.getResources().getDrawable(R.drawable.ic_inprogress);
					inpregressitem.itemName = mContext.getString(R.string.task_menu_inprogress);
					inpregressitem.isCounterVisible = Boolean.FALSE;
					inpregressitem.friendUserId = task.fromUser.FromUserId;
					inpregressitem.friendUserName = task.fromUser.FromUsername;
					inpregressitem.taskId = task.taskId;
					inpregressitem.taskStatus = task.Status;
					items.add(inpregressitem);

					MenuItems completeitem = new MenuItems();
					completeitem.drawable = mContext.getResources().getDrawable(R.drawable.ic_complete);
					completeitem.itemName = mContext.getString(R.string.task_menu_complete);
					completeitem.isCounterVisible = Boolean.FALSE;
					completeitem.friendUserId = task.fromUser.FromUserId;
					completeitem.friendUserName = task.fromUser.FromUsername;
					completeitem.taskId = task.taskId;
					completeitem.taskStatus = task.Status;
					items.add(completeitem);

					MenuItems rejectitem = new MenuItems();
					rejectitem.drawable = mContext.getResources().getDrawable(R.drawable.ic_reject);
					rejectitem.itemName = mContext.getString(R.string.task_menu_reject);
					rejectitem.isCounterVisible = Boolean.FALSE;
					rejectitem.friendUserId = task.fromUser.FromUserId;
					rejectitem.friendUserName = task.fromUser.FromUsername;
					rejectitem.taskId = task.taskId;
					rejectitem.taskStatus = task.Status;
					if (!task.fromUser.FromUsername.equalsIgnoreCase(mContext
							.getResources().getString(R.string.self_user))) {
						items.add(rejectitem);
					}

					MenuItems deleteItem = new MenuItems();
					deleteItem.drawable = mContext.getResources().getDrawable(R.drawable.ic_delete);
					deleteItem.itemName = mContext.getString(R.string.task_menu_delete);
					deleteItem.isCounterVisible = Boolean.FALSE;
					deleteItem.friendUserId = task.fromUser.FromUserId;
					deleteItem.friendUserName = task.fromUser.FromUsername;
					deleteItem.taskId = task.taskId;
					deleteItem.eventId = task.eventId;
					deleteItem.lastStatus = statusView.getText().toString();
					items.add(deleteItem);

					MenuItems evernoteItem = new MenuItems();
					evernoteItem.drawable = mContext.getResources().getDrawable(R.drawable.evernote_active);
					if(task.evernoteStatus.equals("0")){
						evernoteItem.itemName = mContext.getString(R.string.task_menu_evernote);
					}else{
						evernoteItem.itemName = mContext.getString(R.string.task_menu_go_to_evernote);
						
					}
					evernoteItem.isCounterVisible = Boolean.FALSE;
					evernoteItem.isEvernoteVisible = Boolean.FALSE;
					evernoteItem.taskId = task.taskId;
					evernoteItem.evernoteSatus = task.evernoteStatus;
					evernoteItem.friendUserId = task.fromUser.FromUserId;
					evernoteItem.friendUserName = task.fromUser.FromUsername;
					evernoteItem.taskDescription = task.Description;
					evernoteItem.imageURI = task.Image;
					evernoteItem.taskStatus = task.Status;
					items.add(evernoteItem);

					MenuItems reminderItem = new MenuItems();
					reminderItem.drawable = mContext.getResources().getDrawable(R.drawable.bell_active);
								
					if(task.reminderStatus.equals("0")){
						reminderItem.itemName = mContext.getString(R.string.task_menu_add_reminder);
					}else{
						reminderItem.itemName = mContext.getString(R.string.task_menu_edit_reminder);
						
					}
					reminderItem.eventId = task.eventId;
					reminderItem.isCounterVisible = Boolean.FALSE;
					reminderItem.isEvernoteVisible = Boolean.FALSE;
					reminderItem.taskId = task.taskId;
					reminderItem.friendUserName = task.fromUser.FromUsername;
					reminderItem.friendUserId = task.fromUser.FromUserId;
					reminderItem.taskDescription = task.Description;
					reminderItem.taskEndDueDate = task.DauEndDate;
					reminderItem.taskStartDueDate = task.DauStartDate;
					reminderItem.isRepeated = task.Repeated;
					reminderItem.taskRepeatDueDate = task.RepeatFreq;
					reminderItem.taskEndRepeatDueDate = task.EndRepeat;
					reminderItem.taskType = task.Type;
					reminderItem.taskStatus = task.Status;
					items.add(reminderItem);
				} else if (task.Status != null&&(task.Status.equalsIgnoreCase(AppConstant.TASK_STATUS_VALUE.IN_PROGRESS))) {
					MenuItems completeitem = new MenuItems();
					completeitem.drawable = mContext.getResources().getDrawable(R.drawable.ic_complete);
					completeitem.itemName = mContext.getString(R.string.task_menu_complete);
					completeitem.isCounterVisible = Boolean.FALSE;
					completeitem.friendUserId = task.fromUser.FromUserId;
					completeitem.friendUserName = task.fromUser.FromUsername;
					completeitem.taskId = task.taskId;
					completeitem.taskStatus = task.Status;
					items.add(completeitem);

					MenuItems rejectitem = new MenuItems();
					rejectitem.drawable = mContext.getResources().getDrawable(R.drawable.ic_reject);
					rejectitem.itemName = mContext.getString(R.string.task_menu_reject);
					rejectitem.isCounterVisible = Boolean.FALSE;
					rejectitem.friendUserId = task.fromUser.FromUserId;
					rejectitem.friendUserName = task.fromUser.FromUsername;
					rejectitem.taskId = task.taskId;
					rejectitem.taskStatus = task.Status;
					if (!task.fromUser.FromUsername.equalsIgnoreCase(mContext
							.getResources().getString(R.string.self_user))) {
						items.add(rejectitem);
					}

					MenuItems deleteItem = new MenuItems();
					deleteItem.drawable = mContext.getResources().getDrawable(R.drawable.ic_delete);
					deleteItem.itemName = mContext.getString(R.string.task_menu_delete);
					deleteItem.isCounterVisible = Boolean.FALSE;
					deleteItem.friendUserId = task.fromUser.FromUserId;
					deleteItem.friendUserName = task.fromUser.FromUsername;
					deleteItem.taskId = task.taskId;
					deleteItem.eventId = task.eventId;
					deleteItem.lastStatus = statusView.getText().toString();
					items.add(deleteItem);
					
					MenuItems evernoteItem = new MenuItems();
					evernoteItem.drawable = mContext.getResources().getDrawable(R.drawable.evernote_active);
					if(task.evernoteStatus.equals("0")){
						evernoteItem.itemName = mContext.getString(R.string.task_menu_evernote);
					}else{
						evernoteItem.itemName = mContext.getString(R.string.task_menu_go_to_evernote);
					}
					evernoteItem.evernoteSatus = task.evernoteStatus;
					evernoteItem.isCounterVisible = Boolean.FALSE;
					evernoteItem.isEvernoteVisible = Boolean.FALSE;
					evernoteItem.friendUserId = task.fromUser.FromUserId;
					evernoteItem.friendUserName = task.fromUser.FromUsername;
					evernoteItem.taskDescription = task.Description;
					evernoteItem.imageURI = task.Image;
					evernoteItem.taskId = task.taskId;
					evernoteItem.taskStatus = task.Status;
					items.add(evernoteItem);

					MenuItems reminderItem = new MenuItems();
					reminderItem.drawable = mContext.getResources().getDrawable(R.drawable.bell_active);
					if(task.reminderStatus.equals("0")){
						reminderItem.itemName = mContext.getString(R.string.task_menu_add_reminder);
					}else{
						reminderItem.itemName = mContext.getString(R.string.task_menu_edit_reminder);
						reminderItem.eventId = task.eventId;
					}
					reminderItem.isCounterVisible = Boolean.FALSE;
					reminderItem.isEvernoteVisible = Boolean.FALSE;
					reminderItem.taskId = task.taskId;
					reminderItem.friendUserName = task.fromUser.FromUsername;
					reminderItem.friendUserId = task.fromUser.FromUserId;
					reminderItem.taskDescription = task.Description;
					reminderItem.taskEndDueDate = task.DauEndDate;
					reminderItem.taskStartDueDate = task.DauStartDate;
					reminderItem.taskType = task.Type;
					reminderItem.isRepeated = task.Repeated;
					reminderItem.taskRepeatDueDate = task.RepeatFreq;
					reminderItem.taskEndRepeatDueDate = task.EndRepeat;
					reminderItem.taskStatus = task.Status;
					items.add(reminderItem);
				} else if (task.Status != null&& (task.Status.equalsIgnoreCase(AppConstant.TASK_STATUS_VALUE.COMPLETED))) {
					MenuItems deleteItem = new MenuItems();
					deleteItem.drawable = mContext.getResources().getDrawable(R.drawable.ic_delete);
					deleteItem.itemName = mContext.getString(R.string.task_menu_delete);
					deleteItem.isCounterVisible = Boolean.FALSE;
					deleteItem.friendUserId = task.fromUser.FromUserId;
					deleteItem.friendUserName = task.fromUser.FromUsername;
					deleteItem.taskId = task.taskId;
					deleteItem.eventId = task.eventId;
					deleteItem.lastStatus = statusView.getText().toString();
					items.add(deleteItem);
				} else if (task.Status != null && (task.Status.equalsIgnoreCase(AppConstant.TASK_STATUS_VALUE.REJECTED))) {
					MenuItems acceptitem = new MenuItems();
					acceptitem.drawable = mContext.getResources().getDrawable(R.drawable.ic_accept);
					acceptitem.itemName = mContext.getString(R.string.task_menu_accept);
					acceptitem.isCounterVisible = Boolean.FALSE;
					acceptitem.friendUserName = task.fromUser.FromUsername;
					acceptitem.friendUserId = task.fromUser.FromUserId;
					acceptitem.taskDescription = task.Description;
					acceptitem.taskEndDueDate = task.DauEndDate;
					acceptitem.taskId = task.taskId;
					acceptitem.imageURI = task.Image;
					acceptitem.taskStatus = task.Status;
					items.add(acceptitem);

					MenuItems deleteItem = new MenuItems();
					deleteItem.drawable = mContext.getResources().getDrawable(R.drawable.ic_delete);
					deleteItem.itemName = mContext.getString(R.string.task_menu_delete);
					deleteItem.isCounterVisible = Boolean.FALSE;
					deleteItem.friendUserId = task.fromUser.FromUserId;
					deleteItem.friendUserName = task.fromUser.FromUsername;
					deleteItem.taskId = task.taskId;
					deleteItem.eventId = task.eventId;
					deleteItem.lastStatus = statusView.getText().toString();
					items.add(deleteItem);
				} else if (task.Status != null && (task.Status.equalsIgnoreCase(AppConstant.TASK_STATUS_VALUE.EXPIRED))) {
					MenuItems deleteItem = new MenuItems();
					deleteItem.drawable = mContext.getResources().getDrawable(R.drawable.ic_delete);
					deleteItem.itemName = mContext.getString(R.string.task_menu_delete);
					deleteItem.isCounterVisible = Boolean.FALSE;
					deleteItem.friendUserId = task.fromUser.FromUserId;
					deleteItem.friendUserName = task.fromUser.FromUsername;
					deleteItem.taskId = task.taskId;
					deleteItem.eventId = task.eventId;
					deleteItem.lastStatus = statusView.getText().toString();
					items.add(deleteItem);
				} else {
					CommonUtils.showErrorToast(((Activity) mContext), mContext
							.getString(R.string.option_menu_unavailable));

					mForMeController.removeAndRefreshView(task.taskId);
					mForMeController.doApiCallUpdateTaskStatus(
							
							UserSharedPreferences.getInstance(mContext)
									.getString(AppConstant.USER_ID),
							task.taskId, AppConstant.TASK_STATUS_VALUE.DELETED);
				}
				MenuAdapter adapter = new MenuAdapter(mContext, items);
				mMenuView = new CustomMenuView(mContext, adapter);
				mMenuView.setMenuItemClickListener(mForMeController);
				mMenuView.showMenu(v);
				((HomeActivity) mContext).closeSerchview();
			}
		});
	}
}
