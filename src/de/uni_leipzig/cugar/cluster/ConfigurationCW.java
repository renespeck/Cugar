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
public final class ConfigurationCW {
	private ConfigurationCW(){ }
	
	protected static Logger log = Logger.getLogger(ConfigurationCW.class);	
	protected static final Properties properties = new Properties();	
	private static final String file = "plugin"+File.separator+"cw.cfg";
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
	public static final String[] ITERATIONS,OPTIONS,MUTATION,COLOR;
	// initialize constants
	static{		
		String it = properties.getProperty("cw_iterations");
		if(it == null) exit("cw_iterations");
		ITERATIONS = it.split(";");
		trim(ITERATIONS);

		String op = properties.getProperty("cw_options");
		if(op == null) exit("cw_options");
		OPTIONS = op.split(";");
		trim(OPTIONS);


		String mu = properties.getProperty("cw_mutation");
		if(mu == null) exit("cw_mutation");
		MUTATION = mu.split(";");
		trim(MUTATION);
		

		String co = properties.getProperty("cw_color");
		if(co == null) exit("cw_color");
		COLOR = co.split(";");
		trim(COLOR);	
	}
}