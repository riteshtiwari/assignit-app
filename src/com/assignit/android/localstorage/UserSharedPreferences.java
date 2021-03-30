package com.assignit.android.localstorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * This class represent local storage to maintain user session and other preferences.
 * 
 * @author Innoppl
 * 
 */
public class UserSharedPreferences {

	private final String FILE_NAME = "assignit-Preferences";

	private static UserSharedPreferences mUserSharedPreferences;
	public SharedPreferences preferences;

	public static String KEY_IS_REMEMBER_ME = "is_remember_me";
	public static String KEY_DEVICE_ID = "device_id";
	public static String KEY_USER_DETAILS = "user_details";
	public static String KEY_USER_ID = "user_id";
	public static String KEY_USER_NAME = "user_name";
	public static String KEY_USER_PASSWORD = "password";
	public static String KEY_USER_EMAIL = "user_email";
	public static String KEY_ACCESS_TOKEN = "access_token";
	public static String KEY_USER_TYPE = "user_type";
	public static String KEY_USER_IMAGE = "user_image_url";

	public static String KEY_NOTIFICATION_STATUS = "notification_status";
	public static String FIRST_TIME_VIDEO = "first_time_video";

	public static String KEY_IS_ALLOW_FBSHARE_CHECKIN = "is_fbshare_checkin";
	public static String KEY_IS_ALLOW_WINK_PUSH = "is_wink_push";
	public static String KEY_FORME_STATUS = "key_forme_status";
	public static String KEY_FROMME_STATUS = "key_fromme_status";

	public static String KEY_OVERLAY_STATUS = "overlay_status";

	public static String KEY_LOGIN_OVERLAY_STATUS = "login_overlay_status";

	public static String KEY_NOTIFICATION_KEY_ON_CLEAR = "notification_app_clear_status";

	//public static String KEY_NOTIFICATION_EVENT_TYPE = "notification_event_type";

	List<JSONArray> arrays = new ArrayList<JSONArray>();
	
	private UserSharedPreferences() {

	}

	/**
	 *this method used to initialise singleton class.
	 * 
	 * @param context
	 * @return
	 */
	public static UserSharedPreferences getInstance(Context context) {

		if (mUserSharedPreferences == null) {
			mUserSharedPreferences = new UserSharedPreferences();
		}
		mUserSharedPreferences.getPreferenceObject(context);

		return mUserSharedPreferences;
	}

	/**
	 * Method to initialise the Shared preference
	 * 
	 * @param context
	 */
	private void getPreferenceObject(Context context) {
		preferences = context.getSharedPreferences(FILE_NAME,
				Context.MODE_PRIVATE);
	}

	/**
	 * Method to put string in preference
	 * @param key
	 * @param value
	 */
	public void putString(String key, String value) {
		Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * Method to put list item in preference
	 * @param key
	 * @param value
	 */
	public void putList(String key, Collection<? extends String> value) {
		// Set the values
		Editor editor = preferences.edit();
		Set<String> set = new HashSet<String>();
		set.addAll(value);
		editor.putStringSet(key, set);
		editor.commit();
	}

	/**
	 * Converts the provided ArrayList<String> into a JSONArray and saves it as
	 * a single string in the apps shared preferences
	 * 
	 * @param String
	 *            key Preference key for SharedPreferences
	 * @param array
	 *            ArrayList<String> containing the list items
	 */
	public void saveArray(JSONArray array) {
		arrays.add(array);
	}
	
	
	/**
	 * @return offline contacts list for update into server
	 */
	public List<JSONArray> getArray() {
		
		return arrays;
	
	}
	
	/**
	 * Clear array list periodicaaly
	 */
	public void removearray(){
		arrays.clear();
	}

	/**
	 * Loads a JSONArray from shared preferences and converts it to an
	 * ArrayList<String>
	 * 
	 * @param String
	 *            key Preference key for SharedPreferences
	 * @return ArrayList<String> containing the saved values from the JSONArray
	 */
	public ArrayList<String> getArray(String key) {
		ArrayList<String> array = new ArrayList<String>();
		String jArrayString = preferences.getString(key, "NOPREFSAVED");
		if (jArrayString.matches("NOPREFSAVED"))
			return getDefaultArray();
		else {
			try {
				JSONArray jArray = new JSONArray(jArrayString);
				for (int i = 0; i < jArray.length(); i++) {
					array.add(jArray.getString(i));
				}
				return array;
			} catch (JSONException e) {
				return getDefaultArray();
			}
		}
	}
	// Get a default array in the event that there is no array
	// saved or a JSONException occurred
	private ArrayList<String> getDefaultArray() {
	ArrayList<String> array = new ArrayList<String>();
	array.add("Example 1");
	array.add("Example 2");
	array.add("Example 3");
	return array;
	}
	/**
	 * Method to put boolean in preference
	 * @param key
	 * @param value
	 */
	public void putBoolean(String key, Boolean value) {
		Editor editor = preferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	/**
	 * Method to put Long in preference
	 * @param key
	 * @param value
	 */
	public void putLong(String key, Long value) {
		Editor editor = preferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}
	
	/**
	 * Method to put integer in preference
	 * @param key
	 * @param value
	 */
	public void putInt(String key, Integer value) {
		Editor editor = preferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	/**
	 * Method to get String from preference
	 * @param key
	 * @return
	 */
	public String getString(String key) {
		return preferences.getString(key, "");
	}

	/**
	 * Method to get boolean from preference
	 * @param key
	 * @return
	 */
	public Boolean getBoolean(String key) {
		return preferences.getBoolean(key, false);
	}

	/**
	 * Method to get integer from preference
	 * @param key
	 * @return
	 */
	public int getInt(String key) {
		return preferences.getInt(key, 0);
	}

	/**
	 * Method to get long from preference
	 * @param key
	 * @return
	 */
	public long getLong(String key) {
		return preferences.getLong(key, 0);
	}
	
	/**
	 * clear session
	 */
	public void clearUserData() {
		preferences.edit().clear();
	}
}
