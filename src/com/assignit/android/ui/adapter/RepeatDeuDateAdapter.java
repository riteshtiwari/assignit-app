package com.assignit.android.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.assignit.android.R;
/**
 * For Me task Adapter
 * 
 * @author Innoppl
 * 
 */
public class RepeatDeuDateAdapter extends ArrayAdapter<String> {

	Context mContext;

	String[] items;

	int selectedposition;

	public RepeatDeuDateAdapter(Context context, String[] items) {
		super(context, R.layout.row_repeat_items, items);
		this.mContext = context;
		this.items = items;
	}
	
	 	ViewHolder holder;
	    Drawable icon;

	    class ViewHolder {
	        ImageView icon;
	        TextView title;
	    }

	    public void selectItem(int position){
	    	
	    	selectedposition = position;
			
	    }
	    
	    
	    /*
	     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	     */
	    public View getView(int position, View convertView,
	            ViewGroup parent) {
	        final LayoutInflater inflater = (LayoutInflater) mContext.getApplicationContext()
	                .getSystemService(
	                        Context.LAYOUT_INFLATER_SERVICE);

	        if (convertView == null) {
	            convertView = inflater.inflate(
	                    R.layout.row_repeat_items, null);

	            holder = new ViewHolder();
	          holder.icon = (ImageView) convertView.findViewById(R.id.icon);
	            holder.title = (TextView) convertView.findViewById(R.id.title);
	            convertView.setTag(holder);
	        } else {
	          
	            holder = (ViewHolder) convertView.getTag();
	        }       

	        holder.title.setText(items[position]);

	        if(selectedposition == position){
	        	 holder.icon.setVisibility(View.VISIBLE);
	        }else{
	        	 holder.icon.setVisibility(View.GONE);
	        }
	        
	        return convertView;
	    }
}
