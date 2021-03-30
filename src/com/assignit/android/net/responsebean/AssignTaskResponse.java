package com.assignit.android.net.responsebean;

import com.google.gson.annotations.SerializedName;

/**
 * This class represent response assigned taskid container.
 * 
 * @author Innoppl
 * 
 */
public class AssignTaskResponse extends BaseResponse {

	@SerializedName("task_id")
	public String TaskId;
	
	@SerializedName("msg")
	public String message;
}
