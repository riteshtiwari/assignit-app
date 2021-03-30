package com.assignit.android.ui.adapter;

import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;

import com.assignit.android.R;
import com.assignit.android.ui.views.CustomTextView;
import com.assignit.android.ui.views.alphabeticalIndex.Listview.StringMatcher;
/**
 * Adapter class for Country List item in Registration task screen.
 * 
 * @author Innoppl
 * 
 */
public class CountriesListAdapter extends ArrayAdapter<String> implements SectionIndexer{
	Context context;
	String[] values;
	String etcountryCode;
	
	private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	public CountriesListAdapter(Context context, String[] values,
			String etcountryCode) {
		super(context, R.layout.row_countrylist, values);
		this.context = context;
		this.values = values;
		this.etcountryCode = etcountryCode;
			
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater
				.inflate(R.layout.row_countrylist, parent, false);

		CustomTextView countryName = (CustomTextView) rowView
				.findViewById(R.id.tv_countrylist_country_name);
		CustomTextView countryCode = (CustomTextView) rowView
				.findViewById(R.id.tv_countrylist_country_code);

		String[] countryList = values[position].split(",");
		
		countryName.setText(GetCountryZipCode(countryList[1]).trim());
		String counCode = "+" + countryList[0].trim();
		String counkey = countryList[1].trim();
		countryCode.setText(counCode);
		ImageView markImageView = (ImageView) rowView
				.findViewById(R.id.img_mark_image);
		
		if (etcountryCode.equals(counkey)) {
			
			markImageView.setImageResource(R.drawable.mark_icon);
		}
		return rowView;
	}

	/**
	 * Getting country code by id
	 * 
	 * @param ssid
	 * @return
	 */
	private String GetCountryZipCode(String ssid) {
		Locale loc = new Locale("", ssid);

		return loc.getDisplayCountry().trim();
	}
	
	/* 
	 * @see android.widget.SectionIndexer#getPositionForSection(int)
	 */
	@Override
	public int getPositionForSection(int section) {
		
		// If there is no item for current section, previous section will be selected
		for (int i = section; i >= 0; i--) {
			for (int j = 0; j < getCount(); j++) {
				String[] countryList = values[j].split(",");
				if (i == 0) {
					// For numeric section
					for (int k = 0; k <= 9; k++) {
						if (StringMatcher.match(String.valueOf(GetCountryZipCode(countryList[1]).trim().charAt(0)), String.valueOf(k)))
							return j;
					}
				} else {
					if (StringMatcher.match(String.valueOf(GetCountryZipCode(countryList[1]).trim().charAt(0)), String.valueOf(mSections.charAt(i))))
						return j;
				}
			}
		}
		return 0;
	}

	/* 
	 * @see android.widget.SectionIndexer#getSectionForPosition(int)
	 */
	@Override
	public int getSectionForPosition(int position) {
		return position;
	}

	/* 
	 * @see android.widget.SectionIndexer#getSections()
	 */
	@Override
	public Object[] getSections() {
		String[] sections = new String[mSections.length()];
		for (int i = 0; i < mSections.length(); i++)
			sections[i] = String.valueOf(mSections.charAt(i));
		return sections;
	}
	}