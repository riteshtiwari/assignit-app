package com.assignit.android.net.responsebean;

import com.google.gson.annotations.SerializedName;

/**
 * This class represent response of login container.
 * 
 * @author Innoppl
 * 
 */
public class RegisterResponse extends BaseResponse {


	@SerializedName("verificationCode")
	public String verificationCode;
	
	@SerializedName("msg")
	public String message;

}
