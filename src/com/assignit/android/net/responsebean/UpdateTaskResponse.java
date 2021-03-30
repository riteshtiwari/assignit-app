package com.assignit.android.net.responsebean;

import com.assignit.android.pojo.UpdateTask;
import com.google.gson.annotations.SerializedName;

/**
 * This class represent response updated taskid container.
 * 
 * @author Innoppl
 * 
 */
public class UpdateTaskResponse extends BaseResponse {

	
	@SerializedName("userId")
	public String userId;
	
	@SerializedName("task")
	public UpdateTask updateTask;
}
