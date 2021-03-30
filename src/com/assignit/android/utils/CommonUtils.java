package com.assignit.android.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.assignit.android.R;

/**
 * Common utility class to perform common operation.
 * 
 * @author Innoppl
 * 
 */
public class CommonUtils {
	public static final String SENDER_ID = "499444243056";

	public static final String PUSH_NOTIFICATION_ACTION = "com.assignit.android.PUSH_NOTIFICATION";

	public static final String EXTRA_MESSAGE = "message";

	/**
	 * Notifies UI to display a message.
	 * <p>
	 * This method is defined in the common helper because it's used both by the
	 * UI and the background service.
	 * 
	 * @param context
	 *            application's context.
	 * @param message
	 *            message to be displayed.
	 */
	public static void displayMessage(Context context, String message) {
		Intent intent = new Intent(PUSH_NOTIFICATION_ACTION);
		intent.putExtra(EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}

	/**
	 * Method to encode url
	 * 
	 * @param str
	 * @return
	 */
	public static String urlEncode(String str) {
		try {
			return URLEncoder.encode(str, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("failed to encode", e);
		}

	}
	/**
	 * Callback method to show timeout error notification
	 * 
	 * @param activity
	 */
	public static void showTimoutError(final Activity activity){

		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				
				CommonUtils.showErrorToast(activity,
						activity.getResources().getString(R.string.time_out_connetion));
				
			}
		});}
	
	/**
	 * Show using this method show keyword explicitly
	 * 
	 * @param context
	 * @param editText
	 */
	public static void showSoftKeyboard(Activity context, EditText editText) {
		InputMethodManager m = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		if (m != null) {
			m.toggleSoftInputFromWindow(editText.getApplicationWindowToken(),
					InputMethodManager.SHOW_FORCED, 0);
		}
	}

	/**
	 * getting calender event id from description
	 * 
	 * @param description
	 * @return
	 */
	public static String getEventID(Context context, String description) {

		String eventId = "0";
		String[] proj = new String[] { Events._ID, };
		Cursor cursor = context.getContentResolver().query(Events.CONTENT_URI,
				proj, Events.DESCRIPTION + " = ? ",
				new String[] { description }, null);

		if (cursor.moveToFirst()) {
			eventId = cursor.getString(0);
		}

		if (!cursor.isClosed()) {
			cursor.close();
		}

		return eventId;

	}

	/**
	 * Getting event detials by event Id
	 * 
	 * @param ID
	 * @param context
	 * @return
	 */
	public static String getEventDetails(String ID, Context context) {

		String desc = "";

		Cursor eventCursor = context.getContentResolver().query(
				Events.CONTENT_URI,
				new String[] { Events._ID, Events.TITLE, Events.DESCRIPTION },
				null, null, null);

		if (eventCursor.getCount() > 0) {

			eventCursor.moveToFirst();

			while (eventCursor.moveToNext()) {

				final String eventID = eventCursor.getString(0);

				if (eventID.equals(ID)) {

					desc = eventCursor.getString(2);

					break;
				}

			}
			// break;

		}
		return desc;
	}

	/**
	 * Show using this method hide keyword explicitly
	 * 
	 * @param context
	 * @param editText
	 */
	public static void hideSoftKeyboard(Activity context) {
		InputMethodManager inputManager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputManager != null)
			inputManager.hideSoftInputFromWindow(context.getWindow()
					.getDecorView().getApplicationWindowToken(), 0);
		context.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	}

	/**
	 * Edit a calendar event.
	 * 
	 * @param context
	 * @param calendarEventID
	 * @param title
	 * @param content
	 * @param startdate
	 * @param enddate
	 */
	public static void editCalendarEvent(Activity context,
			long calendarEventID, String title, String content,
			String startdate, String enddate) {

		Intent intent = new Intent(Intent.ACTION_INSERT);
		intent.setData(Uri.parse("content://com.android.calendar/events/"
				+ String.valueOf(calendarEventID)));
		intent.putExtra(Events.TITLE, title);
		intent.putExtra(Events.DESCRIPTION, content);

		if (!startdate.equals("0") && startdate.length() > 1) {
			intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
					Long.parseLong(startdate) * 1000);
		}
		if (!enddate.equals("0") && enddate.length() > 1) {
			intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
					Long.parseLong(enddate) * 1000);
		}
		 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);

	}

	/**
	 * delete a calendar event.
	 * 
	 * @param context
	 * @param calendarEventID
	 */
	public static void deleteEvent(Context context, String calendarEventID) {

		Uri deleteUri = null;

		deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI,
				Long.parseLong(calendarEventID));

		int rows = context.getContentResolver().delete(deleteUri, null, null);

		Log.e("deleted rows", "" + rows);

	}

	/**
	 * Get date format
	 * 
	 * @param milliSeconds
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getDate(long milliSeconds) {
		SimpleDateFormat formatter = new SimpleDateFormat(
				"dd/MM/yyyy hh:mm:ss a");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

	/**
	 * Check network availability
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		boolean isNetworkAvailable = false;
		if (context != null) {
			ConnectivityManager conMgr = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = conMgr.getActiveNetworkInfo();

			if (info != null && info.isConnected()) {
				isNetworkAvailable = true;
			} else {
				isNetworkAvailable = false;
			}
			return isNetworkAvailable;
		} else
			return isNetworkAvailable;
	}

	/**
	 * Convert dpi values
	 * 
	 * @param context
	 * @param pixelValue
	 * @return
	 */
	public static int convertToDp(Context context, int pixelValue) {
		// Get the screen's density scale
		final float scale = context.getResources().getDisplayMetrics().density;
		// Convert the dps to pixels, based on density scale
		return (int) (pixelValue * scale + 0.5f);
	}

	/**
	 * Date formatter method yyyy-MM-dd HH:mm:ss
	 * 
	 * @param lastVisit
	 * @return
	 */
	public static String getConvertedDate(String lastVisit) {
		SimpleDateFormat currentVisitedFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.US);
		currentVisitedFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		SimpleDateFormat convertedFormat = new SimpleDateFormat("dd MMM yyyy",
				Locale.US);
		Date parsed = new Date();
		try {
			parsed = currentVisitedFormat.parse(lastVisit);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		lastVisit = convertedFormat.format(parsed);
		return lastVisit;
	}

	/**
	 * Date formatter method dd-MMM-yyyy
	 * 
	 * @param seconds
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getFormatedDateMDY(String seconds) {

		String result = "0";
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");

		try {
			long secs = Long.valueOf(seconds);
			long millis = secs * 1000;

			Date resultdate = new Date(millis);
			result = sdf.format(resultdate);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Getting last created calender event id
	 * 
	 * @param context
	 * @return
	 */
	public static long getNewEventId(Context context) {
		ContentResolver cr = context.getContentResolver();
		Uri cal_uri = Uri.parse("content://com.android.calendar/events");
		Uri local_uri = cal_uri;
		if (cal_uri == null) {
			local_uri = Uri.parse(CalendarContract.CONTENT_URI + "events");
		}
		Cursor cursor = cr.query(local_uri,
				new String[] { "MAX(_id) as max_id" }, null, null, "_id");
		cursor.moveToFirst();
		long max_val = cursor.getLong(cursor.getColumnIndex("max_id"));
		return max_val + 1;
	}

	/**
	 * Get milliseconds from date object
	 * 
	 * @param date
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static long getDateSeconds(String date) {

		DateFormat formatter;
		Date localDate = null;
		formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm a");
		try {
			localDate = formatter.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return localDate.getTime() / 1000;
	}

	/**
	 * Date formatter method yyyy-MM-dd
	 * 
	 * @param date
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static long getYYYYMMDDFormat(String date) {

		DateFormat formatter;
		Date localDate = null;
		formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			localDate = formatter.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return localDate.getTime() / 1000;
	}

	/**
	 * @param date
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static Date getYYYMMDDFormat(String date) {

		DateFormat formatter;
		Date localDate = null;
		formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			localDate = formatter.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return localDate;
	}

	/**
	 * to get formated date from long value
	 * 
	 * @param timaStampValue
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getDateAndTimeStr(long timaStampValue) {
		SimpleDateFormat formatter = new SimpleDateFormat("MMM-d-yyyy h:mm a");
		String dateString = formatter.format(timaStampValue * 1000);

		String date = dateString.split(" ")[0].split("-")[0] + " "
				+ dateString.split(" ")[0].split("-")[1] + ", "
				+ dateString.split(" ")[0].split("-")[2];

		String editTime = dateString.split(" ")[1] + " "
				+ dateString.split(" ")[2];

		return date + "-" + editTime;
	}

	/**
	 * Show success toast message on screen
	 * 
	 * @param context
	 * @param message
	 */
	public static void showSuccessToast(Activity context, String message) {
		if (StringUtils.isNotBlank(message)) {
			LayoutInflater inflater = context.getLayoutInflater();
			View layout = inflater.inflate(R.layout.toast_layout,
					(ViewGroup) context.findViewById(R.id.toast_layout_root));

			ImageView image = (ImageView) layout.findViewById(R.id.image);
			image.setImageResource(R.drawable.ic_tick_green);
			TextView text = (TextView) layout.findViewById(R.id.text);
			text.setText(message);

			Toast toast = new Toast(context);

			toast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 60);
			toast.setDuration(Toast.LENGTH_SHORT);
			toast.setView(layout);
			toast.show();
		}
	}

	/**
	 * Show error toast message on screen
	 * 
	 * @param context
	 * @param message
	 */
	public static void showErrorToast(Activity context, String message) {
		if (StringUtils.isNotBlank(message)) {
			LayoutInflater inflater = context.getLayoutInflater();
			View layout = inflater.inflate(R.layout.toast_layout,
					(ViewGroup) context.findViewById(R.id.toast_layout_root));

			ImageView image = (ImageView) layout.findViewById(R.id.image);
			image.setImageResource(R.drawable.ic_cross_red);
			TextView text = (TextView) layout.findViewById(R.id.text);
			text.setText(message);

			Toast toast = new Toast(context);

			toast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 60);
			toast.setDuration(Toast.LENGTH_SHORT);
			toast.setView(layout);
			toast.show();
		}
	}

	/**
	 * Check task status
	 * 
	 * @param taskDate
	 * @return
	 */
	public static boolean isTaskExpired(String taskDate) {

		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 1);

		long mcurrentTime = (c.getTimeInMillis());

		long taskDueDate = Long.valueOf(taskDate);

		if (mcurrentTime / 1000 > taskDueDate) {
			return true;
		}

		return false;
	}

	/**
	 * Check due date status
	 * 
	 * @param TaskDate
	 * @return
	 */
	public static boolean isTaskDueByToday(String TaskDate) {

		Calendar c = Calendar.getInstance();

		int mcurrentdate = c.get(Calendar.DATE);
		int mcurrentMonth = c.get(Calendar.MONTH);
		int mcurrentYear = c.get(Calendar.YEAR);

		long taskDateInSec = Long.valueOf(TaskDate);
		Calendar taskCalendar = Calendar.getInstance();
		taskCalendar.setTimeInMillis(taskDateInSec * 1000);

		int taskdate = taskCalendar.get(Calendar.DATE);
		int taskMonth = taskCalendar.get(Calendar.MONTH);
		int taskYear = taskCalendar.get(Calendar.YEAR);

		// check for today
		if (mcurrentdate == taskdate && mcurrentMonth == taskMonth
				&& mcurrentYear == taskYear) {
			return true;
		}

		return false;
	}

	/**
	 * Check weekend status
	 * 
	 * @param dueDateInSec
	 * @return
	 */
	public static boolean isTaskDueByThisWeekend(String dueDateInSec) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		c.add(Calendar.DATE, 1);
		int mcurrentdate = c.get(Calendar.DATE);
		int mcurrentMonth = c.get(Calendar.MONTH);
		int mcurrentYear = c.get(Calendar.YEAR);

		long taskDateInSec = Long.valueOf(dueDateInSec);
		Calendar weektaskCalendar = Calendar.getInstance();
		weektaskCalendar.setTimeInMillis(taskDateInSec * 1000);

		int weektaskdate = weektaskCalendar.get(Calendar.DATE);
		int weektaskMonth = weektaskCalendar.get(Calendar.MONTH);
		int weektaskYear = weektaskCalendar.get(Calendar.YEAR);

		// check for weekend date
		if (mcurrentdate == weektaskdate && mcurrentMonth == weektaskMonth
				&& mcurrentYear == weektaskYear) {
			return true;
		}

		return false;

	}

	/**
	 * Check directory deleted
	 * 
	 * @param path
	 * @return
	 */
	public static boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			if (files == null) {
				return true;
			}
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}

		return (path.delete());
	}

	/**
	 * Convert input stream to string
	 * 
	 * @param is
	 * @return
	 */
	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * To find app installed or not
	 * 
	 * @param context
	 * @param uri
	 * @return
	 */
	public static boolean isAppInstalled(Activity context, String uri) {
		PackageManager pm = context.getPackageManager();
		boolean app_installed = false;
		try {
			pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
			app_installed = true;
		} catch (PackageManager.NameNotFoundException e) {
			app_installed = false;
		}
		return app_installed;
	}

	/**
	 * To create bitmap from file
	 * 
	 * @param file
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromFile(String file, int reqWidth,
			int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(file, options);
	}


	/**
	 * To get image sample size using BitmapFactory options
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}
	
	
	/**
	 * Getting picasa images from gallery
	 * 
	 * @param context
	 * @param tag
	 * @param url
	 * @return
	 */
	public static Bitmap getBitmap(Context context,String tag, Uri url)
	{
		File cacheDir;
		// if the device has an SD card
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),".OCFL311");
		} else {
			// it does not have an SD card
		   	cacheDir=context.getCacheDir();
		}
		if(!cacheDir.exists())
		    	cacheDir.mkdirs();

		File f=new File(cacheDir, tag);
		InputStream is = null;
		try {
			
			if (url.toString().startsWith("content://com.google.android.gallery3d")) {
				is=context.getContentResolver().openInputStream(url);
			} else {
				is=new URL(url.toString()).openStream();
			}
			OutputStream os = new FileOutputStream(f);
			CopyStream(is, os);
			os.close();
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			Log.e("bitmap", ""+bitmap);
			return bitmap;
		} catch (Exception ex) {
			Log.d("Utils.DEBUG_TAG", "Exception: " + ex.getMessage());
			ex.printStackTrace();
			return null;
		}finally{
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Copy image file to sdcard
	 * @param is
	 * @param os
	 */
	public static void CopyStream(InputStream is, OutputStream os) {
		byte[] buffer = new byte[1024];
		int len;
		try {
			while ((len = is.read(buffer)) != -1) {
			os.write(buffer, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Callback method to show dialog on notification if on empty list in forme and fromme
	 * 
	 * @param context
	 */
	public static void showDialogOnEmptyList(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(false);
		builder.setTitle(context.getString(R.string.dialog_box_error_title))
				.setMessage(context.getString(R.string.no_task))
				.setPositiveButton(context.getString(R.string.button_ok), new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						//dialog.dismiss();
					}
				}).show();
	}
}
