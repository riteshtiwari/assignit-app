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
import com.assignit.android.utils.StringUtils;
/**
 * For Me task Adapter
 * 
 * @author Innoppl
 * 
 */
public class EndRepeatDeuDateAdapter extends ArrayAdapter<String>  {

	Context mContext;
	
	String[] items;

	int selectedposition = -1;
	
	String dateFormat = "";

	public EndRepeatDeuDateAdapter(Context context, String[] items) {
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
	    
	    public void setNameDate(String date){
	    	
	    	dateFormat = date;
			
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
	        
	        	        
	       if(position == 0){
	        holder.title.setText(items[0]);
	       }else if(position == 1){
	    	   if(StringUtils.isBlank(dateFormat)){
	    		   holder.title.setText(items[1]);
	    	   }else if(dateFormat.equals(items[0])){
	    		   holder.title.setText(items[1]);
	    	   }else{
	    	   holder.title.setText(dateFormat);}
	       }
	       
	       
	        
	       if(selectedposition == position){
	        	 holder.icon.setVisibility(View.VISIBLE);
	        }else{
	        	 holder.icon.setVisibility(View.GONE);
	        }
	        
	        return convertView;
	    }
	    
	  
}
