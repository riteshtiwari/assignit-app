package com.assignit.android.pojo;

import com.google.gson.annotations.SerializedName;
/**
 *  Pojo Class for For me task List
 *  
 *  @author Innoppl
 */
public class FromTaskForMe {
	
	
	
	@SerializedName("userId")
	public String FromUserId;
	
	@SerializedName("name")
	public String FromUsername;
	
	@SerializedName("phone")
	public String FromUserPhone;
	
	@SerializedName("indexId")
	public String FromUserIndexId;
	
	@SerializedName("recordId")
	public String FromUserRecordId;
}
