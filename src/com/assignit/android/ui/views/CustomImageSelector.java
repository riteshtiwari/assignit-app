package com.assignit.android.ui.views;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;

import com.assignit.android.R;
import com.assignit.android.ui.activities.BaseActivity;

/**
 * Custom Image selector
 * 
 * @author Innoppl
 * 
 */
public class CustomImageSelector extends Dialog implements OnClickListener {

	private final BaseActivity mContext;
	private static final int GALARY_PHOTO = 100;
	private static final int CAMERA_PHOTO = 0;

	public CustomImageSelector(BaseActivity context) {
		super(context);

		setContentView(R.layout.custom_image_selector);
		mContext = context;

	}

	/* 
	 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
	 */
	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == 0) {
			Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			mContext.startActivityForResult(cameraIntent, CAMERA_PHOTO);
			dismiss();

		} else if (which == 1) {
			Intent mediaintent = new Intent(Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			mContext.startActivityForResult(mediaintent, GALARY_PHOTO);
			dismiss();

		}

	}

}
