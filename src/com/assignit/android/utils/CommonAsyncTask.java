package com.assignit.android.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.assignit.android.R;
import com.assignit.android.ui.views.CustomProgressbar;

/**
 * @author Innoppl Technologies
 * @category AsyncTask class hold all the request and response
 */
public class CommonAsyncTask extends AsyncTask<String, Void, File> {

	private Context context;

	/**
	 * @category Constructor
	 * @param context
	 */
	public CommonAsyncTask(Context context) {
		this.context = context;

	}

	
	/* 
	 * @see android.os.AsyncTask#onPreExecute()
	 * 
	 * Setting progress bar before start the background task
	 */
	@Override
	protected void onPreExecute() {
		CustomProgressbar.showProgressBar(context, false, context
				.getResources().getString(R.string.loading_login_message));
	}

	/* 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 * 
	 * Method to call on  Background task 
	 */
	@Override
	public File doInBackground(String... params) {
		File file = null;
		try {
			if(StringUtils.isNotBlank(params[0])){
			URL url = new URL(params[0]);

			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File(sdCard.getAbsolutePath() + "/"
					+ AppConstant.TASK_IMAGE_FOLDER_NAME);
			if (!dir.isDirectory()) {
				dir.mkdirs();
			}

			file = new File(dir, url.getFile().replace("/", ""));
			if (!file.exists()) {
				{
					InputStream input = null;
					FileOutputStream output = null;

					try {

						input = url.openConnection().getInputStream();
						output = new FileOutputStream(file);

						int read;
						byte[] data = new byte[1024];
						while ((read = input.read(data)) != -1)
							output.write(data, 0, read);

					} finally {
						if (output != null)
							output.close();
						if (input != null)
							input.close();
					}
				}
			}}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return file;
	}

	/*
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 * 
	 * After background task dismiss progress bar and view downloaded image  
	 */
	@Override
	protected void onPostExecute(File response) {
		CustomProgressbar.hideProgressBar();
		if (response != null) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(response), "image/*");
			context.startActivity(intent);
		}
	}
}
