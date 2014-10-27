package de.uni_leipzig.cugar.gui;
 
public class LogManager {

	private static LogManager m_lm = null;
	private LogPanelAppender m_lpa = null;
	
	private LogManager(){	
		m_lpa = new LogPanelAppender();
	}
	public static LogManager instance(){
		if(m_lm == null)
			m_lm = new LogManager();
		return m_lm;
	}
	public LogPanelAppender getLogPanelAppender(){
		return m_lpa;
	}
}