package com.frame;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import com.logic.Reconstructor;

public class FileReconstructorPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2483883751717024357L;

	private static final String FIRST_PART_FILE_EXTENSION = ".001";
	private static final String FIRST_PART_FILE_DESCTIPTION = String.format("First part file (%s)",
			FIRST_PART_FILE_EXTENSION);

	private static final String INPUT_FILE_JLABEL_TEXT = "Input part file:";
	private static final String OUTPUT_FOLDER_JLABEL_TEXT = "Destination folder:";

	private static final String SELECTION_BUTTON_TEXT = "...";
	private static final String CONFIRM_BUTTON_TEXT = "Reconstruct!";

	private static final String INPUT_FILE_EXIST_ERROR_TITLE = "Input file error";
	private static final String INPUT_FILE_EXTENSION_ERROR_TITLE = "Input file error";
	private static final String OUTPUT_FOLDER_EXIST_ERROR_TITLE = "Output folder error";

	private static final String INPUT_FILE_EXIST_ERROR = "Input file does not exist, error!";
	private static final String INPUT_FILE_EXTENSION_ERROR = "Input file is not the first part file, error!";
	private static final String OUTPUT_FOLDER_EXIST_ERROR = "Output folder does not exist, error!";

	private static final String RECONSTRUCT_ERROR_TITLE = "Reconstruct error";
	private static final String RECONSTRUCT_ERROR = "An error occured during reconstruction...\n";

	private static final String PROGRESS_BAR_TEXT_FORMAT = "%s (%d/%d)";
	private static final String PROGRESS_BAR_COMPLETED_TEXT = " | Complete!";

	private static final int OUTBOUND_INSET = 20;
	private static final int INTERPART_INSET = 5;
	private static final int COMPONENTS_INSET = 5;

	private JTextField inputFileField;
	private JTextField destinationFolderField;

	private JButton inputFileButton;
	private JButton destinationFolderButton;
	private JButton confirmButton;

	private JProgressBar progressBar;

	public FileReconstructorPanel() {
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;

		c.insets = new Insets(OUTBOUND_INSET, COMPONENTS_INSET, INTERPART_INSET, COMPONENTS_INSET);
		c.gridx = 0;
		c.gridy = 0;
		createInputTextField(c);

		c.insets = new Insets(INTERPART_INSET, COMPONENTS_INSET, OUTBOUND_INSET, COMPONENTS_INSET);
		c.gridx = 0;
		c.gridy = 1;
		createDestinationTextField(c);

		c.insets = new Insets(INTERPART_INSET, 0, OUTBOUND_INSET, 0);
		c.gridx = 0;
		c.gridy = 2;
		createConfirmButton(c);

		c.insets = new Insets(0, COMPONENTS_INSET, 0, COMPONENTS_INSET);
		c.gridx = 0;
		c.gridy = 3;
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
		inputFileButton.addActionListener(e -> {
			String selectedFile = PanelUtils.invokeFileChooser(new FileFilter() {

				@Override
				public String getDescription() {
					return FIRST_PART_FILE_DESCTIPTION;
				}

				@Override
				public boolean accept(File f) {
					return f.isDirectory() || f.getName().endsWith(FIRST_PART_FILE_EXTENSION);
				}
			});
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
		destinationFolderButton.addActionListener(e -> {
			String selectedFile = PanelUtils.invokeFolderChooser();
			if (selectedFile != null) {
				destinationFolderField.setText(selectedFile);
			}
		});
		this.add(destinationFolderButton, c);
	}

	private void createConfirmButton(GridBagConstraints c) {
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 3;
		confirmButton = new JButton(CONFIRM_BUTTON_TEXT);

		confirmButton.addActionListener(e -> {
			// Check input file
			File inputFile = new File(inputFileField.getText());
			if (!inputFile.exists()) {
				JOptionPane.showMessageDialog(null, INPUT_FILE_EXIST_ERROR, INPUT_FILE_EXIST_ERROR_TITLE,
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (!inputFile.getName().endsWith(FIRST_PART_FILE_EXTENSION)) {
				JOptionPane.showMessageDialog(null, INPUT_FILE_EXTENSION_ERROR, INPUT_FILE_EXTENSION_ERROR_TITLE,
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

			Reconstructor reconstructor = new Reconstructor(destinationFile, inputFile);
			progressBar.setValue(0);
			progressBar.setMaximum(reconstructor.getFilesToProcessNumber());
			AtomicBoolean inReconstruction = new AtomicBoolean(true);

			// Set reconstruction in a thread so we can display processing
			Thread reconstructorThread = new Thread(() -> {
				try {
					reconstructor.reconstruct();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, RECONSTRUCT_ERROR, RECONSTRUCT_ERROR_TITLE,
							JOptionPane.ERROR_MESSAGE);
				}

				inReconstruction.set(false);
			});

			// Progress bar thread
			Thread progressBarThread = new Thread(() -> {
				confirmButton.setEnabled(false);
				inputFileField.setEnabled(false);
				inputFileButton.setEnabled(false);
				destinationFolderField.setEnabled(false);
				destinationFolderButton.setEnabled(false);
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				while (inReconstruction.get()) {
					updateProgressBar(reconstructor, false);
				}

				// Update last time just to show that everything is processed
				updateProgressBar(reconstructor, true);
				confirmButton.setEnabled(true);
				inputFileField.setEnabled(true);
				inputFileButton.setEnabled(true);
				destinationFolderField.setEnabled(true);
				destinationFolderButton.setEnabled(true);
				setCursor(null);
			});

			progressBarThread.start();
			reconstructorThread.start();
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

	private void updateProgressBar(Reconstructor reconstructor, boolean completed) {
		int processedFileNumber = reconstructor.getProcessedFileNumber();
		String stringValue = String.format(PROGRESS_BAR_TEXT_FORMAT, reconstructor.getCurrentFileName(),
				processedFileNumber, progressBar.getMaximum());

		if (completed) {
			stringValue += PROGRESS_BAR_COMPLETED_TEXT;
		}

		progressBar.setValue(processedFileNumber);
		progressBar.setString(stringValue);
	}
}
