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
public class TaskFromMeRespose extends BaseResponse {
	
	@SerializedName("tasks_from_me")
	public List<TaskFromMe> tasks;
	
	@SerializedName("msg")
	public String message;
}
