
package com.assignit.android;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.assignit.android.utils.CommonUtils;
/**
 * This class is used for sending crash report to server.
 * 
 * @author Innoppl
 * 
 */
public class LogFileWebService implements Runnable
{
	
	Context 	context;
	String 		project			=			"Assignit - Android";
	String 		device_type		=			"1";
	String 		log				=			"";
	Thread 		t;
	String client_id = "";
	
	public LogFileWebService(Context con, String log, String client_id)
	{
		context 		=		 con;
		this.log		=		 log;
		this.client_id = client_id;
		if (CommonUtils.isNetworkAvailable(context)) {
			t 				=		 new Thread(this);
			t.start();
		}	
	}
	
	/**
	 * method to return device detials to service method
	 * 
	 * @return
	 */
	private String writeService()
	{
		try
		{	
			HttpClient httpclient = new DefaultHttpClient();
	        HttpPost httppost = new HttpPost(context.getResources().getString(R.string.crash_report_url));	        
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
	        nameValuePairs.add(new BasicNameValuePair("project", project));
	        nameValuePairs.add(new BasicNameValuePair("device_type", device_type));
	        nameValuePairs.add(new BasicNameValuePair("device_token",  client_id));
	        nameValuePairs.add(new BasicNameValuePair("log", log));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 
	        HttpResponse response = httpclient.execute(httppost);       
			
			return getHttpResponse(response.getEntity());
			
		}catch (Exception e) {
			Log.e("Error", e.toString());
			return "error";
		}
	}
	
	/**
	 * method to Write log to service method
	 * 
	 * @return
	 */
	private String writeLogFile()
	{
		String		line1 		=		 "\n \n = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = >";
		String		line2 		=		line1+ "\n \n Time			: "+new Date() + " - LOCAL DEVICE TIME";
		String 		line3		= 		line2+ "\n Device			: "+getDeviceName();
		String 		line4		= 		line3+ "\n OS Version		: "+Build.VERSION.RELEASE;
		String 		line5		=		line4+"\n \n Message		: "+log;
		String 		line6		=		line5+"\n = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = >\n \n";
					log			=		line6;
		return writeService();
	}
	
		
	/**
	 * Getting http response call
	 * 
	 * @param entity
	 * @return
	 */
	private String getHttpResponse(HttpEntity entity)
	{
		try
		{
			if (entity != null) 
			{
				InputStream inputStream = entity.getContent();
				InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream);
				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);
				StringBuilder stringBuilder = new StringBuilder();
				String res = "";
				while ((res = bufferedReader.readLine()) != null) {
					stringBuilder.append(res);
				}
				Log.e("error report", "-----"+stringBuilder.toString());
				return stringBuilder.toString();
			} else {
				return "error";
			}
		}catch (Exception e) {
			return "error";
		}
	}
	
	/**
	 * method to Get a  device name
	 * 
	 * @return
	 */
	public String getDeviceName() {
		  String manufacturer = Build.MANUFACTURER;
		  String model = Build.MODEL;
		  if (model.startsWith(manufacturer)) {
		    return model;
		  } else {
		    return manufacturer + " " + model;
		  }
		}

	
	@Override
	public void run()
	{
		try
		{
			writeLogFile();
		}
		catch(Exception e)
		{
			Log.e("LogFileWebService- ", "Write Log: "+e);
		}
		
	}	
}