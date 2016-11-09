/*************************************************************************
 * Copyright 2012 Regents of the University of Michigan 
 * 
 * NCIBI - The National Center for Integrative Biomedical Informatics (NCIBI)
 *         http://www.ncib.org.
 * 
 * This product may includes software developed by others; in that case see specific notes in the code. 
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or (at your option) any later version, along with the following terms:
 * 1.	You may convey a work based on this program in accordance with section 5, 
 *      provided that you retain the above notices.
 * 2.	You may convey verbatim copies of this program code as you receive it, 
 *      in any medium, provided that you retain the above notices.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU General Public License for more details, http://www.gnu.org/licenses/.
 * 
 * This work was supported in part by National Institutes of Health Grant #U54DA021519
 *
 ******************************************************************/
package edu.umich.wld;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.NumberFormat;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PearsonTextFieldPair {
	
	public static final Integer LEFT_FIELD = 0;
	public static final Integer RIGHT_FIELD = 1;
	public static final Integer FIELD_COUNT = 2;
	
	private JTextField[] boxPair = new JTextField[FIELD_COUNT];
	private static String savedBoxContents = null;
	private Double[] defaults = new Double[FIELD_COUNT];
	
	public PearsonTextFieldPair() {
		this.defaults[0] = 0.0;
		this.defaults[1] = 1.0;
		initialize();
	}
	
	private void initialize() {
		NumberFormat format2 = NumberFormat.getNumberInstance();
		format2.setMinimumFractionDigits(2);
		format2.setMaximumFractionDigits(2);
		
		for (int i = 0; i < FIELD_COUNT; i++) {
			boxPair[i] = new JTextField(format2.format(defaults[i]));
			boxPair[i].setEnabled(false);
			boxPair[i].setHorizontalAlignment(JTextField.LEFT);
			boxPair[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
			boxPair[i].setInputVerifier(new PearsonVerifier(boxPair[i], defaults[0], defaults[1]));
			final int iCopy = i;
			boxPair[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					String text = boxPair[iCopy].getText();
					Double value = null;
					try {
						value = Double.parseDouble(text);
						((PearsonVerifier) boxPair[iCopy].getInputVerifier()).setLastGood(text);
						if (iCopy == LEFT_FIELD) {
							((PearsonVerifier) boxPair[1 - iCopy].getInputVerifier()).setMin(value);
						} else {
							((PearsonVerifier) boxPair[1 - iCopy].getInputVerifier()).setMax(value);
						}
					} catch (Throwable ignore) {
					}
					AnalysisDialog.resetCountText();
					fillBoxesAndSetSlider(iCopy);
				}
			});
			boxPair[i].getDocument().addDocumentListener(new DocumentListener() {
				public void changedUpdate(DocumentEvent de) {
					doUpdate();
				}
				public void removeUpdate(DocumentEvent de) {
					doUpdate();
				}
				public void insertUpdate(DocumentEvent de) {
					doUpdate();
				}
				private void doUpdate() {
					String text = boxPair[iCopy].getText();
					Double value = null;
					try {
						value = Double.parseDouble(text);
						((PearsonVerifier) boxPair[iCopy].getInputVerifier()).setLastGood(text);
						if (iCopy == LEFT_FIELD) {
							((PearsonVerifier) boxPair[1 - iCopy].getInputVerifier()).setMin(value);
						} else {
							((PearsonVerifier) boxPair[1 - iCopy].getInputVerifier()).setMax(value);
						}
					} catch (Throwable ignore) {
					}
					AnalysisDialog.resetCountText();
				}
			});
			boxPair[i].addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent fe) {
					savedBoxContents = AnalysisDialog.getLimits().getBoxPair()[iCopy].getText();
				}

				public void focusLost(FocusEvent fe) {
					if (AnalysisDialog.getLimits().getBoxPair()[iCopy].getText().equals(savedBoxContents)) {
						savedBoxContents = null;
						return;
					}
					fillBoxesAndSetSlider(iCopy);	
				}
			});
		}
	}
	
	private void fillBoxesAndSetSlider(final Integer i) {
		try {
			String text = AnalysisDialog.getLimits().getBoxPair()[i].getText();
			Double val = Double.parseDouble(text);
			
			if (LEFT_FIELD.equals(i)) {
				AnalysisDialog.getSlider().getRangeModel().setMinValue(val);
			} else {
				AnalysisDialog.getSlider().getRangeModel().setMaxValue(val);
			}
		} catch (Throwable ignore) {
		}
	}
	
	public JTextField[] getBoxPair() {
		return boxPair;
	}
	
	public void setBoxPair(JTextField[] boxPair) {
		this.boxPair = boxPair;
	}
	
	public JTextField getLeftField() {
		return boxPair[LEFT_FIELD];
	}
	
	public void setLeftField(JTextField leftField) {
		this.boxPair[LEFT_FIELD] = leftField;
	}
	
	public JTextField getRightField() {
		return boxPair[RIGHT_FIELD];
	}
	
	public void setRightField(JTextField rightField) {
		this.boxPair[RIGHT_FIELD] = rightField;
	}
	
	public JTextField getMinField(Integer index) {
		return boxPair[LEFT_FIELD];
	}
	
	public JTextField getMaxField(Integer index) {
		return boxPair[RIGHT_FIELD];
	}
	
	public Double getDefaultMin() {
		return defaults[0];
	}
	
	public void setDefaultMin(Double defaultMin) {
		this.defaults[0] = defaultMin;
	}
	
	public Double getDefaultMax() {
		return defaults[1];
	}
	
	public void setDefaultMax(Double defaultMax) {
		this.defaults[1] = defaultMax;
	}
}
