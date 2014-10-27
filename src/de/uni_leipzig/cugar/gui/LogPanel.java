package de.uni_leipzig.cugar.gui;
 
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
/**
 * It's an Observer JPanel to show logs to the application with {@link #showLog(String)} method.
 */
public class LogPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private final String filename = "cvk.log.txt"; 
	private JTextArea logTextArea = null;
	private JToggleButton fileToggleButton = null;
	private JButton clearButton = null;
	private JPanel buttonPanel = null;
	private JScrollPane logScrollPane = null;
	private final String defaultMessage = "";
	/**
	 * This is the default constructor
	 */
	public LogPanel() {
		super();
		// delete log, use new one for every new application start
		//new File(filename).delete();
		initialize();
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(getButtonPanel(), null);
		this.add(getLogScrollPane(), null);
		this.setBackground(Color.white);		
	}
	/**
	 * Adds a String to the JTextArea instance to show the log. 
	 */
	public void showLog(String log) {
		logTextArea.append(log);
		if(fileToggleButton.isSelected()){
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(filename,true));
				out.write(log);
				out.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method initializes logTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getLogTextArea() {
		if (logTextArea == null) {
			logTextArea = new JTextArea();
			logTextArea.setText(defaultMessage);
		}
		return logTextArea;
	}

	/**
	 * This method initializes fileToggleButton	
	 * 	
	 * @return javax.swing.JToggleButton	
	 */
	private JToggleButton getFileToggleButton() {
		if (fileToggleButton == null) {
			fileToggleButton = new JToggleButton();
			fileToggleButton.setText("log to file");
			fileToggleButton.setMaximumSize(new Dimension(152,30));
			fileToggleButton.setPreferredSize(fileToggleButton.getMaximumSize());
			fileToggleButton.setMinimumSize(fileToggleButton.getMaximumSize());
			//fileToggleButton.setSelected(true);		
		}
		return fileToggleButton;
	}

	/**
	 * This method initializes clearButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getClearButton() {
		if (clearButton == null) {
			clearButton = new JButton();
			clearButton.setMaximumSize(new Dimension(152,30));
			clearButton.setPreferredSize(clearButton.getMaximumSize());
			clearButton.setMinimumSize(clearButton.getMaximumSize());
			clearButton.setText("clear log");
			clearButton.addActionListener(new ActionListener(){		
				@Override
				public void actionPerformed(ActionEvent e) {
					logTextArea.setText("");					
				}
			});			
		}
		return clearButton;
	}

	/**
	 * This method initializes buttonPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.setMaximumSize(new Dimension(305,40));
			buttonPanel.setPreferredSize(buttonPanel.getMaximumSize());
			buttonPanel.setMinimumSize(buttonPanel.getMaximumSize());
			buttonPanel.setBackground(Color.white);
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
			buttonPanel.add(getFileToggleButton(), null);
			buttonPanel.add(getClearButton(), null);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes logScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getLogScrollPane() {
		if (logScrollPane == null) {
			logScrollPane = new JScrollPane();
			logScrollPane.setViewportView(getLogTextArea());
		}
		return logScrollPane;
	}
}