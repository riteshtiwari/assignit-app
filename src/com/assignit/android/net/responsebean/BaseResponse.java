package com.assignit.android.net.responsebean;

import com.google.gson.annotations.SerializedName;

/**
 * This class represent parent class of all response class.
 * 
 * @author Innoppl
 * 
 */
public class BaseResponse {

	@SerializedName("httpStatusCode")
	public Integer httpStatusCode;

	@SerializedName("errorMessage ")
	public String errorMessage;
	
	public BaseResponse() {

	}

	/**
	 * Constructor for the class with parameter
	 * @param code
	 * @param msg
	 */
	public BaseResponse(Integer code, String msg) {
		this.httpStatusCode = code;
		this.errorMessage = msg;
	}

	/**
	 * Method to set error information
	 * @param code
	 * @param msg
	 */
	public void setErrorInfo(Integer code, String msg) {
		this.httpStatusCode = code;
		this.errorMessage = msg;
	}
}
