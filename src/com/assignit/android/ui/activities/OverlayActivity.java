package com.assignit.android.ui.activities;

import java.util.ArrayList;
import java.util.List;

import org.taptwo.android.widget.ViewFlow;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.assignit.android.R;
import com.assignit.android.localstorage.UserSharedPreferences;
import com.assignit.android.net.WebServiceManager;
import com.assignit.android.net.responsebean.FriendUploadRespose;
import com.assignit.android.ui.adapter.OverlayAdapter;
import com.assignit.android.ui.views.CustomButton;
import com.assignit.android.ui.views.CustomProgressbar;
import com.assignit.android.utils.AppConstant;
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

/**
 * This is overlay activity class that provide the help screen and redirect to login screen.
 * 
 * @author Innoppl
 * 
 */
@EActivity(R.layout.activity_overlay)
public class OverlayActivity extends Activity implements ViewFlow.ViewSwitchListener {

	@ViewById
	public ViewFlow mViewFlow;

	@ViewById
	CustomButton btnPrevious;

	@ViewById
	CustomButton btnNext;
	
	@ViewById
	CustomButton btnSkipTutorial;
	
	MobileContactsUtils contactUtils;
	
	private int mPosition = 0;

	OverlayAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		boolean overlayStatus = UserSharedPreferences.getInstance(this).getBoolean(
				UserSharedPreferences.KEY_OVERLAY_STATUS);
		if (overlayStatus) {
			Intent intent = new Intent(this, HomeActivity_.class);
			startActivity(intent);
			finish();
		}
	}

	/**
	 * Method to call on view initialisation
	 */
	@AfterViews
	public void initializeView() {
		//startService(new Intent(this, ContactService.class));
		List<String> list = new ArrayList<String>();
		list.add("");
		list.add("");
		list.add("");
		adapter = new OverlayAdapter(this, list);
		mViewFlow.setAdapter(adapter, 0);
		mViewFlow.setOnViewSwitchListener(this);
		contactUtils = new MobileContactsUtils(this);
		String userId = UserSharedPreferences.getInstance(this).getString(AppConstant.USER_ID);
		Boolean isUploaded = UserSharedPreferences.getInstance(this).getBoolean(AppConstant.ISCONTACTUPLOADED);
		if(!isUploaded){
		if (StringUtils.isNotBlank(userId) && CommonUtils.isNetworkAvailable(this)) {
			CustomProgressbar.showProgressBar(this, false,this.getResources().getString(R.string.loading_updateing_contacts));
			UserSharedPreferences.getInstance(this).putBoolean(AppConstant.ISCONTACTUPLOADED,Boolean.TRUE);
			uploadContacts(userId,"0");
		}}
	}

	/**
	 * This method to Upload contact details in background
	 * 
	 * @param userId
	 * @param isUpdated
	 */
	@Background
	public void uploadContacts(String userId,String isUpdated) {
		
		FriendUploadRespose response = null;
		response = WebServiceManager.getInstance(this).uploadFriendDetails(contactUtils.getContactMobileContact(), userId,isUpdated);
		afterUpload(response);
	}
	
	/**
	 * After upload contacts
	 * @param response
	 */
	@UiThread
	public void afterUpload(FriendUploadRespose response) {
		if(response!=null){
		if(StringUtils.isNotBlank(response.message)){
		if (ValidationUtils.isSuccessResponse(response)) {
			Log.e("upload contacts", ""+response.httpStatusCode);
		}
		}}
		CustomProgressbar.hideProgressBar();
	}
	
	/* Method call on configuration change 
	 * android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == 1)
			this.setRequestedOrientation(-1);
		else
			this.setRequestedOrientation(newConfig.orientation);
	}

	/**
	 * This method calls on previous button
	 */
	@Click
	public void btnPrevious() {
		mViewFlow.setAdapter(adapter, mPosition - 1);
	}

	/**
	 * This method calls on skip tutorial button click
	 */
	@Click
	public void btnSkipTutorial() {
		UserSharedPreferences.getInstance(OverlayActivity.this).putBoolean(UserSharedPreferences.KEY_OVERLAY_STATUS,Boolean.TRUE);
		Intent intent = new Intent(OverlayActivity.this, HomeActivity_.class);
		startActivity(intent);
		finish();
	}

	/**
	 * This method calls next button click
	 */
	@Click
	public void btnNext() {
		mViewFlow.setAdapter(adapter, mPosition + 1);
	}

	/*
	 * org.taptwo.android.widget.ViewFlow.ViewSwitchListener#onSwitched(android.view.View,int)
	 */
	@Override
	public void onSwitched(View view, int position) {
		mPosition = position;
		if (position == 0) {
			btnSkipTutorial.setBackgroundResource(R.drawable.selector_btn_skip);
			btnPrevious.setVisibility(View.GONE);
			btnNext.setVisibility(View.VISIBLE);
		} else if (position == 1) {
			btnSkipTutorial.setBackgroundResource(R.drawable.selector_btn_skip);
			btnPrevious.setVisibility(View.VISIBLE);
			btnNext.setVisibility(View.VISIBLE);
		} else if (position == 2) {
			btnSkipTutorial.setBackgroundResource(R.drawable.selector_btn_done);
			btnPrevious.setVisibility(View.VISIBLE);
			btnNext.setVisibility(View.GONE);
		}
	}
}
