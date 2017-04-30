package edu.umich.wld;

import java.awt.Component;

public class LayoutItem {

	private Component component;
	private Double percentOfRow;
	
	public LayoutItem(Component component, Double percentOfRow) {
		this.component = component;
		this.percentOfRow = percentOfRow;
	}
	
	public Component getComponent() {
		return component;
	}
	
	public void setComponent(Component component) {
		this.component = component;
	}
	
	public Double getPercentOfRow() {
		return percentOfRow;
	}
	
	public void setPercentOfRow(Double percentOfRow) {
		this.percentOfRow = percentOfRow;
	}
}
