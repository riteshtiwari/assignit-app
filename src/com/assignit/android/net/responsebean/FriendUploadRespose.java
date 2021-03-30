package com.assignit.android.net.responsebean;

import org.json.JSONArray;

import com.google.gson.annotations.SerializedName;

/**
 * This class represent response of friends list container.
 * 
 * @author Innoppl
 * 
 */
public class FriendUploadRespose extends BaseResponse {

	
	
	@SerializedName("phoneBookFriends")
	public JSONArray phoneBookFriends;
	
	@SerializedName("userId")
	public String userId;
	
	@SerializedName("msg")
	public String message;

}
