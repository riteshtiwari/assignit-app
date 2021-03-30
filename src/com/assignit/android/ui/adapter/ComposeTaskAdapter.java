package com.assignit.android.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.assignit.android.R;

/**
 * Adapter class for Spinner item in compose task screen.
 * 
 * @author Innoppl
 * 
 */
public class ComposeTaskAdapter extends BaseAdapter {

	List<String> FriendList;
	private final Context mContext;

	public ComposeTaskAdapter(Context Context, List<String> friendList) {
		mContext = Context;
		FriendList = friendList;

	}

	@Override
	public int getCount() {
		return FriendList.size();
	}

	@Override
	public Object getItem(int position) {
		return FriendList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			view = View.inflate(mContext, R.layout.row_spinner_adapter, null);
		} else {
			view = convertView;
		}
		setData(view, position);

		return view;
	}

	/**
	 * Method to set name on spinner list
	 * 
	 * @param view
	 * @param position
	 */
	private void setData(View view, int position) {
		TextView tv = (TextView) view.findViewById(R.id.tv_row_spinner_textview);
		tv.setText(FriendList.get(position));

	}

}
