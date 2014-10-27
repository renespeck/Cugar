package de.uni_leipzig.cugar.cluster;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.uni_leipzig.cugar.gui.LogManager;
import de.uni_leipzig.cugar.harden.ClusterGraphInterface;
import de.uni_leipzig.cugar.harden.Harden;
import de.uni_leipzig.cugar.harden.HardenMaxQuality;
import de.uni_leipzig.cugar.harden.QualityMeasure;

import static de.uni_leipzig.cugar.cluster.ClusterTableSettings.CLUSTER_COLUMN_ID;
import static de.uni_leipzig.cugar.cluster.ClusterTableSettings.CLUSTER_COLUMN_ID_TYPE;
import static de.uni_leipzig.cugar.cluster.ClusterTableSettings.CLUSTER_COLUMN_NAME;
import static de.uni_leipzig.cugar.cluster.ClusterTableSettings.CLUSTER_COLUMN_NAME_TYPE;
import static de.uni_leipzig.cugar.cluster.ClusterTableSettings.CLUSTER_COLUMN_SIZE;
import static de.uni_leipzig.cugar.cluster.ClusterTableSettings.CLUSTER_COLUMN_SIZE_TYPE;

import prefuse.data.Table;
import prefuse.data.util.Sort;
/**
 * <p>
 * Is an abstract base class for a concrete cluster algorithm implementation.
 * A concrete algorithm just need to implement the {@link #clustering(String[], double, String, String, String, String)} method.
 * The four String parameters comes from the member String arrays {@link #config_A} ,..., {@link #config_D} 
 * and can be used for different configurations of an algorithm by overriding this members, 
 * e.g. use <code>config_A = new String[]{"k","1",...,"100"}</code> for parameter k of k-nearest neighbours algorithm.
 * The first element in an array is a value for a JLabel which describes the configuration for the user.
 * A user can choose the other elements of each array from a JComboBox in the application and 
 * this values will sent back to the clustering method as parameters so that they can be used for configurations.
 * </p>
 * <p>
 * The String {@link #filename} member variable contains the path to a delimiter separated text file, 
 * separators are space, comma or tabulator. See {@link #getSeparator()}.
 * </p>
 * <p>
 * After a new algorithm has been implemented, it still needs to be added to the application.
 * For this use the {@link de.uni_leipzig.cugar.cluster.ClusterContext} class with the  
 * {@link de.uni_leipzig.cugar.cluster.ClusterContext#addAlgorithm(String, ClusterAlgorithm)} method. 
 * For example, write in {@link de.uni_leipzig.cugar.gui.Main#cugarDemo()} method (which starts the application) the following:
 * <br>
 * <p>
 * <code>
 * Model model = new Model();<br>
 * ClusterContext cc = model.getClusterContext();<br>
 * cc.addAlgorithm("Foobar", new ClusterAlgorithmFoobar());<br>
 * </code>
 * </p>
 * or modify the {@link de.uni_leipzig.cugar.cluster.ClusterContext#ClusterContext()} constructor to append the algorithm.
 * </p>
 */
public abstract class ClusterAlgorithm{	
	public static Logger logger = Logger.getLogger(ClusterAlgorithm.class);
	static{
		logger.setLevel(Level.INFO);
		logger.removeAllAppenders();
		logger.addAppender(LogManager.instance().getLogPanelAppender());
	}
	/** index for hardening */
	protected Map<TreeSet<Integer>, TreeSet<Integer>> clusterSeedMap = null;
	/** labels for hardening*/
	protected Map<TreeSet<String>, TreeSet<String>> clusterSeedMapLabels = null;
	/** Empty constructor. */
	public ClusterAlgorithm(){ }
	
	protected HashMap<String, Integer> index;
	protected HashMap<Integer, String> reverseIndex;
	/**
	 * Overloaded of {@link #hardening(Harden, QualityMeasure, ClusterGraphInterface)} without a QualityMeasure (i.e. with null).
	 * 
	 * @param harden instance of 
	 * @param graph
	 */
	protected void hardening(Harden harden, ClusterGraphInterface graph){
		hardening(harden,null, graph);		
	}
	/**
	 * Hardening and removing of duplicate or empty clusters in {@link #clusterSeedMap}.
	 * 
	 * @param harden
	 * @param qualityMeasure 
	 * @param graph
	 */
	protected void hardening(Harden harden, QualityMeasure qualityMeasure, ClusterGraphInterface graph){

		if(qualityMeasure != null && harden instanceof HardenMaxQuality)
			harden.qualityMeasure(qualityMeasure);

		logger.info("Hardening starts with: " + clusterSeedMap.size());
		// do hardening
		clusterSeedMap = harden.harden(clusterSeedMap, graph);	
		harden.removeDuplicateAndEmptyClusters(clusterSeedMap);

		logger.info("Hardening ends with: " + clusterSeedMap.size());
	}
	/** 
	 * An array of default available settings, override this array for a concrete configuration.
	 * The first element in config_A is a value of a JLabel instance shown in the application.
	 * All other elements in config_A are values which are given back to 
	 * {@link #clustering(String[], double, String, String, String, String)}.
	 */
	protected String[] config_A = { " ", "--"};	
	/** 
	 * An array of default available settings, override this array for a concrete configuration.
	 * The first element in config_B is a value of a JLabel instance shown in the application.
	 * All other elements in config_B are values which are given back to 
	 * {@link #clustering(String[], double, String, String, String, String)}.
	 */
	protected String[] config_B = { " ", "--"};	
	/** 
	 * An array of default available settings, override this array for a concrete configuration.
	 * The first element in config_C is a value of a JLabel instance shown in the application.
	 * All other elements in config_C are values which are given back to 
	 * {@link #clustering(String[], double, String, String, String, String)}.
	 */
	protected String[] config_C = { " ", "--"};	
	/** 
	 * An array of default available settings, override this array for a concrete configuration.
	 * The first element in config_D is a value of a JLabel instance shown in the application.
	 * All other elements in config_D are values which are given back to 
	 * {@link #clustering(String[], double, String, String, String, String)}.
	 */
	protected String[] config_D = { " ", "--"};
	/** path to file */
	protected String filename = "";	
	/**
	 * An abstract method for a concrete clustering algorithm implementation or adaption.
	 * The clustering method gets the cluster back in a table
	 * with a column name of {@link #CLUSTER_COLUMN_NAME} and with a column type of {@link #CLUSTER_COLUMN_NAME_TYPE}.
	 * The column {@link #CLUSTER_COLUMN_SIZE} is reserved for a inner sort operation, you don't have to use it, but you can 
	 * use this column to add the size of a cluster with type of {@link #CLUSTER_COLUMN_SIZE_TYPE}, but be sure that every row is set with a value.
	 * This size defines the order of printing the clusters, the highest value of size for a cluster will print first.
	 * 
	 * @param seeds selected nodes that will be used as seeds
	 * @param threshold threshold is between 0 and 1 or -1 for all nodes
	 * @param values_A value of config_A array
	 * @param values_B value of config_B array
	 * @param values_C value of config_C array
	 * @param values_D value of config_D array
	 * @return table with one or more columns
	 */
	protected abstract Table clustering(
			String[] seeds, double threshold, 
			String values_A, String values_B, 
			String values_C, String values_D
	);
	/**
	 * A template method to call the concrete clustering method.
	 * Checks the cluster table format for validity. ({@link #CLUSTER_COLUMN_NAME} and {@link #CLUSTER_COLUMN_NAME_TYPE})
	 * Sorts clusterTable parameter with the column {@link #CLUSTER_COLUMN_SIZE}.

	 * @param seeds all selected seeds
	 * @param threshold threshold is between 0 and 1 or -1 for all nodes
	 * @param values_A value of config_A array
	 * @param values_B value of config_B array
	 * @param values_C value of config_C array
	 * @param values_D value of config_D array
	 * @return table with one or more columns, null if wrong cluster table format
	 */
	public final Table cluster(
			String[] seeds, double threshold, 
			String values_A, String values_B, 
			String values_C, String values_D
	){
		Table table = clustering( seeds, threshold, values_A,  values_B, values_C, values_D);

		if(table == null) return null;
		// check reserved columns of table
		if(table.getColumnNumber(CLUSTER_COLUMN_NAME) == -1 
				|| !table.getColumnType(CLUSTER_COLUMN_NAME).equals(CLUSTER_COLUMN_NAME_TYPE)
				|| table.getColumnNumber(CLUSTER_COLUMN_ID) != -1 
				|| table.getColumnNumber(CLUSTER_COLUMN_SIZE) != -1		
		){
			logger.warn(
					"Dont use: \"" 
					+ CLUSTER_COLUMN_ID + "\",\"" 												
					+ CLUSTER_COLUMN_SIZE + "\""
					+ " as column in cluster table.\n"
					+ "But you have to use \"" + CLUSTER_COLUMN_NAME + "\""
					+ " for column with cluster data."
			);
			return null;
		}

		if(table.getColumnNumber(CLUSTER_COLUMN_SIZE) == -1){
			table.addColumn(CLUSTER_COLUMN_SIZE, CLUSTER_COLUMN_SIZE_TYPE);
			// add size of elements in a cluster if not given
			for (int row = 0; row < table.getRowCount(); row++ ) {			
				TreeSet<String> cluster = (TreeSet<String>)table.get(row, CLUSTER_COLUMN_NAME);					 				
				table.set(row, CLUSTER_COLUMN_SIZE, cluster.size());
			}
		}
		//sort clusterTable
		if(table!=null)
			table = table.select(null, new Sort(new String[] {CLUSTER_COLUMN_SIZE},new boolean[]{false}));
		// delete column.
		//table.removeColumn(CLUSTER_COLUMN_SIZE); 

		// add decorators id
		if(table.getColumnNumber(CLUSTER_COLUMN_ID) == -1)
			table.addColumn(CLUSTER_COLUMN_ID, CLUSTER_COLUMN_ID_TYPE);
		
		for (int row_id = 0; row_id < table.getRowCount(); row_id++ ) 						 				
			table.set(row_id, CLUSTER_COLUMN_ID, row_id);

		return table;			
	}
	/** sets {@link #filename}, the path to file */
	public void setFilename(String name){
		filename = name;		
	}	
	/**
	 * Gets the separator of {@link #filename} depends on the file extension.
	 * 
	 * @return  "," if file extension is csv <br>
	 * 			" " if file extension is ssv <br> 
	 * 			"\t" if file extension is tab or txt 
	 */
	public String getSeparator(){
		if(filename != null){	
			return 
			filename.endsWith(".csv") ? "," :
			filename.endsWith(".ssv") ? " " : 
			filename.endsWith(".tab") ? "\t" : 
			filename.endsWith(".txt") ? "\t" : "";  
		}
		return "";	
	}	
	/** gets a valid {@link prefuse.data.Table}, that means 
	 * 	with a column of {@link #CLUSTER_COLUMN_NAME} 
	 *  and a type of {@link #CLUSTER_COLUMN_NAME_TYPE}
	 *  for the cluster data
	 */
	protected Table getTable(){
		Table table = new Table();
		table.addColumn(CLUSTER_COLUMN_NAME, CLUSTER_COLUMN_NAME_TYPE);
		return table;		
	}
	/** gets available configuration*/
	public String[] getA(){
		return config_A;
	}	
	/** gets available configuration*/
	public String[] getB(){
		return config_B;
	}
	/** gets available configuration*/
	public String[] getC(){
		return config_C;
	}	
	/** gets available configuration*/
	public String[] getD(){
		return config_D;
	}
	/** gets the name*/
	public abstract String getName();
	/**
	 * Builds {@link #index} 1,...,n for all labels in file 
	 * and builds the {@link #reverseIndex}.
	 */
	public void initializeIndex(String file, String seperator) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));             
			String line = "";			
			TreeSet<String> terms = new TreeSet<String>();             
			while ((line = reader.readLine()) != null) {
				if (line.contains(seperator)) {
					String split[] = line.split(seperator);
					terms.add(split[0]);
					terms.add(split[1]);
				}
			}
			reader.close();
			index = new HashMap<String, Integer>();
			reverseIndex = new HashMap<Integer, String>();             
			Iterator<String> iter = terms.iterator();
			int id = 1;
			while (iter.hasNext()) {
				String term = iter.next();
				index.put(term, new Integer(id));
				reverseIndex.put(new Integer(id), term);
				id++;
			}
		}catch(Exception e){
			logger.warn("Error: build index");			
		}
	}
	/**
	 * Map with index to map with label.
	 * @param clusterSeedMap with index
	 * @return clusterSeedMapLabels with label
	 */
	public Map<TreeSet<String>, TreeSet<String>> getLabels(Map<TreeSet<Integer>, TreeSet<Integer>> clusterSeedMap){	
		if(reverseIndex != null && index != null){
			clusterSeedMapLabels = new HashMap<TreeSet<String>, TreeSet<String>>();			
			for (Entry<TreeSet<Integer>, TreeSet<Integer>> entry : clusterSeedMap.entrySet()) {

				TreeSet<String> keyClear = new TreeSet<String>();
				TreeSet<String> seedClear = new TreeSet<String>();

				for (int i : entry.getKey())
					keyClear.add(reverseIndex.get(i));
				for (int j : entry.getValue())
					seedClear.add(reverseIndex.get(j));

				clusterSeedMapLabels.put(keyClear, seedClear);
			}
		}else{
			logger.warn("reverseIndex or index isn't initialized");
		}
		return clusterSeedMapLabels;		
	}
	/**
	 * Gets this class name.
	 */
	@Override
	public String toString(){
		return getClass().getSimpleName();
	}
}