package com.assignit.android.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.PhoneLookup;

import com.assignit.android.localstorage.UserSharedPreferences;

/**
 * This class is to get COntacts detials from phonebook
 * 
 * @author Innoppl
 * 
 */
public class MobileContactsUtils {
	Context context;

	public MobileContactsUtils(Context context) {
		this.context = context;
	}

	/**
	 * Getting Contacts for session using this method
	 * 
	 */
	public String getContactFromMobileContact() {

		String phoneno = "";
		String contact_id = "0";
		String contact_Version = "0";

		Cursor phones = context.getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " ASC");

		while (phones.moveToNext()) {

			String num = phones
					.getString(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			contact_id = phones
					.getString(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
			String name = phones
					.getString(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			contact_Version = phones.getString(phones
					.getColumnIndex(ContactsContract.RawContacts.VERSION));
		

			int item_HasPhoneNumber = phones
					.getInt(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER));

			if (item_HasPhoneNumber > 0) {

				num = num.replaceAll("[^0-9]", "");
				num = num.trim();

				phoneno += num.toString();
				phoneno += ",";
				phoneno += contact_Version.toString();
				phoneno += ",";
				phoneno += contact_id.toString();
				phoneno += ",";
				phoneno += name.toString();
				phoneno += "-";

			}
		}

		if (phoneno.length() != 0) {
			phoneno = phoneno.substring(0, phoneno.length() - 1);
			if (!contact_id.equals("0")) {
				UserSharedPreferences.getInstance(context).putString(
						AppConstant.LAST_UPDATED_CONTACTS, phoneno);
			}
		}

		if (!phones.isClosed()) {
			phones.close();
		}
		return phoneno;
	}

	/**
	 * Getting json formatted Contacts detials for initial updates
	 * 
	 * @param contactId
	 */
	public JSONArray getContactMobileContact() {

		String contact_id = "0";

		Cursor phones = context.getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " ASC");
		JSONArray jArray = new JSONArray();

		while (phones.moveToNext()) {

			String num = phones
					.getString(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			contact_id = phones
					.getString(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
			String name = phones
					.getString(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			String type = phones
					.getString(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

			int item_HasPhoneNumber = phones
					.getInt(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER));

			if (item_HasPhoneNumber > 0) {

				if (num.startsWith("+")) {
					num = "+" + num.replaceAll("[^0-9]", "");
				} else {
					num = num.replaceAll("[^0-9]", "");
					num = num.trim();
				}

				JSONObject json = new JSONObject();
				try {
					json.put(AppConstant.FRIEND_DETAILS.FRIEND_NAME, name);
					json.put(AppConstant.FRIEND_DETAILS.FRIEND_CONTACTID,
							contact_id);
					json.put(AppConstant.FRIEND_DETAILS.FRIEND_INDEXID, type);
					json.put(AppConstant.FRIEND_DETAILS.FRIEND_CONTACTNUMBER,
							num.toString());

				} catch (JSONException e) {
					e.printStackTrace();
				}
				jArray.put(json);

			}
		}

		if (!phones.isClosed()) {
			phones.close();
		}
		
		return jArray;
	}

	/**
	 * Getting Contacts details for particular contact Id
	 * 
	 * @param contactId
	 */
	public JSONArray getContactDetials(int contactId) {

		JSONArray jArray = new JSONArray();

		Cursor cursor = context.getContentResolver().query(
				CommonDataKinds.Phone.CONTENT_URI, null,
				CommonDataKinds.Phone.CONTACT_ID + " = ?",
				new String[] { Integer.toString(contactId) }, null);

		while (cursor.moveToNext()) {
			String num = cursor.getString(cursor
					.getColumnIndex(CommonDataKinds.Phone.NUMBER));
			String name = cursor.getString(cursor
					.getColumnIndex(CommonDataKinds.Phone.DISPLAY_NAME));
			String IndexId = cursor.getString(cursor
					.getColumnIndex(CommonDataKinds.Phone.TYPE));

			int item_HasPhoneNumber = cursor
					.getInt(cursor
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER));

			if (item_HasPhoneNumber > 0) {

				if (num.startsWith("+")) {
					num = "+" + num.replaceAll("[^0-9]", "");
				} else {
					num = num.replaceAll("[^0-9]", "");
					num = num.trim();
				}

				JSONObject json = new JSONObject();
				try {
					json.put(AppConstant.FRIEND_DETAILS.FRIEND_NAME, name);
					json.put(AppConstant.FRIEND_DETAILS.FRIEND_CONTACTID,
							contactId);
					json.put(AppConstant.FRIEND_DETAILS.FRIEND_INDEXID, IndexId);
					json.put(AppConstant.FRIEND_DETAILS.FRIEND_CONTACTNUMBER,
							num.toString());

				} catch (JSONException e) {
					e.printStackTrace();
				}
				jArray.put(json);

			}
		}
		if (!cursor.isClosed()) {
			cursor.close();
		}
		
		return jArray;
	}

	/**
	 * Method to get contact id for particular number
	 * 
	 * @param phoneNumber
	 * @return
	 */
	public String getContactId(String phoneNumber) {
		String contactid = null;

		ContentResolver contentResolver = context.getContentResolver();

		Uri uri = Uri.withAppendedPath(
				ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(phoneNumber));

		Cursor cursor = contentResolver.query(uri, new String[] {
				PhoneLookup.DISPLAY_NAME, PhoneLookup._ID }, null, null, null);

		if (cursor != null) {
			while (cursor.moveToNext()) {

				contactid = cursor.getString(cursor
						.getColumnIndexOrThrow(PhoneLookup._ID));

			}
			cursor.close();
		}

		return contactid;
	}

	/**
	 * Getting newly inserted Contacts details using last inserted contact id
	 * from session
	 * 
	 */
	public JSONArray getNewlyInsertedContact() {
		JSONArray array = null;

		String contact_id = "0";

		Cursor phones = context.getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " DESC");
		JSONArray jArray = new JSONArray();

		while (phones.moveToNext()) {

			if (contact_id.equals("0")) {

				contact_id = phones
						.getString(phones
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
			}
			String contactId = phones
					.getString(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));

			int item_HasPhoneNumber = phones
					.getInt(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER));

			if (Integer.valueOf(UserSharedPreferences.getInstance(context)
					.getString(AppConstant.LAST_CONTACT_ID)) < Integer
					.valueOf(contactId)) {

				if (item_HasPhoneNumber > 0) {
					List<String> contacts = getPhoneNumber(Integer
							.valueOf(contactId));

					for (int i = 0; i < contacts.size(); i++) {

						JSONObject json = new JSONObject();
						try {
							json.put(AppConstant.FRIEND_DETAILS.FRIEND_NAME,
									getName(Integer.valueOf(contactId)));
							json.put(
									AppConstant.FRIEND_DETAILS.FRIEND_CONTACTID,
									Integer.toString(Integer.valueOf(contactId)));
							json.put(AppConstant.FRIEND_DETAILS.FRIEND_INDEXID,
									Integer.toString(i));
							if (getPhoneNumber(Integer.valueOf(contactId)).get(
									i).startsWith("+")) {
								String contats = getPhoneNumber(
										Integer.valueOf(contactId)).get(i)
										.replaceAll("[^0-9]", "").toString();
								contats = "+" + contats;
								json.put(
										AppConstant.FRIEND_DETAILS.FRIEND_CONTACTNUMBER,
										contats.trim().toString());
							} else {
								json.put(
										AppConstant.FRIEND_DETAILS.FRIEND_CONTACTNUMBER,
										getPhoneNumber(
												Integer.valueOf(contactId))
												.get(i)
												.replaceAll("[^0-9]", "")
												.toString());
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
						jArray.put(json);

					}
				}

			} else {
				break;
			}
			array = jArray;
		}
		phones.close();

		if (!contact_id.equals("0")) {
			UserSharedPreferences.getInstance(context).putString(
					AppConstant.LAST_CONTACT_ID, contact_id);
		}

		return array;
	}

	/**
	 * Getting Contacts name for particular contact Id
	 * 
	 * @param contactId
	 */
	public String getName(int contactId) {
		String name = "";
		final String[] projection = new String[] { Contacts.DISPLAY_NAME };

		final Cursor contact = context.getContentResolver().query(
				Contacts.CONTENT_URI, projection, Contacts._ID + "=?",
				new String[] { String.valueOf(contactId) }, null);

		if (contact.moveToFirst()) {
			name = contact.getString(contact
					.getColumnIndex(Contacts.DISPLAY_NAME));
			contact.close();
		}
		contact.close();
		return name;

	}


	/**
	 * Getting all Contacts numbers for particular contact Id
	 * 
	 * @param contactId
	 */
	// To get Contact Number
	private List<String> getPhoneNumber(int contactId) {
		List<String> contactNumbers = new ArrayList<String>();

		String phoneNumber = "";
		final String[] projection = new String[] { Phone.NUMBER, Phone.TYPE, };
		final Cursor phone = context.getContentResolver().query(
				Phone.CONTENT_URI, projection, Data.CONTACT_ID + "=?",
				new String[] { String.valueOf(contactId) }, null);

		if (phone.moveToFirst()) {
			final int contactNumberColumnIndex = phone
					.getColumnIndex(Phone.DATA);

			while (!phone.isAfterLast()) {
				phoneNumber = phone.getString(contactNumberColumnIndex);

				phone.moveToNext();
				contactNumbers.add(phoneNumber);
			}

		}
		phone.close();

		return contactNumbers;
	}

	
	/**
	 * Getting Contacts thumbnail-sized photo for particular contact Id
	 * 
	 * @param contactId
	 */
	public Bitmap openPhoto(long contactId) {
		Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI,
				contactId);
		Uri photoUri = Uri.withAppendedPath(contactUri,
				Contacts.Photo.CONTENT_DIRECTORY);
		Cursor cursor = context.getContentResolver().query(photoUri,
				new String[] { Contacts.Photo.PHOTO }, null, null, null);
		if (cursor == null) {
			return null;
		}
		try {
			if (cursor.moveToFirst()) {
				byte[] data = cursor.getBlob(0);
				if (data != null) {
					Bitmap bmp = BitmapFactory.decodeByteArray(data, 0,
							data.length);
					Bitmap bm = Bitmap.createScaledBitmap(bmp,
							CommonUtils.convertToDp(context, 40),
							CommonUtils.convertToDp(context, 40), false);

					return bm;
				}
			}
		} finally {
			cursor.close();
		}
		return null;
	}

}