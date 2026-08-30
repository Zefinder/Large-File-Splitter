package com.frame;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import com.logic.SplitSize;
import com.logic.Splitter;

public class FileSplitterPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7012553769344974988L;

	private static final int SPLIT_SIZE_WARNING_THRESHOLD_GB = 2;
	private static final int FILE_NUMBER_WARNING_THRESHOLD = 1000;

	private static final String INPUT_FILE_JLABEL_TEXT = "Input file:";
	private static final String OUTPUT_FOLDER_JLABEL_TEXT = "Destination folder:";
	private static final String SPLIT_OPTIONS_JLABEL_TEXT = "Split options:";

	private static final String SELECTION_BUTTON_TEXT = "...";
	private static final String CONFIRM_BUTTON_TEXT = "Split!";

	private static final String INPUT_FILE_EXIST_ERROR_TITLE = "Input file error";
	private static final String OUTPUT_FOLDER_EXIST_ERROR_TITLE = "Output folder error";
	private static final String SIZE_TEXT_ERROR_TITLE = "Split size format error";
	private static final String SPLIT_SIZE_ZERO_ERROR_TITLE = "Split size error";
	private static final String SPLIT_SIZE_NEGATIVE_ERROR_TITLE = "Split size error";
	private static final String SPLIT_SIZE_BIG_WARNING_TITLE = "Split size warning";
	private static final String SPLIT_SIZE_SMALL_WARNING_TITLE = "Split size warning";

	private static final String INPUT_FILE_EXIST_ERROR = "Input file does not exist, error!";
	private static final String OUTPUT_FOLDER_EXIST_ERROR = "Output folder does not exist, error!";
	private static final String SIZE_TEXT_ERROR = "Split size is not an integer, error!";
	private static final String SPLIT_SIZE_ZERO_ERROR = "Split size is set to 0, error!";
	private static final String SPLIT_SIZE_NEGATIVE_ERROR = "Split size is negative, error!";
	private static final String SPLIT_SIZE_BIG_WARNING = "Split size is set to create files over "
			+ SPLIT_SIZE_WARNING_THRESHOLD_GB + " GB, do you want to continue?";
	private static final String SPLIT_SIZE_SMALL_WARNING = "Split size is set to create at least over "
			+ FILE_NUMBER_WARNING_THRESHOLD + " files, do you want to continue?";

	private static final String SPLIT_ERROR_TITLE = "Split error";
	private static final String SPLIT_ERROR = "An error occured during splitting...\n";

	private static final String PROGRESS_BAR_TEXT_FORMAT = "%s (%d/%d)";
	private static final String PROGRESS_BAR_COMPLETED_TEXT = " | Complete!";

	private static final int OUTBOUND_INSET = 20;
	private static final int INTERPART_INSET = 5;
	private static final int COMPONENTS_INSET = 5;

	private JTextField inputFileField;
	private JTextField destinationFolderField;
	private JTextField splitSizeField;

	private JButton inputFileButton;
	private JButton destinationFolderButton;
	private JButton confirmButton;

	private JComboBox<SplitSize> splitSizeComboBox;

	private JProgressBar progressBar;

	public FileSplitterPanel() {
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;

		c.insets = new Insets(OUTBOUND_INSET, COMPONENTS_INSET, INTERPART_INSET, COMPONENTS_INSET);
		c.gridx = 0;
		c.gridy = 0;
		createInputTextField(c);

		c.insets = new Insets(INTERPART_INSET, COMPONENTS_INSET, INTERPART_INSET, COMPONENTS_INSET);
		c.gridx = 0;
		c.gridy = 1;
		createDestinationTextField(c);

		c.insets = new Insets(INTERPART_INSET, COMPONENTS_INSET, OUTBOUND_INSET, COMPONENTS_INSET);
		c.gridx = 0;
		c.gridy = 2;
		createSplitOption(c);

		c.insets = new Insets(INTERPART_INSET, 0, OUTBOUND_INSET, 0);
		c.gridx = 0;
		c.gridy = 3;
		createConfirmButton(c);

		c.insets = new Insets(0, COMPONENTS_INSET, 0, COMPONENTS_INSET);
		c.gridx = 0;
		c.gridy = 4;
		createProgressBar(c);
	}

	private void createInputTextField(GridBagConstraints c) {
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 0;
		c.gridy = 0;
		this.add(new JLabel(INPUT_FILE_JLABEL_TEXT), c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.gridx++;
		inputFileField = PanelUtils.createTextField();
		this.add(inputFileField, c);

		c.fill = GridBagConstraints.NONE;
		c.gridx++;
		inputFileButton = new JButton(SELECTION_BUTTON_TEXT);
		inputFileButton.addActionListener(_ -> {
			String selectedFile = PanelUtils.invokeFileChooser();
			if (selectedFile != null) {
				inputFileField.setText(selectedFile);
			}
		});
		this.add(inputFileButton, c);
	}

	private void createDestinationTextField(GridBagConstraints c) {
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		this.add(new JLabel(OUTPUT_FOLDER_JLABEL_TEXT), c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.gridx++;
		destinationFolderField = PanelUtils.createTextField();
		this.add(destinationFolderField, c);

		c.fill = GridBagConstraints.NONE;
		c.gridx++;
		destinationFolderButton = new JButton(SELECTION_BUTTON_TEXT);
		destinationFolderButton.addActionListener(_ -> {
			String selectedFile = PanelUtils.invokeFolderChooser();
			if (selectedFile != null) {
				destinationFolderField.setText(selectedFile);
			}
		});
		this.add(destinationFolderButton, c);
	}

	private void createSplitOption(GridBagConstraints c) {
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		this.add(new JLabel(SPLIT_OPTIONS_JLABEL_TEXT), c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.gridx++;
		splitSizeField = PanelUtils.createTextField();
		this.add(splitSizeField, c);

		c.fill = GridBagConstraints.NONE;
		c.gridx++;
		splitSizeComboBox = new JComboBox<SplitSize>(SplitSize.values());
		this.add(splitSizeComboBox, c);
	}

	private void createConfirmButton(GridBagConstraints c) {
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 3;
		confirmButton = new JButton(CONFIRM_BUTTON_TEXT);

		confirmButton.addActionListener(_ -> {
			// Check input file
			File inputFile = new File(inputFileField.getText());
			if (!inputFile.exists()) {
				JOptionPane.showMessageDialog(null, INPUT_FILE_EXIST_ERROR, INPUT_FILE_EXIST_ERROR_TITLE,
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			// Check output file
			File destinationFile = new File(destinationFolderField.getText());
			if (!destinationFile.exists()) {
				JOptionPane.showMessageDialog(null, OUTPUT_FOLDER_EXIST_ERROR, OUTPUT_FOLDER_EXIST_ERROR_TITLE,
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			// Transform split size to long and check if over 2 GB
			int splitValue;
			try {
				splitValue = Integer.valueOf(splitSizeField.getText());
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, SIZE_TEXT_ERROR, SIZE_TEXT_ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (splitValue == 0) {
				JOptionPane.showMessageDialog(null, SPLIT_SIZE_ZERO_ERROR, SPLIT_SIZE_ZERO_ERROR_TITLE,
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (splitValue < 0) {
				JOptionPane.showMessageDialog(null, SPLIT_SIZE_NEGATIVE_ERROR, SPLIT_SIZE_NEGATIVE_ERROR_TITLE,
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			long splitSize = splitValue * ((SplitSize) splitSizeComboBox.getSelectedItem()).getMultiplier();
			if (splitSize > SPLIT_SIZE_WARNING_THRESHOLD_GB * SplitSize.GB.getMultiplier()) {
				if (!PanelUtils.invokeChoiceDialog(SPLIT_SIZE_BIG_WARNING, SPLIT_SIZE_BIG_WARNING_TITLE)) {
					return;
				}
			}

			long fileSize = inputFile.length();
			int finalFileNumber = (int) Math.ceil((double) fileSize / splitSize);
			if (finalFileNumber > FILE_NUMBER_WARNING_THRESHOLD) {
				if (!PanelUtils.invokeChoiceDialog(SPLIT_SIZE_SMALL_WARNING, SPLIT_SIZE_SMALL_WARNING_TITLE)) {
					return;
				}
			}

			Splitter splitter = new Splitter(destinationFile, inputFile, splitSize);
			progressBar.setValue(0);
			progressBar.setMaximum(splitter.getNumberFilesToCreate());
			AtomicBoolean inSplit = new AtomicBoolean(true);

			// Set reconstruction in a thread so we can display processing
			Thread splittingThread = new Thread(() -> {
				try {
					splitter.split();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, SPLIT_ERROR, SPLIT_ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
				}

				inSplit.set(false);
			});

			// Progress bar thread
			Thread progressBarThread = new Thread(() -> {
				confirmButton.setEnabled(false);
				inputFileField.setEnabled(false);
				inputFileButton.setEnabled(false);
				destinationFolderField.setEnabled(false);
				destinationFolderButton.setEnabled(false);
				splitSizeField.setEnabled(false);
				splitSizeComboBox.setEnabled(false);
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				while (inSplit.get()) {
					updateProgressBar(splitter, false);
				}

				// Update last time just to show that everything is processed
				updateProgressBar(splitter, true);
				confirmButton.setEnabled(true);
				inputFileField.setEnabled(true);
				inputFileButton.setEnabled(true);
				destinationFolderField.setEnabled(true);
				destinationFolderButton.setEnabled(true);
				splitSizeField.setEnabled(true);
				splitSizeComboBox.setEnabled(false);
				setCursor(null);
			});

			progressBarThread.start();
			splittingThread.start();
		});
		this.add(confirmButton, c);
	}

	private void createProgressBar(GridBagConstraints c) {
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.SOUTH;
		progressBar = new JProgressBar();
		progressBar.setString("");
		progressBar.setMinimum(0);
		progressBar.setIndeterminate(false);
		progressBar.setStringPainted(true);
		this.add(progressBar, c);
	}

	private void updateProgressBar(Splitter splitter, boolean completed) {
		int processedFileNumber = splitter.getCreatedFilesNumber();
		String stringValue = PROGRESS_BAR_TEXT_FORMAT.formatted(splitter.getCurrentCreatedFileName(),
				processedFileNumber, progressBar.getMaximum());

		if (completed) {
			stringValue += PROGRESS_BAR_COMPLETED_TEXT;
		}

		progressBar.setValue(processedFileNumber);
		progressBar.setString(stringValue);
	}

}
