package de.uni_leipzig.cugar.data;

import static de.uni_leipzig.cugar.data.GraphReaderSettings.EDGE_WEIGHT;
import static de.uni_leipzig.cugar.data.GraphReaderSettings.MAX_EDGE_WEIGHT;
import static de.uni_leipzig.cugar.data.GraphReaderSettings.NODE_NAME;

import java.io.File;
import java.util.Observable;
import java.util.Set;

import de.uni_leipzig.cugar.cluster.ClusterContext;

import prefuse.data.Graph;
import prefuse.data.Table;
import prefuse.data.io.DataIOException;
import prefuse.data.io.DelimitedTextTableReader;
import prefuse.data.io.DelimitedTextTableWriter;
/**
 * This class represents an Observable Model to hold, load and save data with an instances of {@link EdgeListGraphReader} and {@link GraphMLReaderMod} class,
 * to give access to all supported algorithm with an instance of {@link ClusterContext} class and 
 * to notify all registered views by arguments.
 * 
 * @author rspeck
 * 
 */
public class Model extends Observable{	
	/** argument for view*/
	public static final String OPENFILE = "Open File";
	/** argument for view*/
	public static final String PAUSE = "pause";
	/** argument for view*/
	public static final String CLUSTER = "cluster";	
	/** argument for view*/
	public static final String SET_ALGO = "algoChanged";	
	/** argument for view*/
	public static final String NODE_TABLE_EVENT = "nodeTableClicked";	
	/** argument for view*/
	public static final String CLUSTER_TABLE_EVENT = "clusterTableClicked";
	/** argument for view*/
	public static final String QUALITY = "quality";
	/** argument for openGraph method */
	public static final String GRID_GRAPH = "Grid";   
	/** argument for openGraph method */
	public static final String HONEYCOMB = "Honeycomb";
	/** argument for openGraph method */
	public static final String BALANCED_TREE = "Balanced";
	/** argument for openGraph method */
	public static final String DIAMOND_TREE = "Diamond";
	/** argument for openGraph method */
	public static final String TOPPED = "Topped_Tetraedron";
	/** argument for openGraph method */
	public static final String CLIQUE = "Random_Clique";	
	/** argument for openGraph method */
	public static final String ERDOSRENYI = "Erdos_Renyi";	
	public static final String KLEINBERG = "Kleinberg";	
	public static final String BARABASI = "Barabasi_Albert";	
	public static final String EPPSTEIN = "Eppstein";	
	/** resource path */
	public final String IMG_FOLDER = "/folder24.png"; 
	/** resource path */
	public final String IMG_INFO = "/infoabout24.png";
	/** resource path */
	public final String IMG_QUALITY_ON = "/edit24.png";
	/** resource path */
	public final String IMG_QUALITY_OFF = "/edit24_bw.png";
	/** resource path */
	private String img_quality;
	/** resource path */	
	private final String IMG_COLOR_ON = "/paint24.png";
	/** resource path */
	private final String IMG_COLOR_OFF = "/paint24_bw.png";	
	/** resource path */
	private String img_color;
	/** resource path */
	private final String IMG_FILTER_ON = "/network24.png";
	/** resource path */
	private final String IMG_FILTER_OFF = "/network24_bw.png";	
	/** resource path */
	private String img_filter;
	/** resource path */	
	private final String IMG_PAUSE_OFF = "/play24.png";
	/** resource path */
	private final String IMG_PAUSE_ON = "/pause24.png";
	/** resource path */
	private String img_pause;
	/** table to hold cluster data */
	protected Table m_clusterTable = null;
	public Table getClusterTable(){
		return m_clusterTable; 	
	}
	/** current graph data */
	protected Graph m_graph = null;
	public Graph getGraph(){	
		return m_graph; 	
	}
	/** access to all algorithm */
	protected ClusterContext m_clusterContext = null;	
	public ClusterContext getClusterContext(){	
		return m_clusterContext;	
	}
	// settings
	private boolean m_filter = true;	
	public void changeDistanceFilter(){ 	
		m_filter = !m_filter; 	
	}
	public boolean getDistanceFilter(){ 	
		return m_filter;	
	}
	public String getDistanceFilterImg(){ 	
		if(m_filter)
			img_filter = IMG_FILTER_ON;
		else
			img_filter = IMG_FILTER_OFF;
		return img_filter; 
	}
	private boolean m_quality = false;
	public void changeQuality(){ 	
		m_quality = !m_quality;
	}
	public boolean getQuality(){ 		
		return m_quality;	
	}
	public String getQualityImg(){
		if(m_quality)
			img_quality = IMG_QUALITY_ON;
		else
			img_quality = IMG_QUALITY_OFF;
		return img_quality; 		
	}
	private boolean m_pause = false; 
	public void changePause(){	
		m_pause = !m_pause;	
	}	
	public boolean getPause(){ 	
		return m_pause;	
	}
	public String getPauseImg(){ 	
		if(m_pause)
			img_pause = IMG_PAUSE_OFF;
		else
			img_pause = IMG_PAUSE_ON;
		return img_pause; 
	}
	private boolean m_color = true;	
	public void changeColor(){ 		
		m_color = !m_color; 
	}
	public boolean 	getColor(){ 	
		return m_color; 
	}
	public String getColorImg(){ 	
		if(m_color)
			img_color = IMG_COLOR_ON;
		else
			img_color = IMG_COLOR_OFF;
		return img_color; 
	}
	/* flag for GraphReaders */
	private boolean m_directedGraph = false;
	/* setter for direction flag*/
	public void changeDirection(){  	
		m_directedGraph = !m_directedGraph;		
	}	
	public boolean getDirection(){
		return m_directedGraph;
	}
	/* chosen node */
	private String  m_clickedTableNode = "";
	/** sets the chosen node from table */
	public void setClickedTableNode(String nodeName){
		m_clickedTableNode = nodeName;
		notifyGui(NODE_TABLE_EVENT);
	}
	public String getClickedTableNode(){
		return m_clickedTableNode;		
	}
	/* chosen cluster */
	private Set<Integer>  m_clickedCluster = null;
	/** sets the chosen cluster from table */
	public void setClickedTableCluster(Set<Integer> cluster){
		m_clickedCluster = cluster;
		notifyGui(CLUSTER_TABLE_EVENT);
	}
	public Set<Integer> getClickedTableCluster(){
		return m_clickedCluster;		
	}
	private String m_openFile = null;
	private final String TMP_PATH;	
	private GraphGenerator m_graphGenerator = new GraphGenerator();
	/**
	 * Constructs a new Model instance.
	 */
	public Model(){
		m_clusterContext = new ClusterContext();
		// init img
		getPauseImg();
		getDistanceFilterImg();
		getColorImg();
		getQualityImg();

		TMP_PATH = 
			System.getProperty("java.io.tmpdir") + 
			File.separator +	
			"cvk.tmpfile."; 
	}
	public void notifyGui(String s){
		setChanged();
		notifyObservers(s); 
	}
	/** Method to perform the clustering process.*/
	public void cluster(String[] seeds, double threshold, String values_A, String values_B, String values_C, String values_D){
		if(m_openFile != null){
			// pause and backup last state
			boolean paused = true;
			if(!m_pause){
				m_pause = true;
				paused = false;
				notifyGui(PAUSE);
			}
			// clustering
			m_clusterContext.setAlgorithm(m_openFile, m_clusterContext.getCurrentAlgorithmName());
			m_clusterTable = m_clusterContext.cluster(seeds, threshold, values_A, values_B, values_C, values_D);
			notifyGui(CLUSTER);
			// wasn't pause, change state
			if(!paused){
				m_pause = false;
				notifyGui(PAUSE);				
			}
		}
	}
	private String getSeparatorToExtension(String name){
		String sep = "";
		if (name.toLowerCase().endsWith(".txt") || name.toLowerCase().endsWith(".tab"))
			sep = "\t";			
		if(name.toLowerCase().endsWith(".csv"))
			sep = ",";
		if(name.toLowerCase().endsWith(".ssv"))
			sep = " ";
		return sep;		
	}

	/**
	 * Reads or generates a graph to m_graph with GraphLib or GraphGenerator class depends on argument name.
	 * @param name
	 */
	public void openGraph(String name){		
		m_graph = null;
		if(name.equals(GRID_GRAPH))
			m_graph = m_graphGenerator.grid();			
		if(name.equals(HONEYCOMB))
			m_graph = m_graphGenerator.honeycomb();
		if(name.equals( BALANCED_TREE ))
			m_graph = m_graphGenerator.balancedTree();
		if(name.equals(DIAMOND_TREE))
			m_graph = m_graphGenerator.diamondTree();
		if(name.equals(TOPPED))
			m_graph = m_graphGenerator.topped();		
		// for clique() and erdosRenyi() we use one tmp file,
		// problems with more than one instance of this application on one machine,
		// cause of the use of random
		if(name.equals(CLIQUE))
			m_graph = m_graphGenerator.clique();		
		if(name.equals(ERDOSRENYI)){
			m_graph = m_graphGenerator.erdosRenyi();				
		}
		if(name.equals(BARABASI)){
			m_graphGenerator.writeToFile(TMP_PATH + name + ".tab", "\t", m_graphGenerator.getBarabasiAlbertGraph());		
			EdgeListGraphReader er = new EdgeListGraphReader("\t");
			try {
				m_graph = er.readGraph(TMP_PATH + name + ".tab");
			} catch (DataIOException e) {
				e.printStackTrace();
			}			
		}
		if(name.equals(EPPSTEIN)){
			m_graphGenerator.writeToFile(TMP_PATH + name + ".tab", "\t", m_graphGenerator.getEppsteinPowerLaw());		
			EdgeListGraphReader er = new EdgeListGraphReader("\t");
			try {
				m_graph = er.readGraph(TMP_PATH + name + ".tab");
			} catch (DataIOException e) {
				e.printStackTrace();
			}			
		}
		if(name.equals(KLEINBERG)){
			m_graphGenerator.writeToFile(TMP_PATH + name + ".tab", "\t", m_graphGenerator.getKleinbergSmallWorld());		
			EdgeListGraphReader er = new EdgeListGraphReader("\t");
			try {
				m_graph = er.readGraph(TMP_PATH + name + ".tab");
			} catch (DataIOException e) {
				e.printStackTrace();
			}			
		}
		if(m_graph != null){
			DelimitedTextTableWriter dttw = new DelimitedTextTableWriter();				
			dttw.setPrintHeader(false);
			Table edgeTable = m_graph.getEdgeTable();
			if(edgeTable.getColumnCount() == 2)
				edgeTable.addColumn(EDGE_WEIGHT,int.class, 1);	

			m_openFile = TMP_PATH + name + ".tab";
			try{
				dttw.writeTable(edgeTable, m_openFile);	
				openFile(new File(m_openFile));
			}catch(DataIOException e){
				e.printStackTrace();				
			}
		}
	}
	/**
	 * Reads a graph to m_graph with EdgeListGraphReader or 
	 * GraphMLReaderMod class depends on the extension of given file.
	 * Clears m_clusterTable and notify views with OPENFILE argument.
	 * 
	 * @param p_file a file
	 */
	public void openFile(File p_file ){ 
		String sep = getSeparatorToExtension(p_file.getName());
		if (!sep.equals("")) {
			m_openFile = p_file.getAbsolutePath();
			m_graph = new EdgeListGraphReader(sep).readGraph(m_openFile,m_directedGraph);

		}else if (p_file.getName().toLowerCase().endsWith(".xml") 
				|| p_file.getName().toLowerCase().endsWith(".graphml")
				|| p_file.getName().toLowerCase().endsWith(".gz")
		) {	
			try{
				m_graph =  new GraphMLReaderMod().readGraph(p_file);		
			}catch(DataIOException e){
				e.printStackTrace();		
			}
			if(m_graph != null){
				Table nodeTable = m_graph.getNodeTable();
				Table edgeTable  = m_graph.getEdgeTable();	
				// undirected and missing col. EDGE_WEIGHT
				if(edgeTable.getColumnNumber(EDGE_WEIGHT) == -1)
					edgeTable.addColumn(EDGE_WEIGHT, int.class, 1);	
				Table edgeListTable = new Table();
				edgeListTable.addColumn("source", String.class);
				edgeListTable.addColumn("target", String.class);
				edgeListTable.addColumn(EDGE_WEIGHT, Double.class,1.0);	
				for(int i  = 0 ; i < edgeTable.getRowCount() ; i++){
					// add source and target names
					int row = edgeListTable.addRow();
					edgeListTable.set(row, "source", nodeTable.getColumn(NODE_NAME).getString(edgeTable.getInt(i, "source")));
					edgeListTable.set(row, "target", nodeTable.getColumn(NODE_NAME).getString(edgeTable.getInt(i, "target")));
					// add weight 				
					double weight = 0;
					try{
						weight = Double.parseDouble(String.valueOf(edgeTable.get(i, EDGE_WEIGHT)));
					}catch(NumberFormatException e ){
						weight = 1;
					}
					if(weight > MAX_EDGE_WEIGHT)
						weight = MAX_EDGE_WEIGHT;
					if(weight < 0) 
						weight = 1;					
					edgeListTable.set(row, 2,weight);
					edgeTable.set(row, EDGE_WEIGHT, weight);	
				}
				// write edge list
				DelimitedTextTableWriter dttw = new DelimitedTextTableWriter();	
				dttw.setPrintHeader(false);
				m_openFile = TMP_PATH + p_file.getName() + ".tab";
				try{
					dttw.writeTable(edgeListTable,m_openFile);	
				}catch(DataIOException e){
					e.printStackTrace();				
				}
			}
		}
		m_clusterTable = new Table();
		if(m_graph != null)
			notifyGui(OPENFILE);	
	}
	/** reads a delimited file to table*/
	public Table getEdgeListToTable(){
		DelimitedTextTableReader dttr = new DelimitedTextTableReader(getSeparatorToExtension(m_openFile));				
		dttr.setHasHeader(false);
		try{			
			return  dttr.readTable(m_openFile);
		}catch(DataIOException e){
			e.printStackTrace();	
			return null;
		}		
	}
	/** clusters to file*/
	public void saveFile(File p_file){		
		if(m_clusterTable != null)	
			try{
				new DelimitedTextTableWriter().writeTable(m_clusterTable, p_file);
			}catch(DataIOException e){
				e.printStackTrace();
			}
	}
}