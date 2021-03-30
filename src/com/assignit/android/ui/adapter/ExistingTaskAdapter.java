package com.assignit.android.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.assignit.android.R;
import com.assignit.android.localstorage.UserSharedPreferences;
import com.assignit.android.pojo.MenuItems;
import com.assignit.android.pojo.TaskFromMe;
import com.assignit.android.ui.activities.SplashActivity;
import com.assignit.android.ui.views.CustomMenuView;
import com.assignit.android.ui.views.CustomMenuView.OnMenuItemClickListener;
import com.assignit.android.ui.views.CustomTextView;
import com.assignit.android.utils.AppConstant;
import com.assignit.android.utils.CommonAsyncTask;
import com.assignit.android.utils.CommonUtils;
import com.assignit.android.utils.StringUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Exsting task Adapter
 * 
 * @author Innoppl
 * 
 */
public class ExistingTaskAdapter extends BaseAdapter {

	Activity mContext;
	
	private List<TaskFromMe> taskFromMesList;
	
	TextView taskDescriptionTextView;
	
	private final ImageLoader imageLoader;

	private final DisplayImageOptions displayImageOptions;
	

	/**
	 * COnstructor for adapter
	 * 
	 * @param Context
	 * @param fromMes
	 */
	public ExistingTaskAdapter(Activity Context, List<TaskFromMe> fromMes) {
		super();
		this.mContext = Context;
		this.taskFromMesList = fromMes;
		imageLoader = ImageLoader.getInstance();
		displayImageOptions = SplashActivity.getDisplayImageOptions();
	}

	@Override
	public int getCount() {
		return taskFromMesList.size();
	}

	@Override
	public Object getItem(int position) {
		return taskFromMesList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		View view = null;

		if (convertView == null) {
			view = View.inflate(mContext, R.layout.row_from_melist, null);
		} else {
			view = convertView;
		}
		
		taskDescriptionTextView = (TextView) view.findViewById(R.id.mTaskNameView);
		taskDescriptionTextView.setHeight(CommonUtils.convertToDp(mContext, 18));
		taskDescriptionTextView.setSingleLine(true);
		View photo = view.findViewById(R.id.mPhotoLinearLayout);
		if (photo.getVisibility() == View.VISIBLE) {
			photo.setVisibility(View.GONE);
		} 

	

		setData(view, position);

		return view;
	}

	/**
	 * callback method to update from me list from controller
	 * 
	 * @param taskFromMes
	 */
	public void updateAdapter(List<TaskFromMe> taskFromMes) {
		taskFromMesList = taskFromMes;
		notifyDataSetChanged();
	}

	/**
	 * Callback method to get existing task list from adapter
	 * 
	 * @return
	 */
	public List<TaskFromMe> getAdapterList() {
		return taskFromMesList;
	}

	/**
	 * Set data to list view
	 * 
	 * @param view
	 * @param position
	 */
	private void setData(View view, int position) {

		showMenu(view, position);
		CustomTextView dueStatus = (CustomTextView) view.findViewById(R.id.mDueDateView);
		final TaskFromMe task = taskFromMesList.get(position);
		if (task.Description != null) {
			
			taskDescriptionTextView.setHeight(CommonUtils.convertToDp(mContext, 18));
			String text = "<font color=#FFA800><b>" + (position + 1) + ":" + "</b></font> <font color=#222222>" + " "
					+ task.Description + "</font>";
			taskDescriptionTextView.setText(Html.fromHtml(text));
			//taskDescription.setSingleLine(Boolean.TRUE);
		}
		if (task.ToUser.ToUsername != null) {
			CustomTextView assignedName = (CustomTextView) view.findViewById(R.id.mAssignedView);
			boolean isLand = mContext.getResources().getBoolean(R.bool.isLandscape);
			if(!isLand){
				assignedName.setWidth(CommonUtils.convertToDp(mContext, 88));
			}else{
				assignedName.setWidth(CommonUtils.convertToDp(mContext, 300));
			}
			if (task.ToUser.ToUserId.equals(UserSharedPreferences.getInstance(mContext).getString(
					AppConstant.LOGIN.USER_ID))) {
				assignedName.setText(mContext.getResources().getString(R.string.self_user));
			} else {
				if (task.ToUser.ToUsername != null) {
					assignedName.setText(task.ToUser.ToUsername);
				} 
			}
		}

		ImageView photo = (ImageView) view.findViewById(R.id.mPhotoFrame);

		if (StringUtils.isNotBlank(task.Image)) {
			imageLoader.displayImage(task.Image, photo, displayImageOptions);
			photo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					new CommonAsyncTask(mContext).execute(task.Image);
				}
			});
		}
		if ( taskFromMesList.get(position).Type != null) {
			switch (Integer.parseInt(taskFromMesList.get(position).Type)) {

				case 1:
					if(taskFromMesList.get(position).Status.equals(AppConstant.TASK_STATUS_VALUE.EXPIRED)){
						dueStatus.setText(CommonUtils.getFormatedDateMDY(task.DauEndDate));
					}else{
					dueStatus.setText(AppConstant.TASK_DUE_DATE_STATUS.DO_IT_TODAY);
					}
					break;
				case 2:
					if(taskFromMesList.get(position).Status.equals(AppConstant.TASK_STATUS_VALUE.EXPIRED)){
						dueStatus.setText(CommonUtils.getFormatedDateMDY(task.DauEndDate));
					}else{
					dueStatus.setText(AppConstant.TASK_DUE_DATE_STATUS.DO_IT_ANY_TIME_THIS_WEEK);
					break;}
				case 3:
					dueStatus.setText(CommonUtils.getFormatedDateMDY(task.DauEndDate));
					break;
				case 0:
					if(taskFromMesList.get(position).Status.equals(AppConstant.TASK_STATUS_VALUE.COMPLETED) && !task.DauEndDate.equals("0")){
						dueStatus.setText(CommonUtils.getFormatedDateMDY(task.DauEndDate));
					}else{
					dueStatus.setText(AppConstant.TASK_DUE_DATE_STATUS.NO_DUE_DATE);
					}
					
					break;

			}
		}

		
	

		CustomTextView statusView = (CustomTextView) view.findViewById(R.id.mStatusView);
		if (task.Status != null) {
			Integer status = Integer.parseInt(task.Status);
			switch (status) {
			case 1:
				statusView.setText(AppConstant.TASK_STATUS.ACCEPTED);
				statusView.setTextColor(mContext.getResources().getColor(R.color.color_accepted));
				break;

			case 2:
				statusView.setText(AppConstant.TASK_STATUS.IN_PROGRESS);
				statusView.setTextColor(mContext.getResources().getColor(R.color.color_inprogress));
				break;
			case 5:
				statusView.setText(AppConstant.TASK_STATUS.REJECTED);
				statusView.setTextColor(mContext.getResources().getColor(R.color.color_rejected));
				break;
			case 3:
				statusView.setText(AppConstant.TASK_STATUS.COMPLETED);
				statusView.setTextColor(mContext.getResources().getColor(R.color.color_copeleted));
				break;
			case 7:
				statusView.setText(AppConstant.TASK_STATUS.COMPLETED);
				statusView.setTextColor(mContext.getResources().getColor(R.color.color_copeleted));
				break;

			case 6:
				//StrikethroughSpan STRIKE_THROUGH_SPAN = new StrikethroughSpan();

				statusView.setText(AppConstant.TASK_STATUS.REJECTED, TextView.BufferType.SPANNABLE);
				//Spannable spannable = (Spannable) statusView.getText();
				//spannable.setSpan(STRIKE_THROUGH_SPAN, 0, AppConstant.TASK_STATUS.DELETED.length(),
				//		Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				statusView.setTextColor(mContext.getResources().getColor(R.color.color_rejected));
				break;

			case 4:
				statusView.setText(AppConstant.TASK_STATUS.EXPIRED);
				statusView.setTextColor(mContext.getResources().getColor(R.color.color_expired));
				break;
			case 0:
				statusView.setText(AppConstant.TASK_STATUS.NEW);
				statusView.setTextColor(mContext.getResources().getColor(R.color.color_new));
				break;
		}
		}
	}

	/**
	 * Load data for menu view and show
	 * 
	 * @param view
	 * @param position
	 */
	private void showMenu(View view, int position) {
		final TaskFromMe task = taskFromMesList.get(position);
		ImageView menuView = (ImageView) view.findViewById(R.id.mTaskStatusView);
		menuView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				List<MenuItems> items = new ArrayList<MenuItems>();

				if (task.Status != null
						&& (task.Status.equalsIgnoreCase(AppConstant.TASK_STATUS_VALUE.NEW)
								|| task.Status.equalsIgnoreCase(AppConstant.TASK_STATUS_VALUE.ACCEPTED) || task.Status
								.equalsIgnoreCase(AppConstant.TASK_STATUS_VALUE.IN_PROGRESS))) {

					MenuItems edititem = new MenuItems();
					edititem.drawable = mContext.getResources().getDrawable(R.drawable.ic_edit);
					edititem.itemName = mContext.getString(R.string.task_menu_edit);
					edititem.isCounterVisible = Boolean.FALSE;
					edititem.friendUserName = task.ToUser.ToUsername;
					edititem.friendUserId = task.ToUser.ToUserId;
					edititem.taskDescription = task.Description;
					
					
					edititem.taskEndDueDate = task.DauEndDate;
					edititem.taskStartDueDate = task.DauStartDate;
					edititem.taskRepeatDueDate = task.RepeatFreq;
					edititem.taskEndRepeatDueDate = task.EndRepeat;
					edititem.isRepeated = task.Repeated;
					
					edititem.taskType = task.Type;
					edititem.taskStatus = task.Status;
					edititem.taskId = task.taskId;
					edititem.imageURI = task.Image;
					edititem.taskStatus = task.Status;
					items.add(edititem);

					MenuItems reminderItem = new MenuItems();
					reminderItem.drawable = mContext.getResources().getDrawable(R.drawable.ic_reminder);
					reminderItem.itemName = mContext.getString(R.string.task_menu_reminder);
					reminderItem.isCounterVisible = Boolean.FALSE;
					reminderItem.friendUserId = task.ToUser.ToUserId;
					reminderItem.friendUserName = task.ToUser.ToUsername;
					reminderItem.taskId = task.taskId;
					reminderItem.taskType = task.Type;
					reminderItem.taskStatus = task.Status;
					items.add(reminderItem);

					MenuItems deleteItem = new MenuItems();
					deleteItem.drawable = mContext.getResources().getDrawable(R.drawable.ic_delete);
					deleteItem.itemName = mContext.getString(R.string.task_menu_delete);
					deleteItem.isCounterVisible = Boolean.FALSE;
					deleteItem.friendUserId = task.ToUser.ToUserId;
					deleteItem.taskId = task.taskId;
					deleteItem.friendUserName = task.ToUser.ToUsername;
					items.add(deleteItem);

				} else if (task.Status != null
						&& (task.Status.equalsIgnoreCase(AppConstant.TASK_STATUS_VALUE.REJECTED) || task.Status
								.equalsIgnoreCase(AppConstant.TASK_STATUS_VALUE.COMPLETED))) {

					MenuItems edititem = new MenuItems();
					edititem.drawable = mContext.getResources().getDrawable(R.drawable.ic_edit);
					edititem.itemName = mContext.getString(R.string.task_menu_edit);
					edititem.isCounterVisible = Boolean.FALSE;
					edititem.friendUserName = task.ToUser.ToUsername;
					edititem.friendUserId = task.ToUser.ToUserId;
					edititem.taskDescription = task.Description;
					edititem.taskEndDueDate = task.DauEndDate;
					edititem.taskStartDueDate = task.DauStartDate;
					edititem.taskRepeatDueDate = task.RepeatFreq;
					edititem.taskEndRepeatDueDate = task.EndRepeat;
					edititem.isRepeated = task.Repeated;
					edititem.taskType = task.Type;
					edititem.taskStatus = task.Status;
					edititem.taskId = task.taskId;
					edititem.imageURI = task.Image;
					edititem.taskStatus = task.Status;
					items.add(edititem);

					MenuItems deleteItem = new MenuItems();
					deleteItem.drawable = mContext.getResources().getDrawable(R.drawable.ic_delete);
					deleteItem.itemName = mContext.getString(R.string.task_menu_delete);
					deleteItem.isCounterVisible = Boolean.FALSE;
					deleteItem.friendUserId = task.ToUser.ToUserId;
					deleteItem.friendUserName = task.ToUser.ToUsername;
					deleteItem.taskId = task.taskId;
					items.add(deleteItem);

				} else if (task.Status != null && (task.Status.equalsIgnoreCase(AppConstant.TASK_STATUS_VALUE.EXPIRED))) {
					MenuItems edititem = new MenuItems();
					edititem.drawable = mContext.getResources().getDrawable(R.drawable.ic_edit);
					edititem.itemName = mContext.getString(R.string.task_menu_edit);
					edititem.isCounterVisible = Boolean.FALSE;
					edititem.friendUserId = task.ToUser.ToUserId;
					edititem.friendUserName = task.ToUser.ToUsername;
					edititem.friendUserId = task.ToUser.ToUserId;
					edititem.taskDescription = task.Description;
					edititem.taskEndDueDate = task.DauEndDate;
					edititem.taskStartDueDate = task.DauStartDate;
					edititem.taskRepeatDueDate = task.RepeatFreq;
					edititem.taskEndRepeatDueDate = task.EndRepeat;
					edititem.isRepeated = task.Repeated;
					edititem.taskType = task.Type;
					edititem.taskStatus = task.Status;
					edititem.taskId = task.taskId;
					edititem.imageURI = task.Image;
					edititem.taskStatus = task.Status;
					items.add(edititem);

					MenuItems deleteItem = new MenuItems();
					deleteItem.drawable = mContext.getResources().getDrawable(R.drawable.ic_delete);
					deleteItem.itemName = mContext.getString(R.string.task_menu_delete);
					deleteItem.isCounterVisible = Boolean.FALSE;
					deleteItem.friendUserId = task.ToUser.ToUserId;
					deleteItem.friendUserName = task.ToUser.ToUsername;
					deleteItem.taskId = task.taskId;
					items.add(deleteItem);
				} else {
					CommonUtils.showErrorToast((mContext), mContext.getString(R.string.option_menu_unavailable));
				}

				MenuAdapter adapter = new MenuAdapter(mContext, items);
				CustomMenuView menuView = new CustomMenuView(mContext, adapter);
				menuView.setMenuItemClickListener((OnMenuItemClickListener) mContext);
				menuView.showMenu(v);

			}
		});
	}
}
