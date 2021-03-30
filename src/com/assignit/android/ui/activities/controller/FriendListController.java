package com.assignit.android.ui.activities.controller;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.assignit.android.R;
import com.assignit.android.localstorage.UserSharedPreferences;
import com.assignit.android.net.WebServiceManager;
import com.assignit.android.net.responsebean.FriendDetailsRespose;
import com.assignit.android.net.responsebean.FriendUploadRespose;
import com.assignit.android.pojo.Friends;
import com.assignit.android.pojo.MenuItems;
import com.assignit.android.ui.activities.ComposeTaskActivity_;
import com.assignit.android.ui.activities.ExistingTaskActivity_;
import com.assignit.android.ui.activities.HomeActivity;
import com.assignit.android.ui.adapter.FriendListAdapter;
import com.assignit.android.ui.views.CommonAlertDialog;
import com.assignit.android.ui.views.CustomMenuView.OnMenuItemClickListener;
import com.assignit.android.ui.views.CustomProgressbar;
import com.assignit.android.utils.AppConstant;
import com.assignit.android.utils.CommonUtils;
import com.assignit.android.utils.StringUtils;
import com.assignit.android.utils.ValidationUtils;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

/**
 * This class is represent the Home screen(My Friends Tab).It's controlling the
 * view flow and business logic.
 * 
 * @author Innoppl
 * 
 */
@EBean
public  class FriendListController implements OnMenuItemClickListener {

	@RootContext
	public HomeActivity mActivity;
	
	public List<Friends> FinalFriendList;
	
	public List<Friends> spinnerList;

	public FriendListAdapter adapter;

	String isUploadedFirst = "0";
	
	String isUploadedNext = "1";
	
	@ViewById
	ListView mFriendsListView;

	@ViewById
	ListView mFromMeListView;

	@ViewById
	ListView mForMeListView;
	
	// to hide and show selected tab 
	public static int selectedListItem = -1;
	
	/**
	 * upload contact list 
	 */
	public void uploadFriendList() {
		
		if (CommonUtils.isNetworkAvailable(mActivity)) {
			updateFriends();
		} else {
			CommonUtils.showErrorToast(mActivity,mActivity.getResources().getString(R.string.no_internate_connetion));
		}
	}
	
	/**
	 * sending offline updated contacts to the server 
	 */
	private void updateFriends(){

		String userId = UserSharedPreferences.getInstance(mActivity).getString(AppConstant.USER_ID);
		List<JSONArray> updatedContact = UserSharedPreferences.getInstance(mActivity).getArray();
		
		if(updatedContact.size()>0){
			JSONArray array = new JSONArray();
		for(JSONArray jsonArray : updatedContact){
			String tempJson = jsonArray.toString().replace("[","").replace("]","");
			try {
				JSONObject jsonObject = new JSONObject(tempJson);
				array.put(jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if(array.length()>0){
			uploadContacts(array,userId,isUploadedNext);
		}
		
		UserSharedPreferences.getInstance(mActivity).removearray();
		}
	}
	
	/**
     * this method is background service call to send contacts to server
	 * @param phoneBookFriends
	 * @param userId
	 * @param isUpdated
	 */
	@Background
	public void uploadContacts(JSONArray phoneBookFriends, String userId,String isUpdated) {
		
		
		FriendUploadRespose response = null;
		
		response = WebServiceManager.getInstance(mActivity).uploadFriendDetails(phoneBookFriends, userId,isUpdated);
		
		afterUpload(response);
	}
	/**
     * this method is called to show dialog box on after uploading contacts
	 * @param response
	 */
	@UiThread
	public void afterUpload(FriendUploadRespose response) {
		if(response!=null){
		if(StringUtils.isNotBlank(response.message)){
		CommonAlertDialog customAlertDialog = new CommonAlertDialog(mActivity);
		if (ValidationUtils.isSuccessResponse(response)) {
			Log.e("show status of offline update", ""+response.httpStatusCode);
			//customAlertDialog.createDefaultDialog(mActivity.getResources().getString(R.string.dialog_box_record_update),response.message);
		}else if(ValidationUtils.isParameterMissingResponse(response)){
			customAlertDialog.createDefaultDialog(mActivity.getResources().getString(R.string.dialog_box_error_title),response.message);
		}else if(ValidationUtils.isParameterEmptyResponse(response)){
			customAlertDialog.createDefaultDialog(mActivity.getResources().getString(R.string.dialog_box_error_title),response.message);
		}else if(ValidationUtils.isInternalErrorResponse(response)){
			customAlertDialog.createDefaultDialog(mActivity.getResources().getString(R.string.dialog_box_error_title),response.message);
		}else if(ValidationUtils.isUnknownResponse(response)){
			customAlertDialog.createDefaultDialog(mActivity.getResources().getString(R.string.dialog_box_error_title),response.message);
		}else {
			//CommonUtils.showErrorToast(mActivity, response.errorMessage);
		}}}else{
			CommonUtils.showErrorToast(mActivity, mActivity.getResources().getString(R.string.no_internate_connetion));
		}
		CustomProgressbar.hideProgressBar();
		
	}
	/**
	 * Open Home screen.
	 */
	public void openFriendListScreen() {

		if (mFriendsListView != null && mFromMeListView != null && mForMeListView != null) {
			mFriendsListView.setVisibility(View.VISIBLE);
			mFromMeListView.setVisibility(View.GONE);
			mForMeListView.setVisibility(View.GONE);
		}
		
		if (mActivity.etSearchBox.getText().toString().length()>0) {
			adapter.getFilter().filter(mActivity.etSearchBox.getText().toString());
		} else {
			adapter.getFilter().filter("");
		}
	}
	

	/**
	 * Callback method called when initializing the view.
	 */
	@AfterViews
	public void viewInitialise() {
		
		FinalFriendList = new ArrayList<Friends>();
		spinnerList = new ArrayList<Friends>();
		mFriendsListView.setVisibility(View.VISIBLE);
		mFromMeListView.setVisibility(View.GONE);
		mForMeListView.setVisibility(View.GONE);
		
		View headerFooter = mActivity.getLayoutInflater().inflate(R.layout.friends_footer_view, null);
		//mFriendsListView.addHeaderView(headerFooter);
		mFriendsListView.addFooterView(headerFooter);
		
		adapter = new FriendListAdapter(mActivity, FinalFriendList, this);
		mFriendsListView.setAdapter(adapter);
		
	}
	/**
	 * This ui thread method of Annotation api.
	 * @param friendsType
	 */
	@UiThread
	public void getFriendList(int friendsType) {
		
		FinalFriendList.clear();
		String userId = UserSharedPreferences.getInstance(mActivity).getString(AppConstant.USER_ID);
		if (CommonUtils.isNetworkAvailable(mActivity)) {
			CustomProgressbar.showProgressBar(mActivity, false,mActivity.getResources().getString(R.string.loading_friend_list_message));
			getFriendDetails(userId,Integer.toString(friendsType));
		} else {
			CustomProgressbar.hideProgressBar();
			CommonUtils.showErrorToast(mActivity, mActivity.getResources().getString(R.string.no_internate_connetion));
		}
	}
	
	/**
	 * This ui thread method of Annotation api.
	 * @param friendsType
	 */
	@UiThread
	public void refreshFriendList(int friendsType) {
		FinalFriendList.clear();
		String userId = UserSharedPreferences.getInstance(mActivity).getString(AppConstant.USER_ID);
		if (CommonUtils.isNetworkAvailable(mActivity)) {
			CustomProgressbar.showProgressBar(mActivity, false,mActivity.getResources().getString(R.string.refresh_friend_list_message));
			updateFriends();
			getFriendDetails(userId,Integer.toString(friendsType));
		} else {
			CustomProgressbar.hideProgressBar();
			CommonUtils.showErrorToast(mActivity, mActivity.getResources().getString(R.string.no_internate_connetion));
		}
	}
	
	public void hideMenu(){
		if (adapter.mMenuView != null) {
			adapter.mMenuView.dismisView();
		}
	}
	
	/**
	 * This method is used to get friend details from local server
	 * @param userId
	 * @param friend_type
	 */
	@Background
	public void getFriendDetails(String userId,String friend_type) {
				
		FriendDetailsRespose response = null;
		
		if (userId != null) {
			response = WebServiceManager.getInstance(mActivity).getFriendDetails(userId, friend_type);
		} else {
			CustomProgressbar.hideProgressBar();
			CommonUtils.showErrorToast(mActivity, mActivity.getResources().getString(R.string.no_friends_found));
		}
		if(response!=null && friend_type.equals("0")){
			spinnerList.clear();
			spinnerList.addAll(response.friends);
		}
		afterGetFriendDetails(response);
	}

	/**
	 * insert friends data into friends list
	 * 
	 * @param response
	 */
	@UiThread
	public void afterGetFriendDetails(FriendDetailsRespose response) {
		
		if (adapter.mMenuView != null) {
			adapter.mMenuView.dismisView();
		}
		if (response != null) {
		if (ValidationUtils.isSuccessResponse(response)) {
			
			FinalFriendList.clear();
			FinalFriendList.addAll(response.friends);
			Log.e("total friends", "------"+response.friends.size());
			adapter.setData(FinalFriendList);
			adapter.notifyDataSetChanged();
			mFriendsListView.setTextFilterEnabled(true);

			if (mActivity.etSearchBox.getText().toString().length()>0) {
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
	 * This is callback method.It's called when option menu clicked.
	 */
	@Override
	public void onItemClick(MenuItems menuItem) {
		if (menuItem.itemName.equals(mActivity.getResources().getString(R.string.menu_new_task))) {
			Intent intent = new Intent(mActivity, ComposeTaskActivity_.class);
			intent.putExtra(AppConstant.COMPOSE_TASK.USER_ID, menuItem.userId);
			intent.putExtra(AppConstant.COMPOSE_TASK.USER_NAME, menuItem.userName);
			intent.putExtra(AppConstant.COMPOSE_TASK.EDIT_ENABLE, Boolean.FALSE);
			
			MenuItems menuItem2 = new MenuItems();
			menuItem2.frindsList = FinalFriendList;
			
			Bundle bundle = new Bundle();
			bundle.putSerializable(AppConstant.COMPOSE_TASK.MENU_ITEM_OBJECT, menuItem2);
			intent.putExtras(bundle);
			mActivity.startActivityForResult(intent, HomeActivity.COMPOSE_TASK_REQUEST);
		} else if (menuItem.itemName.equals(mActivity.getResources().getString(R.string.menu_existing_task))) {

			
			
			Intent intent = new Intent(mActivity, ExistingTaskActivity_.class);
			intent.putExtra(AppConstant.COMPOSE_TASK.USER_ID, menuItem.userId);
			intent.putExtra(AppConstant.COMPOSE_TASK.USER_NAME, menuItem.userName);
			intent.putExtra(AppConstant.COMPOSE_TASK.CONTACT_ID, menuItem.contactId);

			MenuItems menuItem2 = new MenuItems();
			menuItem2.frindsList = mActivity.mFriendListController.FinalFriendList;
			
			Bundle bundle = new Bundle();
			bundle.putSerializable(AppConstant.COMPOSE_TASK.MENU_ITEM_OBJECT, menuItem2);
			intent.putExtras(bundle);
			mActivity.startActivity(intent);

		}
	}
	/**
     * this method is called on sharing assingit app using intent
	 */
	public void onShareClick() {
	    Resources resources = mActivity.getResources();
	    String playStoreLinkText = "<a href='http://play.google.com/store/apps/details?id=com.assignit.android&hl=en'> https://play.google.com/store/apps/details?id=com.assignit.android&hl=en</a>";
	    Intent emailIntent = new Intent();
	    emailIntent.setAction(Intent.ACTION_SEND);
	    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(mActivity.getString(R.string.email_invite_text) +" "+ playStoreLinkText +" "+ mActivity.getString(R.string.email_invite_text_end)));
	    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mActivity.getString(R.string.email_invite_subject));
	    emailIntent.setType("message/rfc822");

	    PackageManager pm = mActivity.getPackageManager();
	    Intent sendIntent = new Intent(Intent.ACTION_SEND);     
	    sendIntent.setType("text/plain");


	    Intent openInChooser = Intent.createChooser(emailIntent, resources.getString(R.string.share_chooser_text));

	    List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
	    List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();        
	    for (int i = 0; i < resInfo.size(); i++) {
	        // Extract the label, append it, and repackage it in a LabeledIntent
	        ResolveInfo ri = resInfo.get(i);
	        String packageName = ri.activityInfo.packageName;
	        if(packageName.contains("android.email")) {
	            emailIntent.setPackage(packageName);
	        } else if(packageName.contains("android") || packageName.contains("facebook") || packageName.contains("whatsapp")) {
	            Intent intent = new Intent();
	            intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
	            intent.setAction(Intent.ACTION_SEND);
	            intent.setType("text/plain");
	               	 if(packageName.contains("twitter")||packageName.contains("whatsapp")) {
	            		 intent.putExtra(android.content.Intent.EXTRA_TEXT, mActivity.getString(R.string.msg_invite_text)+" "+ mActivity.getString(R.string.play_store_link));
	                 } else if(packageName.contains("facebook")) {
	                	 intent.putExtra(android.content.Intent.EXTRA_TEXT, mActivity.getString(R.string.play_store_link));
	                 } else if(packageName.contains("mms")) {
	                	 intent.putExtra(android.content.Intent.EXTRA_TEXT, mActivity.getString(R.string.msg_invite_text)+" "+ mActivity.getString(R.string.play_store_link));
	                 } else if(packageName.contains("android.gm")) {
	                intent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(mActivity.getString(R.string.email_invite_text) +" "+ playStoreLinkText +" "+ mActivity.getString(R.string.email_invite_text_end)));
	                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, mActivity.getString(R.string.email_invite_subject));               
	                intent.setType("message/rfc822");
	            }else{
	            	intent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(mActivity.getString(R.string.msg_invite_text))+" "+mActivity.getString(R.string.play_store_link));
	            }
	            intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
	        }
	    }
	    // convert intentList to array
	    LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);

	    openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
	    mActivity.startActivity(openInChooser);       
	}
	/**
     * this method is called on friends list item  selected
	 * @param position
	 */
	@ItemClick
	public void mFriendsListView(int position) {
		if (position == FinalFriendList.size()) {
			onShareClick();
		}else{
			if(selectedListItem == position){
				selectedListItem = -1;
				adapter.notifyDataSetChanged();
			}else{
				selectedListItem = position;
				adapter.notifyDataSetChanged();
			}
		}
	}
}
