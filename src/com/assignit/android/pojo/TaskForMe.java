package com.assignit.android.pojo;

import com.google.gson.annotations.SerializedName;
/**
 *  Pojo Sub Class for For me task List 
 *  
 *  @author Innoppl
 */
public class TaskForMe {
	
	@SerializedName("task_id")
	public String taskId;

	@SerializedName("description")
	public String Description;
	
	@SerializedName("image")
	public String Image;
	
	@SerializedName("reminder")
	public String reminderStatus;
	
	@SerializedName("event_id")
	public String eventId;
	
	@SerializedName("from")
	public FromTaskForMe fromUser;
	
	@SerializedName("evernote")
	public String evernoteStatus;
	
	@SerializedName("due_status")
	public String Status;
	
	@SerializedName("type")
	public String Type;
	
	@SerializedName("start_date")
	public String DauStartDate;
	
	@SerializedName("end_date")
	public String DauEndDate;
	
	@SerializedName("created")
	public String Created;

	@SerializedName("modified")
	public String Modified;
	
	@SerializedName("repeated")
	public String Repeated;
	
	@SerializedName("repeatFreq")
	public String RepeatFreq;
	
	@SerializedName("repeatUntil")
	public String EndRepeat;
	
	public String laststatus = "";

	public String mSeletedTaskId = "";

	public int position = 10000;

	public boolean mState;
}
