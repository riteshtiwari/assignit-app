package com.assignit.android.net;

import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.assignit.android.R;
import com.assignit.android.net.responsebean.AddEvernoteRespose;
import com.assignit.android.net.responsebean.AddReminderRespose;
import com.assignit.android.net.responsebean.AssignTaskResponse;
import com.assignit.android.net.responsebean.DeleteTaskResponse;
import com.assignit.android.net.responsebean.ExistingTaskRespose;
import com.assignit.android.net.responsebean.FriendDetailsRespose;
import com.assignit.android.net.responsebean.FriendUploadRespose;
import com.assignit.android.net.responsebean.LoginResponse;
import com.assignit.android.net.responsebean.RegisterResponse;
import com.assignit.android.net.responsebean.SendReminderResponse;
import com.assignit.android.net.responsebean.TaskForMeRespose;
import com.assignit.android.net.responsebean.TaskFromMeRespose;
import com.assignit.android.net.responsebean.UpdateTaskResponse;
import com.assignit.android.net.responsebean.UpdateTaskStatusResponse;
import com.assignit.android.utils.AppConstant;
import com.assignit.android.utils.StringUtils;

/**
 * This class Coordinates clients call with server.
 * 
 * @author Innoppl
 * 
 */
public class WebServiceManager{

	private final WebService webService;

	private static WebServiceManager instance;

	Context context;
	
	String BASEURL;

	/**
	 * COnstructor
	 * @param context
	 */
	private WebServiceManager(Context context) {
		webService = WebService.getInstance(context);
		this.context = context;
		BASEURL = context.getResources().getString(R.string.base_url);
	}

	/**
	 * Initialise the class
	 * @param context
	 * @return
	 */
	public static WebServiceManager getInstance(Context context) {
		if (instance == null) {
			instance = new WebServiceManager(context);
		}
		return instance;
	}
	
	/**
	 * Register with assignit server and return access code to client.
	 * 
	 * @param name
	 * @param countryCode
	 * @param phoneNumber
	 * @return
	 */
	public RegisterResponse register(String name,String countryCode,String phoneNumber) {
		
			JSONObject object = new JSONObject();
			try {
				object.put(AppConstant.REGISTER.NAME,name);
				object.put(AppConstant.REGISTER.API_KEY,AppConstant.APIKEY);
				object.put(AppConstant.REGISTER.COUNTRY_CODE,countryCode);
				object.put(AppConstant.REGISTER.PHONENUMBER,phoneNumber);
				object.put(AppConstant.REGISTER.CLIENT,2);
			} catch (JSONException e) {
				e.printStackTrace();
			} 
		
		RegisterResponse commonResponse = (RegisterResponse) webService.postMethod(BASEURL+context.getResources().getString(R.string.registration),
				object, RegisterResponse.class);
		return commonResponse;
	}
	

	/**
	 * Login with assignit server and return access code to client.
	 * 
	 * @param name
	 * @param countryCode
	 * @param phoneNumber
	 * @param verificationCode 
	 * @param deviceToken
	 * @return
	 */
	
	public LoginResponse userLogin(String name,String countryCode,String phoneNumber,String verificationCode,String deviceToken) {
		
		JSONObject object = new JSONObject();
		try {
			object.put(AppConstant.REGISTER.NAME,name);
			object.put(AppConstant.REGISTER.COUNTRY_CODE,countryCode);
			object.put(AppConstant.REGISTER.PHONENUMBER,phoneNumber);
			object.put(AppConstant.REGISTER.API_KEY,AppConstant.APIKEY);
			object.put(AppConstant.REGISTER.CLIENT,2);
			object.put(AppConstant.USERLOGIN.VERIFICATION_CODE,verificationCode);
			object.put(AppConstant.USERLOGIN.DEVICE_TOKEN,deviceToken);
			
		} catch (JSONException e) {
			e.printStackTrace();
		} 

		LoginResponse commonResponse = (LoginResponse) webService.postMethod(BASEURL+context.getResources().getString(R.string.login),
				object, LoginResponse.class);
		return commonResponse;
	}
	
	
	/**
	 * upload contacts to assignit server and return friends List to client.
	 * 
	 * @param phoneBookFriends
	 * @param userId
	 * @param isUpdated
	 * @return
	 */

	public FriendUploadRespose uploadFriendDetails(JSONArray phoneBookFriends,String userId,String isUpdated) {
				
		JSONObject object = new JSONObject();
		try {
			object.put(AppConstant.FRIEND_DETAILS.PHONE_BOOK,phoneBookFriends);
			object.put(AppConstant.FRIEND_DETAILS.USER_ID,userId);
			object.put(AppConstant.REGISTER.API_KEY,AppConstant.APIKEY);
			object.put(AppConstant.REGISTER.IS_UPDATED,isUpdated);
		} catch (JSONException e) {
			e.printStackTrace();
		} 

		FriendUploadRespose commonResponse = (FriendUploadRespose) webService.postMethod(BASEURL+context.getResources().getString(R.string.contact_upload),
				object, FriendUploadRespose.class);
		return commonResponse;
	}
	
	/**
	 * upload contacts to assignit server and return friends List to client.
	 * 
	 * @param userId
	 * @param friendType
	 * @return
	 */

	public FriendDetailsRespose getFriendDetails(String userId,String friendType) {
				
		JSONObject object = new JSONObject();
		try {
			object.put(AppConstant.FRIEND_DETAILS.USER_ID,userId);
			object.put(AppConstant.FRIEND_DETAILS.FRIEND_TYPE,friendType);
			object.put(AppConstant.REGISTER.API_KEY,AppConstant.APIKEY);
		} catch (JSONException e) {
			e.printStackTrace();
		} 

		FriendDetailsRespose commonResponse = (FriendDetailsRespose) webService.postMethod(BASEURL+context.getResources().getString(R.string.friend_details),
				object, FriendDetailsRespose.class);
		return commonResponse;
	}
	
	/**
	 * Assign new tasks to assignit server 
	 * 
	 * @param userId
	 * @param task
	 * @param image
	 * @return
	 */
	
	public AssignTaskResponse assignNewTask(String userId,JSONObject task,String imageUrl) throws ConnectTimeoutException,SocketTimeoutException{
				
		JSONObject object = new JSONObject();
		try {
			object.put(AppConstant.ASSIGN_TASK.USER_ID,userId);
			object.put(AppConstant.ASSIGN_TASK.TASK,task);
			object.put(AppConstant.REGISTER.API_KEY,AppConstant.APIKEY);
		} catch (JSONException e) {
			e.printStackTrace();
		} 

		AssignTaskResponse commonResponse = (AssignTaskResponse) webService.postMutipart(imageUrl,BASEURL+context.getResources().getString(R.string.assign_task),
				object, AssignTaskResponse.class);
		return commonResponse;
	}
	
	/**
	 * Assign new tasks to assignit server 
	 * 
	 * @param userId
	 * @param task
	 * @return
	 */
	
	public AssignTaskResponse assignTaskWithoutImage(String userId,JSONObject task) {
				
		JSONObject object = new JSONObject();
		try {
			object.put(AppConstant.ASSIGN_TASK.USER_ID,userId);
			object.put(AppConstant.ASSIGN_TASK.TASK,task);
			object.put(AppConstant.REGISTER.API_KEY,AppConstant.APIKEY);
		} catch (JSONException e) {
			e.printStackTrace();
		} 

		AssignTaskResponse commonResponse = (AssignTaskResponse) webService.postMethod(BASEURL+context.getResources().getString(R.string.assign_task),
				object, AssignTaskResponse.class);
		return commonResponse;
	}
	
	
	/**
	 * Update tasks to assignit server 
	 * 
	 * @param userId
	 * @param task
	 * @param imageUrl
	 * @return
	 */
	public UpdateTaskResponse editTask(String userId,JSONObject task,String imageUrl) throws ConnectTimeoutException,SocketTimeoutException {
				
		JSONObject object = new JSONObject();
		try {
			object.put(AppConstant.ASSIGN_TASK.USER_ID,userId);
			object.put(AppConstant.ASSIGN_TASK.TASK,task);
			object.put(AppConstant.REGISTER.API_KEY,AppConstant.APIKEY);
		} catch (JSONException e) {
			e.printStackTrace();
		} 
		UpdateTaskResponse commonResponse = null ;
		if(StringUtils.isBlank(imageUrl)){
			commonResponse = (UpdateTaskResponse) webService.postMethod(BASEURL+context.getResources().getString(R.string.assign_task),
				object, UpdateTaskResponse.class);
		}else{
			commonResponse = (UpdateTaskResponse) webService.postMutipart(imageUrl,BASEURL+context.getResources().getString(R.string.assign_task),
			object, UpdateTaskResponse.class);
		}
		return commonResponse;
	}
	
	/**
	 * Delete tasks to assignit server 
	 * 
	 * @param userId
	 * @param taskId
	 * @return
	 */
	public DeleteTaskResponse deleteTask(String userId,String taskId) {
				
		JSONObject object = new JSONObject();
		try {
			object.put(AppConstant.ASSIGN_TASK.USER_ID,userId);
			object.put(AppConstant.UPDATETASK_STATUS.TASK_ID,taskId);
			object.put(AppConstant.REGISTER.API_KEY,AppConstant.APIKEY);
		} catch (JSONException e) {
			e.printStackTrace();
		} 
		DeleteTaskResponse commonResponse = null ;
		
			commonResponse = (DeleteTaskResponse) webService.postMethod(BASEURL+context.getResources().getString(R.string.delete_task),
				object, DeleteTaskResponse.class);
		
		return commonResponse;
	}
	
	/**
	 * Send Reminder to tasks by assignit server 
	 * 
	 * @param userId
	 * @param taskId
	 * @return
	 */
	public SendReminderResponse remindTask(String userId,String taskId) {
				
		JSONObject object = new JSONObject();
		try {
			object.put(AppConstant.ASSIGN_TASK.USER_ID,userId);
			object.put(AppConstant.UPDATETASK_STATUS.TASK_ID,taskId);
			object.put(AppConstant.REGISTER.API_KEY,AppConstant.APIKEY);
		} catch (JSONException e) {
			e.printStackTrace();
		} 
		SendReminderResponse commonResponse = null ;
		
			commonResponse = (SendReminderResponse) webService.postMethod(BASEURL+context.getResources().getString(R.string.remind_task),
				object, SendReminderResponse.class);
		
		return commonResponse;
	}
	
	/**
	 * Update Task Status to server
	 * 
	 * @param userId
	 * @param taskId
	 * @param status
	 * @return
	 */
	public UpdateTaskStatusResponse updateTaskStatus(String userId,String taskId,String status) {
		
		JSONObject object = new JSONObject();
		try {
			object.put(AppConstant.ASSIGN_TASK.USER_ID,userId);
			object.put(AppConstant.UPDATETASK_STATUS.TASK_ID,taskId);
			object.put(AppConstant.UPDATETASK_STATUS.TASK_STATUS_ID,status);
			object.put(AppConstant.REGISTER.API_KEY,AppConstant.APIKEY);
		} catch (JSONException e) {
			e.printStackTrace();
		} 
		UpdateTaskStatusResponse commonResponse = null ;
		
			commonResponse = (UpdateTaskStatusResponse) webService.postMethod(BASEURL+context.getResources().getString(R.string.update_task_status),
				object, UpdateTaskStatusResponse.class);
		
		return commonResponse;
	}
	
	
	
	
	/**
	 * Getting tasks assigned to me from assignit server 
	 * 
	 * @param userId
	 * @return
	 */
	public TaskForMeRespose getForMeTask(String userId) {
		
		JSONObject object = new JSONObject();
		try {
			object.put(AppConstant.ASSIGN_TASK.USER_ID,userId);
			object.put(AppConstant.REGISTER.API_KEY,AppConstant.APIKEY);
		} catch (JSONException e) {
			e.printStackTrace();
		} 

		TaskForMeRespose commonResponse = (TaskForMeRespose) webService.postMethod(BASEURL+context.getResources().getString(R.string.get_task_for_me),
				object, TaskForMeRespose.class);
		return commonResponse;
	}
	
	/**
	 *  Getting tasks assigned  from me to friends from assignit server 
	 * 
	 * @param userId
	 * @return
	 */
	public TaskFromMeRespose getFromMeTask(String userId) {
		
		JSONObject object = new JSONObject();
		try {
			object.put(AppConstant.ASSIGN_TASK.USER_ID,userId);
			object.put(AppConstant.REGISTER.API_KEY,AppConstant.APIKEY);
		} catch (JSONException e) {
			e.printStackTrace();
		} 

		TaskFromMeRespose commonResponse = (TaskFromMeRespose) webService.postMethod(BASEURL+context.getResources().getString(R.string.get_task_from_me),
				object, TaskFromMeRespose.class);
		return commonResponse;
	}
	
	/**
	 *  Getting existing tasks assigned  from me to friends from assignit server 
	 * 
	 * @param userId
	 * @param toUser
	 * @return
	 */
	public ExistingTaskRespose getExitingTask(String userId,String toUser) {
		
		JSONObject object = new JSONObject();
		try {
			object.put(AppConstant.ASSIGN_TASK.USER_ID,userId);
			object.put(AppConstant.ASSIGN_TASK.TO_USER_ID,toUser);
			object.put(AppConstant.REGISTER.API_KEY,AppConstant.APIKEY);
		} catch (JSONException e) {
			e.printStackTrace();
		} 

		ExistingTaskRespose commonResponse = (ExistingTaskRespose) webService.postMethod(BASEURL+context.getResources().getString(R.string.existing_task),
				object, ExistingTaskRespose.class);
		return commonResponse;
	}
	
	/**
	 *  sending reminder eventid to assignit server 
	 * 
	 * @param userId
	 * @param taskId
	 * @param reminderStatus
	 * @param eventId
	 * @return
	 */
	public AddReminderRespose addReminder(String userId,String taskId,String reminderStatus,String eventId) {
		
		JSONObject object = new JSONObject();
		try {
			object.put(AppConstant.ADD_REMINDER.USER_ID,userId);
			object.put(AppConstant.ADD_REMINDER.TASK_ID,taskId);
			object.put(AppConstant.ADD_REMINDER.REMINDER_STATUS,reminderStatus);
			object.put(AppConstant.ADD_REMINDER.EVENT_ID,eventId);
			object.put(AppConstant.REGISTER.API_KEY,AppConstant.APIKEY);
		} catch (JSONException e) {
			e.printStackTrace();
		} 

		AddReminderRespose commonResponse = (AddReminderRespose) webService.postMethod(BASEURL+context.getResources().getString(R.string.add_reminder),
				object, AddReminderRespose.class);
		return commonResponse;
	}
	
	/**
	 *  Sending Evernote status to assignit server 
	 *
	 * @param userId
	 * @param taskId
	 * @return
	 */
	public AddEvernoteRespose addEvernote(String userId,String taskId) {
		
		JSONObject object = new JSONObject();
		try {
			object.put(AppConstant.ADD_REMINDER.USER_ID,userId);
			object.put(AppConstant.ADD_REMINDER.TASK_ID,taskId);
			object.put(AppConstant.REGISTER.API_KEY,AppConstant.APIKEY);
		} catch (JSONException e) {
			e.printStackTrace();
		} 

		AddEvernoteRespose commonResponse = (AddEvernoteRespose) webService.postMethod(BASEURL+context.getResources().getString(R.string.add_evernote),
				object, AddEvernoteRespose.class);
		return commonResponse;
	}
	/**
	 *  Sending Device Token to server if fails to send on registration time 
	 *
	 * @param userId
	 * @param deviceToken
	 * @return
	 */
	public LoginResponse updateDeviceToken(String userId,String deviceToken) {
		
		JSONObject object = new JSONObject();
		try {
			object.put(AppConstant.ADD_REMINDER.USER_ID,userId);
			object.put(AppConstant.REGISTER.CLIENT,2);
			object.put(AppConstant.USERLOGIN.DEVICE_TOKEN,deviceToken);
			object.put(AppConstant.REGISTER.API_KEY,AppConstant.APIKEY);
		} catch (JSONException e) {
			e.printStackTrace();
		} 

		LoginResponse commonResponse = (LoginResponse) webService.postMethod(BASEURL+context.getResources().getString(R.string.update_device_token),
				object, LoginResponse.class);
		return commonResponse;
	}
	
}
