package com.assignit.android.ui.activities;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.assignit.android.R;
import com.assignit.android.ui.views.CustomTextView;
import com.assignit.android.utils.CommonUtils;
import com.googlecode.androidannotations.annotations.EActivity;

/**
 * This is parent class of all activities.
 * 
 * @author Innoppl
 * 
 */
@EActivity(R.layout.activity_base)
public abstract class BaseActivity extends Activity {

	private ImageView btnSerch, btnReferesh, btnDelete, btnNext;

	private ImageView homeScreenLogo, backArrow, profilePic;

	private Spinner FriendName;

	private LinearLayout serchView,bsaelinearlayput;
	
	private ViewGroup mRootViewGroup;

	private CustomTextView mFriendsNameView;

	@Override
	public void setContentView(int layoutResID) {
		mRootViewGroup = (ViewGroup) View.inflate(this, R.layout.activity_base, null);

		mRootViewGroup.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT));
		initTopbarButtons();
		View.inflate(this, layoutResID, (ViewGroup) mRootViewGroup.findViewById(R.id.base_activity_body));
		super.setContentView(mRootViewGroup);

	}
	

	/**
	 * This method is intialize views
	 */
	public void initTopbarButtons() {
		
		bsaelinearlayput = (LinearLayout) mRootViewGroup.findViewById(R.id.linearlayput1);
		serchView = (LinearLayout) mRootViewGroup.findViewById(R.id.home_screen_serch_view);
		homeScreenLogo = (ImageView) mRootViewGroup.findViewById(R.id.base_header_imageview_home_logo_img);
		btnSerch = (ImageView) mRootViewGroup.findViewById(R.id.btnBaseHeaderButtonSearch);
		btnReferesh = (ImageView) mRootViewGroup.findViewById(R.id.btnBaseHeaderButtonRefresh);
		mFriendsNameView = (CustomTextView) mRootViewGroup.findViewById(R.id.mFriendsNameView);
		// for compose task
		FriendName = (Spinner) mRootViewGroup.findViewById(R.id.base_header_spinner_select_frd);
		backArrow = (ImageView) mRootViewGroup.findViewById(R.id.baseHeaderBackArrow);
		profilePic = (ImageView) mRootViewGroup.findViewById(R.id.composeTaskPprofilePicImageView);
		btnDelete = (ImageView) mRootViewGroup.findViewById(R.id.btnBaseHeaderButtonDelete);
		btnNext = (ImageView) mRootViewGroup.findViewById(R.id.btnBaseHeaderButtonNext);

		//IntentFilter filter = new IntentFilter();
		//filter.addAction(AppConstant.PACKAGE_REDIRECT);

	}

	/**
	 * This method is used to show/hide screen home screen logo.
	 * 
	 * @param needToShow
	 */
	public void showHomeScreenLogo(boolean needToShow) {
		if (needToShow)
			homeScreenLogo.setVisibility(View.VISIBLE);
		else
			homeScreenLogo.setVisibility(View.GONE);
	}

	/**
	 * This method is used to show screen friends name title.
	 * 
	 * @param needToShow
	 */
	public void showHomeScreenFriendsName(boolean needToShow) {
		if (needToShow)
			mFriendsNameView.setVisibility(View.VISIBLE);
		else
			mFriendsNameView.setVisibility(View.GONE);
	}

	/**
	 * This method is used to display search button
	 * @param needToShow
	 */
	public void showSerchButton(boolean needToShow) {
		if (needToShow)
			btnSerch.setVisibility(View.VISIBLE);
		else
			btnSerch.setVisibility(View.GONE);
	}

	/**
	 * This method is used to display referesh button
	 * @param needToShow
	 */
	public void showRefereshButton(boolean needToShow) {
		if (needToShow) {
			btnReferesh.setVisibility(View.VISIBLE);
		} else {
			btnReferesh.setVisibility(View.GONE);
		}

	}

	/**
	 * This method is used to display delete button
	 * @param needToShow
	 */
	public void showDeleteButton(boolean needToShow) {
		if (needToShow)
			btnDelete.setVisibility(View.VISIBLE);
		else
			btnDelete.setVisibility(View.GONE);
	}

	/**
	 * This method is used to display next button
	 * @param needToShow
	 */
	public void showNextButton(boolean needToShow) {
		if (needToShow)
			btnNext.setVisibility(View.VISIBLE);
		else
			btnNext.setVisibility(View.GONE);
	}

	/**
	 * This method is used to display home screen logo
	 * @param needToShow
	 */
	public void showSerchView(boolean needToShow) {
		if (needToShow)
			serchView.setVisibility(View.VISIBLE);
		else
			serchView.setVisibility(View.GONE);
	}
	
	/**
	 * This method is used to display home screen logo
	 * @param needToShow
	 */
	public void showBaseLayout(boolean needToShow) {
		if (needToShow)
			bsaelinearlayput.setVisibility(View.VISIBLE);
		else
			bsaelinearlayput.setVisibility(View.GONE);
	}

	/**
	 * This method is used to display Back arrow of compose task
	 * @param needToShow
	 */
	public void showBackArrow(boolean needToShow) {
		if (needToShow)
			backArrow.setVisibility(View.VISIBLE);
		else
			backArrow.setVisibility(View.GONE);
	}

	/**
	 * This method is used to display home screen logo
	 * @param needToShow
	 */
	public void showProfilePic(boolean needToShow) {
		if (needToShow)
			profilePic.setVisibility(View.VISIBLE);
		else
			profilePic.setVisibility(View.GONE);
	}

	/**
	 * This method is used to show friend name in compose task
	 * @param needToShow
	 */
	public void showFriendName(boolean needToShow) {
		if (needToShow)
			FriendName.setVisibility(View.VISIBLE);
		else
			FriendName.setVisibility(View.GONE);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		CommonUtils.hideSoftKeyboard(this);
	}
	
}
