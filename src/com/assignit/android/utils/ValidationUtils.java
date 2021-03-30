package com.assignit.android.utils;

import com.assignit.android.net.HttpConstants;
import com.assignit.android.net.responsebean.BaseResponse;

/**
 * This class is used for validate the user input as well server response.
 * 
 * @author Innoopl
 * 
 */
public class ValidationUtils {
	/**
	 * method to check whether email address valid or not
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmailValid(String email) {
		String reg = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
		if (email == null || !email.matches(reg)) {
			return false;
		}
		return true;
	}

	/**
	 * Validate phone number
	 * 
	 * @param phNumberStr
	 * @return
	 */
	public static boolean isPhNumberValid(String phNumberStr) {
		if (phNumberStr.length() < 10) {
			return false;
		}
		return true;
	}

	/**
	 * Validate zip code
	 * 
	 * @param zipStr
	 * @return
	 */
	public static boolean isZipValid(String zipStr) {
		if (zipStr.length() < 5) {
			return false;
		}
		return true;
	}


	/**
	 *	Method to check is success Validate response code 
	 * 
	 * @param response
	 * @return
	 */
	public static boolean isSuccessResponse(BaseResponse response) {
		if (response.httpStatusCode != null && response.httpStatusCode == HttpConstants.RESPONSE_CODE.RESPONSE_SUCCESS) {
			return true;
		}

		return false;
	}
	
	/**
	 * Method to check is parameter missing response code
	 * 
	 * @param response
	 * @return
	 */
	public static boolean isParameterMissingResponse(BaseResponse response) {
		if (response.httpStatusCode != null && response.httpStatusCode == HttpConstants.RESPONSE_CODE.RESPONSE_PARAMETER_MISSING) {
			return true;
		}

		return false;
	}
	
	/**
	 * Method to check is parameter value empty response code
	 * 
	 * @param response
	 * @return
	 */
	public static boolean isParameterEmptyResponse(BaseResponse response) {
		if (response.httpStatusCode != null && response.httpStatusCode == HttpConstants.RESPONSE_CODE.RESPONSE_PARAMETER_VALUES_EMPTY) {
			return true;
		}

		return false;
	}
	/**
	 * Method to check is unknown response code
	 * 
	 * @param response
	 * @return
	 */
	public static boolean isUnknownResponse(BaseResponse response) {
		if (response.httpStatusCode != null && response.httpStatusCode == HttpConstants.RESPONSE_CODE.UNKNOWN_METHOD) {
			return true;
		}

		return false;
	}
	/**
	 * Method to check is internal server error response code
	 * 
	 * @param response
	 * @return
	 */
	public static boolean isInternalErrorResponse(BaseResponse response) {
		if (response.httpStatusCode != null && response.httpStatusCode == HttpConstants.RESPONSE_CODE.INTERNAL_SERVER_ERROR) {
			return true;
		}

		return false;
	}



	/**
	 * Method to find is Validate url or not
	 * 
	 * @param url
	 * @return
	 */
	public static boolean isValidURL(String url) {
		boolean isValid = false;

		if (StringUtils.isNotBlank(url) && (url.startsWith("http://") || url.startsWith("https://"))) {
			return true;
		}

		return isValid;
	}

}
