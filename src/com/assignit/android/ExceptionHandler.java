package com.assignit.android;

import java.io.PrintWriter;
import java.io.StringWriter;

import android.content.Context;
import android.os.Build;

import com.assignit.android.localstorage.UserSharedPreferences;
import com.assignit.android.utils.AppConstant;
/**
 * Exception Handler class to Log crash report.
 * 
 * @author Innoppl
 * 
 */
public class ExceptionHandler implements
java.lang.Thread.UncaughtExceptionHandler{
	
	private final Context mContext;
	private final String LINE_SEPARATOR = "\n";
	StringBuilder errorReport =null;

	/**
	 * Constructor for the class
	 * @param context
	 */
	public ExceptionHandler(Context context) {
		mContext = context;
	}

	
	/* Initialise expection message
	 * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread, java.lang.Throwable)
	 */
	public void uncaughtException(Thread thread, Throwable exception) {
		
		StringWriter stackTrace = new StringWriter();
		exception.printStackTrace(new PrintWriter(stackTrace));
		
		errorReport = new StringBuilder();
		errorReport.append("************ CAUSE OF ERROR ************\n\n");
		errorReport.append(stackTrace.toString());

		errorReport.append("\n************ DEVICE INFORMATION ***********\n");
		errorReport.append("Brand: ");
		errorReport.append(Build.BRAND);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Device: ");
		errorReport.append(Build.DEVICE);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Model: ");
		errorReport.append(Build.MODEL);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Id: ");
		errorReport.append(Build.ID);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Product: ");
		errorReport.append(Build.PRODUCT);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("\n************ FIRMWARE ************\n");
		errorReport.append("SDK: ");
		errorReport.append(Build.VERSION.SDK_INT);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Release: ");
		errorReport.append(Build.VERSION.RELEASE);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Incremental: ");
		errorReport.append(Build.VERSION.INCREMENTAL);
		errorReport.append(LINE_SEPARATOR);
			
		new LogFileWebService(mContext, errorReport.toString(), UserSharedPreferences.getInstance(mContext).getString(AppConstant.USER_ID));
		
	}
	
	

}