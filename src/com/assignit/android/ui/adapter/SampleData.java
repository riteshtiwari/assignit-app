package com.assignit.android.ui.adapter;

/**
 * Container to hold same sample data.
 * 
 * You can find more information in my <a href="http://schimpf.es">blog</a>.
 * 
 * @see <a href="http://schimpf.es">Blog</a>
 * @author Innoppl
 * 
 */
public class SampleData {

	private String name;

	private boolean selected;

	public SampleData(String name, boolean selected) {
		super();
		this.name = name;
		this.selected = selected;
	}

	/**
	 * Method to getname from check box collections
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * MEthod to setname to check box items
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Method to find is check box selected or not
	 * @return
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Method to selection on check box items
	 * @param selected
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
