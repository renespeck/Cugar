package de.uni_leipzig.cugar.cluster;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author rspeck
 */
public final class ConfigurationAP{
	private ConfigurationAP(){ }
	
	protected static Logger log = Logger.getLogger(ConfigurationAP.class);	
	protected static final Properties properties = new Properties();	
	private static final String file = "plugin"+File.separator+"ap.cfg";
	protected static final String not_found_message = " not found in " + file + ".";
	
	static{
		log.setLevel(Level.ALL);
		log.info("Load Configuration...");
		try {				
			BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));
			properties.load(stream);
			stream.close();
		} catch (Exception e) {		
			e.printStackTrace();
		}
	}
	/** if a line in {@link #file} isn't valid, this method prints the valid key, a warning message and stops the application 
	 *  @param reason the valid key in config file {@link #file}
	 */
	protected static void exit(String reason){
		log.warn(reason + not_found_message);
		System.exit(1);
	}
	protected static void trim(String[] s){
		for(int i =0;i<s.length;i++)
			s[i] = s[i].trim();
	}
	public static final String[] LAMBDA,ITERATIONS,PREFERENCES,CONVITS;
	// initialize constants
	static{		
		String la = properties.getProperty("ap_lambda");
		if(la == null) exit("ap_lambda");
		LAMBDA = la.split(";");
		trim(LAMBDA);

		String it = properties.getProperty("ap_iterations");
		if(it == null) exit("ap_iterations");
		ITERATIONS = it.split(";");
		trim(ITERATIONS);


		String pr = properties.getProperty("ap_preferences");
		if(pr == null) exit("ap_preferences");
		PREFERENCES = pr.split(";");
		trim(PREFERENCES);
		

		String co = properties.getProperty("ap_convits");
		if(co == null) exit("ap_convits");
		CONVITS = co.split(";");
		trim(CONVITS);	
	}
}