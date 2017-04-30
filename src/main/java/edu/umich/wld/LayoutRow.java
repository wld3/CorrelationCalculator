package edu.umich.wld;

import java.util.ArrayList;
import java.util.List;

public class LayoutRow {

	private List<LayoutItem> items;
	
	public LayoutRow(List<LayoutItem> items) {
		setItems(new ArrayList<LayoutItem>(items));
	}

	public List<LayoutItem> getItems() {
		return items;
	}

	public void setItems(List<LayoutItem> items) {
		this.items = items;
	}
}
