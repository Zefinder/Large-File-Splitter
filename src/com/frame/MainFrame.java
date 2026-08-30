package com.frame;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 750665876410818056L;

	private static final String FRAME_TITLE = "Large file splitter";

	private static final String SPLIT_TAB_NAME = "Split file";
	private static final String RECONSTRUCT_TAB_NAME = "Reconstruct file";

	public MainFrame() {
		this.setTitle(FRAME_TITLE);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JTabbedPane tabPanel = new JTabbedPane();
		tabPanel.addTab(SPLIT_TAB_NAME, new FileSplitterPanel());
		tabPanel.addTab(RECONSTRUCT_TAB_NAME, new FileReconstructorPanel());

		this.add(tabPanel);
		this.setVisible(false);
	}

	public void initFrame() {
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

}
