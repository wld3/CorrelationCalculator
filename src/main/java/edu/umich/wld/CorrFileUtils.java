package edu.umich.wld;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class CorrFileUtils {
	public static int LOAD = 1;
	public static int SAVE = 2;
	public static int DIR = 3;
	
	private static File previousFile = null;
	
	public static File getFile(String title, int load_save, String startFile) {
		return getFile(title, load_save, null, null, startFile);
	}
	
	public static File getFile(String title, int load_save, String extension, String description, String startFile) {
		JFileChooser chooser = new JFileChooser();
		if (startFile != null) {
			chooser.setSelectedFile(new File(startFile));
		}
		int returnVal = JFileChooser.CANCEL_OPTION;
		File selectedFile = null;
		chooser.setDialogTitle(title);
		chooser.setCurrentDirectory(previousFile);
		if (extension != null) {
			chooser.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter(description, extension);
			chooser.setFileFilter(filter);
		}
		if (load_save == LOAD) {
			do {
				returnVal = chooser.showOpenDialog(null);
				selectedFile = chooser.getSelectedFile();
			}
			while (returnVal == JFileChooser.APPROVE_OPTION && !selectedFile.exists());
		}
		else if (load_save == SAVE) {
			do {
				returnVal = chooser.showSaveDialog(null);
				selectedFile = chooser.getSelectedFile();
				if (selectedFile == null) {
					return null;
				}	
				if (extension != null && !selectedFile.getName().endsWith("."+extension)) {
					selectedFile = new File(selectedFile.getPath()+"."+extension);
				}
				if (returnVal == JFileChooser.APPROVE_OPTION && selectedFile.exists()) {
					int answer = JOptionPane.showConfirmDialog(chooser, 
							"The file '"+selectedFile.getName()+"' already exists. Are you sure you want to overwrite it?",
							"File Exists", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					if (answer == JOptionPane.YES_OPTION) {
						break;
					}
				}
			}
			while (returnVal == JFileChooser.APPROVE_OPTION && selectedFile.exists());
		} else if (load_save == DIR) {
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			returnVal = chooser.showSaveDialog(null);
			selectedFile = chooser.getSelectedFile();
			if (selectedFile == null) {
				return null;
			}	
		}
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			previousFile = selectedFile;
			return selectedFile;
		} 
		return null;
	}
}
