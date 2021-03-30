package com.assignit.android.ui.activities;

import java.util.ArrayList;
import java.util.List;

import org.taptwo.android.widget.ViewFlow;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import com.assignit.android.R;
import com.assignit.android.localstorage.UserSharedPreferences;
import com.assignit.android.ui.adapter.OverlayAdapter;
import com.assignit.android.ui.views.CustomButton;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

/**
 * This is overlay activity class that provide the help screen and redirect to login screen.
 * 
 * @author Innoppl
 * 
 */
@EActivity(R.layout.activity_overlay)
public class LoginOverlayActivity extends Activity implements ViewFlow.ViewSwitchListener {

	@ViewById
	public ViewFlow mViewFlow;

	@ViewById
	CustomButton btnPrevious;

	@ViewById
	CustomButton btnNext;
	
	@ViewById
	CustomButton btnSkipTutorial;

	OverlayAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		boolean overlayStatus = UserSharedPreferences.getInstance(this).getBoolean(
				UserSharedPreferences.KEY_LOGIN_OVERLAY_STATUS);
		if (overlayStatus) {
			Intent intent = new Intent(this, RegistrationActivity_.class);
			startActivity(intent);
			finish();

		}
	}

	/**
	 * View initialisation
	 */
	@AfterViews
	public void initializeView() {
		btnSkipTutorial.setBackgroundResource(R.drawable.selector_btn_gotit);
		btnPrevious.setVisibility(View.GONE);
		btnNext.setVisibility(View.GONE);
		
		
		List<String> list = new ArrayList<String>();
		list.add("");
		
		adapter = new OverlayAdapter(this, list);
		mViewFlow.setAdapter(adapter, 0);
		mViewFlow.setOnViewSwitchListener(this);
	}

	/* Call on configuration change
	 * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
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
     * this method is called on got it button press
     */
	@Click
	public void btnSkipTutorial() {
		UserSharedPreferences.getInstance(LoginOverlayActivity.this).putBoolean(UserSharedPreferences.KEY_LOGIN_OVERLAY_STATUS,
				Boolean.TRUE);
		Intent intent = new Intent(LoginOverlayActivity.this, RegistrationActivity_.class);
		startActivity(intent);
		finish();
	}

	

	@Override
	public void onSwitched(View view, int position) {
		//
	}
}
