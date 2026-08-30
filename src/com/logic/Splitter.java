package com.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Splitter {

	private static final int BUFFER_LENGTH = (int) (8 * SplitSize.KB.getMultiplier());
	private static final String EXTENSION_FORMAT = ".%03d";

	private final File inputFile;
	private final String destinationPartPathWithoutExt;
	private final long partSize;
	private final int numberFilesToCreate;
	private final long inputFileSize;

	private String currentCreatedFileName;
	private int createdFilesNumber;

	private FileOutputStream currentPartStream;

	public Splitter(File destinationFolder, File inputFile, long partSize) {
		this.inputFile = inputFile;
		this.destinationPartPathWithoutExt = destinationFolder.getAbsolutePath() + File.separator + inputFile.getName();
		this.partSize = partSize;
		this.inputFileSize = inputFile.length();
		this.numberFilesToCreate = (int) Math.ceil((double) inputFileSize / partSize);

		this.currentCreatedFileName = "";
		this.createdFilesNumber = 0;
	}

	public void split() throws IOException {
		// Create first output part stream
		switchPartFile();

		try (FileInputStream inputStream = new FileInputStream(inputFile)) {
			boolean finished = false;
			long remainingPartSize = this.partSize;
			long totalBytesRead = 0;
			while (!finished) {
				// Keep track of part size

				// If remaining part size is lesser than buffer length (which is an int) then
				// read the remaining part
				int bufferSize = BUFFER_LENGTH > remainingPartSize ? (int) remainingPartSize : BUFFER_LENGTH;
				byte[] buffer = new byte[bufferSize];
				int readSize = inputStream.read(buffer, 0, bufferSize);

				// If nothing to read then it's finished
				if (readSize == -1) {
					finished = true;
				} else {
					totalBytesRead += readSize;
					currentPartStream.write(buffer, 0, readSize);
					remainingPartSize -= bufferSize;
					if (remainingPartSize == 0) {
						// Check if total bytes read is the total file
						if (totalBytesRead == inputFileSize) {
							finished = true;
						} else {
							remainingPartSize = this.partSize;
							switchPartFile();
						}
					}
				}
			}

			// Don't forget to close the last part's stream
			currentPartStream.close();
		}
	}

	private void switchPartFile() throws IOException {
		if (currentPartStream != null) {
			currentPartStream.close();
		}

		createdFilesNumber += 1;
		File partFile = new File(destinationPartPathWithoutExt + String.format(EXTENSION_FORMAT, createdFilesNumber));
		currentCreatedFileName = partFile.getName();
		currentPartStream = new FileOutputStream(partFile);
	}

	public String getCurrentCreatedFileName() {
		return currentCreatedFileName;
	}

	public int getNumberFilesToCreate() {
		return numberFilesToCreate;
	}

	public int getCreatedFilesNumber() {
		return createdFilesNumber;
	}

}
