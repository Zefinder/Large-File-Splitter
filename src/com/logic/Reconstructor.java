package com.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

public class Reconstructor {

	private static final String PATTERN_SANITIZER = "[-.\\+*?\\[^\\]$(){}=!<>|:\\\\]";
	private static final String PATTERN_REPLACEMENT = "\\\\$0";

	private static final String EXTENSION_REGEX = "\\.[0-9]+$";

	private static final int BUFFER_LENGTH = (int) (8 * SplitSize.KB.getMultiplier());

	private final File destinationFolder;
	private final String outputFileName;

	private final File[] partFiles;

	private String currentFileName;
	private int processedFileNumber;

	public Reconstructor(File destinationFolder, File firstPartFile) {
		this.destinationFolder = destinationFolder;

		// Search in the folder for other parts
		File parentFolder = firstPartFile.getParentFile();
		String fileName = firstPartFile.getName();
		int lastDotIndex = fileName.lastIndexOf('.');
		String fileNameWithoutPartExt = fileName.substring(0, lastDotIndex);
		String sanitizedFileNameWithoutPartExt = fileNameWithoutPartExt.replaceAll(PATTERN_SANITIZER,
				PATTERN_REPLACEMENT);

		// If the user has weird files with same name and parted too, well too bad for
		// him.
		Pattern pattern = Pattern.compile(sanitizedFileNameWithoutPartExt + EXTENSION_REGEX);

		this.partFiles = parentFolder.listFiles((_, name) -> pattern.matcher(name).matches());
		this.outputFileName = fileNameWithoutPartExt;

		this.currentFileName = "";
		this.processedFileNumber = 0;
	}

	public void reconstruct() throws IOException {
		try (FileOutputStream outputStream = new FileOutputStream(
				destinationFolder.getAbsolutePath() + File.separator + outputFileName)) {
			// Write everything you read from each file
			for (File partFile : this.partFiles) {
				this.currentFileName = partFile.getName();
				byte[] buffer = new byte[BUFFER_LENGTH];
				try (FileInputStream inputStream = new FileInputStream(partFile)) {
					int readSize;
					while ((readSize = inputStream.read(buffer)) != -1) {
						outputStream.write(buffer, 0, readSize);
					}
				}
				this.processedFileNumber += 1;
			}
		}
	}

	public String getCurrentFileName() {
		return currentFileName;
	}
	
	public int getProcessedFileNumber() {
		return processedFileNumber;
	}
	
	public int getFilesToProcessNumber() {
		return partFiles.length;
	}
}
