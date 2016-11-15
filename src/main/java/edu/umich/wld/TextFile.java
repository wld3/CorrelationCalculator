package edu.umich.wld;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVWriter;

public class TextFile {

	private Vector<Vector<String>> data;
	
	public TextFile() {
		data = new Vector<Vector<String>>();
	}
	
	public TextFile(File file) throws IOException {
		open(file);
	}

	public void open(File file) throws IOException {
		BufferedReader input = new BufferedReader(new FileReader(file));
		String line = input.readLine();
		CSVParser commaParser = new CSVParser();
		CSVParser tabParser = new CSVParser('\t');
		CSVParser parser = (tabParser.parseLine(line).length > 1) ? tabParser: commaParser;
		data = new Vector<Vector<String>>();
		while (line != null) {
			String[] rowArray = parser.parseLine(line);
			Vector<String> row = new Vector<String>(Arrays.asList(rowArray));
			data.add(row);
			line = input.readLine();
		}
	    input.close();
	}
	
	public void save(File file) throws IOException {
		save(file,',');
	}
	
	public void save(File file, char separator) throws IOException {
		save(file,',','"');
	}
	
	public void save(File file, char separator, char quotechar) throws IOException {
		CSVWriter output = new CSVWriter(new FileWriter(file), separator, quotechar);
		for (Vector<String> row : data) {
			if (row != null) {
				output.writeNext((String[]) row.toArray(new String[0]));
			} else {
				output.writeNext(new String[0]);
			}
		}
		output.close();
	}
	
	public void setValue(Double value, int row, int col) {
		if (data.size() < (row+1)) {
			data.setSize(row+1);
		}
			
		if (data.get(row) == null) {
			data.set(row,new Vector<String>());
		}
		
		if (data.get(row).size() < (col+1)) {
			data.get(row).setSize(col+1);
		}
		
		if (value != null) {
			data.get(row).set(col, value.toString());
		} else {
			data.get(row).set(col, null);
		}
			
   	}
	
	public void setValue(Integer value, int row, int col) {
		if (data.size() < (row+1)) {
			data.setSize(row+1);
		}

		if (data.get(row) == null) {
			data.set(row,new Vector<String>());
		}

		if (data.get(row).size() < (col+1)) {
			data.get(row).setSize(col+1);
		}
		
		if (value != null) {
			data.get(row).set(col, value.toString());
		} else {
			data.get(row).set(col, null);
		}
   	}
	
	public void setValue(String value, int row, int col) {
		if (data.size() < (row+1)) {
			data.setSize(row+1);
		}
		
		if (data.get(row) == null) {
			data.set(row, new Vector<String>());
		}
		
		if (data.get(row).size() < (col+1)) {
			data.get(row).setSize(col+1);
		}
		
		data.get(row).set(col, value);
   	}
	
	public String getString(int row, int col)  {
		if (data.size() <= row || data.get(row) == null || data.get(row).size() <= col) {
			return null;
		}
			
		return data.get(row).get(col);
	}

   	public Integer getInteger(int row, int col) {
   		if (data.size() <= row || data.get(row) == null || data.get(row).size() <= col) {
			return null;
   		}
   		
		try {
			return Integer.parseInt(data.get(row).get(col));
		} catch (NumberFormatException nfe) {
			return null;
		}
   	}
   	
   	public Double getDouble(int row, int col) {
   		if (data.size() <= row || data.get(row) == null || data.get(row).size() <= col) {
			return null;
   		} 

		try {
			return Double.parseDouble(data.get(row).get(col));
		} catch (NumberFormatException nfe) {
			return null;
		}	
   	}
   	
	public int getStartRowIndex() {
		if (data.size() == 0) {
			return -1;
		}
		
		return 0;
	}
	
	public int getEndRowIndex() {
		return (data.size() - 1);
	}
	
	public int getStartColIndex(int row) {
		if (data.size() <= row || data.get(row) == null) {
			return -1;
		}
			
		return 0;
	}

	public int getEndColIndex(int row) {
		if (data.size() <= row || data.get(row) == null) {
			return -1;
		}
			
		return data.get(row).size() - 1;
	}  	
}
