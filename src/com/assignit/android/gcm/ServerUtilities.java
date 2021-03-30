package com.assignit.android.gcm;

import java.util.Random;

import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

/**
 * Server Utility class to register and unregister for assignit.
 * 
 * @author Innoppl
 * 
 */
public final class ServerUtilities {
	private static final int MAX_ATTEMPTS = 5;
	private static final int BACKOFF_MILLI_SECONDS = 2000;
	private static final Random random = new Random();

	private static String TAG = ServerUtilities.class.getSimpleName();

	/**
	 * Register this account/device pair within the server.
	 * 
	 * @param context
	 * @param regId
	 */
	public static void register(final Context context, final String regId) {
		
		Log.i(TAG, "registering device (regId = " + regId + ")");

		long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

		for (int i = 1; i <= MAX_ATTEMPTS; i++) {
			Log.d(TAG, "Attempt #" + i + " to register");
			try {
				//Call Your PHP Server Here and Update device Token
				GCMRegistrar.setRegisteredOnServer(context, true);
				return;
			} catch (Exception e) {
				Log.e(TAG, "Failed to register on attempt " + i + ":" + e);
				if (i == MAX_ATTEMPTS) {
					break;
				}
				try {
					Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
					Thread.sleep(backoff);
				} catch (InterruptedException e1) {
					Log.d(TAG, "Thread interrupted: abort remaining retries!");
					Thread.currentThread().interrupt();
					return;
				}
				backoff *= 2;
			}
		}

		
	}

	/**
	 * Unregister this account/device pair within the server.
	 * 
	 * @param context
	 * @param regId
	 */
	public static void unregister(final Context context, final String regId) {

		Log.i(TAG, "unregistering device (regId = " + regId + ")");

		try {
			//Call Your PHP Server Here and Update device Token
			GCMRegistrar.setRegisteredOnServer(context, false);

		} catch (Exception e) {

		}
		
	}
	
}