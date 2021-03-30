package com.assignit.android.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.assignit.android.R;

/**
 * Custom ViewConstants.
 * 
 * @author Innoppl
 * 
 */
public class CustomViewConstants {

	public interface FONTS {
		String FONT_ROBOTO_CONDENSED = "Roboto-Condensed";
		String FONT_ROBOTO_LIGHT = "Roboto-Light";
		String FONT_ROBOTO_MEDIUM = "Roboto-Medium";
		String FONT_ROBOTO_REGULAR = "Roboto-Regular";

	}

	public static Typeface fontRobotoCondensed;
	public static Typeface fontRobotoLight;
	public static Typeface fontRobotoMedium;
	public static Typeface fontRobotoRegular;

	/**
	 * Constructor for the custom font
	 * 
	 * @param mContext
	 */
	public static void loadFonts(Context mContext) {

		fontRobotoCondensed = Typeface.createFromAsset(mContext.getAssets(), "fonts/" + FONTS.FONT_ROBOTO_CONDENSED	+ ".ttf");
		fontRobotoLight = Typeface.createFromAsset(mContext.getAssets(), "fonts/" + FONTS.FONT_ROBOTO_LIGHT + ".ttf");
		fontRobotoMedium = Typeface.createFromAsset(mContext.getAssets(), "fonts/" + FONTS.FONT_ROBOTO_MEDIUM + ".ttf");
		fontRobotoRegular = Typeface.createFromAsset(mContext.getAssets(), "fonts/" + FONTS.FONT_ROBOTO_REGULAR	+ ".ttf");

	}

	/**
	 * Method to get custom font type by attrs
	 * 
	 * @param context
	 * @param attrs
	 * @return
	 */
	public static Typeface getFontType(Context context, AttributeSet attrs) {

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomText);
		CharSequence fontType = a.getString(R.styleable.CustomText_font_type);

		if (fontType != null) {
			if (fontType.equals(FONTS.FONT_ROBOTO_CONDENSED)) {
				return (CustomViewConstants.fontRobotoCondensed);
			} else if (fontType.equals(FONTS.FONT_ROBOTO_LIGHT)) {
				return (CustomViewConstants.fontRobotoLight);
			} else if (fontType.equals(FONTS.FONT_ROBOTO_MEDIUM)) {
				return (CustomViewConstants.fontRobotoMedium);
			} else if (fontType.equals(FONTS.FONT_ROBOTO_REGULAR)) {
				return (CustomViewConstants.fontRobotoRegular);
			}

		} else {
			return (CustomViewConstants.fontRobotoLight);
		}

		return CustomViewConstants.fontRobotoLight;

	}

	/**
	 * Method to get custom font type 
	 * 
	 * @param context
	 * @param FontType
	 * @return
	 */
	public static Typeface getFontType(Context context, String FontType) {

		CharSequence fontType = FontType;

		if (fontType != null) {
			if (fontType.equals(FONTS.FONT_ROBOTO_CONDENSED)) {
				return (CustomViewConstants.fontRobotoCondensed);
			} else if (fontType.equals(FONTS.FONT_ROBOTO_LIGHT)) {
				return (CustomViewConstants.fontRobotoLight);
			} else if (fontType.equals(FONTS.FONT_ROBOTO_MEDIUM)) {
				return (CustomViewConstants.fontRobotoMedium);
			} else if (fontType.equals(FONTS.FONT_ROBOTO_REGULAR)) {
				return (CustomViewConstants.fontRobotoRegular);
			}

		} else {
			return (CustomViewConstants.fontRobotoLight);
		}

		return CustomViewConstants.fontRobotoLight;

	}
}
