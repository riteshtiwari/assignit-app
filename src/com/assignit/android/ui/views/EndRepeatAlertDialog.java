package com.assignit.android.ui.views;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.assignit.android.R;
import com.assignit.android.ui.adapter.EndRepeatDeuDateAdapter;
import com.assignit.android.ui.fragments.DatePickerFragment;
import com.assignit.android.utils.AppConstant;
import com.assignit.android.utils.StringUtils;

/**
 * This custom dialog box to show alert messages.
 * 
 * @author Innoppl
 * 
 */
/**
 * @author Innoppl
 * 
 */
public class EndRepeatAlertDialog extends Dialog implements OnClickListener {

	private CustomButton mBtnPositive;
	private CustomButton mBtnNegative;


	private ListView repeatListView;

	EndRepeatDeuDateAdapter adapter;
	private final Context mContext;
	EndDeuDateListener dateListener;
	String selectedEndRepeatStatus;
	private RelativeLayout rl_alert_button_positive;
	private RelativeLayout rl_alert_button_negative;

	public interface EndDeuDateListener {

		public void onEndDeuDatePicked(String deuEndDateStatus);
	}

	/**
	 * Constructor for EndRepeat Alert Dialog
	 * @param context
	 */
	public EndRepeatAlertDialog(Context context) {
		super(context);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.custom_deudate_alert_dialog);

		mContext = context;
		dateListener = (EndDeuDateListener) mContext;

		mBtnPositive = (CustomButton) findViewById(R.id.alert_button_positive);
		mBtnNegative = (CustomButton) findViewById(R.id.alert_button_negative);

		mBtnPositive.setOnClickListener(this);
		mBtnNegative.setOnClickListener(this);
	}

	/**
	 * Method to create dialog Box
	 * @param context
	 * @param clickListener
	 * @return
	 */
	public static EndRepeatAlertDialog createAlertDialog(Context context,
			OnClickListener clickListener) {

		EndRepeatAlertDialog alertDialog = new EndRepeatAlertDialog(context);

		return alertDialog;
	}

	
	/**
	 * This Method to show End repeat sub menus
	 * 
	 * @param fragmentManager
	 */
	public void createEndRepeatDialog(final FragmentManager fragmentManager,
			String dateStr) {

		final String[] endrepeatitem = mContext.getResources().getStringArray(
				R.array.end_repeat_item);
		adapter = new EndRepeatDeuDateAdapter(mContext, endrepeatitem);
		repeatListView = (ListView) findViewById(R.id.repeatListView);

		repeatListView.setAdapter(adapter);

		if (StringUtils.isNotBlank(dateStr)) {
			if (dateStr.equals(endrepeatitem[0])) {
				selectedEndRepeatStatus = endrepeatitem[0];
				adapter.selectItem(0);
				adapter.setNameDate(dateStr);
			} else {
				selectedEndRepeatStatus = endrepeatitem[1];
				adapter.selectItem(1);
				adapter.setNameDate(dateStr);
			}
			adapter.notifyDataSetChanged();
		}

		repeatListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				selectedEndRepeatStatus = endrepeatitem[position];

				adapter.selectItem(position);
				adapter.notifyDataSetChanged();

				if (position == 1) {

					DialogFragment newFragment = new DatePickerFragment();
					Bundle bundle = new Bundle();
					bundle.putInt(AppConstant.VIEW_ID, R.id.tvDeudateEndRepeat);
					newFragment.setArguments(bundle);
					newFragment.show(fragmentManager, AppConstant.DATE_PICKER);

				}else if(position == 0){
					selectedEndRepeatStatus = endrepeatitem[0];
				}
			}
		});
	}

	/**
	 * Callback method to set date on compose task
	 * 
	 * @param dateStr
	 */
	public void setDate(String dateStr) {

		adapter.setNameDate(dateStr);
		adapter.notifyDataSetChanged();
		selectedEndRepeatStatus = dateStr;
	}

	/**
	 * Callback method to set end deu date status on list
	 * @param endDateNever
	 */
	public void setEndDeuStatus(String endDateNever) {
		final String[] endrepeatitem = mContext.getResources().getStringArray(
				R.array.end_repeat_item);
		adapter = new EndRepeatDeuDateAdapter(mContext, endrepeatitem);
		if(StringUtils.isNotBlank(selectedEndRepeatStatus)){
		if(!selectedEndRepeatStatus.equals(endrepeatitem[0])){
		adapter.setNameDate(endDateNever);}else{
			adapter.setNameDate(endrepeatitem[0]);
		}}
		adapter.notifyDataSetChanged();
		if(StringUtils.isNotBlank(selectedEndRepeatStatus)){
			if(!selectedEndRepeatStatus.equals(endrepeatitem[0])){
				selectedEndRepeatStatus = endDateNever;}
		}
	}


	/**
	 * Method to set text for positive button
	 * @param text
	 */
	public void setPositiveButton(CharSequence text) {
		mBtnPositive.setText(text);
		mBtnPositive.setVisibility(View.VISIBLE);
		rl_alert_button_positive.setVisibility(View.VISIBLE);
	}

	/**
	 * Method to set text for negative button
	 * @param text
	 */
	public void setNegativeButton(CharSequence text) {
		mBtnNegative.setText(text);
		mBtnNegative.setVisibility(View.VISIBLE);
		rl_alert_button_negative.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		 String[] endrepeatitem = mContext.getResources().getStringArray(R.array.end_repeat_item);
		if (v == mBtnPositive) {
			
			
			
			if(StringUtils.isNotBlank(selectedEndRepeatStatus)){
				Log.e("temperatuer----->", ""+selectedEndRepeatStatus);
			if(!selectedEndRepeatStatus.equals(endrepeatitem[1]))
			{
				Log.e("temperatuer", ""+selectedEndRepeatStatus);
			dateListener.onEndDeuDatePicked(selectedEndRepeatStatus);
			}}
			this.dismiss();
		} else if (v == mBtnNegative){
			
			this.dismiss();
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
