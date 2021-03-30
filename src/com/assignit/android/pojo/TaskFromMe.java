package com.assignit.android.pojo;

import com.google.gson.annotations.SerializedName;
/**
 *  Pojo Sub Class for From me task List 
 *  
 *  @author Innoppl
 */
public class TaskFromMe {

	@SerializedName("task_id")
	public String taskId;

	@SerializedName("description")
	public String Description;

	@SerializedName("image")
	public String Image;

	@SerializedName("due_status")
	public String Status;
	
	@SerializedName("type")
	public String Type;

	@SerializedName("start_date")
	public String DauStartDate;
	
		
	@SerializedName("repeated")
	public String Repeated;
	
	@SerializedName("repeatFreq")
	public String RepeatFreq;
	
	@SerializedName("repeatUntil")
	public String EndRepeat;
	
	@SerializedName("end_date")
	public String DauEndDate;

	@SerializedName("created")
	public String Created;

	@SerializedName("modified")
	public String Modified;

	@SerializedName("to")
	public FromTaskFromMe ToUser;

	public String mSeletedTaskId = "";

	public int position = 10000;

	public boolean mState;
}
