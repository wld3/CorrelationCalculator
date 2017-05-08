package edu.umich.wld;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import org.ncibi.metR.ws.common.CorrelationConstants;

@SuppressWarnings("serial")
public class SaveDialog extends JDialog {
	
	private static final ImageIcon icon = ImageUtils.createImageIcon("/question_mark.png", "Help");
	
	private static JPanel coeffPanel;
	private static JButton generateCoeffButton;
	private static JCheckBox saveCoeffCheckBox;
	
	private static JPanel coeffProgPanel;
	private JProgressBar coeffProgBar;
	
	private JPanel coeffWrapperPanel;
	private TitledBorder coeffWrapperBorder;
	
	private static JPanel staticHeatmapPanel;
	private static JButton generateStaticHeatmapButton;
	private static JCheckBox saveStaticHeatmapCheckBox;
	
	private static JPanel staticHeatmapProgPanel;
	private JProgressBar staticHeatmapProgBar;
	
	private JPanel staticHeatmapWrapperPanel;
	private TitledBorder staticHeatmapWrapperBorder;
	
	private static JPanel histogramPanel;
	private static JButton generateHistogramButton;
	private static JCheckBox saveHistogramCheckBox;
	
	private static JPanel histogramProgPanel;
	private JProgressBar histogramProgBar;
	
	private JPanel histogramWrapperPanel;
	private TitledBorder histogramWrapperBorder;
	
	private static JPanel interactiveHeatmapPanel;
	private static JButton generateInteractiveHeatmapButton;
	private static JCheckBox saveInteractiveHeatmapCheckBox;
	private static JButton interactiveHeatmapInfoButton;
	
	private static JPanel interactiveHeatmapProgPanel;
	private JProgressBar interactiveHeatmapProgBar;
	
	private JPanel interactiveHeatmapWrapperPanel;
	private TitledBorder interactiveHeatmapWrapperBorder;
	
	private JPanel buttonPanel;
	private JButton okButton;
	private JButton cancelButton;
	
	private JPanel wrapperPanel;
	
	private String outputFileFirstPiece;
	private String outputFileSecondPiece;
	private boolean cancelled = false;
	
	public SaveDialog(String title, String outputFileFirstPiece, String outputFileSecondPiece) {
		this.outputFileFirstPiece = outputFileFirstPiece;
		this.outputFileSecondPiece = outputFileSecondPiece;
		JDialog dialog = new JDialog();
		dialog.setTitle(title);
		createControls(dialog);
		setInitialStates();
		dialog.add(Box.createHorizontalStrut(8), BorderLayout.WEST);
		dialog.add(wrapperPanel, BorderLayout.CENTER);
		dialog.add(Box.createHorizontalStrut(8), BorderLayout.EAST);
		dialog.setModal(true);
		dialog.setSize(450, 390);
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}
	
	private void createControls(final JDialog dialog) {
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
	
		wrapperPanel = new JPanel();
		wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.Y_AXIS));
		
		coeffPanel = new JPanel();
		coeffPanel.setLayout(new BoxLayout(coeffPanel, BoxLayout.X_AXIS));
		generateCoeffButton = new JButton("Generate File");
		generateCoeffButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				AnalysisDialog.doPearsonAnalysisInWorkerThread(null, CorrelationConstants.PEARSON_COEFF);
			}
		});
		saveCoeffCheckBox = new JCheckBox("Save File");
		saveCoeffCheckBox.setSelected(true);
		saveCoeffCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
			}
		});
		coeffPanel.add(Box.createHorizontalGlue());
		coeffPanel.add(saveCoeffCheckBox);
		coeffPanel.add(Box.createHorizontalGlue());
		coeffPanel.add(generateCoeffButton);
		coeffPanel.add(Box.createHorizontalGlue());
		
		coeffProgPanel = new JPanel();
		coeffProgPanel.setLayout(new BoxLayout(coeffProgPanel, BoxLayout.X_AXIS));
		coeffProgPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		coeffProgBar = new JProgressBar(0, 500);
		coeffProgBar.setIndeterminate(true);
		coeffProgPanel.add(coeffProgBar);
		
		coeffWrapperPanel = new JPanel();
		coeffWrapperPanel.setLayout(new BoxLayout(coeffWrapperPanel, BoxLayout.Y_AXIS));
		coeffWrapperBorder = BorderFactory.createTitledBorder("Pearson's Correlation Coefficients (.csv)");
		coeffWrapperBorder.setTitleFont(boldFontForTitlePanel(coeffWrapperBorder, false));
		coeffWrapperPanel.setBorder(coeffWrapperBorder);
		coeffWrapperPanel.add(coeffPanel);
		coeffWrapperPanel.add(coeffProgPanel);
		
		histogramPanel = new JPanel();
		histogramPanel.setLayout(new BoxLayout(histogramPanel, BoxLayout.X_AXIS));
		generateHistogramButton = new JButton("Generate File");
		generateHistogramButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				AnalysisDialog.doPearsonAnalysisInWorkerThread(null, CorrelationConstants.PEARSON_DIST);
			}
		});
		saveHistogramCheckBox = new JCheckBox("Save File");
		saveHistogramCheckBox.setSelected(true);
		saveHistogramCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
			}
		});
		histogramPanel.add(Box.createHorizontalGlue());
		histogramPanel.add(saveHistogramCheckBox);
		histogramPanel.add(Box.createHorizontalGlue());
		histogramPanel.add(generateHistogramButton);
		histogramPanel.add(Box.createHorizontalGlue());
		
		histogramProgPanel = new JPanel();
		histogramProgPanel.setLayout(new BoxLayout(histogramProgPanel, BoxLayout.X_AXIS));
		histogramProgPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		histogramProgBar = new JProgressBar(0, 500);
		histogramProgBar.setIndeterminate(true);
		histogramProgPanel.add(histogramProgBar);
		
		histogramWrapperPanel = new JPanel();
		histogramWrapperPanel.setLayout(new BoxLayout(histogramWrapperPanel, BoxLayout.Y_AXIS));
		histogramWrapperBorder = BorderFactory.createTitledBorder("Histogram (.pdf)");
		histogramWrapperBorder.setTitleFont(boldFontForTitlePanel(histogramWrapperBorder, false));
		histogramWrapperPanel.setBorder(histogramWrapperBorder);
		histogramWrapperPanel.add(histogramPanel);
		histogramWrapperPanel.add(histogramProgPanel);
		
		staticHeatmapPanel = new JPanel();
		staticHeatmapPanel.setLayout(new BoxLayout(staticHeatmapPanel, BoxLayout.X_AXIS));
		generateStaticHeatmapButton = new JButton("Generate File");
		generateStaticHeatmapButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				AnalysisDialog.doPearsonAnalysisInWorkerThread(null, CorrelationConstants.PEARSON_HEATMAP);
			}
		});
		saveStaticHeatmapCheckBox = new JCheckBox("Save File");
		saveStaticHeatmapCheckBox.setSelected(true);
		saveStaticHeatmapCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
			}
		});
		staticHeatmapPanel.add(Box.createHorizontalGlue());
		staticHeatmapPanel.add(saveStaticHeatmapCheckBox);
		staticHeatmapPanel.add(Box.createHorizontalGlue());
		staticHeatmapPanel.add(generateStaticHeatmapButton);
		staticHeatmapPanel.add(Box.createHorizontalGlue());
		
		staticHeatmapProgPanel = new JPanel();
		staticHeatmapProgPanel.setLayout(new BoxLayout(staticHeatmapProgPanel, BoxLayout.X_AXIS));
		staticHeatmapProgPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		staticHeatmapProgBar = new JProgressBar(0, 500);
		staticHeatmapProgBar.setIndeterminate(true);
		staticHeatmapProgPanel.add(staticHeatmapProgBar);
		
		staticHeatmapWrapperPanel = new JPanel();
		staticHeatmapWrapperPanel.setLayout(new BoxLayout(staticHeatmapWrapperPanel, BoxLayout.Y_AXIS));
		staticHeatmapWrapperBorder = BorderFactory.createTitledBorder("Static Heatmap (.pdf)");
		staticHeatmapWrapperBorder.setTitleFont(boldFontForTitlePanel(staticHeatmapWrapperBorder, false));
		staticHeatmapWrapperPanel.setBorder(staticHeatmapWrapperBorder);
		staticHeatmapWrapperPanel.add(staticHeatmapPanel);
		staticHeatmapWrapperPanel.add(staticHeatmapProgPanel);
		
		interactiveHeatmapPanel = new JPanel();
		interactiveHeatmapPanel.setLayout(new BoxLayout(interactiveHeatmapPanel, BoxLayout.X_AXIS));
		saveInteractiveHeatmapCheckBox = new JCheckBox("Save Files");
		saveInteractiveHeatmapCheckBox.setSelected(true);
		saveInteractiveHeatmapCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
			}
		});
		generateInteractiveHeatmapButton = new JButton("Generate Files");
		generateInteractiveHeatmapButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				AnalysisDialog.doPearsonAnalysisInWorkerThread(null, CorrelationConstants.PEARSON_CLUSTER);
			}
		});
		interactiveHeatmapInfoButton = new JButton();
		interactiveHeatmapInfoButton.setIcon(icon);
		interactiveHeatmapInfoButton.setFocusable(false);
		interactiveHeatmapInfoButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				String message = "These files can be loaded into Java TreeView, a free heatmap browser and viewer program.";
				JOptionPane.showMessageDialog(null, message, "Interactive Heatmap File Information", 
						JOptionPane.INFORMATION_MESSAGE, icon);
			}
		});
		interactiveHeatmapPanel.add(Box.createHorizontalGlue());
		interactiveHeatmapPanel.add(saveInteractiveHeatmapCheckBox);
		interactiveHeatmapPanel.add(Box.createHorizontalGlue());
		interactiveHeatmapPanel.add(generateInteractiveHeatmapButton);
		interactiveHeatmapPanel.add(Box.createHorizontalGlue());
		interactiveHeatmapPanel.add(interactiveHeatmapInfoButton);
		interactiveHeatmapPanel.add(Box.createHorizontalGlue());
		
		interactiveHeatmapProgPanel = new JPanel();
		interactiveHeatmapProgPanel.setLayout(new BoxLayout(interactiveHeatmapProgPanel, BoxLayout.X_AXIS));
		interactiveHeatmapProgPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		interactiveHeatmapProgBar = new JProgressBar(0, 500);
		interactiveHeatmapProgBar.setIndeterminate(true);
		interactiveHeatmapProgPanel.add(interactiveHeatmapProgBar);
		
		interactiveHeatmapWrapperPanel = new JPanel();
		interactiveHeatmapWrapperPanel.setLayout(new BoxLayout(interactiveHeatmapWrapperPanel, BoxLayout.Y_AXIS));
		interactiveHeatmapWrapperBorder = BorderFactory.createTitledBorder("Interactive Heatmap (.cdt, .atr, .gtr)");
		interactiveHeatmapWrapperBorder.setTitleFont(boldFontForTitlePanel(interactiveHeatmapWrapperBorder, false));
		interactiveHeatmapWrapperPanel.setBorder(interactiveHeatmapWrapperBorder);
		interactiveHeatmapWrapperPanel.add(interactiveHeatmapPanel);
		interactiveHeatmapWrapperPanel.add(interactiveHeatmapProgPanel);
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				AnalysisDialog.setSaveCoeffFile(saveCoeffCheckBox.isSelected());
				AnalysisDialog.setSaveStaticHeatmapFile(saveStaticHeatmapCheckBox.isSelected());
				AnalysisDialog.setSaveInteractiveHeatmapFiles(saveInteractiveHeatmapCheckBox.isSelected());
				dialog.setVisible(false);
			}
		});
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				cancelled = true;
				dialog.setVisible(false);
			}
		});
		
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(okButton);
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(cancelButton);
		buttonPanel.add(Box.createHorizontalGlue());
		
		wrapperPanel.add(Box.createVerticalStrut(10));
		wrapperPanel.add(coeffWrapperPanel);
		wrapperPanel.add(Box.createVerticalStrut(10));
		wrapperPanel.add(histogramWrapperPanel);
		wrapperPanel.add(Box.createVerticalStrut(10));
		wrapperPanel.add(staticHeatmapWrapperPanel);
		wrapperPanel.add(Box.createVerticalStrut(10));
		wrapperPanel.add(interactiveHeatmapWrapperPanel);
		wrapperPanel.add(Box.createVerticalStrut(10));
		wrapperPanel.add(buttonPanel);
		wrapperPanel.add(Box.createVerticalStrut(10));
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
	
	private void setInitialStates() {
		coeffProgPanel.setVisible(false);
		staticHeatmapProgPanel.setVisible(false);
		histogramProgPanel.setVisible(false);
		interactiveHeatmapProgPanel.setVisible(false);
		
		File coeffFile = new File(outputFileFirstPiece + CorrelationConstants.PEARSON_COEFF + outputFileSecondPiece + ".csv");
		boolean haveCoeffFile = coeffFile.exists();
		saveCoeffCheckBox.setEnabled(haveCoeffFile);
		saveCoeffCheckBox.setSelected(haveCoeffFile);
		generateCoeffButton.setEnabled(!haveCoeffFile);
		
		File histogramFile = new File(outputFileFirstPiece + CorrelationConstants.PEARSON_DIST + outputFileSecondPiece + ".pdf");
		boolean haveHistogramFile = histogramFile.exists();
		saveHistogramCheckBox.setEnabled(haveHistogramFile);
		saveHistogramCheckBox.setSelected(haveHistogramFile);
		generateHistogramButton.setEnabled(!haveHistogramFile);
		
		File staticHeatmapFile = new File(outputFileFirstPiece + CorrelationConstants.PEARSON_HEATMAP + outputFileSecondPiece + ".pdf");
		boolean haveStaticHeatmapFile = staticHeatmapFile.exists();
		saveStaticHeatmapCheckBox.setEnabled(haveStaticHeatmapFile);
		saveStaticHeatmapCheckBox.setSelected(haveStaticHeatmapFile);
		generateStaticHeatmapButton.setEnabled(!haveStaticHeatmapFile);
		
		String tempBase = outputFileFirstPiece + CorrelationConstants.PEARSON_CLUSTER + outputFileSecondPiece;
		File interactiveHeatmapCdtFile = new File(tempBase + ".cdt");
		File interactiveHeatmapAtrFile = new File(tempBase + ".atr");
		File interactiveHeatmapGtrFile = new File(tempBase + ".gtr");
		boolean haveInteractiveHeatmapFiles = interactiveHeatmapCdtFile.exists() && interactiveHeatmapAtrFile.exists() &&
				interactiveHeatmapGtrFile.exists();
		saveInteractiveHeatmapCheckBox.setEnabled(haveInteractiveHeatmapFiles);
		saveInteractiveHeatmapCheckBox.setSelected(haveInteractiveHeatmapFiles);
		generateInteractiveHeatmapButton.setEnabled(!haveInteractiveHeatmapFiles);
	}
	
	public static JPanel getCoeffPanel() {
		return coeffPanel;
	}
	
	public static JPanel getCoeffProgPanel() {
		return coeffProgPanel;
	}
	
	public static JPanel getHistogramPanel() {
		return histogramPanel;
	}
	
	public static JPanel getHistogramProgPanel() {
		return histogramProgPanel;
	}
	
	public static JPanel getStaticHeatmapPanel() {
		return staticHeatmapPanel;
	}
	
	public static JPanel getStaticHeatmapProgPanel() {
		return staticHeatmapProgPanel;
	}
	
	public static JPanel getInteractiveHeatmapPanel() {
		return interactiveHeatmapPanel;
	}
	
	public static JPanel getInteractiveHeatmapProgPanel() {
		return interactiveHeatmapProgPanel;
	}
	
	public static JCheckBox getSaveCoeffCheckBox() {
		return saveCoeffCheckBox;
	}
	
	public static JButton getGenerateCoeffButton() {
		return generateCoeffButton;
	}
	
	public static JCheckBox getSaveHistogramCheckBox() {
		return saveHistogramCheckBox;
	}
	
	public static JButton getGenerateHistogramButton() {
		return generateHistogramButton;
	}
	
	public static JCheckBox getSaveStaticHeatmapCheckBox() {
		return saveStaticHeatmapCheckBox;
	}
	
	public static JButton getGenerateStaticHeatmapButton() {
		return generateStaticHeatmapButton;
	}
	
	public static JCheckBox getSaveInteractiveHeatmapCheckBox() {
		return saveInteractiveHeatmapCheckBox;
	}
	
	public static JButton getGenerateInteractiveHeatmapButton() {
		return generateInteractiveHeatmapButton;
	}
	
	public boolean getCancelled() {
		return cancelled;
	}
}
