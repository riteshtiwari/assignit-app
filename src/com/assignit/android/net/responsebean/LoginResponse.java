package com.assignit.android.net.responsebean;

import com.google.gson.annotations.SerializedName;

/**
 * This class represent response of login container.
 * 
 * @author Innoppl
 * 
 */
public class LoginResponse extends BaseResponse {
	
	@SerializedName("userId")
	public String userId;

	@SerializedName("msg")
	public String message;

}
