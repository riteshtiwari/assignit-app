package com.assignit.android.net.responsebean;

import java.util.List;

import com.assignit.android.pojo.TaskForMe;
import com.google.gson.annotations.SerializedName;

/**
 * This class represent response of task for me container.
 * 
 * @author Innoppl
 * 
 */
public class TaskForMeRespose extends BaseResponse {

	@SerializedName("tasks_for_me")
	public List<TaskForMe> tasks;
	
	
	@SerializedName("msg")
	public String message;

}

