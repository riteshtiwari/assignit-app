package com.assignit.android.utils;

import com.evernote.client.android.EvernoteSession;

/**
 * This class is defining application constant not ui label or anything.
 * 
 * @author Innoppl
 * 
 */
public class AppConstant {
	
	
	//Production
	//public static String CONSUMER_KEY = "itramji-7896";
	//public static String CONSUMER_SECRET = "a373782b71491dbf";
	public static EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.PRODUCTION;
	
	/*//Sandboxkey 
	public static String CONSUMER_KEY = "naveen867619-1048";
	public static String CONSUMER_SECRET  = "c9b6ce3c76669ebc";
	public static EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;
	*/
	public static String APIKEY = "ASN@123";
	public static String DATA = "data";
	public static String USER_ID = "user_id";
	public static String EVENT_ID = "event_id";
	public static String ISACTIVEUSER = "isactive_user";
	public static String ISCONTACTUPLOADED = "iscontact_uploaded";
	public static String ISCONTACTSAVED = "iscontact_saved";
	public static String LAST_UPDATED_CONTACTS = "last_updated_contacts";
	public static String LAST_CONTACT_ID = "last_contact_id";
	public static String PHONENUMBER = "phone_number";
	public static String FBACCESSTOKEN = "fb_access_token";
	public static String KEY_CAMERA_IMAGE_URI = "camera_image";
	public static String FRIENDS_TAB_VALUE = "0";
	public static String C_NAME = "Country_name";
	public static String C_CODE = "c_code";
	public static String C_SUFFIX = "c_suf";
	public static String TIMEOUT = "time_out_error";
	
	public static String SESSION_NAME = "s_name";
	public static String SESSION_NUMBER = "s_number";
	public static String SESSION_CONTACTID = "s_contactid";
	public static String SESSION_VERSION = "s_version";
	public static String SESSION_INDEXID = "s_indexId";
	
	public static String PRESENT_NAME = "p_name";
	public static String PRESENT_NUMBER = "p_number";
	public static String PRESENT_CONTACTID = "p_contactid";
	public static String PRESENT_VERSION = "p_version";
	public static String PRESENT_INDEXID = "p_indexId";
	
	
	// Common parameter for registration response
	public interface REGISTER {
	
		public final String NAME = "name";
		public final String API_KEY = "api_key";
		public final String IS_UPDATED = "is_update";
		public final String COUNTRY_CODE = "countryCode";
		public final String PHONENUMBER = "phoneNumber";
		public final String CLIENT = "client";
	}

	// Common parameter for user login response
	public interface USERLOGIN {
		
		public final String DEVICE_TOKEN = "device_token";
		public final String VERIFICATION_CODE = "verification_code";
		public final String USER_ID = "userId";
	}
	
	// Common parameter for user local set reminder prefrence 
public interface SETREMINDERPARAMETER {
		
		public final String PREF_FRIENDID = "pref_friendId";
		public final String PREF_TASKID = "pref_taskId";
		public final String PREF_EVENTID = "pref_eventId";
	}
//Common parameter for user login prefrence
	public interface LOGIN {
		public final String USER_ID = "userId";
		public final String ACCESS_TOKEN = "access_token";
		public final String ACCESS_TOKEN_STATUS = "access_token_status";
	}
	// Common parameter for friends detial response
	public interface FRIEND_DETAILS {
		public final String USER_ID = "userId";
		public final String FRIEND_TYPE = "friend_type";
		public final String PHONE_BOOK = "phoneBookFriends";
		public final String FRIEND_NAME = "name";
		//public final String FRIEND_FIRSTNAME = "firstName";
		//public final String FRIEND_LASTNAME = "lastName";
		public final String FRIEND_CONTACTID = "recordId";
		public final String FRIEND_CONTACTNUMBER = "phone";
		public final String FRIEND_INDEXID = "indexId";
	
	}
	// Common parameter to assing task response
	public interface ASSIGN_TASK {
		
		public final String USER_ID = "userId";
		public final String TO_USER_ID = "toUser";
		public final String TASK = "task";
		public final String TASK_ID = "task_id";
		public final String DAU_DATE = "due_date";
		public final String DAU_START_DATE = "start_date";
		public final String DAU_END_DATE = "end_date";
		public final String REPEATED = "repeated";
		public final String REPEAT_FREQ = "repeatFreq";
		public final String REPEAT_UNTIL = "repeatUntil";
		public final String DAU_STATUS = "due_status";
		public final String TASK_TYPE = "type";
		public final String IMAGE = "image";
		public final String DESCRIPTION = "description";
		public final String DELETE_IMAGE = "deleteImage";
		public final String TASK_STATUS_ID = "task_status_id";
	}
	// Common parameter for reminder response
	public interface ADD_REMINDER {
		
		public final String USER_ID = "userId";
		public final String EVENT_ID = "eventId";
		public final String REMINDER_STATUS = "reminder";
		public final String TASK_ID = "taskId";
		
	}
	
	// Common parameter for status update response
	public interface TASK_STATUS {
		public final String NEW = "New";
		public final String COMPLETED = "Completed";
		public final String ACCEPTED = "Accepted";
		public final String IN_PROGRESS = "In Progress";
		public final String REJECTED = "Rejected";
		public final String DELETED = "Deleted";
		public final String EXPIRED = "Expired";
		public final String NOTSTARTED = "Not Started";
	}
	// Common parameter for task status update
	public interface TASK_STATUS_VALUE {
		public final String NEW = "0";
		public final String ACCEPTED = "1";
		public final String IN_PROGRESS = "2";
		public final String REJECTED = "5";
		public final String COMPLETED = "3";
		public final String DELETED = "6";
		public final String EXPIRED = "4";
		public final String COMPLETE = "7";
	}
	
	// Common parameter for task repeat freq
	public interface TASK_REPEAT_FREQ {
		public final String NEVER = "0";
		public final String EVERYDAY = "1";
		public final String EVERYWEEK = "2";
		public final String EVERYTWOWEEK = "3";
		public final String EVERYMONTH = "4";
		public final String EVERYYEAR = "5";
	}
	// Common parameter for repeat type
	public interface REPEATEDTYPE {
		public final String REPEATED = "1";
		public final String NOTREPEATD = "0";
	}
	// Common parameter for reminder sttaus
	public interface REMINDERSTATUS {
		public final String SET_REMINDER = "1";
		public final String DELETE_REMINDER = "0";
	}
	// Common parameter for task deu date status
	public interface TASK_DUE_DATE_STATUS {
		public final String NO_DUE_DATE = "No Due Date";
		public final String DO_IT_TODAY = "Today";
		public final String DO_IT_ANY_TIME_THIS_WEEK = "This week";
		public final String SET_A_DUE_DATE = "Set a due date";
	}
	// Common parameter for task deu date status value
	public interface TASK_DUE_DATE_STATUS_VALUES {
		public final String SET_DUE_DATE = "3";
		public final String NO_DUE_DATE = "0";
		public final String DO_IT_TODAY = "1";
		public final String DO_IT_ANY_TIME_THIS_WEEK = "2";
	}
	// Common parameter for delete task
	public interface DELETE_TASK {
		public final String TASK_ID = "taskId";
		public final String STRIKE_DELETE = "strikeDelete";
	}
	// Common parameter for update task
	public interface UPDATETASK_STATUS {
		public final String TASK_ID = "taskId";
		public final String TASK_STATUS_ID = "status";
	}
	// Common parameter for send reminde rresponse
	public interface SEND_REMINDER {
		public final String TASK_ID = "taskId";
	}
	// Common parameter for update task response
	public interface UPDATE_TASK {
		public final String TASK_ID = "taskId";
		public final String DAU_DATE = "due_date";
		public final String IMAGE = "image";
		public final String DESCRIPTION = "description";
	}
	// Common parameter for compose task response
	public interface COMPOSE_TASK {
		public final String USER_ID = "user_id";
		public final String USER_NAME = "user_name";
		public final String CONTACT_ID = "contact_id";
		
		public final String FRIEND_USER_NAME = "friendusername";
		public final String FRIEND_LIST = "friend_list";
		public final String EDIT_ENABLE = "edit_enable";
		public final String TASK_DESCRIPTION = "task_description";
		
		public final String TASK_DUE_STARTDATE = "due_date_start";
		public final String TASK_DUE_ENDDATE = "due_date_end";
		public final String TASK_DUEDATE_REPEAT = "due_date_repeat";
		public final String TASK_DUEDATE_REPEATEND = "due_date_repeat_end";
		public final String TASK_IS_REPEATEND = "is_repeated";
		
		public final String TASK_ID = "task_id";
		public final String TASK_IMGAGE_URI = "task_image_uri";
		public final String TASK_DUEDATE_STATUS = "due_date_status";
		public final String TASK_TYPE = "task_type";
		public final String MENU_ITEM_OBJECT = "com.assignit.android.menu_item";
		public final String MENU_TASK_COUNTER = "menu_task_counter";
	}
	// Common parameter for notification
	public interface NOTIFICATION {
		public static String ISNOTIFICATION = "notification";
		public static String EVENT = "event";
		public static String EVENTID = "eventId";
		public static String MESSAGE = "message";
		public static String VIEW = "view";
		public static String FOR_ME_EVENT = "1";
		public static String FROM_ME_EVENT = "0";
		public static String FRIENDS = "2";
	}

	public static String TASK_IMAGE_FOLDER_NAME = "AssignIt";
	public static String EVERNOTE_IMAGE_NAME = "EvernoteImage.jpg";
	public static String ACTION_REFRESH = "refresh";
	
	public static String ACTION_REFRESH_FRIENDS = "refresh_friedns";
	public static String ACTION_APP_RATER = "rate_app_friends";
	public static String SELF_FORME_TAB_CHANGE = "self_forme_action";
	public static String DEVICEID = "deviceId";
	public static String PACKAGE_REDIRECT = "com.assignit.android.action";
	public static String DUMMI_TASK_ID = "100000000";
	public static String DATE_PICKER = "datePicker";
	public static String TIME_PICKER = "timePicker";
	public static String VIEW_ID = "viewId";
	public static String CURRENT_HOUR = "hour";
	public static String CURRENT_MINUTES = "minute";

	// upload file width
	public static final int UPLOAD_FILE_WIDTH = 720;
	public static String APPRATING_COUNT = "apprating_count";

	// upload file height
	public static final int UPLOAD_FILE_HEIGHT = 720;

	public static final String STRIKE_DELETE = "1";

	public static final String REJECTED_VALUE = "rejected";
	
	// Preference Constants
	public final static String PREF_NAME = "apprater";
	public final static String PREF_LAUNCH_COUNT = "launch_count";
	public final static String PREF_FIRST_LAUNCHED = "date_firstlaunch";
	public final static String PREF_DONT_SHOW_AGAIN = "dontshowagain";
	public final static String PREF_REMIND_LATER = "remindmelater";
	
		
}
