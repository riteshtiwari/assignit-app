package com.assignit.android;

import android.app.Application;
/**
 * Application class for assignit.
 * 
 * @author Innoppl
 * 
 */
public class AssignitApp extends Application{
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		//callback method to call to Exception handler class on app start
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getApplicationContext()));
	}
	
	
	
	/* 
	 * @see android.app.Application#onTerminate()
	 */
	@Override
	public void onTerminate() {
		super.onTerminate();
	}
	
}
