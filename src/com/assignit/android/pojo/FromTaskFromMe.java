package com.assignit.android.pojo;

import com.google.gson.annotations.SerializedName;
/**
 *  Pojo Class for From me task List
 *  
 *  @author Innoppl
 */
public class FromTaskFromMe {
	@SerializedName("userId")
	public String ToUserId;
	
	@SerializedName("name")
	public String ToUsername;
	
	@SerializedName("phone")
	public String ToUserPhone;
	
	@SerializedName("indexId")
	public String ToUserIndexId;
	
	@SerializedName("recordId")
	public String ToUserRecordId;
}

