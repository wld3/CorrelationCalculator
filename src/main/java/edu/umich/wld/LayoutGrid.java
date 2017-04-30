package edu.umich.wld;

import java.util.ArrayList;
import java.util.List;

public class LayoutGrid {

	private List<LayoutRow> rows;
	
	public LayoutGrid() {
		setRows(new ArrayList<LayoutRow>());
	}

	public List<LayoutRow> getRows() {
		return rows;
	}

	public void setRows(List<LayoutRow> rows) {
		this.rows = rows;
	}

	public void addRow(List<LayoutItem> items) {	
		getRows().add(new LayoutRow(items));
	}
}
