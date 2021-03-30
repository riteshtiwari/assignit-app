package com.assignit.android.ui.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.assignit.android.R;
import com.assignit.android.utils.CommonUtils;

/**
 * Custom Spinner Adapter Class.
 * 
 * @author Innoppl
 * 
 */
@SuppressLint("ResourceAsColor")
public class CustomSpinnerAdapter extends ArrayAdapter<String> {

	// if spinner value is required then dropdown list should not display hint.
	private final boolean mRequired;

	private final Activity mContext;

	// the to be displayed for the spinner
	String customHint;

	public CustomSpinnerAdapter(Activity context, List<String> items, boolean isRequired) {
		this(context, items, isRequired, "Spinner Hint");
	}

	public CustomSpinnerAdapter(Activity context, List<String> items, boolean isRequired, String customHint) {
		super(context, android.R.layout.simple_spinner_item, items);

		this.mContext = context;
		this.mRequired = isRequired;
		this.customHint = customHint;
		setDropDownViewResource(android.R.layout.simple_spinner_item);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = super.getView(position, convertView, parent);
		TextView tv = ((TextView) v.findViewById(android.R.id.text1));
		
		if (position == 0) {
			tv.setText("");
			tv.setHint(getItem(0));// "Hint to be displayed"
		}
		tv.setTextSize(15);
		tv.setTextColor(R.color.spinner_header_color);
		tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
		return v;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {

		View v = null;
		if (mRequired & position == 0) {
			// a not selectable blank view in place of hint
			TextView tv = new TextView(mContext);
			tv.setHeight(0);
			tv.setVisibility(View.GONE);

			v = tv;
		} else {

			// convertView needs to be null, if we pass it as not null it hides
			// some of the elements because we set height of first element (HINT in this case) to 0
			// (if its required field)
			v = super.getDropDownView(position, null, parent);
		}
		CommonUtils.hideSoftKeyboard(mContext);
		v.setPadding(20, 15, 20, 15);
		return v;
	}

}
