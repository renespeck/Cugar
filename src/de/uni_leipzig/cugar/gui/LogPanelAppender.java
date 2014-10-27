package de.uni_leipzig.cugar.gui;
import javax.swing.SwingUtilities;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;
 
public class LogPanelAppender extends WriterAppender{
	/** JPanel to handle*/
	private static LogPanel logPanel = new LogPanel();
	/**
	 * Creates a new LogPanel instance and initialized the LogPanelAppender.
	 */
	public LogPanelAppender() {	
		String pattern = "";
		//pattern += "Location of log event: %l %n";
		pattern += "%l %n";
		pattern += "Message: %m %n %n";
	
		layout = new PatternLayout(pattern);		
	}	
	public LogPanel getLogPanel(){
		return logPanel;
	}
	/**
	 * Format and then append the loggingEvent.
	 */
	public void append(LoggingEvent loggingEvent) {			
		final String message = layout.format(loggingEvent);		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				logPanel.showLog(message);
			}
		});
	}
}