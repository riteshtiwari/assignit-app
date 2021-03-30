package com.assignit.android.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.assignit.android.R;
import com.assignit.android.localstorage.UserSharedPreferences;
import com.assignit.android.pojo.MenuItems;
import com.assignit.android.pojo.TaskFromMe;
import com.assignit.android.ui.activities.HomeActivity;
import com.assignit.android.ui.activities.SplashActivity;
import com.assignit.android.ui.activities.controller.FromMeController;
import com.assignit.android.ui.views.AnimatedTextView;
import com.assignit.android.ui.views.CustomMenuView;
import com.assignit.android.utils.AppConstant;
import com.assignit.android.utils.CommonAsyncTask;
import com.assignit.android.utils.CommonUtils;
import com.assignit.android.utils.StringUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * From me  Adapter class to display ui list of task assigned by me
 * 
 * @author Innoppl
 * 
 */
public class FromMeAdapter extends BaseAdapter {

	Context mContext;

	private List<TaskFromMe> mTaskFromMes;

	private final FromMeController mFromMeController;

	private final ImageLoader imageLoader;
	private final DisplayImageOptions options;
	List<TaskFromMe> taskFromMes;

	public CustomMenuView mMenuView;

	private List<TaskFromMe> filterData = new ArrayList<TaskFromMe>();

	public FromMeAdapter(Context Context, List<TaskFromMe> fromMes, FromMeController controller) {
		super();
		this.mContext = Context;
		this.mTaskFromMes = fromMes;
		this.mFromMeController = controller;
		imageLoader = ImageLoader.getInstance();
		options = SplashActivity.getDisplayImageOptions();
		filterData = mTaskFromMes;

	}

	@Override
	public int getCount() {
		return mTaskFromMes.size();
	}

	@Override
	public Object getItem(int position) {
		return mTaskFromMes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		ViewHolder mHolder;

		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.row_from_melist, null);
			mHolder = new ViewHolder();
			mHolder.taskDescription = (AnimatedTextView) convertView.findViewById(R.id.mTaskNameView);
			mHolder.photoFrame = (ImageView) convertView.findViewById(R.id.mPhotoFrame);
			mHolder.mTaskStatusView = (ImageView) convertView.findViewById(R.id.mTaskStatusView);
			mHolder.dueStatus = (TextView) convertView.findViewById(R.id.mDueDateView);
			mHolder.mAssignedView = (TextView) convertView.findViewById(R.id.mAssignedView);
			mHolder.statusView = (TextView) convertView.findViewById(R.id.mStatusView);
			mHolder.mPhotoFrame = (LinearLayout) convertView.findViewById(R.id.mPhotoLinearLayout);
			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		
		if (mHolder.mPhotoFrame.getVisibility() == View.VISIBLE) {
			mHolder.mPhotoFrame.setVisibility(View.GONE);
		}
		mHolder.taskDescription.setHeight(CommonUtils.convertToDp(mContext, 18));
		mHolder.taskDescription.setSingleLine(true);
		
		setData(mHolder, position);

		return convertView;
	}

	/**
	 * Callback method to update adapter from controller
	 * 
	 * @param taskFromMes
	 */
	public void updateAdapter(List<TaskFromMe> taskFromMes) {
		mTaskFromMes = taskFromMes;
		filterData = taskFromMes;
		notifyDataSetChanged();
	}

	/**
	 * Callback method to get adapter list 
	 * 
	 * @return
	 */
	public List<TaskFromMe> getAdapterList() {
		return mTaskFromMes;
	}

	/**
	 * Set data to list view
	 * 
	 * @param view
	 * @param position
	 */
	private void setData(ViewHolder viewHolder, int position) {

		showMenu(viewHolder, position);

		final TaskFromMe task = mTaskFromMes.get(position);

		if (task.Description != null) {
			TextView taskDescription = viewHolder.taskDescription;
			
			String text = "<font color=#FFA800><b>" + (position + 1) + ":" + "</b></font> <font color=#222222>" + " "
					+ task.Description + "</font>";
			// taskDescription.setText(task.Description);
			taskDescription.setText(Html.fromHtml(text));
			//taskDescription.setSingleLine(Boolean.TRUE);

		}
		if (task.ToUser.ToUsername != null) {
			TextView assignedName = viewHolder.mAssignedView;
			
			boolean isLand = mContext.getResources().getBoolean(R.bool.isLandscape);
			if(!isLand){
				assignedName.setWidth(CommonUtils.convertToDp(mContext, 88));
			}else{
				assignedName.setWidth(CommonUtils.convertToDp(mContext, 300));
			}
			
			if (task.ToUser.ToUserId.equals(UserSharedPreferences.getInstance(mContext).getString(
					AppConstant.LOGIN.USER_ID))) {
				assignedName.setText(mContext.getResources().getString(R.string.self_user));
				task.ToUser.ToUsername = mContext.getResources().getString(R.string.self_user);
				
			} else {
				if (task.ToUser.ToUsername != null) {
					assignedName.setText(task.ToUser.ToUsername);
				} 
			}
		}

		final ImageView photo = viewHolder.photoFrame;
	
		if (StringUtils.isNotBlank(task.Image)) {
			imageLoader.displayImage(task.Image, photo, options);

			photo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					new CommonAsyncTask(mContext).execute(task.Image);
					//new GetImage().execute(task.Image);
				}
			});
		}
		
		if ( mTaskFromMes.get(position).Type != null) {
			TextView dueStatus = viewHolder.dueStatus;
			switch (Integer.parseInt(mTaskFromMes.get(position).Type)) {

				case 1:
					if(mTaskFromMes.get(position).Status.equals(AppConstant.TASK_STATUS_VALUE.EXPIRED)){
						dueStatus.setText(CommonUtils.getFormatedDateMDY(task.DauEndDate));
					}else{
					dueStatus.setText(AppConstant.TASK_DUE_DATE_STATUS.DO_IT_TODAY);
					}
					break;
				case 2:
					if(mTaskFromMes.get(position).Status.equals(AppConstant.TASK_STATUS_VALUE.EXPIRED)){
						dueStatus.setText(CommonUtils.getFormatedDateMDY(task.DauEndDate));
					}else{
					dueStatus.setText(AppConstant.TASK_DUE_DATE_STATUS.DO_IT_ANY_TIME_THIS_WEEK);
					break;}
				case 3:
					dueStatus.setText(CommonUtils.getFormatedDateMDY(task.DauEndDate));
					break;
				case 0:
					if(mTaskFromMes.get(position).Status.equals(AppConstant.TASK_STATUS_VALUE.COMPLETED) && !task.DauEndDate.equals("0")){
						dueStatus.setText(CommonUtils.getFormatedDateMDY(task.DauEndDate));
					}else{
					dueStatus.setText(AppConstant.TASK_DUE_DATE_STATUS.NO_DUE_DATE);
					}
					break;

			}
		}


		TextView statusView = viewHolder.statusView;
		
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
					/*Spannable spannable = (Spannable) statusView.getText();
					spannable.setSpan(STRIKE_THROUGH_SPAN, 0, AppConstant.TASK_STATUS.DELETED.length(),
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);*/
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
	 * Method call on search on the list
	 *  
	 * @return
	 */
	
	public Filter getFilter() {
		Filter filter = new Filter() {

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				mTaskFromMes = (ArrayList<TaskFromMe>) results.values;
				notifyDataSetChanged();

				if (((HomeActivity) mContext).isSearchBoxEnable && !filterData.isEmpty() && mTaskFromMes.isEmpty()) {
					((HomeActivity) mContext).noMatchFoundView.setVisibility(View.VISIBLE);
				} else {
					((HomeActivity) mContext).noMatchFoundView.setVisibility(View.GONE);
				}
			}

			@Override
			protected FilterResults performFiltering(CharSequence charSequence) {
				FilterResults results = new FilterResults();

				// If there's nothing to filter on, return the original data for your list
				if (charSequence == null || charSequence.length() == 0) {
					results.values = filterData;
					results.count = filterData.size();
				} else {
					taskFromMes = new ArrayList<TaskFromMe>();
					String filterString = charSequence.toString().toLowerCase();
					String filterableString;

					for (int i = 0; i < filterData.size(); i++) {
						filterableString = filterData.get(i).ToUser.ToUsername;
						if (filterableString.toLowerCase().contains(filterString)) {
							taskFromMes.add(filterData.get(i));
						}
					}

					results.values = taskFromMes;
					results.count = taskFromMes.size();
				}

				return results;

			}
		};

		return filter;
	}

	/**
	 * Method to Menuview initialisation
	 * 
	 * @param viewHolder
	 * @param position
	 */
	private void showMenu(ViewHolder viewHolder, int position) {
		final TaskFromMe task = mTaskFromMes.get(position);
		ImageView menuView = viewHolder.mTaskStatusView;
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

				} else if (task.Status != null	&& 
					   (task.Status.equalsIgnoreCase(AppConstant.TASK_STATUS_VALUE.COMPLETED)|| 
						task.Status.equalsIgnoreCase(AppConstant.TASK_STATUS_VALUE.COMPLETE)||
						task.Status.equalsIgnoreCase(AppConstant.TASK_STATUS_VALUE.REJECTED)||
						task.Status.equalsIgnoreCase(AppConstant.TASK_STATUS_VALUE.DELETED)||
						task.Status.equalsIgnoreCase(AppConstant.TASK_STATUS_VALUE.EXPIRED))) {

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

				} else {
					CommonUtils.showErrorToast(((Activity) mContext),mContext.getString(R.string.option_menu_unavailable));
				}

				MenuAdapter adapter = new MenuAdapter(mContext, items);
				mMenuView = new CustomMenuView(mContext, adapter);
				mMenuView.setMenuItemClickListener(mFromMeController);
				mMenuView.showMenu(v);
				((HomeActivity) mContext).closeSerchview();
			}
		});
	}

	class ViewHolder {
		TextView statusView, dueStatus, mAssignedView;
		AnimatedTextView taskDescription;
		ImageView photoFrame, mTaskStatusView;
		LinearLayout mPhotoFrame;
	}
}
