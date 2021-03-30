package com.assignit.android.ui.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;

import com.assignit.android.R;

/**
 * This custom dialog box to show alert messages.
 * 
 * @author Innoppl
 * 
 */
public class CommonAlertDialog extends Dialog implements OnClickListener {

	private CustomButton mBtnPositive;
	private CustomButton mBtnNegative;

	private CustomTextView mTvTitle;
	private CustomTextView mTvMessage;

	
	private OnClickListener dialogPositiveButtonClickListener;
	private OnClickListener dialogNegativeButtonClickListener;
	private final Context mContext;
	String selectedRepeatStatus;
	
	// For SingleChoice Mode dialog
	private RelativeLayout rl_alert_title;
	private RelativeLayout rl_alert_button_positive;
	private RelativeLayout rl_alert_button_negative;
	
	
	/**
	 * Constructor to initialise the class 
	 * @param context
	 */
	public CommonAlertDialog(Context context) {
		super(context);
		

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.custom_deudate_alert_dialog);

		mContext = context;
		
		
		mBtnPositive = (CustomButton) findViewById(R.id.alert_button_positive);
		mBtnNegative = (CustomButton) findViewById(R.id.alert_button_negative);

		mBtnPositive.setOnClickListener(this);
		mBtnNegative.setOnClickListener(this);
	}
	
	public static CommonAlertDialog createAlertDialog(Context context,			
			OnClickListener clickListener) {

		CommonAlertDialog alertDialog = new CommonAlertDialog(context);
		
		return alertDialog;
	}


	/**
	 * Method to Single Button dialog box
	 * @param titleResource
	 * @param msgResource
	 */
	public void customSingleChoiceAlertDialog(String titleResource,
			String msgResource) {

		setTitle(titleResource);
		setMessage(msgResource);
		setPositiveButton(mContext.getResources().getString(R.string.button_ok));
		show();

	}
	
	
	/**
	 * Method to Show default dialog box
	 * @param title
	 * @param message
	 */
	@SuppressWarnings("deprecation")
	public void createDefaultDialog(String title, String message) {
		final AlertDialog alertDialog = new AlertDialog.Builder(mContext)
				.create();

		alertDialog.setTitle(title);

		alertDialog.setMessage(message);


		// Setting OK Button
		alertDialog.setButton(mContext.getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Write your code here to execute after dialog closed
				alertDialog.dismiss();
			}
		});
		
		

		// Showing Alert Message
		alertDialog.show();
	}
	
	/**
	 * Method to Show default dialog on success
	 * @param title
	 * @param message
	 * @param clickListener
	 */
	public void createDefaulSuccestDialog(String title, String message,OnClickListener clickListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton("Ok",clickListener);
		builder.create();
		builder.setCancelable(false);
		builder.show();
	}


	
	/*
	 * @see android.app.Dialog#setTitle(java.lang.CharSequence)
	 */
	@Override
	public void setTitle(CharSequence title) {
		if (title != null && title.toString().trim().length() > 0) {
			mTvTitle.setText(title);
			rl_alert_title.setVisibility(View.VISIBLE);
		}
		if (title.equals(mContext.getResources().getString(
				R.string.dialog_box_error_title))) {
			mTvTitle.setTextColor(mContext.getResources().getColor(
					R.color.red_color));
		}
	}

	/* 
	 * @see android.app.Dialog#setTitle(int)
	 */
	@Override
	public void setTitle(int titleId) {
		mTvTitle.setText(mContext.getResources().getString(titleId));
		rl_alert_title.setVisibility(View.VISIBLE);
	}

	
	

	/**
	 * Method to Set positive button text
	 * @param text
	 */
	public void setPositiveButton(CharSequence text) {
		mBtnPositive.setText(text);
		mBtnPositive.setVisibility(View.VISIBLE);
		rl_alert_button_positive.setVisibility(View.VISIBLE);
	}

	/**
	 * Method to Set negative button text
	 * @param text
	 */
	public void setNegativeButton(CharSequence text) {
		mBtnNegative.setText(text);
		mBtnNegative.setVisibility(View.VISIBLE);
		rl_alert_button_negative.setVisibility(View.VISIBLE);
	}

	

	/**
	 * Method to Set dialog message method
	 * @param message
	 */
	public void setMessage(CharSequence message) {
		mTvMessage.setText(message);
		mTvMessage.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		if (v == mBtnPositive) {
			if (dialogPositiveButtonClickListener != null) {
				dialogPositiveButtonClickListener.onClick(this,
						DialogInterface.BUTTON_POSITIVE);
				this.dismiss();
			} else {
				this.dismiss();
			}
		} else if (v == mBtnNegative) {
			if (dialogPositiveButtonClickListener != null) {
				dialogNegativeButtonClickListener.onClick(this,
						DialogInterface.BUTTON_NEGATIVE);
				this.dismiss();
			} else {
				this.dismiss();
			}
		}
	}

	@Override
	public void show() {
		try {
			super.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
