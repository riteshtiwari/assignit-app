package com.assignit.android.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.assignit.android.R;
/**
 * Login overlay  Adapter class to display ui list of task assigned by me
 * 
 * @author Innoppl
 * 
 */
public class LoginOverlayAdapter extends BaseAdapter {

	List<String> mItems = new ArrayList<String>();
	Activity mContext;
	int mPosition;
	// ViewFlow mViewFlow;
	Button mNextBtn, mPreviousBtn,mskipBtn;

	// int mNextFlag = 0, mPreFlag = 0;

	public LoginOverlayAdapter(Activity context, List<String> dataItems) {

		this.mItems = dataItems;
		this.mContext = context;
		// mViewFlow = viewFlow;
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = null;
		mPosition = position;
		LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = layoutInflater.inflate(R.layout.overlay_row_item, null);
		
		mPreviousBtn = (Button) view.findViewById(R.id.btnPrevious);
		mNextBtn = (Button) view.findViewById(R.id.btnNext);
		mskipBtn = (Button) view.findViewById(R.id.skipTutorial);
		
		mPreviousBtn.setVisibility(View.GONE);
		mNextBtn.setVisibility(View.GONE);
		mskipBtn.setVisibility(View.GONE);
		
		if (position == 0) {

			
				view.setBackgroundResource(R.drawable.login_overlay);
		}

		 else {
		
				view.setBackgroundResource(R.drawable.login_overlay);
			

		}

		return view;
	}
}
