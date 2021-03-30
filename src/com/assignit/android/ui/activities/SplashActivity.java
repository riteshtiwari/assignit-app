package com.assignit.android.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.assignit.android.R;
import com.assignit.android.localstorage.UserSharedPreferences;
import com.assignit.android.ui.views.CustomViewConstants;
import com.assignit.android.utils.AppConstant;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * This is app lunching screen activity.
 * 
 * @author Innoppl
 * 
 */
@EActivity(R.layout.splash_layout)
public class SplashActivity extends Activity {

	@ViewById
	ProgressBar mSplashProgressBar;
	
	CountDownTimer mCountDownTimer;
	
	int mCounter = 0;
	
	@ViewById
	RelativeLayout rlSplashScreen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * This is callback method it's called when view initializes.
	 */
	@AfterViews
	public void initializeView() {
		configImageLoader();
		CustomViewConstants.loadFonts(this);
		mSplashProgressBar.setProgress(mCounter);
		mCountDownTimer = new CountDownTimer(3000, 22) {

			@Override
			public void onTick(long millisUntilFinished) {
				mCounter++;
				mSplashProgressBar.setProgress(mCounter);

			}

			@Override
			public void onFinish() {
				
				mCounter++;
				mSplashProgressBar.setProgress(mCounter);
				finish();

				if (!UserSharedPreferences.getInstance(SplashActivity.this)
						.getBoolean(AppConstant.ISACTIVEUSER)) {
				
					Intent intent = new Intent(SplashActivity.this,	LoginOverlayActivity_.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					SplashActivity.this.startActivity(intent);
				} else {
					 Intent intent = new Intent(SplashActivity.this,HomeActivity_.class);
					 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					SplashActivity.this.startActivity(intent);
				}
				}
		};
		mCountDownTimer.start();

	}
	/**
	 * Image loader initialisation
	 */
	private void configImageLoader() {

		// Create global configuration and initialize ImageLoader with this
		// configuration
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
				.defaultDisplayImageOptions(getDisplayImageOptions()) // default
				.build();

		ImageLoader.getInstance().init(config);
	}

	/**
	 * Callback method to display image options
	 * @return
	 */
	public static DisplayImageOptions getDisplayImageOptions() {
		DisplayImageOptions displayOptions = new DisplayImageOptions.Builder().cacheInMemory(true).build();

		return displayOptions;
	}
	

	/**
	 * Set this options while loading user image.
	 * 
	 * @return
	 */
	public static DisplayImageOptions getDisplayImageOptionsWithUserImage() {
		DisplayImageOptions displayOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
				.showImageForEmptyUri(R.drawable.img_friendlist_default_profile_pic)
				.showImageOnFail(R.drawable.img_friendlist_default_profile_pic).build();

		return displayOptions;
	}

	@Override
	protected void onPause() {
		super.onPause();
		mCountDownTimer.cancel();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(mCountDownTimer!=null){
			mCountDownTimer.start();
		}
	}
	
}
