package edu.umich.wld;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class CorrelationCalculator {
	
	private static AppData appData = null;
	private static Logger logger = LogManager.getLogger(CorrelationCalculator.class.getName());
	
	public static void main(String[] args) {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		logger.info("Starting Correlation Calculator");
		
		new AnalysisDialog("Correlation Calculator");
		
		System.exit(0);
	}
	
	public static AppData getAppData() { 
		if (appData == null) {
			appData = new AppData();
		}
		return appData; 
	}
	
	public static Logger getLogger() {
		return logger;
	}
}
