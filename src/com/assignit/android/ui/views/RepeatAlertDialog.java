package com.assignit.android.ui.views;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.assignit.android.R;
import com.assignit.android.ui.adapter.EndRepeatDeuDateAdapter;
import com.assignit.android.ui.adapter.RepeatDeuDateAdapter;
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
public class RepeatAlertDialog extends Dialog implements OnClickListener {

	private CustomButton mBtnPositive;
	private CustomButton mBtnNegative;


	private ListView repeatListView;

	EndRepeatDeuDateAdapter adapter;
	private final Context mContext;
	DeuDateListener dateListener;
	String selectedRepeatStatus, endDateRepeat;
	// For SingleChoice Mode dialog
	private RelativeLayout rl_alert_button_positive;
	private RelativeLayout rl_alert_button_negative;

	public interface DeuDateListener {
		public void onDeuDatePicked(String deuDateStatus);
	}

	/**
	 * Constructor for the class
	 * @param context
	 */
	public RepeatAlertDialog(Context context) {
		super(context);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.custom_deudate_alert_dialog);

		mContext = context;
		dateListener = (DeuDateListener) mContext;

		mBtnPositive = (CustomButton) findViewById(R.id.alert_button_positive);
		mBtnNegative = (CustomButton) findViewById(R.id.alert_button_negative);

		mBtnPositive.setOnClickListener(this);
		mBtnNegative.setOnClickListener(this);
	}

	/**
	 * Method to create alert dialog box
	 * 
	 * @param context
	 * @param clickListener
	 * @return
	 */
	public static RepeatAlertDialog createAlertDialog(Context context,
			OnClickListener clickListener) {

		RepeatAlertDialog alertDialog = new RepeatAlertDialog(context);

		return alertDialog;
	}

	/**
	 * This method to show repeat task sub menus
	 *
	 * @param dateStr
	 */
	public void createRepeatDialog(String dateStr) {

		final String[] repeatitem = mContext.getResources().getStringArray(
				R.array.repeat_item);
		final RepeatDeuDateAdapter adapter = new RepeatDeuDateAdapter(mContext,
				repeatitem);
		repeatListView = (ListView) findViewById(R.id.repeatListView);

		repeatListView.setAdapter(adapter);

		// To view previous selected tick mark
		if (StringUtils.isNotBlank(dateStr)) {
			for (int i = 0; i < repeatitem.length; i++) {
				if (dateStr.equals(repeatitem[i])) {
					selectedRepeatStatus = repeatitem[i];
					adapter.selectItem(i);
					adapter.notifyDataSetChanged();
				}
			}
		}

		repeatListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				selectedRepeatStatus = repeatitem[position];

				adapter.selectItem(position);
				adapter.notifyDataSetChanged();

			}
		});

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
		if (v == mBtnPositive) {
			
			dateListener.onDeuDatePicked(selectedRepeatStatus);
			
		
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
