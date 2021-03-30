package com.assignit.android.pojo;

import com.google.gson.annotations.SerializedName;
/**
 *  Pojo  Class for Update task List 
 *  
 *  @author Innoppl
 */
public class UpdateTask {
	
	@SerializedName("task_id")
	public String taskId;

	@SerializedName("description")
	public String Description;
	
	@SerializedName("toUser")
	public String toUser;
		
	@SerializedName("due_status")
	public String Status;
	
	@SerializedName("start_date")
	public String DauStartDate;
	
	@SerializedName("end_date")
	public String DauEndDate;
	
	@SerializedName("repeated")
	public String Repeated;

	@SerializedName("repeatFreq")
	public String RepeatFrequency;
	
	@SerializedName("repeatUntil")
	public String RepeatUntil;
	
}
