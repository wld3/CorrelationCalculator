package edu.umich.wld;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppData {

	private List<FileData> fileDataStore = new ArrayList<FileData>(Arrays.asList(new FileData()));
	private FileData fileData = new FileData();
	
	public List<FileData> getFileDataStore() {
		return fileDataStore;
	}
	
	public void setFileDataStore(List<FileData> fileDataStore) {
		this.fileDataStore = fileDataStore;
	}
	
	public FileData getFileData() {
		return fileData;
	}
	
	public void setFileData(FileData fileData) {
		this.fileData = fileData;
	}
}
