package com.assignit.android.net.responsebean;

import com.google.gson.annotations.SerializedName;

/**
 * This class represent response updated task status taskid container.
 * 
 * @author Innoppl
 * 
 */
public class UpdateTaskStatusResponse extends BaseResponse {
	
	@SerializedName("msg")
	public String message;
	
	

}
