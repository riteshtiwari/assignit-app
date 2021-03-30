package com.assignit.android.net.responsebean;

import java.util.List;

import com.assignit.android.pojo.TaskFromMe;
import com.google.gson.annotations.SerializedName;

/**
 * This class represent response of task from me container.
 * 
 * @author Innoppl
 * 
 */
public class ExistingTaskRespose extends BaseResponse {
	
	@SerializedName("existing_tasks")
	public List<TaskFromMe> tasks;
	
	@SerializedName("msg")
	public String message;
}
