package com.assignit.android.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;

import com.assignit.android.localstorage.UserSharedPreferences;
import com.assignit.android.net.WebServiceManager;
import com.assignit.android.net.responsebean.FriendUploadRespose;

/**
 * Contact Service class to upload contacts on user modify the contacts.
 * 
 * @author Innoppl
 * 
 */
public class ContactService extends Service {

	private String isUpdated = "1";
	MobileContactsUtils contactUtils;
	int temp = -2;
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	/**
	 * This method is called on serivce class created Register the
	 * ContentObserver class to get notify while changes in the in phone book
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		
		contactUtils = new MobileContactsUtils(this);
		this.getContentResolver().registerContentObserver(
				ContactsContract.Contacts.CONTENT_URI, true, mObserver);
	}

	/**
	 * Content observer class is used to notify changes in the phonebook . This
	 * method is defined in the contact service class because it's upload
	 * contacts on background service even without user.
	 */
	private ContentObserver mObserver = new ContentObserver(new Handler()) {

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);

			updateContacts();

		}

	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		return START_STICKY;
	}

	/**
	 * This method is call on service class destroy to unregister the content
	 * observer
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		getContentResolver().unregisterContentObserver(mObserver);
	}

	/**
	 * This Asynctask class is used to send deleted contacts to server in
	 * background
	 */
	class updateContactTask extends
			AsyncTask<JSONArray, String, FriendUploadRespose> {

		@Override
		protected FriendUploadRespose doInBackground(JSONArray... params) {
			FriendUploadRespose respose = null;
			if (params[0] != null) {
				respose = WebServiceManager.getInstance(ContactService.this)
						.uploadFriendDetails(
								params[0],
								UserSharedPreferences.getInstance(
										ContactService.this).getString(
										AppConstant.USER_ID), isUpdated);
			}
			return respose;
		}

		@Override
		protected void onPostExecute(FriendUploadRespose result) {

			super.onPostExecute(result);
			if (result != null) {
			Log.e("result", "" + result.httpStatusCode);
			}
		}
	}

	/**
	 * 
	 * This method is called to get newly inserted contacts and modified
	 * contacts and deleted contacts.
	 */
	private void updateContacts() {

		String contactList = UserSharedPreferences.getInstance(this).getString(
				AppConstant.LAST_UPDATED_CONTACTS);

		String pcontactList = contactUtils.getContactFromMobileContact()
				.toString().trim();

		String[] sessionContactList = contactList.split("-");
		String[] presentContactList = pcontactList.split("-");
		
		
		if (presentContactList.length > 0 && sessionContactList.length > 0) {
			if (presentContactList.length != sessionContactList.length) {
				// To Get New Contacts
				if (presentContactList.length > sessionContactList.length) {

					List<String> sessionLists = new ArrayList<String>();
					List<String> presentLists = new ArrayList<String>();

					for (int i = 0; i < sessionContactList.length; i++) {

						String[] sessioncontact = sessionContactList[i].split(",");
						if(sessioncontact.length>=2){
						sessionLists.add(sessioncontact[0] + ","+ sessioncontact[2]);}
					}

					for (int i = 0; i < presentContactList.length; i++) {

						String[] presentcontact = presentContactList[i].split(",");
						if(presentcontact.length>=2){
						presentLists.add(presentcontact[0] + "," + presentcontact[2]);}
					}

					int newContactIndex = -5;
					for (int i = 0; i < presentLists.size(); i++) {

						int indexofNewContact = sessionLists.indexOf(presentLists.get(i));

						if (indexofNewContact == -1) {
							newContactIndex = i;
						}
					}
					
					if (newContactIndex != -5 && newContactIndex >= 0) {
						if (StringUtils.isNotBlank(presentLists
								.get(newContactIndex))) {
							if (CommonUtils.isNetworkAvailable(this)
									&& presentLists.get(newContactIndex).split(
											",").length >= 1) {
							
								new updateContactByIdTask()
										.execute(presentLists.get(
												newContactIndex).split(",")[1]);
							} else {
								UserSharedPreferences.getInstance(
										ContactService.this).saveArray(
										contactUtils.getContactDetials(Integer
												.parseInt(presentLists.get(
														newContactIndex).split(
														",")[1])));
							}
						}
					}

					// To Get Deleted Contacts
				} else if (presentContactList.length < sessionContactList.length) {
					List<String> sessionListSingle = new ArrayList<String>();
					List<String> presentListSingle = new ArrayList<String>();
					List<String> sessionList = new ArrayList<String>();
					List<String> presentList = new ArrayList<String>();

					for (int i = 0; i < sessionContactList.length; i++) {

						String[] sessioncontact = sessionContactList[i].split(",");
						if(sessioncontact.length>=2){
						sessionList.add(sessioncontact[2]);
						sessionListSingle.add(sessioncontact[0] + ","	+ sessioncontact[2]);}
					}

					for (int i = 0; i < presentContactList.length; i++) {

						String[] presentcontact = presentContactList[i].split(",");
						if(presentcontact.length>=2){
						presentList.add(presentcontact[2]);
						presentListSingle.add(presentcontact[0] + ","
								+ presentcontact[2]);}
					}

					int deletedContactIndex = -3;

					for (int i = 0; i < sessionList.size(); i++) {

						int indexofdeletedContact = presentList
								.indexOf(sessionList.get(i));

						if (indexofdeletedContact == -1) {
							deletedContactIndex = i;
						}
					}
					if (deletedContactIndex != -3 && deletedContactIndex >= 0) {
						if (StringUtils.isNotBlank(sessionList
								.get(deletedContactIndex))) {
							if (CommonUtils.isNetworkAvailable(this)) {
								
								JSONArray array = getdeletedContactDetials(Integer
										.parseInt(sessionList
												.get(deletedContactIndex)));
								if (array != null) {
									new updateContactTask().execute(array);
								}
							} else {
								UserSharedPreferences
										.getInstance(ContactService.this)
										.saveArray(
												getdeletedContactDetials(Integer.parseInt(sessionList
														.get(deletedContactIndex))));
							}
						}
					}
					if(deletedContactIndex == -3){
					

					int deletedSingleContactIndex = -3;
					for (int i = 0; i < sessionListSingle.size(); i++) {

						int indexofSingledeletedContact = presentListSingle
								.indexOf(sessionListSingle.get(i));

						if (indexofSingledeletedContact == -1) {
							deletedSingleContactIndex = i;

						}
					}
					if (sessionListSingle.size() > 0
							&& deletedSingleContactIndex != -3
							&& deletedSingleContactIndex >= 0) {
						if (StringUtils.isNotBlank(sessionListSingle
								.get(deletedSingleContactIndex))) {
							if (CommonUtils.isNetworkAvailable(this)
									&& sessionListSingle.get(
											deletedSingleContactIndex).split(
											",").length >= 1) {

								new updateContactByIdTask()
										.execute(sessionListSingle.get(
												deletedSingleContactIndex)
												.split(",")[1]);

							} else {
								UserSharedPreferences
										.getInstance(ContactService.this)
										.saveArray(
												contactUtils
														.getContactDetials(Integer
																.parseInt(sessionListSingle
																		.get(deletedSingleContactIndex)
																		.split(",")[1])));
							}
						}
					}

				}}
			} else if (presentContactList.length == sessionContactList.length) {// To
																				// Get
																				// Modified
																				// Contacts
				for (int i = 0; i < presentContactList.length; i++) {
					
					String[] sessioncontact = sessionContactList[i].split(",");
					String[] presentcontact = presentContactList[i].split(",");
					
					if(sessioncontact.length >= 3 && presentcontact.length >= 3){
						

						if (!sessioncontact[0].equals(presentcontact[0]) || !sessioncontact[3].equals(presentcontact[3])) {

							
							int modifiedContactId = Integer.parseInt(presentcontact[2]);
							
							Log.e("working ", ""+modifiedContactId);
							
							if (CommonUtils.isNetworkAvailable(this)) {

								new updateContactByIdTask()
										.execute(Integer
												.toString((modifiedContactId)));

							} else {
								UserSharedPreferences
										.getInstance(ContactService.this)
										.saveArray(
												contactUtils
														.getContactDetials(modifiedContactId));
						}
					}}
				}
			}
		}
	}
	/**
	 * This Asynctask class is used to send modifed contacts to server in
	 * background
	 */
	
	class updateContactByIdTask extends
			AsyncTask<String, String, FriendUploadRespose> {

		@Override
		protected FriendUploadRespose doInBackground(String... params) {
			FriendUploadRespose respose = null;
			if(params.length>0){
			JSONArray jsonArray = contactUtils.getContactDetials(Integer.parseInt(params[0]));

			
			if (params[0] != null) {
				respose = WebServiceManager.getInstance(ContactService.this)
						.uploadFriendDetails(
								jsonArray,
								UserSharedPreferences.getInstance(
										ContactService.this).getString(
										AppConstant.USER_ID), isUpdated);
			}}
			return respose;
		}

		@Override
		protected void onPostExecute(FriendUploadRespose result) {

			super.onPostExecute(result);
			if (result != null) {
				Log.e("result", "" + result.httpStatusCode);
			}
		}
	}

	/**
	 * Getting jsonArray for deleted contact Id to send in service
	 * 
	 * @param deletedcontactId
	 * @return
	 */
	private JSONArray getdeletedContactDetials(int deletedcontactId) {

		JSONArray jsonArray = new JSONArray();

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(AppConstant.FRIEND_DETAILS.FRIEND_NAME, "");
			jsonObject.put(AppConstant.FRIEND_DETAILS.FRIEND_CONTACTID,Integer.toString(deletedcontactId));
			jsonObject.put(AppConstant.FRIEND_DETAILS.FRIEND_INDEXID, "");
			jsonObject.put(AppConstant.FRIEND_DETAILS.FRIEND_CONTACTNUMBER, "");

		} catch (JSONException e) {
			e.printStackTrace();
		}
		jsonArray.put(jsonObject);

		return jsonArray;
	}
}