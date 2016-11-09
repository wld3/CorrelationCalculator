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

public class NumericTextFieldWithListeners<T> extends JTextField {
	
	private static final long serialVersionUID = -538084927445674893L;
	private static String savedBoxContents = null;
	private T initialValue;
	private T lowerLimit;
	private T upperLimit;
	private Integer digits;
	private NumberFormat format = NumberFormat.getNumberInstance();
	
	public NumericTextFieldWithListeners(T initialValue, T lowerLimit, T upperLimit, Integer digits) {
		this.initialValue = initialValue;
		this.lowerLimit = lowerLimit;
		this.upperLimit = upperLimit;
		this.digits = digits;
		initialize();
	}
	
	private void initialize() {
		format.setMinimumFractionDigits(digits);
		format.setMaximumFractionDigits(digits);
		
		setText(format.format(initialValue));
		setHorizontalAlignment(JTextField.LEFT);
		setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
		setInputVerifier(new NumericTextFieldVerifier<T>(this, lowerLimit, upperLimit, format));
		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				((NumericTextFieldVerifier<?>) getInputVerifier()).setLastGood(getText());
			}
		});
		getDocument().addDocumentListener(new DocumentListener() {
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
				((NumericTextFieldVerifier<?>) getInputVerifier()).setLastGood(getText());
			}
		});
		addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent fe) {
				savedBoxContents = getText();
			}

			public void focusLost(FocusEvent fe) {
				if (getText().equals(savedBoxContents)) {
					savedBoxContents = null;
				}
			}
		});
	}

	public static String getSavedBoxContents() {
		return savedBoxContents;
	}

	public static void setSavedBoxContents(String savedBoxContents) {
		NumericTextFieldWithListeners.savedBoxContents = savedBoxContents;
	}

	public T getInitialValue() {
		return initialValue;
	}

	public void setInitialValue(T initialValue) {
		this.initialValue = initialValue;
	}

	public T getLowerLimit() {
		return lowerLimit;
	}

	public void setLowerLimit(T lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	public T getUpperLimit() {
		return upperLimit;
	}

	public void setUpperLimit(T upperLimit) {
		this.upperLimit = upperLimit;
	}

	public Integer getDigits() {
		return digits;
	}

	public void setDigits(Integer digits) {
		this.digits = digits;
	}

	public NumberFormat getFormat() {
		return format;
	}

	public void setFormat(NumberFormat format) {
		this.format = format;
	}
}
