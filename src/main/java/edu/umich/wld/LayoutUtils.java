package edu.umich.wld;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LayoutUtils {
	public static void doGridLayout(JPanel panel, LayoutGrid grid) {
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		
		int iRow = 0;
		for (LayoutRow row : grid.getRows()) {
			int iCol = 0;
			for (LayoutItem item : row.getItems()) {
				constraints.gridx = iCol;
				constraints.gridy = iRow;
				constraints.weightx = item.getPercentOfRow();
				layout.setConstraints(item.getComponent(), constraints);
				panel.add(item.getComponent());
				iCol++;
			}
			iRow++;
		}
	}
	
	public static void addBlankLines(JPanel panel, Integer nLines) {
		JPanel blankPanel = new JPanel();
		blankPanel.setLayout(new BoxLayout(blankPanel, BoxLayout.Y_AXIS));
		for (int i = 0; i < nLines; i++) {
			JLabel blankLabel = new JLabel("  ");
			blankPanel.add(blankLabel);
			blankPanel.add(Box.createVerticalStrut(10));
		}
		panel.add(blankPanel);
	}
}
