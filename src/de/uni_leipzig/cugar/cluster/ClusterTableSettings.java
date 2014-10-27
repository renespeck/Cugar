package de.uni_leipzig.cugar.cluster;
/**
 * Settings for column labels and column types of a {@link prefuse.data.Table} which holds the cluster data.
 */
public final class ClusterTableSettings{
	
	private ClusterTableSettings() { /**/ }
	/** Identifier for the column in cluster table given back from 
	 * {@link de.uni_leipzig.cugar.cluster.ClusterAlgorithm#clustering(String[], double, String, String, String, String)} method.*/
	public static final String CLUSTER_COLUMN_NAME = "Cluster";
	/** Identifier for data type (TreeSet<String>)of column in cluster table given back from 
	 * {@link de.uni_leipzig.cugar.cluster.ClusterAlgorithm#clustering(String[], double, String, String, String, String)} method.*/
	public static final Class CLUSTER_COLUMN_NAME_TYPE = java.util.TreeSet.class;		
	/** Identifier for size of cluster table,
	 *  is used to sort the clusters for a non overlapping painting.
	 *  If this column and values not added to the table, the application will do and should be prefer.
	 */
	public static final String CLUSTER_COLUMN_SIZE = "# Elements";
	/** Identifier for data type (int) of size column.*/
	public static final Class CLUSTER_COLUMN_SIZE_TYPE = int.class;	
	/** Id to show and identify cluster for decorator */
	public static final String CLUSTER_COLUMN_ID = "ID";
	/** Identifier for data type (int) of id column.*/
	public static final Class CLUSTER_COLUMN_ID_TYPE = int.class;		
}