package edu.umich.wld;

import java.text.NumberFormat;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.ncibi.commons.lang.NumUtils;

public class NumericTextFieldVerifier<T> extends InputVerifier {
	private String lastGood;
	private T min;
	private T max;
	private NumberFormat format;
	
	public NumericTextFieldVerifier(JTextField textField, T min, T max, NumberFormat format) {
		this.lastGood = textField.getText();
		this.min = min;
		this.max = max;
		this.format = format;
	}
	
	public boolean shouldYieldFocus(JComponent input) {
		JTextField textField = (JTextField) input;
		if (verify(input)) {
			lastGood = textField.getText();
			return true;
		} else {
			JOptionPane.showMessageDialog(null, "Please enter a value between " + format.format(min) + 
					" and " + format.format(max), "Invalid Input", JOptionPane.ERROR_MESSAGE);
			try {	
				Double value = Double.parseDouble(lastGood);
				textField.setText(format.format(value));
			} catch (Throwable ignore) {
			}
			textField.selectAll();
			return false;
		}
	}

	public boolean verify(JComponent input) {
		JTextField textField = (JTextField) input;
		Double value = NumUtils.toDouble(textField.getText());
		return (value != null && value >= (Double) min && value <= (Double) max);
	}
	
	public String getLastGood() {
		return lastGood;
	}
	
	public void setLastGood(String lastGood) {
		this.lastGood = lastGood;
	}
	
	public T getMin() {
		return min;
	}
	
	public void setMin(T value) {
		this.min = value;
	}
	
	public T getMax() {
		return max;
	}
	
	public void setMax(T value) {
		this.max = value;
	}
	
	public NumberFormat getFormat() {
		return format;
	}
	
	public void setFormat(NumberFormat format) {
		this.format = format;
	}
}
	