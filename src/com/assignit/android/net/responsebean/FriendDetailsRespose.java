package com.assignit.android.net.responsebean;

import java.util.List;

import com.assignit.android.pojo.Friends;
import com.google.gson.annotations.SerializedName;

/**
 * This class represent response of friends list container.
 * 
 * @author Innoppl
 * 
 */
public class FriendDetailsRespose extends BaseResponse {
	
	@SerializedName("friends")
	public List<Friends> friends;
	
}
