package com.assignit.android.pojo;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;
/**
 *  Pojo Class for Friend List
 *  
 *  @author Innoppl
 */
public class Friends implements Serializable{

	
	private static final long serialVersionUID = 1L;

	@SerializedName("user_id")
	public String userId;
	
	@SerializedName("mobile")
	public String contactNumber;
	
	@SerializedName("name")
	public String Name;
		
	@SerializedName("phone")
	public String phoneList;
	
	@SerializedName("indexId")
	public String indexId;
	
	@SerializedName("recordId")
	public String contactId;
	
	@SerializedName("count")
	public String taskCounter = "0";
	
	
	
}
