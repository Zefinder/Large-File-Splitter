package com.frame;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

public final class PanelUtils {

	private PanelUtils() {
		// TODO Auto-generated constructor stub
	}

	public static JTextField createTextField() {
		JTextField field = new JTextField(35);

		return field;
	}

	public static String invokeFileChooser() {
		return invokeFileChooser(null);
	}

	
	public static String invokeFileChooser(FileFilter filter) {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		if (filter != null) {			
			chooser.addChoosableFileFilter(filter);
			chooser.setFileFilter(filter);
		}
				
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile().getAbsolutePath();
		}

		return null;
	}
	
	public static String invokeFolderChooser() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile().getAbsolutePath();
		}

		return null;
	}
	
	public static boolean invokeChoiceDialog(String message, String title) {
		int response = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		return response == JOptionPane.YES_OPTION;
	}

}
