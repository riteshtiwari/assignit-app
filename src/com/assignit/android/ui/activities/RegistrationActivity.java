package com.assignit.android.ui.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.assignit.android.R;
import com.assignit.android.localstorage.UserSharedPreferences;
import com.assignit.android.net.WebServiceManager;
import com.assignit.android.net.responsebean.LoginResponse;
import com.assignit.android.net.responsebean.RegisterResponse;
import com.assignit.android.ui.adapter.CountriesListAdapter;
import com.assignit.android.ui.views.CommonAlertDialog;
import com.assignit.android.ui.views.CustomEditText;
import com.assignit.android.ui.views.CustomProgressbar;
import com.assignit.android.ui.views.CustomTextView;
import com.assignit.android.ui.views.alphabeticalIndex.Listview.IndexableListView;
import com.assignit.android.utils.AppConstant;
import com.assignit.android.utils.CommonUtils;
import com.assignit.android.utils.StringUtils;
import com.assignit.android.utils.ValidationUtils;
import com.google.android.gcm.GCMRegistrar;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.SystemService;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.StringArrayRes;

/**
 * This is central activity for User Registration.
 * 
 * @author Innoppl
 * 
 */
@EActivity(R.layout.activity_register)
public class RegistrationActivity extends BaseActivity implements
		OnFocusChangeListener, TextWatcher,OnClickListener {

	@ViewById
	Button btnGetCode;

	@ViewById
	public CustomEditText etSearchBox;

	@ViewById
	Button btnLogin;

	@ViewById
	ImageView imgviewLoginLogo;

	@ViewById
	ImageView cleareSearch;
	
	@ViewById
	CustomEditText etName;

	@ViewById
	CustomEditText etCountryCode;

	@ViewById
	CustomEditText etPhoneNumber;

	public boolean isSearchBoxEnable = false;

	@ViewById
	CustomEditText etVerifyCode;

	@ViewById
	public CustomTextView noMatchFoundView;

	@ViewById
	IndexableListView mCountryCodeListView;

	ArrayList<HashMap<String, String>> searchResults;

	ArrayList<HashMap<String, String>> originalValues;

	@ViewById
	RelativeLayout rlCountryList;

	@ViewById
	RelativeLayout rlGetCode;

	@ViewById
	LinearLayout llUserLogin;

	CommonAlertDialog alertDialog;

	@ViewById
	LinearLayout llRegistration;

	@StringArrayRes
	String[] CountryCodes;

	CountriesListAdapter adapter;

	@SystemService
	TelephonyManager telephonyManager;

	String countryCode, countryKey;

	String[] stockArr;

	String countryCod = "";
	
	LinearLayout.LayoutParams layoutParam;
	
	LinearLayout.LayoutParams Param;
	
	private final String TAG = RegistrationActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		countryKey = telephonyManager.getSimCountryIso();
		if (StringUtils.isBlank(countryKey)) {
			countryKey = getResources().getConfiguration().locale.getCountry();
		}
		originalValues = new ArrayList<HashMap<String, String>>();

		int noOfCountries = CountryCodes.length;
		stockArr = new String[noOfCountries];
		for (int i = 0; i < noOfCountries; i++) {

			GetCountryZipCode(CountryCodes[i].split(",")[1]);
			
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(AppConstant.C_NAME,	GetCountryZipCode(CountryCodes[i].split(",")[1]));
			map.put(AppConstant.C_CODE, (CountryCodes[i].split(",")[0]));
			map.put(AppConstant.C_SUFFIX, (CountryCodes[i].split(",")[1]));

			originalValues.add(map);

			stockArr[i] = (CountryCodes[i].split(",")[0]) + ","
					+ (CountryCodes[i].split(",")[1]);
		}
	}
	/**
	 * this method is called to get country name from Id
	 */
	private String GetCountryZipCode(String countryId) {
		Locale locale = new Locale("", countryId);

		return locale.getDisplayCountry().trim();
	}
	/**
	 * this method is called on configuration changes to fix the layout for both view
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		 	layoutParam = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			Param = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
		int orientation = getResources().getConfiguration().orientation;
		switch (orientation) {
		case Configuration.ORIENTATION_LANDSCAPE:
		
			layoutParam.setMargins(0, 0, 0, 0);
			Param.setMargins(0, 5, 0, 5);
			llUserLogin.setLayoutParams(layoutParam);
			rlGetCode.setLayoutParams(Param);
			imgviewLoginLogo.getLayoutParams().height = CommonUtils.convertToDp(this, 135);
			btnLogin.getLayoutParams().height = CommonUtils.convertToDp(this,40);
			btnLogin.getLayoutParams().width = CommonUtils.convertToDp(this, 40);
			break;
		case Configuration.ORIENTATION_PORTRAIT:
		
			layoutParam.setMargins(0, 40, 0, 40);
			Param.setMargins(0, 20, 0, 15);
			llUserLogin.setLayoutParams(layoutParam);
			rlGetCode.setLayoutParams(Param);
			imgviewLoginLogo.getLayoutParams().height = CommonUtils.convertToDp(this, 150);
			btnLogin.getLayoutParams().height = CommonUtils.convertToDp(this,50);
			btnLogin.getLayoutParams().width = CommonUtils.convertToDp(this, 50);
			break;
		}
	}

	/**
	 * this method is called after view is initialised
	 */
	@AfterViews
	public void afterInitView() {
	
		countryCod = etCountryCode.getText().toString();
		alertDialog = new CommonAlertDialog(this);
		showBaseLayout(false);

		Boolean island = getResources().getBoolean(R.bool.isLandscape);
		if (!island) {

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			LinearLayout.LayoutParams Params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(0, 40, 0, 40);
			Params.setMargins(0, 20, 0, 15);
			llUserLogin.setLayoutParams(layoutParams);
			rlGetCode.setLayoutParams(Params);

		}

		if (StringUtils.isNotBlank(countryKey)) {
			for (String con : CountryCodes) {
				String[] countryList = con.split(",");
				if (countryKey.equalsIgnoreCase(countryList[1].trim()
						.toString())) {
					countryCode = getString(R.string.country_code)
							+ countryList[0].trim().toString();
					etCountryCode.setText(countryCode);
				}
			}
		}
		etCountryCode.setOnClickListener(this);
		etCountryCode.setOnFocusChangeListener(this);
		mCountryCodeListView.setFastScrollEnabled(true);
		mCountryCodeListView.setTextFilterEnabled(true);
		etName.addTextChangedListener(this);
		etPhoneNumber.addTextChangedListener(this);
		etVerifyCode.addTextChangedListener(this);
		etSearchBox.addTextChangedListener(this);

	}

	/**
	 * THIS METHOD IS CALLED WHEN USER PRESS COUNTRY CODE BUTTONS
	 */
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(v.getId() == R.id.etCountryCode) {
			CommonUtils.hideSoftKeyboard(this);
			if (hasFocus) {
				
				etCountryCode.setCursorVisible(false);
				
			}
		}
	}
	
	
	/**
	 * Getting country code by number
	 * 
	 * @param countryCodeNumber
	 */
	private void getCountryCode(String countryCodeNumber){
		for (String con : CountryCodes) {
			String[] countryList = con.split(",");
			if (etCountryCode.getText().toString().replace("+", "").equals(countryList[0].trim()
					.toString())) {
				 countryKey =  countryList[1].trim().toString();
		}}
	}

	/**
	 * THIS METHOD IS CALLED WHEN USER PRESS GET CODE BUTTONS
	 */

	@Click
	public void btnGetCode() {

		if (StringUtils.isBlank(etName.getText().toString())) {

			alertDialog.createDefaultDialog(
					getResources().getString(R.string.dialog_box_error_title),
					getResources().getString(R.string.name_char_empty));

		} else if (etName.getText().length() == 1) {
			alertDialog.createDefaultDialog(
					getResources().getString(R.string.dialog_box_error_title),
					getResources().getString(R.string.name_char_length));

		} else {

			if (CommonUtils.isNetworkAvailable(this)) {
				String name = etName.getText().toString();
				String countryCode = etCountryCode.getText().toString();
				String phoneNumber = etPhoneNumber.getText().toString();
				CustomProgressbar.showProgressBar(RegistrationActivity.this,
						false, getString(R.string.loading_login_message));
				register(name, countryCode, phoneNumber);
			} else {
				CommonUtils.showErrorToast(
						this,
						getResources().getString(
								R.string.no_internate_connetion));
			}
		}
	}

	/**
	 * THIS METHOD IS CALLED WHEN USER PRESS LOGIN BUTTONS
	 */
	@Click
	public void btnLogin() {

		if (StringUtils.isBlank(etName.getText().toString())) {

			alertDialog.createDefaultDialog(
					getResources().getString(R.string.dialog_box_error_title),
					getResources().getString(R.string.name_char_empty));

		} else if (etName.getText().length() < 2) {
			alertDialog.createDefaultDialog(
					getResources().getString(R.string.dialog_box_error_title),
					getResources().getString(R.string.name_char_length));

		} else if (StringUtils.isBlank(etPhoneNumber.getText().toString())) {

			alertDialog.createDefaultDialog(
					getResources().getString(R.string.dialog_box_error_title),
					getResources().getString(R.string.phone_char_empty));

		} else if (etPhoneNumber.getText().length() < 7) {
			alertDialog.createDefaultDialog(getResources().getString(R.string.dialog_box_error_title),
					getResources().getString(R.string.phone_char_length));

		} else if (CommonUtils.isNetworkAvailable(this)) {
			String deviceToken = "";
			deviceToken = UserSharedPreferences.getInstance(this).getString(AppConstant.LOGIN.ACCESS_TOKEN);
						if(deviceToken.equals("")){
							UserSharedPreferences.getInstance(this).putBoolean(AppConstant.LOGIN.ACCESS_TOKEN_STATUS, Boolean.FALSE);
						}else{
							UserSharedPreferences.getInstance(this).putBoolean(AppConstant.LOGIN.ACCESS_TOKEN_STATUS, Boolean.TRUE);
						}
			CustomProgressbar.showProgressBar(RegistrationActivity.this, false,	getString(R.string.loading_login_message));
			login(etName.getText().toString(), etCountryCode.getText().toString(), etPhoneNumber.getText().toString(), etVerifyCode.getText().toString(), deviceToken);
		} else {
			CommonUtils.showErrorToast(this,
					getResources().getString(R.string.no_internate_connetion));
		}
	}

	/**
	 * THIS METHOD IS TO CALL WEBSERVICE TO GET LOGINRESPONSE
	 * @param name
	 * @param countryCode
	 * @param phoneNumber
	 * @param verificationCode
	 * @param deviceToken
	 */
	@Background
	public void login(String name, String countryCode, String phoneNumber,
			String verificationCode, String deviceToken) {

		LoginResponse response = null;
		response = WebServiceManager.getInstance(this).userLogin(name,
				countryCode, phoneNumber, verificationCode, deviceToken);

		afterLogin(response);
	}

	/**
	 * THIS METHOD IS TO CALL ON AFTER LOGIN TO GET INTO HOME SCREEN IF LOGIN
	 * SUCCESS
	 * @param response
	 */
	@UiThread
	public void afterLogin(LoginResponse response) {
		if (response != null) {
			if (response.httpStatusCode != null) {
				if (StringUtils.isNotBlank(response.message)) {

					if (ValidationUtils.isSuccessResponse(response)) {
						if (StringUtils.isNotBlank(response.userId)) {
							UserSharedPreferences.getInstance(
									RegistrationActivity.this).putBoolean(
									AppConstant.ISACTIVEUSER, Boolean.TRUE);
							UserSharedPreferences.getInstance(this).putString(
									AppConstant.USER_ID, response.userId);
						}
						CommonUtils.showSuccessToast(this, response.message);
						openHomeScreen();
					} else if (ValidationUtils
							.isParameterMissingResponse(response)) {
						etVerifyCode.setText("");
						alertDialog.createDefaultDialog(getResources()
								.getString(R.string.dialog_box_error_title),
								response.message);
					} else if (ValidationUtils
							.isParameterEmptyResponse(response)) {
						etVerifyCode.setText("");
						alertDialog.createDefaultDialog(getResources()
								.getString(R.string.dialog_box_error_title),
								response.message);
					} else if (ValidationUtils
							.isInternalErrorResponse(response)) {
						etVerifyCode.setText("");
						alertDialog.createDefaultDialog(getResources()
								.getString(R.string.dialog_box_error_title),
								response.message);
					} else if (ValidationUtils.isUnknownResponse(response)) {
						etVerifyCode.setText("");
						alertDialog.createDefaultDialog(getResources()
								.getString(R.string.dialog_box_error_title),
								response.message);
					} else {
						etVerifyCode.setText("");
						//CommonUtils.showErrorToast(this, response.errorMessage);
					}
				}
			}
		} else {
			if (CommonUtils.isNetworkAvailable(this)) {
			CommonUtils.showErrorToast(this,
					getResources().getString(R.string.time_out_connetion));
			}else{
				CommonUtils.showErrorToast(this,
						getResources().getString(R.string.no_internate_connetion));
			}
}
		CustomProgressbar.hideProgressBar();
	}

	/**
	 * THIS METHOD IS TO CALL WEBSERVICE TO GET THE VERIFICATION ON AFTER
	 * REGISTER THE PERSONNAL INFO
	 * @param name
	 * @param countryCode
	 * @param phoneNumber
	 */
	@Background
	public void register(String name, String countryCode, String phoneNumber) {

		 doGCMRegistration();

		RegisterResponse response = null;
		response = WebServiceManager.getInstance(this).register(name,
				countryCode, phoneNumber);
		afterRegister(response);
	}

	/**
	 * THIS METHOD IS CALL TO SHOW THE USER GETS THE VERIFICATION CODE ON MOBILE
	 * @param response
	 */
	@UiThread
	public void afterRegister(RegisterResponse response) {
		if (response != null) {
			if (StringUtils.isNotBlank(response.message)) {

				if (ValidationUtils.isSuccessResponse(response)) {
					alertDialog.createDefaultDialog(
							getResources().getString(
									R.string.dialog_box_getcode_title),
							response.message);
				} else if (ValidationUtils.isParameterMissingResponse(response)) {
					alertDialog.createDefaultDialog(
							getResources().getString(
									R.string.dialog_box_error_title),
							response.message);
				} else if (ValidationUtils.isParameterEmptyResponse(response)) {
					alertDialog.createDefaultDialog(
							getResources().getString(
									R.string.dialog_box_error_title),
							response.message);
				} else if (ValidationUtils.isInternalErrorResponse(response)) {
					alertDialog.createDefaultDialog(
							getResources().getString(
									R.string.dialog_box_error_title),
							response.message);
				} else if (ValidationUtils.isUnknownResponse(response)) {
					alertDialog.createDefaultDialog(
							getResources().getString(
									R.string.dialog_box_error_title),
							response.message);
				} 
			}
		} else {
			if (CommonUtils.isNetworkAvailable(this)) {
			CommonUtils.showErrorToast(this,
					getResources().getString(R.string.time_out_connetion));
			}else{
				CommonUtils.showErrorToast(this,
						getResources().getString(R.string.no_internate_connetion));
			}
}

		CustomProgressbar.hideProgressBar();

	}

	/**
	 * THIS METHOD IS CALL ON FIRST LOGIN SHOW THE TUTORIAL SCREEN
	 */
	@UiThread
	public void openHomeScreen() {
		Intent intent = new Intent(this, OverlayActivity_.class);
		startActivity(intent);
		finish();
	}

	/**
	 * THIS METHOD IS CALL ON USER SELECTS THE COUNTRY CODE LIST
	 * @param position
	 */
	@ItemClick
	public void mCountryCodeListView(int position) {
		CommonUtils.hideSoftKeyboard(this);
		try {
			if (etSearchBox.getText().length() > 0) {
				etSearchBox.setText("");
				if (searchResults.size() != CountryCodes.length) {
					if (position < searchResults.size()) {
						HashMap<String, String> map = searchResults
								.get(position);

						etCountryCode.setText(getString(R.string.country_code)
								+ map.get(AppConstant.C_CODE));
						if (searchResults != null) {
							searchResults.clear();
						}

					}
				}
			} else {

				String[] countryList = CountryCodes[position].split(",");
				etCountryCode.setText(getString(R.string.country_code)
						+ countryList[0].trim().toString());

			}
			llUserLogin.setVisibility(View.VISIBLE);
			rlCountryList.setVisibility(View.GONE);
			showBaseLayout(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * THIS METHOD IS CALL WHEN THE USER PRESS ON BACK ARROW IN COUNTRY LIST
	 */
	@Click
	public void baseHeaderBackArrow() {
		CommonUtils.hideSoftKeyboard(this);
		llUserLogin.setVisibility(View.VISIBLE);
		showBaseLayout(false);
		rlCountryList.setVisibility(View.GONE);
		etSearchBox.setText("");
	}

	/**
	 * THIS METHOD IS TO CALL WHEN THE USER PRESS ON REGISTRATION PAGE TO HIDE
	 * KEYBOARD
	 */
	@Click
	public void llRegistration() {
		CommonUtils.hideSoftKeyboard(this);
	}

	/**
	 * THIS METHOD IS CALL WHEN THE USER ENTER THE TEXT ON EDITTEXT
	 */
	@SuppressLint("DefaultLocale")
	@Override
	public void afterTextChanged(Editable s) {
		if (rlCountryList.getVisibility() == View.VISIBLE) {
			if (etSearchBox.getText().length() != 0) {
				mCountryCodeListView.setFastScrollEnabled(false);

				String searchString = etSearchBox.getText().toString()
						.toLowerCase();

				int textLength = searchString.length();
				originalValues.get(0);
				searchResults = new ArrayList<HashMap<String, String>>();

				for (int i = 0; i < originalValues.size(); i++) {
					HashMap<String, String> map = originalValues.get(i);
					String conName = map.get(AppConstant.C_NAME).toString().toLowerCase();
					if (textLength <= conName.length()) {
						if (conName.contains(searchString))searchResults.add(originalValues.get(i));
					}

				}
				stockArr = new String[searchResults.size()];
				for (int k = 0; k < searchResults.size(); k++) {
					HashMap<String, String> map1 = searchResults.get(k);
					stockArr[k] = (map1.get(AppConstant.C_CODE) + "," + map1
							.get(AppConstant.C_SUFFIX));
				}
			
				if(stockArr.length == 0){
				noMatchFoundView.setVisibility(View.VISIBLE);
				}else{
				noMatchFoundView.setVisibility(View.GONE);
				}
					
				adapter = new CountriesListAdapter(this, stockArr,countryKey);
				mCountryCodeListView.setAdapter(adapter);
				
			} else {
				mCountryCodeListView.setFastScrollEnabled(true);

				CommonUtils.hideSoftKeyboard(this);
				
				adapter = new CountriesListAdapter(this, CountryCodes,	countryKey);
				mCountryCodeListView.setAdapter(adapter);
			}
		}

		if (etPhoneNumber.getText().length() > 6) {
			btnGetCode.setEnabled(true);
		} else {
			btnGetCode.setEnabled(false);
		}

		if (etVerifyCode.getText().length() > 3) {
			btnLogin.setEnabled(true);

		} else {
			//etVerifyCode.setCursorVisible(true);
			btnLogin.setEnabled(false);
		}

		if (etVerifyCode.hasFocus() && etVerifyCode.getText().length() == 4) {

			CommonUtils.hideSoftKeyboard(this);
		}

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (etSearchBox.hasFocus()) {
		if (StringUtils.isNotBlank(s.toString())) {
			cleareSearch.setVisibility(View.VISIBLE);
		} else {
			cleareSearch.setVisibility(View.GONE);
		}}
	}

	/**
	 * THIS METHOD IS CALL TO GET USER GCM REGISTRATION
	 */
	
	  private void doGCMRegistration() { 

			//checkNotNull("SENDER_ID", CommonUtils.SENDER_ID);
			// Make sure the device has the proper dependencies.
			GCMRegistrar.checkDevice(this);
			Log.d(TAG, "device check done");
			// Make sure the manifest was properly set - comment out this line
			// while developing the app, then uncomment it when it's ready.
			GCMRegistrar.checkManifest(this);
			Log.d(TAG, "menifeast check done");

			// Get GCM registration id
			final String regId = GCMRegistrar.getRegistrationId(this);
			if(StringUtils.isNotBlank(regId)){
			UserSharedPreferences.getInstance(this).putString(AppConstant.LOGIN.ACCESS_TOKEN, regId);}
			// Check if regid already presents
			if (regId.equals("")) {
				// Registration is not present, register now with GCM
				GCMRegistrar.register(this, CommonUtils.SENDER_ID);
				final String regIdString = GCMRegistrar.getRegistrationId(this);
				
				Log.i("GCM", "Registered with GCM: Reg id :" + regIdString);
				if(StringUtils.isNotBlank(regId)){
				UserSharedPreferences.getInstance(this).putString(AppConstant.LOGIN.ACCESS_TOKEN, regIdString);}
			} 
	  } 
	 
	
	/**
     * click on clear button of search box
     */

    @Click
    public void cleareSearch() {
    	if(noMatchFoundView.getVisibility() == View.VISIBLE){
    	noMatchFoundView.setVisibility(View.GONE);}
        etSearchBox.setText("");
    }
	/**
	 * THIS METHOD IS TO CALL WHEN THE USER PRESS ON SEARCH BUTTON TO SEARCH THE
	 * COUNTRY LIST
	 */
	@Click
	public void btnBaseHeaderButtonSearch() {
		cleareSearch.setVisibility(View.GONE);
		if (isSearchBoxEnable) {
			etSearchBox.setText("");
			isSearchBoxEnable = false;
			CommonUtils.hideSoftKeyboard(this);
			showHomeScreenLogo(true);
			showSerchView(false);
			noMatchFoundView.setVisibility(View.GONE);
		} else {
			isSearchBoxEnable = true;
			etSearchBox.requestFocus();
			CommonUtils.showSoftKeyboard(this, etSearchBox);
			showHomeScreenLogo(false);
			showSerchView(true);
		}

	}
	
	/**
	 * THIS METHOD IS TO CALL WHEN THE USER PRESS ON BACK BUTTON
	 */
	@Override
	public void onBackPressed() {

		if (rlCountryList.getVisibility() == View.VISIBLE) {
			etSearchBox.setText("");
			rlCountryList.setVisibility(View.GONE);
			showBaseLayout(false);
			CommonUtils.hideSoftKeyboard(this);
			llUserLogin.setVisibility(View.VISIBLE);
		} else {
			super.onBackPressed();
		}
	}
	@Override
	public void onClick(View v) {

		showSerchView(false);
		showBackArrow(true);
		showHomeScreenLogo(true);
		showSerchView(false);
		showSerchButton(true);
		showBaseLayout(true);
		showRefereshButton(false);

		if (StringUtils.isNotBlank(etCountryCode.getText().toString())) {
			
			getCountryCode(etCountryCode.getText().toString());
			adapter = new CountriesListAdapter(this, CountryCodes,	countryKey);
			
		}
		mCountryCodeListView.setAdapter(adapter);
		llUserLogin.setVisibility(View.GONE);
		rlCountryList.setVisibility(View.VISIBLE);
		CommonUtils.hideSoftKeyboard(this);
	}
	
}