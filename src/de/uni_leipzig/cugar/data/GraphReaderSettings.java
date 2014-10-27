package de.uni_leipzig.cugar.data;
/**
 * Settings for all GraphReaders.
 */
public final class GraphReaderSettings {
	private GraphReaderSettings(){ /**/ }
	/** Inner id for nodes, should different form any id field in xml file */
	public static final String NODE_NAME = "visual_id"; 
	/** The max. edge weight that will be used */
	public static final double MAX_EDGE_WEIGHT = Double.MAX_VALUE;	
	/** Inner id for edge weights*/
	public static final String EDGE_WEIGHT = "weight"; 
}
