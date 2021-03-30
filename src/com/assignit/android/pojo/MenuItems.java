package com.assignit.android.pojo;

import java.io.Serializable;
import java.util.List;

import android.graphics.drawable.Drawable;
/**
 *  Pojo Class for menu List
 *  
 *  @author Innoppl
 */
public class MenuItems implements Serializable {

	// http://stackoverflow.com/questions/285793/what-is-a-serialversionuid-and-why-should-i-use-it
	private static final long serialVersionUID = -5669688293199114033L;
	
	
	public String userId;
	public String userName;
	public String contactId;
	
	
	public String itemName;
	public Drawable drawable;
	//added temp static counter value 
	public String taskCounter = "0";
	//public Integer taskCounter;
	public Boolean isCounterVisible;
	public Boolean isEvernoteVisible = Boolean.FALSE;
	public Boolean isFriendsTab = Boolean.FALSE;
	public String friendUserId;
	
	public String friendUserName;
	public List<Friends> frindsList;
	public Boolean isFriendList = Boolean.FALSE;
	public String taskId = "";
	public String evernoteSatus = "";
	public String evernoteCreatedDate = "";
	public String lastStatus = "";
	public String taskDescription = "";
	public String eventId = "";
	public String taskStartDueDate = "";
	public String taskEndDueDate = "";
	public String taskRepeatDueDate = "";
	public String taskEndRepeatDueDate = "";
	public String isRepeated = "";
	
	public String taskType = "";
	public String imageURI = "";
	public String taskStatus = "";
	
	
}
