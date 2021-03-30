package com.assignit.android.net;

/**
 * This is constant class where define and configure server stuff.
 * 
 * @author Innoppl
 * 
 */
public class HttpConstants {

	
// common parameter for http status code
	public interface RESPONSE_CODE {
		
		public final int RESPONSE_SUCCESS = 200;
		public final int RESPONSE_PARAMETER_VALUES_EMPTY = 202;
		public final int RESPONSE_PARAMETER_MISSING = 400;
		public final int UNKNOWN_METHOD = 404;
		public final int INTERNAL_SERVER_ERROR = 500;
		public final int RESPONSE_ERROR = -1;
		
	

	}
// common parameter for error response
	public interface RESPONSE_MESSAGE {
		public final String RESPONSE_USER_NOT_ACTIVE = "User is not activated";
	}
}
