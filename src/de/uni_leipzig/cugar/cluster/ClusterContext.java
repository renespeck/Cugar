package de.uni_leipzig.cugar.cluster;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import prefuse.data.Table;
import de.uni_leipzig.cugar.gui.LogManager;
import de.uni_leipzig.cugar.gui.LogPanelAppender;
/**
 * This class represents the clients context interface and
 * holds a concrete {@link ClusterAlgorithm} instance,
 * clients can select, add  and use an available cluster algorithm instance. 
 * The {@link #cluster(String[], double, String, String, String, String)} method delegates task 
 * to a {@link #clusterAlgorithm} instance.
 * 
 */
public class ClusterContext {
	/** an instance of the current ClusterAlgorithm */
	protected ClusterAlgorithm clusterAlgorithm = null;	
	/** all added algorithms */
	protected Map<String,ClusterAlgorithm> clusterAlgorithmMap = new HashMap<String,ClusterAlgorithm>();
	/** the Logger for this class */
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ClusterContext.class);

	/**
	 *  Initializes the all available algorithms 
	 *  and sets the {@link LogPanelAppender} Handler to the Logger.
	 */
	public ClusterContext(){	
		logger.setLevel(org.apache.log4j.Level.INFO);
		logger.addAppender(LogManager.instance().getLogPanelAppender());
		// read plugins
		String pluginFolder = "plugin";
		// TODO: check data.
		if(new File(pluginFolder).isDirectory()){
			logger.info("loading plugins...");
			final String fileList[] = new File(pluginFolder).list();

			for(String file :fileList){
				if(file.endsWith(".jar")){
					logger.info("loading plugin " + file);
					File jar = new File(pluginFolder + File.separator + file);
					ClusterAlgorithm ca = null;
					try {
						ClassLoader cl = URLClassLoader.newInstance(new URL[]{jar.toURI().toURL()});
						ca = (ClusterAlgorithm) cl.loadClass("de.uni_leipzig.cugar.cluster." + jar.getName().replace(".jar", "")).newInstance();
					} catch (Exception e) {
						e.printStackTrace();
					} 
					addAlgorithm(ca.getName(),ca);
				}
			}}
		if(clusterAlgorithmMap.size() < 1){
			logger.error("Can't find an algorithm in plugin folder.");
			System.exit(0);
		}
		
		if(clusterAlgorithmMap.containsKey("BorderFlow"))
			setAlgorithm("BorderFlow");		
		else
			setAlgorithm(getAlgorithmNames()[0]);
	}	
	/**
	 * Adds a new instance of a ClusterAlgorithm.
	 * 
	 * @param name the unique name of the algorithm
	 * @param ca the algorithm instance
	 * @return the algorithm if success
	 */
	public ClusterAlgorithm addAlgorithm(String name, ClusterAlgorithm ca){
		return clusterAlgorithmMap.put(name, ca);		
	}
	/**
	 * The cluster method gets the cluster back in a table.
	 * 
	 * @param seeds all selected seeds
	 * @param threshold threshold in percent
	 * @param value_A value of config_A array
	 * @param value_B value of config_B array
	 * @param value_C value of config_C array
	 * @param value_D value of config_D array
	 * @return table with one or more columns
	 */
	public Table cluster(
			String[] seeds,double threshold, String value_A, String value_B, String value_C, String value_D
	){
		return clusterAlgorithm.cluster(seeds,threshold, value_A, value_B, value_C, value_D);
	}	
	/** select an available algorithm by the given unique name, to get access of the concrete configurations.  */
	public void setAlgorithm(String name){		
		if(clusterAlgorithmMap.containsKey(name))
			clusterAlgorithm = clusterAlgorithmMap.get(name);
	}	
	/** Select an available algorithm by the given unique name. And sets the path to file */
	public void setAlgorithm(String filename,String name){
		setAlgorithm(name);
		if(clusterAlgorithm!=null){
			clusterAlgorithm.setFilename(filename);	
			logger.debug("\nalgorithm:\t" + name + "\n" + "file:\t" + filename);
		}
	}
	/** Gets all available algorithm names. */
	public String[] getAlgorithmNames(){
		String[] algo = new String[clusterAlgorithmMap.size()];
		int i = 0;
		for(String name : clusterAlgorithmMap.keySet() ){
			algo[i] = name;
			i++;
		}		
		return algo; 
	}	

	public String getCurrentAlgorithmName(){
		if(clusterAlgorithm != null){
			Iterator<Map.Entry<String, ClusterAlgorithm>> iter = clusterAlgorithmMap.entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry<String, ClusterAlgorithm> e = iter.next();
				if(e.getValue().equals(clusterAlgorithm))
					return e.getKey();	
			}
		}			
		return "";
	}
	/** Available settings of current cluster algorithm. */
	public String[] getA(){
		return clusterAlgorithm.getA();
	}	
	/** Available settings of current cluster algorithm. */
	public String[] getB(){
		return clusterAlgorithm.getB();
	}		
	/** Available settings of current cluster algorithm. */
	public String[] getC(){
		return clusterAlgorithm.getC();
	}
	/** Available settings of current cluster algorithm. */
	public String[] getD(){
		return clusterAlgorithm.getD();
	}	
}