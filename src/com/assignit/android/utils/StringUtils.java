package com.assignit.android.utils;


/**
 * String utility class to perform common string operation.
 * 
 * @author Innoppl
 * 
 */
public class StringUtils {
	
	/**
	 * Returns false if string is null or empty or blank.
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNotBlank(String str) {
		if (str == null || str.trim().equalsIgnoreCase(""))
			return false;
		return true;
	}

	/**
	 * Returns false if string is null or empty or blank.
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str) {
		if (str == null || str.trim().equalsIgnoreCase(""))
			return true;
		return false;
	}

	
}
