package com.assignit.android.net;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * This class represent media of app to server.
 * 
 * @author mInnoppl
 * 
 */
public class HttpClientFactory {

	private static DefaultHttpClient client;

	/**
	 * Setting timeout and initialise httpclient
	 * 
	 * @return
	 */
	public  static DefaultHttpClient getThreadSafeClient() {

		if (client != null)
			return client;

		client = new DefaultHttpClient();

		ClientConnectionManager mgr = client.getConnectionManager();
		
		int TIMEOUT_MILLISEC = 30000; // = 30 seconds
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params,TIMEOUT_MILLISEC);
		HttpConnectionParams.setSoTimeout(params, TIMEOUT_MILLISEC);
		
		client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, mgr.getSchemeRegistry()), params);

		return client;
	}
}
