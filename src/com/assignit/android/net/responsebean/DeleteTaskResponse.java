package com.assignit.android.net.responsebean;

import com.google.gson.annotations.SerializedName;

/**
 * This class represent response of deleted taskid container.
 * 
 * @author Innoppl
 * 
 */
public class DeleteTaskResponse extends BaseResponse {

	@SerializedName("msg")
	public String message;
}
