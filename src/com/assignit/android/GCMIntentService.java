package com.assignit.android;

import java.util.List;

import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;

import com.assignit.android.gcm.ServerUtilities;
import com.assignit.android.localstorage.UserSharedPreferences;
import com.assignit.android.ui.activities.HomeActivity_;
import com.assignit.android.utils.AppConstant;
import com.assignit.android.utils.CommonUtils;
import com.assignit.android.utils.StringUtils;
import com.google.android.gcm.GCMBaseIntentService;

/**
 * This class is used for communicating with google GCM server.
 * 
 * @author Innoppl
 * 
 */
public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";
	public static String MESSAGE = "";
	public GCMIntentService() {
		super(CommonUtils.SENDER_ID);
	}

	 /**
     * Method called on device registered
     * @param context
     * @param registrationId
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        UserSharedPreferences.getInstance(this).putString(AppConstant.LOGIN.ACCESS_TOKEN, registrationId);
        ServerUtilities.register(context, registrationId);
       
    }

    /**
     * Method called on device unregistration
     * 
     * @param context
     * @param registrationId
     * 
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        ServerUtilities.unregister(context, registrationId);
    }

    /**
     * Method called on Receiving a new push notification
     * @param context
     * @param intent
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
     
        String msg = "";
        String view = "";
        String calenderEventId = "";
        if(extras!=null){
        String message = intent.getExtras().getString(AppConstant.NOTIFICATION.MESSAGE);
        Log.e("msg", ""+message);
        try {

    	    JSONObject obj = new JSONObject(message);
			 msg = obj.getString(AppConstant.NOTIFICATION.MESSAGE);
			 view = obj.getString(AppConstant.NOTIFICATION.VIEW);
			 if(obj.has(AppConstant.NOTIFICATION.EVENTID)){
				 calenderEventId = obj.getString(AppConstant.NOTIFICATION.EVENTID);
				 if (StringUtils.isNotBlank(calenderEventId) && !calenderEventId.equals("0")) {
							CommonUtils.deleteEvent(context, calenderEventId);
						}
			 }
    	} catch (Throwable t) {
    	    Log.e("My App", "Could not parse malformed JSON: ");
    	}
   
        // notifies user
       if(StringUtils.isNotBlank(msg) && StringUtils.isNotBlank(view) && !view.equals(AppConstant.NOTIFICATION.FRIENDS)){
        generateNotification(context, msg,view,calenderEventId);}
       
       }
    }
    
 
	/**
	 * Method called to Getting package class
	 * 
	 * @param context
	 * @return
	 */
	public String getLauncherClassName(Context context) {

	    PackageManager packageManager = context.getPackageManager();

	    Intent intent = new Intent(Intent.ACTION_MAIN);
	    intent.addCategory(Intent.CATEGORY_LAUNCHER);

	    List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);
	    for (ResolveInfo resolveInfo : resolveInfos) {
	        String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
	        if (pkgName.equalsIgnoreCase(context.getPackageName())) {
	            String className = resolveInfo.activityInfo.name;
	            return className;
	        }
	    }
	    return null;
	}


    /**
     * Method called on receiving a deleted message response
     * 
     * @param context
     * @param total
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        // log message
        Log.i(TAG, "Received deleted messages notification");
     
    }

    /**
     * Method called if push notification on Error
     * @param context
     * @param total
     * */
    @Override
    public void onError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received error: " + errorId);
      
    }

    /**
     * Method called if notification on Recoverable error
     * @param context
     * @param total
     * */
    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     * 
     * @param context
     * @param message
     * @param event
     * @param eventId
     */
	@SuppressWarnings("deprecation")
	private static void generateNotification(Context context, String message,String event,String eventId) {
        
		int icon = R.drawable.app_icon;
        long when = System.currentTimeMillis();
        
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        Notification notification = new Notification(icon, message, when);
        
        String title = context.getString(R.string.app_name);
        
        Intent notificationIntent = new Intent(context, HomeActivity_.class);
        
     // set intent so it does not start a new activity
     	notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtra(AppConstant.NOTIFICATION.ISNOTIFICATION, true);
		notificationIntent.putExtra(AppConstant.NOTIFICATION.EVENT, event);
       
		PendingIntent intent = null;
		
		if (event.equalsIgnoreCase(AppConstant.NOTIFICATION.FOR_ME_EVENT)) {
			intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			notification.setLatestEventInfo(context, title + " " + AppConstant.NOTIFICATION.FOR_ME_EVENT, message,
					intent);
			if (UserSharedPreferences.getInstance(context).getBoolean(UserSharedPreferences.KEY_NOTIFICATION_STATUS)) {
				notificationManager.notify(0, notification);
			}
			UserSharedPreferences.getInstance(context).putBoolean(UserSharedPreferences.KEY_FORME_STATUS, Boolean.TRUE);
		} else if (event.equalsIgnoreCase(AppConstant.NOTIFICATION.FROM_ME_EVENT)){
			intent = PendingIntent.getActivity(context, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			notification.setLatestEventInfo(context, title + " " + AppConstant.NOTIFICATION.FROM_ME_EVENT, message,
					intent);
			if (UserSharedPreferences.getInstance(context).getBoolean(UserSharedPreferences.KEY_NOTIFICATION_STATUS)) {
				notificationManager.notify(1, notification);
			}
			UserSharedPreferences.getInstance(context).putBoolean(UserSharedPreferences.KEY_FROMME_STATUS, Boolean.TRUE);
		}else if (event.equalsIgnoreCase(AppConstant.NOTIFICATION.FRIENDS)){
			intent = PendingIntent.getActivity(context, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			notification.setLatestEventInfo(context, title + " " + AppConstant.NOTIFICATION.FRIENDS, message,
					intent);
			//UserSharedPreferences.getInstance(context).putBoolean(AppConstant.ACTION_REFRESH_FRIENDS, Boolean.TRUE);
		}
		
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;
        
        // Vibrate if vibrate is enabled
        //notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);   
        
        if(!event.equals(AppConstant.NOTIFICATION.FRIENDS)){
        Intent refreshIntent = new Intent();
		refreshIntent.putExtra(AppConstant.NOTIFICATION.EVENT, event);
		refreshIntent.putExtra(AppConstant.NOTIFICATION.EVENTID, eventId);
		refreshIntent.putExtra(AppConstant.NOTIFICATION.MESSAGE, message);
		refreshIntent.setAction(AppConstant.ACTION_REFRESH);
		context.sendBroadcast(refreshIntent);
        }
    }
}
