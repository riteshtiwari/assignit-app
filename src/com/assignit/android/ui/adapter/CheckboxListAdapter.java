package com.assignit.android.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.assignit.android.R;

/**
 * BaseAdapter implementation which will inflate the layout "element_example".
 * 
 * You can find more information in my <a href="http://schimpf.es">blog</a>.
 * 
 * @see <a href="http://schimpf.es">Blog</a>
 * @author Innoppl
 * 
 */
public class CheckboxListAdapter extends BaseAdapter {
	Context mContext;
	/** The inflator used to inflate the XML layout */
	private LayoutInflater inflator;

	/** A list containing some sample data to show. */
	private List<SampleData> dataList;

	public CheckboxListAdapter(LayoutInflater inflator, Context context) {
		super();
		this.inflator = inflator;
		this.mContext = context;
		dataList = new ArrayList<SampleData>();
		String[] items = context.getResources().getStringArray(R.array.repeat_item);
		for (int i = 0; i < items.length; i++) {
			dataList.add(new SampleData(items[i], false));
		}
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {

		// We only create the view if its needed
		if (view == null) {
			view = inflator.inflate(R.layout.element_example, null);

			// Set the click listener for the checkbox
			// /view.findViewById(R.id.checkBox1).setOnClickListener(this);
		}

		SampleData data = (SampleData) getItem(position);

		// Set the example text and the state of the checkbox
		// ImageView cb = (ImageView) view.findViewById(R.id.checkBox1);

		TextView tv = (TextView) view.findViewById(R.id.textView1);
		tv.setText(data.getName());

		return view;
	}

	/*
	 * @Override
	 *//** Will be called when a checkbox has been clicked. */
	/*
	 * public void onClick(View view) { SampleData data = (SampleData)
	 * view.getTag(); data.setSelected(((ImageView) view).isChecked()); }
	 */

}
