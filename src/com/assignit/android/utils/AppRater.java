package com.assignit.android.utils;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.assignit.android.R;
/**
 * This class is shows dialog  to rate Application and session maintains for later and never options.
 * 
 * @author Innoppl
 * 
 */
public class AppRater {
    private final static String APP_TITLE = "AssignIt";// App Name
    private final static String APP_PNAME = "com.assignit.android";// Package Name
     
  private final static int LAUNCHES_UNTIL_PROMPT = 6;//Min number of launches

    /**
     * Method to initialise app rater class
     * 
     * @param mContext
     */
    public static void appLaunched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(AppConstant.PREF_NAME, 0);
        if (prefs.getBoolean(AppConstant.PREF_DONT_SHOW_AGAIN, false)) { return ; }

        SharedPreferences.Editor editor = prefs.edit();
        
     // Increment launch counter
        long  launch_count = prefs.getLong(AppConstant.PREF_LAUNCH_COUNT, 0) + 1;
        
        editor.putLong(AppConstant.PREF_LAUNCH_COUNT, launch_count);
        
        if ( launch_count >= LAUNCHES_UNTIL_PROMPT) {
            
       	 editor.putLong(AppConstant.PREF_LAUNCH_COUNT, 1);
       	 launch_count = 1;
       }
    
      // Wait at least n days before opening
        if (launch_count == 1 ){
                showRateDialog(mContext, editor);
           
        }

        editor.commit();
    }   
    
   

    /**
     * Method to show dialog for app rating 
     * 
     * @param mContext
     * @param editor
     */
    public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {

        Builder builder  =  new AlertDialog.Builder(mContext);;
      
        builder.setTitle("Rate " + APP_TITLE);

        builder.setMessage(mContext.getString(R.string.rate_message));

        builder.setPositiveButton(mContext.getString(R.string.button_now),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mContext.getString(R.string.rate_app) + APP_PNAME)));
                if (editor != null) {
                    editor.putBoolean(AppConstant.PREF_DONT_SHOW_AGAIN, true);
                    editor.commit();
                }
                dialog.dismiss();
                
            }
        });        
      
        builder.setNeutralButton(mContext.getString(R.string.button_later),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(mContext.getString(R.string.button_never),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                if (editor != null) {
                    editor.putBoolean(AppConstant.PREF_DONT_SHOW_AGAIN, true);
                    editor.commit();
                }
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
             
    }
}