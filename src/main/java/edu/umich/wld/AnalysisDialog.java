package edu.umich.wld;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.ncibi.commons.file.DataFile;
import org.ncibi.commons.file.ExcelFile;
import org.ncibi.commons.file.TextFile;
import org.ncibi.metR.ws.client.MetRService;
import org.ncibi.metR.ws.common.CorrelationArguments;
import org.ncibi.metR.ws.common.CorrelationConstants;
import org.ncibi.ws.HttpRequestType;
import org.ncibi.ws.Response;

import au.com.bytecode.opencsv.CSVParser;

import com.google.common.base.CharMatcher;

import cytoscape.weblaunch.LaunchEngine;

@SuppressWarnings("serial")
public class AnalysisDialog extends JDialog {
	// set the following flag for internal versions; clear it for release versions (wld 8/25/15)
	private static final Boolean isInternalVersion = false;
	
	private static final Color TITLE_COLOR = new Color(0, 0, 205);
	private static final Double SLIDER_EXTENT = 500.0;
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	// if we switch to a Windows-based R server, the next line will no longer be valid (wld 1/26/15)
	private static final String SERVER_LINE_SEPARATOR = "\n";
	private static final String FILE_SEPARATOR = System.getProperty("file.separator");
	private static final String TEMP_DIRECTORY = System.getProperty("java.io.tmpdir") + FILE_SEPARATOR;
	private static final String HOME_DIRECTORY = System.getProperty("user.home", "~" + FILE_SEPARATOR);
	
	private static String inputFileFullPathName;
	private static String tempFileDirectoryName;
	private static FileData currentFileData = null;
	private static Integer filteredCount = null;
	private static Double [] pearsonMaxValues = null;
	private Boolean exportInProgress = false;
	private static Boolean saveInProgress = false;
	private static Boolean saveCoeffFile = false;
	private static Boolean saveHistogramFile = false;
	private static Boolean saveStaticHeatmapFile = false;
	private static Boolean saveInteractiveHeatmapFiles = false;
	private static String [] metabList = null;
	
	private static Map<String, String> dataMap = null;
	private static Map<Integer, List<String>> clusterMap = null;
	
	private JPanel outerPanel;
	private JScrollPane scrollPane;
	private JPanel innerPanel;
	private JTabbedPane tabbedPane;
	
	private JPanel inputWrapPanel;
	private JPanel normWrapPanel;
	private JPanel analysisWrapPanel;
	
	private static JPanel inputFileFormatPanel;
	private TitledBorder inputFileFormatBorder;
	private static JComboBox<String> inputFileFormatComboBox;
	private static JCheckBox labeledCheckBox;

	private static JPanel inputFileWrapPanel;
	private TitledBorder inputFileWrapBorder;
	private static JPanel inputFilePanel;
	private static JComboBox<FileData> inputFileComboBox;
	private static JButton inputFileButton;
	private static JPanel inputFileProgPanel;
	private static JProgressBar inputFileProgBar;
	private static JPanel inputPanel;
	private TitledBorder inputBorder;
	
	private JPanel dataNormPanel;
	private TitledBorder dataNormBorder;
	private JPanel dataOptionsPanel;
	private TitledBorder dataOptionsBorder;
	private static JCheckBox transformCheckBox;
	private static JCheckBox scaleCheckBox;
	private JPanel dataActionWrapPanel;
	private TitledBorder dataActionWrapBorder;
	private static JPanel dataActionPanel;
	private static JButton runNormButton;
	private static JButton viewNormButton;
	private static JButton saveNormButton;
	private JPanel dataActionProgPanel;
	private JProgressBar dataActionProgBar;
	
	private static JPanel internalUsePanel;
	private TitledBorder internalUseBorder;
	
	private static JPanel clusterWrapPanel;
	private TitledBorder clusterWrapBorder;
	private static JPanel clusterPanel;
	private static JButton runClusterAnalysisButton;
	private static JButton viewClusterResultsButton;
	private static JButton saveClusterResultsButton;
	private static JPanel clusterProgPanel;
	private static JProgressBar clusterProgBar;
	
	private static JPanel batchWrapPanel;
	private TitledBorder batchWrapBorder;
	private static JPanel batchPanel;
	private static JButton runBatchAnalysisButton;
	private static JButton viewBatchNetworkButton;
	private static JButton viewBatchResultsButton;
	private static JButton saveBatchResultsButton;
	private static JPanel batchProgPanel;
	private static JProgressBar batchProgBar;
	
	private static JPanel compareParametersWrapPanel;
	private TitledBorder compareParametersWrapBorder;
	private static JPanel compareParametersPanel;
	private static JLabel lambdaLabel;
	private static NumericTextFieldWithListeners<Double> lambdaBox;
	private static JButton tuneButton;
	private static JLabel iterLabel;
	private static NumericTextFieldWithListeners<Integer> iterBox;
	private static JPanel compareParametersProgPanel;
	private static JProgressBar compareParametersProgBar;
	
	private static JPanel compareWrapPanel;
	private TitledBorder compareWrapBorder;
	private static JPanel comparePanel;
	private static JButton compareButton;
	private static JButton viewCompareNetworkButton;
	private static JButton viewCompareMetabsButton;
	private static JButton viewCompareStabSelButton;
	private static JPanel compareProgPanel;
	private static JProgressBar compareProgBar;
	
	private static JPanel hidePanel;
	private static JButton hideButton;
	
	private static JPanel pearsonWrapPanel;
	private TitledBorder pearsonWrapBorder;
	private static JPanel pearsonPanel;
	private static JButton runPearsonButton;
	private static JButton createPearsonHeatmapButton;
	private static JButton viewPearsonDistributionButton;
	private static JButton viewPearsonResultsButton;
	private static JButton savePearsonButton;
	private static JPanel pearsonProgPanel;
	private static JProgressBar pearsonProgBar;
	
	private static JPanel sliderPanel;
	private TitledBorder sliderBorder;
	private static PearsonSlider slider;
	
	private static JPanel titlePanel;
	private static PearsonTextFieldPair limits = new PearsonTextFieldPair();
	private static JLabel titleLabel;
	
	private JPanel methodPanel;
	private TitledBorder methodBorder;
	private JPanel methodCountsPanel;
	private static JLabel countsLabel;
	private JPanel methodRadioPanel;
	private ButtonGroup methodGroup;
	private static JRadioButton basicButton;
	private static JRadioButton lassoButton;
	
	private JPanel partialWrapPanel;
	private TitledBorder partialWrapBorder;
	private static JPanel partialPanel;
	private static JButton runPartialButton;
	private static JButton viewPartialNetworkButton;
	private static JButton viewPartialResultsButton;
	private static JButton savePartialButton;
	private JPanel partialProgPanel;
	private JProgressBar partialProgBar;
	
	private JPanel analysisPanel;
	private TitledBorder analysisBorder;
	
	private JPanel buttonPanel;
	private JButton prevButton;
	private JButton closeButton;
	private JButton nextButton;
	
	public AnalysisDialog(String title) {
		JDialog dialog = new JDialog();
		dialog.setTitle(title);
		createControls(dialog);
		dialog.add(Box.createHorizontalStrut(8), BorderLayout.WEST);
		dialog.add(outerPanel, BorderLayout.CENTER);
		dialog.add(Box.createHorizontalStrut(8), BorderLayout.EAST);
		setInitialVisibilityStates();
		setInitialEnabledStates();
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				doCleanupInWorkerThread(null);
			}
		});
		dialog.setModal(true);
		dialog.setSize(600, 550);
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}
	
	private void createControls(final JDialog dialog) {
		innerPanel = new JPanel();
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
		
		outerPanel = new JPanel();
		outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.Y_AXIS));
				
		scrollPane = new JScrollPane(innerPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		outerPanel.add(scrollPane);
		
		inputFilePanel = new JPanel();
		inputFilePanel.setLayout(new BoxLayout(inputFilePanel, BoxLayout.X_AXIS));
		inputFilePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		inputFileComboBox = new JComboBox<FileData>();
		inputFileComboBox.setEditable(false);
		for (FileData fileData: CorrelationCalculator.getAppData().getFileDataStore()) {
			inputFileComboBox.insertItemAt(fileData, 0);
		}
		inputFileComboBox.setSelectedItem(CorrelationCalculator.getAppData().getFileData());
		//inputFileComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		inputFileComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if ("comboBoxChanged".equals(ae.getActionCommand())) {
					currentFileData = (FileData) inputFileComboBox.getSelectedItem();
					if (!exportInProgress) {
						countSamplesAndMetabolites();
						setInitialEnabledStates();
						boolean inputFileSelected = !"(none)".equals(currentFileData.getName());
						enableInputFileFormatPanel(inputFileSelected);
						enableDataNormPanel(inputFileSelected);
						enableInternalUsePanel(inputFileSelected);
						enableAnalysisPanel(inputFileSelected);
						enableSliderPanel(inputFileSelected && currentFileData.getDidPearsonCoeff());
					}
				}
			}
		});
		inputFileButton = new JButton("Browse...");
		inputFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				File file = CorrFileUtils.getFile("Select Metabolite Data Input File", CorrFileUtils.LOAD, "csv",
						"Comma-Separated Value Files", null);
				if (file != null) {
					inputFileFullPathName = file.getAbsolutePath();
					FileData fileData = new FileData();
					fileData.setName(file.getName());
					fileData.setRequestId(UUID.randomUUID());
					tempFileDirectoryName = TEMP_DIRECTORY + fileData.getRequestId().toString() + File.separatorChar;
					File tempFileDirectory = new File(tempFileDirectoryName);
					if (!tempFileDirectory.exists()) {
						try {
							tempFileDirectory.mkdir();
						} catch (SecurityException se) {
						}        
					}
					CorrelationCalculator.getAppData().getFileDataStore().add(fileData);
					currentFileData = fileData;
					inputFileComboBox.insertItemAt(currentFileData, 0);
					inputFileComboBox.setSelectedIndex(0);
					exportInProgress = true;
					doFileExportInWorkerThread();
				}
			}
		});
		inputFilePanel.add(Box.createHorizontalStrut(8));
		inputFilePanel.add(inputFileComboBox);
		inputFilePanel.add(Box.createHorizontalStrut(8));
		inputFilePanel.add(inputFileButton);
		inputFilePanel.add(Box.createHorizontalStrut(8));
		
		inputFileProgPanel = new JPanel();
		inputFileProgPanel.setLayout(new BoxLayout(inputFileProgPanel, BoxLayout.X_AXIS));
		inputFileProgPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		inputFileProgBar = new JProgressBar(0, 500);
		inputFileProgBar.setIndeterminate(true);
		inputFileProgPanel.add(inputFileProgBar);
		
		inputFileWrapPanel = new JPanel();
		inputFileWrapPanel.setLayout(new BoxLayout(inputFileWrapPanel, BoxLayout.Y_AXIS));
		inputFileWrapBorder = BorderFactory.createTitledBorder("Select File (must be .csv)");
		inputFileWrapBorder.setTitleFont(boldFontForTitlePanel(inputFileWrapBorder, false));
		inputFileWrapPanel.setBorder(inputFileWrapBorder);
		inputFileWrapPanel.add(inputFilePanel);
		inputFileWrapPanel.add(inputFileProgPanel);
		
		inputFileFormatPanel = new JPanel();
		inputFileFormatBorder = BorderFactory.createTitledBorder("Specify File Format   ");
		inputFileFormatBorder.setTitleFont(boldFontForTitlePanel(inputFileFormatBorder, false));
		inputFileFormatPanel.setBorder(inputFileFormatBorder);
		inputFileFormatPanel.setLayout(new BoxLayout(inputFileFormatPanel, BoxLayout.X_AXIS));
		inputFileFormatComboBox = new JComboBox<String>();
		inputFileFormatComboBox.setEditable(false);
		inputFileFormatComboBox.insertItemAt("Samples in Rows", 0);
		inputFileFormatComboBox.insertItemAt("Samples in Columns", 1);
		inputFileFormatComboBox.setSelectedIndex(0);
		//inputFileFormatComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		inputFileFormatComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if ("comboBoxChanged".equals(ae.getActionCommand())) {
					countSamplesAndMetabolites();
				}
			}
		});
		labeledCheckBox = new JCheckBox("Samples Labeled   ");
		labeledCheckBox.setSelected(true);
		labeledCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				countSamplesAndMetabolites();
			}
		});
		inputFileFormatPanel.add(Box.createHorizontalGlue());
		inputFileFormatPanel.add(inputFileFormatComboBox);
		inputFileFormatPanel.add(Box.createHorizontalGlue());
		
		inputPanel = new JPanel();
		inputBorder = BorderFactory.createTitledBorder("Input   ");
		inputBorder.setTitleFont(boldFontForTitlePanel(inputBorder, true));
		inputBorder.setTitleColor(TITLE_COLOR);
		inputPanel.setBorder(inputBorder);
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		inputPanel.add(inputFileWrapPanel);
		inputPanel.add(Box.createVerticalStrut(2));
		inputPanel.add(inputFileFormatPanel);
		
		transformCheckBox = new JCheckBox("Log2-Transform Data  ");
		transformCheckBox.setSelected(false);
		transformCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				runNormButton.setEnabled(transformCheckBox.isSelected() || scaleCheckBox.isSelected());
			}
		});
		scaleCheckBox = new JCheckBox("Autoscale Data   ");
		scaleCheckBox.setSelected(false);
		scaleCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				runNormButton.setEnabled(transformCheckBox.isSelected() || scaleCheckBox.isSelected());
			}
		});
		
		dataOptionsPanel = new JPanel();
		dataOptionsBorder = BorderFactory.createTitledBorder("Select Method(s)   ");
		dataOptionsBorder.setTitleFont(boldFontForTitlePanel(dataOptionsBorder, false));
		dataOptionsPanel.setBorder(dataOptionsBorder);
		dataOptionsPanel.setLayout(new BoxLayout(dataOptionsPanel, BoxLayout.X_AXIS));
		dataOptionsPanel.add(Box.createHorizontalGlue());
		dataOptionsPanel.add(transformCheckBox);
		dataOptionsPanel.add(Box.createHorizontalGlue());
		dataOptionsPanel.add(scaleCheckBox);
		dataOptionsPanel.add(Box.createHorizontalGlue());
		
		runNormButton = new JButton("Run");
		runNormButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				doNormInWorkerThread();
			}
		});
		viewNormButton = new JButton("View Normalized Data");
		viewNormButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String outputFile = TEMP_DIRECTORY + currentFileData.getRequestId().toString() + 
						File.separatorChar + FilenameUtils.getBaseName(inputFileFullPathName) + ".output." + 
						CorrelationConstants.NORMALIZE + ".csv";
				try {
					Desktop.getDesktop().open(new File(outputFile));
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		});
		saveNormButton = new JButton("Save...");
		saveNormButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String outputBase = FilenameUtils.getBaseName(inputFileFullPathName) + ".output." + 
						CorrelationConstants.NORMALIZE;
				File file = CorrFileUtils.getFile("Select Output Directory", CorrFileUtils.SAVE, outputBase);
				if (file == null) {
					return;
				}
				String path = file.getAbsolutePath();
				if (!path.endsWith(".csv")) {
					path += ".csv";
				}
				try {
					Files.copy(new File(TEMP_DIRECTORY + currentFileData.getRequestId().toString() + 
							File.separatorChar + outputBase + ".csv").toPath(), new File(path).toPath(), 
							StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		});
		
		dataActionPanel = new JPanel();
		dataActionPanel.setLayout(new BoxLayout(dataActionPanel, BoxLayout.X_AXIS));
		dataActionPanel.add(Box.createHorizontalGlue());
		dataActionPanel.add(runNormButton);
		dataActionPanel.add(Box.createHorizontalGlue());
		dataActionPanel.add(viewNormButton);
		dataActionPanel.add(Box.createHorizontalGlue());
		dataActionPanel.add(saveNormButton);
		dataActionPanel.add(Box.createHorizontalGlue());
		
		dataActionProgPanel = new JPanel();
		dataActionProgPanel.setLayout(new BoxLayout(dataActionProgPanel, BoxLayout.X_AXIS));
		dataActionProgPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		dataActionProgBar = new JProgressBar(0, 500);
		dataActionProgBar.setIndeterminate(true);
		dataActionProgPanel.add(dataActionProgBar);
		
		dataActionWrapPanel = new JPanel();
		dataActionWrapBorder = BorderFactory.createTitledBorder("Normalize Data   ");
		dataActionWrapBorder.setTitleFont(boldFontForTitlePanel(dataActionWrapBorder, false));
		dataActionWrapPanel.setBorder(dataActionWrapBorder);
		dataActionWrapPanel.setLayout(new BoxLayout(dataActionWrapPanel, BoxLayout.Y_AXIS));
		dataActionWrapPanel.add(dataActionPanel);
		dataActionWrapPanel.add(dataActionProgPanel);

		dataNormPanel = new JPanel();
		dataNormBorder = BorderFactory.createTitledBorder("Data Normalization   ");
		dataNormBorder.setTitleFont(boldFontForTitlePanel(dataNormBorder, true));
		dataNormBorder.setTitleColor(TITLE_COLOR);
		dataNormPanel.setBorder(dataNormBorder);
		dataNormPanel.setLayout(new BoxLayout(dataNormPanel, BoxLayout.Y_AXIS));
		dataNormPanel.add(dataOptionsPanel);
		dataNormPanel.add(Box.createVerticalStrut(2));
		dataNormPanel.add(dataActionWrapPanel);
		
		clusterPanel = new JPanel();
		clusterPanel.setLayout(new BoxLayout(clusterPanel, BoxLayout.X_AXIS));
		runClusterAnalysisButton = new JButton("Create Clusters");
		runClusterAnalysisButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				doPearsonAnalysisInWorkerThread(null, CorrelationConstants.PEARSON_TREECUT);
			}
		});
		viewClusterResultsButton = new JButton("View CSV File");
		viewClusterResultsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String outputFile = TEMP_DIRECTORY + currentFileData.getRequestId().toString() + File.separatorChar + 
						FilenameUtils.getBaseName(inputFileFullPathName) + ".output." + 
						CorrelationConstants.PEARSON_TREECUT + "." + filteredCount + "of" + 
						currentFileData.getNMetabolites() + ".csv";
				File file = new File(outputFile);
				if (file.exists()) {
					try {
						Desktop.getDesktop().open(file);
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				} else {
					doPearsonAnalysisInWorkerThread(outputFile, CorrelationConstants.PEARSON_TREECUT);
				}
			}
		});
		saveClusterResultsButton = new JButton("Save...");
		saveClusterResultsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String outputBase = FilenameUtils.getBaseName(inputFileFullPathName) + ".output." + 
						CorrelationConstants.PEARSON_TREECUT + "." + filteredCount + "of" + 
						currentFileData.getNMetabolites();
				File file = CorrFileUtils.getFile("Select Output Directory", CorrFileUtils.SAVE, outputBase);
				if (file == null) {
					return;
				}
				String path = file.getAbsolutePath();
				if (!path.endsWith(".csv")) {
					path += ".csv";
				}
				try {
					Files.copy(new File(TEMP_DIRECTORY + currentFileData.getRequestId().toString() + 
							File.separatorChar + outputBase + ".csv").toPath(), new File(path).toPath(), 
							StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		});
		clusterPanel.add(Box.createHorizontalGlue());
		clusterPanel.add(runClusterAnalysisButton);
		clusterPanel.add(Box.createHorizontalGlue());
		clusterPanel.add(viewClusterResultsButton);
		clusterPanel.add(Box.createHorizontalGlue());
		clusterPanel.add(saveClusterResultsButton);
		clusterPanel.add(Box.createHorizontalGlue());
		
		clusterProgPanel = new JPanel();
		clusterProgPanel.setLayout(new BoxLayout(clusterProgPanel, BoxLayout.X_AXIS));
		clusterProgPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		clusterProgBar = new JProgressBar(0, 500);
		clusterProgBar.setIndeterminate(true);
		clusterProgPanel.add(clusterProgBar);
		
		clusterWrapPanel = new JPanel();
		clusterWrapBorder = BorderFactory.createTitledBorder("Cluster Analysis   ");
		clusterWrapBorder.setTitleFont(boldFontForTitlePanel(clusterWrapBorder, false));
		clusterWrapPanel.setBorder(clusterWrapBorder);
		clusterWrapPanel.setLayout(new BoxLayout(clusterWrapPanel, BoxLayout.Y_AXIS));
		clusterWrapPanel.add(clusterPanel);
		clusterWrapPanel.add(clusterProgPanel);
		
		batchPanel = new JPanel();
		batchPanel.setLayout(new BoxLayout(batchPanel, BoxLayout.X_AXIS));
		runBatchAnalysisButton = new JButton("Run");
		runBatchAnalysisButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				doPartialAnalysisInWorkerThread(null, null, CorrelationConstants.PARTIAL_BATCH);
			}
		});
		viewBatchNetworkButton = new JButton("View in MetScape");
		viewBatchResultsButton = new JButton("View CSV File");
		viewBatchResultsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String outputFile = TEMP_DIRECTORY + currentFileData.getRequestId().toString() + 
						File.separatorChar + FilenameUtils.getBaseName(inputFileFullPathName) + 
						".output." + CorrelationConstants.PARTIAL_BATCH + "." + filteredCount + "of" + 
						currentFileData.getNMetabolites() + ".csv";
				File file = new File(outputFile);
				if (file.exists()) {
					try {
						Desktop.getDesktop().open(file);
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				} else {
					doPartialAnalysisInWorkerThread(outputFile, true, CorrelationConstants.PARTIAL_BATCH);
				}
			}
		});
		saveBatchResultsButton = new JButton("Save...");
		saveBatchResultsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String outputBase = FilenameUtils.getBaseName(inputFileFullPathName) + ".output." + 
						CorrelationConstants.PARTIAL_BATCH + "." + filteredCount + "of" + 
						currentFileData.getNMetabolites();
				File file = CorrFileUtils.getFile("Select Output Directory", CorrFileUtils.SAVE, outputBase);
				if (file == null) {
					return;
				}
				String path = file.getAbsolutePath();
				if (!path.endsWith(".csv")) {
					path += ".csv";
				}
				try {
					Files.copy(new File(TEMP_DIRECTORY + currentFileData.getRequestId().toString() + 
							File.separatorChar + outputBase + ".csv").toPath(), new File(path).toPath(), 
							StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		});
		batchPanel.add(Box.createHorizontalGlue());
		batchPanel.add(runBatchAnalysisButton);
		batchPanel.add(Box.createHorizontalGlue());
		batchPanel.add(viewBatchNetworkButton);
		batchPanel.add(Box.createHorizontalGlue());
		batchPanel.add(viewBatchResultsButton);
		batchPanel.add(Box.createHorizontalGlue());
		batchPanel.add(saveBatchResultsButton);
		batchPanel.add(Box.createHorizontalGlue());
		
		batchProgPanel = new JPanel();
		batchProgPanel.setLayout(new BoxLayout(batchProgPanel, BoxLayout.X_AXIS));
		batchProgPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		batchProgBar = new JProgressBar(0, 500);
		batchProgBar.setIndeterminate(true);
		batchProgPanel.add(batchProgBar);
		
		batchWrapPanel = new JPanel();
		batchWrapBorder = BorderFactory.createTitledBorder("Batch Analysis   ");
		batchWrapBorder.setTitleFont(boldFontForTitlePanel(batchWrapBorder, false));
		batchWrapPanel.setBorder(batchWrapBorder);
		batchWrapPanel.setLayout(new BoxLayout(batchWrapPanel, BoxLayout.Y_AXIS));
		batchWrapPanel.add(batchPanel);
		batchWrapPanel.add(batchProgPanel);
		
		compareParametersPanel = new JPanel();
		compareParametersPanel.setLayout(new BoxLayout(compareParametersPanel, BoxLayout.X_AXIS));
		iterLabel = new JLabel("Iterations:   ");
		iterBox = new NumericTextFieldWithListeners<Integer>(50, 1, 1000, 0);	
		lambdaLabel = new JLabel("Lambda:   ");
		lambdaBox = new NumericTextFieldWithListeners<Double>(0.03, 0.0, 1.0, 2);
		tuneButton = new JButton("Tune Lambda");
		tuneButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				doPartialAnalysisInWorkerThread(null, null, CorrelationConstants.PARTIAL_BIC_TUNE);
			}
		});
		compareParametersPanel.add(Box.createHorizontalStrut(20));
		compareParametersPanel.add(iterLabel);
		compareParametersPanel.add(Box.createHorizontalStrut(10));
		compareParametersPanel.add(iterBox);
		compareParametersPanel.add(Box.createHorizontalStrut(20));
		compareParametersPanel.add(lambdaLabel);
		compareParametersPanel.add(Box.createHorizontalStrut(10));
		compareParametersPanel.add(lambdaBox);
		compareParametersPanel.add(Box.createHorizontalStrut(20));
		compareParametersPanel.add(tuneButton);
		compareParametersPanel.add(Box.createHorizontalStrut(20));
		
		compareParametersProgPanel = new JPanel();
		compareParametersProgPanel.setLayout(new BoxLayout(compareParametersProgPanel, BoxLayout.X_AXIS));
		compareParametersProgPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		compareParametersProgBar = new JProgressBar(0, 500);
		compareParametersProgBar.setIndeterminate(true);
		compareParametersProgPanel.add(compareParametersProgBar);
		
		compareParametersWrapPanel = new JPanel();
		compareParametersWrapBorder = BorderFactory.createTitledBorder("Stability Selection Parameters   ");
		compareParametersWrapBorder.setTitleFont(boldFontForTitlePanel(compareParametersWrapBorder, false));
		compareParametersWrapPanel.setBorder(compareParametersWrapBorder);
		compareParametersWrapPanel.setLayout(new BoxLayout(compareParametersWrapPanel, BoxLayout.Y_AXIS));
		compareParametersWrapPanel.add(compareParametersPanel);
		compareParametersWrapPanel.add(compareParametersProgPanel);
		
		comparePanel = new JPanel();
		comparePanel.setLayout(new BoxLayout(comparePanel, BoxLayout.X_AXIS));
		compareButton = new JButton("Run");
		compareButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				doPartialAnalysisInWorkerThread(null, null, CorrelationConstants.PARTIAL_COMPARE);
			}
		});
		viewCompareNetworkButton = new JButton("View Network File");
		viewCompareNetworkButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String outputFile = TEMP_DIRECTORY + currentFileData.getRequestId().toString() + 
						File.separatorChar + FilenameUtils.getBaseName(inputFileFullPathName) + 
						".output." + CorrelationConstants.PARTIAL_COMPARE + "." + filteredCount + 
						"of" + currentFileData.getNMetabolites() + ".edge_list.csv";
				File file = new File(outputFile);
				if (file.exists()) {
					try {
						Desktop.getDesktop().open(file);
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				} else {
					doPartialAnalysisInWorkerThread(outputFile, true, CorrelationConstants.PARTIAL_COMPARE);
				}
			}
		});
		viewCompareMetabsButton = new JButton("View Metab File");
		viewCompareMetabsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String outputFile = TEMP_DIRECTORY + currentFileData.getRequestId().toString() + 
						File.separatorChar + FilenameUtils.getBaseName(inputFileFullPathName) + 
						".output." + CorrelationConstants.PARTIAL_COMPARE + "." + filteredCount + 
						"of" + currentFileData.getNMetabolites() + ".metab_info.csv";
				File file = new File(outputFile);
				if (file.exists()) {
					try {
						Desktop.getDesktop().open(file);
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				} else {
					doPartialAnalysisInWorkerThread(outputFile, true, CorrelationConstants.PARTIAL_COMPARE);
				}
			}
		});
		viewCompareStabSelButton = new JButton("View Stab Sel File");
		viewCompareStabSelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String outputFile = TEMP_DIRECTORY + currentFileData.getRequestId().toString() + 
						File.separatorChar + FilenameUtils.getBaseName(inputFileFullPathName) + 
						".output." + CorrelationConstants.PARTIAL_COMPARE + "." + filteredCount + 
						"of" + currentFileData.getNMetabolites() + ".sel_mat.csv";
				File file = new File(outputFile);
				if (file.exists()) {
					try {
						Desktop.getDesktop().open(file);
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				} else {
					doPartialAnalysisInWorkerThread(outputFile, true, CorrelationConstants.PARTIAL_COMPARE);
				}
			}
		});

		comparePanel.add(Box.createHorizontalGlue());
		comparePanel.add(compareButton);
		comparePanel.add(Box.createHorizontalGlue());
		comparePanel.add(viewCompareNetworkButton);
		comparePanel.add(Box.createHorizontalGlue());
		comparePanel.add(viewCompareMetabsButton);
		comparePanel.add(Box.createHorizontalGlue());
		comparePanel.add(viewCompareStabSelButton);
		comparePanel.add(Box.createHorizontalGlue());
		
		compareProgPanel = new JPanel();
		compareProgPanel.setLayout(new BoxLayout(compareProgPanel, BoxLayout.X_AXIS));
		compareProgPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		compareProgBar = new JProgressBar(0, 500);
		compareProgBar.setIndeterminate(true);
		compareProgPanel.add(compareProgBar);
		
		compareWrapPanel = new JPanel();
		compareWrapBorder = BorderFactory.createTitledBorder("Differential Network Analysis   ");
		compareWrapBorder.setTitleFont(boldFontForTitlePanel(compareWrapBorder, false));
		compareWrapPanel.setBorder(compareWrapBorder);
		compareWrapPanel.setLayout(new BoxLayout(compareWrapPanel, BoxLayout.Y_AXIS));
		compareWrapPanel.add(comparePanel);
		compareWrapPanel.add(compareProgPanel);
		
		hidePanel = new JPanel();
		hideButton = new JButton("Hide Internal Use Panel (restart calculator to show again)");
		hidePanel.add(hideButton);
		
		internalUsePanel = new JPanel();
		internalUseBorder = BorderFactory.createTitledBorder("Internal Use Only   ");
		internalUseBorder.setTitleFont(boldFontForTitlePanel(internalUseBorder, true));
		internalUseBorder.setTitleColor(TITLE_COLOR);
		internalUsePanel.setBorder(internalUseBorder);
		internalUsePanel.setLayout(new BoxLayout(internalUsePanel, BoxLayout.Y_AXIS));
		internalUsePanel.add(clusterWrapPanel);
		internalUsePanel.add(Box.createVerticalStrut(2));
		internalUsePanel.add(batchWrapPanel);
		internalUsePanel.add(Box.createVerticalStrut(2));
		internalUsePanel.add(compareParametersWrapPanel);
		internalUsePanel.add(Box.createVerticalStrut(2));
		internalUsePanel.add(compareWrapPanel);
		internalUsePanel.add(Box.createVerticalStrut(2));
		internalUsePanel.add(hidePanel);
		internalUsePanel.setVisible(isInternalVersion);
		
		hideButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				internalUsePanel.setVisible(false);
			}
		});
		
		pearsonPanel = new JPanel();
		pearsonPanel.setLayout(new BoxLayout(pearsonPanel, BoxLayout.X_AXIS));
		runPearsonButton = new JButton("Run");
		runPearsonButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				doPearsonAnalysisInWorkerThread(null, CorrelationConstants.PEARSON_COEFF);
			}
		});
		createPearsonHeatmapButton = new JButton("View Heatmap");
		createPearsonHeatmapButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String outputFile = TEMP_DIRECTORY + currentFileData.getRequestId().toString() + 
						File.separatorChar + FilenameUtils.getBaseName(inputFileFullPathName) + 
						".output." + CorrelationConstants.PEARSON_HEATMAP + "." + filteredCount + 
						"of" + currentFileData.getNMetabolites() + ".pdf";
				File file = new File(outputFile);
				if (file.exists()) {
					try {
						Desktop.getDesktop().open(file);
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				} else {
					doPearsonAnalysisInWorkerThread(outputFile, CorrelationConstants.PEARSON_HEATMAP);
				}
			}
		});
		viewPearsonDistributionButton = new JButton("View Histogram");
		viewPearsonDistributionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String outputFile = TEMP_DIRECTORY + currentFileData.getRequestId().toString() + 
						File.separatorChar + FilenameUtils.getBaseName(inputFileFullPathName) + 
						".output." + CorrelationConstants.PEARSON_DIST + "." + filteredCount + 
						"of" + currentFileData.getNMetabolites() + ".pdf";
				File file = new File(outputFile);
				if (file.exists()) {
					try {
						Desktop.getDesktop().open(file);
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				} else {
					doPearsonAnalysisInWorkerThread(outputFile, CorrelationConstants.PEARSON_DIST);
				}
			}
		});
		viewPearsonResultsButton = new JButton("View CSV File");
		viewPearsonResultsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String outputFile = TEMP_DIRECTORY + currentFileData.getRequestId().toString() + 
						File.separatorChar + FilenameUtils.getBaseName(inputFileFullPathName) + 
						".output." + CorrelationConstants.PEARSON_COEFF + "." + filteredCount + 
						"of" + currentFileData.getNMetabolites() + ".csv";
				File file = new File(outputFile);
				if (file.exists()) {
					try {
						Desktop.getDesktop().open(file);
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				} else {
					doPearsonAnalysisInWorkerThread(outputFile, CorrelationConstants.PEARSON_COEFF);
				}
			}
		});
		savePearsonButton = new JButton("Save...");
		savePearsonButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String outputFileFirstPiece = TEMP_DIRECTORY + currentFileData.getRequestId().toString() + 
						File.separatorChar + FilenameUtils.getBaseName(inputFileFullPathName) + ".output.";
				String outputFileSecondPiece = "." + filteredCount + "of" + currentFileData.getNMetabolites();
				
				saveInProgress = true;
				SaveDialog saveDialog = new SaveDialog("Save Files", outputFileFirstPiece, outputFileSecondPiece);
				saveInProgress = false;
				if (saveDialog.getCancelled()) {
					return;
				}
				
				File destFile = CorrFileUtils.getFile("Select Output Directory", CorrFileUtils.SAVE, 
						FilenameUtils.getBaseName(outputFileFirstPiece) + "." + CorrelationConstants.PEARSON_COEFF + outputFileSecondPiece);
				if (destFile == null) {
					return;
				}
				String destPath = destFile.getAbsolutePath();
				
				if (saveCoeffFile) {
					copyFileFromTempSpace(outputFileFirstPiece + CorrelationConstants.PEARSON_COEFF + outputFileSecondPiece, 
							destPath, ".csv");
				}
				
				if (saveHistogramFile) {
					copyFileFromTempSpace(outputFileFirstPiece + CorrelationConstants.PEARSON_DIST + outputFileSecondPiece, 
							destPath, ".pdf");
				}
				
				if (saveStaticHeatmapFile) {
					copyFileFromTempSpace(outputFileFirstPiece + CorrelationConstants.PEARSON_HEATMAP + outputFileSecondPiece, 
							destPath, ".pdf");
				}
				
				if (saveInteractiveHeatmapFiles) {
					String tempBase = outputFileFirstPiece + CorrelationConstants.PEARSON_CLUSTER + outputFileSecondPiece;
					copyFileFromTempSpace(tempBase, destPath, ".cdt");
					copyFileFromTempSpace(tempBase, destPath, ".atr");
					copyFileFromTempSpace(tempBase, destPath, ".gtr");
				}
			}
		});
		
		pearsonPanel.add(Box.createHorizontalGlue());
		pearsonPanel.add(runPearsonButton);
		pearsonPanel.add(Box.createHorizontalGlue());
		pearsonPanel.add(viewPearsonDistributionButton);
		pearsonPanel.add(Box.createHorizontalGlue());
		pearsonPanel.add(createPearsonHeatmapButton);
		pearsonPanel.add(Box.createHorizontalGlue());
		pearsonPanel.add(viewPearsonResultsButton);
		pearsonPanel.add(Box.createHorizontalGlue());
		pearsonPanel.add(savePearsonButton);
		pearsonPanel.add(Box.createHorizontalGlue());
		
		pearsonProgPanel = new JPanel();
		pearsonProgPanel.setLayout(new BoxLayout(pearsonProgPanel, BoxLayout.X_AXIS));
		pearsonProgPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		pearsonProgBar = new JProgressBar(0, 500);
		pearsonProgBar.setIndeterminate(true);
		pearsonProgPanel.add(pearsonProgBar);
		
		pearsonWrapPanel = new JPanel();
		pearsonWrapBorder = BorderFactory.createTitledBorder("Calculate Pearson's Correlations   ");
		pearsonWrapBorder.setTitleFont(boldFontForTitlePanel(pearsonWrapBorder, false));
		pearsonWrapPanel.setBorder(pearsonWrapBorder);
		pearsonWrapPanel.setLayout(new BoxLayout(pearsonWrapPanel, BoxLayout.Y_AXIS));
		pearsonWrapPanel.add(pearsonPanel);
		pearsonWrapPanel.add(pearsonProgPanel);
		
		titlePanel = new JPanel();
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
		titleLabel = new JLabel("Pearson's Correlation (Absolute Value)   ");
		titlePanel.add(Box.createHorizontalStrut(30));
		titlePanel.add(limits.getBoxPair()[PearsonTextFieldPair.LEFT_FIELD]);
		titlePanel.add(Box.createHorizontalStrut(30));
		titlePanel.add(titleLabel);
		titlePanel.add(Box.createHorizontalStrut(30));
		titlePanel.add(limits.getBoxPair()[PearsonTextFieldPair.RIGHT_FIELD]);
		titlePanel.add(Box.createHorizontalStrut(30));
		
		sliderPanel = new JPanel();
		sliderBorder = BorderFactory.createTitledBorder("Filter By Pearson's Correlations   ");
		sliderBorder.setTitleFont(boldFontForTitlePanel(sliderBorder, false));
		sliderPanel.setBorder(sliderBorder);
		sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
		slider = new PearsonSlider(SLIDER_EXTENT);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		sliderPanel.add(Box.createVerticalStrut(5));
		sliderPanel.add(titlePanel);
		sliderPanel.add(slider);
		sliderPanel.add(Box.createVerticalStrut(5));
		
		methodCountsPanel = new JPanel();
		methodCountsPanel.setLayout(new BoxLayout(methodCountsPanel, BoxLayout.X_AXIS));
		countsLabel = new JLabel("Metabolites included: N/A of N/A    Samples included: N/A   ");
		Font italicFont = new Font(countsLabel.getFont().getName(), Font.ITALIC, countsLabel.getFont().getSize());  
		countsLabel.setFont(italicFont);
		methodCountsPanel.add(Box.createHorizontalStrut(8));
		methodCountsPanel.add(countsLabel);
		methodCountsPanel.add(Box.createHorizontalStrut(8));
		
		methodRadioPanel = new JPanel();
		methodRadioPanel.setLayout(new BoxLayout(methodRadioPanel, BoxLayout.X_AXIS));
		methodGroup = new ButtonGroup();
		lassoButton = new JRadioButton("DSPC Method");
		lassoButton.setSelected(true);
		basicButton = new JRadioButton("Basic Partial Correlation Method");
		methodGroup.add(lassoButton);
		methodGroup.add(basicButton);
		methodRadioPanel.add(Box.createHorizontalGlue());
		methodRadioPanel.add(lassoButton);
		methodRadioPanel.add(Box.createHorizontalGlue());
		methodRadioPanel.add(basicButton);
		methodRadioPanel.add(Box.createHorizontalGlue());
		
		methodPanel = new JPanel();
		methodBorder = BorderFactory.createTitledBorder("Select Partial Correlation Method    ");
		methodBorder.setTitleFont(boldFontForTitlePanel(methodBorder, false));
		methodPanel.setBorder(methodBorder);
		methodPanel.setLayout(new BoxLayout(methodPanel, BoxLayout.Y_AXIS));
		methodPanel.add(Box.createVerticalStrut(3));
		methodPanel.add(methodCountsPanel);
		methodPanel.add(Box.createVerticalStrut(3));
		methodPanel.add(methodRadioPanel);
		methodPanel.add(Box.createVerticalStrut(3));
		
		partialPanel = new JPanel();
		partialPanel.setLayout(new BoxLayout(partialPanel, BoxLayout.X_AXIS));
		runPartialButton = new JButton("Run");
		runPartialButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				doPartialAnalysisInWorkerThread(null, null, basicButton.isSelected() ? 
						CorrelationConstants.PARTIAL_BASIC : CorrelationConstants.PARTIAL_LASSO);
			}
		});
		viewPartialNetworkButton = new JButton("View in MetScape");
		viewPartialNetworkButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String outputFile = TEMP_DIRECTORY + currentFileData.getRequestId().toString() + 
						File.separatorChar + FilenameUtils.getBaseName(inputFileFullPathName) + 
						".output." + (basicButton.isSelected() ? CorrelationConstants.PARTIAL_BASIC : 
						CorrelationConstants.PARTIAL_LASSO) + "." + filteredCount + "of" + 
						currentFileData.getNMetabolites() + ".csv";
				File file = new File(outputFile);
				if (file.exists()) {
					launchMetScapeInWorkerThread(outputFile);
				} else {
					doPartialAnalysisInWorkerThread(outputFile, false, basicButton.isSelected() ? 
							CorrelationConstants.PARTIAL_BASIC : CorrelationConstants.PARTIAL_LASSO);
				}
			}
		});
		viewPartialResultsButton = new JButton("View CSV File");
		viewPartialResultsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String outputFile = TEMP_DIRECTORY + currentFileData.getRequestId().toString() + 
						File.separatorChar + FilenameUtils.getBaseName(inputFileFullPathName) + 
						".output." + (basicButton.isSelected() ? CorrelationConstants.PARTIAL_BASIC : 
						CorrelationConstants.PARTIAL_LASSO) + "." + filteredCount + "of" + 
						currentFileData.getNMetabolites() + ".csv";
				File file = new File(outputFile);
				if (file.exists()) {
					try {
						Desktop.getDesktop().open(file);
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				} else {
					doPartialAnalysisInWorkerThread(outputFile, true, basicButton.isSelected() ? 
							CorrelationConstants.PARTIAL_BASIC : CorrelationConstants.PARTIAL_LASSO);
				}
			}
		});
		savePartialButton = new JButton("Save...");
		savePartialButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String outputBase = FilenameUtils.getBaseName(inputFileFullPathName) + 
						".output." + (basicButton.isSelected() ? CorrelationConstants.PARTIAL_BASIC : 
						CorrelationConstants.PARTIAL_LASSO) + "." + filteredCount + "of" + 
						currentFileData.getNMetabolites();
				File file = CorrFileUtils.getFile("Select Output Directory", CorrFileUtils.SAVE, outputBase);
				if (file == null) {
					return;
				}
				String path = file.getAbsolutePath();
				if (!path.endsWith(".csv")) {
					path += ".csv";
				}
				try {
					Files.copy(new File(TEMP_DIRECTORY + currentFileData.getRequestId().toString() + 
							File.separatorChar + outputBase + ".csv").toPath(), new File(path).toPath(), 
							StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		});
		partialPanel.add(Box.createHorizontalGlue());
		partialPanel.add(runPartialButton);
		partialPanel.add(Box.createHorizontalGlue());
		partialPanel.add(viewPartialNetworkButton);
		partialPanel.add(Box.createHorizontalGlue());
		partialPanel.add(viewPartialResultsButton);
		partialPanel.add(Box.createHorizontalGlue());
		partialPanel.add(savePartialButton);
		partialPanel.add(Box.createHorizontalGlue());
		
		partialProgPanel = new JPanel();
		partialProgPanel.setLayout(new BoxLayout(partialProgPanel, BoxLayout.X_AXIS));
		partialProgPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		partialProgBar = new JProgressBar(0, 500);
		partialProgBar.setIndeterminate(true);
		partialProgPanel.add(partialProgBar);
		
		partialWrapPanel = new JPanel();
		partialWrapBorder = BorderFactory.createTitledBorder("Calculate Partial Correlations   ");
		partialWrapBorder.setTitleFont(boldFontForTitlePanel(partialWrapBorder, false));
		partialWrapPanel.setBorder(partialWrapBorder);
		partialWrapPanel.setLayout(new BoxLayout(partialWrapPanel, BoxLayout.Y_AXIS));
		partialWrapPanel.add(partialPanel);
		partialWrapPanel.add(partialProgPanel);
		
		analysisPanel = new JPanel();
		analysisBorder = BorderFactory.createTitledBorder("Data Analysis    ");
		analysisBorder.setTitleFont(boldFontForTitlePanel(analysisBorder, true));
		analysisBorder.setTitleColor(TITLE_COLOR);
		analysisPanel.setBorder(analysisBorder);
		analysisPanel.setLayout(new BoxLayout(analysisPanel, BoxLayout.Y_AXIS));
		analysisPanel.add(pearsonWrapPanel);
		analysisPanel.add(Box.createVerticalStrut(2));
		analysisPanel.add(sliderPanel);
		analysisPanel.add(Box.createVerticalStrut(2));
		analysisPanel.add(methodPanel);
		analysisPanel.add(Box.createVerticalStrut(2));
		analysisPanel.add(partialWrapPanel);
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		prevButton = new JButton("<<  Previous");
		prevButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				tabbedPane.setSelectedIndex(tabbedPane.getSelectedIndex() - 1);
			}
		});
		
		closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				doCleanupInWorkerThread(dialog);
			}
		});
		
		nextButton = new JButton("Next  >>");
		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				tabbedPane.setSelectedIndex(tabbedPane.getSelectedIndex() + 1);
			}
		});
		
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(prevButton);
		buttonPanel.add(Box.createHorizontalGlue());
		//buttonPanel.add(closeButton);
		//buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(nextButton);
		buttonPanel.add(Box.createHorizontalGlue());
		
		inputWrapPanel = new JPanel();
		inputWrapPanel.setLayout(new BoxLayout(inputWrapPanel, BoxLayout.Y_AXIS));
		inputWrapPanel.add(inputPanel);
		LayoutUtils.addBlankLines(inputWrapPanel, 10);
		
		normWrapPanel = new JPanel();
		normWrapPanel.setLayout(new BoxLayout(normWrapPanel, BoxLayout.Y_AXIS));
		normWrapPanel.add(dataNormPanel);
		LayoutUtils.addBlankLines(normWrapPanel, 10);
		
		analysisWrapPanel = new JPanel();
		analysisWrapPanel.setLayout(new BoxLayout(analysisWrapPanel, BoxLayout.Y_AXIS));
		analysisWrapPanel.add(analysisPanel);
		
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Input  ", null, inputWrapPanel, "Select Input Files and Configuration   ");
		tabbedPane.addTab("Data Normalization  ", null, normWrapPanel, "Select Method and Normalize Data   ");
		tabbedPane.addTab("Data Analysis  ", null, analysisWrapPanel, "Choose Settings and Run Analysis  ");
		tabbedPane.addChangeListener(new ChangeListener() {
	        public void stateChanged(ChangeEvent ce) {
	            if (tabbedPane.getSelectedIndex() == 0) {
	            	prevButton.setEnabled(false);
	            	nextButton.setEnabled(true);
	            } else if (tabbedPane.getSelectedIndex() == tabbedPane.getTabCount() - 1) {
	            	prevButton.setEnabled(true);
	            	nextButton.setEnabled(false);
	            } else {
	            	prevButton.setEnabled(true);
	            	nextButton.setEnabled(true);
	            }
	        }
	    });
		
		innerPanel.add(Box.createVerticalStrut(5));
		innerPanel.add(tabbedPane);
		innerPanel.add(Box.createVerticalStrut(5));
		
		outerPanel.add(Box.createVerticalStrut(5));
		outerPanel.add(buttonPanel);
		outerPanel.add(Box.createVerticalStrut(5));
	}
	
	private void doFileExportInWorkerThread() {
		SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
		    @Override
		    public Boolean doInBackground() {
		    	inputFileProgPanel.setVisible(true);
				inputFilePanel.setVisible(false);
		    	return doFileExport();
		    }

		    @Override
		    public void done() {
		        try {
		        	if (!get()) {
		        		JOptionPane.showMessageDialog(null, "Error sending input file to server      ");
		        	}
		        } catch (InterruptedException ignore) {
		        } catch (ExecutionException ee) {
		            String why = null;
		            Throwable cause = ee.getCause();
		            if (cause != null) {
		                why = cause.getMessage();
		            } else {
		                why = ee.getMessage();
		            }
		            System.err.println("Error sending input file to server: " + why);
		        } finally {
		        	exportInProgress = false;
		        }
		        inputFilePanel.setVisible(true);
				inputFileProgPanel.setVisible(false);
				countSamplesAndMetabolites();
		        enableInputPanel(true);
				enableDataNormPanel(true);
				enableInternalUsePanel(true);
				enableAnalysisPanel(true);
				enableSliderPanel(false);
		    }
		};
		worker.execute();
	}
	
	private boolean doFileExport() {
		return doServerRequest(CorrelationConstants.FILE_EXPORT, null);
	}
	
	private void doNormInWorkerThread() {
		SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
		    @Override
		    public Boolean doInBackground() {
		    	disableAllActionControls();
				enableDataActionWrapPanel(true);
				dataActionProgPanel.setVisible(true);
				dataActionPanel.setVisible(false);
		    	return doNorm();
		    }

		    @Override
		    public void done() {
		        try {
		        	if (!get()) {
		        		JOptionPane.showMessageDialog(null, "Error doing data Normalization      ");
		        	}
		        } catch (InterruptedException ignore) {
		        } catch (ExecutionException ee) {
		            String why = null;
		            Throwable cause = ee.getCause();
		            if (cause != null) {
		                why = cause.getMessage();
		            } else {
		                why = ee.getMessage();
		            }
		            System.err.println("Error doing data Normalization: " + why);
		        }
		        dataActionPanel.setVisible(true);
				dataActionProgPanel.setVisible(false);
		        currentFileData.setDidTransform(transformCheckBox.isSelected());
				currentFileData.setDidScaling(scaleCheckBox.isSelected());
				enableInputPanel(true);
				enableDataActionPanel(true);
		    }
		};
		worker.execute();
	}
	
	private boolean doNorm() {
		return doServerRequest(CorrelationConstants.NORMALIZE, null);
	}
	
	public static void doPearsonAnalysisInWorkerThread(final String filename, final String analysis) {
		SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
		    @Override
		    public Boolean doInBackground() {
		    	disableAllActionControls();
		    	if (CorrelationConstants.PEARSON_TREECUT.equals(analysis)) {
		    		enableClusterWrapPanel(true);
					clusterProgPanel.setVisible(true);
					clusterPanel.setVisible(false);
		    	} else {
					enablePearsonWrapPanel(true);
					pearsonProgPanel.setVisible(true);
					pearsonPanel.setVisible(false);
		    	}
				if (saveInProgress) {
					if (CorrelationConstants.PEARSON_COEFF.equals(analysis)) {
						SaveDialog.getCoeffProgPanel().setVisible(true);
						SaveDialog.getCoeffPanel().setVisible(false);
					} else if (CorrelationConstants.PEARSON_DIST.equals(analysis)) {
						SaveDialog.getHistogramProgPanel().setVisible(true);
						SaveDialog.getHistogramPanel().setVisible(false);
					} else if (CorrelationConstants.PEARSON_HEATMAP.equals(analysis)) {
						SaveDialog.getStaticHeatmapProgPanel().setVisible(true);
						SaveDialog.getStaticHeatmapPanel().setVisible(false);
					} else if (CorrelationConstants.PEARSON_CLUSTER.equals(analysis)) {
						SaveDialog.getInteractiveHeatmapProgPanel().setVisible(true);
						SaveDialog.getInteractiveHeatmapPanel().setVisible(false);
					}
				}
		    	return doPearsonAnalysis(analysis);
		    }

		    @Override
		    public void done() {
		        try {
		        	if (!get()) {
		        		JOptionPane.showMessageDialog(null, "Error doing Pearson analysis      ");
		        	}
		        } catch (InterruptedException ignore) {
		        } catch (ExecutionException ee) {
		            String why = null;
		            Throwable cause = ee.getCause();
		            if (cause != null) {
		                why = cause.getMessage();
		            } else {
		                why = ee.getMessage();
		            }
		            System.err.println("Error doing Pearson analysis: " + why);
		        }
		        if (CorrelationConstants.PEARSON_TREECUT.equals(analysis)) {
		        	currentFileData.setDidPearsonTreecut(true);
					clusterPanel.setVisible(true);
					clusterProgPanel.setVisible(false);
		    	} else {
		    		pearsonPanel.setVisible(true);
					pearsonProgPanel.setVisible(false);
		    	}
				if (CorrelationConstants.PEARSON_COEFF.equals(analysis)) {
					currentFileData.setDidPearsonCoeff(true);
					if (saveInProgress) {
						SaveDialog.getCoeffPanel().setVisible(true);
						SaveDialog.getCoeffProgPanel().setVisible(false);
						SaveDialog.getSaveCoeffCheckBox().setEnabled(true);
						SaveDialog.getSaveCoeffCheckBox().setSelected(true);
						SaveDialog.getGenerateCoeffButton().setEnabled(false);
					}
				} else if (CorrelationConstants.PEARSON_DIST.equals(analysis)) {
					currentFileData.setDidPearsonDist(true);
					if (saveInProgress) {
						SaveDialog.getHistogramPanel().setVisible(true);
						SaveDialog.getHistogramProgPanel().setVisible(false);
						SaveDialog.getSaveHistogramCheckBox().setEnabled(true);
						SaveDialog.getSaveHistogramCheckBox().setSelected(true);
						SaveDialog.getGenerateHistogramButton().setEnabled(false);
					}
				} else if (CorrelationConstants.PEARSON_HEATMAP.equals(analysis)) {
					currentFileData.setDidPearsonHeatmap(true);
					if (saveInProgress) {
						SaveDialog.getStaticHeatmapPanel().setVisible(true);
						SaveDialog.getStaticHeatmapProgPanel().setVisible(false);
						SaveDialog.getSaveStaticHeatmapCheckBox().setEnabled(true);
						SaveDialog.getSaveStaticHeatmapCheckBox().setSelected(true);
						SaveDialog.getGenerateStaticHeatmapButton().setEnabled(false);
					}
				} else if (CorrelationConstants.PEARSON_CLUSTER.equals(analysis)) {
					currentFileData.setDidPearsonCluster(true);
					if (saveInProgress) {
						SaveDialog.getInteractiveHeatmapPanel().setVisible(true);
						SaveDialog.getInteractiveHeatmapProgPanel().setVisible(false);
						SaveDialog.getSaveInteractiveHeatmapCheckBox().setEnabled(true);
						SaveDialog.getSaveInteractiveHeatmapCheckBox().setSelected(true);
						SaveDialog.getGenerateInteractiveHeatmapButton().setEnabled(false);
					}
				}
		        enableInputPanel(true);
		        enableInternalUsePanel(true);
		        enablePearsonPanel(true);
		        enableSliderPanel(true);
		        if (filename != null) {
		        	try {
						Desktop.getDesktop().open(new File(filename));
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
		        }
		    }
		};
		worker.execute();
	}
	
	private static boolean doPearsonAnalysis(String analysis) {
		boolean retVal = doServerRequest(analysis, null);
		if (CorrelationConstants.PEARSON_COEFF.equals(analysis) && !currentFileData.getDidPearsonCoeff()) {
			parsePearsonMatrix(new File(TEMP_DIRECTORY + currentFileData.getRequestId().toString() + 
					File.separatorChar + FilenameUtils.getBaseName(inputFileFullPathName) + ".output." + 
					CorrelationConstants.PEARSON_COEFF + "." + filteredCount + "of" + 
					currentFileData.getNMetabolites() + ".csv"));
		}
		return retVal;
	}
	
	private void copyFileFromTempSpace(String sourceBase, String destBase, String extension) {
		File sourceFile = new File(sourceBase + extension);
		File destFile = new File(destBase + extension);
		if (sourceFile.exists()) {
			try {
				Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
	
	private void doPartialAnalysisInWorkerThread(final String filename, final Boolean viewResults, final String analysis) {
		SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
		    @Override
		    public Boolean doInBackground() {
		    	disableAllActionControls();
		    	if (CorrelationConstants.PARTIAL_BATCH.equals(analysis)) {
		    		enableBatchWrapPanel(true);
					batchProgPanel.setVisible(true);
					batchPanel.setVisible(false);
		    	} else if (CorrelationConstants.PARTIAL_BASIC.equals(analysis) ||
		    			CorrelationConstants.PARTIAL_LASSO.equals(analysis)) {
					enablePartialWrapPanel(true);
					partialProgPanel.setVisible(true);
					partialPanel.setVisible(false);
		    	} else if (CorrelationConstants.PARTIAL_COMPARE.equals(analysis)) {
		    		enableCompareWrapPanel(true);
		    		compareProgPanel.setVisible(true);
		    		comparePanel.setVisible(false);
		    	} else if (CorrelationConstants.PARTIAL_BIC_TUNE.equals(analysis)) {
		    		enableCompareParametersWrapPanel(true);
		    		compareParametersProgPanel.setVisible(true);
		    		compareParametersPanel.setVisible(false);
		    	}
		    	return doPartialAnalysis(analysis);
		    }

		    @Override
		    public void done() {
		        try {
		        	if (!get()) {
		        		JOptionPane.showMessageDialog(null, "Error doing partial correlation analysis      ");
		        	}
		        } catch (InterruptedException ignore) {
		        } catch (ExecutionException ee) {
		            String why = null;
		            Throwable cause = ee.getCause();
		            if (cause != null) {
		                why = cause.getMessage();
		            } else {
		                why = ee.getMessage();
		            }
		            System.err.println("Error doing partial correlation analysis: " + why);
		        }
		        if (CorrelationConstants.PARTIAL_BATCH.equals(analysis)) {
					batchPanel.setVisible(true);
					batchProgPanel.setVisible(false);
					enablePartialPanel(true);
		    	} else if (CorrelationConstants.PARTIAL_BASIC.equals(analysis) ||
		    			CorrelationConstants.PARTIAL_LASSO.equals(analysis)) {
		    		partialPanel.setVisible(true);
					partialProgPanel.setVisible(false);
					currentFileData.setDidPartial(true);
					enablePartialPanel(true);
		    	} else if (CorrelationConstants.PARTIAL_COMPARE.equals(analysis)) {
		    		comparePanel.setVisible(true);
		    		compareProgPanel.setVisible(false);
		    		enableComparePanel(true);
		    	} else if (CorrelationConstants.PARTIAL_BIC_TUNE.equals(analysis)) {
		    		compareParametersPanel.setVisible(true);
		    		compareParametersProgPanel.setVisible(false);
		    		enableCompareParametersPanel(true);
		    	}
				enableInputPanel(true);
				if (filename != null) {
					if (viewResults) {
			        	try {
							Desktop.getDesktop().open(new File(filename));
						} catch (IOException ioe) {
							ioe.printStackTrace();
						}
					} else {
						launchMetScapeInWorkerThread(filename);
					}
		        }
		    }
		};
		worker.execute();
	}
	
	private boolean doPartialAnalysis(final String analysis) {
		return doServerRequest(analysis, null);
	}
	
	private void launchMetScapeInWorkerThread(final String outputFile) {
		final Component parent = analysisPanel;
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
		    @Override
		    public Void doInBackground() {
		    	disableAllActionControls();
				enablePartialWrapPanel(true);
				partialProgPanel.setVisible(true);
				partialPanel.setVisible(false);
				String[] arguments = new String[2];
				arguments[0] = "-P";
				arguments[1] = "correlationFile=" + outputFile;
				
				new LaunchEngine(arguments, parent);
				return null;
		    }

		    @Override
		    public void done() {
		        try {
		        	get();
		        } catch (InterruptedException ignore) {
		        } catch (ExecutionException ee) {
		            String why = null;
		            Throwable cause = ee.getCause();
		            if (cause != null) {
		                why = cause.getMessage();
		            } else {
		                why = ee.getMessage();
		            }
		            System.err.println("Error launching MetScape: " + why);
		        }
		        partialPanel.setVisible(true);
				partialProgPanel.setVisible(false);
				enableInputPanel(true);
				enablePartialPanel(true);
		    }
		};
		worker.execute();
	}
	
	private void doCleanupInWorkerThread(final JDialog dialog) {
		SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
		    @Override
		    public Boolean doInBackground() {
		    	return doCleanup();
		    }

		    @Override
		    public void done() {
		        try {
		        	if (!get()) {
		        		JOptionPane.showMessageDialog(null, "Error doing cleanup      ");
		        	}
		        } catch (InterruptedException ignore) {
		        } catch (ExecutionException ee) {
		            String why = null;
		            Throwable cause = ee.getCause();
		            if (cause != null) {
		                why = cause.getMessage();
		            } else {
		                why = ee.getMessage();
		            }
		            System.err.println("Error doing cleanup: " + why);
		        }
		        if (dialog != null) {
		        	dialog.setVisible(false);
		        }
		    }
		};
		worker.execute();
	}
	
	private boolean doCleanup() {
		if (currentFileData == null) {
			return true;
		}
		List<FileData> fileDataStore = CorrelationCalculator.getAppData().getFileDataStore();
		if (fileDataStore == null) {
			return true;
		}
		boolean retVal = true;
		for (FileData fileData: fileDataStore) {
			if ("(none)".equals(fileData.getName())) {
				continue;
			}
			String requestId = fileData.getRequestId().toString();
			removeTempFiles(requestId);
			retVal &= doServerRequest(CorrelationConstants.CLEANUP, requestId);
		}
		return retVal;
	}
	
	private void removeTempFiles(String uniqueTag) {
		File tempDir = new File(TEMP_DIRECTORY);
		for (File directory: tempDir.listFiles()) {
			if (directory.isDirectory() && directory.getName().contains(uniqueTag)) {
				for (File file: directory.listFiles()) {
					file.delete();
				}
				directory.delete();
				break;
			}
		}
	}
	
	private static boolean doServerRequest(String analysisName, String requestId) {
		CorrelationArguments arguments = new CorrelationArguments();
		arguments.setRowSamples("Samples in Rows".equals(inputFileFormatComboBox.getSelectedItem().toString()) ? "true" : "false");
		arguments.setLabeledSamples("true");
		arguments.setDoTransform(transformCheckBox.isSelected() ? "true" : "false");
		arguments.setDoScaling(scaleCheckBox.isSelected() ? "true" : "false");
		arguments.setAnalysis(analysisName);
		arguments.setRequestId(requestId != null ? requestId : currentFileData.getRequestId().toString());
		
		Response<String> response = null;
		if (CorrelationConstants.FILE_EXPORT.equals(analysisName)) {
			response = sendInputFileToServer(arguments);
		} else {
			arguments.setClientData("");
			arguments.setFirstBuffer("false");
			arguments.setLastBuffer("true");
			arguments.setExclusionList(getExclusionList());
			arguments.setPearsonLowerThreshold(getPearsonLowerThreshold());
			arguments.setPearsonUpperThreshold(getPearsonUpperThreshold());
			arguments.setStabSelParam(lambdaBox.getText());
			arguments.setStabSelIters(iterBox.getText());
			MetRService service = new MetRService(HttpRequestType.POST, null);
			response = service.submitMetRRequest(arguments);
			CorrelationCalculator.getLogger().info("Finished with " + analysisName + " analysis.");
			if (response != null && response.getResponseStatus().isSuccess()) {
				String result = response.getResponseValue();
				if (CorrelationConstants.NORMALIZE.equals(analysisName)) {
					String outputFilename = FilenameUtils.getBaseName(inputFileFullPathName) + 
							".output." + analysisName + ".csv";
					response = getOutputFileFromServer(arguments, result, 0, outputFilename, false);
				} else if (CorrelationConstants.PEARSON_COEFF.equals(analysisName)) {
					String outputFilename = FilenameUtils.getBaseName(inputFileFullPathName) + 
							".output." + analysisName +
							"." + filteredCount + "of" + currentFileData.getNMetabolites() + ".csv";
					response = getOutputFileFromServer(arguments, result, 0, outputFilename, false);
				} else if (CorrelationConstants.PEARSON_DIST.equals(analysisName)) {
					String outputFilename = FilenameUtils.getBaseName(inputFileFullPathName) + 
							".output." + analysisName + "." + filteredCount + "of" + 
							currentFileData.getNMetabolites() + ".pdf";
					response = getOutputFileFromServer(arguments, result, 0, outputFilename, true);
				} else if (CorrelationConstants.PEARSON_HEATMAP.equals(analysisName)) {
					String outputFilename = FilenameUtils.getBaseName(inputFileFullPathName) + 
							".output." + analysisName + "." + filteredCount + "of" + 
							currentFileData.getNMetabolites() + ".pdf";
					response = getOutputFileFromServer(arguments, result, 0, outputFilename, true);
				} else if (CorrelationConstants.PEARSON_CLUSTER.equals(analysisName)) {
					String outputFileBase = FilenameUtils.getBaseName(inputFileFullPathName) + 
							".output." + analysisName + "." + filteredCount + "of" + 
							currentFileData.getNMetabolites();
					response = getOutputFileFromServer(arguments, result, 0, outputFileBase + ".cdt", false);
					if (response != null  && response.getResponseStatus().isSuccess()) {
						response = getOutputFileFromServer(arguments, result, 2, outputFileBase + ".atr", false);
						if (response != null  && response.getResponseStatus().isSuccess()) {
							response = getOutputFileFromServer(arguments, result, 4, outputFileBase + ".gtr", false);
						}
					}
				} else if (CorrelationConstants.PEARSON_TREECUT.equals(analysisName)) {
					String outputFilename = FilenameUtils.getBaseName(inputFileFullPathName) + 
							".output." + analysisName + "." + filteredCount + "of" + 
							currentFileData.getNMetabolites() + ".csv";
					response = getOutputFileFromServer(arguments, result, 0, outputFilename, false);
				} else if (CorrelationConstants.PARTIAL_BASIC.equals(analysisName) || 
						CorrelationConstants.PARTIAL_LASSO.equals(analysisName)) {
					String outputFileBase = FilenameUtils.getBaseName(inputFileFullPathName) + 
							".output." + analysisName + "." + filteredCount + "of" + 
							currentFileData.getNMetabolites();
					response = getOutputFileFromServer(arguments, result, 0, outputFileBase + ".csv", false);
					if (response != null  && response.getResponseStatus().isSuccess()) {
						response = getOutputFileFromServer(arguments, result, 2, outputFileBase + "_coeffs.csv", false);
						if (response != null  && response.getResponseStatus().isSuccess()) {
							response = getOutputFileFromServer(arguments, result, 4, outputFileBase + "_pvals.csv", false);
							if (response != null  && response.getResponseStatus().isSuccess()) {
								response = getOutputFileFromServer(arguments, result, 6, outputFileBase + "_qvals.csv", false);
							}
						}
					}
					writeMetabolitesAsPairs(outputFileBase);
					combineFiles(outputFileBase, false, true);
				} else if (CorrelationConstants.PARTIAL_BATCH.equals(analysisName)) {
					String clusterFilename = TEMP_DIRECTORY + currentFileData.getRequestId().toString() + 
							File.separatorChar + FilenameUtils.getBaseName(inputFileFullPathName) + 
							".output." + CorrelationConstants.PEARSON_TREECUT + "." + filteredCount + 
							"of" + currentFileData.getNMetabolites() + ".csv";
					CorrelationCalculator.getLogger().info(clusterFilename);
					dataMap = parseDataFile(inputFileFullPathName);
					clusterMap = parseClusterFile(clusterFilename);
					if (dataMap == null || dataMap.isEmpty() || clusterMap == null || clusterMap.isEmpty()) {
						response = null;
					} else {
						String inputFilenameStart = TEMP_DIRECTORY + currentFileData.getRequestId().toString() + 
								File.separatorChar + FilenameUtils.getBaseName(inputFileFullPathName) + ".input.cluster";
						String inputFilenameEnd = "." + filteredCount + "of" + currentFileData.getNMetabolites() + ".csv";
						String outputFileBase = FilenameUtils.getBaseName(inputFileFullPathName) + ".output." + 
								CorrelationConstants.PARTIAL_LASSO + "." + filteredCount + "of" + 
								currentFileData.getNMetabolites();
						for (Integer cluster: clusterMap.keySet()) {
							createClusterDataFile(inputFilenameStart, inputFilenameEnd, cluster);
							sendClusterFileToServer(inputFilenameStart + cluster.toString() + inputFilenameEnd);
							runDGLassoOnDataFile();
							writeResultsToClusterSpecificFile(outputFileBase, cluster);
							appendResultsToOutputFile(outputFileBase, cluster == 1);
						}
					}
				} else if (CorrelationConstants.PARTIAL_COMPARE.equals(analysisName)) {
					String outputFileBase = FilenameUtils.getBaseName(inputFileFullPathName) + 
							".output." + analysisName + "." + filteredCount + "of" + 
							currentFileData.getNMetabolites();
					response = getOutputFileFromServer(arguments, result, 0, outputFileBase + ".sel_mat.csv", false);
					if (response != null  && response.getResponseStatus().isSuccess()) {
						response = getOutputFileFromServer(arguments, result, 2, outputFileBase + ".edge_list.csv", false);
						if (response != null  && response.getResponseStatus().isSuccess()) {
							response = getOutputFileFromServer(arguments, result, 4, outputFileBase + ".metab_info.csv", false);
						}
					}
				} else if (CorrelationConstants.PARTIAL_BIC_TUNE.equals(analysisName)) {
					String outputFilename = FilenameUtils.getBaseName(inputFileFullPathName) + 
							".output." + analysisName + "." + filteredCount + "of" + 
							currentFileData.getNMetabolites() + ".lambda.csv";
					response = getOutputFileFromServer(arguments, result, 0, outputFilename, false);
					updateLambda();
				}
			}
		}
		
		return (response != null && response.getResponseStatus().isSuccess());
	}
	
	private static void updateLambda() {
		String outputFile = TEMP_DIRECTORY + currentFileData.getRequestId().toString() + 
				File.separatorChar + FilenameUtils.getBaseName(inputFileFullPathName) + 
				".output." + CorrelationConstants.PARTIAL_BIC_TUNE + "." + filteredCount + 
				"of" + currentFileData.getNMetabolites() + ".lambda.csv";
		try {
			BufferedReader br = new BufferedReader(new FileReader(outputFile));
			String lineStr = null;
			if ((lineStr = br.readLine()) == null) {
				br.close();
				return;
			}
			Double lambda = Double.parseDouble(lineStr);
			if (lambda == null || lambda.isNaN()) {
				lambdaBox.setText("N/A");
			} else {
				lambdaBox.setText(lambda.toString());
			}
			br.close();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	private static Map<Integer, List<String>> parseClusterFile(String clusterFilename) {
		Map<Integer, List<String>> clusterMap = new HashMap<Integer, List<String>>();
		
		File clusterFile = new File(clusterFilename);
		if (!clusterFile.exists()) {
			return null;
		}
		
		try {
			DataFile base = new TextFile(clusterFile);
			
			for (int row = base.getStartRowIndex() + 1; row <= base.getEndRowIndex(); row++) {
				String compoundNameOrId = base.getString(row, 0);
				Integer cluster = base.getInteger(row, 1);
				if (compoundNameOrId == null || cluster == null) {
					continue;
				}
				compoundNameOrId = CharMatcher.WHITESPACE.trimFrom(compoundNameOrId);
				List<String> compounds = clusterMap.get(cluster);
				if (compounds == null) {
					compounds = new ArrayList<String>();
				}
				if (!compounds.contains(compoundNameOrId)) {
					compounds.add(compoundNameOrId);
					clusterMap.put(cluster, compounds);
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
		CorrelationCalculator.getLogger().info("Cluster map has " + clusterMap.size() + " entries");
		
		return clusterMap;
	}
	
	private static Map<String, String> parseDataFile(String dataFile) {
		Map<String, String> dataMap = new HashMap<String, String>();
		String dataLine = null;
		String [] feature = null;
		
		// only works for unlabeled samples with features in rows
		try {
			BufferedReader input = new BufferedReader(new FileReader(new File(dataFile)));
			CSVParser commaParser = new CSVParser();
			while ((dataLine = input.readLine()) != null) {
				feature = commaParser.parseLine(dataLine);
				if (feature != null && feature.length > 0) {
					dataMap.put(feature[0], dataLine);
				}
			}
		    input.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		CorrelationCalculator.getLogger().info("Data map has " + dataMap.size() + " entries");
		
		return dataMap;
	}
	
	private static void createClusterDataFile(String filenameStart, String filenameEnd, Integer cluster) {
		List<String> compounds = clusterMap.get(cluster);
		if (compounds == null || compounds.isEmpty()) {
			return;
		}
		String filename = filenameStart + cluster.toString() + filenameEnd;
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(filename), CorrelationConstants.BUFFER_SIZE);
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
		for (String compound: compounds) {
			try {
				String dataLine = dataMap.get(compound);
				if (dataLine != null && !dataLine.isEmpty()) {
					dataLine += "\n";
					bos.write(dataLine.getBytes());
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		try {
			bos.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	private static void sendClusterFileToServer(String filename) {
		CorrelationArguments arguments = new CorrelationArguments();
		arguments.setRowSamples("true");
		arguments.setLabeledSamples("false");
		arguments.setDoTransform(transformCheckBox.isSelected() ? "true" : "false");
		arguments.setDoScaling(scaleCheckBox.isSelected() ? "true" : "false");
		arguments.setAnalysis(CorrelationConstants.FILE_EXPORT);
		arguments.setRequestId(currentFileData.getRequestId().toString());
		arguments.setExclusionList("");
		BufferedInputStream bis = null;
		File inputFile = null;
		byte[] buffer = new byte[CorrelationConstants.BUFFER_SIZE];
		
		try {
			inputFile = new File(filename);
			bis = new BufferedInputStream(new FileInputStream(inputFile), CorrelationConstants.BUFFER_SIZE);
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
		
		int nRequests = (int) Math.ceil(inputFile.length() / (CorrelationConstants.BUFFER_SIZE + 0.0));
		for (int bufIndex = 0; bufIndex < nRequests; bufIndex++) {
			Integer len = null;
			try {
				len = bis.read(buffer);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			String dataAsString = null;
			if (len < CorrelationConstants.BUFFER_SIZE) {
				dataAsString = new String(Arrays.copyOfRange(buffer, 0, len));
			} else {
				dataAsString = new String(buffer);
			}
			arguments.setClientData(dataAsString);
			arguments.setFirstBuffer(bufIndex == 0 ? "true" : "false");
			arguments.setLastBuffer(bufIndex == nRequests - 1 ? "true" : "false");
			MetRService service = new MetRService(HttpRequestType.POST, null);
			service.submitMetRRequest(arguments);
		}
		
		try {
			bis.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	private static void runDGLassoOnDataFile() {
		doServerRequest(CorrelationConstants.PARTIAL_LASSO, null);
	}
	
	private static void appendResultsToOutputFile(String outputFileBase, boolean fFirst) {
		combineFiles(outputFileBase, true, fFirst);
	}
	
	private static void writeResultsToClusterSpecificFile(String outputFileBase, Integer cluster) {
		BufferedReader brMetabs = null;
		BufferedReader brCoeffs = null;
		BufferedReader brPvals = null;
		BufferedReader brQvals = null;
		String ioBase = TEMP_DIRECTORY + currentFileData.getRequestId().toString() + File.separatorChar + outputFileBase;
		try {
			brMetabs = new BufferedReader(new FileReader(ioBase + "_metabs.csv"));
			brCoeffs = new BufferedReader(new FileReader(ioBase + "_coeffs.csv"));
			brPvals = new BufferedReader(new FileReader(ioBase + "_pvals.csv"));
			brQvals = new BufferedReader(new FileReader(ioBase + "_qvals.csv"));
			File outputFile = new File(HOME_DIRECTORY + File.separatorChar + "BatchOutput" + File.separatorChar +
					FilenameUtils.getBaseName(inputFileFullPathName) + File.separatorChar +
					outputFileBase.replaceFirst(CorrelationConstants.PARTIAL_LASSO, CorrelationConstants.PARTIAL_BATCH) +
					".cluster" + cluster + ".csv");
			String metabNames = null;			
			try {
				FileUtils.writeStringToFile(outputFile, "metab1, metab2, pcor, pval, adj pval" + LINE_SEPARATOR, 
						null, false);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
						
			StringBuilder columnBasedString = new StringBuilder();
			int k = 0;
			while ((metabNames = brMetabs.readLine()) != null) {
				columnBasedString.append(metabNames + ",\"" + brCoeffs.readLine() + "\",\"" + brPvals.readLine() + 
						"\",\"" + brQvals.readLine() + "\"" + LINE_SEPARATOR);
				k++;
				if (k % 1000 == 0) {
					try {
						FileUtils.writeStringToFile(outputFile, columnBasedString.toString(), null, true);
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
					columnBasedString.delete(0, columnBasedString.length());
				}
			}
			
			if (columnBasedString.length() > 0) {
				FileUtils.writeStringToFile(outputFile, columnBasedString.toString(), null, true);
			}
			
			brMetabs.close();
			brCoeffs.close();
			brPvals.close();
			brQvals.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	private static Response<String> sendInputFileToServer(CorrelationArguments arguments) {
		File inputFile = null;
		BufferedInputStream bis = null;
		Response<String> response = null;
		byte[] buffer = new byte[CorrelationConstants.BUFFER_SIZE];
		
		inputFileProgBar.setString("Validating Input File ...   ");
		inputFileProgBar.setStringPainted(true);
		if (!isValidInputFile()) {
			inputFileProgBar.setStringPainted(false);		
			return response;
		}
			
		inputFileProgBar.setString("Sending Input File to Server ...   ");
		try {
			inputFile = new File(inputFileFullPathName);
			bis = new BufferedInputStream(new FileInputStream(inputFile), CorrelationConstants.BUFFER_SIZE);
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
		
		arguments.setExclusionList("");
		int nRequests = (int) Math.ceil(inputFile.length() / (CorrelationConstants.BUFFER_SIZE + 0.0));
		for (int bufIndex = 0; bufIndex < nRequests; bufIndex++) {
			Integer len = null;
			try {
				len = bis.read(buffer);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			String dataAsString = null;
			if (len < CorrelationConstants.BUFFER_SIZE) {
				dataAsString = new String(Arrays.copyOfRange(buffer, 0, len));
			} else {
				dataAsString = new String(buffer);
			}
			arguments.setClientData(dataAsString);
			arguments.setFirstBuffer(bufIndex == 0 ? "true" : "false");
			arguments.setLastBuffer(bufIndex == nRequests - 1 ? "true" : "false");
			MetRService service = new MetRService(HttpRequestType.POST, null);
			response = service.submitMetRRequest(arguments);
			if (response == null || !response.getResponseStatus().isSuccess()) {
				break;
			}
		}
		
		try {
			bis.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		inputFileProgBar.setStringPainted(false);		
		return response;
	}
	
	private static Boolean isValidInputFile() {
    	TextFile inputFile = null;
		try {
			inputFile = new TextFile(new File(inputFileFullPathName));
		} catch (IOException | NullPointerException e) {
			JOptionPane.showMessageDialog(null, "File format error: Empty or invalid input file      ");
    		return false;
		}
		
		if (!enoughEntries(inputFile)) {
			JOptionPane.showMessageDialog(null, "File format error: Must have at least 2 rows and 2 columns     ");
    		return false;
		}
    	
    	Integer k = missingRowLabel(inputFile);
    	if (k > 0) {
    		JOptionPane.showMessageDialog(null, "File format error: Missing row label for row " + k + "      ");
    		return false;
    	}
    	
    	k = missingColumnLabel(inputFile);
    	if (k > 0) {
    		JOptionPane.showMessageDialog(null, "File format error: Missing column label for column " + k + "      ");
    		return false;
    	}
    	
    	k = nonMatchingRows(inputFile);
    	if (k > 0) {
    		JOptionPane.showMessageDialog(null, "File format error: Wrong number of entries in row " + k + "      ");
    		return false;
    	}
    
    	return true;
    }
	
	private static Boolean enoughEntries(TextFile file) {
		if (file.getEndRowIndex() < 2) {
			return false;
		}
		
		if (file.getEndColIndex(0) < 2) {
			return false;
		}
		
		return true;
	}
	
	private static Integer missingRowLabel(TextFile file) {
		for (int i = 1; i <= file.getEndRowIndex(); i++) {
			if (file.getString(i, 0).isEmpty()) {
				return i + 1;
			}
		}
		
		return -1;
	}
	
	private static Integer missingColumnLabel(TextFile file) {
		for (int i = 1; i <= file.getEndColIndex(0); i++) {
			if (file.getString(0, i).isEmpty()) {
				return i + 1;
			}
		}
		
		return -1;
	}
	
	private static Integer nonMatchingRows(TextFile file) {
		int width = file.getEndColIndex(0);
		for (int i = 1; i <= file.getEndRowIndex(); i++) {
			if (file.getEndColIndex(i) != width) {
				return i + 1;
			}
		}
		
		return -1;
	}
	
	private static Response<String> getOutputFileFromServer(CorrelationArguments arguments, String result, int offset,
			String outputFilename, boolean isBinaryFile) {
		File outputFile = new File(TEMP_DIRECTORY + currentFileData.getRequestId().toString() + 
				File.separatorChar + outputFilename);
		BufferedOutputStream bos = null;
		String[] filesWithSizes = result.split(",");
		Response<String> response = null;
			
		try {
			bos = new BufferedOutputStream(new FileOutputStream(outputFile), CorrelationConstants.BUFFER_SIZE);
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
		
		Integer nBytes = Integer.valueOf(filesWithSizes[offset + 1]);
		int nRequests = (int) Math.ceil(nBytes / (CorrelationConstants.BUFFER_SIZE + 0.0));
		arguments.setAnalysis(CorrelationConstants.FILE_IMPORT);
		arguments.setServerFile(filesWithSizes[offset]);
		arguments.setExclusionList("");
		for (int bufIndex = 0; bufIndex < nRequests; bufIndex++) {
			arguments.setFirstBuffer(bufIndex == 0 ? "true" : "false");
			arguments.setLastBuffer(bufIndex == nRequests - 1 ? "true" : "false");
			MetRService service = new MetRService(HttpRequestType.POST, null);
			response = service.submitMetRRequest(arguments);
			if (response == null || !response.getResponseStatus().isSuccess()) {
				return response;
			}
			try {
				if (isBinaryFile) {
					bos.write(DatatypeConverter.parseBase64Binary(response.getResponseValue()));
				} else {
					bos.write(response.getResponseValue().getBytes());
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		
		try {
			bos.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		return response;
	}
	
	private static void writeMetabolitesAsPairs(String outputBase) {
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(TEMP_DIRECTORY + currentFileData.getRequestId().toString() + 
					File.separatorChar + outputBase + ".csv"), CorrelationConstants.BUFFER_SIZE);
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
		
		StringBuilder allMetabs = new StringBuilder();
		try {
			byte[] buffer = new byte[CorrelationConstants.BUFFER_SIZE];
			Integer len = null;
			while ((len = bis.read(buffer)) != -1) {
				if (len < CorrelationConstants.BUFFER_SIZE) {
					allMetabs.append(new String(Arrays.copyOfRange(buffer, 0, len)));
				} else {
					allMetabs.append(buffer);
				}
			}
			bis.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		String[] metabNames = allMetabs.toString().split(SERVER_LINE_SEPARATOR);
		String outputFileName = TEMP_DIRECTORY + currentFileData.getRequestId().toString() + File.separatorChar + 
				outputBase + "_metabs.csv";
		File outputFile = new File(outputFileName);
		
		StringBuilder columnBasedString = new StringBuilder();
		int k = 0;
		for (int j = 1; j < metabNames.length; j++) {
			for (int i = 0; i < j; i++) {
				columnBasedString.append(metabNames[i] + "," + metabNames[j] + LINE_SEPARATOR);
				k++;
				if (k % 1000 == 0) {
					try {
						FileUtils.writeStringToFile(outputFile, columnBasedString.toString(), null, true);
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
					columnBasedString.delete(0, columnBasedString.length());
				}
			}
		}
		
		if (columnBasedString.length() > 0) {
			try {
				FileUtils.writeStringToFile(outputFile, columnBasedString.toString(), null, true);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}	
	}
	
	private static void combineFiles(String outputBase, boolean fBatch, boolean fFirst) {
		BufferedReader brMetabs = null;
		BufferedReader brCoeffs = null;
		BufferedReader brPvals = null;
		BufferedReader brQvals = null;
		String ioBase = TEMP_DIRECTORY + currentFileData.getRequestId().toString() + File.separatorChar + outputBase;
		try {
			brMetabs = new BufferedReader(new FileReader(ioBase + "_metabs.csv"));
			brCoeffs = new BufferedReader(new FileReader(ioBase + "_coeffs.csv"));
			brPvals = new BufferedReader(new FileReader(ioBase + "_pvals.csv"));
			brQvals = new BufferedReader(new FileReader(ioBase + "_qvals.csv"));
			File outputFile = null;
			if (fBatch) {
				String batchFileBase = ioBase.replaceFirst(CorrelationConstants.PARTIAL_LASSO, 
						CorrelationConstants.PARTIAL_BATCH);
				outputFile = new File(batchFileBase + ".csv");
			} else {
				outputFile = new File(ioBase + ".csv");
			}
			String metabNames = null;
			if (fFirst) {
				try {
					FileUtils.writeStringToFile(outputFile, "metab1, metab2, pcor, pval, adj pval" + LINE_SEPARATOR, 
							null, false);
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			
			StringBuilder columnBasedString = new StringBuilder();
			int k = 0;
			while ((metabNames = brMetabs.readLine()) != null) {
				columnBasedString.append(metabNames + ",\"" + brCoeffs.readLine() + "\",\"" + brPvals.readLine() + 
						"\",\"" + brQvals.readLine() + "\"" + LINE_SEPARATOR);
				k++;
				if (k % 1000 == 0) {
					try {
						FileUtils.writeStringToFile(outputFile, columnBasedString.toString(), null, true);
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
					columnBasedString.delete(0, columnBasedString.length());
				}
			}
			
			if (columnBasedString.length() > 0) {
				FileUtils.writeStringToFile(outputFile, columnBasedString.toString(), null, true);
			}
			
			if (fBatch) {
				Files.delete(new File(ioBase + "_metabs.csv").toPath());
				Files.delete(new File(ioBase + "_coeffs.csv").toPath());
				Files.delete(new File(ioBase + "_pvals.csv").toPath());
				Files.delete(new File(ioBase + "_qvals.csv").toPath());
			}
			
			brMetabs.close();
			brCoeffs.close();
			brPvals.close();
			brQvals.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	private static String getPearsonLowerThreshold() {
		if (!currentFileData.getDidPearsonCoeff()) {
			return "0.0";
		}
		return String.valueOf(slider.getRangeModel().getMinValue());
	}
	
	private static String getPearsonUpperThreshold() {
		if (!currentFileData.getDidPearsonCoeff()) {
			return "1.0";
		}
		return String.valueOf(slider.getRangeModel().getMaxValue());
	}
	
	private static String getExclusionList() {
		String excludeList = "";
		if (!currentFileData.getDidPearsonCoeff()) {
			return excludeList;
		}
		Double lowerValue = slider.getRangeModel().getMinValue();
		Double upperValue = slider.getRangeModel().getMaxValue();
		for (int i = 0; i < pearsonMaxValues.length; i++) {
			if (pearsonMaxValues[i] < lowerValue || upperValue < pearsonMaxValues[i]) {
				excludeList += "|" + metabList[i];
			}
		}
		if (!excludeList.isEmpty()) {
			excludeList += "|";
		}
		return excludeList;
	}
	
	private void countSamplesAndMetabolites() {
		if (currentFileData == null) {
			return;
		}
		try {
			DataFile base;
			if (inputFileFullPathName.endsWith(".xls") || inputFileFullPathName.endsWith(".xlsx")) {
				base = new ExcelFile(new File(inputFileFullPathName));
			} else {
				base = new TextFile(new File(inputFileFullPathName));
			}
			
			int startRow = base.getStartRowIndex();
			int endRow = base.getEndRowIndex();
			int startCol = Integer.MAX_VALUE;
			int endCol = -1;
			String strValue = null;
			Double dblValue = null;
			for (int i = startRow; i <= endRow; i++) {
				int sc = base.getStartColIndex(i);
				if (sc < startCol) {
					startCol = sc;
				}
				int ec = base.getEndColIndex(i);
				if (ec > endCol) {
					endCol = ec;
				}
				if (!currentFileData.getDataParsed()) {
					for (int j = sc; j <= ec; j++) {
						strValue = base.getString(i, j);
						if (strValue != null && !strValue.isEmpty() && CorrNumUtils.isParsableAsDouble(strValue)) {
							dblValue = Double.parseDouble(strValue);
							if (dblValue <= 0) {
								currentFileData.setDataParsed(true);
								currentFileData.setDataNonPos(true);
								transformCheckBox.setEnabled(false);
								break;
							}
						}
					}
				}
			}
			currentFileData.setDataParsed(true);
			
			Integer sampleCount = null;
			Integer metaboliteCount = null;
			if ("Samples in Rows".equals(inputFileFormatComboBox.getSelectedItem().toString())) {
				sampleCount = endRow - startRow;
				metaboliteCount = endCol - startCol + 1;
			} else {
				sampleCount = endCol - startCol;
				metaboliteCount = endRow - startRow + 1;
			} 
			if (labeledCheckBox.isSelected()) {
				metaboliteCount--;
			}
			currentFileData.setNSamples(sampleCount);
			currentFileData.setNMetabolites(metaboliteCount);
			filteredCount = metaboliteCount;
			resetCountText();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	public static void resetCountText() {
		countsLabel.setText("Metabolites included: " + filteredCount + " of " +  currentFileData.getNMetabolites() + 
				"     Samples included: " + currentFileData.getNSamples() + "   ");
		if (filteredCount >= currentFileData.getNSamples()) {
			basicButton.setEnabled(false);
			lassoButton.setSelected(true);
		} else {
			basicButton.setEnabled(true);
		}
	}
	
	private static void parsePearsonMatrix(File file) {
		Integer metabCount = currentFileData.getNMetabolites();
		pearsonMaxValues = new Double[metabCount];
		metabList = new String[metabCount];
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String lineStr = null;
			Integer lineCount = 0;
			if ((lineStr = br.readLine()) == null) {
				br.close();
				return;
			}
			while ((lineStr = br.readLine()) != null) {
				if (lineCount > metabCount) {
					JOptionPane.showMessageDialog(null, "Error: Pearson output file and internal " +
							"metabolite count do not match      ");
					br.close();
					return;
				}
				Integer nameStart = lineStr.indexOf("\"");
				Integer nameEnd = lineStr.lastIndexOf("\"");
				metabList[lineCount] = lineStr.substring(nameStart + 1, nameEnd);
				String[] lineArray = lineStr.substring(nameEnd + 2).split(",");
				Double max = -1.0;
				for (int i = 0; i < lineArray.length; i++) {
					if (i == lineCount) {
						// skip the diagonal
						continue;
					}
					Double data = Double.parseDouble(lineArray[i]);
					if (data == null || data.isNaN()) {
						continue;
					}
					if (Math.abs(data) > max) {
						max = Math.abs(data);
					}
				}
				pearsonMaxValues[lineCount++] = max;
			}
			br.close();
			if (!lineCount.equals(metabCount)) {
				JOptionPane.showMessageDialog(null, "Error: Pearson output file and internal " +
						"metabolite count do not match      ");
				return;
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	public static int getFilteredCountFromValues(Double minValue, Double maxValue) {
		int count = 0;
		
		if (minValue != null && maxValue != null && pearsonMaxValues != null) {
			for (int i = 0; i < pearsonMaxValues.length; i++) {
				if (pearsonMaxValues[i] >= minValue && pearsonMaxValues[i] <= maxValue) {
					count++;
				}
			}
		}
		
		return count;
	}
	
	public Font boldFontForTitlePanel(TitledBorder border, boolean makeEvenLarger){
		//see http://bugs.sun.com/view_bug.do?bug_id=7022041 - getTitleFont() can return null - tew 8/14/12
		// A special thanks to zq (signed 'thomas') from gdufs.edu.cn and Dr. Zaho at kiz.ac.cn for spotting
		// the bug and assisting with the fix.
		Font font = border.getTitleFont();
		if (font == null) {
			font = UIManager.getDefaults().getFont("TitledBorder.font");
			if (font == null) {
				font = new Font("SansSerif", Font.BOLD, 12);
			} else {
				font = font.deriveFont(Font.BOLD);
			}
		} else {
			font = font.deriveFont(Font.BOLD);			
		}
		Font biggerFont = new Font(font.getName(), font.getStyle(), font.getSize() + (makeEvenLarger ? 3 : 1));
		return biggerFont;
	}
	
	private void setInitialVisibilityStates() {
		inputFileProgPanel.setVisible(false);
		dataActionProgPanel.setVisible(false);
		clusterProgPanel.setVisible(false);
		batchProgPanel.setVisible(false);
		compareParametersProgPanel.setVisible(false);
		compareProgPanel.setVisible(false);
		pearsonProgPanel.setVisible(false);
		partialProgPanel.setVisible(false);
	}
	
	private void setInitialEnabledStates() {
		enableInputPanel(true);
		enableInputFileFormatPanel(false);
		enableDataNormPanel(false);
		enableInternalUsePanel(true);
		enableAnalysisPanel(false);
		enableButtonPanel(true);
		prevButton.setEnabled(false);
	}
	
	private static void disableAllActionControls() {
		enableDataActionPanel(false);
		enablePearsonPanel(false);
		enablePartialPanel(false);
		enableClusterPanel(false);
		enableBatchPanel(false);
		inputFileButton.setEnabled(false);	
	}
	
	private static void enableInputPanel(boolean enable) {
		inputPanel.setEnabled(enable);
		enableInputFileWrapPanel(enable);
		enableInputFileFormatPanel(enable);
	}
	
	private static void enableInputFileWrapPanel(boolean enable) {
		inputFileWrapPanel.setEnabled(enable);
		enableInputFilePanel(enable);
		enableInputFileProgPanel(enable);
	}
	
	private static void enableInputFilePanel(boolean enable) {
		inputPanel.setEnabled(enable);
		inputFileComboBox.setEnabled(enable);
		inputFileButton.setEnabled(enable);
	}
	
	private static void enableInputFileProgPanel(boolean enable) {
		inputFileProgPanel.setEnabled(enable);
		inputFileProgBar.setEnabled(enable);
	}
	
	private static void enableInputFileFormatPanel(boolean enable) {
		inputFileFormatPanel.setEnabled(enable);
		inputFileFormatComboBox.setEnabled(enable);
		labeledCheckBox.setEnabled(enable);
	}
	
	private void enableDataNormPanel(boolean enable) {
		dataNormPanel.setEnabled(enable);
		enableDataOptionsPanel(enable);
		enableDataActionWrapPanel(enable);
	}
	
	private void enableDataOptionsPanel(boolean enable) {
		dataOptionsPanel.setEnabled(enable);
		transformCheckBox.setEnabled(enable && (currentFileData == null || !currentFileData.getDataNonPos()));
		scaleCheckBox.setEnabled(enable);
	}
	
	private void enableDataActionWrapPanel(boolean enable) {
		dataActionWrapPanel.setEnabled(enable);
		enableDataActionPanel(enable);
		enableDataActionProgPanel(enable);
	}
	
	private static void enableDataActionPanel(boolean enable) {
		dataActionPanel.setEnabled(enable);
		runNormButton.setEnabled(currentFileData != null && !"(none)".equals(currentFileData.getName()) &&
				(transformCheckBox.isSelected() || scaleCheckBox.isSelected()));
		boolean doneWithRun = currentFileData != null && (currentFileData.getDidTransform() || currentFileData.getDidScaling());
		viewNormButton.setEnabled(doneWithRun);
		saveNormButton.setEnabled(doneWithRun);
	}
	
	private void enableDataActionProgPanel(boolean enable) {
		dataActionProgPanel.setEnabled(enable);
		dataActionProgBar.setEnabled(enable);
	}
	
	private static void enableInternalUsePanel(boolean enable) {
		internalUsePanel.setEnabled(enable);
		enableClusterWrapPanel(currentFileData != null && !"(none)".equals(currentFileData.getName()) && 
				currentFileData.getDidPearsonCoeff());
		enableBatchWrapPanel(currentFileData != null && !"(none)".equals(currentFileData.getName()) && 
				currentFileData.getDidPearsonTreecut());
		enableCompareParametersWrapPanel(currentFileData != null && !"(none)".equals(currentFileData.getName()));
		enableCompareWrapPanel(currentFileData != null && !"(none)".equals(currentFileData.getName()));
	}
	
	private static void enableClusterWrapPanel(boolean enable) {
		clusterWrapPanel.setEnabled(enable);
		enableClusterPanel(enable);
		enableClusterProgPanel(enable);
	}
	
	private static void enableClusterPanel(boolean enable) {
		clusterPanel.setEnabled(enable);
		boolean inputFileSelected = currentFileData != null && !"(none)".equals(currentFileData.getName());
		runClusterAnalysisButton.setEnabled(inputFileSelected && currentFileData.getDidPearsonCoeff());
		viewClusterResultsButton.setEnabled(inputFileSelected && currentFileData.getDidPearsonCoeff());
		saveClusterResultsButton.setEnabled(inputFileSelected && currentFileData.getDidPearsonCoeff());
	}
	
	private static void enableClusterProgPanel(boolean enable) {
		clusterProgPanel.setEnabled(enable);
		clusterProgBar.setEnabled(enable);
	}
	
	private static void enableBatchWrapPanel(boolean enable) {
		batchWrapPanel.setEnabled(enable);
		enableBatchPanel(enable);
		enableBatchProgPanel(enable);
	}
	
	private static void enableBatchPanel(boolean enable) {
		batchPanel.setEnabled(enable);
		boolean inputFileSelected = currentFileData != null && !"(none)".equals(currentFileData.getName());
		runBatchAnalysisButton.setEnabled(inputFileSelected && currentFileData.getDidPearsonTreecut());
		viewBatchNetworkButton.setEnabled(inputFileSelected && currentFileData.getDidPearsonTreecut());
		viewBatchResultsButton.setEnabled(inputFileSelected && currentFileData.getDidPearsonTreecut());
		saveBatchResultsButton.setEnabled(inputFileSelected && currentFileData.getDidPearsonTreecut());
	}
	
	private static void enableBatchProgPanel(boolean enable) {
		batchProgPanel.setEnabled(enable);
		batchProgBar.setEnabled(enable);
	}
	
	private static void enableCompareParametersWrapPanel(boolean enable) {
		compareParametersWrapPanel.setEnabled(enable);
		enableCompareParametersPanel(enable);
		enableCompareParametersProgPanel(enable);
	}
	
	private static void enableCompareParametersPanel(boolean enable) {
		compareParametersPanel.setEnabled(enable);
		boolean inputFileSelected = currentFileData != null && !"(none)".equals(currentFileData.getName());
		lambdaLabel.setEnabled(inputFileSelected);
		lambdaBox.setEnabled(inputFileSelected);
		tuneButton.setEnabled(inputFileSelected);
		iterLabel.setEnabled(inputFileSelected);
		iterBox.setEnabled(inputFileSelected);
	}
	
	private static void enableCompareParametersProgPanel(boolean enable) {
		compareParametersProgPanel.setEnabled(enable);
		compareParametersProgBar.setEnabled(enable);
	}
	
	private static void enableCompareWrapPanel(boolean enable) {
		compareWrapPanel.setEnabled(enable);
		enableComparePanel(enable);
		enableCompareProgPanel(enable);
	}
	
	private static void enableComparePanel(boolean enable) {
		comparePanel.setEnabled(enable);
		boolean inputFileSelected = currentFileData != null && !"(none)".equals(currentFileData.getName());
		compareButton.setEnabled(inputFileSelected);
		viewCompareNetworkButton.setEnabled(inputFileSelected);
		viewCompareMetabsButton.setEnabled(inputFileSelected);
		viewCompareStabSelButton.setEnabled(inputFileSelected);
	}
	
	private static void enableCompareProgPanel(boolean enable) {
		compareProgPanel.setEnabled(enable);
		compareProgBar.setEnabled(enable);
	}
	
	private void enableAnalysisPanel(boolean enable) {
		enablePearsonWrapPanel(enable);
		enableSliderPanel(currentFileData != null && !"(none)".equals(currentFileData.getName()) && 
				currentFileData.getDidPearsonCoeff());
		enableMethodPanel(enable);
		enablePartialWrapPanel(enable);
	}
	
	private static void enablePearsonWrapPanel(boolean enable) {
		pearsonWrapPanel.setEnabled(enable);
		enablePearsonPanel(enable);
		enablePearsonProgPanel(enable);
	}
	
	private static void enablePearsonPanel(boolean enable) {
		pearsonPanel.setEnabled(enable);
		boolean inputFileSelected = currentFileData != null && !"(none)".equals(currentFileData.getName());
		runPearsonButton.setEnabled(inputFileSelected);
		createPearsonHeatmapButton.setEnabled(inputFileSelected && currentFileData.getDidPearsonCoeff());
		viewPearsonDistributionButton.setEnabled(inputFileSelected && currentFileData.getDidPearsonCoeff());
		viewPearsonResultsButton.setEnabled(inputFileSelected && currentFileData.getDidPearsonCoeff());
		savePearsonButton.setEnabled(inputFileSelected && currentFileData.getDidPearsonCoeff());
	}
	
	private static void enablePearsonProgPanel(boolean enable) {
		pearsonProgPanel.setEnabled(enable);
		pearsonProgBar.setEnabled(enable);
	}
	
	private static void enableSliderPanel(boolean enable) {
		sliderPanel.setEnabled(enable);
		slider.setEnabled(enable);
		enableLabelPanel(enable);
		if (enable) {
			Double sliderMinVal = slider.getRangeModel().getMinValue();
			Double sliderMaxVal = slider.getRangeModel().getMaxValue();
			filteredCount = getFilteredCountFromValues(sliderMinVal, sliderMaxVal);
			currentFileData.setNMetabolites(pearsonMaxValues.length);
			resetCountText();
		}
	}
	
	private static void enableLabelPanel(boolean enable) {
		titlePanel.setEnabled(enable);
		limits.getBoxPair()[PearsonTextFieldPair.LEFT_FIELD].setEnabled(enable);
		titleLabel.setEnabled(enable);
		limits.getBoxPair()[PearsonTextFieldPair.RIGHT_FIELD].setEnabled(enable);
	}
	
	private void enableMethodPanel(boolean enable) {
		methodPanel.setEnabled(enable);
		enableMethodCountsPanel(enable);
		enableMethodRadioPanel(enable);
	}
	
	private void enableMethodCountsPanel(boolean enable) {
		methodCountsPanel.setEnabled(enable);
		countsLabel.setEnabled(enable);
	}
	
	private void enableMethodRadioPanel(boolean enable) {
		methodRadioPanel.setEnabled(enable);
		basicButton.setEnabled(enable);
		lassoButton.setEnabled(enable);
	}
	
	private void enablePartialWrapPanel(boolean enable) {
		partialWrapPanel.setEnabled(enable);
		enablePartialPanel(enable);
		enablePartialProgPanel(enable);
	}
	
	private static void enablePartialPanel(boolean enable) {
		partialPanel.setEnabled(enable);
		runPartialButton.setEnabled(currentFileData != null && !"(none)".equals(currentFileData.getName()));
		viewPartialNetworkButton.setEnabled(currentFileData != null && currentFileData.getDidPartial());
		viewPartialResultsButton.setEnabled(currentFileData != null && currentFileData.getDidPartial());
		savePartialButton.setEnabled(currentFileData != null && currentFileData.getDidPartial());
	}
	
	private void enablePartialProgPanel(boolean enable) {
		partialProgPanel.setEnabled(enable);
		partialProgBar.setEnabled(enable);
	}
	
	private void enableButtonPanel(boolean enable) {
		buttonPanel.setEnabled(enable);
		closeButton.setEnabled(enable);
	}
	
	public String getInputFileFullPathName() {
		return inputFileFullPathName;
	}
	
	public String getTempDirectory() {
		return TEMP_DIRECTORY;
	}
	
	public static Integer getFilteredCount() {
		return filteredCount;
	}
	
	public static void setFilteredCount(Integer count) {
		filteredCount = count;
	}

	public static Double [] getPearsonMaxValues() {
		return pearsonMaxValues;
	}
	
	public static PearsonSlider getSlider() {
		return slider;
	}
	
	public static FileData getCurrentFileData() {
		return currentFileData;
	}
	
	public static PearsonTextFieldPair getLimits() {
		return limits;
	}
	
	public static void setSaveCoeffFile(boolean saveFile) {
		saveCoeffFile = saveFile;
	}
	
	public static void setSaveHistogramFile(boolean saveFile) {
		saveHistogramFile = saveFile;
	}
	
	public static void setSaveStaticHeatmapFile(boolean saveFile) {
		saveStaticHeatmapFile = saveFile;
	}
	
	public static void setSaveInteractiveHeatmapFiles(boolean saveFiles) {
		saveInteractiveHeatmapFiles = saveFiles;
	}
}
