package com.assignit.android.net.responsebean;

import com.google.gson.annotations.SerializedName;

/**
 * This class represent response of friends list container.
 * 
 * @author Innoppl
 * 
 */
public class AddEvernoteRespose extends BaseResponse {

		
	@SerializedName("taskId")
	public String taskId;
	
	@SerializedName("userId")
	public String userId;
	
	@SerializedName("msg")
	public String message;

}
