package edu.umich.wld;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.ncibi.commons.lang.NumUtils;

public class PearsonVerifier extends InputVerifier {
		private String lastGood;
		private Double min;
		private Double max;
		
		public PearsonVerifier(JTextField textField, Double min, Double max) {
			this.lastGood = textField.getText();
			if (min <= max) {
				this.min = min;
				this.max = max;
			} else {
				this.min = max;
				this.max = min;
			}
		}
		
		public boolean shouldYieldFocus(JComponent input) {
			JTextField textField = (JTextField) input;
			if (verify(input)) {
				lastGood = textField.getText();
				return true;
			} else {
				JOptionPane.showMessageDialog(null, 
						"Please enter a numerical value between " + PearsonSlider.getDefaultFormat().format(min) + 
						" and " + PearsonSlider.getDefaultFormat().format(max), "Invalid Input", JOptionPane.ERROR_MESSAGE);
				try {	
					Double value = Double.parseDouble(lastGood);
					textField.setText(PearsonSlider.getDefaultFormat().format(value));
				} catch (Throwable ignore) {
				}
				textField.selectAll();
				return false;
			}
		}

		public boolean verify(JComponent input) {
			JTextField textField = (JTextField) input;
			Double value = NumUtils.toDouble(textField.getText());
			return (value != null && value >= min && value <= max);
		}

		public String getLastGood() {
			return lastGood;
		}
		
		public void setLastGood(String lastGood) {
			this.lastGood = lastGood;
		}
		
		public Double getMin() {
			return min;
		}
		
		public void setMin(Double value) {
			this.min = value;
		}
		
		public Double getMax() {
			return max;
		}
		
		public void setMax(Double value) {
			this.max = value;
		}
	}
	