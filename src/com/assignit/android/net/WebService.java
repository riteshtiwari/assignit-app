package com.assignit.android.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.assignit.android.net.responsebean.BaseResponse;
import com.assignit.android.utils.AppConstant;
import com.google.gson.Gson;

/**
 * This is represent to remote call(that would be get ,post or postmultipart)
 * 
 * @author Innoppl
 * 
 */
public class WebService {

	private final HttpClient mHttpClient;
	private final Gson mGson;
	Context context;

	private final static String LOG_TAG = WebService.class.getSimpleName();

	/**
	 * Method to initialise this class
	 * 
	 * @param context
	 */
	private WebService(Context context) {
		this.context = context;
		mGson = new Gson();
		mHttpClient = HttpClientFactory.getThreadSafeClient();
	}

	/**
	 * Method to  initialise as singleton class
	 * 
	 * @param context
	 * @return
	 */
	public static WebService getInstance(Context context) {
		WebService instance = new WebService(context);
		return instance;
	}

	
	/**
	 * Post parmas to server and server response back status or some specific
	 * data.
	 * @param url
	 * @param jsonObject
	 * @param clazz
	 * @return
	 */
	public synchronized BaseResponse postMethod(String url, JSONObject jsonObject,
			Class<?> clazz) {

		Log.d("URL CALLED :", url);
		Log.d("PARAMS :", ""+jsonObject);
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		BaseResponse jsonResponse = null;
		try {
		
			HttpPost httpPost = new HttpPost(url);
			nameValuePairs.add(new BasicNameValuePair(AppConstant.DATA, jsonObject.toString()));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = mHttpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			
			if(entity!=null){
				
			Reader reader = new InputStreamReader(entity.getContent());
			jsonResponse = (BaseResponse) mGson.fromJson(reader, clazz);
			jsonResponse.httpStatusCode = response.getStatusLine().getStatusCode();
			
			//setServerError(jsonResponse);
			}
			
		}catch (ConnectTimeoutException ste) {
			ste.printStackTrace();
			return null;
		}catch (SocketTimeoutException ste) {
			ste.printStackTrace();
			return null;
		}catch (SocketException ste) {
			return null;
		} catch (UnknownHostException ste) {
			return null;
		} catch (Exception e) {
			Log.d(LOG_TAG,		"While getting server response server generate error. ");
			e.printStackTrace();
			return null;
		}
		return jsonResponse;
	}
	

	/**
	 * Post parmas to server and server response back status or some specific
	 * data.
	 * @param url
	 * @param jsonObject
	 * @param clazz
	 * @return
	 */
	public synchronized BaseResponse postTestMethod(String url, JSONObject jsonObject,
			Class<?> clazz) {

		Log.d("URL CALLED :", url);
		Log.d("PARAMS :", ""+jsonObject);
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		BaseResponse jsonResponse = null;
		try {
			
			HttpPost httpPost = new HttpPost(url);
			nameValuePairs.add(new BasicNameValuePair(AppConstant.DATA, jsonObject.toString()));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = mHttpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			
			if(entity!=null){
				convertStreamToString(entity.getContent());
		
			}
			
		}catch (SocketTimeoutException ste) {
			ste.printStackTrace();
			return null;
		} catch (SocketException ste) {
			return null;
		} catch (UnknownHostException ste) {
			return null;
		} catch (Exception e) {
			Log.d(LOG_TAG,		"While getting server response server generate error. ");
			e.printStackTrace();
			return null;
		}
		return jsonResponse;
	}
	
	
	/**
	 * This method is used for data streaming purposes.
	 * 
	 * @param imageurl
	 * @param url
	 * @param paramsMap
	 * @param clazz
	 * @return
	 */
	public synchronized BaseResponse postMutipart(String imageurl, String url, JSONObject jsonObject,
			Class<?> clazz) throws SocketTimeoutException,ConnectTimeoutException{

		Log.d("URL CALLED", url);
		Log.d("image uri", imageurl);
		Log.d("PARAMS :", jsonObject.toString());
		
		File imagefile;
		BaseResponse jsonResponse = null;
		try {

		
			HttpPost httpPost = new HttpPost(url);
			imagefile = new File(imageurl);

			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			
			reqEntity.addPart(AppConstant.DATA, new StringBody(jsonObject.toString(), Charset.forName("UTF-8")));
			reqEntity.addPart(AppConstant.ASSIGN_TASK.IMAGE, new FileBody(imagefile));

			httpPost.setEntity(reqEntity);
			
			HttpResponse response = mHttpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if(entity!=null){
				Reader reader = new InputStreamReader(entity.getContent());
			
			jsonResponse = (BaseResponse) mGson.fromJson(reader, clazz);
			jsonResponse.httpStatusCode = response.getStatusLine().getStatusCode();
			
			//setServerError(jsonResponse);
			}
		}catch (SocketException ste) {
			return null;
		} catch (UnknownHostException ste) {
			return null;
		} catch (Exception e) {
			Log.d(LOG_TAG,
					"While getting server response server generate error. "+e);
			return null;
		}	
		return jsonResponse;
	}

	/**
	 * Method to set error response from server response
	 * 
	 * @param baseResponse
	 */
	public void setServerError(BaseResponse baseResponse) {

		if (baseResponse != null && baseResponse.errorMessage != null)
			baseResponse.errorMessage = baseResponse.errorMessage;
	}

	/**
	 * Method to convert input stream to json format string
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public String convertStreamToString(InputStream input) throws IOException {
		InputStream in = input/* your InputStream */;
		InputStreamReader is = new InputStreamReader(in);
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(is);
		String read = br.readLine();

		while (read != null) {
			sb.append(read);
			read = br.readLine();

		}
		return sb.toString();
			
		
	}

	/**
	 * Method to return json object
	 * @param object
	 * @return
	 */
	public static JSONObject Object(Object object) {
		try {
			return new JSONObject(new Gson().toJson(object));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	
}